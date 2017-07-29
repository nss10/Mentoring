package kmit.mentoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * Created by nvas on 2/3/17.
 */

public class RatingBarDialog extends Activity {

    String TAG = "RatingBarDialog";
    int[] ratingBarIdList = new int[]{R.id.ratingBarGD, R.id.ratingBarCS, R.id.ratingBarGRM, R.id.ratingBarBWP, R.id.ratingBarBWF, R.id.ratingBarCCA, R.id.ratingBarECA};
    int[] ratingBarResultList;
    String ratingBarResultString,mode;
    RatingBar rb;
    boolean isInteractable, isRatingSubmittable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Current Mentor Fields");
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.rating_bar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ratingBarResultString = extras.getString("ratingBarResultString");
            isRatingSubmittable = Boolean.parseBoolean(extras.getString("isRatingSubmittable"));
            mode = extras.getString("mode");
        }
        //Log.d(TAG, isRatingSubmittable + "");


        String[] temp = ratingBarResultString.split("~");
        ratingBarResultList = new int[temp.length];

        for (int i = 0; i < ratingBarIdList.length; i++) {
            ratingBarResultList[i] = Integer.parseInt(temp[i]);
            //Log.d(TAG, "So and so: " + ratingBarResultList[i]);
        }

        if(mode.equals("edit"))
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(RatingBarDialog.this);
            builder.setTitle("Are you sure, you want to continue?");
            builder.setMessage("Any changes made in this activity, might lead to overriding of exixting data.");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes,I want to edit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    isInteractable = true;
                    for (int i = 0; i < ratingBarIdList.length; i++)
                        changeInteractionMode(i);
                    Button saveButton = (Button) findViewById(R.id.saveButton);
                    if(!isRatingSubmittable)
                        saveButton.setVisibility(View.VISIBLE);
                    Button submitButton = (Button) findViewById(R.id.submitButton);
                    submitButton.setVisibility(View.VISIBLE);
                    Button backButton = (Button) findViewById(R.id.backButton);
                    backButton.setVisibility(View.GONE);

                }
            });
            builder.setNegativeButton("No,I don't want to edit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    isInteractable = false;
                    for (int i = 0; i < ratingBarIdList.length; i++)
                        changeInteractionMode(i);
                    Button saveButton = (Button) findViewById(R.id.saveButton);
                    saveButton.setVisibility(View.GONE);
                    Button submitButton = (Button) findViewById(R.id.submitButton);
                    submitButton.setVisibility(View.GONE);
                    Button backButton = (Button) findViewById(R.id.backButton);
                    backButton.setVisibility(View.VISIBLE);

                }
            });

            if(ratingBarResultString.replaceAll("0","").length()==ratingBarIdList.length)
            {
                isInteractable = true;
                for (int i = 0; i < ratingBarIdList.length; i++)
                    changeInteractionMode(i);
                Button saveButton = (Button) findViewById(R.id.saveButton);
                saveButton.setVisibility(View.VISIBLE);
                Button submitButton = (Button) findViewById(R.id.submitButton);
                submitButton.setVisibility(View.VISIBLE);
                Button backButton = (Button) findViewById(R.id.backButton);
                backButton.setVisibility(View.GONE);
            }
            else
            {

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }


        for (int i = 0; i < ratingBarIdList.length; i++)
            Store_Remark(i);
    }


    void changeInteractionMode(final int id)
    {
        if (ratingBarResultList == null)
        ratingBarResultList = new int[ratingBarIdList.length];

        rb = (RatingBar) findViewById(ratingBarIdList[id]);
        rb.setIsIndicator(!isInteractable);


    }
    void Store_Remark(final int id) {

        if (ratingBarResultList == null)
            ratingBarResultList = new int[ratingBarIdList.length];

        rb = (RatingBar) findViewById(ratingBarIdList[id]);


        rb.setNumStars(5);
        rb.setStepSize(1);

        rb.setRating(ratingBarResultList[id]);
        rb.setIsIndicator(!isInteractable);


        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    ratingBarResultList[id] = (int) rating;


            }
        });

            Button saveButton = (Button) findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = "";
                    for (int i = 0; i < ratingBarIdList.length; i++) {
                        str += ratingBarResultList[i] + "~";

                    }

                    Intent i = new Intent();
                    i.putExtra("Rating String", str);
                    i.putExtra("isSubmittable", "false");
                    setResult(2, i);
                    finish();
                }
            });

            Button submitButton = (Button) findViewById(R.id.submitButton);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSubmittable = true;
                    String str = "";
                    for (int i = 0; i < ratingBarIdList.length; i++) {
                        str += ratingBarResultList[i] + "~";
                        if (ratingBarResultList[i] == 0) {
                            isSubmittable = false;
                            break;
                        }
                    }
                    if (isSubmittable) {
                        Intent i = new Intent();
                        i.putExtra("Rating String", str);
                        i.putExtra("isSubmittable", "true");
                        setResult(2, i);
                        finish();
                    } else {
                        Toast.makeText(RatingBarDialog.this, "You need to rate all fields to submit", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (!isInteractable) {
            Intent i = new Intent();
            i.putExtra("Rating String", ratingBarResultString);
            i.putExtra("isSubmittable", isRatingSubmittable+"");
            setResult(2, i);
            finish();

        }
    }

}
