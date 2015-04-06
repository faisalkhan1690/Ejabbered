package com.example.xmppchatdemoactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmppchatdemoactivity.ChatApp.MessageRcd;

public class XMPPChatDemoActivity extends Activity implements MessageRcd {

	// public static final String HOST = "talk.google.com";
	// public static final int PORT = 5222;
	// public static final String SERVICE = "gmail.com";
	// public static final String USERNAME = "rsranjeet086@gmail.com";
	// public static final String PASSWORD = "xxxxxxx";

	// public static final String HOST = "jabber.org";
	// public static final String HOST = "192.168.0.103";
	// public static final int PORT = 5222;

	// public static final String USERNAME = "hisham";
	// public static final String PASSWORD = "hisham";

	// new module dev

	private EditText recipient;
	private EditText textMessage;
	private ListView listview;
	private Button btn_registration;
	private ChatApp app;
	MessageRcd rcd;

	private ImageView ivStatus;
	private ArrayList<String> messages = new ArrayList<String>();
	private TextView txtStatus;
	private String user;
	private TextView txtFileTransfer;
	private ImageView imgDocument;
	ImageView viewImage;
	private Object progressDialog;
	private String memberChat;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ivStatus = (ImageView) findViewById(R.id.ivStatus);
		txtStatus = (TextView) findViewById(R.id.txtStatus);
		viewImage = (ImageView) findViewById(R.id.viewImage);

		app = ChatApp.getInstance();
		rcd = this;
		app.initializeListener(rcd);

		recipient = (EditText) this.findViewById(R.id.toET);
		textMessage = (EditText) this.findViewById(R.id.chatET);
		listview = (ListView) this.findViewById(R.id.listMessages);
		setListAdapter();

		if (getIntent().getExtras() != null) {
			user = getIntent().getExtras().getString("email");
			recipient.setText(user);
			updateUserStatus();
			
			memberChat = getIntent().getExtras().getString("memberChat");
		}

		// Set a listener to send a chat text message
		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(textMessage.getText().toString())) {
					textMessage.setError("Can't send empty message");
					return;
				}
				app.sendMessage(getText(recipient), getText(textMessage),memberChat);
				messages.add(app.getUsername() + ":" + getText(textMessage));
				textMessage.setText("");
			}
		});

		// go to file transfer
		txtFileTransfer = (TextView) findViewById(R.id.txtFileTransfer);
		txtFileTransfer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), GroupList.class);
				startActivity(i);

			}
		});

		// Choose image from gallery
		imgDocument = (ImageView) findViewById(R.id.imgDocument);
		imgDocument.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
				Toast.makeText(XMPPChatDemoActivity.this, "true", Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(XMPPChatDemoActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory().toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}

				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);

					viewImage.setImageBitmap(bitmap);

					String path = android.os.Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;
					File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.w("path of image from gallery......******************.........", picturePath + "");
				viewImage.setImageBitmap(thumbnail);
				sendData(user, picturePath);
			}
		}
	}

	private void sendData(final String Receiver, final String Directory) {

		 
        Thread thread = new Thread() {
 
            public void run() {
 
                ServiceDiscoveryManager sdm = ServiceDiscoveryManager
                        .getInstanceFor(app.connection);
 
                if (sdm == null)
                sdm = new ServiceDiscoveryManager(app.connection);
 
                sdm.addFeature("http://jabber.org/protocol/disco#info");
 
                sdm.addFeature("jabber:iq:privacy");
               
                // Create the file transfer manager
                FileTransferManager manager = new FileTransferManager(
                        app.connection);
                FileTransferNegotiator.setServiceEnabled(app.connection, true);
 
                // Create the outgoing file transfer
                 OutgoingFileTransfer transfer = manager
                        .createOutgoingFileTransfer(Receiver + "/Smack");
                Log.i("transfere file", "outgoingfiletransfere is created");
               
  
               
         
              
                try {
 
                    OutgoingFileTransfer.setResponseTimeout(30000);
 
                    transfer.sendFile(new File(Directory), "Description");
 
                    Log.i("transfere file", "sending file");
 
   
                   
                    while (!transfer.isDone()) {
 
                        try {
                           Thread.sleep(1000);
                        Log.i("transfere file", "sending file status "
                                    + transfer.getStatus() + "progress: "
                                    + transfer.getProgress());
                            if (transfer.getStatus() == Status.error) {
                            transfer.cancel();
                                break;
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
 
 
            
 
                } catch (XMPPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
           
                Log.i("transfere file", "sending file done");
 
            }
        };
        thread.start();
	
		
	}

	private void updateUserStatus() {
		if (!TextUtils.isEmpty(user)) {
			Type type = app.getStatusOfUser(user);
			if (type == Type.available) {
				ivStatus.setImageResource(R.drawable.ic_green_online);
				txtStatus.setText("Online");
			} else {
				ivStatus.setImageResource(R.drawable.ic_red_offline);
				txtStatus.setText("Offline");
			}
		}
	}

	private String getText(EditText et) {
		return et.getText().toString().trim();
	}

	private void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
		listview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.sendMessage(getText(recipient), "Bye",memberChat);
		// app.offline();
		// try {
		// if (connection != null)
		// connection.disconnect();
		// } catch (Exception e) {
		//
		// }
	}

	@Override
	public void onMessageReceived(String message) {
		// if (message.equals("Bye")) {
		// updateUserStatus();
		// }
		messages.add(message);
		setListAdapter();
	}

}