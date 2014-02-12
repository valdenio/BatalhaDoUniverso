package tcc.android.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public interface IBluetoothSocketConnect {

	/** disparado quando é estabelecida uma conexão com outro dispositivo */
	void onSocketConnected(BluetoothSocket socket);

	/** disparado quando a tentativa de conexão falha */
	void onConnectionFailed(IOException exception);

	/** disparado quando dados são recebidos do outro dispositivo */
	void onDataReceived(String msg);

	/** disparado quando a conexão com o dispositivo é encerrada */
	void onConnectionClosed();

}