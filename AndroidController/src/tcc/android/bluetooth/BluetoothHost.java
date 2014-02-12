package tcc.android.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

public class BluetoothHost extends BluetoothSocketManager {
	private final BluetoothServerSocket serverSocket;

	public BluetoothHost(Context context, String uuid, String serviceName, boolean secure) {
		super(context, uuid, secure);

		BluetoothServerSocket tmp = null;
		try {
			if (this.authentication)
				tmp = btAdapter.listenUsingRfcommWithServiceRecord(serviceName, app_uuid);
			else
				tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(serviceName, app_uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverSocket = tmp;
	}

	/** set the device in discovery mode */
	public void setVisible(int time) {
		if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
			context.startActivity(discoverableIntent);
		}
	}

	/** starts standby for remote connections */
	public void open() {
		new AcceptThread().start();
	}

	@Override
	public void closeConnection() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	private class AcceptThread extends Thread {

		@Override
		public void run() {
			BluetoothSocket socket = null;

			while (true) {
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					exception = e;
					handler.post(connectionFailed);
					break;
				}
				if (socket != null) {
					socketConnected(socket);
					// close();
					break;
				}
			}
		}
	}
}