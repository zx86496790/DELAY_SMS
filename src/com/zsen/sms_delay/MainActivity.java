package com.zsen.sms_delay;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText editTextReceiver;
	EditText editTextContent;
	ImageButton imageButtonReceiver;
	ImageButton imageButtonSend;
	ImageButton imageButtonCancle;
	SeekBar seekBarTime;
	ProgressBar pb;
	Handler handler = new Handler();
	Handler handler2=new Handler();
	Runnable updateThread;
	String usernumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editTextReceiver = (EditText) findViewById(R.id.editTexReceiver);
		editTextContent = (EditText) findViewById(R.id.editTextContent);
		imageButtonReceiver = (ImageButton) findViewById(R.id.imageButtonReceiver);
		imageButtonSend = (ImageButton) findViewById(R.id.imageButtonSend);
		imageButtonCancle = (ImageButton) findViewById(R.id.imageButtonCancle);
		seekBarTime = (SeekBar) findViewById(R.id.seekBarTime);
		imageButtonReceiver.setOnClickListener(new ChooseReceiverListener());
		imageButtonSend.setOnClickListener(new SendListener());
		imageButtonCancle.setOnClickListener(new CancleListener());
		pb=(ProgressBar)findViewById(R.id.progressBar1);
		updateThread = new Runnable() {
			@Override
			public void run() {
				SmsManager manager = SmsManager.getDefault();
				ArrayList<String> messages = manager
						.divideMessage(editTextContent.getText().toString());
				for (String ms : messages) {
					manager.sendTextMessage(usernumber, null, ms, null, null);
					Toast.makeText(getApplicationContext(), "SENT!", 0).show();
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ContentResolver reContentResolverol = getContentResolver();
			Uri contactData = data.getData();
			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			String username = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = reContentResolverol.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				usernumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				editTextReceiver.setText("To:" + username);
			}

		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public class ChooseReceiverListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startActivityForResult(new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI), 0);
		}

	}

	public class CancleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			handler.removeCallbacks(updateThread);
			Toast.makeText(getApplicationContext(), "Your text has been cancled", Toast.LENGTH_SHORT).show();
		}

	}

	public class SendListener implements OnClickListener {
		public void onClick(View v) {
			pb.setMax(seekBarTime.getProgress());
			pb.setProgress(seekBarTime.getProgress());
			handler.postDelayed(updateThread, seekBarTime.getProgress() * 1000);
		}
	}

}
