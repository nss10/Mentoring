package kmit.mentoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Toast;
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

    UpdateData(Context context, String port, String username, int studentSem) {
        this.TAG = "UpdateData";
        this.context = context;
        this.port = port;
        this.username = username;
        this.studentSem = studentSem;
    }

    boolean UpdateToServer() {

        this.db_obj = new localStruct().new localDB(this.context);
        this.db = this.db_obj.getReadableDatabase();
        String[] projection = new String[]{"_id", mentorTable.column1, mentorTable.column2, mentorTable.column3, mentorTable.column4, mentorTable.column5, mentorTable.column6, mentorTable.column7, mentorTable.column8};
        int[] views = new int[]{R.id.ht_no, R.id.name};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this.context, R.layout.ht_no_and_name, this.db.query(mentorTable.table_name, projection, null, null, null, null, null), projection, views, 1);
        String sid = "";
        String remarks = "";
        String date = "";
        String rating = "";
        String isRatingSubmittable = "";
        String isStudentFlagged = "";
        for (int i = 0; i < simpleCursorAdapter.getCount(); i++) {
            Cursor c = (Cursor) simpleCursorAdapter.getItem(i);
            sid = sid + c.getString(c.getColumnIndex(mentorTable.column1)) + "SPLITTER";
            remarks = remarks + c.getString(c.getColumnIndex(mentorTable.column4)) + "SPLITTER";
            date = date + c.getString(c.getColumnIndex(mentorTable.column5)) + "SPLITTER";
            rating = rating + c.getString(c.getColumnIndex(mentorTable.column6)) + "SPLITTER";
            isRatingSubmittable = isRatingSubmittable + c.getString(c.getColumnIndex(mentorTable.column7)) + "SPLITTER";
            isStudentFlagged = isStudentFlagged + c.getString(c.getColumnIndex(mentorTable.column8)) + "SPLITTER";
            Log.d(this.TAG, "here we go " + isStudentFlagged + " " + c.getString(c.getColumnIndex(mentorTable.column8)));
        }
        mbgToInsertData kmit_mentoring_mbgToInsertData = new mbgToInsertData(this.context);
        Log.d(this.TAG, remarks);
        kmit_mentoring_mbgToInsertData.execute(new String[]{this.port, this.username, sid, remarks, date, rating, isRatingSubmittable, this.studentSem + BuildConfig.FLAVOR, isStudentFlagged});
        kmit_mentoring_mbgToInsertData.onProgressUpdate(new Void[0]);
        Log.d(this.TAG, kmit_mentoring_mbgToInsertData.result);
        if (kmit_mentoring_mbgToInsertData.result.matches("NO NET")) {
            Toast.makeText(this.context, "NO NET", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(this.context, "Data Updated and logged out", 1).show();
        return true;
    }

    String getCurMF(int sem, String[] mf) {
        String str = "";
        if (mf[0].length() < (sem * 2) - 1 || mf.length == 1) {
            return "0~0~0~0~0~0~0~";
        }
        for (int i = 0; i < 7; i++) {
            str = str + mf[i].charAt((sem - 1) * 2) + "~";
        }
        return str;
    }

    void UpdateLocal(String updateString) {

        this.db_obj = new localStruct().new localDB(this.context);
        this.db = this.db_obj.getWritableDatabase();
        this.db.delete(mentorTable.table_name, null, null);
        ContentValues values = new ContentValues();
        String[] studentStrings = updateString.split("STUDENT SEPERATOR SPLIT");
        this.studentCount = studentStrings.length;
        for (int i = 0; i < this.studentCount; i++) {
            Log.d(this.TAG, "studentString[" + i + "]" + studentStrings[i]);
            String[] listData = studentStrings[i].split("<br>");
            values.put(mentorTable.column1, listData[0]);
            values.put(mentorTable.column2, listData[1].split("&&")[0]);
            this.studentSem = Integer.parseInt(listData[2].split("&&")[1]);
            values.put(mentorTable.column3, studentStrings[i]);
            Log.d(this.TAG, "isDataSubmittable = ");
            String temp = getCurMF(this.studentSem, listData[9].split("~"));
            Log.d(this.TAG, "Sem =" + this.studentSem);
            values.put(mentorTable.column6, temp);
            if (temp.contains("0")) {
                values.put(mentorTable.column7, "0");
            } else {
                values.put(mentorTable.column7, "1");
            }
            values.put(mentorTable.column8, listData[5]);
            Log.d(this.TAG, "inserting " + listData[0] + "," + listData[1] + " , " + studentStrings[i] + "Current sem rating:" + getCurMF(this.studentSem, listData[9].split("~")) + " isStudentFlagged = " + listData[5]);
            this.db.insert(mentorTable.table_name, null, values);
        }
    }
}
