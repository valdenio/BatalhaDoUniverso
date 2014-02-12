package tcc.android.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

public abstract class BluetoothSocketManager {

	public static final int REQUEST_ENABLE_BT = 3;
	public static final int VISIBLE_INFINITE = 0;
	private BluetoothSocket socket_;
	private IBluetoothSocketConnect connectionListener = null;
	private static BluetoothSocketManager instance;
	private byte[] bytes_;
	private ConnectedThread con;
	protected Context context;
	public UUID app_uuid;
	protected final BluetoothAdapter btAdapter;
	protected boolean authentication = false;
	IOException exception;
	Handler handler;

	protected BluetoothSocketManager(Context context, String uuid, boolean secure) {
		this.context = context;
		this.app_uuid = UUID.fromString(uuid);
		this.authentication = secure;
		this.handler = new Handler();

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		} else {
			this.btAdapter = getNewAdapter();
		}

		if (btAdapter == null) {
			new IOException("Bluetooth não está disponível para esse dispositivo");
		} else {
			instance = this;
		}
	}

	/**
	 * recupera o {@code BluetoothAdapter} através da nova classe {@code BluetoothManager}
	 * adicionada na API 18, utilizando o {@code getSystemService()}
	 * 
	 * @return o {@code BluetoothAdapter}
	 */
	@TargetApi(18)
	private BluetoothAdapter getNewAdapter() {
		final BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		return bluetoothManager.getAdapter();
	}

	protected void closeConnection() {
		if (con != null) {
			try {
				con.cancel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ----------PUBLIC METHODS-----------
	/**
	 * retorna a única instancia dessa classe
	 * 
	 * @return a instancia do {@code BluetoothSocketManager}
	 */
	public static BluetoothSocketManager getInstance() {
		return instance;
	}

	/**
	 * recupera o {@code BluetoothAdapter} padrão do sistema. Essa chamada já é compatível com a
	 * classe {@code BluetoothManager} adicionada na API 18
	 * 
	 * @return o {@code BluetoothAdapter} que está sendo usado
	 */
	public BluetoothAdapter getAdapter() {
		return btAdapter;
	}

	/** adiciona o listener para gerenciar a conexão remota */
	public void startListening(IBluetoothSocketConnect listener) {
		connectionListener = listener;
	}

	/** Inicia a comunicação com o dispositivo remoto */
	public void startCommunication() {
		con = new ConnectedThread(socket_);
		con.start();
	}

	/** envia os dados para o computador */
	public void sendMessage(String msg) {
		if (con != null && con.isAlive()) {
			byte bytes[] = msg.getBytes();
			try {
				con.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * remove o listener e cancela a busca por novos dispositivos
	 */
	public void stopListening() {
		connectionListener = null;
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}

	/**
	 * Thread responsavel pela transmissao e recepcao dos dados
	 */
	private class ConnectedThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer;

			while (true) {
				try {
					buffer = new byte[1024];
					mmInStream.read(buffer);
					msgReceived(buffer);

				} catch (IOException e) {
					handler.post(connectionClosed);
					break;
				}
			}
		}

		private void write(byte[] bytes) throws IOException {
			mmOutStream.write(bytes);
		}

		private void cancel() throws IOException {
			mmSocket.close();
		}
	};

	// -----------TRIGGER METHODS------------
	protected void socketConnected(BluetoothSocket socket) {
		this.socket_ = socket;
		handler.post(connected);
	}

	void msgReceived(byte[] buffer) {
		this.bytes_ = buffer;
		handler.post(dataReceived);
	}

	// ------------ RUNNABLES-------------
	final Runnable connected = new Runnable() {

		public void run() {
			if (connectionListener != null) {
				connectionListener.onSocketConnected(socket_);
			} else {
				handler.post(connectionFailed);
			}
		}
	};

	final Runnable dataReceived = new Runnable() {

		public void run() {
			if (connectionListener != null) {
				String str = new String(bytes_).trim();
				connectionListener.onDataReceived(str);
			}
		}
	};

	final Runnable connectionClosed = new Runnable() {

		public void run() {
			if (connectionListener != null) {
				connectionListener.onConnectionClosed();
			}
		}
	};

	final Runnable connectionFailed = new Runnable() {

		public void run() {
			if (connectionListener != null) {
				connectionListener.onConnectionFailed(exception);
			}
		}
	};
}