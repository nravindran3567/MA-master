package ma.ma;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputEditText;
//import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private EditText username;
    private EditText email;
    private EditText password;
    private Button create_account;
   // private static final String TAG = "MainActivity";

    private ProgressDialog regProgress;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.reg_username);
        email =  (EditText) findViewById(R.id.reg_email);
        password =  (EditText) findViewById(R.id.reg_password);
        create_account = (Button) findViewById(R.id.create_acc);

        regProgress= new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = username.getText().toString();
                String e_mail = email.getText().toString();
                String pword = password.getText().toString();

                if(!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(e_mail) || !TextUtils.isEmpty(pword)){
                    regProgress.setTitle("registering user");
                    regProgress.setMessage("please wait");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();

                    register_user(user_name, e_mail, pword);
                }
            }
        });
    }

    private void register_user(String username, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    regProgress.dismiss();
                    Intent mainIntent = new Intent(Register.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    regProgress.hide();
                    Toast.makeText(Register.this,"ERROR",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
