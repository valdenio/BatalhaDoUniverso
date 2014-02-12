package tcc.android.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class BluetoothClient extends BluetoothSocketManager {

	private BluetoothDevice remoteDevice;
	BluetoothSocket socket;

	public BluetoothClient(Context context, String uuid, boolean secure) {
		super(context, uuid, secure);
	}

	/**
	 * procura por dispositivos visiveis
	 * 
	 * @param receiver
	 *            o {@code BroadcastReceiver} para receber os resultados da busca
	 * @return {@code true} se a busca foi iniciada ou {@code false} se não foi possível iniciar
	 */
	public boolean searchDevices(BroadcastReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		context.registerReceiver(receiver, filter);

		btAdapter.cancelDiscovery();
		return btAdapter.startDiscovery();
	}

	/**
	 * Tenta se conectar ao dispositivo remoto
	 * 
	 * @param host
	 *            O dispositivo a qual deve ser feita a tentativa de conexão
	 * @return O {@code BluetoothSocket} criado para a tentativa de conexão
	 */
	public BluetoothSocket connectToHost(BluetoothDevice host) {
		remoteDevice = host;
		BluetoothSocket tmp = null;
		btAdapter.cancelDiscovery();

		try {
			if (this.authentication) {
				tmp = remoteDevice.createRfcommSocketToServiceRecord(app_uuid);
			} else {
				tmp = remoteDevice.createInsecureRfcommSocketToServiceRecord(app_uuid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = tmp;
		// inicia a tentativa de conexao
		new ConnectThread().start();
		return socket;
	}

	/**
	 * cancela a tentativa de conexão
	 */
	public void abortConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * cancela a conexão ativa
	 */
	@Override
	public void closeConnection() {
		super.closeConnection();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stopListening();
	};

	/**
	 * Thread para tentar fazer a conexão com o dispositivo remoto
	 * 
	 */
	private class ConnectThread extends Thread {

		@Override
		public void run() {
			try {
				socket.connect();
				socketConnected(socket);
			} catch (IOException e) {
				exception = e;
				handler.post(connectionFailed);
				closeConnection();
				return;
			}
		}
	}
}