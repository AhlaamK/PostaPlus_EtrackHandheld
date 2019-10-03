package com.postaplus.etrack;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import koamtac.kdc.sdk.KDCBarcodeDataReceivedListener;
import koamtac.kdc.sdk.KDCConnectionListener;
import koamtac.kdc.sdk.KDCConstants;
import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCDataReceivedListener;
import koamtac.kdc.sdk.KDCReader;
import util.ActivityNotification;
import webservice.WebService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.postaplus.etrack.LoginActivity.PREFS_NAME;
import static com.postaplus.etrack.LoginActivity.PREF_loginstatus;
import static com.postaplus.etrack.MainActivity.barcodeReader;
import static com.postaplus.etrack.ScreenActivity.loginflag;

public class WC_MaxActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener, BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener  {
    ActivityNotification actNoty = new ActivityNotification();
    Button Btndec,Btninc,Btnclr,Btnsave;
    TextView txtcounter;
    int deccnt=0 , inccnt =0 ,totlcnt=0;
    WC_MaxActivity _activity;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    String waybill, connote;
    KDCData ScannerData;
    KDCReader _kdcReader;
    public boolean DonotInterruptKDCScan = true;
    EditText Edtreasn;
    TextView totshpmtcnt,waybilltxt,prentwctxt,incwctxt,rsntxt,Wcmaxcntxt,deltxt;
    TableRow tr,tr1;
    TableRow.LayoutParams lp;
    TableLayout resulttab;
    boolean flag = false;
    int rowid = -1;
    int countN = 0;
    public int SCANNER_REQUEST_CODE = 123;
    String camerabill,logintokn,errmsgchkwc,resultchkwc,resultUpdtWCMax,errmsgUpdtWCMax;
    String CheckWcResp,GetWcMaxResp,UpdateWCMaxResp;
    String tablwaybill,tablwcmaxcnt;
    JSONArray jsonArray = new JSONArray();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    ProgressBar Pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wc__max);
        System.out.println("barcodeReader are:" + barcodeReader);
        if(barcodeReader!= null){
            barcodeReader.addBarcodeListener(WC_MaxActivity.this);
        }
        Initializeview();
        logintokn = actNoty.getToken(WC_MaxActivity.this);

        // code for decrement button
        Btndec.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                totlcnt--;

                if(totlcnt<=0){
                    txtcounter.setText(String.valueOf(0));
                    totlcnt = 0;
                }else{
                    txtcounter.setText(String.valueOf(totlcnt));
                }

            }

        });

        // code for increment button
        Btninc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                totlcnt++;
                txtcounter.setText(String.valueOf(totlcnt));

            }

        });

        // code for clear button
        Btnclr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                Edtreasn.setText("");
                txtcounter.setText(" WC count");
                totlcnt=0;
                totshpmtcnt.setEnabled(true);
                jsonArray = new JSONArray();
                totshpmtcnt.setText("");
                System.out.println("jsonArray on save:" + jsonArray);
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();

            }

        });

//code for Save button
        Btnsave.setOnClickListener(new View.OnClickListener() {

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
                    Wcmaxcntxt =(TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);

                    tablwaybill = waybilltxt.getText().toString();
                    tablwcmaxcnt = Wcmaxcntxt.getText().toString();

                    tr = (TableRow) resulttab.getChildAt(j);

                    System.out.println("tablwcmaxcnt on trn:" + Wcmaxcntxt.getText().toString());
                    waybilltxt.setId(j);
                    tr.setId(j);



                    System.out.println("jsonArray WCMAX n:" + jsonArray);
                    // "counter:"+jsonArray.length()+"progrflag"+progrflag+ "logintokn"+logintokn);

                    try {

                        postedJSON.put("Connote", tablwaybill);
                        postedJSON.put("WcMaxCount",tablwcmaxcnt);


                        System.out.println("postedJSON obj n:" + postedJSON);

                    } catch (JSONException e) {
                        e.printStackTrace();


                    }


                    jsonArray.put(postedJSON);
                }


                System.out.println(" tr tag"+tr.getId());
                if(jsonArray.length()!= 0){
                    UpdateWCMaxResp = WebService.UpdateConnoteMaxWC(logintokn,jsonArray,Edtreasn.getText().toString());

                    System.out.println("UpdateWCMaxResp on trn:" + UpdateWCMaxResp+"jaaaray"+jsonArray+"Edtreasn are:"+Edtreasn.getText().toString().trim());
                }


                if (UpdateWCMaxResp != null) {

                    JSONObject jsonupdtWCresp = null;
                    try {
                        jsonupdtWCresp = (JSONObject) new JSONTokener(UpdateWCMaxResp.toString()).nextValue();
                        System.out.println("jsonupdtWCresp on trn:" + jsonupdtWCresp+"jaaaray"+jsonArray);
                        //  JSONObject json2 = jsonupdtWCresp.getJSONObject("d");
                        errmsgUpdtWCMax =  jsonupdtWCresp.getString("Message");
                        resultUpdtWCMax = jsonupdtWCresp .getString("Result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println("errmsgUpdtWCMax is"+errmsgUpdtWCMax);

                    if (resultUpdtWCMax.matches("SUCCESS")) {
                        v.setActivated(false);
                        v.setSelected(false);
                        resulttab.removeAllViews();
                        Edtreasn.setText("");
                        txtcounter.setText(" WC count");
                        totlcnt=0;
                        int tab = resulttab.getChildCount();
                        jsonArray = new JSONArray();
                        Toast toast = Toast.makeText(getApplicationContext(), "WC Increased Successfully", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        System.out.println("jsonArray on save:" + jsonArray);
                      /*  for (int l = 0; l < tab; l++) {

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
                            }*//* else if (!checkremartexts.getText().toString().matches("Valid")) {
                                  //  Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_SHORT);
                                  //  toast.setGravity(Gravity.CENTER, 0, 0);
                                  //  toast.show();
                                }*//*

                        }*/

                        totshpmtcnt.setText(String.valueOf(resulttab.getChildCount()));

                        for (int k = 0; k < resulttab.getChildCount(); k++) {
                            tr = (TableRow) resulttab.getChildAt(k);
                            tr.setId(k);
                            System.out.println("tr aftr del:" + tr.getId());
                        }

                        //  System.out.println("progrflag:" + progrflag);

                        // Edtrst.setText("");
                        // Edtcurname.setText("");
                        // currentdate.setText("");



                    } else {
                        if (resultUpdtWCMax.matches("FAILURE")) {
                            if(errmsgUpdtWCMax.contentEquals("Invalid Session")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(WC_MaxActivity.this).create();
                                alertDialog.setTitle("Invalid Session");
                                alertDialog.setMessage("Your session is out. Please re-login to continue");
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Edtrst.setText("");
                                                //  Edtrst.setEnabled(true);

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
                                                WC_MaxActivity.this.finish();
                                                Log.e("here","hu");
                                                loginflag= false;
                                            }
                                        });
                                alertDialog.show();

                                v.setActivated(false);
                                v.setSelected(false);
                            }
                            else if(!errmsgUpdtWCMax.contentEquals("")){
                                Toast toast = Toast.makeText(getApplicationContext(), errmsgUpdtWCMax, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                jsonArray = new JSONArray();
                                //progressBarMain.setVisibility(View.INVISIBLE);
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
                totshpmtcnt.setText(String.valueOf(resulttab.getChildCount()));




            }

        });


    }

    public void Initializeview(){
        Btndec= (Button)findViewById(R.id.buttondec) ;
        Btninc= (Button)findViewById(R.id.buttoninc) ;
        txtcounter =(TextView)findViewById(R.id.textViewcount);
        Btnclr = (Button)findViewById(R.id.buttonclear) ;
        Btnsave = (Button)findViewById(R.id.buttonsave) ;
        totshpmtcnt=(TextView)findViewById(R.id.textViewcnt);
        Edtreasn = (EditText) findViewById(R.id.edtreasn) ;
        resulttab = (TableLayout) findViewById( R .id.resulttable1);
        Pb = (ProgressBar) findViewById(R.id.progressBar);
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
            //  KDCTaskExecutable.execute();
        }
    }

    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC wc Activity BarCodeReceived Block");
        System.out.print("KDCReader WC Activity  BarCodeReceived Block");

        // if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();
            if(Edtreasn.getText().toString().trim().isEmpty()) {

                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter reason", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                return;
            }  else if (txtcounter.getText().toString().contentEquals(" WC count")||txtcounter.getText().toString().contentEquals("0")) {

                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please select wc count", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
                return;

            }

            if (Check_ValidWaybill(kdcbarcodedata.GetData()) == true) {

                System.out.println(" kdcbarcodedata ID : ");
                // System.out.println(R.id.WC_Frame);
                System.out.println(" value for kdcbarcodedata is : ");
                System.out.println(kdcbarcodedata);


                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (waybill != null) {
                            connote = waybill;
                            //  if (Validations() == true) {
                            new checkWCMaxwaybill(connote).execute();
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

        Log.i("KDCReader", "KDC wcmax Activity connection changed block");
        System.out.print("KDCReader wcmax Activity connection changed block");
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

    public static boolean Check_ValidWaybill(String s) {

        if (s.length() == 10 || s.length() == 12 || s.length() == 13) {
            // return StringUtils.isNumeric(s) == true;
            return StringUtils.isAlphanumeric(s) == true;
        } else if (s.length() == 18) {
            return StringUtils.isAlphanumeric(s) == true;
        }
        return false;
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

                    if(Edtreasn.getText().toString().trim().isEmpty()) {

                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please enter reason", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                        return;
                    }  else if (txtcounter.getText().toString().contentEquals(" WC count")||txtcounter.getText().toString().contentEquals("0")) {

                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(), "Please select wc count", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        });
                        return;

                    }

                    if (Check_ValidWaybill(honeywellevent.getBarcodeData()) == true) {

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


                                    new checkWCMaxwaybill(connote).execute();
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

            if(barcodeReader != null){
                barcodeReader.removeBarcodeListener(WC_MaxActivity.this);
                barcodeReader.release();
            }
            Intent int1 = new Intent(this, MainActivity.class);
            //  int1.putExtra("usrnamee", usrnamee);
            //  int1.putExtra("passworde", passworde);

            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class checkWCMaxwaybill extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public checkWCMaxwaybill(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;


            System.out.println("taskwaybill wc pre:" + Taskwabill);
        }

        public void onPreExecute() {

            Pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            checkWCwaybill(Taskwabill);

            return "";
        }

        private void checkWCwaybill(String wabil) {
            // TODO Auto-generated method stub
            try {
                wabil = Taskwabill;
                System.out.println("wabil val" + wabil);


                CheckWcResp = WebService.CheckConnoteMaxWC(logintokn,wabil);
                System.out.println("CheckWcResp val" + CheckWcResp);
                if (CheckWcResp != null) {
                    try {
                        JSONObject jsonchckres = (JSONObject) new JSONTokener(CheckWcResp.toString()).nextValue();

                        errmsgchkwc =  jsonchckres.getString("Message");
                        resultchkwc=jsonchckres.getString("Result");
                        System.out.println("errmsgchkwc is" + errmsgchkwc+"resultchkwc"+resultchkwc);

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

      /*  private void getWcwayCount(String wabil) {
            // TODO Auto-generated method stub
            try {
                wabil = Taskwabill;
                System.out.println("wabil val" + wabil);


             GetWcMaxResp = WebService.GetConnoteMaxWC(logintokn,wabil);
                System.out.println("GetWcMaxResp val" + GetWcMaxResp);
                if (CheckWcResp != null) {
                    try {
                        JSONObject jsonmaxwcres = (JSONObject) new JSONTokener(GetWcMaxResp.toString()).nextValue();

                        System.out.println("jsonmaxres is" + jsonmaxwcres);

                        errmsgetMaxwc =  jsonmaxwcres.getString("Message");
                        resultgetMaxwc = (String) jsonmaxwcres.get("Result");
                        System.out.println("errmsgetMaxwc is" + errmsgetMaxwc);

                    }catch (Exception e ){


                    }

                }


                //  JSONObject jsnobj = JSONObject

            } catch (Exception e) {
                //Log.e("EventActivity:", e.getMessage().toString());
                //Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }*/

        @Override
        public void onPostExecute(String res) {
            //response=null;

            Pb.setVisibility(View.INVISIBLE);

            tr = new TableRow(WC_MaxActivity.this);

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


            deltxt = new TextView(WC_MaxActivity.this);
            // deltxt.setLayoutParams(lp);
            deltxt.setWidth(80);
            // deltxt.setText("Delete");
            deltxt.setText("Del");

            if(resultchkwc.contentEquals("SUCCESS")) {

                waybilltxt = new TextView(WC_MaxActivity.this);
                waybilltxt.setLayoutParams(lp);
                //  waybilltxt.setWidth(50);
                waybilltxt.setText(Taskwabill);
                System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);


                prentwctxt = new TextView(WC_MaxActivity.this);
                prentwctxt.setLayoutParams(lp);
                prentwctxt.setWidth(50);
                prentwctxt.setText(errmsgchkwc);


                incwctxt = new TextView(WC_MaxActivity.this);
                incwctxt.setLayoutParams(lp);
                //   incwctxt.setWidth(10);
                incwctxt.setText(txtcounter.getText().toString());



            /*  rsntxt = new TextView(WC_MaxActivity.this);
              rsntxt.setLayoutParams(lp);
              // rsntxt.setWidth(10);
              rsntxt.setText(Edtreasn.getText().toString());*/





                tr.addView(deltxt);
                tr.addView(waybilltxt);
                tr.addView(prentwctxt);
                tr.addView(incwctxt);



                System.out.println("res wc chld:" + resulttab.getChildCount());

                if (resulttab.getChildCount() == 0) {
                    resulttab.addView(tr, 0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    //	counttxt.setText(String.valueOf(resulttab.getChildCount()));
                    //	System.out.println("value of counttxt in pickup update"+counttxt.toString());

                    String wb = null;
                    String verytxt = null;
                    TextView deltextt = null;
                    for (int i = 0; i < resulttab.getChildCount(); i++) {

                        TextView wbill = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(1);
                        wb = wbill.getText().toString();

                        System.out.println("wb wc wal:" + wb + "Twaybill wc is:" + Taskwabill);
                        if (!Taskwabill.equals(wb)) {
                            flag = true;

                        } else if (Taskwabill.equals(wb)) {
                            flag = false;
                            System.out.println(flag);


                            break;

                        }
                    }

                    if (flag) {
                        resulttab.addView(tr, 0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        //tr.setBackgroundColor(Color.parseColor("#d73232"));
                    } else {
                        System.out.println("value of counttxt in else" + countN);

                        countN = countN - 1;


                        //   textcount1.setText(String.valueOf(resulttab.getChildCount()));

                        if (Taskwabill.equals(wb)) {
                            //  verfytxt.setText("Y");
                            //  tr.setBackgroundColor(Color.parseColor("#14FF3D"));
                            Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            //  textcount2.setText(String.valueOf(valdcnt = valdcnt - 1));
                            Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                            return;
                        }


                    }


                    // Pb.setVisibility(View.INVISIBLE);


                }

                System.out.println("text tot valid" + resulttab.getChildCount());
                //textcount1.setText(String.valueOf(resulttab.getChildCount()));
// commented byAK 13MAY18 avoid delete
                tr.setOnClickListener(new View.OnClickListener() {

                                          @Override
                                          public void onClick(View v) {
                                           System.out.println("v val on clic n  del:"+v);
                                              System.out.println("Rowid:" + rowid + ",v.getID:" + v.getId());
                                              if (rowid != v.getId()) {
                                                  rowid = v.getId();
                                                  Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                                              } else if (rowid == v.getId()) {
                                                  System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());

                                                  resulttab.removeViewAt(v.getId());

                                                  //   System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                                                  Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                                                  rowid = -1;
                                                  totlcnt = totlcnt - 1;

                                                  int tabcount = (resulttab.getChildCount());
                                                  for (int i = 0; i < tabcount; i++) {
                                                      tr = (TableRow) resulttab.getChildAt(i);
                                                      tr.setId(i);
                                                      System.out.println("tr aftr del:" + tr.getId());
                                                  }


                                                  //textcount1.setText(String.valueOf(resulttab.getChildCount()));
                                                  totshpmtcnt.setText(String.valueOf(resulttab.getChildCount()));
                                              }
                                          }
                                          // }
                                      }
                );
            }else {

                Toast toast = Toast.makeText(getApplicationContext(), errmsgchkwc, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //return;

            }
            int tabcount = (resulttab.getChildCount());
            for (int k = 0; k < tabcount; k++) {
                tr = (TableRow) resulttab.getChildAt(k);
                tr.setId(k);
                System.out.println("tr aftr del:" + tr.getId());
            }
            totshpmtcnt.setText(String.valueOf(resulttab.getChildCount()));

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("KDCReader on wc actvity While Pause : " + _kdcReader);
        if (!isActivityActiveFlag) isActivityActiveFlag = false;

     /*   if (ThrKdc != null) {
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
            System.out.println("THRKDC in pause activated on" + ThrKdc);
        }
*/

        if (!DonotInterruptKDCScan) {
            System.out.println("KDCReader on wc  While Pause : " + _kdcReader);
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
        } else {
            DonotInterruptKDCScan = false;
        }

        if(barcodeReader != null){
             barcodeReader.removeBarcodeListener(WC_MaxActivity.this);
        }
    //_activity.finish();
}

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("barcode1 wc onres:" + barcodeReader);
        if (barcodeReader != null) {
            try {

                barcodeReader.claim();
                System.out.println("barcode wc onres:" + barcodeReader);
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


        System.out.println("Resume activate in wc");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent

            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");

                camerabill = contents;


                if (Check_ValidWaybill(camerabill) == true) {
                    connote = camerabill;
                    new checkWCMaxwaybill(camerabill).execute();
                } else {
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:

                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;
                    if(Edtreasn.getText().toString().trim().isEmpty()) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter reason ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }else  if(txtcounter.getText().toString().contentEquals(" WC count")||txtcounter.getText().toString().contentEquals("0")){


                        Toast toast = Toast.makeText(getApplicationContext(), "Please select wc count", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }else {
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);

                    }
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;

                    if(Edtreasn.getText().toString().trim().isEmpty()) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter reason ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else if (txtcounter.getText().toString().contentEquals(" WC count")||txtcounter.getText().toString().contentEquals("0")) {


                        Toast toast = Toast.makeText(getApplicationContext(), "Please select wc count", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }

                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
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

        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(barcodeReader != null){
                barcodeReader.removeBarcodeListener(WC_MaxActivity.this);
                barcodeReader.release();
                WC_MaxActivity.this.finish();
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
                WC_MaxActivity.this.finish();
                Intent int1 = new Intent(this, MainActivity.class);
                // int1.putExtra("usrnamee", usrnamee);
                // int1.putExtra("passworde", passworde);
                startActivity(new Intent(int1));
                return true;
            }

        }
        return false;
    }

}
