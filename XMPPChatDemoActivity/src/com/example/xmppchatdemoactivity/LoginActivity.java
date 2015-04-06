package com.example.xmppchatdemoactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {
	private ChatApp app;
	EditText etUsername, etPwd;

	// public static final String USERNAME = "ranjeet";// hisham
	// public static final String PASSWORD = "ranjeet";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etPwd = (EditText) findViewById(R.id.etPwd);
	}

	
	public void register(View v){
		startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
	}
	
	public void login(View v) {
		app = ChatApp.getInstance();
		new ConnectToServer().execute();
	}

	public class ConnectToServer extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(LoginActivity.this, "Connecting...", "Please wait...", false);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return app.connect(LoginActivity.this);
		}

		private String getText(EditText et) {
			return et.getText().toString().trim();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				boolean isLoggedIn = app.login(getText(etUsername), getText(etPwd));
				if (isLoggedIn) {
					startActivity(new Intent(LoginActivity.this, HomeActivity.class));
					// ivStatus.setImageResource(R.drawable.ic_green_online);
				} else {
					// ivStatus.setImageResource(R.drawable.ic_red_offline);
				}

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
