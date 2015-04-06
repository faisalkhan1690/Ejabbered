package com.example.xmppchatdemoactivity;

import org.jivesoftware.smack.XMPPException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xmppchatdemoactivity.LoginActivity.ConnectToServer;

public class RegistrationActivity extends ActionBarActivity {
	private ChatApp app;
	EditText etUsername, etPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPwd = (EditText) findViewById(R.id.etPwd);
		
		app = ChatApp.getInstance();
		
	}
	

	
	public void back(View v){
		finish();
	}

	
	public void register(View v){
		new ConnectToServer().execute();
	}


	public class ConnectToServer extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(RegistrationActivity.this, "Connecting...", "Please wait...", false);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			 app.connect(RegistrationActivity.this);
			return app.register(getText(etUsername), getText(etPwd));
		}
		
		private String getText(EditText et){
			return et.getText().toString().trim();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
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
}
