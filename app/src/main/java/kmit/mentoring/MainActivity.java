package kmit.mentoring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener{
    Button reg;
    String TAG="MAIN";
    boolean isCompatible = true;
    public static final int RequestPermissionCode = 1;
    int RequestCheckResult;
    boolean RequestTF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        String username=sharedPreferences.getString(getString(R.string.user_name),"unknown");
        if(!username.matches("unknown"))
        {
            Intent i;
            if(username.length()!=10) {
                i=new Intent(this,MentorHome.class);

            }

            else
                i=new Intent(this,StudentHome.class);
            startActivity(i);

        }
        Log.d(TAG,"Content View not Set");
        try {
            setContentView(R.layout.activity_main);


        }
        catch (Exception e)
        {
            Toast.makeText(this, "Incompatible OS", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Incompatible OS");
            builder.setMessage("We're very sorry :( , this App is incompatible with your version of android. \n Hope we see you soon with Lollipop or above ");
            builder.setCancelable(true);
//            builder.setIcon(R.drawable.lollipoplogo);
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(TAG,"Exception Handled"+e);
            isCompatible = false;
//            setContentView(R.layout.sorry_page);


        }
        Log.d(TAG,"Content View Set with isCompatible="+isCompatible);


        if(isCompatible)
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            reg =(Button) findViewById(R.id.loginButton);
            reg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START,true);

                    // Do something in response to button click
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            reg=(Button)findViewById(R.id.regbutton);
            reg.setOnClickListener(this);
            EnableRuntimePermission();
        }

        /*new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                System.exit(1);
            }
        }, 10000);*/

    }



    @Override
    public void onBackPressed() {

        if(!isCompatible)
        {
            Log.d(TAG,"isCompatible = "+ isCompatible);
            System.exit(1);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mentor) {
            // Handle the camera action
            Intent i=new Intent(this,mentorlogin.class);
            startActivity(i);
        } else if (id == R.id.student) {
            Intent i=new Intent(this,studentlogin.class);
            startActivity(i);
        } /*else if (id == R.id.admin) {
            Intent i=new Intent(this, temp.class);
            startActivity(i);
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*//*
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.loginButton:
            {

                Log.d(TAG,"Nvas");
                break;
            }
            case R.id.regbutton:
            {
                /*Intent i =new Intent(this,register.class);
                startActivity(i);
                break;*/
            }
        }
    }
    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_PHONE_STATE")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Permission Required");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please provide phone permissions to Student Mentoring .This helps you keep your account secured");
            alertDialog.setButton(-1, "Provide Permissions", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                        MainActivity.this.startActivity(intent);
                    }

            });
            alertDialog.show();
            return;
        }
        String[] strArr = new String[RequestPermissionCode];
        strArr[0] = "android.permission.READ_PHONE_STATE";
        ActivityCompat.requestPermissions(this, strArr, RequestPermissionCode);
    }

    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {
        switch (RC) {
            case RequestPermissionCode /*1*/:
                if (PResult.length <= 0 || PResult[0] != 0) {
                    Toast.makeText(this, "Permission Canceled, Now your application cannot access IMEI.", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    public void PermissionStatus() {
        this.RequestCheckResult = ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_PHONE_STATE");
        if (this.RequestCheckResult == 0) {
            this.RequestTF = true;
        } else {
            this.RequestTF = false;
        }
    }
}

