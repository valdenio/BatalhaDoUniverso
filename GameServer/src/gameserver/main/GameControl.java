package gameserver.main;

import java.io.IOException;

/**
 * @author vmelo
 * 
 */
public class GameControl {

	private static boolean unityReady = false;
	private static boolean androidReady = false;

	/**
	 * processa os dados recebidos do jogo e realiza as ações correspondentes
	 * 
	 * @param input
	 * @return
	 */
	public String processGameData(String input) {
		String output = input;

		if (input.equalsIgnoreCase("READY")) {
			unityReady = true;
			if (androidReady) {
				MainGame.setGameReady();
				output = "GAME_READY";
			} else {
				output = "WAITING_ANDROID";
			}
			System.out.println("Conetado com o jogo!");
		}
		return output;
	}

	public synchronized static void setAndroidReady() throws IOException {
		androidReady = true;
		if (unityReady) {
			GameSocketThread.sendToGame("GAME_READY");
			MainGame.setGameReady();
		}
	}

	/**
	 * Altera o status atual do jogo
	 * 
	 * @param newState
	 *            O novo status do jogo
	 */
	public synchronized static void changeGameStatus(String newStatus) {
		try {
			GameSocketThread.sendToGame(newStatus);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}