package tcc.android.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public interface IBluetoothSocketConnect {

	/** disparado quando � estabelecida uma conex�o com outro dispositivo */
	void onSocketConnected(BluetoothSocket socket);

	/** disparado quando a tentativa de conex�o falha */
	void onConnectionFailed(IOException exception);

	/** disparado quando dados s�o recebidos do outro dispositivo */
	void onDataReceived(String msg);

	/** disparado quando a conex�o com o dispositivo � encerrada */
	void onConnectionClosed();

}