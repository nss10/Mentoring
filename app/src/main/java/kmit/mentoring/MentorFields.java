package kmit.mentoring;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by nvas on 21/6/17.
 */

public class MentorFields implements Parcelable {


    private int[] gd,cs,grooming,bwp,bwf,cca,eca,aggr;
    private String [] tempArr;
    private final String TAG="MentorFields";

    public MentorFields() {
    }

    public MentorFields(String mfString) {

        String [] tempMfArr = mfString.split("~");

        tempArr = tempMfArr[0].split(",");
        gd = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            gd[i] = Integer.parseInt(tempArr[i]);


        tempArr = tempMfArr[1].split(",");
        cs = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            cs[i] = Integer.parseInt(tempArr[i]);


        tempArr = tempMfArr[2].split(",");
        grooming = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            grooming[i] = Integer.parseInt(tempArr[i]);



        tempArr = tempMfArr[3].split(",");
        bwp = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            bwp[i] = Integer.parseInt(tempArr[i]);



        tempArr = tempMfArr[4].split(",");
        bwf = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            bwf[i] = Integer.parseInt(tempArr[i]);



        tempArr = tempMfArr[5].split(",");
        cca = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            cca[i] = Integer.parseInt(tempArr[i]);



        tempArr = tempMfArr[6].split(",");
        eca = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            eca[i] = Integer.parseInt(tempArr[i]);


        //Log.d(TAG,tempMfArr[6]);
        tempArr = tempMfArr[7].split(",");
        aggr = new int[tempArr.length];
        for(int i = 0;i<tempArr.length;i++)
            aggr[i] = Integer.parseInt(tempArr[i]);



    }



    public int[] getGd() {
        return gd;
    }

    public int[] getCs() {
        return cs;
    }

    public int[] getGrooming() {
        return grooming;
    }

    public int[] getBwp() {
        return bwp;
    }

    public int[] getBwf() {
        return bwf;
    }

    public int[] getCca() {
        return cca;
    }

    public int[] getEca() {
        return eca;
    }

    public int[] getAggr() {
        return aggr;
    }

    public int[] getFieldArray(int fieldVal)
    {
        switch(fieldVal)
        {
            case 0: return getGd();
            case 1 : return getCs();
            case 2: return getGrooming();
            case 3 : return getBwp();
            case 4: return getBwf();
            case 5 : return getCca();
            case 6: return getEca();
            default: return null;

        }
    }
    @Override
    public String toString() {
        return "MentorFields{" +
                "gd=" + Arrays.toString(gd) +
                ", cs=" + Arrays.toString(cs) +
                ", grooming=" + Arrays.toString(grooming) +
                ", bwp=" + Arrays.toString(bwp) +
                ", bwf=" + Arrays.toString(bwf) +
                ", cca=" + Arrays.toString(cca) +
                ", eca=" + Arrays.toString(eca) +
                ", aggr=" + Arrays.toString(aggr) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.gd);
        dest.writeIntArray(this.cs);
        dest.writeIntArray(this.grooming);
        dest.writeIntArray(this.bwp);
        dest.writeIntArray(this.bwf);
        dest.writeIntArray(this.cca);
        dest.writeIntArray(this.eca);
        dest.writeIntArray(this.aggr);
        dest.writeStringArray(this.tempArr);
    }

    protected MentorFields(Parcel in) {
        this.gd = in.createIntArray();
        this.cs = in.createIntArray();
        this.grooming = in.createIntArray();
        this.bwp = in.createIntArray();
        this.bwf = in.createIntArray();
        this.cca = in.createIntArray();
        this.eca = in.createIntArray();
        this.aggr = in.createIntArray();
        this.tempArr = in.createStringArray();
    }

    public static final Parcelable.Creator<MentorFields> CREATOR = new Parcelable.Creator<MentorFields>() {
        @Override
        public MentorFields createFromParcel(Parcel source) {
            return new MentorFields(source);
        }

        @Override
        public MentorFields[] newArray(int size) {
            return new MentorFields[size];
        }
    };
}
