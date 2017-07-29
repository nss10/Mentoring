package kmit.mentoring;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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

public class validateOTPBG extends AsyncTask<String, Void, String> {
    String TAG;
    AlertDialog alertDialog;
    Context context;
    ProgressDialog loading;
    String otp;
    String result;
    String user_name;

    validateOTPBG(Context ctx) {
        this.TAG = "getOTPBG";
        this.context = ctx;
    }

    protected String doInBackground(String... params) {
        String port = params[0];
        this.user_name = params[1];
        this.otp = params[2];
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(port + "validateOTP.php").openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(this.user_name, "UTF-8") + "&" + URLEncoder.encode("OTP", "UTF-8") + "=" + URLEncoder.encode(this.otp, "UTF-8");
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
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //Log.d(this.TAG, "finally: " + this.result);
                    return this.result;
                }
            }
        } catch (MalformedURLException e) {
            //Log.d(this.TAG, "into catch1" + e);
            //Log.d(this.TAG, "finally: " + this.result);
            return this.result;
        } catch (IOException e2) {
            //Log.d(this.TAG, "into catch2:" + e2);
            this.result = "NO NET";
            //Log.d(this.TAG, "finally: " + this.result);
            return this.result;
        } catch (Throwable th) {
            //Log.d(this.TAG, "finally: " + this.result);
            return this.result;
        }
    }

    protected void onPreExecute() {
        this.loading = ProgressDialog.show(this.context, "OTP status", "Validating OTP");
    }

    protected void onPostExecute(String result) {
        this.loading.dismiss();
        this.alertDialog = new Builder(this.context).create();
        this.alertDialog.setTitle("OTP Status");
        this.alertDialog.setMessage(result);
        this.alertDialog.show();
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
