package ma.ma;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Chat extends AppCompatActivity {

    private String cUser;
    private Toolbar cToolbar;

    private DatabaseReference rootRef;

    private TextView titleView;
    private TextView lastSeen;
    private CircleImageView dp;

    private FirebaseAuth mAuth;
    private String cUserID;

    private ImageButton addBtn;
    private ImageButton sendBtn;
    private EditText msg;

    private RecyclerView msgsList;
    private final List<Message> messagesList = new ArrayList<>();
    private LinearLayoutManager lLayout;
    //setting the adapter
    private MessageAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        cToolbar = (Toolbar) findViewById(R.id.chat_bar);
        setSupportActionBar(cToolbar);

        ActionBar aBar = getSupportActionBar();

        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setDisplayShowCustomEnabled(true);
        //getting reference to the firebase database instance
        rootRef = FirebaseDatabase.getInstance().getReference();
        //getting firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //getting the user ID
        cUserID = mAuth.getCurrentUser().getUid();
        //getting the value of the string that was put in the friend fragment called user_id
        cUser = getIntent().getStringExtra("user_id");
        //getting the value of the string that was put in the friend fragment called user_name
        String userName = getIntent().getStringExtra("user_name");

        //getSupportActionBar().setTitle(userName);

        //inflates the chat bar
        LayoutInflater lInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View aBarView = lInflater.inflate(R.layout.chat_bar,null);

        aBar.setCustomView(aBarView);

        //custom action bar

        titleView = (TextView) findViewById(R.id.bar_name);
        lastSeen = (TextView) findViewById(R.id.bar_seen);
        dp = (CircleImageView) findViewById(R.id.bar_image);

        addBtn = (ImageButton) findViewById(R.id.add);
        sendBtn = (ImageButton) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.message);

        adapter = new MessageAdapter(messagesList);
        msgsList = (RecyclerView) findViewById(R.id.msg_list);
        //instantiate the recyclerview
        lLayout = new LinearLayoutManager(this);
        //assigning it to msgList
        msgsList.setHasFixedSize(true);
        msgsList.setLayoutManager(lLayout);

        msgsList.setAdapter(adapter);

        //calling the loadMessages function
        loadMessages();

        //the bar name has been set as the user's name
        titleView.setText(userName);

        rootRef.child("Users").child(cUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieves their image and whether they are online as a string from the database
                String online = dataSnapshot.child("online").getValue().toString();
                String pic = dataSnapshot.child("image").getValue().toString();
                //if the user is online
                if(online.equals("true")){
                    //set the lastSeen textView as "Online"
                    lastSeen.setText("Online");
                }else{
                    //new GetTimeAgo object created
                    GetTimeAgo gTA = new GetTimeAgo();
                    //converting string time to long
                    long lTime = Long.parseLong(online);
                    //
                    String lSeenTime = gTA.getTimeAgo(lTime,getApplicationContext());
                    //set the lastseen textView as lSeenTime value
                    lastSeen.setText(lSeenTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //adding valueeventlistener to check when there is a change in the cUserID string of the "chat" child
        rootRef.child("Chat").child(cUserID).addValueEventListener(new ValueEventListener() {
            @Override
            //when the data changes
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if it contains the user id
                if(!dataSnapshot.hasChild(cUser)){
                    //new hashmap is created called cAddMap
                    Map cAddMap = new HashMap();
                    // keys and values added to the hashmap
                    cAddMap.put("seen", false);
                    cAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    //new hashmap is created called cAddMap
                    Map cUMap = new HashMap();
                    //storing cUserID and cUser values to the "chat" child along with the cAddMap values
                    cUMap.put("Chat/" + cUserID + "/"+ cUser, cAddMap);
                    //storing the same values in the other user id
                    cUMap.put("Chat/" + cUser+ "/" + cUserID, cAddMap);

                    rootRef.updateChildren(cAddMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //checking for error
                            if(databaseError != null){
                                Log.d("Chat log", databaseError.getMessage().toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when the send button is pressed the sendMessage() function is called
                sendMessage();
            }
        });
    }

    private void loadMessages(){
        //retrieve the data
        //childeventlistener added
        rootRef.child("messages").child(cUserID).child(cUser).addChildEventListener(new ChildEventListener() {
            @Override
            //when the child is added
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //once the messages are received, they are received as datasnapshot, the value is then saved in a Message object
                Message msg = dataSnapshot.getValue(Message.class);
                //Message object msg added to the messagesList arrayList
                messagesList.add(msg);
                //notify adapter when the dataset has been changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        //string variable created which holds the value retrieved from the message box
        String message = msg.getText().toString();
        //id the message variable is not empty
        if(!TextUtils.isEmpty(message)){

            String cURef = "messages/" + cUserID + "/" + cUser;
            String chatuRef = "messages/" + cUser + "/" + cUserID;
            //messages being pushed to the messages child
            DatabaseReference msg_push = rootRef.child("messages").child(cUserID).child(cUser).push();
            //getting the key of message push
            String push_id = msg_push.getKey();

            //adding it to the database
            //new hashmap created
            Map mMap = new HashMap();
            //keys and values added to the hashmap
            mMap.put("message", message);
            mMap.put("seen", false);
            mMap.put("type", "text");
            mMap.put("time", ServerValue.TIMESTAMP);
            mMap.put("from", cUserID);
            //the hash map below adds the message to the message child of both sender and the receiver
            Map mUsermap = new HashMap();
            mUsermap.put(cURef + "/" + push_id, mMap);
            mUsermap.put(chatuRef + "/" + push_id, mMap);
            //after the message is sent the textbox is cleared
            msg.setText(" ");

            rootRef.updateChildren(mUsermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    //error checking
                    if(databaseError != null){
                        Log.d("Chat log", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
