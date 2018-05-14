package ma.ma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    //creating variables
    private EditText username;
    private EditText email;
    private EditText password;
    private Button create_account;

    private DatabaseReference mDB;
    private ProgressDialog regProgress;
    private FirebaseAuth mAuth;
    private String TAG = "register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initialising the variables
        username = (EditText) findViewById(R.id.reg_username);
        email =  (EditText) findViewById(R.id.reg_email);
        password =  (EditText) findViewById(R.id.reg_password);
        create_account = (Button) findViewById(R.id.create_acc);

        regProgress= new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        //when the create account button is clicked...
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the text of the username, email and password and storing them in a string variable
                String user_name = username.getText().toString();
                String e_mail = email.getText().toString();
                String pword = password.getText().toString();
                // if the textboxes are not empty
                if(!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(e_mail) || !TextUtils.isEmpty(pword)){
                    //display progress bar with information
                    regProgress.setTitle("registering user");
                    regProgress.setMessage("please wait");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();
                    //call register_user function
                    register_user(user_name, e_mail, pword);
                }
            }
        });
    }

    public void register_user(final String username, String email, String password){
        //createUserWithEmailAndPassword function to create new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if the task is successful...
                        if (task.isSuccessful()) {
                            //get the current user
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            //store the user ID
                            String uid = current_user.getUid();
                            //storing the database reference of the user
                            mDB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            //getting the token and storing it
                            String devToken = FirebaseInstanceId.getInstance().getToken();
                            //builds a hash map to add values to the child nodes in the database
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name", username);
                            userMap.put("status", "I'm using Cocoon!");
                            userMap.put("image", "default");
                            userMap.put("thumb_image","default");
                            userMap.put("device_token", devToken);
                            mDB.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //if the task is successful...
                                    if(task.isSuccessful()) {
                                        //dismiss the progress bar
                                        regProgress.dismiss();
                                        //redirects to main activity page
                                        Intent register_intent = new Intent(Register.this,MainActivity.class);
                                        register_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(register_intent);
                                        finish();
                                    }

                                }
                            });
                        } else {
                            //hide the progress bar
                            regProgress.hide();
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                            //display toast
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}