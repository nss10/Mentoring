package kmit.mentoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class register extends Activity {
    AlertDialog alertDialog;
    Button back;
    Button confirm_otp;
    Button getOtpButton;
    String otp;
    EditText otpET;
    Button resend_otp;
    String uid;
    EditText useridET;
    boolean shouldEnterOTP;



    void validateOTP() {
        validateOTPBG validate = new validateOTPBG(this);
        validate.execute(new String[]{getString(R.string.connection_string), this.uid, this.otp});
        validate.onProgressUpdate(new Void[0]);
        if (validate.result.matches("1")) {
            Intent i = new Intent(getApplicationContext(), createPassword.class);
            i.putExtra("uid", this.uid);
            startActivity(i);
            finish();
            return;
        }
        Toast.makeText(this, "Incorrect OTP. Please check!", Toast.LENGTH_LONG).show();
    }

    void requestOTP() {
        getOTPBG get_otp = new getOTPBG(this);
        get_otp.execute(new String[]{getString(R.string.connection_string), this.uid});
        get_otp.onProgressUpdate(new Void[0]);
        this.alertDialog = new Builder(this).create();
        this.alertDialog.setTitle("OTP Status");
        this.alertDialog.setMessage(get_otp.result);
        this.alertDialog.setCancelable(false);
        if (get_otp.result.contains("already registered")) {
            this.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Login", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    register.this.startActivity(new Intent(register.this.getApplicationContext(), studentlogin.class));
                    register.this.finish();

                }
            });
        }
        else if(get_otp.result.contains("hall")){

            this.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Back", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        }
        else  {
            shouldEnterOTP = true;
            this.alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Enter OTP", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    register.this.alertDialog.dismiss();

                }
            });
        }
        this.alertDialog.show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ((TextView) findViewById(R.id.slabel)).setText(extras.getString("title"));
        }
        this.useridET = (EditText) findViewById(R.id.sid);
        this.otpET = (EditText) findViewById(R.id.otp);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.useridET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                register.this.uid = register.this.useridET.getText().toString().trim();
                if (imm != null && register.this.uid.length() >= 10) {
                    imm.toggleSoftInput(1, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.otpET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                register.this.otp = register.this.otpET.getText().toString().trim();
                if (imm != null && register.this.otp.length() >= 4) {
                    imm.toggleSoftInput(1, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.getOtpButton = (Button) findViewById(R.id.get_otp);
        this.back = (Button) findViewById(R.id.back_to_main);
        this.confirm_otp = (Button) findViewById(R.id.confirm_otp);
        this.resend_otp = (Button) findViewById(R.id.resend_otp);
        this.getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register.this.uid.length() == 10) {
                    register.this.getOtpButton.setText("Obtaining OTP...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            register.this.requestOTP();
                            if(shouldEnterOTP)
                            {
                                register.this.useridET.setVisibility(View.INVISIBLE);
                                register.this.getOtpButton.setVisibility(View.INVISIBLE);
                                register.this.back.setVisibility(View.INVISIBLE);
                                register.this.otpET = (EditText) register.this.findViewById(R.id.otp);
                                register.this.otpET.setVisibility(View.VISIBLE);
                                register.this.confirm_otp.setVisibility(View.VISIBLE);
                                register.this.resend_otp.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                register.this.getOtpButton.setText("GET OTP");
                                register.this.useridET.setText("");

                            }
                        }
                    }, 2000);
                    return;
                }
                Toast.makeText(register.this, "Please Enter a valid ID", Toast.LENGTH_LONG).show();
            }
        });
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.this.alertDialog = new Builder(register.this).create();
                register.this.alertDialog.setTitle("Are you sure?");
                register.this.alertDialog.setMessage("Do you want to exit the registration process?");
                register.this.alertDialog.setCancelable(false);
                register.this.alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                register.this.alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        register.this.alertDialog.dismiss();
                    }
                });
                register.this.alertDialog.show();



            }
        });
        this.confirm_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.this.validateOTP();
            }
        });
        this.resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.this.requestOTP();
            }
        });
    }

    public void onBackPressed() {
        Intent startMain = new Intent("android.intent.action.MAIN");
        startMain.addCategory("android.intent.category.HOME");
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
