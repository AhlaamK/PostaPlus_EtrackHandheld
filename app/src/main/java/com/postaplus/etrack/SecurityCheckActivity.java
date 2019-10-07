package com.postaplus.etrack;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import koamtac.kdc.sdk.KDCBarcodeDataReceivedListener;
import koamtac.kdc.sdk.KDCConnectionListener;
import koamtac.kdc.sdk.KDCConstants;
import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCDataReceivedListener;
import koamtac.kdc.sdk.KDCReader;
import util.ActivityNotification;
import utils.Utils;
import webservice.WebService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.postaplus.etrack.LoginActivity.PREFS_NAME;
import static com.postaplus.etrack.LoginActivity.PREF_loginstatus;
import static com.postaplus.etrack.MainActivity.barcodeReader;
import static com.postaplus.etrack.ScreenActivity.loginflag;

public class SecurityCheckActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener,
        BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener  {
    String todaydate;
    EditText Edtrst,Edtcurname;
    Button Btnverify,Btnclear;
    TextView textcounttotl,currentdate;
    ProgressBar progressBar;
    KDCData ScannerData;
    KDCReader _kdcReader;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    public boolean DonotInterruptKDCScan = true;

    public int SCANNER_REQUEST_CODE = 123;
    String connote,waybill;
    TableLayout resulttab;
    String camerabill;
    TableRow tr,tr1;
    TableRow.LayoutParams lp;
    boolean flag = false;
    int rowid = -1;
    int countN = 0;
    TextView deltxt,waybilltxt,acnotxt,acnametxt, verfytxt,verfytxtdel;
    ActivityNotification actNoty = new ActivityNotification();
    String logintokn;
    JSONObject GetRstResponse;
    String curname;
    JSONArray detailsarray;
    JSONArray detlarray = null;
    String respconte = null,respaccno = null, respaccname = null;
    String rstdate;
    String errmsg ,result,errmsgRstVer,resultRstVer;
    JSONObject CheckcntevryResp;
    Button Btnsearch;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    String tablwaybill ,tablaccno,tablacname;
    TextView chcverfttxt,delvisibletxt,acctxt,aacnamtxt,ACtxt;
    String errmsgchk ,resultchk;
    SecurityCheckActivity _activity;
    String RstvrfyResp;
    JSONArray jsonArray = new JSONArray();
    int progrflag=0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_check);
        System.out.println("barcodeReader are:" + barcodeReader);
        if(barcodeReader!= null){
            barcodeReader.addBarcodeListener(SecurityCheckActivity.this);
        }
        IntializeViews();


        // code for Verify button
        Btnverify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));

                System.out.println("v:"+v.isSelected());

                if(resulttab.getChildCount()==0){
                    //  progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);
                    Toast toast = Toast.makeText(getApplicationContext(), "No Airwaybills to save", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;

                }else{

                    if (v.isActivated() && v.isSelected()) {
                        return;
                    }else
                        v.setActivated(true);
                    v.setSelected(true);

                    // progressBarMain.setVisibility(View.VISIBLE);

                }



                System.out.println("resulttab.getChildCount are:" + resulttab.getChildCount());
                int tabcount = resulttab.getChildCount();

                for (int j = 0; j < tabcount; j++) {
                    JSONObject postedJSON = new JSONObject();
                    System.out.println("tabcount is:" + tabcount + "j val " + j);
                    waybilltxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(1);
                    acctxt =(TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(2);
                    aacnamtxt =(TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);
                    tablwaybill = waybilltxt.getText().toString();
                    tablaccno = acctxt.getText().toString();
                    tablacname= aacnamtxt.getText().toString();
                    tr = (TableRow) resulttab.getChildAt(j);

                    System.out.println("tablacname on trn:" + aacnamtxt.getText().toString());
                    waybilltxt.setId(j);
                    tr.setId(j);
                    chcverfttxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(4);
                    delvisibletxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(0);

                    System.out.println("chcverfttxt on trn:" + chcverfttxt.getText().toString());
                    if (delvisibletxt.getText().toString().matches("DEL")&& delvisibletxt.isShown()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please Delete Awbs not in RST", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        v.setActivated(false);
                        v.setSelected(false);
                        return;

                    }else if (chcverfttxt.getText().toString().matches("N")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please Verify all Awbs in RST", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        v.setActivated(false);
                        v.setSelected(false);
                        jsonArray = new JSONArray();
                        System.out.println("jsonArray N  n:" + jsonArray);
                        return;

                    } else {
                        try {

                            postedJSON.put("Connote", tablwaybill);
                            postedJSON.put("ShipperAcc",tablaccno);
                            postedJSON.put("ShipperName",tablacname);

                            System.out.println("postedJSON obj n:" + postedJSON);

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }


                        jsonArray.put(postedJSON);

                        progrflag ++;


                    }


                    System.out.println("jsonArray secry n:" + jsonArray);
                    // "counter:"+jsonArray.length()+"progrflag"+progrflag+ "logintokn"+logintokn);


                }


                System.out.println(" tr tag"+tr.getId());
                if(jsonArray.length()!= 0){
                    RstvrfyResp = WebService.SetRunsheetVerify(logintokn,Edtrst.getText().toString(),jsonArray);

                    System.out.println("RstvrfyResp on trn:" + RstvrfyResp+"jaaaray"+jsonArray);
                }


                if (RstvrfyResp != null) {

                    JSONObject jsonrstveresp = null;
                    try {
                        jsonrstveresp = (JSONObject) new JSONTokener(RstvrfyResp.toString()).nextValue();


                        errmsgRstVer = (String) jsonrstveresp.get("Message");
                        resultRstVer = (String) jsonrstveresp .get("Result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println("errmsgRstVer is"+errmsgRstVer);

                    if (errmsgRstVer.matches("VALID")) {
                        v.setActivated(false);
                        v.setSelected(false);

                        int tab = resulttab.getChildCount();
                        for (int l = 0; l < tab; l++) {

                            System.out.println("resultab val l:" + l);
                            verfytxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(4);

                            System.out.println("verfytxtdel save are:" + verfytxtdel.getText().toString() + "tr save id :" + tr.getId());
                            if (verfytxtdel.getText().toString().matches("Y")) {
                                ///valdcnt = valdcnt - 1;
                                //  textcount2.setText(String.valueOf(valdcnt));
                                resulttab.removeViewAt(l);
                                l = l - 1;
                                tab = tab - 1;
                                progrflag = progrflag - 1;

                                Toast toast = Toast.makeText(getApplicationContext(), "Airwaybills verified successfully", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();



                                System.out.println("jsonArray onsave :" + jsonArray.length());
                                System.out.println("tab new val " + tab + "new l" + l);
                            }/* else if (!checkremartexts.getText().toString().matches("Valid")) {
                                  //  Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_SHORT);
                                  //  toast.setGravity(Gravity.CENTER, 0, 0);
                                  //  toast.show();
                                }*/

                        }

                        textcounttotl.setText(String.valueOf(resulttab.getChildCount()));

                        for (int k = 0; k < resulttab.getChildCount(); k++) {
                            tr = (TableRow) resulttab.getChildAt(k);
                            tr.setId(k);
                            System.out.println("tr aftr del:" + tr.getId());
                        }

                        System.out.println("progrflag:" + progrflag);

                        Edtrst.setText("");
                        Edtcurname.setText("");
                        currentdate.setText("");



                    } else {
                        if (resultRstVer.matches("FAILURE")) {



                            if(errmsgRstVer.contentEquals("Invalid Session")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(SecurityCheckActivity.this).create();
                                alertDialog.setTitle("Invalid Session");
                                alertDialog.setMessage("Your session is out. Please re-login to continue");
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Edtrst.setText("");
                                                Edtrst.setEnabled(true);

                                                DonotInterruptKDCScan=false;
                                                dialog.dismiss();
                                                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                                                loginPrefsEditor = loginPreferences.edit();
                                                loginPrefsEditor.commit();
                                                //loginval = "FALSE";
                                                loginPrefsEditor.putString(PREF_loginstatus, "FALSE");

                                                // loginPrefsEditor.putString("loginval", "FALSE");
                                                loginPrefsEditor.commit();
                                                // Log.e("loginval//",loginval);
                                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                // Add new Flag to start new Activity
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                Log.e("hee","hu");
                                                getBaseContext().startActivity(intent);
                                                SecurityCheckActivity.this.finish();
                                                Log.e("here","hu");
                                                loginflag= false;
                                            }
                                        });
                                alertDialog.show();

                                v.setActivated(false);
                                v.setSelected(false);
                            }
                            else if(!errmsgRstVer.contentEquals("VALID")){
                                Toast toast = Toast.makeText(getApplicationContext(), errmsgRstVer, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                //   progressBarMain.setVisibility(View.INVISIBLE);
                                v.setActivated(false);
                                v.setSelected(false);


                                // return;
                            }


                        } else {
                           /* Toast toast = Toast.makeText(getApplicationContext(), errmsgRstVer, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            // progressBarMain.setVisibility(View.INVISIBLE);
                            v.setActivated(false);
                            v.setSelected(false);*/
                            //return;
                        }
                    }
                    //SetConnoteWHStockTransResp = null;


                }
                textcounttotl.setText(String.valueOf(resulttab.getChildCount()));

                //  textcount2.setText(String.valueOf(valdcnt=valdcnt-1));
                // valdcnt=0;
                // valdcnt=valdcnt-1;
                // System.out.println("prgr val:" + progrflag + "reslt " + resulttab.getChildCount()+"vald"+valdcnt+"i b"+invaldcnt);

               /* if(progrflag==valdcnt){
                    // progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);
                }*/



            }

        });



        // code for clear button
        Btnclear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                Edtrst.setText("");
                Edtcurname.setText("");
                Edtrst.setEnabled(true);
                currentdate.setText("");
                Edtcurname.setEnabled(true);
                textcounttotl.setText("");
                jsonArray= new JSONArray();
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();

                // v.getBackground().clearColorFilter();
            }

        });

        // code for search button
        Btnsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                Edtrst.setEnabled(false);
                currentdate.setEnabled(false);
                Edtcurname.setEnabled(false);

                if(Edtrst.getText().toString().contentEquals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Scan or Enter RST No.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Edtrst.setEnabled(true);
                    currentdate.setEnabled(true);
                    Edtcurname.setEnabled(true);
                    jsonArray = new JSONArray();
                }else {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {

                            GetRstDetails();

                        }
                    });

                }
                // v.getBackground().clearColorFilter();
            }

        });
    }

    public void  IntializeViews() {
        Edtrst = (EditText) findViewById(R.id.edtrst);
        Edtcurname = (EditText) findViewById(R.id.edtcurname);
        Btnverify = (Button) findViewById(R.id.btnverify);
        Btnclear = (Button) findViewById(R.id.btnclear);
        Btnsearch = (Button) findViewById(R.id.searchbtn);
        textcounttotl = (TextView) findViewById(R.id.textViewcount);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        currentdate = (TextView) findViewById(R.id.datespinner);
        resulttab = (TableLayout) findViewById(R.id.resulttable1);
        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
        todaydate = format.format(myCalendar.getTime());
        // currentdate.setText(todaydate);

        logintokn = actNoty.getToken(SecurityCheckActivity.this);
       /* if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
            //  KDCTaskExecutable.execute();
        }*/
    }


    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC ofdsc Activity BarCodeReceived Block");
        System.out.print("KDCReader ofdsc Activity  BarCodeReceived Block");

        // if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();
            // System.out.print("ScannerData.GetData()"+ScannerData.GetData()+"length:"+ScannerData.GetData().length());

            if(Edtrst.getText().toString().matches("")) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtrst.setText(ScannerData.GetData());
                        GetRstDetails();
                        //  Edtcurname.setText(curname);
                        //   currentdate.setText(rstdate);
                        Edtrst.setEnabled(false);
                        currentdate.setEnabled(false);
                        Edtcurname.setEnabled(false);
                    }
                });
                return;
            }

            if (Utils.checkValidWaybill(kdcbarcodedata.GetData()) == true) {

                System.out.println("kdcbarcodedata ID : ");
                // System.out.println(R.id.WC_Frame);
                System.out.println(" value for kdcbarcodedata is : ");
                System.out.println(kdcbarcodedata);


                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (waybill != null) {
                            connote = waybill;

                            System.out.println("connote run is : "+connote);
                            //  if (Validations() == true) {


                            new checkSCwaybill(connote).execute();
                        }

                        //  }
                        // wbilldata1=contents;

                    }
                });

            } else  {

                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                        Log.e("222","222");
                    }
                });
            }
        } else {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                    Log.e("111","1");
                }
            });
        }
    }

    @Override
    public void ConnectionChanged(BluetoothDevice device, int state) {
        //ToDo Auto-generated method stub

        Log.i("KDCReader", "KDC ofdsc Activity connection changed block");
        System.out.print("KDCReader ofdsc Activity connection changed block");
        System.out.print("State is " + state);
        switch (state) {

            case KDCConstants.CONNECTION_STATE_CONNECTED:
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(_activity, "Scanner Connected", Toast.LENGTH_LONG).show();
                        isActivityActiveFlag = true;
                    }
                });
                break;

            case KDCConstants.CONNECTION_STATE_LOST:
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(_activity, "Scanner Connection Lost", Toast.LENGTH_LONG).show();
                        isActivityActiveFlag = true;
                    }
                });
                break;
        }
    }

    @Override
    public void DataReceived(KDCData kdcData) {

    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent honeywellevent) {
        System.out.println("honeywellevent honeywel is:" + honeywellevent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String barcodeData = honeywellevent.getBarcodeData();
                // update UI to reflect the data

                System.out.println("barcodeData honeywel is:" + barcodeData);


                if (barcodeData != null) {

                    //ScannerData = barcodeData;
                    waybill = barcodeData;
                    // StartDeliveryActivity.WaybillFromScanner = ScannerData.GetData();

                    if(Edtrst.getText().toString().matches("")) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Edtrst.setText(honeywellevent.getBarcodeData());
                                GetRstDetails();
                                //  Edtcurname.setText(curname);
                                //   currentdate.setText(rstdate);
                                Edtrst.setEnabled(false);
                                currentdate.setEnabled(false);
                                Edtcurname.setEnabled(false);
                            }
                        });
                        return;
                    }

                    if (Utils.checkValidWaybill(honeywellevent.getBarcodeData()) == true) {

                        System.out.println("honeywellevent ID : ");
                        // System.out.println(R.id.WC_Frame);
                        System.out.println(" value for kdcbarcodedata is : ");
                        System.out.println(honeywellevent);


                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (waybill != null) {
                                    connote = waybill;

                                    System.out.println("connote run is : "+connote);
                                    //  if (Validations() == true) {


                                    new checkSCwaybill(connote).execute();
                                }


                            }
                        });

                    } else  {

                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                                Log.e("222","222");
                            }
                        });
                    }
                } else {
                    _activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                            Log.e("111","1");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent honeywellevent) {

    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent honeywellevent) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            barcodeReader.aim(honeywellevent.getState());
            barcodeReader.light(honeywellevent.getState());
            barcodeReader.decode(honeywellevent.getState());


        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public class KDCTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

            if (ThrKdc != null) {
                ThrKdc.run();
            } else {

                ThrKdc = new Thread() {
                    @Override
                    public void run() {
                        _kdcReader = new KDCReader(null, _activity, _activity, null, null, null, _activity, false);
                        MainActivity.ScannerDevice = _kdcReader.GetBluetoothDevice();
                        //_kdcReader.EnableBluetoothAutoPowerOn(false);
                        //_kdcReader.EnableAutoReconnect(false);
                        //_kdcReader.EnableBluetoothWakeupNull(false);
                        _kdcReader.EnableBluetoothWakeupNull(true);
                        if (isCancelled()) ThrKdc.interrupt();
                    }
                };
                ThrKdc.start();
                if (isCancelled()) ThrKdc.interrupt();
            }
            return "";
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    startActivityForResult(intent, SCANNER_REQUEST_CODE);
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    startActivityForResult(intent, SCANNER_REQUEST_CODE);
                }


                return true;
            default:
                return super.dispatchKeyEvent(event);
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

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            DonotInterruptKDCScan = false;
            Intent int1 = new Intent(this, MainActivity.class);
            //  int1.putExtra("usrnamee", usrnamee);
            //  int1.putExtra("passworde", passworde);
            if(barcodeReader != null){
                barcodeReader.removeBarcodeListener(SecurityCheckActivity.this);
                barcodeReader.release();
            }
            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(barcodeReader != null){
                barcodeReader.removeBarcodeListener(SecurityCheckActivity.this);
                barcodeReader.release();
                SecurityCheckActivity.this.finish();
                Intent int1 = new Intent(this, MainActivity.class);
                // int1.putExtra("usrnamee", usrnamee);
                // int1.putExtra("passworde", passworde);
                startActivity(new Intent(int1));

            }else

            if (!isActivityActiveFlag) {
                Toast.makeText(getApplicationContext(), "Please wait for scanner to connect",
                        Toast.LENGTH_LONG).show();
                return false;
            } else {

                if (_kdcReader != null) _kdcReader.Disconnect();
                if (ThrKdc != null) ThrKdc.interrupt();
                KDCTaskExecutable.cancel(true);
                SecurityCheckActivity.this.finish();
                Intent int1 = new Intent(this, MainActivity.class);
                // int1.putExtra("usrnamee", usrnamee);
                // int1.putExtra("passworde", passworde);
                startActivity(new Intent(int1));
                return true;
            }


        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("KDCReader on hold actvity While Pause : " + _kdcReader);
        if (!isActivityActiveFlag) isActivityActiveFlag = false;

     /*   if (ThrKdc != null) {
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
            System.out.println("THRKDC in pause activated on" + ThrKdc);
        }
*/

        if (!DonotInterruptKDCScan) {
            System.out.println("KDCReader on evnt  While Pause : " + _kdcReader);
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
        } else {
            DonotInterruptKDCScan = false;
        }

        if(barcodeReader != null){
            barcodeReader.removeBarcodeListener(SecurityCheckActivity.this);
            barcodeReader.release();
        }
        //_activity.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("barcode1 scc onres:" + barcodeReader);
        if (barcodeReader != null) {
            try {

                barcodeReader.claim();
                System.out.println("barcode scc onres:" + barcodeReader);
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isActivityActiveFlag) isActivityActiveFlag = false;
        _activity = this;
        //   getcouriers();

        if (!KDCTaskExecutable.getStatus().equals(AsyncTask.Status.RUNNING) && !KDCTaskExecutable.getStatus().equals(AsyncTask.Status.FINISHED)) {
            //KDCTaskExecutable.cancel(true);
            KDCTaskExecutable.execute();
            System.out.println(" KDCTask Executed");
        }


        System.out.println("Resume activate in rst");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent

            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");

                camerabill = contents;

                if(Edtrst.getText().toString().contentEquals("")) {

                    if(_activity!= null) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Edtrst.setText(camerabill);
                                GetRstDetails();
                                Edtrst.setEnabled(false);
                                currentdate.setEnabled(false);
                                Edtcurname.setEnabled(false);
                              /*  Edtcurname.setText(curname);
                                currentdate.setText(rstdate);
                                Edtcurname.setText(curname);
                                Edtcurname.setEnabled(false);
                                Edtrst.setEnabled(false);*/

                            }
                        });
                        return;
                    } }else
              /*  if (Utils.checkValidWaybill(camerabill) == false) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid Waybill", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }*/
                    if (Utils.checkValidWaybill(camerabill) == true) {
                        connote = camerabill;
                        new checkSCwaybill(camerabill).execute();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid Waybill", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }



            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        } else {
            // Handle other intents
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Log.e("TouchTest", "Touch down");
        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            Log.e("TouchTest", "Touch up");
        }
      /*  if(spinevtcode != null){
            if (spinevtcode.matches("AS")) {
                weightet.setEnabled(true);
                weightet.setClickable(true);
            }else{
                weightet.setEnabled(false);
                weightet.setClickable(false);
            }
        }*/
        return true;
    }


    public class checkSCwaybill extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public checkSCwaybill(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;


            System.out.println("taskwaybill sc pre:" + Taskwabill);
        }

        public void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            checkwaybill(Taskwabill);
            return "";
        }

        private void checkwaybill(String wabil) {
            // TODO Auto-generated method stub
            try {
                wabil = Taskwabill;
                System.out.println("wabil val" + wabil);

                //  String secwaybl;
            /*    for (int i = 0; i < resulttab.getChildCount(); i++) {

                    TextView wbill = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(4);
                    secwaybl = wbill.getText().toString();

                    System.out.println("secwaybl wal:" + secwaybl + "waybill is:" + Taskwabill);
                    if (!Taskwabill.equals(secwaybl)) {
                        flag = true;

                    } else if (Taskwabill.equals(secwaybl)) {
                        flag = false;
                        CheckcntevryResp = WebService.CheckConnoteVerify(logintokn,Edtrst.getText().toString(),wabil);

                      //  break;

                    }
                }
*/
                CheckcntevryResp = WebService.CheckConnoteVerify(logintokn,Edtrst.getText().toString(),wabil);
                System.out.println("CheckcntevryResp val" + CheckcntevryResp);
                if (CheckcntevryResp != null) {
                    try {
                        JSONObject jsonchckres = (JSONObject) new JSONTokener(CheckcntevryResp.toString()).nextValue();
                        JSONObject jsnchkobj = jsonchckres.getJSONObject("d");
                        System.out.println("jsnchkobj is" + jsnchkobj);

                        errmsgchk =  jsnchkobj.getString("Message");
                        resultchk = (String) jsnchkobj.get("Result");
                        System.out.println("errmsgchk is" + errmsgchk);

                    }catch (Exception e ){


                    }

                }


                //  JSONObject jsnobj = JSONObject

            } catch (Exception e) {
                //Log.e("EventActivity:", e.getMessage().toString());
                //Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }

        @Override
        public void onPostExecute(String res) {
            //response=null;

            progressBar.setVisibility(View.INVISIBLE);

            tr = new TableRow(SecurityCheckActivity.this);

          /*  if(errmsgchk.matches("VALID")){

                verfytxt.setText("Y");
                tr.setBackgroundColor(Color.parseColor("#14FF3D"));
            }else*/

            if (Build.MODEL.contains("SM-N")) {


                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
                //   lp.setMargins(18, 2, 95, 2);
                lp.setMargins(20, 5, 200, 0);

                tr.setLayoutParams(lp);
                System.out.println("tr id:" + tr.getId());

            } else {
                lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                //tr.setLayoutParams(lp);
                //lp.setMargins(0, 20, 5, 0);
                lp.setMargins(0,5,70, 0);
                tr.setLayoutParams(lp);

            }

            //	System.out.println("response : " + response.toString());


            deltxt = new TextView(SecurityCheckActivity.this);
            // deltxt.setLayoutParams(lp);
            deltxt.setWidth(80);
            // deltxt.setText("Delete");
            deltxt.setText("DEL");
            waybilltxt = new TextView(SecurityCheckActivity.this);
            // waybilltxt.setLayoutParams(lp);
            waybilltxt.setWidth(50);
            waybilltxt.setText(Taskwabill);
            System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);


            acnotxt = new TextView(SecurityCheckActivity.this);
            // stckstattxt.setLayoutParams(lp);
            acnotxt.setWidth(50);
            //  acnotxt.setText(StockStatus);



            acnametxt = new TextView(SecurityCheckActivity.this);
            // toloctxt.setLayoutParams(lp);
            acnametxt.setWidth(10);
            // acnametxt.setText(EdtToLoc.getText().toString());


            verfytxt = new TextView(SecurityCheckActivity.this);
            if (Build.MODEL.contains("SM-N")) {
                verfytxt.setLayoutParams(lp);
            }else{
                verfytxt.setWidth(100);
            }
            // verfytxt.setLayoutParams(lp);
            //verfytxt.setWidth(250);
            verfytxt.setText("N");

            //  System.out.println("cnametxt  is"+cname1);





            tr.addView(deltxt);
            tr.addView(waybilltxt);
            tr.addView(acnotxt);
            tr.addView(acnametxt);
            tr.addView(verfytxt);



            System.out.println("res chld:" + resulttab.getChildCount());

            if (resulttab.getChildCount() == 0) {
                resulttab.addView(tr,0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            } else {
                //	counttxt.setText(String.valueOf(resulttab.getChildCount()));
                //	System.out.println("value of counttxt in pickup update"+counttxt.toString());

                String wb = null;
                String verytxt = null;
                TextView deltextt = null;
                for (int i = 0; i < resulttab.getChildCount(); i++) {

                    TextView wbill = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(1);
                    wb = wbill.getText().toString();

                    System.out.println("wb wal:" + wb + "waybill is:" + Taskwabill);
                    if (!Taskwabill.equals(wb)) {
                        flag = true;

                    } else if (Taskwabill.equals(wb)) {
                        flag = false;
                        System.out.println("flag is:"+flag);
                        TextView vertxt = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(4);
                        deltextt  = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(0);
                        ACtxt  = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(2);
                        System.out.println("acnotxt is:"+ACtxt.getText().toString());
                        if(errmsgchk.matches("VALID")&& (!ACtxt.getText().toString().isEmpty())) {
                            verytxt = vertxt.getText().toString();
                            tr1 = ((TableRow) resulttab.getChildAt(i));
                            tr1.setBackgroundColor(Color.parseColor("#32CD32"));
                            vertxt.setText("Y");
                        }

                        break;

                    }
                }

                if (flag) {
                    resulttab.addView(tr,0,new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tr.setBackgroundColor(Color.parseColor("#d73232"));
                } else {
                    System.out.println("value of counttxt in else" + countN);

                    countN = countN - 1;


                    //   textcount1.setText(String.valueOf(resulttab.getChildCount()));


                  /*  if (Taskwabill.equals(wb)&&acnotxt.getText().toString().isEmpty()) {
                        // verfytxt.setText("Y");
                        tr.setBackgroundColor(Color.parseColor("#14FF3D"));
                        Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        //  textcount2.setText(String.valueOf(valdcnt = valdcnt - 1));
                        //Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                        return;
                    }
*/

                }


                // Pb.setVisibility(View.INVISIBLE);


            }

            System.out.println("text tot valid" + resulttab.getChildCount());
            //textcount1.setText(String.valueOf(resulttab.getChildCount()));

            tr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("v val:" + v);
                    System.out.println("Rowid:" + rowid + ",v.getID:" + v.getId());
                    if (rowid != v.getId()) {
                        rowid = v.getId();
                        Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                    } else if (rowid == v.getId()) {
                        System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());
                        if (resulttab.getChildCount() != 0) {
                            verfytxtdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(4);

                            System.out.println("verfytxtdel are:" + verfytxtdel.getText().toString());

                            //   checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(rowid)).getChildAt(3);
                            //   System.out.println("checkremartextdel are:" + checkremartextdel.getText().toString());
                            if (!verfytxtdel.getText().toString().matches("Y")) {
                                //invaldcnt = invaldcnt - 1;
                                // textcount3.setText(String.valueOf(invaldcnt));
                            } else {
                                // valdcnt = valdcnt - 1;
                                // textcount2.setText(String.valueOf(valdcnt));
                            }
                            resulttab.removeViewAt(v.getId());

                            //   System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                            Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                            rowid = -1;
                            int tabcount = (resulttab.getChildCount());
                            for (int i = 0; i < tabcount; i++) {
                                tr = (TableRow) resulttab.getChildAt(i);
                                tr.setId(i);
                                System.out.println("tr aftr del:" + tr.getId());
                            }


                            //textcount1.setText(String.valueOf(resulttab.getChildCount()));

                        }
                    }
                }
            });

            int tabcount = (resulttab.getChildCount());
            for (int k = 0; k < tabcount; k++) {
                tr = (TableRow) resulttab.getChildAt(k);
                tr.setId(k);
                System.out.println("tr aftr del:" + tr.getId());
            }


        }

    }

    public void GetRstDetails() {

        GetRstResponse = WebService.GetRunsheetDetails(logintokn, Edtrst.getText().toString());

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);
        System.out.println("GetRstResponse text is:" + GetRstResponse);
   /*    if(GetRstResponse != null) {
            try {
                GetRunsheetDetails_Header RstHd = new GetRunsheetDetails_Header();
               RstHd.CodeCourier = GetRstResponse.get("CodeCourier");
               Object cuname = RstHd.CourierName = GetRstResponse.get("CourierName");
                Object rsdate = RstHd.RunsheetDate = GetRstResponse.get("RunsheetDate");
                RstHd.RunsheetNum = GetRstResponse.getString("RunsheetNum");
                RstHd.ServResp = (ServiceResponse) GetRstResponse.get("ServiceResponse");
                 detlarray = GetRstResponse.getJSONArray("Details");
                System.out.println("detlarray text is" + detlarray + "detlarray leng" + detlarray.length());
                System.out.println("cuname " + cuname );
                if (detlarray.length() != 0) {
                    Detailsarraydisplay();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        if (GetRstResponse != null) {
            try {
                JSONObject json = (JSONObject) new JSONTokener(GetRstResponse.toString()).nextValue();
                JSONObject json2 = json.getJSONObject("d");

                JSONObject jsonservres = json2.getJSONObject("ServiceResponse") ;
                errmsg = (String) jsonservres.get("Message");
                result = (String) jsonservres .get("Result");
                System.out.println("errmsg is"+errmsg);

                if(result.contentEquals("FAILURE")){
                    if(errmsg.contentEquals("Invalid Session")){
                        final AlertDialog alertDialog = new AlertDialog.Builder(SecurityCheckActivity.this).create();
                        alertDialog.setTitle("Invalid Session");
                        alertDialog.setMessage("Your session is expired. Please re-login to continue");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        DonotInterruptKDCScan=false;
                                        dialog.dismiss();
                                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                                        loginPrefsEditor = loginPreferences.edit();
                                        loginPrefsEditor.commit();
                                        //loginval = "FALSE";
                                        loginPrefsEditor.putString(PREF_loginstatus, "FALSE");

                                        // loginPrefsEditor.putString("loginval", "FALSE");
                                        loginPrefsEditor.commit();
                                        // Log.e("loginval//",loginval);
                                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        // Add new Flag to start new Activity
                                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Log.e("hee","hu");
                                        getBaseContext().startActivity(intent);
                                        SecurityCheckActivity.this.finish();
                                        Log.e("here","hu");
                                        loginflag= false;
                                    }
                                });
                        alertDialog.show();
                    }else{
                        // Toast toast = Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_LONG);
                        // toast.setGravity(Gravity.CENTER, 0, 0);
                        //  toast.show();
                        // Edtrst.setText("");
                        Alert();

                    }


                }else{

                    detailsarray = (JSONArray) json2.get("Details");
                    curname = (String) json2.get("CourierName");


                    rstdate = (String) json2.get("RunsheetDate");
                    Edtcurname.setText(curname);
                    currentdate.setText(rstdate);
                    Edtcurname.setText(curname);
                    Edtcurname.setEnabled(false);
                    Edtrst.setEnabled(false);
                    int totlshp = detailsarray.length();
                    textcounttotl.setText(String.valueOf(totlshp));
                    ArrayList<String> temp = new ArrayList<String>();
                    System.out.println("detailsarray text is" + detailsarray + "Taskwabill" + respconte+"rstdate"+rstdate+"errmsg"+errmsg);
                    //  for (int i = 0; i< detailsarray.length();i++){

                    JSONObject jsonResponse;
                    String Connte = null;
                    JSONObject rstdetl = null;
                    String respconte = null, respaccno = null, respaccname = null;
                    try {
                        if (Build.MODEL.contains("SM-N")) {


                            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


                            //   lp.setMargins(18, 2, 95, 2);
                            lp.setMargins(20, 5, 200, 0);
                            //  tr.setLayoutParams(lp);
                            //  System.out.println("tr id:" + tr.getId());

                        }else {
                            lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                            //tr.setLayoutParams(lp);
                            //lp.setMargins(0, 20, 5, 0);
                         //   lp.setMargins(0,20,70, 0);
                           lp.setMargins(0,5,70,0);

                        }
                        for (int i = 0; i < detailsarray.length(); i++) {
                            tr = new TableRow(SecurityCheckActivity.this);
                            JSONObject jsonobject = detailsarray.getJSONObject(i);
                            respconte = jsonobject.getString("Connote");
                            respaccno = jsonobject.getString("ShipperAcc");
                            respaccname = jsonobject.getString("ShipperName");

                            deltxt = new TextView(SecurityCheckActivity.this);
                            deltxt.setWidth(50);
                            deltxt.setText("DEL");
                            deltxt.setVisibility(View.INVISIBLE);

                            waybilltxt = new TextView(SecurityCheckActivity.this);

                            if(Build.MODEL.contains("SM-N")){
                                waybilltxt.setLayoutParams(lp);
                            }else{
                                waybilltxt.setWidth(250);
                            }

                            waybilltxt.setText(respconte);
                            System.out.println("respconte text is" + connote + "Taskwabill" + respconte);


                            acnotxt = new TextView(SecurityCheckActivity.this);
                            acnotxt.setWidth(250);
                            acnotxt.setText(respaccno);


                            acnametxt = new TextView(SecurityCheckActivity.this);
                            acnametxt.setWidth(450);
                            acnametxt.setText(respaccname);


                            verfytxt = new TextView(SecurityCheckActivity.this);
                            if (Build.MODEL.contains("SM-N")) {
                                verfytxt.setLayoutParams(lp);
                            }else{
                                verfytxt.setWidth(50);
                            }
                            //  verfytxt.setWidth(250);
                            verfytxt.setText("N");

                            tr.addView(deltxt);
                            tr.addView(waybilltxt);
                            tr.addView(acnotxt);
                            tr.addView(acnametxt);
                            tr.addView(verfytxt);

                            resulttab.addView(tr, 0);
                        }
                        System.out.println("rstdetl tabl " + rstdetl + "Connte " + respconte + "respaccno " + respaccno + "respaccname " + respaccname);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                /// System.out.println("detailsarray are:" + detailsarray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public  void Alert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(SecurityCheckActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(errmsg);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Edtrst.setText("");
                        Edtrst.setEnabled(true);
                    }
                });
        alertDialog.show();

    }

}
