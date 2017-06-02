package kmit.mentoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

/**
 * Created by sesha sai on 1/10/2017.
 */

public class studentlogin extends Activity{
    EditText usernameEt,passwordEt;
    String TAG="StudentLogin";
    //Context context=getApplicationContext();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        usernameEt=(EditText)findViewById(R.id.sid);
        passwordEt=(EditText)findViewById(R.id.spass);
    }

    public void onLogin(View view) throws InterruptedException {
        String username=usernameEt.getText().toString().trim();
        String password=passwordEt.getText().toString().trim();
        String type="Login";
        //Toast.makeText(getApplicationContext(),"username :"+username+"!",Toast.LENGTH_LONG).show();
        if(username.matches("")||password.matches(""))
        {
            Toast.makeText(getApplicationContext(),"Enter credentials to login",Toast.LENGTH_LONG).show();
        }
        else if(username.length()!=10)
        {
            Toast.makeText(getApplicationContext(),"Incorrect credentials",Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(getApplicationContext(),username+","+password,Toast.LENGTH_LONG).show();
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String port=getString(R.string.connection_string);

                Log.d(TAG,backgroundWorker.getStatus()+"before");
                backgroundWorker.execute(type, username, password,port);
                Log.d(TAG,backgroundWorker.getStatus()+"after1");
                backgroundWorker.onProgressUpdate();



            Log.d(TAG,"here again"+backgroundWorker.result);
            if(backgroundWorker.result.matches("0"))
                Toast.makeText(getApplicationContext(),"here again !!No internet"+backgroundWorker.result,Toast.LENGTH_LONG).show();
            if(backgroundWorker.result.matches("1"))
            {
                Toast.makeText(getApplicationContext(),"login successful "+backgroundWorker.result,Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(getString(R.string.user_name),username);
                editor.commit();
                Intent i=new Intent(this,StudentHome.class);
                startActivity(i);
            }
        }
    }

}
