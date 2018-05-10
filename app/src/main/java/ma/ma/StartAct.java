package ma.ma;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

public class StartAct extends AppCompatActivity {
    //variables
    private Button reg_btn;
    private Button log_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //button for creating an account
        //intent takes the user from the current page to the register place if it is clicked

        reg_btn= (Button) findViewById(R.id.new_ac_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartAct.this,Register.class);
                startActivity(reg_intent);
            }
        });

        //button for login
        //takes the user from the current page to the login page.

        log_btn = (Button) findViewById(R.id.existAcc);
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent log_intent = new Intent(StartAct.this,Login.class);
                startActivity(log_intent);

            }
        });
    }
}