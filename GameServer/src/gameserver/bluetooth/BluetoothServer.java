package gameserver.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer {

	private String url;
	private LocalDevice btStack;
	private StreamConnectionNotifier serverConnection;
	public InputStream inputStream = null;
	public OutputStream outputStream = null;

	public BluetoothServer(String uuid, String serviceName) throws BluetoothStateException {
		final UUID serviceUUID = new UUID(uuid.replace("-", ""), false);
		url = "btspp://localhost:" + serviceUUID.toString() + ";name=" + serviceName + ";authenticate=false;master=true";
		btStack = LocalDevice.getLocalDevice();
	}

	public boolean setVisible() throws BluetoothStateException {
		if (btStack.getDiscoverable() != DiscoveryAgent.GIAC) {
			return btStack.setDiscoverable(DiscoveryAgent.GIAC);
		} else {
			return true;
		}
	}

	public StreamConnection open() throws IOException {
		StreamConnection connection = null;
		serverConnection = (StreamConnectionNotifier) Connector.open(url);
		connection = (StreamConnection) serverConnection.acceptAndOpen();
		inputStream = connection.openInputStream();
		outputStream = connection.openOutputStream();
		return connection;
	}

	public void close() throws IOException {
		serverConnection.close();
	}

	public String read() throws IOException {
		byte buffer[] = new byte[1024];
		int length = inputStream.read(buffer);
		if (length > -1) {
			String s = new String(buffer, 0, length);
			return s.trim();
		}
		return null;
	}
}