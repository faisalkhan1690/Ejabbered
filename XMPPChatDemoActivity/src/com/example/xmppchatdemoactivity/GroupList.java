package com.example.xmppchatdemoactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;

import com.example.xmppchatdemoAdapter.MemberGroupListAdapter;

public class GroupList extends Activity {

	private ChatApp app;
	private ArrayList<RosterGroup> memberGroupList = new ArrayList<RosterGroup>();
	private ListView lvGroupList;
	private MemberGroupListAdapter memberGroupListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_transfer);
		app = ChatApp.getInstance();

		lvGroupList = (ListView) findViewById(R.id.lvGroupList);
		//RosterGroup rosterGroup = (RosterGroup) app.connection.getRoster().getGroups();

		Roster roster = app.connection.getRoster();
		for (RosterGroup group : roster.getGroups()) {
			memberGroupList.add(group);
		}
		memberGroupListAdapter = new MemberGroupListAdapter(GroupList.this, memberGroupList);
		lvGroupList.setAdapter(memberGroupListAdapter);
		memberGroupListAdapter.notifyDataSetChanged();
		lvGroupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(getApplicationContext(), XMPPChatDemoActivity.class);
				intent.putExtra("email", memberGroupList.get(position).getName());
				intent.putExtra("memberChat", "group");
				startActivity(intent);

				// get member according to group

				// RosterGroup rosterGroup =
				// app.connection.getRoster().getGroup(memberGroupList.get(position).getName());
				// if (rosterGroup != null) {
				//
				// for (RosterEntry entry : rosterGroup.getEntries()) {
				//
				// Log.i("entry", "" + entry.getUser());
				// }
				//
				// }

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds options to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
