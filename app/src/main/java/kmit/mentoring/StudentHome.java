package kmit.mentoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.format;
import static android.R.attr.id;
import static android.R.attr.rating;
import static android.R.attr.value;
import static android.R.attr.visible;
import static android.R.attr.windowSoftInputMode;
import static android.provider.BaseColumns._ID;
import static android.widget.ArrayAdapter.createFromResource;
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

public class StudentHome extends Activity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    String TAG = "StudentHome";
    TextView test;
    int sem;
    String[] arr;
    GraphView graph;
    String tempResult,local_rem,local_date;
    Spinner sp;
    ArrayAdapter adapter;
    String[] FieldList = {"Group Discussions", "Communication Skills", "Grooming", "Behavior With Peers", "Behavior With Faculty", "Co-Cirricular Activities", "Extra-Cirricular Activities"};
    String[] FieldListAbbr = {"GD", "CS", "Grm", "BWP", "BWF", "CCA", "ECA"};
    String ratingBarResultString;
    String from = "login";
    BarGraphSeries<DataPoint> series;
    String sid,Student_Name;
    String Mentor_Name;
    int Attendance;
    String[] Remarks, Dates, mentor_fields;
    double[] Academics;
    double Academic_Aggregate;
    boolean isRatingSubmittable,shouldRatingbeUpdated=true;
    SQLiteDatabase db;
    localStruct.localDB db_obj;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            ratingBarResultString =data.getStringExtra("Rating String");

//            Toast.makeText(this, ratingBarResultString, Toast.LENGTH_SHORT).show();

            isRatingSubmittable=Boolean.parseBoolean(data.getStringExtra("isSubmittable"));
            Log.d(TAG,"onActivityResult isRatingSubmittable: "+isRatingSubmittable);
            updateRatingToLocal();
        }
    }




    void updateRatingToLocal()
    {

        db_obj=new localStruct().new localDB(getApplicationContext());

        db=db_obj.getWritableDatabase();
        String val = isRatingSubmittable?"1":"0";
        ContentValues values = new ContentValues();
        values.put(column6, ratingBarResultString);
        values.put(column7, val);


        Log.d(TAG,"Local Rating Updated "+db.update(table_name, values, column1 + "='" + sid + "'", null));

    }
    void nav_SetRemark()  {

        Spinner sp = (Spinner) findViewById(R.id.RemarkSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.RemarkList, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);




        Button submitRemark = (Button)findViewById(R.id.SubmitRemark);

        db_obj=new localStruct().new localDB(getApplicationContext());
        db=db_obj.getWritableDatabase();

        final InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        final EditText et = (EditText) findViewById(R.id.RemarkText);
        submitRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String new_rem = et.getText().toString();
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                if(new_rem.length()==0)
                    Toast.makeText(StudentHome.this, "Please Enter a remark to submit", Toast.LENGTH_SHORT).show();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
                    builder.setTitle("Are you sure you want to submit Remark?");
                    builder.setMessage(new_rem);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date d = new Date();
                            Log.d(TAG, new_rem + " " + dateFormat.format(d));
                            ContentValues values = new ContentValues();
                            /*values.put(column1, sid);
                            values.put(column2, Student_Name);
                            values.put(column3, tempResult);*/
                            if (local_date != null && local_rem != null) {
                                local_rem += "~" + new_rem.trim();
                                local_date += "~" + dateFormat.format(d);
                                Log.d(TAG, "Updated local_rem" + local_rem);
                            } else {
                                local_rem = new_rem.trim();
                                local_date = dateFormat.format(d);
                            }
                            values.put(column4, local_rem);
                            values.put(column5, local_date);
                            db.update(table_name, values, column1 + "='" + sid + "'", null);
                            Toast.makeText(getApplicationContext(), "Remark Entered", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, new_rem + " " + dateFormat.format(d));
                            et.setText("");


                        }
                    });

                    builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            et.setText("");
                        }
                    });

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            if(imm != null){
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                            }

                        }
                    });




                    AlertDialog dialog = builder.create();
                    dialog.show();


                }

            }
        });

        Button clearRemark = (Button)findViewById(R.id.ClearRemark);
        clearRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et.setText("");




            }
        });

        Button submitMentorFieldsButton = (Button)findViewById(R.id.submitMentorFields);
        submitMentorFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isRatingSubmittable)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentHome.this);
                    builder.setTitle("You have already Submitted this data before");
                    builder.setMessage("Do you want to resubmit?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button

                            Intent i = new Intent(StudentHome.this,RatingBarDialog.class);
                            Log.d(TAG,"So and So:" + ratingBarResultString);
                            i.putExtra("ratingBarResultString",ratingBarResultString);
                            i.putExtra("isRatingSubmittable",isRatingSubmittable+"");
                            i.putExtra("mode","edit");
                            startActivityForResult(i,2);

                        }
                    });

                    builder.setNeutralButton("View", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            Intent i = new Intent(StudentHome.this,RatingBarDialog.class);
                            Log.d(TAG,"So and So:" + ratingBarResultString);
                            i.putExtra("ratingBarResultString",ratingBarResultString);
                            i.putExtra("isRatingSubmittable",isRatingSubmittable+"");
                            i.putExtra("mode","view");
                            startActivityForResult(i,2);

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                           dialog.dismiss();

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else
                {
                    Intent i = new Intent(StudentHome.this,RatingBarDialog.class);
                    Log.d(TAG,"So and So:" + ratingBarResultString);
                    i.putExtra("ratingBarResultString",ratingBarResultString);
                    i.putExtra("isRatingSubmittable",isRatingSubmittable+"");

                    i.putExtra("mode","edit");
                    startActivityForResult(i,2);

                }


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
                    if(position==7)
                        Toast.makeText(StudentHome.this, "Type the backlog subject name", Toast.LENGTH_SHORT).show();

                    if(et.getText().length()!=0)
                        et.setText(et.getText().toString().trim()+","+ (String) parent.getItemAtPosition(position)+" ");
                    else
                        et.setText((String) parent.getItemAtPosition(position)+" ");

                    et.setSelection(et.getText().length());

                    if(imm != null&& et.getText().toString().length()>0){
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        /*b = (Button) findViewById(R.id.SubmitRemark);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.RemarkText);
                String RemarkString = et.getText().toString();
                Log.d(TAG, "Remark need to be submitted "+ RemarkString);
            }
        });*/
    }

    void setAreaFor(int id) {
        graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);
        graph = (GraphView) findViewById(R.id.aggrGraph);
        graph.setVisibility(View.INVISIBLE);
        graph = (GraphView) findViewById(R.id.acadGraph);
        graph.setVisibility(View.INVISIBLE);
        View rm = (View)findViewById(R.id.RemarkView);
        rm.setVisibility(View.INVISIBLE);
        if(id==R.id.RemarkView)
        {

            sp = (Spinner) findViewById(R.id.spinner);
            sp.setVisibility(View.INVISIBLE);
            TextView tv = (TextView) findViewById(R.id.AcadHead);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.NoGraph);
            tv.setVisibility(View.INVISIBLE);
            graph = (GraphView) findViewById(R.id.attGraph);
            graph.setVisibility(View.INVISIBLE);
            test.setVisibility(View.INVISIBLE);
            ScrollView scrollView = (ScrollView) findViewById(R.id.RemarkScroll);
            scrollView.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.RemHead);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.AttendanceHead);
            tv.setVisibility(View.INVISIBLE);

        }
        if (id == R.id.nav_MyAttendance) {
            sp = (Spinner) findViewById(R.id.spinner);
            sp.setVisibility(View.INVISIBLE);
            TextView tv = (TextView) findViewById(R.id.AcadHead);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.temp);
            tv.setVisibility(View.VISIBLE);
            tv = (TextView) findViewById(R.id.NoGraph);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.AttendanceHead);
            tv.setVisibility(View.VISIBLE);
            ScrollView scrollView = (ScrollView) findViewById(R.id.RemarkScroll);
            scrollView.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_MyPerformance) {
            graph = (GraphView) findViewById(R.id.attGraph);
            graph.setVisibility(View.INVISIBLE);

            sp = (Spinner) findViewById(R.id.spinner);

            test.setVisibility(View.INVISIBLE);

            if (mentor_fields.length != 1) {
                sp.setVisibility(View.VISIBLE);
            }
            ScrollView scrollView = (ScrollView) findViewById(R.id.RemarkScroll);
            scrollView.setVisibility(View.INVISIBLE);
            TextView tv = (TextView) findViewById(R.id.RemHead);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.AttendanceHead);
            tv.setVisibility(View.INVISIBLE);
            tv = (TextView) findViewById(R.id.AcadHead);
            tv.setVisibility(View.VISIBLE);
            tv = (TextView) findViewById(R.id.NoGraph);
            tv.setVisibility(View.VISIBLE);
            getAcadGraph(Academic_Aggregate);

        }
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    void getRemarks() {



        StringBuilder server_data = new StringBuilder();
        String [] Local_Dates,Local_Remarks;
        TextView RemUI = (TextView) findViewById(R.id.Remarks);
        RemUI.setText("");
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


        RemUI.setVisibility(View.VISIBLE);
        TextView RemHead = (TextView) findViewById(R.id.RemHead);
        RemHead.setVisibility(View.VISIBLE);
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
        String[] mentor_fields = arr[8].split("~");
        if (mentor_fields.length == 1)
            return;
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
        double sum = 0;
        for (double d : arr)
            sum += d;
        return sum / sem;
    }

    void getData() {
        studentBackground sbg = new studentBackground(this);

        String username = null;
        String port = getString(R.string.connection_string);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("sid");
            tempResult=extras.getString("res_str");
            if(local_date==null && local_rem==null) {
                local_rem = extras.getString("new_rem");
                local_date = extras.getString("new_date");
                if(shouldRatingbeUpdated)
                {
                    ratingBarResultString=extras.getString("ratingBarResultString");
                    isRatingSubmittable=extras.getString("isRatingSubmittable").equals("1")?true:false;
                    shouldRatingbeUpdated=false;
                }
                Log.d(TAG,"Data about isRating: "+ isRatingSubmittable);
            }
            from = "mentor";
            //The key argument here must match that used in the other activity
        } else {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
            username = sharedPreferences.getString(getString(R.string.user_name), "unknown");
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

        if (tempResult != null) {
            arr = tempResult.split("<br>");


            if (arr.length != 1) {

                sid=arr[0];
                Student_Name = arr[1];
                sem = Integer.parseInt(arr[2]);
                Attendance = Integer.parseInt(arr[3]);
                String[] temp = arr[4].split("~");
                Log.d(TAG, arr[4] + " " + temp.length);
                Academics = new double[sem];
                for (int i = 0; i < sem; i++)
                    Academics[i] = Double.parseDouble(temp[i]);
                Academic_Aggregate = getAggregate(Academics);
                Mentor_Name = arr[5];
                Remarks = arr[6].split("~");
                Dates = arr[7].split("~");
                mentor_fields = arr[8].split("~");
                Log.d(TAG,"mentor fields String: "+mentor_fields[0]);


            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG,"Nvas");
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Reached in Student");
        getData();

        if (tempResult != null) {
            //arr = tempResult.split("<br>");


            if (arr.length != 1) {


                setContentView(R.layout.student_homepage);
                test = (TextView) findViewById(R.id.temp);
                //getData();

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                //The Navigtion drawer opens 800ms second after the page is loaded.

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 800ms
                        drawer.openDrawer(GravityCompat.START, true);
                    }
                }, 800);


                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();
                toolbar.setTitle("My Attendance");
                toolbar.setTitleTextColor(Color.DKGRAY);
                final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

                if (from.equals("mentor")) {
                    navigationView.getMenu().getItem(2).setVisible(true);
                    navigationView.getMenu().getItem(3).setVisible(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setVisible(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(false);
                    navigationView.setCheckedItem(R.id.nav_setRemarks);
                    onNavigationItemSelected(navigationView.getMenu().getItem(2));

                } else {
                    navigationView.getMenu().getItem(2).setVisible(false);
                    navigationView.getMenu().getItem(3).setVisible(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(false);
                    navigationView.setCheckedItem(R.id.nav_MyAttendance);
                    onNavigationItemSelected(navigationView.getMenu().getItem(0));
                }

                navigationView.setNavigationItemSelectedListener(this);

                navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                        navigationView.removeOnLayoutChangeListener(this);

                        TextView textView = (TextView) navigationView.findViewById(R.id.temp2);
                        if (from.equals("login"))
                            textView.setText("Hello " + arr[1]);
                        else
                            textView.setText("This is " + arr[1]);
                        textView.setVisibility(View.VISIBLE);
                    }
                });




            }
        }
    }

    boolean ExitFlag;
    @Override
    public void onBackPressed() {

        if (ExitFlag) {


            if (from.equals("mentor")) {
//                super.onBackPressed();
                Intent i = new Intent(this, MentorHome.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

                return;
            }
        }
            else
            {



            this.ExitFlag = true;
            Toast.makeText(this, "Please click BACK again to leave this page", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    ExitFlag = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Toolbar toolbar;
        getData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (id == R.id.nav_MyPerformance) {
            toolbar.setTitle("My Performance");
            setAreaFor(id);
            adapter = ArrayAdapter.createFromResource(this, R.array.fieldList, android.R.layout.simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Entering the listener" + (int) parent.getItemIdAtPosition(position));
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                    ((TextView) parent.getChildAt(0)).setTextSize(23);
                    if ((int) parent.getItemIdAtPosition(position) > 0) {
                        TextView tv = (TextView) findViewById(R.id.NoGraph);
                        tv.setVisibility(View.INVISIBLE);
                        if(position<=7)
                            getFieldGraph((int) parent.getItemIdAtPosition(position) - 1);
                        else
                            getSemAcadGraph();

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });
            getAggrGraph();


        } else if (id == R.id.nav_MyAttendance) {
            toolbar.setTitle("My Attendance");
            setAreaFor(id);
            getRemarks();
            getAttGraph(Attendance);

        } else if (id == R.id.nav_setRemarks) {
            toolbar.setTitle("Set Remarks");
            setAreaFor(R.id.RemarkView);
            View setRemarks = (View)findViewById(R.id.RemarkView);
            setRemarks.setVisibility(View.VISIBLE);
            nav_SetRemark();

        } else if (id == R.id.Logout) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.Back)
        {
            Intent i = new Intent(this, MentorHome.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    @Override
    public void onClick(View v) {


    }
}
