package com.postaplus.etrack;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import webservice.WebService;

public class ScreenActivity extends Activity {
    public static String ipaddress = null;
    String uname=null;
    private static int SPLASH_TIME_OUT = 2800;
    ImageView logoapp;
    boolean netstatus, servicestatus;
    public static boolean loginflag= false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    //  private servc
// public static String url = "http://172.53.1.34/OpsCourierScannerService/OpsGCScanSrv.svc/";
    //public servc
    //  public static String url = "http://168.187.136.18/OpsCourierScannerService/OpsGCScanSrv.svc/";

    // Stag service
    //public static String url = "http://172.53.1.34/OpsCourierScannerServiceStag/OpsGCScanSrv.svc/";
    // Live service
    // public static String url = "http://postascan.postaplus.com/ServiceV2_0/OpsGCScanSrv.svc/";
    //public static String ipaddress="http://postascan.postaplus.com/ServiceV2_0/OpsGCScanSrv.svc/";
    public static String url ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screen);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //getActionBar().hide();
        logoapp = (ImageView) findViewById(R.id.imagelogo);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound1);

        if(!checkAndRequestPermissions()) finish();
        //To Check the User Permissions


        //create a textfile to store the ip address
        File ip_addressfile = new File(Environment.getExternalStorageDirectory(), "EtrackHandHeld/Data");
        ip_addressfile.mkdirs();
        File ipaddfile = new File(ip_addressfile, "ipaddress.txt");
        //File gpxfile = new File(file, sFileName);

        //textfile not exists
        if (!ipaddfile.exists()) {

            try {

                FileWriter writer = new FileWriter(ipaddfile);

                // LIVE LINK
                //ipaddress="https://etrack.postaplus.net/EtrackHandHeld/HHSrvAj.svc";

                //stag link
               // ipaddress="http://168.187.136.18:8095/EtrackHandHeld/HHSrvAj.svc";

                // test new service
                //ipaddress="http://168.187.136.18:8080/EtrackHandHeld/HHSrvAj.svc";

                //stag link 2019
                 ipaddress="https://staging.postaplus.net/EtrackHandHeld/HHSrvAj.svc";
                writer.append(ipaddress);

                writer.flush();
                writer.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            StringBuilder text = new StringBuilder();

            try {

                BufferedReader br = new BufferedReader(new FileReader(ipaddfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    //text.append('\n');
                }
                //}

                br.close();
                ipaddress = text.toString();
                //System.out.println(ipaddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            url=ipaddress+"/";
        }
        //if textfile exist
        else {

            //read the ip address from textfile
            StringBuilder text = new StringBuilder();

            try {

                BufferedReader br = new BufferedReader(new FileReader(ipaddfile));
                String line;

                while ((line = br.readLine()) != null) {

                    text.append(line);
                    //text.append('\n');
                }
                //}
                br.close();

                ipaddress = text.toString();
                //System.out.println(ipaddress);

            } catch (IOException e) {
                ScreenActivity.this.finish();
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "IP address reading error", Toast.LENGTH_LONG).show();
            }

            System.out.println("ipaddress read us:"+ipaddress);


            url=ipaddress+"/";
        }
        logoapp.startAnimation(rotate);
        //mp.start();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                netstatus = isNetworkConnected();
                if (netstatus) {
                    servicestatus = isConnected();
                    System.out.println("Service status is"+servicestatus);
                    if (servicestatus) {


                        if(loginflag==true){
                            Intent localIntent = new Intent(ScreenActivity.this, MainActivity.class);
                            //ScreenActivity.this.startActivity(localIntent);
                            // new code
                            ScreenActivity.this.startActivity(new Intent(localIntent));

                            ScreenActivity.this.finish();
                        }else{

                            Intent localIntent = new Intent(ScreenActivity.this, LoginActivity.class);
                            //ScreenActivity.this.startActivity(localIntent);
                            // new code
                            ScreenActivity.this.startActivity(new Intent(localIntent));

                            ScreenActivity.this.finish();

                        }
                    } else {
                        ScreenActivity.this.finish();
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    ScreenActivity.this.finish();
                    Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }


            public boolean isConnected() {
                try {

                    ConnectivityManager cm = (ConnectivityManager) getSystemService
                            (Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    System.out.println("netInfo is"+netInfo);
                    if (netInfo != null && netInfo.isConnected()) {
                        Boolean Connect = WebService.GET_SERVICE_STATUS();
                        System.out.println("Connect is"+Connect);
                        if (Connect) return true;
                        else {
                            Toast.makeText(getBaseContext(), "Webservice not available",
                                    Toast.LENGTH_LONG).show();

                            Log.e("WebSer/Err", "WebService not available");
                            return false;

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;

            }

            private boolean isNetworkConnected() {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                return ni != null;
            }
        }, SPLASH_TIME_OUT);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private  boolean checkAndRequestPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();
        int PERMISSION_CHECK_RESULT = -1;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


		/*if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.CLEAR_APP_CACHE)
				!= PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.CLEAR_APP_CACHE);
			//ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},1);
			//ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}*/

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Screen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.postaplus.etrack/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Screen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.postaplus.etrack/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
