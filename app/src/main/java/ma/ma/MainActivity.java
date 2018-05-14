package ma.ma;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fBAuth;
    private Toolbar tb;
    private ViewPager vPager;
    private SectionsPagerAdapter mSPA;
    private TabLayout tLayout;
    private DatabaseReference uRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting the firebase auth instance
        fBAuth = FirebaseAuth.getInstance();
        // storing the toolbar in the toolbar variable
        tb = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(tb);
        //setting the toolbar title to Cocoon
        getSupportActionBar().setTitle("Cocoon");
        //if the there is a user available
        if(fBAuth.getCurrentUser() !=null){
            //get the user id
            uRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fBAuth.getCurrentUser().getUid());
        }
        // storing the viewpager in the viewpager variable
        vPager=(ViewPager) findViewById(R.id.tabPager);

        mSPA= new SectionsPagerAdapter(getSupportFragmentManager());
        //setting adapter to the viewpager
        vPager.setAdapter(mSPA);
        tLayout = (TabLayout) findViewById(R.id.main_tabs);
        tLayout.setupWithViewPager(vPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fBAuth.getCurrentUser();
        //if the current user is not null
        if(currentUser==null){
            //go to start activity
            Intent startIntent = new Intent(MainActivity.this,StartAct.class);
            startActivity(startIntent);
            finish();
        } else{
            //set true if they are online
            uRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //gets the current user
        FirebaseUser currentUser = fBAuth.getCurrentUser();
        if(currentUser != null){
            //set with a timestamp if they are not online
            uRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //inflates the menu
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //menu contains three items
    //log out, account settings and all users
    //each directs user to a different page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logout){
            //takes the user back to the start screen of the app
            Intent home = new Intent(MainActivity.this,StartAct.class);
            startActivity(home);
            //and signs the user out of their account
            fBAuth.getInstance().signOut();
        }
        // takes the user to account settings from main activity
        if(item.getItemId() == R.id.AccountSettings){
            Intent setIntent = new Intent(MainActivity.this, SettingsAct.class);
            startActivity(setIntent);
        }
        //takes the user to the page with all the users from the main activity
        if(item.getItemId() == R.id.AllUsers){
            Intent uIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(uIntent);
        }
        return true;
    }
}
