package kmit.mentoring;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
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
import java.util.Date;
import kmit.mentoring.localStruct.localDB;
import kmit.mentoring.localStruct.mentorTable;

public class StudentHome extends Activity implements OnNavigationItemSelectedListener, OnClickListener {
    int Acad_loader;
    double Academic_Aggregate;
    double[] Academics;
    int Attendance;
    String[] Dates;
    boolean ExitFlag;
    String[] FieldList;
    String[] FieldListAbbr;
    String[] LoggedInDevices;
    String Mentor_Name;
    String Parent_Name;
    String[] Remarks;
    String Student_Name;
    String TAG;
    ArrayAdapter adapter;
    String[] arr;
    String class_id;
    SQLiteDatabase db;
    localDB db_obj;
    boolean defaultImage;
    String from;
    GraphView graph;
    boolean hasInternet;
    boolean isRatingSubmittable;
    boolean isStudentFlagged;
    boolean issharedPreferencesEmpty;
    boolean justLoggedIn;
    String local_date;
    String local_rem;
    String[] mentor_fields;
    String mentor_id;
    String otp;
    String ratingBarResultString;
    statusCheckBG scbg;
    int sem;
    BarGraphSeries<DataPoint> series;
    boolean shouldRatingbeUpdated;
    String sid;
    Spinner sp;
    String state;
    String studentImage;
    String tempResult;
    TextView test;
    static boolean testFlag;
    /* renamed from: kmit.mentoring.StudentHome.19 */
    class AnonymousClass19 implements OnCheckedChangeListener {
        final /* synthetic */ CheckBox val$cb;

        AnonymousClass19(CheckBox checkBox) {
            this.val$cb = checkBox;
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(StudentHome.this.TAG, "isStudentFlagged = " + this.val$cb.isChecked());
            StudentHome.this.isStudentFlagged = this.val$cb.isChecked();
            StudentHome studentHome = StudentHome.this;
            localStruct kmit_mentoring_localStruct = new localStruct();
            kmit_mentoring_localStruct.getClass();
            studentHome.db_obj = new localStruct().new localDB(StudentHome.this.getApplicationContext());
            StudentHome.this.db = StudentHome.this.db_obj.getWritableDatabase();
            String val = StudentHome.this.isStudentFlagged ? "1" : "0";
            ContentValues values = new ContentValues();
            values.put(mentorTable.column8, val);
            Log.d(StudentHome.this.TAG, "db.update val = " + StudentHome.this.db.update(mentorTable.table_name, values, "ht_no='" + StudentHome.this.sid + "'", null));
            Log.d(StudentHome.this.TAG, "isStudentFlagged is updated to the local as" + values);
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.1 */
    class C03991 implements OnClickListener {
        final /* synthetic */ EditText val$et;
        final /* synthetic */ InputMethodManager val$imm;

        /* renamed from: kmit.mentoring.StudentHome.1.1 */
        class C03971 implements DialogInterface.OnClickListener {
            final /* synthetic */ String val$new_rem;

            C03971(String str) {
                this.val$new_rem = str;
            }

            public void onClick(DialogInterface dialog, int id) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date d = new Date();
                Log.d(StudentHome.this.TAG, this.val$new_rem + " " + dateFormat.format(d));
                ContentValues values = new ContentValues();
                if (StudentHome.this.local_date == null || StudentHome.this.local_rem == null) {
                    StudentHome.this.local_rem = this.val$new_rem.trim();
                    StudentHome.this.local_date = dateFormat.format(d);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    StudentHome studentHome = StudentHome.this;
                    studentHome.local_rem = stringBuilder.append(studentHome.local_rem).append("~").append(this.val$new_rem.trim()).toString();
                    stringBuilder = new StringBuilder();
                    studentHome = StudentHome.this;
                    studentHome.local_date = stringBuilder.append(studentHome.local_date).append("~").append(dateFormat.format(d)).toString();
                    Log.d(StudentHome.this.TAG, "Updated local_rem" + StudentHome.this.local_rem);
                }
                values.put(mentorTable.column4, StudentHome.this.local_rem);
                values.put(mentorTable.column5, StudentHome.this.local_date);
                StudentHome.this.db.update(mentorTable.table_name, values, "ht_no='" + StudentHome.this.sid + "'", null);
                Toast.makeText(StudentHome.this.getApplicationContext(), "Remark Entered", Toast.LENGTH_SHORT).show();
                Log.d(StudentHome.this.TAG, this.val$new_rem + " " + dateFormat.format(d));
                C03991.this.val$et.setText(BuildConfig.FLAVOR);
            }
        }

        /* renamed from: kmit.mentoring.StudentHome.1.2 */
        class C03982 implements DialogInterface.OnClickListener {
            C03982() {
            }

            public void onClick(DialogInterface dialog, int id) {
                if (C03991.this.val$imm != null) {
                    C03991.this.val$imm.toggleSoftInput(1, 0);
                }
            }
        }

        C03991(EditText editText, InputMethodManager inputMethodManager) {
            this.val$et = editText;
            this.val$imm = inputMethodManager;
        }

        public void onClick(View v) {
            String new_rem = this.val$et.getText().toString();
            if (this.val$imm != null) {
                this.val$imm.toggleSoftInput(1, 0);
            }
            if (new_rem.length() == 0) {
                Toast.makeText(StudentHome.this, "Please Enter a remark to submit", Toast.LENGTH_SHORT).show();
                return;
            }
            Builder builder = new Builder(StudentHome.this);
            builder.setTitle("Are you sure you want to submit Remark?");
            builder.setMessage(new_rem);
            builder.setCancelable(false);
            builder.setPositiveButton("Confirm", new C03971(new_rem));
            builder.setNegativeButton("Edit", new C03982());
            builder.create().show();
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.20 */
    class AnonymousClass20 implements OnLayoutChangeListener {
        final /* synthetic */ NavigationView val$navigationView;

        AnonymousClass20(NavigationView navigationView) {
            this.val$navigationView = navigationView;
        }

        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            this.val$navigationView.removeOnLayoutChangeListener(this);
            TextView textView = (TextView) this.val$navigationView.findViewById(R.id.temp2);
            if (StudentHome.this.from.equals(Event.LOGIN)) {
                textView.setText("Hello " + StudentHome.this.Student_Name);
            } else {
                textView.setText("This is " + StudentHome.this.Student_Name);
            }
            textView.setVisibility(View.VISIBLE);
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.25 */
    class AnonymousClass25 implements DialogInterface.OnClickListener {
        final /* synthetic */ LogoutBG val$lbg;
        final /* synthetic */ String val$port;

        AnonymousClass25(LogoutBG logoutBG, String str) {
            this.val$lbg = logoutBG;
            this.val$port = str;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$lbg.execute(new String[]{this.val$port, StudentHome.this.sid});
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.26 */
    class AnonymousClass26 implements DialogInterface.OnClickListener {
        final /* synthetic */ LogoutBG val$lbg;
        final /* synthetic */ String val$port;

        AnonymousClass26(LogoutBG logoutBG, String str) {
            this.val$lbg = logoutBG;
            this.val$port = str;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$lbg.execute(new String[]{this.val$port, StudentHome.this.sid, StudentHome.this.getIMEI(StudentHome.this)});
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.27 */
    class AnonymousClass27 implements Runnable {
        final /* synthetic */ Handler val$h1;
        final /* synthetic */ LogoutBG val$lbg;

        AnonymousClass27(LogoutBG logoutBG, Handler handler) {
            this.val$lbg = logoutBG;
            this.val$h1 = handler;
        }

        public void run() {
            if (this.val$lbg.result == null || this.val$lbg.result.equals(BuildConfig.FLAVOR)) {
                this.val$h1.postDelayed(this, 5);
            } else if (this.val$lbg.result.equals("1")) {
                StudentHome.this.onLogout();
            } else {
                Toast.makeText(StudentHome.this, "Unable to logout", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.2 */
    class C04002 implements OnClickListener {
        final /* synthetic */ EditText val$et;

        C04002(EditText editText) {
            this.val$et = editText;
        }

        public void onClick(View v) {
            this.val$et.setText(BuildConfig.FLAVOR);
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.3 */
    class C04043 implements OnClickListener {

        /* renamed from: kmit.mentoring.StudentHome.3.1 */
        class C04011 implements DialogInterface.OnClickListener {
            C04011() {
            }

            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
                Log.d(StudentHome.this.TAG, "So and So:" + StudentHome.this.ratingBarResultString);
                i.putExtra("ratingBarResultString", StudentHome.this.ratingBarResultString);
                i.putExtra("isRatingSubmittable", StudentHome.this.isRatingSubmittable + BuildConfig.FLAVOR);
                i.putExtra("mode", "edit");
                StudentHome.this.startActivityForResult(i, 2);
            }
        }

        /* renamed from: kmit.mentoring.StudentHome.3.2 */
        class C04022 implements DialogInterface.OnClickListener {
            C04022() {
            }

            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
                Log.d(StudentHome.this.TAG, "So and So:" + StudentHome.this.ratingBarResultString);
                i.putExtra("ratingBarResultString", StudentHome.this.ratingBarResultString);
                i.putExtra("isRatingSubmittable", StudentHome.this.isRatingSubmittable + BuildConfig.FLAVOR);
                i.putExtra("mode", "view");
                StudentHome.this.startActivityForResult(i, 2);
            }
        }

        /* renamed from: kmit.mentoring.StudentHome.3.3 */
        class C04033 implements DialogInterface.OnClickListener {
            C04033() {
            }

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }

        C04043() {
        }

        public void onClick(View v) {
            if (StudentHome.this.isRatingSubmittable) {
                Builder builder = new Builder(StudentHome.this);
                builder.setTitle("You have already Submitted this data before");
                builder.setMessage("Do you want to resubmit?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new C04011());
                builder.setNeutralButton("View", new C04022());
                builder.setNegativeButton("Cancel", new C04033());
                builder.create().show();
                return;
            }
            Intent i = new Intent(StudentHome.this, RatingBarDialog.class);
            Log.d(StudentHome.this.TAG, "So and So:" + StudentHome.this.ratingBarResultString);
            i.putExtra("ratingBarResultString", StudentHome.this.ratingBarResultString);
            i.putExtra("isRatingSubmittable", StudentHome.this.isRatingSubmittable + BuildConfig.FLAVOR);
            i.putExtra("mode", "edit");
            StudentHome.this.startActivityForResult(i, 2);
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.4 */
    class C04054 implements OnTouchListener {
        final /* synthetic */ InputMethodManager val$imm;

        C04054(InputMethodManager inputMethodManager) {
            this.val$imm = inputMethodManager;
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (this.val$imm != null) {
                this.val$imm.toggleSoftInput(1, 0);
            }
            return false;
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.5 */
    class C04065 implements OnItemSelectedListener {
        final /* synthetic */ InputMethodManager val$imm;
        final /* synthetic */ Spinner val$sp;

        C04065(InputMethodManager inputMethodManager, Spinner spinner) {
            this.val$imm = inputMethodManager;
            this.val$sp = spinner;
        }

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(StudentHome.this.TAG, "Entering the listener" + ((int) parent.getItemIdAtPosition(position)));
            if (this.val$imm != null) {
                this.val$imm.toggleSoftInput(1, 0);
            }
            if (((int) parent.getItemIdAtPosition(position)) > 1) {
                this.val$sp.setSelection(1);
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
                if (this.val$imm != null && et.getText().toString().length() > 0) {
                    this.val$imm.toggleSoftInput(1, 0);
                }
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.6 */
    class C04076 implements Runnable {
        final /* synthetic */ TextView val$AcadValue;
        final /* synthetic */ Handler val$h;

        C04076(TextView textView, Handler handler) {
            this.val$AcadValue = textView;
            this.val$h = handler;
        }

        public void run() {
            if (((double) StudentHome.this.Acad_loader) < StudentHome.this.Academic_Aggregate) {
                this.val$AcadValue.setText(StudentHome.this.Acad_loader +"");
                StudentHome studentHome = StudentHome.this;
                studentHome.Acad_loader += 3;
                this.val$h.postDelayed(this, 3);
                return;
            }
            this.val$AcadValue.setText(StudentHome.round(StudentHome.this.Academic_Aggregate, 2) +"");
            StudentHome.this.Acad_loader = 0;
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.21 */
    class AnonymousClass21 implements OnTabSelectListener {
        final /* synthetic */ Toolbar val$toolbar;

        AnonymousClass21(Toolbar toolbar) {
            this.val$toolbar = toolbar;
        }

        public void onTabSelected(int tabId) {
            switch (tabId) {
                case R.id.tab_student_dashboard /*2131689762*/:
                    this.val$toolbar.setTitle((CharSequence) "DashBoard");
                    StudentHome.this.setAreaFor(R.id.student_dashboard);
                    Log.d(TAG,"Setting area for dashBoard!");
                    StudentHome.this.setProfile();
                    StudentHome.this.getAttGraph(StudentHome.this.Attendance);
                case R.id.tab_view_remarks /*2131689763*/:
                    ProgressDialog pd = ProgressDialog.show(StudentHome.this, "Loading Remarks", "Retrieving from database");
                    pd.setProgressStyle(1);
                    StudentHome.this.getData();
                    this.val$toolbar.setTitle((CharSequence) "Attendance & Remarks");
                    StudentHome.this.setAreaFor(R.id.view_remarks);
                    StudentHome.this.getRemarks();
                    Log.d(StudentHome.this.TAG, "Button Clicked");
                    pd.dismiss();
                case R.id.tab_performance /*2131689764*/:
                    this.val$toolbar.setTitle((CharSequence) "Performance");
                    StudentHome.this.setAreaFor(R.id.view_performance);
                    StudentHome.this.showPerformance();
                    StudentHome.this.showAcadValue();
                    Log.d(StudentHome.this.TAG, "Button Clicked");
                case R.id.tab_set_remarks /*2131689765*/:
                    this.val$toolbar.setTitle((CharSequence) "Set Remarks");
                    StudentHome.this.setAreaFor(R.id.RemarkView);
                    StudentHome.this.findViewById(R.id.RemarkView).setVisibility(View.VISIBLE);
                    StudentHome.this.nav_SetRemark();
                    Log.d(StudentHome.this.TAG, "Button Clicked");
                default:
            }
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.7 */
    class C06107 implements LabelFormatter {
        C06107() {
        }

        public String formatLabel(double value, boolean isValueX) {
            return StudentHome.round(value, 2) + BuildConfig.FLAVOR;
        }

        public void setViewport(Viewport viewport) {
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.8 */
    class C06118 implements OnDataPointTapListener {
        C06118() {
        }

        public void onTap(Series series, DataPointInterface dataPoint) {
            StudentHome.this.sp.setSelection(0);
            ((TextView) StudentHome.this.findViewById(R.id.NoGraph)).setVisibility(View.INVISIBLE);
            StudentHome.this.sp.setSelection(8);
        }
    }

    /* renamed from: kmit.mentoring.StudentHome.9 */
    class C06129 implements ValueDependentColor {
        C06129() {
        }

        public int get(DataPointInterface data) {
            if (data.getY() > 70.0d) {
                return -16776961;
            }
            if (data.getY() > 65.0d) {
                return Color.rgb(MotionEventCompat.ACTION_MASK, 100, 0);
            }
            return SupportMenu.CATEGORY_MASK;
        }
    }

    public StudentHome() {
        this.TAG = "StudentHome";
        this.FieldList = new String[]{"General Discipline", "Communication Skills", "Grooming", "Behavior With Peers", "Behavior With Faculty", "Co-Cirricular Activities", "Extra-Cirricular Activities"};
        this.FieldListAbbr = new String[]{"GD", "CS", "Grm", "BWP", "BWF", "CCA", "ECA"};
        this.from = Event.LOGIN;
        this.shouldRatingbeUpdated = true;
        this.defaultImage = true;
        this.justLoggedIn = true;
        this.hasInternet = true;
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

    private void getImage() {
        new AsyncTask<String, Void, Bitmap>() {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                this.loading = ProgressDialog.show(StudentHome.this, "Loading Profile...", null, true, true);
            }

            protected void onPostExecute(Bitmap b) {
                Log.d(StudentHome.this.TAG, "onPostExecute" + b);
                super.onPostExecute(b);
                this.loading.dismiss();
                if (b != null && !StudentHome.this.issharedPreferencesEmpty) {
                    StudentHome.this.studentImage = StudentHome.encodeTobase64(b);
                    SharedPreferences img_store = StudentHome.this.getSharedPreferences("img_store", 0);
                    Log.d(StudentHome.this.TAG, "StudentImage adding in sharedPreferences" + StudentHome.this.studentImage);
                    Editor editor = img_store.edit();
                    editor.putString("img_stud", StudentHome.this.studentImage);
                    editor.apply();
                    StudentHome.this.defaultImage = false;
                    Log.d(StudentHome.this.TAG, "defaultImageStatus in onPostExecute = " + StudentHome.this.defaultImage);
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
                        Log.d(StudentHome.this.TAG, BuildConfig.FLAVOR + url.openConnection().getInputStream());
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
        }.execute(new String[]{this.sid});
        Log.d(this.TAG, "sid when getting image = " + this.sid);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            this.ratingBarResultString = data.getStringExtra("Rating String");
            this.isRatingSubmittable = Boolean.parseBoolean(data.getStringExtra("isSubmittable"));
            Log.d(this.TAG, "onActivityResult isRatingSubmittable: " + this.isRatingSubmittable);
            updateRatingToLocal();
        }
    }

    void updateRatingToLocal() {

        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        String val = this.isRatingSubmittable ? "1" : "0";
        ContentValues values = new ContentValues();
        values.put(mentorTable.column6, this.ratingBarResultString);
        values.put(mentorTable.column7, val);
        Log.d(this.TAG, "Local Rating Updated " + this.db.update(mentorTable.table_name, values, "ht_no='" + this.sid + "'", null));
    }

    void nav_SetRemark() {
        Spinner sp = (Spinner) findViewById(R.id.RemarkSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.RemarkList, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        Button submitRemark = (Button) findViewById(R.id.SubmitRemark);
        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText et = (EditText) findViewById(R.id.RemarkText);
        submitRemark.setOnClickListener(new C03991(et, imm));
        ((Button) findViewById(R.id.ClearRemark)).setOnClickListener(new C04002(et));
        ((Button) findViewById(R.id.submitMentorFields)).setOnClickListener(new C04043());
        sp.setOnTouchListener(new C04054(imm));
        sp.setOnItemSelectedListener(new C04065(imm, sp));
    }

    void changePassword() {
        EditText et_new_pwd = (EditText) findViewById(R.id.new_pwd);
        EditText et_confirm_new_pwd = (EditText) findViewById(R.id.confirm_new_pwd);
        Button change_pwd = (Button) findViewById(R.id.button_change_password);
        String cur_pwd = ((EditText) findViewById(R.id.cur_pwd)).getText().toString().trim();
        String new_pwd = ((EditText) findViewById(R.id.new_pwd)).getText().toString().trim();
        if (et_new_pwd.getText().toString().trim().equals(et_confirm_new_pwd.getText().toString().trim())) {
            setPasswordBG spbg = new setPasswordBG(this);
            spbg.execute(new String[]{getString(R.string.connection_string), this.sid, cur_pwd, new_pwd});
            spbg.onProgressUpdate(new Void[0]);
            return;
        }
        Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT).show();
    }

    void setAreaFor(int id) {
        View std_dash = findViewById(R.id.student_dashboard);
        std_dash.setVisibility(View.INVISIBLE);
        View rm = findViewById(R.id.RemarkView);
        rm.setVisibility(View.INVISIBLE);
        View vp = findViewById(R.id.view_performance);
        vp.setVisibility(View.INVISIBLE);
        View vr = findViewById(R.id.view_remarks);
        vr.setVisibility(View.INVISIBLE);
        View cp = findViewById(R.id.view_change_password);
        cp.setVisibility(View.INVISIBLE);
        if (id == R.id.RemarkView) {
            rm.setVisibility(View.VISIBLE);
        } else if (id == R.id.student_dashboard) {
            std_dash.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_remarks) {
            vr.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_change_password) {
            Log.d(this.TAG, "View Changing for ChangePassword");
            cp.setVisibility(View.VISIBLE);
        } else if (id == R.id.view_performance) {
            vp.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.NoGraph)).setVisibility(View.VISIBLE);
            this.graph = (GraphView) findViewById(R.id.graph);
            this.graph.setVisibility(View.INVISIBLE);
        }
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    void getRemarks() {



        StringBuilder server_data = new StringBuilder();
        String [] Local_Dates,Local_Remarks;
        TextView RemUI = (TextView) findViewById(R.id.Remarks);
        RemUI.setText("");
        RemUI.setVisibility(View.VISIBLE);
        TextView RemHead = (TextView) findViewById(R.id.RemHead);
        RemHead.setVisibility(View.VISIBLE);
        Log.d(TAG,local_rem+" This is the local_rem");
        if(local_rem!=null && local_date!=null)
        {
            Local_Dates =local_date.split("~");
            Local_Remarks =local_rem.split("~");

            for(int i=Local_Dates.length-1;i>=0;i--)
            {
                String rem = Local_Remarks[i];//getColoredSpanned(Local_Remarks[i], "#ff0000");
                String date= getColoredSpanned(Local_Dates[i],"#080000");

                RemUI.append(Html.fromHtml(date));
                RemUI.append("\n");
                RemUI.append(Html.fromHtml(rem));
                RemUI.append("\n\n");
            }
        }
        if(Remarks[0].trim().equals("No")) {
            if(local_rem==null && local_date==null)
                RemUI.setText("No Remarks");
        }
        else{
            Log.d(TAG,Remarks[0]+" Remarks[0]");

            for (int i = Remarks.length-1; i >=0 ; i--){

                String rem = getColoredSpanned(Remarks[i], "#0000FF");
                String date= getColoredSpanned(Dates[i],"#080000");
                RemUI.append(Html.fromHtml(date));
                RemUI.append("\n");
                RemUI.append(Html.fromHtml(rem));
                RemUI.append("\n\n");
            }
        }
        test.setText("Mentor: " + Mentor_Name + "\n");



    }


    void showAcadValue() {
        TextView AcadValue = (TextView) findViewById(R.id.AcadValue);
        if (this.Academic_Aggregate == 0.0d) {
            AcadValue.setVisibility(View.VISIBLE);
        }
        Handler h = new Handler();
        h.post(new C04076(AcadValue, h));
    }

    public static double round(double value, int places) {
        Log.d("StudentHome", value + "");
        if (((int) value) == 0) {
            return 0.0d;
        }
        if (places >= 0) {
            return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
        }
        throw new IllegalArgumentException();
    }


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
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
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
                if (data.getY() > 70)
                    return Color.BLUE;
                else if (data.getY() > 65)
                    return Color.rgb(255, 100, 0);
                else
                    return Color.RED;
            }
        });
    }

    void getAttGraph(int Attendance) {
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
        String[] mentor_fields = arr[9].split("~");

        if (mentor_fields.length == 1)
            return;
        Log.d(TAG,"This is mento_fields[6]"+mentor_fields[6]);
        String[] aggr = mentor_fields[7].split(",");
        DataPoint[] dp = new DataPoint[9];
        dp[0] = new DataPoint(0, 6);
        dp[8] = new DataPoint(8, 6);
        for (int i = 1; i < 8; i++)
            dp[i] = new DataPoint(i, Integer.parseInt(aggr[i - 1]));

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

    void getFieldGraph(int fieldVal) {


        graph = null;

        final int switch_var = sem;
        if (mentor_fields.length != 1) {

            String[] field = mentor_fields[fieldVal].split(",");
            Log.d(TAG,"Entered the FieldGraph "+field.length);
            for(int i=0;i<field.length;i++)
                Log.d(TAG,"Field Values: "+field[i]);
            Log.d(TAG,"Entered the FieldGraph "+field.length);
            while(field.length<sem) {
                mentor_fields[fieldVal] += "0,";
                field = mentor_fields[fieldVal].split(",");
            }

            graph = (GraphView) findViewById(R.id.graph);
            DataPoint dp[] = new DataPoint[sem];
            graph.setTitle("Semester wise performance");
            graph.setTitleTextSize(44);

            for (int i = 0; i < sem; i++) {

                dp[i] = new DataPoint(i, Integer.parseInt(field[i]));
                Log.d(TAG, field[i]);
            }
            series = new BarGraphSeries<>(dp);
            graph.removeAllSeries();

            graph.addSeries(series);
            graph.setVisibility(View.VISIBLE);
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.BLACK);
            series.setSpacing(10);

            graph.getViewport().setMinX(-1);
            graph.getViewport().setMaxX(sem);

            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(5);

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setNumHorizontalLabels(sem);
//            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.VERTICAL);
            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

            graph.getGridLabelRenderer().setNumHorizontalLabels(sem);
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
            test.setText("\n Not graded");

        }
    }

    void getSemAcadGraph() {


        graph = null;


        graph = (GraphView) findViewById(R.id.graph);
        DataPoint dp[] = new DataPoint[sem];
        graph.setTitle("Semester wise performance");
        graph.setTitleTextSize(44);
        for (int i = 0; i < sem; i++) {

            dp[i] = new DataPoint(i, Academics[i]);
            Log.d(TAG, Academics[i] + "");
        }
        series = new BarGraphSeries<>(dp);
        graph.removeAllSeries();
        graph.getViewport().setScalable(true);

        graph.addSeries(series);
        graph.setVisibility(View.VISIBLE);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);

        graph.getViewport().setMinX(-1);
        graph.getViewport().setMaxX(sem);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(sem);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Performance on a scale of 100");

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

    double getAggregate(double[] arr) {
        double sum = 0.0d;
        int count = 0;
        for (double d : arr) {
            Log.d(this.TAG, "d =  " + d);
            if (d > 0.0d) {
                count++;
            }
            sum += d;
        }
        Log.d(this.TAG, "Zero not sent due to " + count + "&&" + sum);
        if (count == 0 || sum == 0.0d) {
            return 0.0d;
        }
        return sum / ((double) count);
    }

    void getData() {
        String username;
        String str;
        studentBackground sbg = new studentBackground(this);
        String port = getString(R.string.connection_string);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mentor_id = extras.getString("mentor_id");
            username = extras.getString("sid");
            this.state = extras.getString("state");
            this.tempResult = extras.getString("res_str");
            if (this.local_date == null && this.local_rem == null) {
                this.local_rem = extras.getString("new_rem");
                this.local_date = extras.getString("new_date");
                if (this.shouldRatingbeUpdated) {
                    this.ratingBarResultString = extras.getString("ratingBarResultString");
                    this.isRatingSubmittable = extras.getString("isRatingSubmittable").equals("1");
                    this.isStudentFlagged = extras.getString(mentorTable.column8).equals("1");
                    this.shouldRatingbeUpdated = false;
                }
                Log.d(this.TAG, "Bundle from Mentor isStudentFlagged = " + this.isStudentFlagged);
                Log.d(this.TAG, "Data about isRating: " + this.isRatingSubmittable);
            }
            this.studentImage = extras.getString(mentorTable.column9);
            if (this.studentImage != null) {
                this.defaultImage = false;
            }
            Log.d(this.TAG, "defaultImageStatus in fromMentor = " + this.defaultImage);
            this.from = "mentor";
        } else {
            username = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).getString(getString(R.string.user_name), EnvironmentCompat.MEDIA_UNKNOWN);
            this.studentImage = getSharedPreferences("img_store", 0).getString("img_stud", "null");
            Log.d(this.TAG, "getting studentImage from SharedPreferences = " + this.studentImage);
            if (!this.studentImage.equals("null")) {
                this.defaultImage = false;
            }
            Log.d(this.TAG, "defaultImageStatus in getData(extras==null) = " + this.defaultImage);
        }
        if(from.equals("login")) {
            Log.d(TAG, username + extras);
            sbg.execute(username, port);
            Log.d(TAG, "Started on progresUpdate");
            sbg.onProgressUpdate();
            if (sbg.result.equals("NO Net") || sbg.result.equals("Error.studentHome.php")) {


                SharedPreferences sharedPreferences2 = getApplicationContext().getSharedPreferences("StudResult", Context.MODE_PRIVATE);
                tempResult = sharedPreferences2.getString("HomeStuff", "");


            } else {
                tempResult = sbg.result;
                SharedPreferences settings = getSharedPreferences("StudResult", MODE_PRIVATE);

                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("HomeStuff", tempResult);
                editor.apply();

                // Reading from SharedPreferences
                String value = settings.getString("key", "");
                Log.d(TAG, value + tempResult);
            }
        }
        if (this.tempResult != null) {
            this.arr = this.tempResult.split("<br>");
            int length = this.arr.length;
            if (arr.length != 1) {
                this.sid = this.arr[0];
                if (this.from.equals(Event.LOGIN) && this.justLoggedIn) {
                    getImage();
                    Log.d(this.TAG, "Getting Image");
                    this.justLoggedIn = false;
                }
                Log.d(this.TAG, this.arr[1] + " " + this.arr[2]);
                String[] namePair = this.arr[1].split("&&");
                this.Student_Name = namePair[0];
                this.Parent_Name = namePair[1];
                length = namePair.length;
                if (namePair.length < 2 || !namePair[2].substring(0, 1).equals("0")) {
                    Log.d(this.TAG, "OTP = None");
                } else {
                    Log.d(this.TAG, namePair[2].substring(0, 1));
                    this.otp = namePair[2].substring(2);
                    Log.d(this.TAG, "OTP = " + this.otp);
                }
                String[] classInfoPair = this.arr[2].split("&&");
                this.class_id = classInfoPair[0];
                this.sem = Integer.parseInt(classInfoPair[1]);
                /*str = this.TAG;
                String str4 = this.Parent_Name;
                Log.d(str2, str2 + " " + this.class_id);*/
                this.Attendance = Integer.parseInt(this.arr[3]);
                /*Log.d(this.TAG, "arr[5] = " + this.arr[4]);*/
                String[] temp = this.arr[4].split("~");
                while (temp.length < this.sem) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    String[] strArr = this.arr;
                    strArr[4] = stringBuilder2.append(strArr[4]).append("0~").toString();
                    temp = this.arr[4].split("~");
                }
                str = this.TAG;
                String[] strArr2 = this.arr;
                /*Log.d(str2, r0[4] + " " + temp.length);*/
                this.Academics = new double[this.sem];
                for (int i = 0; i < sem; i++)
                    Academics[i] = Double.parseDouble(temp[i]);
                this.Academic_Aggregate = getAggregate(this.Academics);
                Log.d(this.TAG, "FCFS AGGR = " + this.Academic_Aggregate);
                isStudentFlagged = arr[5].equals("1"); //returns true if the student is flagged in the database
                this.Mentor_Name = this.arr[6];
                this.Remarks = this.arr[7].split("~");
                this.Dates = this.arr[8].split("~");
                this.mentor_fields = this.arr[9].split("~");
                Log.d(this.TAG, "mentor fields String: " + this.mentor_fields[0]);
                return;
                    }
                }
            }



    void setCircularImage() {
        if (!this.defaultImage) {
            ((CircleImageView) findViewById(R.id.circ_img)).setImageBitmap(decodeBase64(this.studentImage));
            Log.d(this.TAG, "getImage done");
        }
    }

    void setProfile() {
        ((TextView) findViewById(R.id.ht_noRight)).setText(this.sid);
        ((TextView) findViewById(R.id.nameRight)).setText(this.Student_Name);
        ((TextView) findViewById(R.id.parentNameRight)).setText(this.Parent_Name);
        ((TextView) findViewById(R.id.class_idRight)).setText((this.sem % 2 == 0 ? this.sem / 2 : (this.sem / 2) + 1) + BuildConfig.FLAVOR);
        Log.d(this.TAG, "defaultImageStatus in setProfile = " + this.defaultImage);
        setCircularImage();
        ((TextView) findViewById(R.id.branchRight)).setText(this.class_id.substring(1, 3).equalsIgnoreCase("IT") ? "IT" : this.class_id.substring(1, 3).toUpperCase() + "E");
        ((TextView) findViewById(R.id.sectionRight)).setText(this.class_id.substring(3).toUpperCase());
        ((TextView) findViewById(R.id.semRight)).setText((this.sem % 2 == 0 ? 2 : 1) + BuildConfig.FLAVOR);
        Log.d(this.TAG, "setProfile isStudentFlagged = " + this.isStudentFlagged);
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
        cb.setChecked(this.isStudentFlagged);
        Log.d(this.TAG, this.sid);
        if (!this.sid.equalsIgnoreCase("14BD1A052X")) {
            ((TextView) findViewById(R.id.parentNoLeft)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.parentNoRight)).setVisibility(View.INVISIBLE);
        }
        if (this.from.equals(Event.LOGIN)) {
            cb.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.link_change_password)).setVisibility(View.VISIBLE);
        } else if (this.from.equals("mentor")) {
            if (this.otp != null) {
                ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.OTPRight)).setText(this.otp);
            }
            cb.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.link_change_password)).setVisibility(View.INVISIBLE);
        }
        cb.setOnCheckedChangeListener(new AnonymousClass19(cb));
    }

    String getLoginStatus() {
        String port = getString(R.string.connection_string);
        this.scbg = new statusCheckBG(this);
        this.scbg.execute(new String[]{port, this.sid});
        this.scbg.onProgressUpdate(new Void[0]);
        if (!this.scbg.result.matches("NO NET")) {
            return this.scbg.result;
        }
        Log.d(this.TAG, "scbg.result" + this.scbg.result);
        Toast.makeText(this, this.scbg.result, Toast.LENGTH_SHORT).show();
        Log.d(this.TAG, "Just before return");
        return null;
    }

    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.TAG, "Reached in Student");
        super.onCreate(savedInstanceState);
        testFlag=true;
        getData();
        if (this.tempResult != null && this.arr.length != 1) {
            BottomBar bottomBar;
            setContentView(R.layout.student_homepage);
            this.test = (TextView) findViewById(R.id.mentorName);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            toolbar.setTitle((CharSequence) "My Attendance");
            toolbar.setTitleTextColor(Color.GRAY);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (this.from.equals(Event.LOGIN)) {
                navigationView.getMenu().getItem(0).setVisible(true);
                navigationView.getMenu().getItem(1).setVisible(false);
            } else {
                navigationView.getMenu().getItem(0).setVisible(false);
                navigationView.getMenu().getItem(1).setVisible(true);
            }
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.addOnLayoutChangeListener(new AnonymousClass20(navigationView));
            Log.d(TAG,from + "printing from!");
            if (this.from.equals("mentor")) {
                bottomBar = (BottomBar) findViewById(R.id.bottomBar_mentor);
                bottomBar.setVisibility(View.VISIBLE);
                if (this.state.equals("repeat")) {
                    bottomBar.selectTabWithId(R.id.tab_set_remarks);
                }
            } else {
                bottomBar = (BottomBar) findViewById(R.id.bottomBar_student);
                Log.d(TAG,"Reaching this line");
                if(bottomBar!=null)
                bottomBar.setVisibility(View.VISIBLE);
            }

            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    switch (tabId) {
                        case R.id.tab_student_dashboard /*2131689762*/:
                            Log.d(TAG,"Setting area for dashBoard!");
                            toolbar.setTitle((CharSequence) "DashBoard");
                            StudentHome.this.setAreaFor(R.id.student_dashboard);
                            StudentHome.this.setProfile();
                            StudentHome.this.getAttGraph(StudentHome.this.Attendance);
                            break;
                        case R.id.tab_view_remarks /*2131689763*/:

                            /*ProgressDialog pd = ProgressDialog.show(StudentHome.this, "Loading Remarks", "Retrieving from database");
                            pd.setProgressStyle(1);*/
                            StudentHome.this.getData();
                            toolbar.setTitle((CharSequence) "Attendance & Remarks");
                            StudentHome.this.setAreaFor(R.id.view_remarks);
                            StudentHome.this.getRemarks();
                            StudentHome.this.getRemarks();

                            break;
                        case R.id.tab_performance /*2131689764*/:
                            toolbar.setTitle((CharSequence) "Performance");
                            StudentHome.this.setAreaFor(R.id.view_performance);
                            StudentHome.this.showPerformance();
                            StudentHome.this.showAcadValue();
                            Log.d(StudentHome.this.TAG, "Button Clicked");
                            break;
                        case R.id.tab_set_remarks /*2131689765*/:
                            toolbar.setTitle((CharSequence) "Set Remarks");
                            StudentHome.this.setAreaFor(R.id.RemarkView);
                            StudentHome.this.findViewById(R.id.RemarkView).setVisibility(View.VISIBLE);
                            StudentHome.this.nav_SetRemark();
                            Log.d(StudentHome.this.TAG, "Button Clicked");
                            break;
                        default:
                    }
                }
            });
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!StudentHome.this.from.equals(Event.LOGIN)) {
                        return;
                    }
                    if (StudentHome.this.getLoginStatus() != null) {
                        StudentHome.this.LoggedInDevices = StudentHome.this.getLoginStatus().split(",");
                        if (StudentHome.this.getLoginStatus().indexOf(StudentHome.this.getIMEI(StudentHome.this)) < 0) {
                            Toast.makeText(StudentHome.this, "You are logged out from " + StudentHome.this.sid, Toast.LENGTH_SHORT).show();
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
                    Log.d(StudentHome.this.TAG, "Internet Status" + StudentHome.this.hasInternet);
                }
            }, 5000);
        }
    }

    void showPerformance() {
        this.adapter = ArrayAdapter.createFromResource(this, R.array.fieldList, android.R.layout.simple_spinner_dropdown_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sp = (Spinner) findViewById(R.id.spinner_perf);
        this.sp.setAdapter(this.adapter);
        this.sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(StudentHome.this.TAG, "Entering the listener" + ((int) parent.getItemIdAtPosition(position)));
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
        Log.d(this.TAG, "acadAggr before acadGraph = " + this.Academic_Aggregate + BuildConfig.FLAVOR);
        getAcadGraph(this.Academic_Aggregate);
        getAggrGraph();
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
            startMain.setFlags(268435456);
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
        getApplicationContext().getSharedPreferences("img_store", 0).edit().clear().commit();
        Log.d(this.TAG, "Shared Preferences are cleared");
        this.issharedPreferencesEmpty = true;
        startActivity(new Intent(this, MainActivity.class));
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        getData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (id == R.id.Logout) {
            Log.d(this.TAG, "hasInternet = " + this.hasInternet);
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START, true);
            if (getLoginStatus() == null) {
                this.hasInternet = false;
            } else {
                this.hasInternet = true;
            }
            if (this.hasInternet) {
                LogoutBG lbg = new LogoutBG(this);
                String port = getString(R.string.connection_string);
                AlertDialog alertDialog = new Builder(this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Your account is already active in some other device");
                alertDialog.setButton(-1, "All Devices", new AnonymousClass25(lbg, port));
                alertDialog.setButton(-2, "Current Device", new AnonymousClass26(lbg, port));
                if (this.LoggedInDevices == null || this.LoggedInDevices.length <= 1) {
                    lbg.execute(new String[]{port, this.sid});
                } else {
                    alertDialog.show();
                    Log.d(this.TAG, "Dialog shown");
                }
                Handler h1 = new Handler();
                h1.post(new AnonymousClass27(lbg, h1));
            } else {
                Toast.makeText(this, "NO NET", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.Back) {
            startActivity(new Intent(this, MentorHome.class));
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
            getAttGraph(this.Attendance);
        } else if (v.getId() == R.id.UpdateToServerStudentSide) {
            Log.d(this.TAG, "Update To server Clicked");
            Button b = (Button) findViewById(R.id.UpdateToServerStudentSide);
            b.setText("Updating Data...");
            b.setClickable(false);
            if (new UpdateData(this, getString(R.string.connection_string), this.mentor_id, this.sem).UpdateToServer()) {
                Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MentorHome.class);
                i.putExtra("coming_from", this.sid);
                Editor editor = getApplicationContext().getSharedPreferences("from_student", 0).edit();
                editor.putString("from_sid", this.sid);
                editor.commit();
                startActivity(i);
                return;
            }
            Toast.makeText(this, "NO NET", Toast.LENGTH_LONG).show();
            b.setText("CLICK TO UPDATE TO SERVER");
        }
    }
}