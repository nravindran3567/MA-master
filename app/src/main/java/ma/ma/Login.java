package ma.ma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    private EditText emailLogin;
    private EditText pwLogin;
    private Button login;

    private ProgressDialog logProgress;
    private FirebaseAuth mAuth;

    private DatabaseReference userDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //instantiating firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //creating a new progress dialog
        logProgress= new ProgressDialog(this);
       // points to the child "users" in the database and stores it in the string
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        //initialising the variables
        emailLogin= (EditText) findViewById(R.id.log_email);
        pwLogin = (EditText) findViewById(R.id.log_password);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //getting the text of the  email and password and storing them in a string variable
                String email = emailLogin.getEditableText().toString();
                String password = pwLogin.getEditableText().toString();
                //if email or password is empty...
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    //create a toast to show error
                    Toast.makeText(Login.this, "email or password is incorrect.", Toast.LENGTH_LONG).show();

                }
                //else display the progress log if the email or password is not empty
                else if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){
                    //setting the title of the progress log
                    logProgress.setTitle("Login");
                    //the body of the progress log
                    logProgress.setMessage("Please wait");
                    //if user touches outside the dialog box, it will not dismiss the progress log
                    logProgress.setCanceledOnTouchOutside(false);
                    //shows the progress dialog box
                    logProgress.show();
                    //calls the loginUser function
                    loginUser(email, password);
                }

            }
        });
    }

    private void loginUser(String email, String password) {
    //signInWithEmailAndPassword function to sign the user in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the task is successful,
                        if (task.isSuccessful()) {
                            //it will dismiss the progress dialog
                            logProgress.dismiss();
                            //the current user's id is stored in a string
                            String currentUserID = mAuth.getCurrentUser().getUid();
                            //getting the token and storing it
                            String devToken = FirebaseInstanceId.getInstance().getToken();
                            //sets the devToken value within the "device_token"child of the current user id
                            userDB.child(currentUserID).child("device_token").setValue(devToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                //intent method takes the user from the login page to the main screen
                                    Intent mIntent = new Intent(Login.this, MainActivity.class);
                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    finish();

                                }
                            });
                        } else {
                            //if the user fails to log in,
                            //hide the progress log
                            //stores the error message in a string
                            logProgress.hide();
                            String result = task.getException().getMessage().toString();
                            //and displays it in a toast
                            Toast.makeText(Login.this, "Error : " + result, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}
