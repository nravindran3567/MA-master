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

        tb= (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Cocoon");

        pic = (CircleImageView) findViewById(R.id.setImage);
        mName = (TextView) findViewById(R.id.set_name);
        Status= (TextView) findViewById(R.id.status);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String currentUID = currentUser.getUid();

        mUDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);

        mUDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Toast.makeText(SettingsAct.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();

               String name = dataSnapshot.child("name").getValue().toString();
               String image = dataSnapshot.child("image").getValue().toString();
               String status = dataSnapshot.child("status").getValue().toString();
               String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

               mName.setText(name);
               Status.setText(status);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
