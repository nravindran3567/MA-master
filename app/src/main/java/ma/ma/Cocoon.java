package ma.ma;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Nitharani on 26/04/2018.
 */
//handles the disconnection from the internet

public class Cocoon extends Application {

    private DatabaseReference uDB;
    private FirebaseAuth mAuth;

    public void onCreate() {
        super.onCreate();

        //getting the instance of the firebase object
        mAuth= FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

            uDB = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            //gets the user id of the current user within "users" in the database of firebase

            uDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){
                        //if this query gets disconnected, then set it to false
                        uDB.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);


                        //uDB.child("online").setValue(true);
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}
