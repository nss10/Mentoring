package kmit.mentoring;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.provider.BaseColumns._ID;
import static kmit.mentoring.localStruct.mentorTable.column1;
import static kmit.mentoring.localStruct.mentorTable.column2;
import static kmit.mentoring.localStruct.mentorTable.column3;
import static kmit.mentoring.localStruct.mentorTable.column4;
import static kmit.mentoring.localStruct.mentorTable.column5;
import static kmit.mentoring.localStruct.mentorTable.column6;
import static kmit.mentoring.localStruct.mentorTable.column7;
import static kmit.mentoring.localStruct.mentorTable.table_name;

/**
 * Created by sesha sai on 2/5/2017.
 */

public class MentorHome extends Activity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String TAG = "MentorHome";
    SQLiteDatabase db;
    localStruct.localDB db_obj;
    int studentSem;
    String result[], username,name,allStudentString,loginStatus;
    Toolbar toolbar;
    ListView stdlist;
    boolean isDataUpdated,shouldRatingBeChanged=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_homepage);

        Log.d(TAG, "Mentor created~" + savedInstanceState);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        loginStatus = sharedPreferences.getString(getString(R.string.just_logged_in), "unknown");
        username = sharedPreferences.getString(getString(R.string.user_name), "unknown");
        getDataFromLocal();
        Log.d(TAG,"SEMpref="+studentSem);


        if (loginStatus.matches("true")) {
            editor.putString(getString(R.string.name_of_user), name);
            editor.putString(getString(R.string.just_logged_in), "false");
             editor.putInt("SEM",studentSem);
        }
        else
        {
            name = sharedPreferences.getString(getString(R.string.name_of_user), "unknown");
            studentSem = sharedPreferences.getInt("SEM",0);
        }
        editor.commit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.DKGRAY);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle("MY STUDENTS");

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                navigationView.removeOnLayoutChangeListener(this);

                TextView textView = (TextView) navigationView.findViewById(R.id.login_name);
                textView.setText("Hello " + name);
            }
        });

        Button b = (Button) findViewById(R.id.UpdateToServer);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToServer();
                getDataFromServer();
                if(isDataUpdated){
                    onCreate(null);
                    Toast.makeText(getApplicationContext(),"Data Updated",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Data cannot be Updated",Toast.LENGTH_LONG).show();
            }
        });


    }

    void getDataFromServer() {
        mentorBackground mbg = new mentorBackground(this);
        String port = getString(R.string.connection_string);
        mbg.execute(username, port);
        mbg.onProgressUpdate();
        result = mbg.result.split("#");
        Log.d(TAG, mbg.result);
        if (!mbg.result.matches("NO NET")) {
            result = mbg.result.split("#");
            Log.d(TAG, mbg.result);
            name = result[0];
            Log.d(TAG,"Getting Name from server and is "+name);
            allStudentString = result[1];
            UpdateLocal(allStudentString);

        }
    }

    String getCurMF(int sem,String[] mf)
    {
        String str="";
           if(mf[0].length()>=2*sem-1 && mf.length!=1)
            for(int i=0;i<7;i++)
                str+=mf[i].charAt(2*(sem-1))+"~";
            else
               str="0~0~0~0~0~0~0~";
        return str;

    }
    void UpdateLocal(String updateString) {
        db_obj = new localStruct().new localDB(getApplicationContext());
        db = db_obj.getWritableDatabase();
        db.delete(table_name, null, null);
        ContentValues values = new ContentValues();
        String[] listData;
        String[] studentStrings = updateString.split("STUDENT SEPERATOR SPLIT");
        for (int i = 0; i < studentStrings.length; i++) {

            listData = studentStrings[i].split("<br>");

            values.put(column1, listData[0]);
            values.put(column2, listData[1]);
            studentSem=Integer.parseInt(listData[2]);
            values.put(column3, studentStrings[i]);
            String temp=getCurMF(studentSem,listData[8].split("~"));
            Log.d(TAG,"Sem ="+studentSem);
            values.put(column6,temp);
            if(temp.contains("0"))
                values.put(column7,"0");
            else
                values.put(column7,"1");

            Log.d(TAG, "inserting " + listData[0] + "," + listData[1] + " , " + studentStrings[i]+"Current sem rating:"+getCurMF(Integer.parseInt(listData[2]),listData[8].split("~")));
            db.insert(table_name, null, values);
        }

    }

    void getDataFromLocal() {

        if(loginStatus.matches("true"))
            getDataFromServer();
        db_obj = new localStruct().new localDB(getApplicationContext());
        db = db_obj.getReadableDatabase();
        Log.d(TAG, "" + db);
        String[] projection = new String[]{_ID, column1, column2, column3, column4, column5,column6,column7};
        int views[] = new int[]{R.id._id, R.id.ht_no, R.id.name};
        Cursor cursor = db.query(table_name, projection, null, null, null, null, null);
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.ht_no_and_name, cursor, projection, views, 1);
        stdlist = (ListView) findViewById(R.id.std_list2);
        stdlist.setAdapter(simpleCursorAdapter);



        stdlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String sid = c.getString(c.getColumnIndex(column1));
                String res_str = c.getString(c.getColumnIndex(column3));
                String new_rem = c.getString(c.getColumnIndex(column4));
                String new_date = c.getString(c.getColumnIndex(column5));
                String ratingBarResultString = c.getString(c.getColumnIndex(column6));
                String isRatingSubmittable = c.getString(c.getColumnIndex(column7));
                if(ratingBarResultString==null) ratingBarResultString="0~0~0~0~0~0~0~";//If that student is never rated before, 0 is given to avoid nullPointerException
                Intent i = new Intent(getApplicationContext(), StudentHome.class);
                i.putExtra("sid", sid);
                i.putExtra("res_str", res_str);
                i.putExtra("new_rem", new_rem);
                i.putExtra("new_date", new_date);
                i.putExtra("ratingBarResultString", ratingBarResultString);
                i.putExtra("isRatingSubmittable", isRatingSubmittable);
                startActivity(i);

            }
        });



    }

    void UpdateToServer() {

        db_obj = new localStruct().new localDB(getApplicationContext());
        db = db_obj.getReadableDatabase();
        getDataFromLocal();
        String[] projection = new String[]{_ID, column1, column2, column3, column4, column5,column6,column7};
        int views[] = new int[]{R.id._id, R.id.ht_no, R.id.name};
        Cursor cursor = db.query(table_name, projection, null, null, null, null, null);
        Log.d(TAG,"Sem ="+studentSem);

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this, R.layout.ht_no_and_name, cursor, projection, views, 1);
        //Getting Data From Local DB and concatenating
        String sid = "", remarks = "", date = "",rating="",isRatingSubmittable="";
        for (int i = 0; i < simpleCursorAdapter.getCount(); i++) {
            Cursor c = (Cursor) simpleCursorAdapter.getItem(i);
            sid += c.getString(c.getColumnIndex(column1)) + "SPLITTER";
            remarks += c.getString(c.getColumnIndex(column4)) + "SPLITTER";
            date += c.getString(c.getColumnIndex(column5)) + "SPLITTER";
            rating += c.getString(c.getColumnIndex(column6)) + "SPLITTER";
            isRatingSubmittable += c.getString(c.getColumnIndex(column7)) + "SPLITTER";

        }


        mbgToInsertData mbgi = new mbgToInsertData(this);
        String port = getString(R.string.connection_string);
        Log.d(TAG, port + " " + username + " " + sid + " " + remarks + " " + date+" "+rating+" "+isRatingSubmittable+" "+studentSem+"");
        mbgi.execute(port, username, sid, remarks, date,rating,isRatingSubmittable,studentSem+"");
        mbgi.onProgressUpdate();

        if(mbgi.result.matches("NO NET")) {
            Toast.makeText(getApplicationContext(),"NO NET",Toast.LENGTH_LONG).show();
            isDataUpdated=false;
        }
        else
            isDataUpdated=true;



    }



    public void onBackPressed() {
        //Action of Home Button
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        switch(id){
            case R.id.students:

                toolbar.setTitle("MY STUDENTS");
                break;

            case R.id.Account:

                toolbar.setTitle("MY Account");
                break;

            case R.id.logout:

                UpdateToServer();
                if(isDataUpdated)
                {
                    Toast.makeText(getApplicationContext(),"Data Updated and logged out",Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences = getApplicationContext()
                            .getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    db.delete(table_name, null, null);
                    editor.remove("username");
                    editor.commit();
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
                else
                    Toast.makeText(getApplicationContext(),"You need internet to logout",Toast.LENGTH_LONG).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
