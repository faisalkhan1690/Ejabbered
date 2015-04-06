package com.example.xmppchatdemoactivity;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.MUCUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends ActionBarActivity {

	private ListView lvFriends;
	private ChatApp app;
	private ArrayAdapter<String> adapter;
	private ArrayList<RosterEntry> entry;
	private TextView tvFriedRequest;
	private TextView tvLogout;
	private TextView tvCreateGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		lvFriends = (ListView) findViewById(R.id.lvFriends);
		app = ChatApp.getInstance();
		adapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_1, app.getFriendListString());
		entry = new ArrayList<RosterEntry>(app.getFriendList());
		lvFriends.setAdapter(adapter);

		lvFriends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(getApplicationContext(), XMPPChatDemoActivity.class);
				intent.putExtra("email", entry.get(position).getUser().toString());
				intent.putExtra("memberChat", "single");
				startActivity(intent);
			}
		});

		// friendRequest
		tvFriedRequest = (TextView) findViewById(R.id.tvFriedRequest);
		tvFriedRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FriendRequestActivity
				Intent intent = new Intent(getApplicationContext(), FriendRequestActivity.class);
				startActivity(intent);

			}
		});

		// logout
		tvLogout = (TextView) findViewById(R.id.tvLogout);
		tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (app.connection != null)
						app.connection.disconnect();
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(intent);

				} catch (Exception e) {

				}

			}
		});

		// create group
		tvCreateGroup = (TextView) findViewById(R.id.tvCreateGroup);
		tvCreateGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// String jid = app.getUsername();
				// String user = "rahul@xmpp.deepco.com.br";
				// String group = "Approutes";
				String username = "rahul" + "@" + app.SERVICE;
				String nickname = "rahul";
				String group = "IBM" + "@" + app.SERVICE;

				try {
					// createGroup(username, nickname, group);

					updateBuddy(username, nickname, group);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public RosterEntry updateBuddy(String username, String nickname, String group) throws XMPPException {
				String jid = username;
				String[] groups = { group };
				Roster roster = app.getRoster();
				RosterEntry userEntry = roster.getEntry(jid);
				RosterGroup rosterGroup = roster.getGroup(group);
				if (rosterGroup == null) {
					rosterGroup = roster.createGroup(group);
				}
				if (userEntry == null) {
					roster.createEntry(jid, nickname, groups);
					userEntry = roster.getEntry(jid);
				} else {
					userEntry.setName(nickname);
					rosterGroup.addEntry(userEntry);
					
					
					MultiUserChat muc = new MultiUserChat(app.connection, group);
					muc.join(group, jid);
					muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
					
					
					//MultiUserChat muc6 = new MultiUserChat(app.connection, group);
					//muc6.invite(jid, "Invite friend");
					//muc6.join(group);

					// MUCUser mucUser = new MUCUser();
					// MUCUser.Invite invite = new MUCUser.Invite();
					// invite.setTo(jid);
					// invite.setReason("user is being invited");
					// mucUser.setInvite(invite);
					// Add the MUCUser packet that includes the invitation to
					// the message
					// message.addExtension(mucUser);
					// app.connection.sendPacket(mucUser);

				}
				userEntry = roster.getEntry(jid);
				return userEntry;

			}

			private RosterEntry createGroup(String username, String nickname, String group) throws XMPPException {
				String jid = username;
				String[] groups = { group };
				Roster roster = app.getRoster();
				RosterEntry userEntry = roster.getEntry(jid);
				boolean isSubscribed = true;
				if (userEntry != null)
					isSubscribed = userEntry.getGroups().isEmpty();
				if (isSubscribed) {
					try {
						roster.createEntry(jid, nickname, groups);
					} catch (XMPPException ex) {
					}
					return roster.getEntry(jid);
				}
				try {
					RosterGroup rosterGroup = roster.getGroup(group);
					if (rosterGroup == null)
						rosterGroup = roster.createGroup(group);
					if (userEntry == null) {
						roster.createEntry(jid, nickname, groups);
						userEntry = roster.getEntry(jid);
					} else {
						userEntry.setName(nickname);
						rosterGroup.addEntry(userEntry);
					}
					userEntry = roster.getEntry(jid);
				} catch (XMPPException ex) {
				}
				return userEntry;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		dimStatusandNavigationBars();
	}

	/**
	 * Dim the Status and Navigation Bars This example uses decor view, but you
	 * can use any visible view.
	 * http://developer.android.com/training/system-ui/dim.html
	 */
	private void dimStatusandNavigationBars() {

		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		decorView.setSystemUiVisibility(uiOptions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			showStatusandNavigationBars();
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Show Status And Navigation Bars Calling setSystemUiVisibility() with a
	 * value of 0 clears all flags.
	 */
	private void showStatusandNavigationBars() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(0);
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
