package kmit.mentoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kmit.mentoring.localStruct.localDB;
import kmit.mentoring.localStruct.mentorTable;

public class MentorHome extends Activity implements OnNavigationItemSelectedListener, OnClickListener {

    Student studArr[];

    public int studentCount;
    int cur_sidCount;
    int studentSem;

    String TAG;
    String allStudentString;
    String loginStatus;
    String username;
    String name;
    String from_sid;
    String mPort;
    String[] result;

    SQLiteDatabase db;
    localDB db_obj;

    Editor editor;

    boolean hasInternet;
    boolean isDataUpdated;
    boolean isLocalDataFilled;
    boolean shouldRatingBeChanged;

    ProgressDialog loading;
    NavigationView navigationView;
    ListView stdlist;
    Toolbar toolbar;
    statusCheckBG scbg;



    public MentorHome() {
        this.TAG = "MentorHome";
        this.shouldRatingBeChanged = true;

    }


//Image related
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
    /* renamed from: kmit.mentoring.MentorHome.1GetImage */
    class AnonymousClass1GetImage extends AsyncTask<String, Void, Bitmap> {
        String img_str;
        final /* synthetic */ String val$sid;

        AnonymousClass1GetImage(String str) {
            this.val$sid = str;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(MentorHome.this.TAG, "Getting images");
        }

        protected void onPostExecute(Bitmap b) {
            Log.d(MentorHome.this.TAG, "onPostExecute" + b);
            super.onPostExecute(b);
            MentorHome mentorHome = MentorHome.this;
            mentorHome.cur_sidCount++;
            Log.d(MentorHome.this.TAG, "Loading isShowing" + MentorHome.this.loading.isShowing());
            MentorHome.this.loading.setCancelable(false);
            if (!MentorHome.this.loading.isShowing()) {
                MentorHome.this.loading = ProgressDialog.show(MentorHome.this, "Obtaining data from server...", null, true, true);
            }
            MentorHome.this.loading.setMessage(MentorHome.this.cur_sidCount + " out of " + MentorHome.this.studentCount + " students updated");
            if (b != null) {
                this.img_str = MentorHome.encodeTobase64(b);
                mentorHome = MentorHome.this;
                localStruct kmit_mentoring_localStruct = new localStruct();
                kmit_mentoring_localStruct.getClass();
                mentorHome.db_obj = new localStruct().new localDB(MentorHome.this.getApplicationContext());
                MentorHome.this.db = MentorHome.this.db_obj.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(mentorTable.column9, this.img_str);
                imageWarehouse.imageMap.put(val$sid ,this.img_str);
                Log.d(MentorHome.this.TAG, MentorHome.this.db.update(mentorTable.table_name, values, "ht_no='" + this.val$sid + "'", null) + " row(s) affected");
                Log.d(MentorHome.this.TAG, this.val$sid + " image updated to localdb where " + mentorTable.column1 + "='" + this.val$sid + "'");
            }
            if (MentorHome.this.cur_sidCount == MentorHome.this.studentCount) {
                MentorHome.this.isLocalDataFilled = true;
                MentorHome.this.cur_sidCount = 0;
                MentorHome.this.loading.dismiss();
            }
        }

        protected Bitmap doInBackground(String... params) {
            URL url;
            MalformedURLException e;
            IOException e2;
            Bitmap image = null;
            try {
                URL url2 = new URL(MentorHome.this.getString(R.string.connection_string) + "getImage.php?id=" + params[0]);
                try {
                    Log.d(MentorHome.this.TAG, "" + url2.openConnection().getInputStream());
                    image = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
                    url = url2;
                } catch (MalformedURLException e3) {
                    e = e3;
                    url = url2;
                    e.printStackTrace();
                    return image;
                } catch (IOException e4) {
                    e2 = e4;
                    url = url2;
                    e2.printStackTrace();
                    return image;
                }
            }  catch (IOException e6) {
                e2 = e6;
                e2.printStackTrace();
                return image;
            }
            return image;
        }
    }
    private void getImage(String sid) {
        new AnonymousClass1GetImage(sid).execute(new String[]{sid});
    }


//Date Related methods
    //Retrieval methods
    void getDataFromLocal() {
        Log.d(this.TAG, "getDataFromLocal");
        if (this.loginStatus.matches("true")) {
            Log.d(this.TAG, "getDataFromLocal->getDataFromServer");

            getDataFromServer();
        }

        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getReadableDatabase();
        Log.d(this.TAG, BuildConfig.FLAVOR + this.db);
        int[] views = new int[]{R.id.ht_no, R.id.name};
        CustomCursorAdapter custom_adapter = new CustomCursorAdapter(this, this.db.query(mentorTable.table_name, new String[]{"_id", mentorTable.column1, mentorTable.column2, mentorTable.column3, mentorTable.column4, mentorTable.column5, mentorTable.column6, mentorTable.column7, mentorTable.column8, mentorTable.column9}, null, null, null, null, "isStudentFlagged DESC"));
        this.stdlist = (ListView) findViewById(R.id.std_list2);
        this.stdlist.setAdapter(custom_adapter);

        /*Cursor c2 = (Cursor) this.stdlist.getItemAtPosition(3);
        Log.d(TAG,"IsRatingUpdatableFor" + c2.getString(c2.getColumnIndex(mentorTable.column1))+ " - " + c2.getString(c2.getColumnIndex(mentorTable.column7)));*/
        if (this.from_sid != null) {
            for (int position = 0; position < this.studentCount; position++) {
                Cursor c = (Cursor) this.stdlist.getItemAtPosition(position);
                if (this.from_sid.equals(c.getString(c.getColumnIndex(mentorTable.column1)))) {
                    selectStudent(c, "repeat");
                }
            }
        }
        this.stdlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MentorHome.this.selectStudent((Cursor) parent.getAdapter().getItem(position), "fresh");

            }
        });
    }
    void getDataFromServer() {
        Log.d(this.TAG, "GetDataFromServer");
        mentorBackground mbg = new mentorBackground(this);
        String port = getString(R.string.connection_string);
        mbg.execute(this.username, port);
        mbg.onProgressUpdate();
        Log.d(this.TAG, "indexOfEOF :<" + mbg.result + ">");
        while (mbg.result.indexOf("E.O.F") < 0) {
            //Log.d(this.TAG, "result :<" + this.result + ">");

            if (mbg.result.matches("NO NET")) {
                Toast.makeText(this, "NO NET", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        this.result = mbg.result.split("#");
        Log.d(this.TAG, mbg.result);
        this.name = this.result[0];
        Log.d(this.TAG, "Getting Name from server and is " + this.result[0]);
        this.allStudentString = this.result[1];
        Log.d(this.TAG, "result[1] = " + this.result[1]);
        UpdateLocal(this.allStudentString);

    }

    //Updater methods
    void UpdateLocal(String updateString) {

        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        this.db.delete(mentorTable.table_name, null, null);
        ContentValues values = new ContentValues();
        String[] studentStrings = updateString.split("STUDENT SEPERATOR SPLIT");
        this.studentCount = studentStrings.length;
        studArr = new Student[studentCount];
        for (int i = 0; i < this.studentCount; i++) {
            studArr[i]  = new Student(studentStrings[i]);
            String[] listData = studentStrings[i].split("<br>");
            this.studentSem = studArr[i].getSem();
            Log.d(this.TAG, "loginStatus = " + this.loginStatus);
            Log.d(this.TAG, "studentSem = " + studentSem);

            if (this.loginStatus.matches("true")) {
                getImage(studArr[i].getSid());
            } else {
                this.isLocalDataFilled = true;
            }
//            new UpdateData(this, getString(R.string.connection_string), this.username, studentSem).UpdateLocal(updateString);
        }
        new UpdateData(this, getString(R.string.connection_string), this.username, studentSem).UpdateLocal2(studArr);

    }
    void UpdateToServer() {
        Log.d(TAG,"Time begins here");

        String port = getString(R.string.connection_string);
        getDataFromLocal();
        Log.d(TAG,"StudentSeminUpdatetosServer = "+ studentSem);
        this.isDataUpdated = new UpdateData(this, port, this.username, this.studentSem).UpdateToServer();
        Log.d(TAG,"Time ends here");

    }


//Other state getters
    String getLoginStatus() {
        String port = getString(R.string.connection_string);
        this.scbg = new statusCheckBG(this);
        this.scbg.execute(new String[]{port, this.username});
        this.scbg.onProgressUpdate(new Void[0]);
        if (this.scbg.result.matches("NO NET")) {
            Toast.makeText(this, this.scbg.result, Toast.LENGTH_SHORT).show();
            return null;
        }
        this.hasInternet = true;
        return this.scbg.result;
    }
    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }


//Other misc. methods
    void changePassword() {
        EditText et_cur_pwd = (EditText) findViewById(R.id.cur_pwd);
        EditText et_new_pwd = (EditText) findViewById(R.id.new_pwd);
        EditText et_confirm_new_pwd = (EditText) findViewById(R.id.confirm_new_pwd);
        Button change_pwd = (Button) findViewById(R.id.button_change_password);
        String cur_pwd = et_cur_pwd.getText().toString().trim();
        if (et_new_pwd.getText().toString().trim().equals(et_confirm_new_pwd.getText().toString().trim())) {
            String new_pwd = et_new_pwd.getText().toString().trim();
            setPasswordBG spbg = new setPasswordBG(this);
            String port = getString(R.string.connection_string);
            Log.d(this.TAG, spbg.getStatus() + "before");
            spbg.execute(port, this.username, cur_pwd, new_pwd);
            Log.d(this.TAG, spbg.getStatus() + "after1");
            spbg.onProgressUpdate(new Void[0]);
        } else {
            Toast.makeText(this, "Passwords did not match", Toast.LENGTH_SHORT).show();
        }
        et_cur_pwd.setText(BuildConfig.FLAVOR);
        et_new_pwd.setText(BuildConfig.FLAVOR);
        et_confirm_new_pwd.setText(BuildConfig.FLAVOR);
    }
    void UIonDataUpdate() {
        final Handler h1 = new Handler();
        Log.d(this.TAG, "isLocalDataFilled" + this.isLocalDataFilled);
        if (this.isDataUpdated) {
            h1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (MentorHome.this.isLocalDataFilled) {
                        MentorHome.this.onCreate(null);
                        Toast.makeText(MentorHome.this.getApplicationContext(), "Data Updated",Toast.LENGTH_LONG).show();
                        return;
                    }
                    h1.postDelayed(this, 100);
                }
            }, 0);
            return;
        }
        Toast.makeText(getApplicationContext(), "Data cannot be Updated", Toast.LENGTH_SHORT).show();
        Button b = (Button) findViewById(R.id.UpdateToServer);
        b.setText("CLICK TO UPDATE TO SERVER");
        b.setClickable(true);
        if (this.loading != null) {
            this.loading.dismiss();
        }
    }
    void selectStudent(Cursor c, String state) {
        String sid = c.getString(c.getColumnIndex(mentorTable.column1));
        String res_str = c.getString(c.getColumnIndex(mentorTable.column3));
        String new_rem = c.getString(c.getColumnIndex(mentorTable.column4));
        String new_date = c.getString(c.getColumnIndex(mentorTable.column5));
        String ratingBarResultString = c.getString(c.getColumnIndex(mentorTable.column6));
        String isRatingSubmittable = c.getString(c.getColumnIndex(mentorTable.column7));
        String isStudentFlagged = c.getString(c.getColumnIndex(mentorTable.column8));
        String studentImage = c.getString(c.getColumnIndex(mentorTable.column9));
//        String studentImage = imageWarehouse.imageMap.get(sid);

        if (isStudentFlagged == null) {
            isStudentFlagged = "0";
        }
        if (ratingBarResultString == null) {
            ratingBarResultString = "0~0~0~0~0~0~0~";
        }
        Intent i = new Intent(getApplicationContext(), StudentHome.class);

        Student student = new Student(res_str);
        student.setLocalRemarks(new_rem,new_date);
        student.setRatingBarResultString(ratingBarResultString);
        student.setRatingSubmittable(isRatingSubmittable.equals("1"));
        student.setStudentFlagged(isStudentFlagged.equals("1"));
        Log.d(TAG,"Student image equals " + studentImage);
        //student.setStudentImage(studentImage);
//         Use to display the changing intent problem
        SharedPreferences img_store = this.getSharedPreferences("img_store", 0);
        Log.d(TAG, "StudentImage adding in sharedPreferences" + student.getStudentImage());
        Editor editor = img_store.edit();
        editor.putString("img_stud", studentImage);
        editor.apply();
        i.putExtra("studObj",student);
        i.putExtra("mentor_id", this.username);
        i.putExtra("state", state);
        startActivity(i);
        finish();
        Log.d(this.TAG, "Creating intent for sid = " + student.getSid());
    }




//Event listeners and actions
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0);
        this.editor = sharedPreferences.edit();
        this.loginStatus = sharedPreferences.getString(getString(R.string.just_logged_in), EnvironmentCompat.MEDIA_UNKNOWN);
        this.username = sharedPreferences.getString(getString(R.string.user_name), EnvironmentCompat.MEDIA_UNKNOWN);
        setContentView(R.layout.mentor_homepage);
        if(imageWarehouse.imageMap==null)
            imageWarehouse.imageMap = new HashMap();
        mPort = getString(R.string.connection_string);
        Log.d(this.TAG, "Mentor created~" + savedInstanceState);
        if (this.loginStatus.matches("true")) {
            this.loading = ProgressDialog.show(this, "Obtaining data from server...", null, true, true);
            this.editor.putString(getString(R.string.just_logged_in), "false");
            Log.d(this.TAG, "Getting data from Server");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Log.d(MentorHome.this.TAG, "gettingDataFromLocalAsync running...");
                    MentorHome.this.getDataFromLocal();
                    MentorHome.this.editor.putString(MentorHome.this.getString(R.string.name_of_user), MentorHome.this.name);

                    MentorHome.this.editor.putInt("SEM", MentorHome.this.studentSem);
                    editor.commit();
                    Log.d(TAG,"SEMfromInnerClass" + studentSem);
                    Log.d(TAG,"NAMEfromInnerClass" + username);
                    MentorHome.this.isDataUpdated = true;
                    final Handler UIOnCreateHandler = new Handler();
                    UIOnCreateHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(MentorHome.this.TAG, "isDataUpdated" + MentorHome.this.isDataUpdated);
                            if (MentorHome.this.isDataUpdated) {
                                MentorHome.this.UIonDataUpdate();
                            } else {
                                UIOnCreateHandler.postDelayed(this, 1000);
                            }
                        }
                    }, 2000);
                }
            });
        } else {
            getDataFromLocal();
            Log.d(this.TAG, "Getting Name from server and is " + this.name);
            this.name = sharedPreferences.getString(getString(R.string.name_of_user), this.name);
            this.studentSem = sharedPreferences.getInt("SEM", 0);
        }
        Log.d(this.TAG, "SEMpref=" + this.studentSem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.GRAY);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle((CharSequence) "MY STUDENTS");
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.editor.putString(getString(R.string.name_of_user), this.name);
        this.navigationView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                MentorHome.this.navigationView.removeOnLayoutChangeListener(this);
                ((TextView) MentorHome.this.navigationView.findViewById(R.id.login_name)).setText("Hello,\n" + MentorHome.this.name);
                ((TextView) MentorHome.this.navigationView.findViewById(R.id.LoginID)).setText(username.toUpperCase());
            }
        });
        this.editor.commit();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("coming_from")) {

            Log.d(TAG,"extras.containsKey(\"coming_from\") -> getDataFromServer");
            getDataFromServer();
            getIntent().removeExtra("coming_from");
            onCreate(null);
        } else if (!this.loginStatus.matches("true")) {
            sharedPreferences = getApplicationContext().getSharedPreferences("from_student", 0);
            this.editor = sharedPreferences.edit();
            this.from_sid = sharedPreferences.getString("from_sid", EnvironmentCompat.MEDIA_UNKNOWN);
            Log.d(this.TAG, "Coming From " + this.from_sid);
            this.editor.remove("from_sid");
            this.editor.apply();
            getDataFromLocal();
        }
        final Button b = (Button) findViewById(R.id.UpdateToServer);
        b.setHovered(true);

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                b.setClickable(false);
                MentorHome.this.loading = ProgressDialog.show(MentorHome.this, "Updating data to the server...", null, true, true);
                loading.setCancelable(false);
                b.setText("Data Updating...");


                new Handler().postDelayed(new Runnable(){
                    public void run()
                    {

                        Log.d(TAG,"ButtonClicked -> UpdateToServer");
                        MentorHome.this.UpdateToServer();
                        MentorHome.this.getDataFromServer();
                        MentorHome.this.UIonDataUpdate();
                        if(loading!=null)
                            MentorHome.this.loading.dismiss();

                    }
                },1000);


            }
        });
        if (this.hasInternet) {
            if (getLoginStatus().indexOf(getIMEI(this)) < 0) {
                Log.d(this.TAG, "loginStatus " + this.loginStatus);
                Toast.makeText(this, "You are logged out from " + this.username, Toast.LENGTH_LONG).show();
                onLogout(false);
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onBackPressed() {
        Intent startMain = new Intent("android.intent.action.MAIN");
        startMain.addCategory("android.intent.category.HOME");
        startMain.setFlags(268435456);
        startActivity(startMain);
    }
    void onLogout(boolean shouldUpdate) {

        if (shouldUpdate) {
            UpdateToServer();
        }
        if (this.isDataUpdated || !shouldUpdate) {
            Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).edit();
            this.db.delete(mentorTable.table_name, null, null);
            editor.remove("username");
            editor.remove("login_status");
            editor.remove("image_stud");
            editor.commit();
            imageWarehouse.imageMap = null; //emptying the images
            LogoutBG lbg = new LogoutBG(this);
            lbg.execute(getString(R.string.connection_string), this.username);
            lbg.onProgressUpdate(new Void[0]);
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(MentorHome.this, "Data Updated and logged out", Toast.LENGTH_SHORT).show();

            return;
        }
        Toast.makeText(getApplicationContext(), "You need internet to logout", Toast.LENGTH_LONG).show();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.link_back) {
            ((ListView) findViewById(R.id.std_list2)).setVisibility(View.VISIBLE);
            findViewById(R.id.view_change_password).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.UpdateToServer)).setVisibility(View.VISIBLE);
            ((TableRow) findViewById(R.id.indicatorRow)).setVisibility(View.VISIBLE);
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lv = (ListView) findViewById(R.id.std_list2);
        View view_change_password = findViewById(R.id.view_change_password);
        switch (id) {
            case R.id.students /*2131689770*/:
                this.toolbar.setTitle((CharSequence) "MY STUDENTS");
                lv.setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.UpdateToServer)).setVisibility(View.VISIBLE);
                ((TableRow) findViewById(R.id.indicatorRow)).setVisibility(View.VISIBLE);
                view_change_password.setVisibility(View.INVISIBLE);
                break;
            case R.id.change_password /*2131689771*/:
                this.toolbar.setTitle((CharSequence) "MY Account");
                lv.setVisibility(View.INVISIBLE);
                view_change_password.setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.UpdateToServer)).setVisibility(View.INVISIBLE);
                ((TableRow) findViewById(R.id.indicatorRow)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.button_change_password)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MentorHome.this.changePassword();
                    }
                });
                break;
            case R.id.logout /*2131689772*/:
                MentorHome.this.loading = ProgressDialog.show(MentorHome.this, "Logging out...", "Please wait", true, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onLogout(true);
                    }
                },1000);
                break;
            case R.id.Report_Bug:
                goToUrl("https://goo.gl/forms/LhgsYP4n03M1NkcG2");
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}
