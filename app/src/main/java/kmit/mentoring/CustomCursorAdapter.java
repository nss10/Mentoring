package kmit.mentoring;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import kmit.mentoring.localStruct.mentorTable;

public class CustomCursorAdapter extends CursorAdapter {
    static int id_val;
    Context context;
    int studentCount;
    String TAG ="CustomCursorAdapter";

    static {
        id_val = 0;
    }

    public CustomCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.ht_no_and_name, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        this.studentCount = cursor.getCount();
        TextView ht_no = (TextView) view.findViewById(R.id.ht_no);
        String roll = cursor.getString(cursor.getColumnIndexOrThrow(mentorTable.column1));
        TextView name = (TextView) view.findViewById(R.id.name);
        String std_name = cursor.getString(cursor.getColumnIndexOrThrow(mentorTable.column2));
        ImageView iv = (ImageView) view.findViewById(R.id.item_is_submittable);
        //Log.d(TAG,"Star status" + cursor.getString(cursor.getColumnIndex(mentorTable.column7)));
        if (cursor.getString(cursor.getColumnIndex(mentorTable.column7)).equals("1")) {
            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_black_24dp));
        } else {
            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
        }
        ht_no.setText(roll +"");
        name.setText(std_name);
        ImageView flag = (ImageView) view.findViewById(R.id.item_student_flagged);
        if (cursor.getString(cursor.getColumnIndex(mentorTable.column8)).equals("1")) {
            flag.setVisibility(View.VISIBLE);
        } else {
            flag.setVisibility(View.INVISIBLE);
        }
    }
}
