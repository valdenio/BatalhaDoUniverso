package gameserver.bluetooth;

import gameserver.main.GameControl;
import gameserver.main.GameSocketThread;
import gameserver.main.MainGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.StreamConnection;

public class BluetoothThread extends Thread {

	private StreamConnection connection;
	private BluetoothServer btServer;
	private InputStream in = null;
	private static OutputStream out = null;
	private BluetoothDataManager dataManager = null;

	public BluetoothThread() throws IOException {
		super("BluetoothThread");

		this.btServer = new BluetoothServer(MainGame.UUID, "BLUETOOTH_GAME");
		this.btServer.setVisible();

		System.out.println("Nome Bluetooth: " + LocalDevice.getLocalDevice().getFriendlyName());
		System.out.println("Aguardando conexao com dispositivo Bluetooth...");
	}

	public void run() {
		// possibilita reconexões
		while (true) {
			try {
				connection = btServer.open();
				in = btServer.inputStream;
				out = btServer.outputStream;
				dataManager = new BluetoothDataManager();

				System.out.println("Dispositivo Bluetooth conectado!");
				GameControl.setAndroidReady();

				// update loop
				bluetoothUpdate();

				in.close();
				out.close();
				connection.close();
				btServer.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("Conexão Bluetooth encerrada");
				GameControl.changeGameStatus("WAITING_ANDROID");
			}
		}
	}

	private void bluetoothUpdate() throws IOException {
		while (true) {
			String received = btServer.read();
			// verifica se a conexão ainda está aberta
			if (received == null || received.equalsIgnoreCase("stop")) {
				break;
			}
			// trata os dados recebidos
			String response = dataManager.processData(received);

			// verifica se o comando recebido precisa ser enviado a Unity
			if (response.equalsIgnoreCase(received) && MainGame.isRunning()) {
				GameSocketThread.sendToGame(received);
			}
		}
	}

	public synchronized static void sendToDevice(String message) throws IOException {
		if (message != null)
			out.write(message.getBytes());
	}
}