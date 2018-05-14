package ma.ma;

import android.appwidget.AppWidgetHost;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersActivity extends AppCompatActivity {
    //creating variables
    private Toolbar toolbar;
    private RecyclerView uList;
    private DatabaseReference mUDB;
    private String TAG = "UsersActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        //storing toolbar in toolbar variable
        toolbar = (Toolbar) findViewById(R.id.u_appbar);
        //setting up toolbar as actionbar
        setSupportActionBar(toolbar);
        //setting the title for the action bar
        getSupportActionBar().setTitle("All users");
        //icon and title in the action bar made clickable
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getting the database reference for the
        mUDB = FirebaseDatabase.getInstance().getReference().child("Users");


        uList = (RecyclerView) findViewById(R.id.users_list);
        //ensures the size does not change when items are removed or deleted
        uList.setHasFixedSize(true);
        //the arrangements of the layout is controlled by layoutmanger
        //this creates a new layoutmanager
        uList.setLayoutManager(new LinearLayoutManager(this));
    }

//https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md

    @Override
    protected void onStart() {
        super.onStart();
        //getting the reference from child "Users" and storing to the query variable
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);
        //configuring firebase adapter with firebase recycler options
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        //creating firebase recycler adapter
        final FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                //setting the name of the holder by calling in the getName() function
                holder.setName(model.getName());

                final String uID = getRef(position).getKey();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //directs to the profile screen
                        Intent pIntent = new Intent(UsersActivity.this, Profile.class);
                        //taking alongside the user id
                        pIntent.putExtra("id", uID);
                        startActivity(pIntent);

                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflates the users layout list
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_s_layout,parent,false);
                return new UsersViewHolder(view);

            }

        };
        //setting the adapter to the uList
        uList.setAdapter(firebaseRecyclerAdapter);
        //start retrieving the data
        firebaseRecyclerAdapter.startListening();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        //view object created
        View view;
        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        //setting name
        public void setName(String name){
            //textview is set with the user_name id from the layout
            TextView userNameView = view.findViewById(R.id.user_name);
            userNameView.setText(name);
        }
    }
}




