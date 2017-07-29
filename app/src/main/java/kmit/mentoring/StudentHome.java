package kmit.mentoring;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.transition.Fade;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import kmit.mentoring.localStruct.localDB;
import kmit.mentoring.localStruct.mentorTable;

public class StudentHome extends Activity implements OnNavigationItemSelectedListener, OnClickListener {

    Student student;

    int Acad_loader;

    boolean defaultImage;
    boolean hasInternet;
    boolean isSharedPreferencesEmpty;
    boolean justLoggedIn;
    boolean shouldRatingbeUpdated;
    boolean ExitFlag;

    View mDashboardView,mViewRemarksView,mSetRemarksView,mViewPerformanceView,mChangePasswordView;

    String[] FieldList;
    String[] FieldListAbbr;
    String[] LoggedInDevices;

    ArrayAdapter adapter;

    SQLiteDatabase db;
    localDB db_obj;

    GraphView graph;
    BarGraphSeries<DataPoint> series;

    String from;
    String TAG;
    String state;
    String mentor_id;
    String tempResult;
    String mPort;

    Spinner sp;
    TextView mentorNameTV;
    BottomBar bottomBar;

    statusCheckBG scbg;






//Constructor

    public StudentHome() {
        this.TAG = "StudentHome";
        this.FieldList = new String[]{"General Discipline", "Communication Skills", "Grooming", "Behavior With Peers", "Behavior With Faculty", "Co-Cirricular Activities", "Extra-Cirricular Activities"};
        this.FieldListAbbr = new String[]{"GD", "CS", "Grm", "BWP", "BWF", "CCA", "ECA"};
        this.from = "login";
        this.shouldRatingbeUpdated = true;
        this.defaultImage = true;
        this.justLoggedIn = true;
        this.hasInternet = true;

    }



//Image Related

    private void getImage() {
        new AsyncTask<String, Void, Bitmap>() {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                this.loading = ProgressDialog.show(StudentHome.this, "Loading Profile...", null, true, true);
            }

            protected void onPostExecute(Bitmap b) {
                //Log.d(StudentHome.this.TAG, "onPostExecute" + b);
                super.onPostExecute(b);
                this.loading.dismiss();
                if (b != null && !StudentHome.this.isSharedPreferencesEmpty) {
                    student.setStudentImage(StudentHome.encodeTobase64(b));
                    SharedPreferences img_store = StudentHome.this.getSharedPreferences("img_store", 0);
                    //Log.d(StudentHome.this.TAG, "StudentImage adding in sharedPreferences" + student.getStudentImage());
                    Editor editor = img_store.edit();
                    editor.putString("img_stud", student.getStudentImage());
                    editor.apply();
                    StudentHome.this.defaultImage = false;
                    //Log.d(StudentHome.this.TAG, "defaultImageStatus in onPostExecute = " + StudentHome.this.defaultImage);
                    StudentHome.this.setCircularImage();
                }
            }

            protected Bitmap doInBackground(String... params) {
                MalformedURLException e;
                IOException e2;
                Bitmap image = null;
                try {
                    URL url = new URL(StudentHome.this.getString(R.string.connection_string) + "getImage.php?id=" + params[0]);
                    URL url2;
                    try {
                        //Log.d(StudentHome.this.TAG, BuildConfig.FLAVOR + url.openConnection().getInputStream());
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        url2 = url;
                    } catch (MalformedURLException e3) {
                        e = e3;
                        url2 = url;
                        e.printStackTrace();
                        return image;
                    } catch (IOException e4) {
                        e2 = e4;
                        url2 = url;
                        e2.printStackTrace();
                        return image;
                    }
                } catch (MalformedURLException e5) {
                    e = e5;
                    e.printStackTrace();
                    return image;
                }
                return image;
            }
        }.execute(student.getSid());
        //Log.d(this.TAG, "sid when getting image = " + student.getSid());
    }
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    void setCircularImage() {
        if (!this.defaultImage) {
            ((CircleImageView) findViewById(R.id.circ_img)).setImageBitmap(decodeBase64(student.getStudentImage()));
            //Log.d(this.TAG, "getImage done");
        }
    }




//UI related

    void setAreaFor(int id) {
       mDashboardView.setVisibility(View.INVISIBLE);
        mSetRemarksView.setVisibility(View.INVISIBLE);
        mViewPerformanceView.setVisibility(View.INVISIBLE);
        mViewRemarksView.setVisibility(View.INVISIBLE);
        mChangePasswordView.setVisibility(View.INVISIBLE);


        if (id == R.id.RemarkView) {
            mSetRemarksView.setVisibility(View.VISIBLE);
        } else if (id == R.id.student_dashboard) {
            mDashboardView.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_remarks) {
            mViewRemarksView.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_change_password) {
            //Log.d(this.TAG, "View Changing for ChangePassword");
            mChangePasswordView.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_performance) {
            mViewPerformanceView.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.NoGraph)).setVisibility(View.VISIBLE);
            this.graph = (GraphView) findViewById(R.id.graph);
            this.graph.setVisibility(View.INVISIBLE);
        }
    }
    void setProfile() {
        ((TextView) findViewById(R.id.ht_noRight)).setText(student.getSid());
        ((TextView) findViewById(R.id.nameRight)).setText(student.getName());
        ((TextView) findViewById(R.id.parentNameRight)).setText(student.getParentName());
        ((TextView) findViewById(R.id.class_idRight)).setText(student.getYear() + BuildConfig.FLAVOR);
        //Log.d(this.TAG, "defaultImageStatus in setProfile = " + this.defaultImage);
        setCircularImage();
        ((TextView) findViewById(R.id.branchRight)).setText(student.getDepartment());
        ((TextView) findViewById(R.id.sectionRight)).setText(String.valueOf(student.getSection()));
        ((TextView) findViewById(R.id.semRight)).setText((student.getSem() % 2 == 0 ? 2 : 1) + BuildConfig.FLAVOR);


        ((TextView) findViewById(R.id.parentNoRight)).setText((student.getPhone()));
        //Log.d(this.TAG, "setProfile isStudentFlagged = " + student.isStudentFlagged());
        final CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
        cb.setChecked(student.isStudentFlagged());
        //Log.d(this.TAG, student.getSid()  +":" + student.getPhone() + " : " + student.getPhone().equals("1111111111"));

        if (student.getPhone().equals("1111111111")) {
            ((TextView) findViewById(R.id.parentNoLeft)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.parentNoRight)).setVisibility(View.INVISIBLE);
        }
        if (this.from.equals("login")) {
            cb.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.link_change_password)).setVisibility(View.VISIBLE);
        } else if (this.from.equals("mentor")) {
            if (!student.getOTP().equals("1")) {
                ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.OTPRight)).setText(student.getOTP());
            }
            cb.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.link_change_password)).setVisibility(View.INVISIBLE);
        }
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d(StudentHome.this.TAG, "isStudentFlagged = " + cb.isChecked());
                student.setStudentFlagged(cb.isChecked());
                StudentHome studentHome = StudentHome.this;
                localStruct kmit_mentoring_localStruct = new localStruct();
                kmit_mentoring_localStruct.getClass();
                studentHome.db_obj = new localStruct().new localDB(StudentHome.this.getApplicationContext());
                StudentHome.this.db = StudentHome.this.db_obj.getWritableDatabase();
                String val = student.isStudentFlagged() ? "1" : "0";
                ContentValues values = new ContentValues();
                values.put(mentorTable.column8, val);
                //Log.d(StudentHome.this.TAG, "db.update val = " + StudentHome.this.db.update(mentorTable.table_name, values, "ht_no='" + student.getSid() + "'", null));
                //Log.d(StudentHome.this.TAG, "isStudentFlagged is updated to the local as" + values);






            }
        });
    }
    void nav_SetRemark() {
        final Spinner sp = (Spinner) findViewById(R.id.RemarkSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.RemarkList, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.requestFocus();
        Button submitRemark = (Button) findViewById(R.id.SubmitRemark);
        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final EditText et = (EditText) findViewById(R.id.RemarkText);
        submitRemark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String new_rem = et.getText().toString().trim();
               if(imm!=null)
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

                if (new_rem.length() == 0) {
                    Toast.makeText(StudentHome.this, "Please Enter a remark to submit", Toast.LENGTH_SHORT).show();
                    return;
                }
                Builder builder = new Builder(StudentHome.this);
                builder.setTitle("Are you sure you want to submit Remark?");
                builder.setMessage(new_rem);
                builder.setCancelable(false);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String cur_date = dateFormat.format(new Date());
                        //Log.d(StudentHome.this.TAG, new_rem + " " + cur_date);
                        ContentValues values = new ContentValues();

                        if(student.getNew_remarkList()==null)
                            student.setLocalRemarks(new_rem,cur_date);
                        else
                        {
                            //Log.d(TAG,"size = "+student.getNew_remarkList().size());
                            student.setLocalRemarks(new_rem,cur_date);
                            //Log.d(TAG,"size = "+student.getNew_remarkList().size());
                        }

                        Remark cumulativeLocalRemark = student.getCumulativeLocalRemark();
                        values.put(mentorTable.column4, cumulativeLocalRemark.getRemarkString());
                        values.put(mentorTable.column5, cumulativeLocalRemark.getDate());
                        //Log.d(TAG,"" + StudentHome.this.db.update(mentorTable.table_name, values, "ht_no='" + student.getSid()+ "'", null));



                        Toast.makeText(StudentHome.this.getApplicationContext(), "Remark Entered", Toast.LENGTH_SHORT).show();
                        //Log.d(StudentHome.this.TAG, new_rem + " " + cur_date + " string = " + cumulativeLocalRemark.toString() + "values = " + values.toString());
                        et.getText().clear();

                    }
                });
                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*if (imm != null) {
                            imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, InputMethodManager.RESULT_SHOWN);
                        }*/
                        et.requestFocus();
                        imm.showSoftInput(et,InputMethodManager.SHOW_FORCED);


                    }
                });
                builder.create().show();
            }
        });
        ((Button) findViewById(R.id.ClearRemark)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText(BuildConfig.FLAVOR);
            }
        });
        ((Button) findViewById(R.id.submitMentorFields)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (student.isRatingSubmittable()) {
                    Builder builder = new Builder(StudentHome.this);
                    builder.setTitle("You have already Submitted this data before");
                    builder.setMessage("Do you want to resubmit?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
                            //Log.d(StudentHome.this.TAG, "So and So:" + student.getRatingBarResultString());
                            i.putExtra("ratingBarResultString", student.getRatingBarResultString());
                            i.putExtra("isRatingSubmittable", student.isRatingSubmittable() + BuildConfig.FLAVOR);
                            i.putExtra("mode", "edit");
                            StudentHome.this.startActivityForResult(i, 2);
                        }
                    });
                    builder.setNeutralButton("View", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
                            //Log.d(StudentHome.this.TAG, "So and So:" + student.getRatingBarResultString());
                            i.putExtra("ratingBarResultString", student.getRatingBarResultString());
                            i.putExtra("isRatingSubmittable", student.isRatingSubmittable() + BuildConfig.FLAVOR);
                            i.putExtra("mode", "view");
                            StudentHome.this.startActivityForResult(i, 2);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return;
                }
                Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
                //Log.d(StudentHome.this.TAG, "So and So:" + student.getRatingBarResultString());
                i.putExtra("ratingBarResultString", student.getRatingBarResultString());
                i.putExtra("isRatingSubmittable", student.isRatingSubmittable() + BuildConfig.FLAVOR);
                i.putExtra("mode", "edit");
                StudentHome.this.startActivityForResult(i, 2);
            }
        });
        sp.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                }
                return false;
            }
        });
        sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(StudentHome.this.TAG, "Entering the listener" + ((int) parent.getItemIdAtPosition(position)));
                if (imm != null) {
                    imm.toggleSoftInput(1, 0);
                }
                if (((int) parent.getItemIdAtPosition(position)) > 1) {
                    sp.setSelection(1);
                    EditText et = (EditText) StudentHome.this.findViewById(R.id.RemarkText);
                    if (position == 8) {
                        Toast.makeText(StudentHome.this, "Type the backlog subject name", Toast.LENGTH_SHORT).show();
                    }
                    if (et.getText().length() != 0) {
                        et.setText(et.getText().toString().trim() + "," + ((String) parent.getItemAtPosition(position)) + " ");
                    } else {
                        et.setText(((String) parent.getItemAtPosition(position)) + " ");
                    }
                    et.setSelection(et.getText().length());
                    if (imm != null && et.getText().toString().length() > 0) {
                        imm.toggleSoftInput(1, 0);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void getRemarks() {

        //Log.d(TAG,"student = "+student.toString());

        TextView RemUI = (TextView) findViewById(R.id.Remarks);
        RemUI.setText("");
        RemUI.setVisibility(View.VISIBLE);
        TextView RemHead = (TextView) findViewById(R.id.RemHead);
        RemHead.setVisibility(View.VISIBLE);
        if(student.getNew_remarkList()==null && student.getRemarkList()==null)
        {
            RemUI.setText("No Remarks");
            return;
        }
        else
        {
            ArrayList<Remark> localRemList = student.getNew_remarkList();
            ArrayList<Remark> serverRemList = student.getRemarkList();
            //Log.d(TAG,"localRemarksList = " + String.valueOf(localRemList!=null) + "serverRemarksList = " + String.valueOf(serverRemList!=null));
            if(localRemList!=null)
                for(int i=localRemList.size()-1;i>=0;i--)
                {
                    String rem = localRemList.get(i).getRemarkString();//getColoredSpanned(Local_Remarks[i], "#ff0000");
                    String date= getColoredSpanned(localRemList.get(i).getDate(),"#080000");

                    RemUI.append(Html.fromHtml(date));
                    RemUI.append("\n");
                    RemUI.append(Html.fromHtml(rem));
                    RemUI.append("\n\n");
                }

            if(serverRemList!=null)
                for (int i = serverRemList.size()-1; i >=0 ; i--){

                    String rem = getColoredSpanned(serverRemList.get(i).getRemarkString(), "#0000FF");
                    String date= getColoredSpanned(serverRemList.get(i).getDate(),"#080000");
                    RemUI.append(Html.fromHtml(date));
                    RemUI.append("\n");
                    RemUI.append(Html.fromHtml(rem));
                    RemUI.append("\n\n");
                }
        }

        mentorNameTV.setText("Mentor: " + student.getMentorName()+ "\n");



    }
    void showPerformance() {
        this.adapter = ArrayAdapter.createFromResource(this, R.array.fieldList, android.R.layout.simple_spinner_dropdown_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sp = (Spinner) findViewById(R.id.spinner_perf);
        this.sp.setAdapter(this.adapter);
        this.sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(StudentHome.this.TAG, "Entering the listener" + ((int) parent.getItemIdAtPosition(position)));
                ((TextView) parent.getChildAt(0)).setTextSize(18.0f);
                if (((int) parent.getItemIdAtPosition(position)) > 0) {
                    ((TextView) StudentHome.this.findViewById(R.id.NoGraph)).setVisibility(View.INVISIBLE);
                    if (position <= 7) {
                        StudentHome.this.getFieldGraph(((int) parent.getItemIdAtPosition(position)) - 1);
                    } else {
                        StudentHome.this.getSemAcadGraph();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //Log.d(this.TAG, "acadAggr before acadGraph = " + student.getAcadAggr() + BuildConfig.FLAVOR);
        getAcadGraph(student.getAcadAggr());
        getAggrGraph();
    }
    void changePassword() {
        EditText et_new_pwd = (EditText) findViewById(R.id.new_pwd);
        EditText et_confirm_new_pwd = (EditText) findViewById(R.id.confirm_new_pwd);
        Button change_pwd = (Button) findViewById(R.id.button_change_password);
        String cur_pwd = ((EditText) findViewById(R.id.cur_pwd)).getText().toString().trim();
        String new_pwd = ((EditText) findViewById(R.id.new_pwd)).getText().toString().trim();
        if (et_new_pwd.getText().toString().trim().equals(et_confirm_new_pwd.getText().toString().trim())) {
            setPasswordBG spbg = new setPasswordBG(this);
            spbg.execute(getString(R.string.connection_string), student.getSid(), cur_pwd, new_pwd);
            spbg.onProgressUpdate();
            return;
        }
        Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT).show();
    }


//Data related

    void getData() {
        String username = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).getString(getString(R.string.user_name), EnvironmentCompat.MEDIA_UNKNOWN);

        studentBackground sbg = new studentBackground(this);

    from = getIntent().getExtras()==null?"login":"mentor";
    if(from.equals("login")) {
        //Log.d(TAG, username);
        sbg.execute(username, mPort);
        //Log.d(TAG, "Started on progresUpdate");
        sbg.onProgressUpdate();

        if (sbg.result.equals("NO Net") || sbg.result.equals("Error_studentHome_php")) {


            SharedPreferences sharedPreferences2 = getApplicationContext().getSharedPreferences("StudResult", Context.MODE_PRIVATE);
            tempResult = sharedPreferences2.getString("HomeStuff", "");
            student = new Student(sharedPreferences2.getString("HomeStuff", ""));


        } else {
            tempResult = sbg.result;
            //Log.d(TAG,sbg.result);
            student = new Student(sbg.result);
            SharedPreferences settings = getSharedPreferences("StudResult", MODE_PRIVATE);

            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("HomeStuff", tempResult);
            editor.apply();

            // Reading from SharedPreferences
            String value = settings.getString("key", "");
            //Log.d(TAG, value + tempResult);
        }
    }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mentor_id = extras.getString("mentor_id");
            student = extras.getParcelable("studObj");
            //Log.d(TAG,student.toString());
            this.state = extras.getString("state");

            student.setStudentImage(getSharedPreferences("img_store", 0).getString("img_stud", "null"));
            if (!student.getStudentImage().equals("null")) {
                this.defaultImage = false;
            }
//                uncomment to show the changing intent problem

            //Log.d(this.TAG, "defaultImageStatus in fromMentor = " + this.defaultImage + " : " + student.getStudentImage());
            this.from = "mentor";
        } else {
            student.setStudentImage(getSharedPreferences("img_store", 0).getString("img_stud", "null"));
            //Log.d(this.TAG, "getting studentImage from SharedPreferences = " + student.getStudentImage());
            if (!student.getStudentImage().equals("null")) {
                this.defaultImage = false;
            }
            //Log.d(this.TAG, "defaultImageStatus in getData(extras==null) = " + this.defaultImage);
        }
    if (this.tempResult != null) {

        if (this.from.equals("login") && this.justLoggedIn) {
            getImage();
            //Log.d(this.TAG, "Getting Image");
            this.justLoggedIn = false;
        }

        return;
    }
}
    void updateRatingToLocal() {

        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        String val = student.isRatingSubmittable() ? "1" : "0";
        ContentValues values = new ContentValues();
        values.put(mentorTable.column6, student.getRatingBarResultString());
        values.put(mentorTable.column7, val);
        //Log.d(this.TAG, "Local Rating Updated " + this.db.update(mentorTable.table_name, values, "ht_no='" + student.getSid()+ "'", null)+student.getRatingBarResultString()+ "isRatingSubmittable in LocalStruct " + student.isRatingSubmittable());
    }



//UI_HelperMethods

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }
    void showAcadValue() {
        final TextView AcadValue = (TextView) findViewById(R.id.AcadValue);
        if (student.getAcadAggr() == 0.0d) {
            AcadValue.setVisibility(View.VISIBLE);
        }
        final Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                if (((double) StudentHome.this.Acad_loader) < student.getAcadAggr()) {
                    AcadValue.setText(StudentHome.this.Acad_loader +"");
                    StudentHome studentHome = StudentHome.this;
                    studentHome.Acad_loader += 3;
                    h.postDelayed(this, 3);
                    return;
                }
                AcadValue.setText(StudentHome.round(student.getAcadAggr(), 2) +"");
                StudentHome.this.Acad_loader = 0;
            }
        });
    }
    public static double round(double value, int places) {
        //Log.d("StudentHome", value + "");
        if (((int) value) == 0) {
            return 0.0d;
        }
        if (places >= 0) {
            return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
        }
        throw new IllegalArgumentException();
    }


//Graph related

    void getAcadGraph(double Academic_Aggregate) {
        graph = (GraphView) findViewById(R.id.acadGraph);
        graph.removeAllSeries();
        graph.setVisibility(View.VISIBLE);
        BarGraphSeries series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, Academic_Aggregate)

        });
        graph.addSeries(series);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        series.setAnimated(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(1.4);
        if(Integer.parseInt(student.getSid().substring(0,2))>=16)
        {
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(10);
        }
        else
        {
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(100);
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);


        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                sp.setSelection(0);
                TextView tv = (TextView) findViewById(R.id.NoGraph);
                tv.setVisibility(View.INVISIBLE);
                sp.setSelection(8);
            }
        });
        series.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(DataPointInterface data) {
                if (data.getY() > 0.7*(graph.getViewport().getMaxY(true)))
                    return Color.BLUE;
                else if (data.getY() > 0.65*(graph.getViewport().getMaxY(true)))
                    return Color.rgb(255, 100, 0);
                else
                    return Color.RED;
            }
        });
    }
    void getAttGraph(double Attendance) {
        graph = (GraphView) findViewById(R.id.attGraph);
        graph.removeAllSeries();
        graph.setVisibility(View.VISIBLE);
        BarGraphSeries series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, Attendance)

        });
        TextView temp = (TextView) findViewById(R.id.AttendanceHead);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(1.4);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);


        graph.addSeries(series);
        series.setAnimated(true);
        series.setValuesOnTopColor(Color.RED);
        series.setDrawValuesOnTop(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);


        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return value + "%";
                }
                return "";
            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        });


        series.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(DataPointInterface data) {
                if (data.getY() < 65) {
                    return Color.rgb(255, 100, 100);
                } else {
                    return Color.rgb(21, 212, 0);
                }
            }
        });
    }
    void getAggrGraph() {

        if(student.getMentorFields()==null)
                return;


        int[] aggr = student.getMentorFields().getAggr();
        DataPoint[] dp = new DataPoint[9];
        dp[0] = new DataPoint(0, 6);
        dp[8] = new DataPoint(8, 6);
        for (int i = 1; i < 8; i++)
            dp[i] = new DataPoint(i, aggr[i - 1]);

        graph = (GraphView) findViewById(R.id.aggrGraph);

        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(7);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(5);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.setTitle("Your Aggregate performance graph");
        graph.setTitleTextSize(44);
        graph.removeAllSeries();
        series = new BarGraphSeries<>(dp);
        graph.addSeries(series);
        series.setSpacing(20);
        series.setAnimated(true);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if ((int) data.getX() != 8 && (int) data.getX() != 0) {

                    switch ((int) data.getX()) {
                        case 1:
                            return Color.rgb(51, 204, 204);
                        case 2:
                            return Color.rgb(255, 229, 0);//return Color.rgb(255, 153, 102);
                        case 3:
                            return Color.rgb(204, 102, 204);
                        case 4:
                            return Color.rgb(255, 127, 100);//return Color.rgb(0,255,255);
                        case 5:
                            return Color.rgb(0, 200, 0);
                        case 6:
                            return Color.rgb(204, 0, 102);
                        case 7:
                            return Color.rgb(102, 153, 153);
                        case 8:
                            return Color.rgb(200, 200, 200);

                        default:
                            return Color.rgb(255, 255, 255);
                    }
                }
                return Color.rgb(255, 255, 255);
            }
        });
        /*graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);*/
        graph.getGridLabelRenderer().setNumHorizontalLabels(8);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String field = "";
                sp.setSelection((int) dataPoint.getX(), true);

            }
        });


        graph.getGridLabelRenderer().setLabelFormatter(
                new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            // show x values
                            switch (super.formatLabel(value, isValueX)) {
                                case "1":
                                    return "GD";
                                case "2":
                                    return "CS";
                                case "3":
                                    return "Grm";
                                case "4":
                                    return "BWP";
                                case "5":
                                    return "BWF";
                                case "6":
                                    return "CCA";
                                case "7":
                                    return "ECA";
                                default:
                                    return "";
                            }
                        } else {
                            // show y values
                            switch (super.formatLabel(value, isValueX)) {
                                case "1":
                                    return "1";//"Very Poor";
                                case "2":
                                    return "2";//"Poor";
                                case "3":
                                    return "3";//"Average";
                                case "4":
                                    return "4";//"Good";
                                case "5":
                                    return "5";//"Excellent";

                                default:
                                    return "";
                            }
                        }
                    }
                }
        );

        /*graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);*/

        graph.setVisibility(View.VISIBLE);

    }
    void getSemAcadGraph() {


        graph = null;

        int acadCount = student.getAcadArr().length;
        graph = (GraphView) findViewById(R.id.graph);
        DataPoint dp[] = new DataPoint[acadCount];
        graph.setTitle("Semester wise performance");
        graph.setTitleTextSize(44);
        for (int i = 0; i < acadCount; i++) {

            dp[i] = new DataPoint(i, student.getAcadArr()[i]);
            //Log.d(TAG, student.getAcadArr()[i] + "");
        }
        series = new BarGraphSeries<>(dp);
        graph.removeAllSeries();
        graph.getViewport().setScalable(true);

        graph.addSeries(series);
        graph.setVisibility(View.VISIBLE);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);

        graph.getViewport().setMinX(-1);
        graph.getViewport().setMaxX(student.getSem());

        if(Integer.parseInt(student.getSid().substring(0,2))>=16)
        {
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(10);
            graph.getGridLabelRenderer().setVerticalAxisTitle("GPA 0-10");
        }
        else
        {
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(100);
            graph.getGridLabelRenderer().setVerticalAxisTitle("Percentage 0-100");
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(student.getSem());
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        series.setAnimated(true);
        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override

            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return  (((int)value)/2+1) + "-" +(((int)value)%2+1);
                return (int)value + "";
            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        });
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                switch ((int) data.getX() + 1) {
                    case 1:
                        return Color.rgb(51, 204, 204);
                    case 2:
                        return Color.rgb(255, 229, 0);//return Color.rgb(255, 153, 102);
                    case 3:
                        return Color.rgb(204, 102, 204);
                    case 4:
                        return Color.rgb(255, 127, 100);//return Color.rgb(0,255,255);
                    case 5:
                        return Color.rgb(0, 200, 0);
                    case 6:
                        return Color.rgb(204, 0, 102);
                    case 7:
                        return Color.rgb(102, 153, 153);
                    case 8:
                        return Color.rgb(200, 200, 200);

                    default:
                        return Color.rgb(255, 255, 255);
                }

            }
        });

    }
    void getFieldGraph(int fieldVal) {


        graph = null;

        if (student.getMentorFields()!=null) {
            int[] field = student.getMentorFields().getFieldArray(fieldVal);


            graph = (GraphView) findViewById(R.id.graph);
            DataPoint dp[] = new DataPoint[student.getSem()];
            graph.setTitle("Semester wise performance");
            graph.setTitleTextSize(44);

            for (int i = 0; i < student.getSem(); i++) {
                if(i<field.length)
                    dp[i] = new DataPoint(i, field[i]);
                else
                    dp[i] = new DataPoint(i, 0);

                
            }
            series = new BarGraphSeries<>(dp);
            graph.removeAllSeries();

            graph.addSeries(series);
            graph.setVisibility(View.VISIBLE);
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.BLACK);
            series.setSpacing(10);

            graph.getViewport().setMinX(-1);
            graph.getViewport().setMaxX(student.getSem());

            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(5);

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(student.getSem());
//            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);
            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

            graph.getGridLabelRenderer().setNumHorizontalLabels(student.getSem());
            graph.getGridLabelRenderer().setNumVerticalLabels(5);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Semesters");
            graph.getGridLabelRenderer().setVerticalAxisTitle("Performance on a scale of 5");
            graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(34);
            graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(34);


            graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {

                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX)
                        return  (((int)value)/2+1) + "-" +(((int)value)%2+1);
                    return value + "";
                }

                @Override
                public void setViewport(Viewport viewport) {

                }
            });
            series.setAnimated(true);
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    switch ((int) data.getY()) {
                        case 1:
                            return Color.rgb(255, 0, 0);
                        case 2:
                            return Color.rgb(255, 100, 0);//return Color.rgb(255, 153, 102);
                        case 3:
                            return Color.rgb(200, 100, 100);
                        case 4:
                            return Color.rgb(200, 200, 100);//return Color.rgb(0,255,255);
                        case 5:
                            return Color.rgb(0, 200, 0);


                        default:
                            return Color.rgb(255, 255, 255);
                    }
                }

            });
        } else {
            TextView NoGraphTV = (TextView)findViewById(R.id.NoGraph);
            NoGraphTV.setText("\n Not graded");

        }
    }






//BackgroundHelperMethods

    String getLoginStatus() {
        String port = getString(R.string.connection_string);
        this.scbg = new statusCheckBG(this);
        this.scbg.execute(port, student.getSid());
        this.scbg.onProgressUpdate();
        //Log.d(this.TAG, "scbg.result" + this.scbg.result);
        if (!this.scbg.result.matches("NO NET")) {
            return this.scbg.result;
        }
        Toast.makeText(this, this.scbg.result, Toast.LENGTH_SHORT).show();
        //Log.d(this.TAG, "Just before return");
        return null;
    }
    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }




//Events and Actions

    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(this.TAG, "Reached in Student");
        super.onCreate(savedInstanceState);
        mPort = getString(R.string.connection_string);
        getData();

        if (student!=null) {
            setContentView(R.layout.student_homepage);
            mDashboardView = findViewById(R.id.student_dashboard);
            mSetRemarksView = findViewById(R.id.RemarkView);
            mViewPerformanceView = findViewById(R.id.view_performance);
            mViewRemarksView= findViewById(R.id.view_remarks);
            mChangePasswordView = findViewById(R.id.view_change_password);

            this.mentorNameTV = (TextView) findViewById(R.id.mentorName);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            toolbar.setTitle("My Attendance");
            toolbar.setTitleTextColor(Color.GRAY);

            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (this.from.equals("login")) {
                navigationView.getMenu().getItem(0).setVisible(true);
                navigationView.getMenu().getItem(1).setVisible(false);
            } else {
                navigationView.getMenu().getItem(0).setVisible(false);
                navigationView.getMenu().getItem(1).setVisible(true);
            }
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    navigationView.removeOnLayoutChangeListener(this);
                    TextView textView = (TextView) navigationView.findViewById(R.id.temp2);
                    if (StudentHome.this.from.equals("login")) {
                        textView.setText("Hello " + student.getName());
                    } else {
                        textView.setText("This is " + student.getName());
                    }
                    textView.setVisibility(View.VISIBLE);
                }
            });
            if (this.from.equals("mentor")) {
                bottomBar = (BottomBar) findViewById(R.id.bottomBar_mentor);
                bottomBar.setVisibility(View.VISIBLE);
                if (this.state.equals("repeat")) {
                    bottomBar.selectTabWithId(R.id.tab_set_remarks);
                }
            } else {
                bottomBar = (BottomBar) findViewById(R.id.bottomBar_student);
                if(bottomBar!=null)
                    bottomBar.setVisibility(View.VISIBLE);
            }

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    switch (tabId) {
                        case R.id.tab_student_dashboard :
                            toolbar.setTitle("DashBoard");
                            StudentHome.this.setAreaFor(R.id.student_dashboard);
                            StudentHome.this.setProfile();
                            StudentHome.this.getAttGraph(student.getAttendance());
                            break;
                        case R.id.tab_view_remarks:

                            toolbar.setTitle("Attendance & Remarks");
                            StudentHome.this.setAreaFor(R.id.view_remarks);
                            StudentHome.this.getRemarks();
                            StudentHome.this.getRemarks();
                            break;
                        case R.id.tab_performance :
                            toolbar.setTitle("Performance");
                            StudentHome.this.setAreaFor(R.id.view_performance);
                            StudentHome.this.showPerformance();
                            StudentHome.this.showAcadValue();
                            break;
                        case R.id.tab_set_remarks :
                            toolbar.setTitle("Set Remarks");
                            StudentHome.this.setAreaFor(R.id.RemarkView);
                            StudentHome.this.findViewById(R.id.RemarkView).setVisibility(View.VISIBLE);
                            StudentHome.this.nav_SetRemark();
                            break;
                        default:
                    }
                }
            });
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!StudentHome.this.from.equals("login")) {
                        return;
                    }
                    if (StudentHome.this.getLoginStatus() != null) {
                        StudentHome.this.LoggedInDevices = StudentHome.this.getLoginStatus().split(",");
                        if (!StudentHome.this.getLoginStatus().contains(StudentHome.this.getIMEI(StudentHome.this))) {
                            Toast.makeText(StudentHome.this, "You are logged out from " + student.getSid(), Toast.LENGTH_SHORT).show();
                            StudentHome.this.onLogout();
                            return;
                        } else if (StudentHome.this.LoggedInDevices.length > 0) {
                            Toast.makeText(StudentHome.this, "You are currently logged in " + StudentHome.this.LoggedInDevices.length + " device(s)", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            return;
                        }
                    }
                    StudentHome.this.hasInternet = false;
                    //Log.d(StudentHome.this.TAG, "Internet Status" + StudentHome.this.hasInternet);
                }
            }, 5000);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            student.setRatingBarResultString(data.getStringExtra("Rating String"));
            student.setRatingSubmittable(Boolean.parseBoolean(data.getStringExtra("isSubmittable")));
            //Log.d(this.TAG, "onActivityResult isRatingSubmittable: " + student.isRatingSubmittable());
            updateRatingToLocal();
        }
    }
    public void onBackPressed() {
        if (!this.ExitFlag) {
            this.ExitFlag = true;
            Toast.makeText(this, "Please click BACK again to leave this page", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    StudentHome.this.ExitFlag = false;
                }
            }, 2000);
        } else if (this.from.equals("mentor")) {
            startActivity(new Intent(this, MentorHome.class));

        } else {
            Intent startMain = new Intent("android.intent.action.MAIN");
            startMain.addCategory("android.intent.category.HOME");
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    void onLogout() {
        Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).edit();
        editor.remove("username");
        editor.remove("login_status");
        editor.commit();
        LogoutBG lbg = new LogoutBG(this);
        lbg.execute(getString(R.string.connection_string), student.getSid(),getIMEI(this));
        lbg.onProgressUpdate(new Void[0]);
        getApplicationContext().getSharedPreferences("img_store", 0).edit().clear().commit();
        //Log.d(this.TAG, "Shared Preferences are cleared");

        this.isSharedPreferencesEmpty = true;
        startActivity(new Intent(this, MainActivity.class));
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        getData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (id == R.id.Logout) {
            //Log.d(this.TAG, "hasInternet = " + this.hasInternet);
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START, true);
            if (getLoginStatus() == null) {
                this.hasInternet = false;
            } else {
                this.hasInternet = true;
            }
            if (this.hasInternet) {
                final LogoutBG lbg = new LogoutBG(this);
                final String port = getString(R.string.connection_string);
                AlertDialog alertDialog = new Builder(this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Your account is already active in some other device");
                alertDialog.setButton(-1, "All Devices", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lbg.execute(port, student.getSid());

                    }
                });
                alertDialog.setButton(-2, "Current Device", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lbg.execute(port, student.getSid(), StudentHome.this.getIMEI(StudentHome.this));

                    }
                });
                if (this.LoggedInDevices == null || this.LoggedInDevices.length <= 1) {
                    lbg.execute(port, student.getSid());
                } else {
                    alertDialog.show();
                    //Log.d(this.TAG, "Dialog shown");
                }
                final Handler h1 = new Handler();
                h1.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lbg.result == null || lbg.result.equals(BuildConfig.FLAVOR)) {
                            h1.postDelayed(this, 5);
                        } else if (lbg.result.equals("1")) {
                            StudentHome.this.onLogout();
                        } else {
                            Toast.makeText(StudentHome.this, "Unable to logout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "NO NET", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.Back) {
            startActivity(new Intent(this, MentorHome.class));
        }
        else if(id==R.id.Report_Bug)
        {
                goToUrl("https://goo.gl/forms/LhgsYP4n03M1NkcG2");
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }
    public void onClick(View v) {
        if (v.getId() == R.id.link_change_password) {
            setAreaFor(R.id.view_change_password);
            ((Button) findViewById(R.id.button_change_password)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    StudentHome.this.changePassword();
                }
            });
        } else if (v.getId() == R.id.link_back) {
            setAreaFor(R.id.student_dashboard);
            setProfile();
            getAttGraph(student.getAttendance());
        } else if (v.getId() == R.id.UpdateToServerStudentSide) {
            //Log.d(this.TAG, "Update To server Clicked");
            Button b = (Button) findViewById(R.id.UpdateToServerStudentSide);
            b.setText("Updating Data...");
            b.setClickable(false);
            if (new UpdateData(this, getString(R.string.connection_string), this.mentor_id,student.getSem()).UpdateToServer()) {
                Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MentorHome.class);
                i.putExtra("coming_from", student.getSid());
                Editor editor = getApplicationContext().getSharedPreferences("from_student", 0).edit();
                editor.putString("from_sid", student.getSid());
                editor.commit();
                startActivity(i);
                return;
            }
            Toast.makeText(this, "NO NET", Toast.LENGTH_LONG).show();
            b.setText("CLICK TO UPDATE TO SERVER");
        }
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}