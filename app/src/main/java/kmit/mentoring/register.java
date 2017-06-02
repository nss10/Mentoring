package kmit.mentoring;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sesha sai on 1/22/2017.
 */

public class register extends Activity {
    EditText userid;
    Button getOtp;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.register);
        userid=(EditText)findViewById(R.id.usr);
        getOtp=(Button)findViewById(R.id.usrbutton);
    }
}
