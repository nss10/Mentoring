package kmit.mentoring;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nvas on 21/6/17.
 */

public class Student implements Parcelable {

    private String sid;
    private String name;
    private String parentName;
    private String mentorName;
    private int year;
    private String department;
    private char section;
    private int sem;
    private String eMail;
    private String phone;
    private String studentImage;
    private double attendance;
    private double acadArr[];
    private double acadAggr;
    private boolean isStudentFlagged;
    private String OTP;
    private String ratingBarResultString;
    private boolean isRatingSubmittable;
    private ArrayList<Remark> remarkList,new_remarkList;
    private MentorFields mentorFields;

    private String inputString;

    private int [] curMF;
    //Image??;

    //Constructors
    public Student() {

    }
    public Student(String studentString) {
        inputString = studentString;


        String listData[] = studentString.split("<br>");

        sid = listData[0];

        String nameParentOtpPhnoQuad[] = listData[1].split("&&");
        name = nameParentOtpPhnoQuad[0];
        parentName = nameParentOtpPhnoQuad[1];
        String OTP_arr[] =nameParentOtpPhnoQuad[2].split(",");
        OTP = OTP_arr[OTP_arr.length-1];
        phone = nameParentOtpPhnoQuad[3];

        String classIdSemPair[] = listData[2].split("&&");
        year = Integer.parseInt(String.valueOf(classIdSemPair[0].charAt(0)));
        String tempDept = classIdSemPair[0].substring(1,3).toUpperCase();
        if(tempDept.equals("IT"))
            department = "IT";
        else
            department=tempDept+"E";
        section = Character.toUpperCase(classIdSemPair[0].charAt(3));
        sem = Integer.parseInt(classIdSemPair[1]);
        attendance = Double.parseDouble(listData[3]);


        String tempAcad_arr [] = listData[4].split("~");
        acadArr = new double[tempAcad_arr.length];
        for(int i=0;i<tempAcad_arr.length;i++)
            acadArr[i] = Double.parseDouble(tempAcad_arr[i]);

        isStudentFlagged = (listData[5].equals("1"));

        mentorName = listData[6];
        if(listData[7].trim().equals("No"))
            remarkList=null;
        else
        {
            String tempRemarkArr[] = listData[7].split("~");
            String tempDateArr[] = listData[8].split("~");
            remarkList = new ArrayList<>();
            for(int i=0;i<tempRemarkArr.length;i++)
                remarkList.add(new Remark(tempRemarkArr[i],tempDateArr[i]));
        }

        Log.d("Rajni",sid + listData[9]);
        if(!listData[9].equalsIgnoreCase("Not graded"))
            mentorFields = new  MentorFields(listData[9]);



    }


    //getters

    double getAcadAggr() {
        double sum = 0.0d;
        int count = 0;
        for (double d : this.acadArr) {
            if (d > 0.0d) {
                count++;
            }
            sum += d;
        }
        if (count == 0 || sum == 0.0d) {
            return 0.0d;
        }
        acadAggr=  sum / ((double) count);
        return acadAggr;
    }

    public String getInputString() {
        return inputString;
    }

    public int[] getCurMF()    {
        curMF = new int[7];
        try
        {

            curMF[0] = mentorFields.getGd()[sem-1];
            curMF[1] = mentorFields.getCs()[sem-1];
            curMF[2] = mentorFields.getGrooming()[sem-1];
            curMF[3] = mentorFields.getBwp()[sem-1];
            curMF[4] = mentorFields.getBwf()[sem-1];
            curMF[5] = mentorFields.getCca()[sem-1];
            curMF[6] = mentorFields.getEca()[sem-1];
        }
        catch (ArrayIndexOutOfBoundsException ae)
        {

        }
        finally {
            return curMF;
        }

    }

    public String getCurMFString()    {
        String str="";
        for(int i:curMF)
            str=str+i+"~";

        return str;
    }

    public String getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }
    public String getParentName() {
        return parentName;
    }

    public String getMentorName() {
        return mentorName;
    }

    public int getYear() {
        return year;
    }

    public String getDepartment() {
        return department;
    }

    public char getSection() {
        return section;
    }


    public int getSem() {
        return sem;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPhone() {
        return phone;
    }

    public double getAttendance() {
        return attendance;
    }

    public double[] getAcadArr() {
        return acadArr;
    }

    public boolean isStudentFlagged() {
        return isStudentFlagged;
    }

    public String getOTP() {
        return OTP;
    }

    public String getStudentImage() {
        return studentImage;
    }

    public ArrayList<Remark> getRemarkList() {
        return remarkList;
    }

    public ArrayList<Remark> getNew_remarkList() {
        return new_remarkList;
    }

    public Remark getCumulativeLocalRemark()    {
       if(new_remarkList==null)
            return null;

        String rem_str="";
        String date_str="";
        boolean isFirst=true;
        for(Remark rem:new_remarkList)
        {
            if(isFirst)
            {
                rem_str = rem.getRemarkString();
                date_str =rem.getDate();
            }
            else
            {
                rem_str = rem_str+rem.getRemarkString() + "~" ;
                date_str = date_str + rem.getDate() + "~" ;
            }
            isFirst=false;

        }

        return new Remark(rem_str,date_str);
    }

    public MentorFields getMentorFields() {
        return mentorFields;
    }


    public String getRatingBarResultString() {
        return ratingBarResultString;
    }

    public boolean isRatingSubmittable() {
        return isRatingSubmittable;
    }



  //Setters
    public void setLocalRemarks(String rem_String, String date_string) {
        if(rem_String==null && date_string==null)
        {
            return;
        }
        if(new_remarkList==null) {
            new_remarkList = new ArrayList<>();
        }
        String tempRemarkArr[] = rem_String.split("~");
        String tempDateArr[] = date_string.split("~");
        for(int i=0;i<tempRemarkArr.length;i++)
            new_remarkList.add(new Remark(tempRemarkArr[i],tempDateArr[i]));
    }

    public void setRatingBarResultString(String ratingBarResultString) {
        this.ratingBarResultString = ratingBarResultString;
    }

    public void setStudentFlagged(boolean studentFlagged) {
        isStudentFlagged = studentFlagged;
    }

    public void setRatingSubmittable(boolean ratingSubmittable) {
        isRatingSubmittable = ratingSubmittable;
    }

    public void setStudentImage(String studentImage) {
        this.studentImage = studentImage;
    }

    @Override
    public String toString() {
        return "Student{" +
                "sid='" + sid + '\'' +
                ", name='" + name + '\'' +
                ", parentName='" + parentName + '\'' +
                ", mentorName='" + mentorName + '\'' +
                ", year=" + year +
                ", department='" + department + '\'' +
                ", section=" + section +
                ", sem=" + sem +
                ", eMail='" + eMail + '\'' +
                ", phone='" + phone + '\'' +
                ", attendance=" + attendance +
                ", acadArr=" + Arrays.toString(acadArr) +
                ", isStudentFlagged=" + isStudentFlagged +
                ", OTP='" + OTP + '\'' +
                ", remarkList=" + remarkList +
                ", new_remarkList=" + new_remarkList +
                ", mentorFields=" + mentorFields +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sid);
        dest.writeString(this.name);
        dest.writeString(this.parentName);
        dest.writeString(this.mentorName);
        dest.writeInt(this.year);
        dest.writeString(this.department);
        dest.writeInt(this.section);
        dest.writeInt(this.sem);
        dest.writeString(this.eMail);
        dest.writeString(this.phone);
        dest.writeString(this.ratingBarResultString);
        dest.writeDouble(this.attendance);
        dest.writeDoubleArray(this.acadArr);
        dest.writeByte(this.isStudentFlagged ? (byte) 1 : (byte) 0);
        dest.writeString(this.OTP);
        dest.writeList(this.remarkList);
        dest.writeList(this.new_remarkList);
        dest.writeParcelable(this.mentorFields, flags);
        dest.writeString(this.inputString);
        dest.writeString(this.studentImage);
        dest.writeIntArray(this.curMF);
    }

    protected Student(Parcel in) {
        this.sid = in.readString();
        this.name = in.readString();
        this.parentName = in.readString();
        this.mentorName = in.readString();
        this.year = in.readInt();
        this.department = in.readString();
        this.section = (char) in.readInt();
        this.sem = in.readInt();
        this.eMail = in.readString();
        this.phone = in.readString();
        this.ratingBarResultString = in.readString();
        this.attendance = in.readDouble();
        this.acadArr = in.createDoubleArray();
        this.isStudentFlagged = in.readByte() != 0;
        this.OTP = in.readString();
        this.remarkList = new ArrayList<Remark>();
        in.readList(this.remarkList, Remark.class.getClassLoader());
        this.new_remarkList = new ArrayList<Remark>();
        in.readList(this.new_remarkList, Remark.class.getClassLoader());
        this.mentorFields = in.readParcelable(MentorFields.class.getClassLoader());
        this.inputString = in.readString();
        this.studentImage = in.readString();
        this.curMF = in.createIntArray();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
