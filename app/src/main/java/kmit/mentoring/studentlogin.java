package kmit.mentoring;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class studentlogin extends Activity implements OnClickListener {
    String TAG;
    Intent i;
    EditText passwordEt;
    EditText usernameEt;
    View mLoginFormView,mProgressView;


    public studentlogin() {
        this.TAG = "StudentLogin";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mLoginFormView = (ScrollView)findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progressBar);
        this.usernameEt = (EditText) findViewById(R.id.sid);
        this.passwordEt = (EditText) findViewById(R.id.spass);
    }

    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onLogin(View view) throws InterruptedException {
        String username = this.usernameEt.getText().toString().trim();
        String password = this.passwordEt.getText().toString().trim();
        String type = "Login";
        Button b = (Button) findViewById(R.id.sbutton);
        b.setText("Logging in...");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            b.setText("LOGIN");
            Toast.makeText(getApplicationContext(), "Enter credentials to login", Toast.LENGTH_LONG).show();
        } else if (username.length() != 10) {
            b.setText("LOGIN");
            Toast.makeText(getApplicationContext(), "This is a student Login page. please Check!", Toast.LENGTH_LONG).show();
        } else {
            showProgress(true);
            String IMEI = getIMEI(this);
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String port = getString(R.string.connection_string);
            //Log.d(this.TAG, backgroundWorker.getStatus() + "before");
            backgroundWorker.execute(new String[]{type, username, password, port, IMEI});
            //Log.d(this.TAG, backgroundWorker.getStatus() + "after1");
            backgroundWorker.onProgressUpdate(new Void[0]);
            AlertDialog alertDialog = new Builder(this).create();
            //Log.d(TAG,backgroundWorker.result);
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
            } else if (backgroundWorker.result.contains("MySQL server has gone away")) {
                alertDialog.setTitle("We're Sorry");
                alertDialog.setMessage("Server is currently down! Please come back later");
            }
            showProgress(false);
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