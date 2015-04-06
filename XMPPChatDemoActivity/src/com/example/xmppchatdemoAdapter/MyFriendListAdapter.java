package com.example.xmppchatdemoAdapter;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
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

public class MyFriendListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<RosterEntry> friendList;
	private LayoutInflater inflater;
	private ChatApp app;
	private String nickname;
	private String idExtension;

	public MyFriendListAdapter(Context context, ArrayList<RosterEntry> friendList) {
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
		private TextView tvDenny;
	}

	@Override
	public View getView(final int position, View v, ViewGroup arg2) {

		ViewHolderItem holder;
		if (v == null) {
			holder = new ViewHolderItem();
			v = inflater.inflate(R.layout.list_row_myfriend_list, null);
			holder.tvFriendName = (TextView) v.findViewById(R.id.tvFriendName);
			holder.tvDenny = (TextView) v.findViewById(R.id.tvDenny);
			v.setTag(holder);
		} else {
			holder = (ViewHolderItem) v.getTag();
		}
		app = ChatApp.getInstance();
		if (friendList.get(position) != null) {
			holder.tvFriendName.setText("" + friendList.get(position).getUser());
		}

		// denny friend request
		holder.tvDenny.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (BlockFriend(friendList.get(position).getUser())) {
					Toast.makeText(context, "Block Friend Successfully", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Block Friend fail", Toast.LENGTH_SHORT).show();
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

			}
		});
		return v;
	}

}
