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
import kmit.mentoring.localStruct.localDB;
import kmit.mentoring.localStruct.mentorTable;

public class MentorHome extends Activity implements OnNavigationItemSelectedListener, OnClickListener {
    ListView Flaggedstdlist;
    String TAG;
    String allStudentString;
    int cur_sidCount;
    SQLiteDatabase db;
    localDB db_obj;
    Editor editor;
    Bundle extras;
    String from_sid;
    boolean hasInternet;
    boolean isDataUpdated;
    boolean isLocalDataFilled;
    ProgressDialog loading;
    String loginStatus;
    String name;
    NavigationView navigationView;
    String[] result;
    statusCheckBG scbg;
    boolean shouldRatingBeChanged;
    ListView stdlist;
    public int studentCount;
    int studentSem;
    Toolbar toolbar;
    String username;

    /* renamed from: kmit.mentoring.MentorHome.1 */
    class C03781 implements Runnable {
        final /* synthetic */ Handler val$h1;

        C03781(Handler handler) {
            this.val$h1 = handler;
        }

        public void run() {
            if (MentorHome.this.isLocalDataFilled) {
                MentorHome.this.onCreate(null);
                Toast.makeText(MentorHome.this.getApplicationContext(), "Data Updated",Toast.LENGTH_LONG).show();
                return;
            }
            this.val$h1.postDelayed(this, 100);
        }
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
            } catch (MalformedURLException e5) {
                e = e5;
                e.printStackTrace();
                return image;
            } catch (IOException e6) {
                e2 = e6;
                e2.printStackTrace();
                return image;
            }
            return image;
        }
    }

    /* renamed from: kmit.mentoring.MentorHome.2 */
    class C03802 implements Runnable {

        /* renamed from: kmit.mentoring.MentorHome.2.1 */
        class C03791 implements Runnable {
            final /* synthetic */ Handler val$UIOnCreateHandler;

            C03791(Handler handler) {
                this.val$UIOnCreateHandler = handler;
            }

            public void run() {
                Log.d(MentorHome.this.TAG, "isDataUpdated" + MentorHome.this.isDataUpdated);
                if (MentorHome.this.isDataUpdated) {
                    MentorHome.this.UIonDataUpdate();
                } else {
                    this.val$UIOnCreateHandler.postDelayed(this, 1000);
                }
            }
        }

        C03802() {
        }

        public void run() {
            Log.d(MentorHome.this.TAG, "gettingDataFromLocalAsync running...");
            MentorHome.this.getDataFromLocal();
            MentorHome.this.editor.putString(MentorHome.this.getString(R.string.name_of_user), MentorHome.this.name);
            MentorHome.this.editor.putInt("SEM", MentorHome.this.studentSem);
            editor.commit();
            Log.d(TAG,"SEMfromInnerClass" + studentSem);
            MentorHome.this.isDataUpdated = true;
            Handler UIOnCreateHandler = new Handler();
            UIOnCreateHandler.postDelayed(new C03791(UIOnCreateHandler), 2000);
        }
    }

    /* renamed from: kmit.mentoring.MentorHome.3 */
    class C03813 implements OnLayoutChangeListener {
        C03813() {
        }

        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            MentorHome.this.navigationView.removeOnLayoutChangeListener(this);
            ((TextView) MentorHome.this.navigationView.findViewById(R.id.login_name)).setText("Hello " + MentorHome.this.name);
        }
    }

    /* renamed from: kmit.mentoring.MentorHome.4 */
    class C03824 implements OnClickListener {
        final /* synthetic */ Button val$b;

        C03824(Button button) {
            this.val$b = button;
        }

        public void onClick(View v) {
            this.val$b.setClickable(false);
            this.val$b.setText("Data Updating...");
            MentorHome.this.getDataFromLocal();
            Log.d(TAG,"ButtonClicked -> UpdateToServer");
            MentorHome.this.UpdateToServer();
            MentorHome.this.getDataFromServer();
            MentorHome.this.UIonDataUpdate();
        }
    }

    /* renamed from: kmit.mentoring.MentorHome.5 */
    class C03835 implements OnItemClickListener {
        C03835() {
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MentorHome.this.selectStudent((Cursor) parent.getAdapter().getItem(position), "fresh");
        }
    }

    /* renamed from: kmit.mentoring.MentorHome.6 */
    class C03846 implements OnClickListener {
        C03846() {
        }

        public void onClick(View v) {
            MentorHome.this.changePassword();
        }
    }

    public MentorHome() {
        this.TAG = "MentorHome";
        this.shouldRatingBeChanged = true;
    }

    void UIonDataUpdate() {
        Handler h1 = new Handler();
        Log.d(this.TAG, "isLocalDataFilled" + this.isLocalDataFilled);
        if (this.isDataUpdated) {
            h1.postDelayed(new C03781(h1), 0);
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

    void onLogout(boolean shouldUpdate) {
        if (shouldUpdate) {
            UpdateToServer();
        }
        if (this.isDataUpdated || !shouldUpdate) {
            Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0).edit();
            this.db.delete(mentorTable.table_name, null, null);
            editor.remove("username");
            editor.remove("login_status");
            editor.commit();
            LogoutBG lbg = new LogoutBG(this);
            lbg.execute(new String[]{getString(R.string.connection_string), this.username});
            lbg.onProgressUpdate(new Void[0]);
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        Toast.makeText(getApplicationContext(), "You need internet to logout", Toast.LENGTH_LONG).show();
    }

    public String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), 0);
        this.editor = sharedPreferences.edit();
        this.loginStatus = sharedPreferences.getString(getString(R.string.just_logged_in), EnvironmentCompat.MEDIA_UNKNOWN);
        this.username = sharedPreferences.getString(getString(R.string.user_name), EnvironmentCompat.MEDIA_UNKNOWN);
        setContentView(R.layout.mentor_homepage);
        Log.d(this.TAG, "Mentor created~" + savedInstanceState);
        if (this.loginStatus.matches("true")) {
            this.loading = ProgressDialog.show(this, "Obtaining data from server...", null, true, true);
            this.editor.putString(getString(R.string.just_logged_in), "false");
            Log.d(this.TAG, "Getting data from Server");
            new Handler().post(new C03802());
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
        this.navigationView.addOnLayoutChangeListener(new C03813());
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
        Button b = (Button) findViewById(R.id.UpdateToServer);
        b.setHovered(true);
        b.setOnClickListener(new C03824(b));
        if (this.hasInternet) {
            if (getLoginStatus().indexOf(getIMEI(this)) < 0) {
                Log.d(this.TAG, "loginStatus " + this.loginStatus);
                Toast.makeText(this, "You are logged out from " + this.username, 1).show();
                onLogout(false);
            }
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    private void getImage(String sid) {
        new AnonymousClass1GetImage(sid).execute(new String[]{sid});
    }

    void getDataFromServer() {
        Log.d(this.TAG, "GetDataFromServer");
        mentorBackground mbg = new mentorBackground(this);
        String port = getString(R.string.connection_string);
        mbg.execute(new String[]{this.username, port});
        mbg.onProgressUpdate(new Void[0]);
        Log.d(this.TAG, "indexOfEOF :<" + mbg.result + ">");
        while (mbg.result.indexOf("E.O.F") < 0) {
            Log.d(this.TAG, "result :<" + this.result + ">");
            if (mbg.result.matches("NO NET")) {
                break;
            }
        }
        if (mbg.result.matches("NO NET")) {
            Toast.makeText(this, "NO NET", Toast.LENGTH_SHORT).show();
        }
        this.result = mbg.result.split("#");
        Log.d(this.TAG, mbg.result);
        if (!mbg.result.matches("NO NET")) {
            this.result = mbg.result.split("#");
            Log.d(this.TAG, mbg.result);
            this.name = this.result[0];
            Log.d(this.TAG, "Getting Name from server and is " + this.result[0]);
            this.allStudentString = this.result[1];
            Log.d(this.TAG, "result[1] = " + this.result[1]);
            UpdateLocal(this.allStudentString);
        }
    }

    void UpdateLocal(String updateString) {

        this.db_obj = new localStruct().new localDB(getApplicationContext());
        this.db = this.db_obj.getWritableDatabase();
        this.db.delete(mentorTable.table_name, null, null);
        ContentValues values = new ContentValues();
        String[] studentStrings = updateString.split("STUDENT SEPERATOR SPLIT");
        this.studentCount = studentStrings.length;
        for (int i = 0; i < this.studentCount; i++) {
            String[] listData = studentStrings[i].split("<br>");
            this.studentSem = Integer.parseInt(listData[2].split("&&")[1]);
            Log.d(this.TAG, "loginStatus = " + this.loginStatus);
            Log.d(this.TAG, "studentSem = " + studentSem + listData[2].split("&&")[1]);

            if (this.loginStatus.matches("true")) {
                getImage(listData[0]);
            } else {
                this.isLocalDataFilled = true;
            }
            new UpdateData(this, getString(R.string.connection_string), this.username, studentSem).UpdateLocal(updateString);

        }
    }

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

        Cursor c2 = (Cursor) this.stdlist.getItemAtPosition(3);
        Log.d(TAG,"IsRatingUpdatableFor" + c2.getString(c2.getColumnIndex(mentorTable.column1))+ " - " + c2.getString(c2.getColumnIndex(mentorTable.column7)));
        if (this.from_sid != null) {
            for (int position = 0; position < this.studentCount; position++) {
                Cursor c = (Cursor) this.stdlist.getItemAtPosition(position);
                if (this.from_sid.equals(c.getString(c.getColumnIndex(mentorTable.column1)))) {
                    selectStudent(c, "repeat");
                }
            }
        }
        this.stdlist.setOnItemClickListener(new C03835());
    }

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


    void selectStudent(Cursor c, String state) {
        String sid = c.getString(c.getColumnIndex(mentorTable.column1));
        String res_str = c.getString(c.getColumnIndex(mentorTable.column3));
        String new_rem = c.getString(c.getColumnIndex(mentorTable.column4));
        String new_date = c.getString(c.getColumnIndex(mentorTable.column5));
        String ratingBarResultString = c.getString(c.getColumnIndex(mentorTable.column6));
        String isRatingSubmittable = c.getString(c.getColumnIndex(mentorTable.column7));
        String isStudentFlagged = c.getString(c.getColumnIndex(mentorTable.column8));
        String studentImage = c.getString(c.getColumnIndex(mentorTable.column9));
        Log.d(this.TAG, "sid = " + sid);
        if (isStudentFlagged == null) {
            isStudentFlagged = "0";
        }
        if (ratingBarResultString == null) {
            ratingBarResultString = "0~0~0~0~0~0~0~";
        }
        Intent i = new Intent(getApplicationContext(), StudentHome.class);
        i.putExtra("sid", sid);
        i.putExtra("mentor_id", this.username);
        i.putExtra("state", state);
        Log.d(this.TAG, "Creating intent for sid = " + sid);
        i.putExtra("res_str", res_str);
        i.putExtra("new_rem", new_rem);
        i.putExtra("new_date", new_date);
        i.putExtra("ratingBarResultString", ratingBarResultString);
        i.putExtra("isRatingSubmittable", isRatingSubmittable);
        i.putExtra(mentorTable.column8, isStudentFlagged);
        i.putExtra(mentorTable.column9, studentImage);
        startActivity(i);
        finish();
        Log.d(this.TAG, "Creating intent for sid = " + sid);
    }

    void UpdateToServer() {
        String port = getString(R.string.connection_string);
        getDataFromLocal();
        Log.d(TAG,"StudentSeminUpdatetosServer = "+ studentSem);
        this.isDataUpdated = new UpdateData(this, port, this.username, this.studentSem).UpdateToServer();
    }

    public void onBackPressed() {
        Intent startMain = new Intent("android.intent.action.MAIN");
        startMain.addCategory("android.intent.category.HOME");
        startMain.setFlags(268435456);
        startActivity(startMain);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lv = (ListView) findViewById(R.id.std_list2);
        View view_change_password = findViewById(R.id.view_change_password);
        switch (id) {
            case R.id.students /*2131689770*/:
                this.toolbar.setTitle((CharSequence) "MY STUDENTS");
                lv.setVisibility(View.INVISIBLE);
                view_change_password.setVisibility(View.VISIBLE);
                break;
            case R.id.change_password /*2131689771*/:
                this.toolbar.setTitle((CharSequence) "MY Account");
                lv.setVisibility(View.INVISIBLE);
                view_change_password.setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.UpdateToServer)).setVisibility(View.INVISIBLE);
                ((TableRow) findViewById(R.id.indicatorRow)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.button_change_password)).setOnClickListener(new C03846());
                break;
            case R.id.logout /*2131689772*/:
                onLogout(true);
                break;
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
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
}
