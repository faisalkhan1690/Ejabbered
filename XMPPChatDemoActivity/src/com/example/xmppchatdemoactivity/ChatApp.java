package com.example.xmppchatdemoactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class ChatApp {

	private static ChatApp instance = null;
	public static final String HOST = "192.168.1.64";
	//public static final String HOST = "192.168.0.103";
	
	//fndnj
	public static final int PORT = 5222;
	// public static final String SERVICE = "jabber.org";
	public static final String SERVICE = "xmpp.deepco.com.br";// 192.168.1.64http://localhost:5280/admin/
	private MessageRcd msgrcd;

	private String username;

	public String getUsername() {
		return username;
	}

	private ChatApp() {
		// private constructor
	}

	public static ChatApp getInstance() {
		if (instance == null) {
			instance = new ChatApp();
		}
		return instance;
	}

	// make private
	public XMPPConnection connection;
	private ArrayList<String> messages = new ArrayList<String>();
	private Handler mHandler = new Handler();
	private Context context;

	/**
	 * Send message to the specified address
	 * 
	 * @param to
	 * @param message
	 */
	public void sendMessage(String to, String message, String memberChat) {
		Message msg = null;
		if (memberChat.equals("single")) {

			msg = new Message(to, Message.Type.chat);
			msg.setBody(message);
		} else if (memberChat.equals("group")) {
			msg = new Message(to, Message.Type.groupchat);
			msg.setBody(message);
			connection.sendPacket(msg);
		}

		if (connection != null) {
			connection.sendPacket(msg);
		}
	}

	/**
	 * User registration on server
	 * 
	 * @param username
	 * @param password
	 * @throws XMPPException
	 */
	public boolean register(String username, String password) {

		try {
			// --create account on server--By Rs---
			AccountManager am = new AccountManager(connection);
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("username", username);
			attributes.put("password", password);
			am.createAccount(username, password, attributes);

			Registration reg = new Registration();
			reg.setType(IQ.Type.SET);
			reg.setTo(connection.getServiceName());
			PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
			PacketCollector collector = connection.createPacketCollector(filter);
			connection.sendPacket(reg);
			return true;
			// ---end---
		} catch (XMPPException e) {
		}
		return false;
	}

	/**
	 * Logging in and getting friend list and its status
	 * 
	 * @param context
	 */
	public boolean connect(Context context) {
		this.context = context;
		if (connection == null) {
			// Create a connection
			ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
			connection = new XMPPConnection(connConfig);
			try {
				// connConfig.setSASLAuthenticationEnabled(true);
				connection.connect();
				Log.i("XMPPChatDemoActivity", "Connected to " + connection.getHost());
				return true;
			} catch (XMPPException ex) {
				Log.e("XMPPChatDemoActivity", "Failed to connect to " + connection.getHost());
				Log.e("XMPPChatDemoActivity", ex.toString());
				setConnection(null);
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * login
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(final String username, final String password) {
		// if(!TextUtils.isEmpty(connection.getUser())){
		// return true;
		// }
		try {
			connection.login(username, password);
			this.username = connection.getUser();
			setStatusAvailable();
			logInfoMessage("Logged in as " + connection.getUser());
			return true;
		} catch (XMPPException ex) {
			logErrorMessage("Failed to log in as " + username);
			logErrorMessage(ex.toString());
			setConnection(null);
			return false;
		}
	}

	private int logInfoMessage(String message) {
		return Log.i("XMPPChatDemoActivity", message);
	}

	private int logErrorMessage(String message) {
		return Log.i("XMPPChatDemoActivity", message);
	}

	/**
	 * Set status available after logging in.
	 */
	private void setStatusAvailable() {
		// Set the status to available
		Presence presence = new Presence(Presence.Type.available);
		connection.sendPacket(presence);
		setConnection(connection);
	}

	/**
	 * Return Friend list
	 * 
	 * @return
	 */
	public Collection<RosterEntry> getFriendList() {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			Log.d("XMPPChatDemoActivity", "--------------------------------------");
			Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
			Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
			Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
			Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
			Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
			Presence entryPresence = roster.getPresence(entry.getUser());

			Log.d("XMPPChatDemoActivity", "Presence Status: " + entryPresence.getStatus());
			Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
			Presence.Type type = entryPresence.getType();
			if (type == Presence.Type.available)
				Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
			Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);

		}
		return entries;
	}

	public Roster getRoster() {
		if(connection != null)
		{
			return connection.getRoster();
		}
		return null;
	}
	
	/**
	 * Return Friend list
	 * 
	 * @return
	 */
	public ArrayList<String> getFriendListString() {
		ArrayList<String> item = new ArrayList<String>();
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			String data = "";
			Log.d("XMPPChatDemoActivity", "--------------------------------------");
			Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
			Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
			Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
			Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
			Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
			Presence entryPresence = roster.getPresence(entry.getUser());

			Log.d("XMPPChatDemoActivity", "Presence Status: " + entryPresence.getStatus());
			Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
			Presence.Type type = entryPresence.getType();
			if (type == Presence.Type.available)
				Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
			Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);

			data += entry + "\n";
			data += entry.getUser() + "\n";
			data += entry.getName() + "\n";
			data += entry.getStatus() + "\n";
			data += entry.getType() + "\n";
			data += entryPresence.getStatus() + "\n";
			data += entryPresence.getType() + "\n";
			data += entryPresence + "\n";

			item.add(data);
		}
		return item;
	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					final Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());
						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody() + " from " + fromName);
						messages.add(fromName + ":");
						messages.add(message.getBody());
						// Add the incoming message to the list view
						playBeep();
						mHandler.post(new Runnable() {
							public void run() {
								// TODO check split
								msgrcd.onMessageReceived((message.getFrom().split("@"))[0].toString() + ":" + message.getBody());
							}
						});
					}
				}
			}, filter);
		}
	}

	/**
	 * Plays device's default notification sound
	 */
	public void playBeep() {

		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface MessageRcd {
		public void onMessageReceived(String message);
	}

	public void initializeListener(MessageRcd rcd) {
		this.msgrcd = rcd;
	}

	public Type getStatusOfUser(String userCompleteAddress) {

		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			if (entry.getUser().equals(userCompleteAddress)) {
				Presence entryPresence = roster.getPresence(entry.getUser());
				Presence.Type type = entryPresence.getType();
				return type;
			}
		}
		return null;
	}

	// public void offline() {
	// Presence presence = new Presence(Presence.Type.unavailable);
	// connection.sendPacket(presence);
	// setConnection(connection);
	// }
}
