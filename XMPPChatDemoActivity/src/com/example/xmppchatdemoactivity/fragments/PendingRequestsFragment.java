package com.example.xmppchatdemoactivity.fragments;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xmppchatdemoAdapter.PendingFriendRequestAdapter;
import com.example.xmppchatdemoactivity.ChatApp;
import com.example.xmppchatdemoactivity.FriendRequestActivity.MyPagerAdapter;
import com.example.xmppchatdemoactivity.R;

public class PendingRequestsFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;
	private ChatApp app;
	private ArrayList<RosterEntry> fArrayList;
	private ArrayList<RosterEntry> stringFarrrayList = new ArrayList<RosterEntry>();
	private PendingFriendRequestAdapter pendingFriendRequestAdapter;
	private static Context mContext;

	public static PendingRequestsFragment newInstance(int position, Context context) {
		PendingRequestsFragment f = new PendingRequestsFragment();
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
			// item.getName() returns ranjeet while item.getUser() returns ranjeet@xmpp.deepco.com.br
			Presence entryPresence = roster.getPresence(item.getUser());

			Log.d("XMPPChatDemoActivity", "Presence Status: " + entryPresence.getStatus());
		//	if(("to").equalsIgnoreCase(item.getStatus().toString())) 
				//stringFarrrayList.add(item.getName());
				
				if (!item.getType().name().equals("")) {
					if (item.getType().name().equalsIgnoreCase("from")) {
						stringFarrrayList.add(item);
					}
				}
				
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.friend_listview, null);
		ListView list = (ListView) v.findViewById(R.id.lvFriendId);
		//pendingFriendRequestAdapter = new PendingFriendRequestAdapter(this, PendingFriendRequestAdapter)
		pendingFriendRequestAdapter = new PendingFriendRequestAdapter(mContext,	stringFarrrayList);
		list.setAdapter(pendingFriendRequestAdapter);
		pendingFriendRequestAdapter.notifyDataSetChanged();

		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringFarrrayList);
		//list.setAdapter(adapter);
		
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
//		Toast.makeText(getActivity(), "resume - " + position, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		Toast.makeText(getActivity(), "paused - " + position, Toast.LENGTH_SHORT).show();
	}
	
}