package ma.ma;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {


    private TextView profileName,friendsNum;
    private Button sendRequest, decline;

    private DatabaseReference mUDB;

    private ProgressDialog progressDialog;

    private DatabaseReference ReqDatabase;

    private DatabaseReference friendDatabase;

    private DatabaseReference notifDatabase;

    private DatabaseReference rootRef;

    private FirebaseUser currentUser;

    private String current_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String uID = getIntent().getStringExtra("uID");

        rootRef = FirebaseDatabase.getInstance().getReference();

        //instantiating the variables

        mUDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);
        ReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notifDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        profileName= (TextView) findViewById(R.id.profile_name);
        friendsNum = (TextView) findViewById(R.id.friendsNum);
        sendRequest = (Button) findViewById(R.id.sendReq);
        decline = (Button) findViewById(R.id.Decline);

        current_s = "not_friends";

        decline.setVisibility(View.INVISIBLE);
        decline.setEnabled(false);
        //progress dialog when users are loaded
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Users");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mUDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            //setting profile name from the value in the "name" child
                String dName = dataSnapshot.child("name").getValue().toString();
                profileName.setText(dName);


                //friends list/request

                ReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(uID)){
                            //passes request type as a string
                            String req_type = dataSnapshot.child(uID).child("request_type").getValue().toString();
                                //if the request type is "received"
                            if(req_type.equals("received")){
                                //current state is request received
                                //sendRequest.setEnabled(true);
                                current_s = "req_received";
                                //sent button is set as "accept request"
                                sendRequest.setText("Accept Request");
                                //sets decline button to visible
                                decline.setVisibility(View.VISIBLE);
                                decline.setEnabled(true);

                            }else if(req_type.equals("sent")){
                                //if the request type is sent, the current state is "Request sent"
                                current_s = "req_sent";
                                //button text is set to "cancel friend request"
                                sendRequest.setText("Cancel Friend Request ");
                                //decline is set invisible
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            }
                                //the dialog box will be dismissed
                            progressDialog.dismiss();



                        } else{
                            //as it is a single event, get the current user's user id
                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //if the current state is friends,
                                    if(dataSnapshot.hasChild(uID)){
                                        current_s = "friends";
                                        //set the button text to "unfriend this person"
                                        sendRequest.setText("Unfriend this Person");
                                        //set decline button to invisible
                                        decline.setVisibility(View.INVISIBLE);
                                        decline.setEnabled(false);
                                    }
                                    //progress dialog will be dismissed
                                    progressDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    progressDialog.dismiss();
                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest.setEnabled(false);

                if(current_s.equals("not_friends")){
                    //if the current state is "not friends"
                    //creating a database reference
                    DatabaseReference newNotificationref = rootRef.child("notifications").child(uID).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", currentUser.getUid());
                    notificationData.put("type", "request");
                    //creating a hashmap to store the notifications in the database
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + currentUser.getUid() + "/" + uID + "/request_type", "sent");
                    requestMap.put("Friend_req/" + uID + "/" + currentUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + uID + "/" + newNotificationId, notificationData);
                    //the rootref is updated
                    rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //if there is an error in sending a request, a toast will appear
                            if(databaseError != null){

                                Toast.makeText(Profile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {
                                //else, the current state is "request sent"
                                //and the button will be set to "Cancel friend request"
                                current_s = "req_sent";
                                sendRequest.setText("Cancel Friend Request");

                            }

                            sendRequest.setEnabled(true);


                        }
                    });

                }


                //cancel
                    //if the current state is "request sent"
                if(current_s.equals("req_sent")){
                    //in the database, remove value
                    ReqDatabase.child(currentUser.getUid()).child(uID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //the user id is removed of the current user
                            ReqDatabase.child(uID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //current state is not friends
                                    //button set text set to "send request"
                                    sendRequest.setEnabled(true);
                                    current_s = "not_friends";
                                    sendRequest.setText("Send Request");
                                    //decline button is set to invisible
                                    decline.setVisibility(View.INVISIBLE);
                                    decline.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                    //Request Received
                //if current state is request received
                if(current_s.equals("req_received")){
                    //date is stored in the form of a string
                   final String date = DateFormat.getDateTimeInstance().format(new java.util.Date());
                   //create a new hashmap
                    Map friendsMap = new HashMap();
                    //put into the map the date and the user id
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + uID + "/date", date);
                    friendsMap.put("Friends/" + uID + "/"  + currentUser.getUid() + "/date", date);

                    friendsMap.put("Friend_req/" + currentUser.getUid() + "/" + uID, null);
                    friendsMap.put("Friend_req/" + uID + "/" + currentUser.getUid(), null);

                    //update the rootref
                    rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){
                                // if the current state is "friends"
                                sendRequest.setEnabled(true);
                                current_s = "friends";
                                //change the button to "unfriend this person"
                                sendRequest.setText("Unfriend this Person");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {
                                //display error message through a toast

                                String error = databaseError.getMessage();

                                Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }


                if(current_s.equals("friends")){
                    //creating a hashmap for friends
                    //storing the user's id
                    Map ufMap = new HashMap();
                    ufMap.put("Friends/" + currentUser.getUid() + "/" + uID, null);
                    ufMap.put("Friends/" + uID + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(ufMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){
                                //there are no errors
                                //current state is "not friends"
                                current_s = "not_friends";
                                //update text to "Send friend request"
                                sendRequest.setText("Send Friend Request");
                                //decline button is set to invisible
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {
                                //display error message through a toast
                                String error = databaseError.getMessage();

                                Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();


                            }

                            sendRequest.setEnabled(true);

                        }
                    });

                }

            }
        });
    }
}
