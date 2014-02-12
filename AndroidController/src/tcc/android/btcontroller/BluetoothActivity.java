package tcc.android.btcontroller;

import java.io.IOException;

import tcc.android.bluetooth.BluetoothClient;
import tcc.android.bluetooth.IBluetoothSocketConnect;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends ListActivity implements OnClickListener,
		IBluetoothSocketConnect {

	private BluetoothClient btClient;
	private TwoLineAdapter mArrayAdapter;
	private ProgressDialog progressDialog;
	private Button findDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String uuid = intent.getStringExtra(MainActivity.START);
		setContentView(R.layout.activity_bluetooth);
		findDevices = (Button) findViewById(R.id.find);

		mArrayAdapter = new TwoLineAdapter(this);
		setListAdapter(mArrayAdapter);
		btClient = new BluetoothClient(this, uuid, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		findDevices.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		btClient.startListening(this);
		findDevices.performClick();
	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);

		BluetoothDevice device = mArrayAdapter.getItem(position);
		btClient.connectToHost(device);
		// mostra um loader de conexão
		showLoader(view.getContext(),
				String.format(getString(R.string.connection_device), device.getName()));
	}

	@Override
	public void onClick(View view) {
		Button button = (Button) view;
		if (button.equals(findDevices)) {
			mArrayAdapter.clear();
			findDevices.setText(getString(R.string.searching));
			btClient.searchDevices(discoveryReceiver);
		}
	}

	@Override
	public void onSocketConnected(BluetoothSocket socket) {
		progressDialog.dismiss();
		Toast.makeText(getBaseContext(), "Conectado", Toast.LENGTH_LONG).show();

		startActivity(new Intent(getBaseContext(), GameActivity.class));
		finish();
	}

	@Override
	public void onConnectionFailed(IOException exception) {
		progressDialog.dismiss();
		Toast.makeText(getBaseContext(), getString(R.string.bt_connection_failed),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDataReceived(String msg) {
	}

	@Override
	public void onConnectionClosed() {
	}

	/**
	 * {@code BroadcastReceiver} para gerenciar os estados da procura por dispositivos
	 */
	private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				findDevices.setText(getString(R.string.searching));
				mArrayAdapter.clear();

			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				findDevices.setText(getString(R.string.search_computers));

			} else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothClass btClass = device.getBluetoothClass();
				if (btClass.getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER) {
					mArrayAdapter.add(device);
				}
			}
		}
	};

	/**
	 * mostra um loader circular enquanto é feita a conexão
	 * 
	 * @param viewContext
	 *            o contexto atual
	 * @param message
	 *            a mensagem que deve ser mostrada no loader
	 */
	private void showLoader(Context viewContext, String message) {
		progressDialog = new ProgressDialog(viewContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(message);
		progressDialog.show();
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				Toast.makeText(getBaseContext(), getString(R.string.bt_connection_canceled),
						Toast.LENGTH_SHORT).show();
				btClient.abortConnection();
			}
		});
	}

	/**
	 * Adapter para utlizar o "simple_list_item_2", que possui dois {@code TextView}
	 * */
	private class TwoLineAdapter extends ArrayAdapter<BluetoothDevice> {

		public TwoLineAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2, android.R.id.text1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			TextView text2 = (TextView) view.findViewById(android.R.id.text2);

			text1.setText(getItem(position).getName());
			text2.setText(getItem(position).getAddress());
			return view;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		btClient.stopListening();
		unregisterReceiver(discoveryReceiver);
	}
}