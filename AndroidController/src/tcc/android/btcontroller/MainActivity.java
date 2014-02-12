package tcc.android.btcontroller;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import tcc.android.bluetooth.BluetoothSocketManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class MainActivity extends Activity implements OnClickListener,
		DialogInterface.OnClickListener {

	private final static String UUID = "FE09AED7-8C99-4C10-AC29-9011E1C642AA";
	public final static String START = "tcc.android.bluetooth.START";
	public final static String EXIT = "tcc.android.bluetooth.EXIT";
	private BluetoothAdapter btAdapter;
	private Button play, tutorial, credits, exit;
	private boolean backPressed = false;
	private ViewAnimator vAnimator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(EXIT, false)) {
			finish();
			return;
		} else {
			setContentView(R.layout.activity_main);

			final TextView titulo = (TextView) findViewById(R.id.titulo);
			titulo.setText(getString(R.string.app_name).toUpperCase(Locale.getDefault()));

			vAnimator = (ViewAnimator) findViewById(R.id.animator);
			play = (Button) findViewById(R.id.play);
			tutorial = (Button) findViewById(R.id.tutorial);
			credits = (Button) findViewById(R.id.credits);
			exit = (Button) findViewById(R.id.exit);

			btAdapter = BluetoothAdapter.getDefaultAdapter();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// recupera os botoes via reflection
		for (Field g : (getClass().getDeclaredFields())) {
			g.setAccessible(true);
			if (g.getType().isAssignableFrom(Button.class))
				try {
					// adiciona o clickListener no botao atual
					((Button) g.get(this)).setOnClickListener(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public void onClick(View view) {
		Button button = (Button) view;
		if (button.equals(play)) {
			if (!btAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, BluetoothSocketManager.REQUEST_ENABLE_BT);
			} else {
				initGame();
			}

		} else if (button.equals(tutorial)) {
			vAnimator.setDisplayedChild(1);

		} else if (button.equals(credits)) {
			vAnimator.setDisplayedChild(2);

		} else if (button.equals(exit)) {
			new AlertDialog.Builder(this).setMessage(getString(R.string.prompt_exit))
					.setNegativeButton("Sim", this).setPositiveButton("Não", this).show();
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == BluetoothSocketManager.REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				initGame();
			} else {
				Toast.makeText(getBaseContext(), getString(R.string.bluetooth_denied_message),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (vAnimator.getDisplayedChild() == 0) {
			if (!backPressed) {
				Toast.makeText(this, getString(R.string.toast_exit_confirm), Toast.LENGTH_SHORT)
						.show();
				backPressed = true;

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						backPressed = false;
					}
				}, 2000);
				return;
			}
			finish();
		} else {
			vAnimator.setDisplayedChild(0);
		}
	}

	private void initGame() {
		Intent intent = new Intent(getBaseContext(), BluetoothActivity.class);
		intent.putExtra(START, UUID);
		startActivity(intent);
	}
}