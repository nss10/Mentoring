package kmit.mentoring;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by nvas on 21/6/17.
 */

public class Remark implements Parcelable {

    String remarkString;
    String date;

    public Remark() {
    }

    public Remark(String remarkString,String dateString) {

        this.remarkString = remarkString;


        this.date = dateString;

    }


    public String getRemarkString() {
        return remarkString;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Remark{" +
                "remarkString='" + remarkString + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.remarkString);
        dest.writeString(this.date);
    }

    protected Remark(Parcel in) {
        this.remarkString = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<Remark> CREATOR = new Parcelable.Creator<Remark>() {
        @Override
        public Remark createFromParcel(Parcel source) {
            return new Remark(source);
        }

        @Override
        public Remark[] newArray(int size) {
            return new Remark[size];
        }
    };
}
