package tcc.android.btcontroller;

import java.io.IOException;
import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import tcc.android.bluetooth.BluetoothClient;
import tcc.android.bluetooth.BluetoothSocketManager;
import tcc.android.bluetooth.IBluetoothSocketConnect;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class GameActivity extends Activity implements OnClickListener, IBluetoothSocketConnect, SensorEventListener,
		DialogInterface.OnClickListener {

	private static final float ALPHA = 0.8f;
	private ViewAnimator vAnimator;
	private BluetoothClient btClient;
	private ImageButton left, right, up, down, shoot, enter, close;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] gravity = new float[3];
	private float[] linearAcceleration = new float[3];
	private JSONObject json, send;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		vAnimator = (ViewAnimator) findViewById(R.id.viewAnimator);
		left = (ImageButton) findViewById(R.id.left);
		right = (ImageButton) findViewById(R.id.right);
		up = (ImageButton) findViewById(R.id.up);
		down = (ImageButton) findViewById(R.id.down);
		enter = (ImageButton) findViewById(R.id.enter);
		close = (ImageButton) findViewById(R.id.close);
		shoot = (ImageButton) findViewById(R.id.shoot);

		btClient = (BluetoothClient) BluetoothSocketManager.getInstance();
	}

	@Override
	public void onStart() {
		super.onStart();

		// recupera os botoes via reflection
		for (Field g : (getClass().getDeclaredFields())) {
			g.setAccessible(true);
			if (g.getType().isAssignableFrom(ImageButton.class))
				try {
					// adiciona o clicklistener no botao atual
					((ImageButton) g.get(this)).setOnClickListener(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		btClient.startCommunication();
		btClient.startListening(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		btClient.closeConnection();
		finish();
	}

	// click nas imagens
	@Override
	public void onClick(View view) {
		btClient.sendMessage(view.getTag().toString());

		if (view.equals(enter)) {
			vibrator.vibrate(50);

		} else {
			vibrator.vibrate(10);

			// trata o click no "X", mostrando um alerta
			if (view.equals(close)) {
				new AlertDialog.Builder(this).setMessage(getString(R.string.prompt_exit))
						.setNegativeButton("Sim", this).setPositiveButton("Não", this).show();
			}
		}
	}

	// listener para click no alerta gerado pelo "X"
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// se o jogador escolher sim, encerra a conexão e volta ao menu inicial
		if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
			exit(false);
		}
	}

	@Override
	public void onDataReceived(String str) {
		if (str.equalsIgnoreCase("STOP")) {
			btClient.closeConnection();
			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

			// quando a nave sofre dano
		} else if (str.equalsIgnoreCase("DAMAGE")) {
			vibrator.vibrate(100);
			// quando o jogo e despausado (pelo teclado ou nao)
		} else if (vAnimator.getDisplayedChild() != 1 && str.equalsIgnoreCase("PLAY")) {
			resumeGame();

			// quando o jogo e pausado (pelo teclado ou nao)
		} else if (vAnimator.getDisplayedChild() != 0 && str.equalsIgnoreCase("PAUSE")) {
			pauseGame();
		}
	}

	@Override
	public void onConnectionClosed() {
		Toast.makeText(getBaseContext(), getString(R.string.bt_connection_closed), Toast.LENGTH_SHORT).show();
		exit(false);
	}

	@Override
	public void onSocketConnected(BluetoothSocket socket) {
	}

	@Override
	public void onConnectionFailed(IOException exception) {
	}

	@Override
	public void onBackPressed() {
		if (vAnimator.getDisplayedChild() != 0) {
			btClient.sendMessage("PAUSE");

		} else if (vAnimator.getDisplayedChild() != 1) {
			btClient.sendMessage("RESUME");
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		System.arraycopy(event.values, 0, linearAcceleration, 0, event.values.length);

		// aplica o low-pass filter para isolar os valores da gravidade
		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

		// aplica o high-pass filter para desconsiderar os valores da gravidade
		linearAcceleration[0] -= gravity[0];
		linearAcceleration[1] -= gravity[1];
		linearAcceleration[2] -= gravity[2];

		sendJSON(linearAcceleration);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private void pauseGame() {
		vAnimator.setDisplayedChild(0);
		mSensorManager.unregisterListener(this);
	}

	private void resumeGame() {
		vAnimator.setDisplayedChild(1);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	private void sendJSON(float[] acceleration) {
		json = new JSONObject();
		send = new JSONObject();
		try {
			json.put("x", acceleration[0]);
			json.put("y", acceleration[1]);
			json.put("z", acceleration[2]);
			send.put("acelerometro", json);

			btClient.sendMessage(send.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void exit(boolean out) {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.EXIT, out);
		startActivity(intent);
		finish();
	}
}