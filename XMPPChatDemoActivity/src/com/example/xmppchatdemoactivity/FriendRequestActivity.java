package com.example.xmppchatdemoactivity;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.xmppchatdemoactivity.fragments.MyFriendListFragment;
import com.example.xmppchatdemoactivity.fragments.PendingRequestsFragment;
import com.example.xmppchatdemoactivity.fragments.RequestsFragment;

public class FriendRequestActivity extends ActionBarActivity {
	private Button btnfriendRequest;
	private EditText etUsername;
	private String jid;
	private ChatApp app;
	private String nickname;
	private String idExtension;
	private EditText etUsernamesub;
	private Button btnfriendRequestsubs;
	private EditText etUsernameblock;
	private Button btnblockfriend;
	private EditText etUsernameRejectId;
	private Button btnRejectFriend;

	private final Handler handler = new Handler();

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	private Drawable oldBackground = null;
	private int currentColor = 0xFF666666;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_request);

		// /////
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);

		// changeColor(currentColor);
		// /////

		app = ChatApp.getInstance();

		btnfriendRequest = (Button) findViewById(R.id.btnfriendRequest);
		etUsername = (EditText) findViewById(R.id.etUsername);

		// jid = "shakir";
		// send request subcribe
		btnfriendRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etUsername.getText().toString().trim().equals("")) {
					Toast.makeText(FriendRequestActivity.this, "Enter Username", Toast.LENGTH_LONG).show();
				} else {
					jid = etUsername.getText().toString().trim() + "@" + app.SERVICE;
					addFriendSubscribe(jid);
				}

			}
		});

		// Accept subcribed
		etUsernamesub = (EditText) findViewById(R.id.etUsernamesub);
		btnfriendRequestsubs = (Button) findViewById(R.id.btnfriendRequestsubs);

		btnfriendRequestsubs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etUsernamesub.getText().toString().trim().equals("")) {
					Toast.makeText(FriendRequestActivity.this, "Enter Username", Toast.LENGTH_LONG).show();
				} else {
					jid = etUsernamesub.getText().toString().trim() + "@" + app.SERVICE;
					AcceptFriendRequestSubscribed(jid);
				}

			}

			private boolean AcceptFriendRequestSubscribed(String jid) {

				nickname = jid;
				idExtension = jid;
				nickname = StringUtils.parseBareAddress(jid);
				Roster roster = app.connection.getRoster();
				try {
					roster.createEntry(idExtension, nickname, null);
					// to subscribe the user in the entry
					Presence subscribed = new Presence(Presence.Type.subscribed);
					subscribed.setTo(idExtension);
					app.connection.sendPacket(subscribed);
					return true;
				} catch (XMPPException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.err.println("Error in adding friend");
					return false;
				}

			}
		});

		// reject friend Request
		etUsernameRejectId = (EditText) findViewById(R.id.etUsernameRejectId);
		btnRejectFriend = (Button) findViewById(R.id.btnRejectFriend);
		btnRejectFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (etUsernameRejectId.getText().toString().trim().equals("")) {
					Toast.makeText(FriendRequestActivity.this, "Enter Username", Toast.LENGTH_LONG).show();
				} else {
					jid = etUsernameRejectId.getText().toString().trim() + "@" + app.SERVICE;
					rejectFriendRequest(jid);
				}

			}

			private boolean rejectFriendRequest(String jid) {

				nickname = jid;
				idExtension = jid;
				nickname = StringUtils.parseBareAddress(jid);
				Roster roster = app.connection.getRoster();
				try {
					roster.createEntry(idExtension, nickname, null);
					// to subscribe the user in the entry
					Presence unsubscribed = new Presence(Presence.Type.unsubscribed);
					unsubscribed.setTo(idExtension);
					app.connection.sendPacket(unsubscribed);
					return true;
				} catch (XMPPException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.err.println("Error in adding friend");
					return false;
				}

			}
		});

		// block friend
		etUsernameblock = (EditText) findViewById(R.id.etUsernameblock);
		btnblockfriend = (Button) findViewById(R.id.btnblockfriend);
		btnblockfriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etUsernameblock.getText().toString().trim().equals("")) {
					Toast.makeText(FriendRequestActivity.this, "Enter Username", Toast.LENGTH_LONG).show();
				} else {
					jid = etUsernameblock.getText().toString().trim() + "@" + app.SERVICE;
					BlockFriend(jid);
				}

			}

			private boolean BlockFriend(String jid) {
				RosterPacket packet = new RosterPacket();
				packet.setType(IQ.Type.SET);
				RosterPacket.Item item = new RosterPacket.Item(jid, null);
				item.setItemType(RosterPacket.ItemType.remove);
				packet.addRosterItem(item);
				app.connection.sendPacket(packet);
				return true;

				// nickname = jid;
				// idExtension = jid;
				// nickname = StringUtils.parseBareAddress(jid);
				// Roster roster = app.connection.getRoster();
				// try {
				// roster.createEntry(idExtension, nickname, null);
				// // to subscribe the user in the entry
				// Presence unsubscribe = new
				// Presence(Presence.Type.unsubscribe);
				// unsubscribe.setTo(idExtension);
				// app.connection.sendPacket(unsubscribe);
				// return true;
				// } catch (XMPPException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// System.err.println("Error in adding friend");
				// return false;
				// }

			}
		});

	}

	private boolean addFriendSubscribe(String jid) {
		nickname = jid;
		idExtension = jid;
		nickname = StringUtils.parseBareAddress(jid);
		Roster roster = app.connection.getRoster();
		if (!roster.contains(idExtension)) {
			try {
				roster.createEntry(idExtension, nickname, null);
				// to subscribe the user in the entry
				Presence subscribe = new Presence(Presence.Type.subscribe);
				subscribe.setTo(idExtension);
				app.connection.sendPacket(subscribe);
				return true;
			} catch (XMPPException e) {
				System.err.println("Error in adding friend");
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_request, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add_friend) {
			showAlert();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this);
		builder.setTitle("Send a request");
		LayoutInflater inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = inflator.inflate(R.layout.alert, null);
		builder.setView(view);
		builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText etUserName = (EditText) view.findViewById(R.id.editUserName);
				String username = etUserName.getText().toString();
				if(TextUtils.isEmpty(username)){
					etUserName.setError("Please enter username");
					return;
				}
				if (addFriendSubscribe(username + "@" + app.SERVICE)) {
					Toast.makeText(getApplicationContext(), "Friend Request Send", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Friend Request Failed", Toast.LENGTH_SHORT).show();
				}
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		
		builder.create().show();
	}

	// private void changeColor(int newColor) {
	//
	// tabs.setIndicatorColor(newColor);
	//
	// // change ActionBar color just if an ActionBar is available
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	//
	// Drawable colorDrawable = new ColorDrawable(newColor);
	// Drawable bottomDrawable =
	// getResources().getDrawable(R.drawable.actionbar_bottom);
	// LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable,
	// bottomDrawable });
	//
	// if (oldBackground == null) {
	//
	// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	// ld.setCallback(drawableCallback);
	// } else {
	// getActionBar().setBackgroundDrawable(ld);
	// }
	//
	// } else {
	//
	// TransitionDrawable td = new TransitionDrawable(new Drawable[] {
	// oldBackground, ld });
	//
	// // workaround for broken ActionBarContainer drawable handling on
	// // pre-API 17 builds
	// //
	// https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
	// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
	// td.setCallback(drawableCallback);
	// } else {
	// getActionBar().setBackgroundDrawable(td);
	// }
	//
	// td.startTransition(200);
	//
	// }
	//
	// oldBackground = ld;
	//
	// //
	// http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
	// getActionBar().setDisplayShowTitleEnabled(false);
	// getActionBar().setDisplayShowTitleEnabled(true);
	//
	// }
	//
	// currentColor = newColor;
	//
	// }

	public void onColorClicked(View v) {

		int color = Color.parseColor(v.getTag().toString());
		// changeColor(color);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentColor", currentColor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentColor = savedInstanceState.getInt("currentColor");
		// changeColor(currentColor);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Your Friends", "Requests Sent", "Pending Requests" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			// case 0:
			// return RequestsFragment.newInstance(position);
			// case 2:
			// return PendingRequestsFragment.newInstance(position);
			// default:
			// return RequestsFragment.newInstance(position);

			case 0:
				return MyFriendListFragment.newInstance(position, FriendRequestActivity.this);
			case 1:
				return RequestsFragment.newInstance(position, FriendRequestActivity.this);
			case 2:
				return PendingRequestsFragment.newInstance(position, FriendRequestActivity.this);
			default:
				return MyFriendListFragment.newInstance(position, FriendRequestActivity.this);

			}
		}

	}

}
