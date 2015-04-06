package com.example.xmppchatdemoAdapter;

import java.util.ArrayList;

import org.jivesoftware.smack.RosterGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xmppchatdemoactivity.ChatApp;
import com.example.xmppchatdemoactivity.R;

public class MemberGroupListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<RosterGroup> groupName;
	private LayoutInflater inflater;
	private ChatApp app;

	public MemberGroupListAdapter(Context context, ArrayList<RosterGroup> groupName) {
		this.context = context;
		this.groupName = groupName;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groupName.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groupName.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class ViewHolderItem {
		private TextView tvGroupName;
	}

	@Override
	public View getView(final int position, View v, ViewGroup arg2) {

		ViewHolderItem holder;
		if (v == null) {
			holder = new ViewHolderItem();
			v = inflater.inflate(R.layout.member_group_list, null);
			holder.tvGroupName = (TextView) v.findViewById(R.id.tvGroupName);
			v.setTag(holder);
		} else {
			holder = (ViewHolderItem) v.getTag();
		}
		app = ChatApp.getInstance();
		if (groupName.get(position) != null) {
			holder.tvGroupName.setText(groupName.get(position).getName());
		}

		return v;
	}

}
