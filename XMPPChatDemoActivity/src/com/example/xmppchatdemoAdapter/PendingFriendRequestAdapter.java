package com.example.xmppchatdemoAdapter;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmppchatdemoactivity.ChatApp;
import com.example.xmppchatdemoactivity.R;

public class PendingFriendRequestAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<RosterEntry> friendList;
	private LayoutInflater inflater;
	private ChatApp app;
	private String nickname;
	private String idExtension;

	public PendingFriendRequestAdapter(Context context, ArrayList<RosterEntry> friendList) {
		this.context = context;
		this.friendList = friendList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return friendList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return friendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class ViewHolderItem {
		private TextView tvFriendName;
		private TextView tvAccept;
		private TextView tvDenny;
	}

	@Override
	public View getView(final int position, View v, ViewGroup arg2) {

		ViewHolderItem holder;
		if (v == null) {
			holder = new ViewHolderItem();
			v = inflater.inflate(R.layout.list_row_pending_friend_list, null);
			holder.tvFriendName = (TextView) v.findViewById(R.id.tvFriendName);
			holder.tvAccept = (TextView) v.findViewById(R.id.tvAccept);
			holder.tvDenny = (TextView) v.findViewById(R.id.tvDenny);
			v.setTag(holder);
		} else {
			holder = (ViewHolderItem) v.getTag();
		}
		app = ChatApp.getInstance();
		if (friendList.get(position) != null) {
			holder.tvFriendName.setText("" + friendList.get(position).getUser());
		}
		// accept friend request
		holder.tvAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AcceptFriendRequestSubscribed(friendList.get(position).getUser())) {
					Toast.makeText(context, "Accept Friend Request Successfully", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Accept Friend Request fail", Toast.LENGTH_SHORT).show();
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

		// denny friend request
		holder.tvDenny.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (RejectFriendRequest(friendList.get(position).getUser())) {
					Toast.makeText(context, "Denny Friend Request Successfully", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Denny Friend Request fail", Toast.LENGTH_SHORT).show();
				}

			}

			private boolean RejectFriendRequest(String jid) {

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
		return v;
	}

}
