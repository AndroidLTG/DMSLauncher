package com.vietdms.mobile.dmslauncher;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import CommonLib.EventPool;
import CommonLib.EventType;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class CustomErrorActivity extends Activity {
    private Session session;
    private ProgressBar progressBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_custom_error);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        intent = new Intent(getApplicationContext(), Home.class);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");
//
//        session = Session.getDefaultInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("vietdmsreport@gmail.com", "vietdms.com");
//            }
//        });
//
//        //    pdialog = ProgressDialog.show(getApplicationContext(), "", "Đang gửi...", true);
//
//        RetreiveFeedTask task = new RetreiveFeedTask();
//        task.execute();
        EventPool.control().enQueue(new EventType.EventLogCrash(CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent())));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                MyMethod.showToast(getApplicationContext(), "Báo lỗi đã gửi đi, cảm ơn bạn");
                progressBar.setVisibility(View.GONE);
                CustomActivityOnCrash.restartApplicationWithIntent(CustomErrorActivity.this, intent);
            }
        }, 3000);

    }


    class RetreiveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("testfrom354@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("androidltg94@gmail.com"));
                message.setSubject("Báo lỗi phần mềm DMS từ " + Build.MODEL + " phiên bản Android : " + Build.VERSION.RELEASE);
                message.setContent(CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent()), "text/html; charset=utf-8");
                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Báo lỗi đã gửi đi, cảm ơn bạn", Toast.LENGTH_LONG).show();
            CustomActivityOnCrash.restartApplicationWithIntent(CustomErrorActivity.this, intent);
        }
    }
}