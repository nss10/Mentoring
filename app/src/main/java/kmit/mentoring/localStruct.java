package kmit.mentoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static kmit.mentoring.localStruct.mentorTable.column1;
import static kmit.mentoring.localStruct.mentorTable.column6;
import static kmit.mentoring.localStruct.mentorTable.column7;
import static kmit.mentoring.localStruct.mentorTable.table_name;

/**
 * Created by sesha sai on 2/15/2017.
 */

public final class localStruct{
    //private localStruct(){}
    public static class mentorTable implements BaseColumns
    {
        public static final String table_name="mentorStudents";
        public static final String column1="ht_no";
        public static final String column2="name";
        public static final String column3="resultString";
        public static final String column4="new_remarks";
        public static final String column5="new_dates";
        public static final String column6="mentorFields";
        public static final String column7="isRatingUpdatable";
        String TAG="localStruct";

    }

    private static final String SQL_CREATE_ENTRIES="CREATE TABLE "+ table_name+"("+mentorTable._ID+" INTEGER PRIMARY KEY ,"+mentorTable.column1
            +" varchar(8) ,"+mentorTable.column2+" varchar(50) ,"+mentorTable.column3 +" varchar(2000) ,"+mentorTable.column4+" varchar(500) ,"+mentorTable.column5+" varchar(200),"+mentorTable.column6+" varchar(15),"+mentorTable.column7+" varchar(1));";
    private static final String SQL_DELETE_ENTRIES="DROP TABLE IF EXISTS "+ table_name;

    public class localDB extends SQLiteOpenHelper
    {

        public localDB(Context context) {
            super(context,"mentoring", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            ContentValues values = new ContentValues();



        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.delete(table_name , null, null);
            onCreate(db);
        }

        public void onLogout(SQLiteDatabase db)
        {
            db.execSQL(SQL_DELETE_ENTRIES);
        }
        /*public void UpdateColumn(SQLiteDatabase db,String roll_no,String new_ColumnContent)
        {
            String strSQL = "UPDATE "+ table_name + "SET Column1 = "+new_ColumnContent +" WHERE columnId = "+ roll_no;

            db.execSQL(strSQL);
        }*/


    }


}
