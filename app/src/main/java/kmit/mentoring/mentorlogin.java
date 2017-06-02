package kmit.mentoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;
import static android.R.attr.onClick;

/**
 * Created by sesha sai on 1/10/2017.
 */

public class mentorlogin extends Activity implements View.OnClickListener {
    String TAG="MENTORLOGIN";
    Button mlogin;
    EditText mentorid,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Login called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginmentor);

        mentorid=(EditText)findViewById(R.id.editText);
        password=(EditText)findViewById(R.id.editText2);
        mlogin=(Button)findViewById(R.id.button);
        //mlogin.setOnClickListener((View.OnClickListener)this);
        mlogin.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        //Log.d(TAG,"sample");
        //Log.d(TAG,mentorid.getText().toString());
        //Log.d(TAG,password.getText().toString());
        String username=mentorid.getText().toString().trim();
        String pass=password.getText().toString().trim()
                ;
        String type="Login";
        //Toast.makeText(getApplicationContext(),"username :"+username+"!",Toast.LENGTH_LONG).show();
        if(username.matches("")||pass.matches(""))
        {
            Toast.makeText(getApplicationContext(),"Enter credentials to login",Toast.LENGTH_LONG).show();
        }
        else if(username.length()!=8)
        {
            Toast.makeText(getApplicationContext(),"Incorrect credentials",Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(getApplicationContext(),username+","+password,Toast.LENGTH_LONG).show();
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            String port=getString(R.string.connection_string);
            backgroundWorker.execute(type, username, pass,port);
            //backgroundWorker.execute(type, username, pass);
            backgroundWorker.onProgressUpdate();
            Log.d(TAG,"BackgroundWorker progressUpdate");
            if(backgroundWorker.result.matches("1"))
            {
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(getString(R.string.user_name),username);
                editor.putString(getString(R.string.just_logged_in),"true");
                editor.commit();
                Intent i=new Intent(this,MentorHome.class);
                startActivity(i);
            }
        }
    }
}
