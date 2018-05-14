package ma.ma;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private RecyclerView friendlist;
    private DatabaseReference friendDB;
    private DatabaseReference userDB;
    private FirebaseAuth fbAuth;
    private String currentUid;
    private View mView;



    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate the layout
        mView = inflater.inflate(R.layout.fragment_friend, container, false);
        //inflated fragment requires recyclerview to access the friendsnum
        friendlist = (RecyclerView) mView.findViewById(R.id.friendsNum);
        fbAuth = FirebaseAuth.getInstance();

        //gets the user id of the current user
        currentUid = fbAuth.getCurrentUser().getUid();
        //database reference points to the child called "friends" in the database and gets the current uer id
        friendDB = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUid);
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        //prevents user being loaded repeatedly (offline feature)
        friendDB.keepSynced(true);
        //database reference points to the users child
        friendDB = FirebaseDatabase.getInstance().getReference().child("Users");
        friendDB.keepSynced(true);

        //sets the recyclerview size to true
        friendlist.setHasFixedSize(true);
        //sets the layoutmanager to new layout manager
        friendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        //return mView View object
        return mView;
    }
    //https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
    @Override
    public void onStart() {
        super.onStart();
        // get the database reference for the child "Users",limit it to 50 and store it in a query variable
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);
        //configuring firebase adapter with firebase recycler options
        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(query, Friend.class)
                        .build();

        //new firebase recycler adapter creater
        final FirebaseRecyclerAdapter<Friend, FriendHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendHolder>(options) {

            @NonNull
            @Override

            public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflates the users_s_layout
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_s_layout,parent,false);
                //return friend view holder
                return new FriendHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendHolder holder, int position, @NonNull Friend model) {
                //setting the data of the holder by getting the date of the model
                holder.setDate(model.getDate());
                //getting the key for the reference with the value of "position"
                final String lUid = getRef(position).getKey();
                //adding value event listener
                userDB.child(lUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    //gets the name from the users in the database
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //retrieving user name and whether they are online from the database
                        final String userName = dataSnapshot.child("name").getValue().toString();

                        //if the datasnapshot contains online
                        if(dataSnapshot.hasChild("online")) {
                            //retrieve their value and store it in the string
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            //set the user online on the holder
                            holder.setuOnline(userOnline);
                        }
                        //setting the holder name as the userName
                        holder.setName(userName);



                        holder.itemView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                //alert dialog is created where when the user clicks on another user,
                                //they will be given two options

                                CharSequence options[] = new CharSequence[]{ "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                //setting the title for the alert dialog
                                builder.setTitle("Select Options");

                                builder.setItems(options, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int op) {
                                        //click each item to direct to a different screen
                                        //return op as the position
                                        if(op == 0){
                                            //sends to the chat page to send a message to the user
                                            Intent cIntent = new Intent(getContext(), Chat.class);
                                            cIntent.putExtra("user_id", lUid);
                                            cIntent.putExtra("user_name", userName);
                                            startActivity(cIntent);

                                        }

                                    }
                                });
                                //shows the alert dialog
                                builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        //associates an adapter with the list
        friendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class FriendHolder extends RecyclerView.ViewHolder {
        //creating new view object mview
        View mView;

        public FriendHolder(View itemView) {

            super(itemView);

            mView = itemView;
        }

        //set the date in the status
        public void setDate(String date) {
            TextView userStatusView = (TextView) itemView.findViewById(R.id.user_status);
            userStatusView.setText(date);
        }
        //setting the name
        public void setName(String name) {
            //adds reference to the textview
            TextView userNameView = (TextView) itemView.findViewById(R.id.user_name);
            //text that you receive from the recyclerview
            userNameView.setText(name);
        }
        //if the user is online, the image of a green dot will appear next to their name
        public void setuOnline(String usersOnline) {
            ImageView userOnline = (ImageView) itemView.findViewById(R.id.online);
            //if the user is online
            if (usersOnline.equals("true")) {
                //set the userOnline visibility as visible
                userOnline.setVisibility(View.VISIBLE);
                //else it will be set invisible
            } else {
                //set the userOnline visibility as invisible
                userOnline.setVisibility(View.INVISIBLE);

            }
        }
    }
}



