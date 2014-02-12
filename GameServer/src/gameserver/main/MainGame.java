package gameserver.main;

import gameserver.bluetooth.BluetoothThread;

import java.io.File;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.swing.JOptionPane;

public class MainGame {

	private static boolean RUNNING = false;
	private static Process gameProcess;
	public final static String UUID = "FE09AED7-8C99-4C10-AC29-9011E1C642AA";

	public static synchronized void setGameReady() {
		RUNNING = true;
		try {
			BluetoothThread.sendToDevice("PLAY");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean isRunning() {
		return RUNNING;
	}

	public static void main(String[] args) {
		String gamePath = "";
		// verifica se o caminho do jogo foi especificado
		if (args.length > 0) {
			gamePath = args[0];
		}
		initGame(gamePath);
	}

	public static void initGame(String gamePath) {
		try {
			new BluetoothThread().start();
			new GameSocketThread().start();

			// inicia o processo da unity
			File file = new File(gamePath);
			if (file.exists()) {
				gameProcess = new ProcessBuilder(gamePath).start();
				System.out.println("Iniciando o jogo...");
			} else {
				System.out
						.println("Não foi possível iniciar o jogo automaticamente. Você pode abrir o arquivo 'game.exe' para jogar, mas sem fechar esta janela.");
				// exitGame();
			}

		} catch (BluetoothStateException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Não foi detectado nenhum módulo Bluetooth instalado em seu computador.");
			exitGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exitGame() {
		if (gameProcess != null)
			gameProcess.destroy();
		System.exit(0);
	}
}