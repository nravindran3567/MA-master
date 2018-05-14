package ma.ma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsAct extends AppCompatActivity {
    //initialising variables
    private DatabaseReference mUDB;
    private FirebaseUser currentUser;

    private CircleImageView pic;
    private TextView mName;
    private TextView Status;

    private Toolbar tb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //storing toolbar in the toolbar variable
        tb = (Toolbar) findViewById(R.id.register_toolbar);
        //setting up toolbar as action bar
        setSupportActionBar(tb);
        //setting the title for the action bar
        getSupportActionBar().setTitle("Cocoon");

        pic = (CircleImageView) findViewById(R.id.setImage);
        mName = (TextView) findViewById(R.id.set_name);
        Status= (TextView) findViewById(R.id.status);

        //getting the current user and saving it to the FirebaseUser variable
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //Storing the user ID
        String currentUID = currentUser.getUid();
        //getting the database reference for the current user
        mUDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        //adding a value event listener
        mUDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //saving the data from the snapshot into the variables
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                //setting the name as name variable
                mName.setText(name);
                //setting the status as the status variable
                Status.setText(status);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
