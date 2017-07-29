package kmit.mentoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class mentorlogin extends Activity implements OnClickListener {
    String TAG;
    EditText mentorid;
    Button mlogin;
    EditText password;

    public mentorlogin() {
        this.TAG = "MENTORLOGIN";
    }

    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(this.TAG, "Login called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginmentor);
        this.mentorid = (EditText) findViewById(R.id.editText);
        this.password = (EditText) findViewById(R.id.editText2);
        this.mlogin = (Button) findViewById(R.id.button);
        this.mlogin.setOnClickListener(this);
    }

    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public void onClick(View view) {
        Button b = (Button) findViewById(R.id.button);
        b.setClickable(false);
        b.setText("Logging in...");
        String username = this.mentorid.getText().toString().trim();
        String pass = this.password.getText().toString().trim();
        String type = "Login";
        String IMEI = getIMEI(this);
        if (username.matches("") || pass.matches("")) {
            b.setText("LOGIN");
            b.setClickable(true);
            Toast.makeText(getApplicationContext(), "Enter credentials to login", Toast.LENGTH_SHORT).show();
        } else if (username.length() != 8) {
            b.setText("LOGIN");
            b.setClickable(true);
            Toast.makeText(getApplicationContext(), "This is a mentor login page.Please check!", Toast.LENGTH_SHORT).show();
        } else {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String port = getString(R.string.connection_string);
            backgroundWorker.execute(new String[]{type, username, pass, port, IMEI});
            backgroundWorker.onProgressUpdate(new Void[0]);
            AlertDialog alertDialog = new Builder(this).create();
            if (backgroundWorker.result.matches("1")) {
                Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).edit();
                editor.putString(getString(R.string.user_name), username);
                editor.putString("login_status", "1");
                editor.putString(getString(R.string.just_logged_in), "true");
                editor.commit();
                startActivity(new Intent(this, MentorHome.class));
            } else if (backgroundWorker.result.equalsIgnoreCase("Already Logged in")) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage("Your account is already been opened in some other device");
            } else if (backgroundWorker.result.matches("0")) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage("Incorrect credentials !");
            } else if (backgroundWorker.result.matches("NO NET")) {
                alertDialog.setTitle("Internet required");
                alertDialog.setMessage("Please check your internet connection and Login again!");
            } else if (backgroundWorker.result.indexOf("MySQL server has gone away") >= 0) {
                alertDialog.setTitle("We're Sorry");
                alertDialog.setMessage("Server is currently down! Please come back later");
            } else if (backgroundWorker.result.matches("You're not a mentor")) {
                alertDialog.setTitle("Unauthorized Access");
                alertDialog.setMessage("You are not a mentor");
            }
            alertDialog.show();
            b.setText("LOGIN");
            b.setClickable(true);
        }
    }
}