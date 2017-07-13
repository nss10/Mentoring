package kmit.mentoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class createPassword extends Activity {
    final String TAG;
    AlertDialog alertDialog;
    EditText confrim_new_pwdET;
    Context context;
    EditText new_pwdET;
    String password;
    String uid;


    public createPassword() {
        this.TAG = "createPassword";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.change_password);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.uid = extras.getString("uid");
        }
        ((EditText) findViewById(R.id.cur_pwd)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.link_back)).setVisibility(View.INVISIBLE);
        this.new_pwdET = (EditText) findViewById(R.id.new_pwd);
        this.confrim_new_pwdET = (EditText) findViewById(R.id.confirm_new_pwd);
        Button b = (Button) findViewById(R.id.button_change_password);
        b.setText("Create Password");
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createPassword.this.new_pwdET.getText().toString().equals(createPassword.this.confrim_new_pwdET.getText().toString())) {
                    createPassword.this.password = createPassword.this.new_pwdET.getText().toString();
                    CreatePasswordBG createPasswordBG = new CreatePasswordBG(createPassword.this);
                    String port = createPassword.this.getString(R.string.connection_string);
                    createPasswordBG.execute(new String[]{port, createPassword.this.uid, createPassword.this.password});
                    createPasswordBG.onProgressUpdate(new Void[0]);
                    if (createPasswordBG.result.matches("1")) {
                        createPassword.this.alertDialog = new Builder(createPassword.this.context).create();
                        createPassword.this.alertDialog.setTitle("Status");
                        createPassword.this.alertDialog.setMessage("Account created Successfully!");
                        createPassword.this.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createPassword.this.startActivity(new Intent(createPassword.this.context, studentlogin.class));
                                createPassword.this.finish();
                            }
                        });
                        createPassword.this.alertDialog.show();
                        return;
                    }
                    return;
                }
                Toast.makeText(createPassword.this.context, "Passwords did not match.Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}
