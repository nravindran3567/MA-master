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

    private DatabaseReference convoDB;
    private DatabaseReference msgDB;
    private DatabaseReference uDB;

    private FirebaseAuth mAuth;
    private String cUID;
    private View mView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        convoList = (RecyclerView) mView.findViewById(R.id.convoList);
        mAuth = FirebaseAuth.getInstance();

        cUID = mAuth.getCurrentUser().getUid();
        convoDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(cUID);
        convoDB.keepSynced(true);

        uDB = FirebaseDatabase.getInstance().getReference().child("Users");
        msgDB = FirebaseDatabase.getInstance().getReference().child("messages").child(cUID);
        uDB.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        convoList.setHasFixedSize(true);
        convoList.setLayoutManager(linearLayoutManager);

        return mView;


    }

    @Override
    public void onStart() {
        super.onStart();

        //arrange according to timestamp
        Query convoQuery = convoDB.orderByChild("timestamp");

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);

        FirebaseRecyclerOptions<Convo> options =
                new FirebaseRecyclerOptions.Builder<Convo>()
                        .setQuery(query, Convo.class)
                        .build();


        FirebaseRecyclerAdapter<Convo, ConvoHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Convo, ConvoHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final ConvoHolder holder, int position, @NonNull final Convo model) {

                final String lUID = getRef(position).getKey();
                //creates a query that points the message database to the user
                //and limits to the last one message
                Query lastMessageQuery = msgDB.child(lUID).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener(){

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //gets the data of the last message and displays it
                        String data = dataSnapshot.child("messages").getValue().toString();
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

                uDB.child(lUID).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);

                        }

                        holder.setName(userName);
                        //holder.setUserImage(userThumb, getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public ConvoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_s_layout,parent,false);
                return new ConvoHolder(view);

            }


        };

        convoList.setAdapter(firebaseConvAdapter);
        firebaseConvAdapter.startListening();
    }


    private class ConvoHolder extends RecyclerView.ViewHolder {
        View mView;
        public ConvoHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessage(String data, boolean isSeen) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_name);
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
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.online);

            if(userOnline.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);
        }
    }
}
