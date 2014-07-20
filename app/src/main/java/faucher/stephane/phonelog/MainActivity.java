package faucher.stephane.phonelog;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemLongClickListener {

	private PhoneLogTable phoneLogTable;

	// Declaration du ArrayList
	private List<PhoneLog> phoneLog = new ArrayList<PhoneLog>();
	private PhoneLogAdapter phoneLogAdapter;
	private Set<Integer> selection = new HashSet<Integer>();
	private int lstPosition;
	private ListView listView;

	public class PhoneLogAdapter extends ArrayAdapter<Item> {

		public PhoneLogAdapter(Context context, int resource,
				List<PhoneLog> items) {
			super(context, resource);
		}

		@Override
		public int getCount() {
			Log.d("getCount", "count : " + phoneLog.size());
			return phoneLog.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {

				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.log_layout, null);

			}

			SimpleDateFormat sDate = new SimpleDateFormat("dd MMM yyyy");

			// Get Phone Call Object
			PhoneLog pLog = phoneLog.get(position);

			// Get Phone Number Information
			TextView txtPhone = (TextView) v.findViewById(R.id.txtPhoneNumber);
			txtPhone.setText(PhoneNumberUtils.formatNumber(pLog
					.getPhoneNumber()));

			// Get Date Information
			TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
			Date callDate = new Date(pLog.getStartTime());
			txtDate.setText(sDate.format(callDate));

			// Get Time Information
			TextView txtTime = (TextView) v.findViewById(R.id.txtHour);
			txtTime.setText(DateFormat.format("hh:mm:ss", pLog.getEndTime()));

			// Get Status Information
			long elapsed;
			elapsed = ((pLog.getEndTime() - pLog.getStartTime()));

			TextView txtStatus = (TextView) v.findViewById(R.id.txtCallStatus);

			String callStatus = null;
			switch (pLog.getPhoneState()) {

			case 2:
				callStatus = "Duree de la conversation : "
						+ DateFormat.format("mm", elapsed) + "min "
						+ DateFormat.format("ss", elapsed) + "sec";
				break;
			case 3:
				callStatus = "Appel manque.";
			default:
				break;
			}
			txtStatus.setText(callStatus);

			return v;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set adapter
		refreshList();

		// onClickListener on Delete All
		Button btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);
		btnDeleteAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Create instance of PhoneLogTable
				phoneLogTable = new PhoneLogTable(v.getContext());

				// Open database
				phoneLogTable.open();

				// Delete table
				phoneLogTable.deleteTable();

				// Close database
				phoneLogTable.close();

				//Toast.makeText(v.getContext(), "Tous les enregistrements ont ete effaces !",
						//Toast.LENGTH_LONG).show();

				// Notify adapter
				notifyAdapter();

			}
		});

		// onClickListener on Delete Selected
		Button btnDeleteSelected = (Button) findViewById(R.id.btnDeleteSelected);
		btnDeleteSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Get Phone Call Object
				PhoneLog pLog = phoneLog.get(lstPosition);

				// Create instance of PhoneLogTable
				phoneLogTable = new PhoneLogTable(v.getContext());

				// Open database
				phoneLogTable.open();

				// Delete table
				phoneLogTable.deletePhoneLog(pLog.getId());

				// Close database
				phoneLogTable.close();

				// Notify adapter
				notifyAdapter();

			}
		});
		
		
		//Long click listener on list view
		ListView listView = (ListView) findViewById(R.id.lstPhoneLog);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				//Set position for the delete button
				lstPosition = position;

				return false;
			}
		});
	}

	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Life Cycles
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("Cycle", "onStart");

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("Cycle", "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Notify adapter
		notifyAdapter();
		Log.d("Cycle", "onResume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("Cycle", "onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("Cycle", "onStop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Cycle", "onDestroy");
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	private void refreshList() {

		// Create instance of PhoneLogTable
		phoneLogTable = new PhoneLogTable(this);

		// Open database
		phoneLogTable.open();

		phoneLog = phoneLogTable.getLogs();

		// Close database
		phoneLogTable.close();

		ListView logListView = (ListView) findViewById(R.id.lstPhoneLog);

		phoneLogAdapter = new PhoneLogAdapter(this, R.layout.log_layout,
				phoneLog);

		logListView.setAdapter(phoneLogAdapter);

	}

	public void notifyAdapter() {
		// Create instance of PhoneLogTable
		phoneLogTable = new PhoneLogTable(this);

		// Open database
		phoneLogTable.open();

		phoneLog = phoneLogTable.getLogs();

		// Close database
		phoneLogTable.close();
		phoneLogAdapter.notifyDataSetChanged();
		
		//Toast.makeText(getContext(), "La liste a ete refraichie !",
				//Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
