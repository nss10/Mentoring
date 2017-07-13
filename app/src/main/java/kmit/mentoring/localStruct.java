package kmit.mentoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class localStruct {
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE mentorStudents(_id INTEGER PRIMARY KEY ,ht_no varchar(8) ,name varchar(50) ,resultString varchar(2000) ,new_remarks varchar(500) ,new_dates varchar(200),mentorFields varchar(15),isRatingUpdatable varchar(1),isStudentFlagged varchar(1),studentImage varchar);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS mentorStudents";

    public class localDB extends SQLiteOpenHelper {
        public localDB(Context context) {
            super(context, "mentoring", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(localStruct.SQL_CREATE_ENTRIES);
            ContentValues values = new ContentValues();
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.delete(mentorTable.table_name, null, null);
            onCreate(db);
        }

        public void storeImagesIntoMap(SQLiteDatabase db) {

        }

        public void onLogout(SQLiteDatabase db) {
            db.execSQL(localStruct.SQL_DELETE_ENTRIES);
        }


    }

    public static class mentorTable implements BaseColumns {
        public static final String column1 = "ht_no";
        public static final String column2 = "name";
        public static final String column3 = "resultString";
        public static final String column4 = "new_remarks";
        public static final String column5 = "new_dates";
        public static final String column6 = "mentorFields";
        public static final String column7 = "isRatingUpdatable";
        public static final String column8 = "isStudentFlagged";
        public static final String column9 = "studentImage";
        public static final String table_name = "mentorStudents";
        String TAG;

        public mentorTable() {
            this.TAG = "localStruct";
        }



    }
}