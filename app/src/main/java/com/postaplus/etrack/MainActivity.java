package com.postaplus.etrack;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.TriggerStateChangeEvent;

import java.util.HashMap;
import java.util.Map;

import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCReader;
import util.ActivityNotification;
import webservice.FuncClasses.Events;
import webservice.WebService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.postaplus.etrack.LoginActivity.PREFS_NAME;
import static com.postaplus.etrack.ScreenActivity.loginflag;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {
    Events[] eventResponse;
    SQLiteDatabase sqldb = null;
    DatabaseHandler db;
    String usrnamee, passworde;
    DrawerLayout drawer;
    //KDC Parameters
    public static BluetoothDevice ScannerDevice = null;
    KDCData ScannerData;
    KDCReader _kdcReader;

    public static String KDCScannerCallFrom = "";
    public static String WaybillFromScanner = "";
    ActivityNotification actNoty = new ActivityNotification();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    String AppVersion = "";
    TextView versionDisp;
    TextView useridtxt,logintext;
    String LogoutResp;
    String logintoken;
    ImageView imageViewpp,imageViewpp1;
    TextView logotext,logotext1;
   // public static Boolean saveLogin;

    public static BarcodeReader barcodeReader;
    MainActivity  _activity;
    public AidcManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      //  _activity = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // usrnamee=getIntent().getExtras().getString("username");
        //  passworde=getIntent().getExtras().getString("password");
        usrnamee=actNoty.getUserName(MainActivity.this);
        passworde=actNoty.getPassword(MainActivity.this);
        logotext1=(TextView)findViewById(R.id.logotext1);
        imageViewpp=(ImageView)findViewById(R.id.imageViewpp) ;
        imageViewpp1=(ImageView)findViewById(R.id.imageViewpp1) ;
        System.out.println("Username first mainactv is"+usrnamee+"password mainactv is"+passworde);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        versionDisp=(TextView)findViewById(R.id.versntxt);
        useridtxt=(TextView)findViewById(R.id.usrntxtv);
        logotext = (TextView)findViewById(R.id.logotext);
        String mVersion = "";
        try {
            mVersion = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mVersion = "";
        }

        AppVersion = mVersion;
        System.out.println("versionDisp before home: " + versionDisp);
        versionDisp.setText("Version "+AppVersion);

        useridtxt.setText(usrnamee);
        logintoken= actNoty.getToken(MainActivity.this);
//honeywell
        _activity=this;
        AidcManager.create(_activity, new AidcManager.CreatedCallback() {

                        @Override
                        public void onCreated(AidcManager aidcManager) {
                            manager = aidcManager;
                            barcodeReader = manager.createBarcodeReader();

                            System.out.println("manager in main ocree:" + manager + "barcode is:" + barcodeReader);

                            try {
                               /* if (barcodeReader != null) {
                                    barcodeReader.claim();

                                    System.out.println("barcodeReader main in try:" + barcodeReader);
                                }*/
                                // apply settings
                                Map<String, Object> properties = new HashMap<String, Object>();
                                // Set Symbologies On/Off
                                properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
                                properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
                                properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
                                //properties.put(BarcodeReader.)
                                // Set Trigger Mode
                                properties.put(BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL, true);
                                // Set Max Code 39 barcode length
                                properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
                                // Turn on center decoding
                                properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
                                // Disable bad read response, handle in onFailureEvent
                                properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);

                                // Apply the settings
                                //barcodeReader.setProperties(properties);
                                //barcodeReader.addBarcodeListener(_activity);
                                //arcodeReader.addTriggerListener(_activity);
                            /*try {
                                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                                        BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
                            } catch (UnsupportedPropertyException e) {
                                Toast.makeText(_activity, "Failed to apply properties", Toast.LENGTH_SHORT).show();
                            }*/

                                System.out.println("barcodeReader main in finish" + barcodeReader);
                            } catch (Exception e) {
                            }


                        }

                    }
            );


        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = imageViewpp.getWidth();
                final float translationX = width * progress;
                imageViewpp.setTranslationX(translationX);
                imageViewpp1.setTranslationX(translationX - width);
                logotext.setTranslationX(translationX);
                logotext1.setTranslationX(translationX - width);
            }
        });
        animator.start();



    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            // super.onBackPressed();
            moveTaskToBack(true);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            Intent int1 = new Intent(MainActivity.this,EventActivity.class);
           /* int1.putExtra("username",usrnamee);
            int1.putExtra("password",passworde);*/
            // getevent();
            startActivity(new Intent(int1));
            //this.finish();

            MainActivity.this.finish();
            // Handle the event action
        } else if (id == R.id.nav_rstverfctn) {

            Intent int1 = new Intent(MainActivity.this,RstVerificationActivity.class);
          /* int1.putExtra("username",usrnamee);
            int1.putExtra("password",passworde);*/
            startActivity(new Intent(int1));
            //  startActivity(new Intent(MainActivity.this, RstVerificationActivity.class));
            drawer.isDrawerOpen(Gravity.LEFT);
            return true;



        } else if (id == R.id.nav_pckpverfctn) {

            Intent int1 = new Intent(MainActivity.this,PickupVerificationActivity.class);
            startActivity(new Intent(int1));


        } else if (id == R.id.nav_utlwarehouse) {
            Intent int1 = new Intent(MainActivity.this,UTLWarehouseActivity.class);
            startActivity(new Intent(int1));
            this.finish();
        } else if (id == R.id.nav_wcmax) {
            Intent int1 = new Intent(MainActivity.this,WC_MaxActivity.class);
            startActivity(new Intent(int1));
            this.finish();
        }else if (id == R.id.nav_scrtychck) {
            Intent int1 = new Intent(MainActivity.this,SecurityCheckActivity.class);
            startActivity(new Intent(int1));
                this.finish();
        }else if (id == R.id.nav_bagconso) {
            Intent int1 = new Intent(MainActivity.this,BagConsolidateActivity.class);
            startActivity(new Intent(int1));
            this.finish();
        }else if (id == R.id.nav_manifest) {
            Intent int1 = new Intent(MainActivity.this,ManifestActivity.class);
            startActivity(new Intent(int1));
            this.finish();
        }

        else if (id == R.id.nav_logout) {
            Intent int1= new Intent(MainActivity.this,LoginActivity.class);
            //int1.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            // int1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // int1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
         /*   usrnamee="";
            passworde="";
            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(int1));
*/
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog


                    finish();


                    LogoutResp = WebService.AuthSessionLoggOut(logintoken);

                    System.out.println("LogoutResp on main:"+LogoutResp);
                    if(LogoutResp!=null){

                        if(LogoutResp.equals("true")){
                            Intent intent = new Intent(MainActivity.this,
                                    LoginActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                loginflag = false;
                            // Add new Flag to start new Activity
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            MainActivity.this.finish();
                            //  saveLogin=false;
                          //  System.out.println("savelogin bef val is:" + saveLogin);
                            //saveLogin = loginPreferences.getBoolean("saveLogin",false);
                            //     loginPrefsEditor.clear();
                            //     loginPrefsEditor.commit();
                            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                            loginPrefsEditor = loginPreferences.edit();
                            // loginPrefsEditor.clear();
                            loginPrefsEditor.putString("Loginstatus", "FALSE");
                            loginPrefsEditor.commit();

                          //  System.out.println("savelogin val in logout is:" + saveLogin);


                            //shipacc="";
                            //devicserialresp= WebService.SetAppDeviceSerial(shipacc,regId,devtyp,devsts="I");
                            dialog.dismiss();
                        }
                    }
                  /*  Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Add new Flag to start new Activity
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //  saveLogin=false;
                    System.out.println("savelogin bef val is:" + saveLogin);
                    //saveLogin = loginPreferences.getBoolean("saveLogin",false);
                    //     loginPrefsEditor.clear();
                    //     loginPrefsEditor.commit();
                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    loginPrefsEditor = loginPreferences.edit();
                    // loginPrefsEditor.clear();
                    loginPrefsEditor.putString("Loginstatus", "FALSE");
                    loginPrefsEditor.commit();

                    System.out.println("savelogin val in logout is:" + saveLogin);


                    //shipacc="";
                    //devicserialresp= WebService.SetAppDeviceSerial(shipacc,regId,devtyp,devsts="I");
                    dialog.dismiss();*/

                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {


    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {

    }


}
