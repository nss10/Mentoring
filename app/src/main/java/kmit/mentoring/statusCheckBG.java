package kmit.mentoring;

import android.app.AlertDialog;
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

public class statusCheckBG extends AsyncTask<String, Void, String> {
    String TAG;
    AlertDialog alertDialog;
    Context context;
    String result;
    String user_name;

    statusCheckBG(Context ctx) {
        this.TAG = "statusCheckBG";
        this.context = ctx;
    }

    protected String doInBackground(String... params) {
        String login_url = params[0] + "statusCheck.php";
        try {
            this.user_name = params[1];
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(login_url).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(this.user_name, "UTF-8");
            bufferedWriter.write(post_data);
            //Log.d(this.TAG, post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            this.result = BuildConfig.FLAVOR;
            String str = BuildConfig.FLAVOR;
            while (true) {
                str = bufferedReader.readLine();
                if (str != null) {
                    this.result += str;
                } else {
                    //Log.d(this.TAG, this.result + " test");
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //Log.d(this.TAG, "finally: " + this.result);
                    return this.result;
                }
            }
        } catch (MalformedURLException e) {
            //Log.d(this.TAG, "into MalformedURLException" + e);

        } catch (IOException e2) {
            //Log.d(this.TAG, "into IOException:" + e2);
            this.result = "NO NET";
        } catch (Exception e3) {
            //Log.d(this.TAG, "into catchException:" + e3);
        }
        finally {
            //Log.d(this.TAG, "finally: " + this.result);
            return this.result;
        }
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(String result) {
    }

    protected void onProgressUpdate(Void... values) {
        while (true) {
            if (this.result != null && this.result != BuildConfig.FLAVOR) {
                //Log.d(this.TAG, "out , result : <" + this.result + ">");
                super.onProgressUpdate(values);
                return;
            }
        }
    }
}