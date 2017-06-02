
package kmit.mentoring;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;



/**
 * Created by nvas on 25/2/17.
 */

public class SetRemarks extends Activity {
    final String TAG = "SetRemarks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_remarks);
        Log.d(TAG, "Entering the Create");
        Spinner sp = (Spinner) findViewById(R.id.RemarkSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.RemarkList, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setSelection(1);
        RatingBar rb = (RatingBar) findViewById(R.id.ratingBarBWF);
        //rb.setNumStars(5);
        rb.setStepSize(1);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG,"Rating Given as " + rating);
            }
        });
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Entering the listener" + (int) parent.getItemIdAtPosition(position));
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                //((TextView) parent.getChildAt(0)).setTextSize(23);
                if ((int) parent.getItemIdAtPosition(position) > 0) {
                    EditText et = (EditText) findViewById(R.id.RemarkText);
                    et.setText((String) parent.getItemAtPosition(position));

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        Button b = (Button) findViewById(R.id.SubmitRemark);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.RemarkText);
                String RemarkString = et.getText().toString();
                Log.d(TAG, "Remark need to be submitted "+ RemarkString);
            }
        });
    }
}