package gameserver.bluetooth;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class BluetoothDataManager {

	private Robot robot = null;

	public BluetoothDataManager() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public String processData(String str) throws IOException {
		String output = "";

		switch (str.toUpperCase()) {
			case "LEFT":
				pressKeys(KeyEvent.VK_LEFT);
				break;
			case "RIGHT":
				pressKeys(KeyEvent.VK_RIGHT);
				break;
			case "UP":
				pressKeys(KeyEvent.VK_UP);
				break;
			case "DOWN":
				pressKeys(KeyEvent.VK_DOWN);
				break;
			case "SELECT":
				pressKeys(KeyEvent.VK_SPACE);
				break;
			case "PAUSE":
				pressKeys(KeyEvent.VK_ESCAPE);
				break;
			case "RESUME":
				pressKeys(KeyEvent.VK_ESCAPE);
				break;
			case "SHOOT":
				pressKeys(KeyEvent.VK_TAB);
				break;
			default:
				output = str;
				break;
		}
		return output;
	}

	public void pressKeys(int... keycodes) {
		for (int code : keycodes) {
			robot.keyPress(code);
		}
		for (int i = keycodes.length - 1; i >= 0; i--) {
			robot.keyRelease(keycodes[i]);
		}
	}
}