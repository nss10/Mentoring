package kmit.mentoring;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by sesha sai on 2/5/2017.
 */

public class mentorBackground extends AsyncTask<String,Void,String>{
    String TAG="MentorBG";
    String result;
    Context context;
    mentorBackground(Context ctx){context=ctx;}
    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG,"started mentorHome");
        String port=params[1];
        String mentor_url=port+"mentorHome.php";
        //String mentor_url="http://192.168.43.231/mentorHome.php";
        String user_name=params[0];
        try
        {
            Log.d(TAG,"started mentorHome");
            URL m_url = new URL(mentor_url);
            HttpURLConnection m_httpURLConnection = (HttpURLConnection) m_url.openConnection();
            m_httpURLConnection.setRequestMethod("POST");
            m_httpURLConnection.setDoOutput(true);
            m_httpURLConnection.setDoInput(true);
            OutputStream outputStream =m_httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream=m_httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            result="";
            String line="";
            while((line=bufferedReader.readLine())!=null)
            {
                result+=line;
            }
            bufferedReader.close();
            inputStream.close();
            //Toast.makeText(context,"result is : <"+result+">",Toast.LENGTH_LONG).show();
            Log.d(TAG,"result is : <"+result+">");
        }
        catch(MalformedURLException e)
        {
            Log.d(TAG,"into catch1"+e);
        }
        catch(IOException e)
        {
            Log.d(TAG,"into catch2:"+e);
            result = "NO NET";
        }
        catch(Exception e)
        {
            Log.d(TAG,"into catch3: "+e);
        }
        finally{
            return result;
        }
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
            //Log.d(TAG,"result :<"+result+">");
        }
        Log.d(TAG,"out,result : <"+result+">");
        super.onProgressUpdate(values);
    }
}
