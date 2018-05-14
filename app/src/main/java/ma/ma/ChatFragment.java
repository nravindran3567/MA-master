package ma.ma;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView convoList;

    private DatabaseReference convoDB; //convo DatabaseReference
    private DatabaseReference msgDB;    //message DatabaseReference
    private DatabaseReference uDB;  //user DatabaseReference

    private FirebaseAuth fBAuth;
    private String cUID;
    private View mView;

    public ChatFragment() {
        //empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        //deals with the items in the dataset.
        convoList = (RecyclerView) mView.findViewById(R.id.convoList);
        fBAuth = FirebaseAuth.getInstance();
        //getting the current user ID
        cUID = fBAuth.getCurrentUser().getUid();
        //getting the database reference for the "Chat" child with the "cUID"
        convoDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(cUID);
        convoDB.keepSynced(true);

        uDB = FirebaseDatabase.getInstance().getReference().child("Users");
        msgDB = FirebaseDatabase.getInstance().getReference().child("message").child(cUID);
        //downloading the database and storing it on the user database reference variable
        uDB.keepSynced(true);
        //aligns the layoutmanager to new layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        //setting fixed size for the recycler view so that it doesn't change it's height or the width
        convoList.setHasFixedSize(true);
        // setting the layout
        convoList.setLayoutManager(linearLayoutManager);

        return mView;


    }

    @Override
    public void onStart() {
        super.onStart();

        //querying the data and arranging according to timestamp
        Query convoQuery = convoDB.orderByChild("timestamp");
        // querying the data and getting the reference of the child "Users"
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        //https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
        //﻿configuring firebase adapter with firebase recycler options
        FirebaseRecyclerOptions<Convo> options =
                new FirebaseRecyclerOptions.Builder<Convo>()
                        .setQuery(query, Convo.class)
                        .build();

        // new firebse recycler adapter created
        FirebaseRecyclerAdapter<Convo, ConvoHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Convo, ConvoHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final ConvoHolder holder, int position, @NonNull final Convo model) {
                //getting the key of position variable and storing it as a string
                final String lUID = getRef(position).getKey();
                //creates a query that points the message database to the user
                //and limits to the last one message
                Query lastMessageQuery = msgDB.child(lUID).limitToLast(1);
                //adding a child event listener
                lastMessageQuery.addChildEventListener(new ChildEventListener(){

                    @Override
                    //when a child is added
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //gets the data of the last message and displays it
                        String data = dataSnapshot.child("message").getValue().toString();
                        //sends the data and if it is seen
                        holder.setMessage(data, model.isSeen());

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
                //child event listener added
                uDB.child(lUID).addValueEventListener(new ValueEventListener() {

                    @Override
                    //when the data changes...
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //store the value of the child "name" in a string variable
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        //store the value of the child "thumb_image" in a string variable
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        // if there is a child called online
                        if(dataSnapshot.hasChild("online")) {
                            //store the value of the child "online" in a string variable
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            //the holder value will be set to the value of the userOnline variable
                            holder.setUserOnline(userOnline);

                        }
                        //name of the holder set to the userName variable
                        holder.setName(userName);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public ConvoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflate the layout
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_s_layout,parent,false);
                return new ConvoHolder(view);

            }


        };
        //setting the adapter as the conversation list
        convoList.setAdapter(firebaseConvAdapter);
        //start listening to the adapter
        firebaseConvAdapter.startListening();
    }


    private class ConvoHolder extends RecyclerView.ViewHolder {
        View mView;
        public ConvoHolder(View itemView) {
            super(itemView);
            //setting the mview View to itemView
            mView = itemView;
        }

        public void setMessage(String data, boolean isSeen) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_status);
            userStatusView.setText(data);

            if(!isSeen){
                // if it is not seen, the font of the text will be bold
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                //or it will be normal
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setUserOnline(String userOnline) {
            //green dot displayed when the user is online
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.online);
            //if the user is online,
            if(userOnline.equals("true")){
                //set the image to visible
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                //else set the image to invisible
                userOnlineView.setVisibility(View.INVISIBLE);

            }
        }
        //setter for the name
        public void setName(String name) {
            // storing the value of the user_name to the userNameView variable
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            //setting the text of the userNameView as the name variable
            userNameView.setText(name);
        }
    }
}
