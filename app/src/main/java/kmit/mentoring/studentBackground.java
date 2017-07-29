package kmit.mentoring;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sesha sai on 2/7/2017.
 */

public class studentBackground extends AsyncTask<String,Void,String> {
    String TAG="StudentBG";
    String result;
    Context context;
    studentBackground(Context ctx){context=ctx;}
    @Override
    protected String doInBackground(String... params) {
        String port=params[1];
        String student_url=port+"studentHome.php";
        //String student_url="http://192.168.43.231/studentHome.php";
        String user_name=params[0];
        try
        {
            //Log.d(TAG,"started studentHomeBG");
            URL s_url = new URL(student_url);
            HttpURLConnection s_httpURLConnection = (HttpURLConnection) s_url.openConnection();
            s_httpURLConnection.setRequestMethod("POST");
            s_httpURLConnection.setDoOutput(true);
            s_httpURLConnection.setDoInput(true);
            OutputStream outputStream =s_httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            //Log.d(TAG,post_data);
            InputStream inputStream=s_httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            result="";
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {
                result+=line;
                //Log.d(TAG,"result is : <"+result+">");
            }
            bufferedReader.close();
            inputStream.close();
            //Toast.makeText(context,"result is : <"+result+">",Toast.LENGTH_LONG).show();
            return result;
        }
        catch(MalformedURLException e)
        {
            //Log.d(TAG,"into catch1"+e);
        }
        catch(IOException e)
        {
            //Log.d(TAG,"into catch2:"+e);
            result = "NO Net";
            return result;
        }
        catch(Exception e)
        {
            //Log.d(TAG,"into catch3: "+e);
        }
        finally
        {
            //Log.d(TAG,"Finally: "+result);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        while(result==null || result=="")
        {
           // Log.d(TAG,"result :<"+result+">");
        }
        //  Log.d(TAG,"out,result : <"+result+">");
        super.onProgressUpdate(values);
    }
}
