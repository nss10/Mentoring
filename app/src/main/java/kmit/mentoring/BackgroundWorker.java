package kmit.mentoring;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by sesha sai on 1/22/2017.
 */

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context context;
    String TAG="BackgroundWorker";
    String user_name,password,result;
    BackgroundWorker(Context ctx){
        context=ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type=params[0];
        String port=params[3];
        String login_url=port+"login.php";
        //String login_url="http://192.168.43.231/login.php";
        if(type.equals("Login"))
        {
            try {
                    user_name = params[1];
                    password = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                    bufferedWriter.write(post_data);
                    Log.d(TAG,post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    result="";
                    String line="";
                    while((line=bufferedReader.readLine())!=null)
                    {
                        result+=line;
                    }
                Log.d(TAG,result+" test");
                bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                }
            catch(MalformedURLException e)
            {
                Log.d(TAG,"into catch1"+e);
            }
            catch(IOException e)
            {
                Log.d(TAG,"into catch2:"+e);
                result="NO NET";
            }
            finally
            {
                Log.d(TAG,"finally: "+result);
                return result;
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {
        //Log.d(TAG,result);
        /*alertDialog=new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage(result);
        alertDialog.show();*/
        if(result.matches("0"))
        {
            alertDialog=new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Login Status");
            alertDialog.setMessage("Incorrect credentials !");
            alertDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        while(result==null || result=="")
        {
           // Log.d(TAG,"here brother ! your result :< "+result+">");
        }
        Log.d(TAG,"out , result : <"+result+">");
        super.onProgressUpdate(values);
    }
}
