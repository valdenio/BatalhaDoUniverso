package gameserver.main;

import gameserver.bluetooth.BluetoothThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameSocketThread extends Thread {

	private static final int PORT = 45;
	private ServerSocket serverSocket = null;
	private InputStream in = null;
	private static OutputStream out = null;
	private GameControl gameController = null;
	private String inputLine, outputLine;

	public GameSocketThread() throws IOException {
		super("GameSocketThread");
		this.serverSocket = new ServerSocket(PORT);
	}

	public void run() {
		// permite com que a conexão seja reestabelecida, caso o jogo seja reiniciado
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				out = socket.getOutputStream();
				in = socket.getInputStream();
				gameController = new GameControl();

				// loop atual do jogo
				updateLoop();

				out.close();
				in.close();
				socket.close();
				serverSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Conexão com o jogo encerrada!");
			}
		}
	}

	private void updateLoop() throws IOException {
		while (true) {
			inputLine = read();
			if (inputLine.equalsIgnoreCase("STOP")) {
				MainGame.exitGame();
				break;
			} else {
				outputLine = gameController.processGameData(inputLine);

				// verifica se o comando recebido precisa ser enviado ao android
				if (outputLine.equalsIgnoreCase(inputLine)) {
					BluetoothThread.sendToDevice(outputLine);
				} else {
					sendToGame(outputLine);
				}
			}
		}
	}

	private String read() throws IOException {
		byte buffer[] = new byte[1024];
		int length = in.read(buffer);
		String s = new String(buffer, 0, length);
		return s.trim();
	}

	public synchronized static void sendToGame(String message) throws IOException {
		if (out != null && message != null)
			out.write(message.getBytes());
	}
}