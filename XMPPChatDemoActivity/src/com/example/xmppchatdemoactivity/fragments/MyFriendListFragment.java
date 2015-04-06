package com.example.xmppchatdemoactivity.fragments;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.example.xmppchatdemoAdapter.MyFriendListAdapter;
import com.example.xmppchatdemoAdapter.PendingFriendRequestAdapter;
import com.example.xmppchatdemoactivity.ChatApp;
import com.example.xmppchatdemoactivity.FriendRequestActivity.MyPagerAdapter;
import com.example.xmppchatdemoactivity.R;

public class MyFriendListFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private ChatApp app;
	private ArrayList<RosterEntry> fArrayList;
	//private ArrayList<String> stringFarrrayList = new ArrayList<String>();
	private ArrayList<RosterEntry> stringFarrrayList = new ArrayList<RosterEntry>();
	private MyFriendListAdapter myFriendListAdapter;
	private int position;
	private static Context mContext;

	public static MyFriendListFragment newInstance(int position, Context context) {
		MyFriendListFragment f = new MyFriendListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		mContext = context;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
		app = ChatApp.getInstance();
		Roster roster = app.getRoster();
		fArrayList = new ArrayList<RosterEntry>(app.getFriendList());
		for (RosterEntry item : fArrayList) {
			Presence entryPresence = roster.getPresence(item.getUser());

			Log.d("XMPPChatDemoActivity", "Presence Status: " + entryPresence.getStatus());
			// if(("to").equalsIgnoreCase(item.getStatus().toString()))
			if (!item.getType().name().equals("")) {
				if (item.getType().name().equalsIgnoreCase("both")) {
					stringFarrrayList.add(item);
				}
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.friend_listview, null);
		ListView list = (ListView) v.findViewById(R.id.lvFriendId);
		myFriendListAdapter = new MyFriendListAdapter(mContext,	stringFarrrayList);
		list.setAdapter(myFriendListAdapter);
		myFriendListAdapter.notifyDataSetChanged();

		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringFarrrayList);
		//list.setAdapter(adapter);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Toast.makeText(getActivity(), "resume - " + position,
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPause() {
		super.onPause();
		// Toast.makeText(getActivity(), "paused - " + position,
		// Toast.LENGTH_SHORT).show();
	}

}