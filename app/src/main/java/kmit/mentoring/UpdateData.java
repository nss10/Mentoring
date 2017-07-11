package kmit.mentoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import kmit.mentoring.localStruct.localDB;
import kmit.mentoring.localStruct.mentorTable;

public class UpdateData {
    String TAG;
    Context context;
    SQLiteDatabase db;
    localDB db_obj;
    String port;
    int studentCount;
    int studentSem;
    String username;
    Cursor imageCursor;

    UpdateData(Context context, String port, String username, int studentSem) {
        this.TAG = "UpdateData";
        this.context = context;
        this.port = port;
        this.username = username;
        this.studentSem = studentSem;
        Log.d(TAG,"studentSeminConstructor " + studentSem);
    }

    boolean UpdateToServer() {

        this.db_obj = new localStruct().new localDB(this.context);
        this.db = this.db_obj.getReadableDatabase();
        String sid = "";
        String remarks = "";
        String date = "";
        String rating = "";
        String isRatingSubmittable = "";
        String isStudentFlagged = "";
        Cursor c = db.rawQuery("SELECT * from " + mentorTable.table_name,null);
        imageCursor = c;
        c.moveToFirst();
        Log.d(TAG,c.getCount()+"-column count");
        while(!c.isAfterLast())
        {
            sid = sid + c.getString(c.getColumnIndex(mentorTable.column1)) + "SPLITTER";
            remarks = remarks + c.getString(c.getColumnIndex(mentorTable.column4)) + "SPLITTER";
            date = date + c.getString(c.getColumnIndex(mentorTable.column5)) + "SPLITTER";
            rating = rating + c.getString(c.getColumnIndex(mentorTable.column6)) + "SPLITTER";
            isRatingSubmittable = isRatingSubmittable + c.getString(c.getColumnIndex(mentorTable.column7)) + "SPLITTER";
            isStudentFlagged = isStudentFlagged + c.getString(c.getColumnIndex(mentorTable.column8)) + "SPLITTER";
            imageWarehouse.imageMap.put(c.getString(c.getColumnIndex(mentorTable.column1)),c.getString(c.getColumnIndex(mentorTable.column9)));
            c.moveToNext();
        }
        Log.d(this.TAG, "here we go isRatingSubmittable - "+ sid +"-" + rating + "sem = " + studentSem);
        mbgToInsertData kmit_mentoring_mbgToInsertData = new mbgToInsertData(this.context);
        Log.d(this.TAG, remarks);
        Log.d(this.TAG,"printing test");

        kmit_mentoring_mbgToInsertData.execute(new String[]{this.port, this.username, sid, remarks, date, rating, isRatingSubmittable, this.studentSem +"", isStudentFlagged});
//        kmit_mentoring_mbgToInsertData.onProgressUpdate(new Void[0]);

        Log.d(this.TAG,"printing Result - "+  kmit_mentoring_mbgToInsertData.result);
        /*if (kmit_mentoring_mbgToInsertData.result.matches("NO NET")) {
            Toast.makeText(this.context, "NO NET", Toast.LENGTH_LONG).show();
            return false;
        }*/
        return true;
    }

    String getCurMF(int sem, String[] mf) {
        String str = "";
        if (mf[0].length() < (sem * 2) - 1 || mf.length == 1) {
            Log.d(TAG,"In the if");
            return "0~0~0~0~0~0~0~";
        }
        Log.d(TAG,"In the else");

        for (int i = 0; i < 7; i++) {
            str = str + mf[i].charAt((sem - 1) * 2) + "~";
        }
        return str;
    }



    void UpdateLocal2(Student[] studArr)
    {
        Log.d(TAG,"Time begins here");
        this.db_obj = new localStruct().new localDB(this.context);
        this.db = this.db_obj.getWritableDatabase();
        this.db.delete(mentorTable.table_name, null, null);
        ContentValues values = new ContentValues();

        for(Student student : studArr)
        {
            Log.d(this.TAG, student.toString());
            values.put(mentorTable.column1, student.getSid());
            values.put(mentorTable.column2, student.getName());
            this.studentSem = student.getSem();
            values.put(mentorTable.column3, student.getInputString());
            List<int[]> curMFlist = Arrays.asList(student.getCurMF());
            values.put(mentorTable.column6, student.getCurMFString());
            if (curMFlist.contains(0)) {
                values.put(mentorTable.column7, "0");
            } else {
                values.put(mentorTable.column7, "1");
            }
            values.put(mentorTable.column8, student.isStudentFlagged()?"1":"0");
            values.put(mentorTable.column9, imageWarehouse.imageMap.get(student.getSid()));   //gettingFromTheMap
            this.db.insert(mentorTable.table_name, null, values);
            Log.d(TAG,"col1 " + student.getSid());
            Log.d(TAG,"col2 " + student.getName());
            Log.d(TAG,"col3 " + student.getInputString());
            Log.d(TAG,"col6 " + student.getCurMFString());
            Log.d(TAG,"col7 ");
            Log.d(TAG,curMFlist.contains("0")?"0":"1");
            Log.d(TAG,"col8 " + (student.isStudentFlagged()?"1":"0"));
            Log.d(TAG,"****************");
        }
        Log.d(TAG,"Time ends here");
    }
    void UpdateLocal(String updateString) {

        this.db_obj = new localStruct().new localDB(this.context);
        this.db = this.db_obj.getWritableDatabase();
        this.db.delete(mentorTable.table_name, null, null);
        ContentValues values = new ContentValues();

        String[] studentStrings = updateString.split("STUDENT SEPERATOR SPLIT");
        this.studentCount = studentStrings.length;
        for (int i = 0; i < this.studentCount; i++) {
//            Log.d(this.TAG, "studentString[" + i + "]" + studentStrings[i]);
            String[] listData = studentStrings[i].split("<br>");
            values.put(mentorTable.column1, listData[0]);


            values.put(mentorTable.column2, listData[1].split("&&")[0]);
            this.studentSem = Integer.parseInt(listData[2].split("&&")[1]);
            values.put(mentorTable.column3, studentStrings[i]);
            String temp = getCurMF(this.studentSem, listData[9].split("~"));
//            Log.d(this.TAG, "isDataSubmittable = "+temp+ " " +!temp.contains("0"));
//            Log.d(this.TAG, "Sem =" + this.studentSem +"-"+ listData[9]);
            values.put(mentorTable.column6, temp);
            if (temp.contains("0")) {
                values.put(mentorTable.column7, "0");
            } else {
                values.put(mentorTable.column7, "1");
            }
            values.put(mentorTable.column8, listData[5]);
//            Log.d(this.TAG, "inserting " + listData[0] + "," + listData[1] + " , " + studentStrings[i] + "Current sem rating:" + getCurMF(this.studentSem, listData[9].split("~")) + " isStudentFlagged = " + listData[5]);
            this.db.insert(mentorTable.table_name, null, values);
            Log.d(TAG,"col1" + listData[0]);
            Log.d(TAG,"col2" + listData[1].split("&&")[0]);
            Log.d(TAG,"col3" + studentStrings[i]);
            Log.d(TAG,"col6" + temp);
            Log.d(TAG,"col7");
            Log.d(TAG,temp.contains("0")?"0":"1");
            Log.d(TAG,"col8" + listData[5]);

            Log.d(TAG,"****************");
        }
    }
}
