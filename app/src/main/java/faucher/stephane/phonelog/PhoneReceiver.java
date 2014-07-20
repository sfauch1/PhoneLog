package faucher.stephane.phonelog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {

	private String phoneNumber = null;
	private PhoneLogTable db;
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Debug.waitForDebugger();

		Bundle extras = intent.getExtras();
		int phoneState = 0 ;

		if (extras != null) {

			// Get phone state
			String state = extras.getString(TelephonyManager.EXTRA_STATE);

			switch (state) {
			// Phone is idle
			case "IDLE":
				
				// Create instance of database object
				db = new PhoneLogTable(context);

				// Open DB connection
				db.open();

				// Update Phone log
				db.closePhoneLog();

				// Close DB connection
				db.close();
				
				break;
			case "OFFHOOK":
				phoneState = 2;

				// Create instance of database object
				db = new PhoneLogTable(context);

				// Open DB connection
				db.open();

				// Update Phone log
				db.updatePhoneLog(phoneState);

				// Close DB connection
				db.close();

				break;
			// Phone is ringing
			case "RINGING":

				phoneState = 1;

				// Get phone number
				phoneNumber = extras
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				// Incoming call


				// Create instance of database object
				db = new PhoneLogTable(context);

				// Create instance of PhoneLog object
				PhoneLog log = new PhoneLog(0, phoneNumber,
						System.currentTimeMillis(), System.currentTimeMillis(),
						phoneState);

				// Open DB connection
				db.open();

				// Insert Phone log
				db.insertPhoneLog(log);

				// Close DB connection
				db.close();

				break;
			default:
				break;
			}
		}

	}

}
