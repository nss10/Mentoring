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

public class studentlogin extends Activity implements OnClickListener {
    String TAG;
    Intent i;
    EditText passwordEt;
    EditText usernameEt;

    public studentlogin() {
        this.TAG = "StudentLogin";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.usernameEt = (EditText) findViewById(R.id.sid);
        this.passwordEt = (EditText) findViewById(R.id.spass);
    }

    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public void onLogin(View view) throws InterruptedException {
        String username = this.usernameEt.getText().toString().trim();
        String password = this.passwordEt.getText().toString().trim();
        String type = "Login";
        Button b = (Button) findViewById(R.id.sbutton);
        b.setText("Logging in...");
        if (username.matches(BuildConfig.FLAVOR) || password.matches(BuildConfig.FLAVOR)) {
            b.setText("LOGIN");
            Toast.makeText(getApplicationContext(), "Enter credentials to login", 1).show();
        } else if (username.length() != 10) {
            b.setText("LOGIN");
            Toast.makeText(getApplicationContext(), "This is a student Login page. please Check!", 1).show();
        } else {
            String IMEI = getIMEI(this);
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String port = getString(R.string.connection_string);
            Log.d(this.TAG, backgroundWorker.getStatus() + "before");
            backgroundWorker.execute(new String[]{type, username, password, port, IMEI});
            Log.d(this.TAG, backgroundWorker.getStatus() + "after1");
            backgroundWorker.onProgressUpdate(new Void[0]);
            AlertDialog alertDialog = new Builder(this).create();
            if (backgroundWorker.result.matches("1")) {
                Toast.makeText(getApplicationContext(), "login successful " + backgroundWorker.result, Toast.LENGTH_SHORT).show();
                Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).edit();
                editor.putString(getString(R.string.user_name), username);
                editor.putString("login_status", "1");
                editor.commit();
                startActivity(new Intent(this, StudentHome.class));
            } else if (backgroundWorker.result.equalsIgnoreCase("Already Logged in")) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage("You cannot login in more than 3 devices at a time.");
            } else if (backgroundWorker.result.matches("0")) {
                alertDialog.setTitle("Login Status");
                alertDialog.setMessage("Incorrect credentials !");
            } else if (backgroundWorker.result.matches("NO NET")) {
                alertDialog.setTitle("Internet required");
                alertDialog.setMessage("Please check your internet connection and Login again!");
            } else if (backgroundWorker.result.indexOf("MySQL server has gone away") >= 0) {
                alertDialog.setTitle("We're Sorry");
                alertDialog.setMessage("Server is currently down! Please come back later");
            }
            b.setText("LOGIN");
            alertDialog.show();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.link_forgot_password) {
            i = new Intent(getApplicationContext(), register.class);
        }
        this.i.putExtra("title", "Forgot password?");
        startActivity(this.i);
    }
}