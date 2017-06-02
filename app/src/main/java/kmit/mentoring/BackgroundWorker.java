package kmit.mentoring;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class BackgroundWorker extends AsyncTask<String, Void, String> {
    String IMEI;
    String TAG;
    AlertDialog alertDialog;
    Context context;
    String password;
    ProgressDialog pd;
    String result;
    String user_name;

    BackgroundWorker(Context ctx) {
        this.TAG = "BackgroundWorker";
        this.context = ctx;
    }

    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = params[3] + "login.php";
        if (!type.equals("Login")) {
            return null;
        }
        try {
            this.user_name = params[1];
            this.password = params[2];
            this.IMEI = params[4];
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(login_url).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(this.user_name, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(this.password, "UTF-8") + "&" + URLEncoder.encode("IMEI", "UTF-8") + "=" + URLEncoder.encode(this.IMEI, "UTF-8");
            bufferedWriter.write(post_data);
            Log.d(this.TAG, post_data);
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
                    Log.d(this.TAG, this.result + " test");
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    Log.d(this.TAG, "finally0: " + this.result);
                    return this.result;
                }
            }
        } catch (MalformedURLException e) {
            Log.d(this.TAG, "into catch1" + e);
            Log.d(this.TAG, "finally1: " + this.result);
            return this.result;
        } catch (IOException e2) {
            Log.d(this.TAG, "into catch2:" + e2);
            this.result = "NO NET";
            Log.d(this.TAG, "finally2: " + this.result);
            return this.result;
        } catch (Throwable th) {
            Log.d(this.TAG, "finally3: " + this.result + th);
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
                Log.d(this.TAG, "out , result : <" + this.result + ">");
                super.onProgressUpdate(values);
                return;
            }
        }
    }
}