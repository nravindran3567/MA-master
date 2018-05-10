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

        mAuth = FirebaseAuth.getInstance();

        logProgress= new ProgressDialog(this);

        userDB = FirebaseDatabase.getInstance().getReference().child("Users");

        emailLogin= (EditText) findViewById(R.id.log_email);
        pwLogin = (EditText) findViewById(R.id.log_password);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailLogin.getEditableText().toString();
                String password = pwLogin.getEditableText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "email or password is incorrect.", Toast.LENGTH_LONG).show();

                }

                else if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){
                    logProgress.setTitle("Login");
                    logProgress.setMessage("Please wait");
                    logProgress.setCanceledOnTouchOutside(false);
                    logProgress.show();
                    loginUser(email, password);
                }

            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            logProgress.dismiss();

                            String currentUserID = mAuth.getCurrentUser().getUid();
                            String devToken = FirebaseInstanceId.getInstance().getToken();
                            userDB.child(currentUserID).child("device_token").setValue(devToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mIntent = new Intent(Login.this, MainActivity.class);
                                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mIntent);
                                    finish();

                                }
                            });
                        } else {
                            logProgress.hide();
                            String result = task.getException().getMessage().toString();

                            Toast.makeText(Login.this, "Error : " + result, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}
