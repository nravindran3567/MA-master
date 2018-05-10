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

    //use to retrieve user id
    private FirebaseAuth fbAuth;


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
        //variables
        public TextView msg_txt;
        public CircleImageView profilePic;


        public MessageViewHolder(View view) {
            super(view);
            //layout
            msg_txt = (TextView) view.findViewById(R.id.msg_txt_layout);
            profilePic = (CircleImageView) view.findViewById(R.id.msg_pic_layout);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        //instantiating the firebase auth
        fbAuth = FirebaseAuth.getInstance();
        //getting the current user's id and storing it in a string
        String cUID = fbAuth.getCurrentUser().getUid();
        //get the position of the message in the list and store in c.
        Message c = mList.get(position);
        //get the sender of the message and store it in a string
        String fuser = c.getFrom();
        //get the type of message sent
        String message_type = c.getType();
            //if the from user is the current user
        if(fuser.equals(cUID)){
            //set their textview to dark grey
            holder.msg_txt.setBackgroundColor(Color.DKGRAY);
            //and set the message to white
            holder.msg_txt.setTextColor(Color.WHITE);
        } else{
            //whereas if it is the recipient, //
            // set their textview to the background in the layout
            holder.msg_txt.setBackgroundResource(R.drawable.msg_txt_bg);
            //and set the text colour to black
            holder.msg_txt.setTextColor(Color.BLACK);
        }

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
