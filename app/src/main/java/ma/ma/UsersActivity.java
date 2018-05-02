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

    private Toolbar toolbar;
    private RecyclerView usersList;
    private DatabaseReference mUDB;
    private String TAG = "UsersActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar = (Toolbar) findViewById(R.id.u_appbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUDB = FirebaseDatabase.getInstance().getReference().child("Users");


        usersList = (RecyclerView) findViewById(R.id.users_list);
        //ensures the size does not change when items are removed or deleted
        usersList.setHasFixedSize(true);
        //the arrangements of the layout is controlled my layoutmanger
        //this creates a new layoutmanager
        usersList.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();


        final FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setName(model.getName());

                final String uID = getRef(position).getKey();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent pIntent = new Intent(UsersActivity.this, Profile.class);
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
        usersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        //firebaseRecyclerAdapter.stopListening();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name){
            TextView userNameView = view.findViewById(R.id.user_name);
            userNameView.setText(name);
        }
    }
}




