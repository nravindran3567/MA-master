package ma.ma;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nitharani on 27/04/2018.
 */

//gets the data from the console and stores it within a listview

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    //list that holds the messages
    private List<Message> mList;
    private DatabaseReference mUserDatabase;

    //use to retrieve user id
    //private FirebaseAuth mAuth;


    public MessageAdapter(List<Message> mList) {
        this.mList = mList;

    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //adding the layout
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_s_layout, parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView msg_txt;
        public CircleImageView profilePic;
        //public TextView displayName;

        public MessageViewHolder(View view) {
            super(view);

            msg_txt = (TextView) view.findViewById(R.id.msg_txt_layout);
            profilePic = (CircleImageView) view.findViewById(R.id.msg_pic_layout);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {


//        String cUID = mAuth.getInstance().getCurrentUser().getUid();
//        Log.d("UID", "CUID" + cUID);
//
//        Message m = mList.get(position);
//        //get the from id which is stored in Message - message.class
//        //get this value as a string
//        //get the from id stored in the message
//        String fromU = m.getFrom();
//        //Log.d("FROMU","FROMU"+cUID);
//        //if statement for the layout changes
//        if (fromU.equals(cUID)) {
//
//            //setting the background colour and text colour for current user's messages
//
//            holder.msg_txt.setBackgroundColor(Color.WHITE);
//            holder.msg_txt.setTextColor(Color.BLACK);
//
//        } else {
//            //background and text colour for the other user
//            holder.msg_txt.setBackgroundResource(R.drawable.msg_txt_bg);
//            holder.msg_txt.setTextColor(Color.BLACK);
//
//        }
//
//        holder.msg_txt.setText(m.getMessage());
//    }
        Message c = mList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {
            holder.msg_txt.setText(c.getMessage());
        } else {
            holder.msg_txt.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        //returning the size of the message
        Log.d("size", "size" + mList.size());

        return mList.size();
    }

}
