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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Users");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mUDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dName = dataSnapshot.child("name").getValue().toString();
                profileName.setText(dName);


                //friends list/request

                ReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(uID)){

                            String req_type = dataSnapshot.child(uID).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                //sendRequest.setEnabled(true);
                                current_s = "req_received";
                                sendRequest.setText("Accept Request");

                                decline.setVisibility(View.VISIBLE);
                                decline.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                current_s = "req_sent";
                                sendRequest.setText("Cancel Friend Request ");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            }

                            progressDialog.dismiss();



                        } else{

                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(uID)){
                                        current_s = "friends";
                                        sendRequest.setText("Unfriend this Person");

                                        decline.setVisibility(View.INVISIBLE);
                                        decline.setEnabled(false);
                                    }
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


                    DatabaseReference newNotificationref = rootRef.child("notifications").child(uID).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", currentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + currentUser.getUid() + "/" + uID + "/request_type", "sent");
                    requestMap.put("Friend_req/" + uID + "/" + currentUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + uID + "/" + newNotificationId, notificationData);

                    rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(Profile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {

                                current_s = "req_sent";
                                sendRequest.setText("Cancel Friend Request");

                            }

                            sendRequest.setEnabled(true);


                        }
                    });

                }


                //cancel

                if(current_s.equals("req_sent")){
                    ReqDatabase.child(currentUser.getUid()).child(uID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ReqDatabase.child(uID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendRequest.setEnabled(true);
                                    current_s = "not_friends";
                                    sendRequest.setText("Send Request");

                                    decline.setVisibility(View.INVISIBLE);
                                    decline.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                    //Request Received

                if(current_s.equals("req_received")){

                   final String date = DateFormat.getDateTimeInstance().format(new java.util.Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + uID + "/date", date);
                    friendsMap.put("Friends/" + uID + "/"  + currentUser.getUid() + "/date", date);


                    friendsMap.put("Friend_req/" + currentUser.getUid() + "/" + uID, null);
                    friendsMap.put("Friend_req/" + uID + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                sendRequest.setEnabled(true);
                                current_s = "friends";
                                sendRequest.setText("Unfriend this Person");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }

//                decline.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Map declineMap = new HashMap();
//
//                        declineMap.put("Friend_req/" + currentUser.getUid() + "/" + uID, null);
//                        declineMap.put("Friend_req/" + uID + "/" + currentUser.getUid(), null);
//
//                        rootRef.updateChildren(declineMap, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                                if(databaseError == null)
//                                {
//
//                                    current_s = "not friends";
//                                    sendRequest.setText("Send Friend Request");
//
//                                }else{
//                                    String error = databaseError.getMessage();
//                                    Toast.makeText(Profile.this, error, Toast.LENGTH_LONG).show();
//                                }
//
//                                sendRequest.setEnabled(true);
//                            }
//                        });
//
//                    }
//                });

                if(current_s.equals("friends")){

                    Map ufMap = new HashMap();
                    ufMap.put("Friends/" + currentUser.getUid() + "/" + uID, null);
                    ufMap.put("Friends/" + uID + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(ufMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                current_s = "not_friends";
                                sendRequest.setText("Send Friend Request");

                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            } else {

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
