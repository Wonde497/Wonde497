package com.example.downloading;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText url;
    ProgressBar progressBar;
    ImageButton downloadBtn;
    public static final int PERMISSION_STORAGE_CODE=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url=findViewById(R.id.downloadUrl);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        downloadBtn=findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[]permissions={android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        requestPermissions(permissions,PERMISSION_STORAGE_CODE);



                    }else{
                        if(isNetworkConnected()){


                            //onToggleImage(view);
                            progressBar.setVisibility(View.VISIBLE);
                            downloadBtn.setVisibility(View.GONE);
                            startDownloading();
                            if(isValidUrl(url.getText().toString().trim())){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(),"Downloaded Successfuly!",Toast.LENGTH_SHORT).show();
                                        downloadBtn.setVisibility(View.VISIBLE);

                                    }
                                },5000);
                                Toast.makeText(getApplicationContext(),"downloading....",Toast.LENGTH_LONG).show();

                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"No internet connection!",Toast.LENGTH_LONG).show();
                        }



                    }



                }else{
                   if(isNetworkConnected()){


                       //onToggleImage(view);
                       progressBar.setVisibility(View.VISIBLE);
                       downloadBtn.setVisibility(View.GONE);
                       startDownloading();
                       if(isValidUrl(url.getText().toString().trim())){
                           new Handler().postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   progressBar.setVisibility(View.GONE);
                                   Toast.makeText(getApplicationContext(),"Downloaded Successfuly!",Toast.LENGTH_SHORT).show();
                                   downloadBtn.setVisibility(View.VISIBLE);

                               }
                           },5000);
                           Toast.makeText(getApplicationContext(),"downloading....",Toast.LENGTH_LONG).show();

                       }

                   }else{
                       Toast.makeText(getApplicationContext(),"No internet connection!",Toast.LENGTH_LONG).show();
                   }



               }

            }
        });


    }

private boolean isNetworkConnected(){
    ConnectivityManager cm= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

       }

    private void startDownloading() {
        String downloadurl=url.getText().toString().trim();
        if(isValidUrl(downloadurl)){



                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(downloadurl));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("Download");
                request.setDescription("Downloading file...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"Downloader"+System.currentTimeMillis());
                DownloadManager manager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);





    }
    else{
        progressBar.setVisibility(View.GONE);
        downloadBtn.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(this.getCurrentFocus(), "please input correct link!", Snackbar.LENGTH_LONG);
            snackbar.setAction("need help?", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBrowser("https://www.urlvoid.com");
                }
            });
            snackbar.show();

        }
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode,String[]permissions,int[] grantResult ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        switch(requestCode){
            case PERMISSION_STORAGE_CODE:{
            if (grantResult.length>0||grantResult[0]==PackageManager.PERMISSION_GRANTED){
                startDownloading();

            }else{
                Toast.makeText(getApplicationContext(),"permission denied....",Toast.LENGTH_SHORT).show();
                }
            }



        }
    }
    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
    public void onToggleImage(View view) {

        // Toggle the image
        if (downloadBtn.getTag() == null || downloadBtn.getTag().equals("arrow")) {
            downloadBtn.setImageResource(Integer.parseInt("downloading"));
            downloadBtn.setTag("downloading");
        } else {
            downloadBtn.setImageResource(R.drawable.arrow);
            downloadBtn.setTag("arrow");
        }
    }
    private boolean isValidYouTubeUrl(String url) {
        String pattern = "(http(s)?://)?(www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})";
        return url.matches(pattern);
    }

    private String extractYouTubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}