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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

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

public class ManifestActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener ,BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener {
    KDCData ScannerData;
    KDCReader _kdcReader;
    public boolean DonotInterruptKDCScan = true;
    String waybill, connote,camerabill;
    ManifestActivity _activity;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    EditText Edtmanfstno,Edtbagno;
    TableLayout resulttab;
    boolean flag = false;
    int rowid = -1;
    int countN = 0;
    public int SCANNER_REQUEST_CODE = 123;
    ProgressBar Pb;
    Button BtnSave, BtnClear ,BtnSearch ;
    TableRow tr,trmanft;
    TextView txtcnttotl;
    TableRow.LayoutParams lp;
    String CheckManicntResp,SetmnfstResp,DelBagcnntRsp;
    String logintokn;
    ActivityNotification actNoty = new ActivityNotification();
    String resultchkmnftbag,errmsgckmnftbag;
    TextView waybilltxt,bagtxt,deltxt,delawb,delawbbag,Tablawb;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    String Geterrmsg,Getresult;
    JSONArray connotearray;
    int serlcnt=0;
    String mnfststtas,manfstresult,mnfstmsg;
    JSONObject GetManftBagResp;
    String delbagmsg, delrslt ,delstatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest);
        InitializeViews();



        // code for clear button
        BtnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                Edtbagno.setText("");
                Edtmanfstno.setText("");
                Edtbagno.setEnabled(true);
                Edtmanfstno.setEnabled(true);
                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();


            }

        });



        // code for search button
        BtnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                GetManftAWbs();
                //new GetMnftAwbTask().execute();

            }

        });

        // code for save button
        BtnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
               // resulttab.removeAllViews();
                System.out.println("Edtmanfstno trm:"+Edtmanfstno.getText().toString().toUpperCase().replaceAll(" ",""));
                SetmnfstResp= WebService.SetManifest(logintokn,Edtmanfstno.getText().toString().trim().toUpperCase().replaceAll(" ","") );
                System.out.println("Set manfts:"+SetmnfstResp);

                if (SetmnfstResp != null) {
                    try {
                        JSONObject jsonsetres = (JSONObject) new JSONTokener(SetmnfstResp.toString()).nextValue();
                        System.out.println("jsonsetres is" + jsonsetres);
                        // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                        //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                        mnfststtas = jsonsetres.getString("Status");
                        manfstresult = (String) jsonsetres.get("Result");
                        mnfstmsg = jsonsetres.getString("Message");
                        System.out.println("manfstresult is" + manfstresult + "mnfststtas are " + mnfststtas);

                    } catch (Exception e) {


                    }
                }

                if(mnfststtas!=null){
                    if(mnfststtas.contentEquals("SUCCESS")) {
                      //  Edtbagno.setText(resultbagno);
                       // Edtbagno.setEnabled(false);
                        resulttab.removeAllViews();
                        Edtbagno.setText("");
                        Edtbagno.setEnabled(true);
                        Edtmanfstno.setText("");
                        Edtmanfstno.setEnabled(true);

                        Toast toast = Toast.makeText(getApplicationContext(), mnfstmsg, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        v.setActivated(false);
                        v.setSelected(false);
                    } else {
                        v.setActivated(false);
                        v.setSelected(false);


                        if (mnfstmsg.contentEquals("Invalid Session")) {

                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                            alertDialog.setTitle("Invalid Session");
                            alertDialog.setMessage("Your session is expired. Please re-login to continue");
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            DonotInterruptKDCScan = false;
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
                                            Log.e("hee", "hu");
                                            getBaseContext().startActivity(intent);
                                            ManifestActivity.this.finish();
                                            Log.e("here", "hu");
                                            loginflag = false;
                                        }
                                    });
                            alertDialog.show();
                        } else {



                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();

                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage(mnfstmsg);


                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            // Edtrst.setText("");
                                            // Edtrst.setEnabled(true);
                                        }
                                    });
                            alertDialog.show();
                        }

                        System.out.println("mnfstmsg is" + mnfstmsg);
                        //  Toast toast = Toast.makeText(getApplicationContext(),consbagmsg, Toast.LENGTH_LONG);
                        // toast.setGravity(Gravity.CENTER, 0, 0);
                        // toast.show();


                        //Toast toast = Toast.makeText(getApplicationContext(), msgbagconso, Toast.LENGTH_LONG);
                        // toast.setGravity(Gravity.CENTER, 0, 0);
                        // toast.show();
                    }

                }
                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));

                // if(progrflag==resulttab.getChildCount()){
                // progressBarMain.setVisibility(View.INVISIBLE);
                v.setActivated(false);
                v.setSelected(false);
            }


        });
                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));



            }

    public void InitializeViews(){
        Edtmanfstno = (EditText) findViewById(R.id.edittxtmanifstno);
        resulttab = (TableLayout) findViewById(R.id.resulttable1);
        Pb = (ProgressBar)findViewById(R.id.progressBar);
        BtnSave = (Button)findViewById(R.id.btnsave);
        BtnClear = (Button)findViewById(R.id.btnclear);
        BtnSearch = (Button) findViewById(R.id.btnsearch);
        txtcnttotl = (TextView)findViewById(R.id.txtvcountshpmt);
        logintokn = actNoty.getToken(ManifestActivity.this);
        Edtbagno = (EditText) findViewById(R.id.edittxtbagno);

        if (barcodeReader != null) {
            barcodeReader.addBarcodeListener(ManifestActivity.this);
        }

    }

    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC manfst Activity BarCodeReceived Block");
        System.out.print("KDCReader manfst Activity  BarCodeReceived Block");

        // if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();
            if (Edtmanfstno.getText().toString().trim().isEmpty()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtmanfstno.setText(waybill);
                        Edtmanfstno.setEnabled(false);
                       // GetBagAWbs();
                    }
                });
                return;
            }else   if (Edtbagno.getText().toString().trim().isEmpty()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtbagno.setText(waybill);
                        Edtbagno.setEnabled(false);
                       GetManftAWbs();
                      //  new GetMnftAwbTask().execute();
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
                            //if (Validations() == true) {
                          new CheckManifstTask(connote).execute();
                        }

                        // }
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
    public void ConnectionChanged(BluetoothDevice device, int state) {
        //ToDo Auto-generated method stub

        Log.i("KDCReader", "KDC manfst Activity connection changed block");
        System.out.print("KDCReader manfst Activity connection changed block");
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
    public void onPause() {
        super.onPause();
        System.out.println("KDCReader on manfst actvity While Pause : " + _kdcReader);

        if(barcodeReader != null){
             barcodeReader.removeBarcodeListener(ManifestActivity.this);
            barcodeReader.release();
        }else
        if (!isActivityActiveFlag) isActivityActiveFlag = false;

     /*   if (ThrKdc != null) {
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
            System.out.println("THRKDC in pause activated on" + ThrKdc);
        }
*/

        if (!DonotInterruptKDCScan) {
            System.out.println("KDCReader on manfst  While Pause : " + _kdcReader);
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
        } else {
            DonotInterruptKDCScan = false;
        }


        //_activity.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("barcode1 mnfst onres:" + barcodeReader);
        if (barcodeReader != null) {
            try {

                barcodeReader.claim();
                System.out.println("barcode mnfst  onres:" + barcodeReader);
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


        System.out.println("Resume activate in mfst");
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent honeywllevent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                System.out.println("brcde activate in mfst"+honeywllevent.getBarcodeData());
                if (Edtmanfstno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtmanfstno.setText(honeywllevent.getBarcodeData());
                            Edtmanfstno.setEnabled(false);

                        }
                    });
                    return;
                }else   if (Edtbagno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtbagno.setText(honeywllevent.getBarcodeData());
                            Edtbagno.setEnabled(false);
                            GetManftAWbs();
                        }
                    });
                    return;
                }
                if (honeywllevent != null) {

                    //ScannerData = honeywellevent;
                    waybill = honeywllevent.getBarcodeData();


                    if (Check_ValidWaybill(honeywllevent.getBarcodeData()) == true) {

                        System.out.println("honeywllevent ID : ");
                        // System.out.println(R.id.WC_Frame);
                        System.out.println(" value for honeywllevent is : ");
                        System.out.println(honeywllevent);


                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (waybill != null) {
                                    connote = waybill;
                                   // if (Validations() == true) {

                                        new CheckManifstTask(connote).execute();

                                        //Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG).show();
                                 //   }
                                }    // wbilldata1=contents;

                            }
                        });

                    } else {

                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    _activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent

            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");

                camerabill = contents;
                if (Edtmanfstno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtmanfstno.setText(camerabill);
                            Edtmanfstno.setEnabled(false);
                            // GetBagAWbs();
                        }
                    });
                    return;
                }else   if (Edtbagno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtbagno.setText(camerabill);
                            Edtbagno.setEnabled(false);
                            GetManftAWbs();
                        }
                    });
                    return;
                }

                if (Check_ValidWaybill(camerabill) == true) {
                    connote = camerabill;
                    new CheckManifstTask(camerabill).execute();
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
                 /*   if(Edtreasn.getText().toString().trim().isEmpty()) {

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
*/
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    System.out.println("reslt val:"+Activity.RESULT_OK);
                    startActivityForResult(intent, SCANNER_REQUEST_CODE);
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;

                  /*  if(Edtreasn.getText().toString().trim().isEmpty()) {

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
                    }*/


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
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Log.e("TouchTest", "Touch down");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e("TouchTest", "Touch up");
        }

        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(barcodeReader!=null){
                barcodeReader.release();
                barcodeReader.removeBarcodeListener(_activity);
                Intent int1 = new Intent(this, MainActivity.class);
                //int1.putExtra("usrnamee", usrnamee);
                //int1.putExtra("passworde", passworde);
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
                ManifestActivity.this.finish();
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
            if (barcodeReader != null) {
                barcodeReader.removeBarcodeListener(ManifestActivity.this);
                barcodeReader.release();

            }
            Intent int1 = new Intent(this, MainActivity.class);
            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CheckManifstTask extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public CheckManifstTask(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;


            System.out.println("taskwaybill mnfst pre:" + Taskwabill);
        }

        public void onPreExecute() {

            Pb.setVisibility(View.VISIBLE);
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


                // CheckcntbagResp = WebService.CheckBagconnote(logintokn,wabil,resultbagno);
                CheckManicntResp = WebService.CheckManifestConnote(logintokn, wabil, Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase());
                System.out.println("CheckManicntResp val" + CheckManicntResp);
                if (CheckManicntResp != null) {
                    try {
                        JSONObject jsonchckmnftres = (JSONObject) new JSONTokener(CheckManicntResp.toString()).nextValue();
                        System.out.println("jsonchckmnftres is" + jsonchckmnftres);
                        // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                        //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                        errmsgckmnftbag = jsonchckmnftres.getString("Message");
                        resultchkmnftbag = (String) jsonchckmnftres.get("Result");
                        System.out.println("errmsgckmnftbag is" + errmsgckmnftbag + "resultchkmnftbag" + resultchkmnftbag);

                    } catch (Exception e) {


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

            Pb.setVisibility(View.INVISIBLE);

            tr = new TableRow(ManifestActivity.this);

          /*  if(errmsgchk.matches("VALID")){

                verfytxt.setText("Y");
                tr.setBackgroundColor(Color.parseColor("#14FF3D"));
            }else*/

            if (Build.MODEL.contains("SM-N")) {
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
                //   lp.setMargins(18, 2, 95, 2);
                lp.setMargins(10, 5, 200, 0);

                tr.setLayoutParams(lp);
                System.out.println("tr id:" + tr.getId());

            } else {
                lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                //tr.setLayoutParams(lp);
                //lp.setMargins(0, 20, 5, 0);
                lp.setMargins(0, 5, 40, 0);
                tr.setLayoutParams(lp);

            }

            //	System.out.println("response : " + response.toString());


            if (resultchkmnftbag.contentEquals("SUCCESS")) {



                deltxt = new TextView(ManifestActivity.this);
                deltxt.setLayoutParams(lp);
                //deltxt.setWidth(120);
               // deltxt.setWidth(250);
                // deltxt.setText("Delete");
                deltxt.setText("DEL");



                bagtxt = new TextView(ManifestActivity.this);
                bagtxt.setLayoutParams(lp);
                // remaktxt.setWidth(120);
                bagtxt.setText(Edtbagno.getText().toString());


                waybilltxt = new TextView(ManifestActivity.this);
                waybilltxt.setLayoutParams(lp);
                //  waybilltxt.setWidth(50);
                waybilltxt.setText(Taskwabill);
                System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);

              /*  remaktxt = new TextView(ManifestActivity.this);
                remaktxt.setLayoutParams(lp);
                // remaktxt.setWidth(120);
                remaktxt.setText(errmsgchkbag);
*/

                //  System.out.println("cnametxt  is"+cname1);


                tr.addView(deltxt);
                tr.addView(bagtxt);
                tr.addView(waybilltxt);



                System.out.println("res mnfst chld:" + resulttab.getChildCount());

                if (resulttab.getChildCount() == 0) {
                    resulttab.addView(tr, 0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                     tr.setBackgroundColor(Color.parseColor("#eb7a02"));

                } else {
                    //	counttxt.setText(String.valueOf(resulttab.getChildCount()));
                    //	System.out.println("value of counttxt in pickup update"+counttxt.toString());

                    String wb = null;
                    String verytxt = null;
                    TextView deltextt = null;
                    for (int i = 0; i < resulttab.getChildCount(); i++) {

                        TextView wbill = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(2);
                        wb = wbill.getText().toString();

                        System.out.println("wb wc wal:" + wb + "Twaybill wc is:" + Taskwabill);
                        if (!Taskwabill.equals(wb)) {
                            flag = true;

                        } else if (Taskwabill.equals(wb)) {
                            flag = false;
                            System.out.println(flag);
                            TextView vertxt = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(4);
                            deltextt = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(1);
                       /* if(errmsgchk.matches("VALID")) {
                            verytxt = vertxt.getText().toString();
                            tr1 = ((TableRow) resulttab.getChildAt(i));
                            tr1.setBackgroundColor(Color.parseColor("#32CD32"));
                            vertxt.setText("Y");
                        }*/

                            break;

                        }
                    }

                    if (flag) {
                        resulttab.addView(tr, 0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.setBackgroundColor(Color.parseColor("#eb7a02"));
                        //  tr.setBackgroundColor(Color.parseColor("#eb7a02"));
                       /* List<String> a = new ArrayList<String>();
                        a.add(Taskwabill);
                        // a.add("pp");


                        String[] myArray = new String[a.size()];
                        a.toArray(myArray);

                        System.out.println("my array s"+a);*/

                    } else {
                        System.out.println("value of counttxt in else" + countN);

                        countN = countN - 1;


                        //   textcount1.setText(String.valueOf(resulttab.getChildCount()));

                        if (Taskwabill.equals(wb)) {
                            // verfytxt.setText("Y");
                            // tr.setBackgroundColor(Color.parseColor("#14FF3D"));
                            Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                           // serlcnt = serlcnt - 1;
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
                //  AwbLIST.add(Taskwabill,0);
              //  AwbLIST.add(0,Taskwabill);
                ArrayList<String> TotalList = new ArrayList();
              /*  if(connotearray!= null){
                    if(connotearray.length()!=0){
                        for (int i = 0; i < connotearray.length(); i++) {
                            try {
                                TotalList.add(connotearray.getString(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        AwbLIST.addAll(TotalList);

                    }
                }*/
             /*   AwbLIST = new ArrayList<String>(new LinkedHashSet<String>(AwbLIST));
                System.out.println("AwbLIST  all is" +  AwbLIST);
                AwbArray = new String[AwbLIST.size()];
                AwbLIST.toArray(AwbArray);
                System.out.println("AwbLIST size is" +  AwbLIST.size());*/

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
                                delawb = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);

                                DelBagcnntRsp = WebService.DeleteManifestBagconnote(logintokn, delawb.getText().toString(), Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase().replaceAll(" ",""));

                                System.out.println("delawb are:" + delawb.getText().toString());

                               if (DelBagcnntRsp != null) {
                                    try {
                                        JSONObject jsondelres = (JSONObject) new JSONTokener(DelBagcnntRsp.toString()).nextValue();
                                        System.out.println("jsondelres is" + jsondelres);
                                        // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                                        //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                                        delbagmsg = jsondelres.getString("Message");
                                        delrslt = (String) jsondelres.get("Result");
                                        delstatus = (String) jsondelres.get("Status");

                                        System.out.println("delbagmsg is" + delbagmsg );

                                    } catch (Exception e) {


                                    }

                                }

                                if (delstatus.contentEquals("SUCCESS")) {
                                    // resulttab.removeViewAt(v.getId());
                                    txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));

                                    /*Dellist = new ArrayList<String>();
                                    // JSONArray jsonArray = (JSONArray)jsonObject;
                                    int len = AwbLIST.size();
                                    if (AwbLIST != null) {
                                        for (int i = 0; i < len; i++) {

                                            Dellist.add(AwbLIST.get(i));

                                        }
                                    }*/
//Remove the element from arraylist
                                    // Dellist.remove(v.getId());
                                  //  System.out.println("AwbLIST befr del:" + AwbLIST);
                                  //  AwbLIST.remove(v.getId());
                                  //  System.out.println("AwbLIST aftr del:" + AwbLIST);
//Recreate JSON Array
                                 //   AwbArray = new String[AwbLIST.size()];
                                  //  txtcnttotl.setText(String.valueOf(AwbArray.length));

                                  //  System.out.println("AwbArray nrml refr is" + AwbArray.length+"v.getId()"+v.getId());

                                   // System.out.println("Awblist aftr del "+AwbLIST+"list are"+Dellist);

                                    System.out.println("v was getId:"+v.getId()+"Tr");
                                    //resulttab.removeViewAt(v.getId());
                                    resulttab.removeViewAt(v.getId());
                                    //resulttab.removeAllViews();
                                  //  txtcnttotl.setText(String.valueOf(AwbArray.length));
                                    serlcnt = 0;
                                  //  TableReseq();
                                  //  Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                } else {
                                 //   Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                }

                                //resulttab.removeViewAt(v.getId());
                                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                                //  resulttab.removeViewAt(v.getId());

                                //   System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                                //  Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
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
                    // }
                });

                int tabcount = (resulttab.getChildCount());
                for (int k = 0; k < tabcount; k++) {
                    tr = (TableRow) resulttab.getChildAt(k);
                    tr.setId(k);
                    System.out.println("tr aftr del:" + tr.getId());
                }
                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
            } else {

                if (errmsgckmnftbag.contentEquals("Invalid Session")) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                    alertDialog.setTitle("Invalid Session");
                    alertDialog.setMessage("Your session is expired. Please re-login to continue");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    DonotInterruptKDCScan = false;
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
                                    Log.e("hee", "hu");
                                    getBaseContext().startActivity(intent);
                                    ManifestActivity.this.finish();
                                    Log.e("here", "hu");
                                    loginflag = false;
                                }
                            });
                    alertDialog.show();
                } else {

                    Alert();
                }
            }
        }


    }

    public void Alert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
        alertDialog.setTitle("Alert");
       /* if (CBFindawb.isChecked()) {
            alertDialog.setMessage(errmsgchkbag);
        } else if (CBFindBag.isChecked()) {
            alertDialog.setMessage(Geterrmsg);
        } else if (CBShpmnt.isChecked()) {

        }*/
        alertDialog.setMessage(errmsgckmnftbag);

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Edtrst.setText("");
                        // Edtrst.setEnabled(true);
                    }
                });
        alertDialog.show();

    }

    public void GetManftAWbs(){

        GetManftBagResp = WebService.GetManifestBagConnote(logintokn, Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase().replaceAll(" ",""));

        System.out.println("GetManftBagResp for bag:" + GetManftBagResp);


        if (GetManftBagResp != null) {
            resulttab.removeAllViews();
            Edtmanfstno.setEnabled(false);
            Edtbagno.setEnabled(false);
          //  serlcnt = 0;
            try {
                JSONObject json = (JSONObject) new JSONTokener(GetManftBagResp.toString()).nextValue();
                JSONObject json2 = json.getJSONObject("d");

                JSONObject jsonservres = json2.getJSONObject("ServiceResponse");
                System.out.println("jsonservres is" + jsonservres);
                Geterrmsg = jsonservres.getString("Message");
                Getresult = jsonservres.getString("Result");
                System.out.println("Geterrmsg is" + Geterrmsg);
                System.out.println("Getresult is" + Getresult);
                if (Getresult.contentEquals("FAILURE")) {
                    if (Geterrmsg.contentEquals("Invalid Session")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                        alertDialog.setTitle("Invalid Session");
                        alertDialog.setMessage("Your session is expired. Please re-login to continue");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        DonotInterruptKDCScan = false;
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
                                        Log.e("hee", "hu");
                                        getBaseContext().startActivity(intent);
                                        ManifestActivity.this.finish();
                                        Log.e("here", "hu");
                                        loginflag = false;
                                    }
                                });
                        alertDialog.show();
                    } else {
                        // Toast toast = Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_LONG);
                        // toast.setGravity(Gravity.CENTER, 0, 0);
                        //  toast.show();
                        // Edtrst.setText("");
                        final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                        alertDialog.setTitle("Alert");

                        alertDialog.setMessage(Geterrmsg);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // Edtrst.setText("");
                                        // Edtrst.setEnabled(true);
                                    }
                                });
                        alertDialog.show();


                    }


                } else {
                    System.out.println("connotearray else text is" + connotearray);
                  //  if (connotearray!=null) {

                        connotearray = (JSONArray) json2.get("Connote");
                        int totlawbs = connotearray.length();
                        txtcnttotl.setText(String.valueOf(totlawbs));
                        ArrayList<String> temp = new ArrayList<String>();
                        System.out.println("connotearray text is" + connotearray);
                        //  for (int i = 0; i< detailsarray.length();i++){

                        JSONObject jsonResponse;
                        String Connte = null;
                        JSONObject rstdetl = null;
                        String respconte = null, respaccno = null, respaccname = null;
                        trmanft = new TableRow(ManifestActivity.this);
                        try {
                            if (Build.MODEL.contains("SM-N")) {


                                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                                trmanft.setId((resulttab.getChildCount()));
                                //   lp.setMargins(18, 2, 95, 2);
                                lp.setMargins(10, 5, 200, 0);
                                trmanft.setLayoutParams(lp);
                                System.out.println("trmanft id:" + trmanft.getId());

                            } else {
                                lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                                trmanft.setLayoutParams(lp);
                                //lp.setMargins(0, 20, 5, 0);
                                //   lp.setMargins(0,20,70, 0);
                                lp.setMargins(0, 5, 70, 0);

                            }
                            for (int i = 0; i < connotearray.length(); i++) {
                                trmanft = new TableRow(ManifestActivity.this);
                                final String connoteresp = connotearray.getString(i);
                                System.out.println("connoteresp text is" + connoteresp);
                                //  serlcnt++;

                                deltxt = new TextView(ManifestActivity.this);
                                deltxt.setWidth(120);
                                deltxt.setLayoutParams(lp);
                            /*deltxt.setLayoutParams(lp);
                            deltxt.setWidth(120);*/
                                deltxt.setText("DEL");
                                deltxt.setVisibility(View.VISIBLE);


                                bagtxt = new TextView(ManifestActivity.this);
                                bagtxt.setLayoutParams(lp);
                                // remaktxt.setWidth(120);
                                bagtxt.setText(Edtbagno.getText().toString());


                                waybilltxt = new TextView(ManifestActivity.this);

                                if (Build.MODEL.contains("SM-N")) {
                                    waybilltxt.setLayoutParams(lp);
                                } else {
                                    waybilltxt.setWidth(250);
                                }

                                waybilltxt.setText(connoteresp);
                                System.out.println("connoteresp text is" + connoteresp + "Taskwabill" + connoteresp);

                          /*  remaktxt = new TextView(ManifestActivity.this);
                            remaktxt.setLayoutParams(lp);
                            // remaktxt.setWidth(120);
                            remaktxt.setText(errmsgchkbag);*/

                                trmanft.addView(deltxt);
                                trmanft.addView(bagtxt);
                                trmanft.addView(waybilltxt);

                          /*  if(Geterrmsg!=null){
                                tr.addView(remaktxt);
                            }*/
                                resulttab.addView(trmanft, 0);
                                // bagsercnt++;

                                final int finalI = i;


                                final int finalI1 = i;
                                trmanft.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    System.out.println("v val:" + v);
                                    System.out.println("Rowid bag:" + rowid + ",v.getID:" + v.getId());
                                    if (rowid != v.getId()) {
                                        rowid = v.getId();
                                        Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                                    } else if (rowid == v.getId()) {
                                        System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());


                                        if (resulttab.getChildCount() != 0) {
                                            delawbbag = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                                            System.out.println("delawbbag org are:" + delawbbag);

                                            DelBagcnntRsp = WebService.DeleteManifestBagconnote(logintokn, delawbbag.getText().toString(), Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase().replaceAll(" ",""));

                                            System.out.println("DelBagcnntRsp are:" + DelBagcnntRsp);

                                            if (DelBagcnntRsp != null) {
                                                try {
                                                    JSONObject jsondelres = (JSONObject) new JSONTokener(DelBagcnntRsp.toString()).nextValue();
                                                    System.out.println("jsondelres is" + jsondelres);
                                                    // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                                                    //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                                                    delbagmsg = jsondelres.getString("Message");
                                                    delrslt = (String) jsondelres.get("Result");
                                                    delstatus = (String) jsondelres.get("Status");

                                                   // System.out.println("delbagmsg is" + delbagmsg );

                                                } catch (Exception e) {


                                                }

                                            }

                                            if (delstatus.contentEquals("SUCCESS")) {
                                               // System.out.println("delID is" + v.getId());
                                                //resulttab.removeViewAt(v.getId());
                                                // resulttab.refreshDrawableState();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    // connotearray.remove(v.getId());
                                                    ArrayList<String> list = new ArrayList<String>();
                                                    // JSONArray jsonArray = (JSONArray)jsonObject;
                                                    int len = connotearray.length();
                                                    if (connotearray != null) {
                                                        for (int i = 0; i < len; i++) {
                                                            try {
                                                                Tablawb = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                                                                list.add(connotearray.getString(i));
                                                                // list.add(0,Tablawb.getText().toString());
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        System.out.println("Tablawb new aftr del is" + Tablawb.getText().toString());
                                                    }
//Remove the element from arraylist
                                                    System.out.println("list befre are:"+list);
                                                   // list.remove(finalI);
                                                    connotearray.remove(finalI1);
                                                    System.out.println("list  after are:"+list);
//Recreate JSON Array
                                                    try {
                                                        connotearray = new JSONArray(connotearray);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    System.out.println("updatd cnte arr are:"+list);
                                                    //   AwbLIST.addAll(list);
                                                    // AwbLIST.add(0,Tablawb.g);

                                                }
                                                //AwbLIST = new ArrayList<String>(new LinkedHashSet<String>(AwbLIST));
                                                System.out.println("connotearray new aftr del is" + connotearray);
                                              //  System.out.println("AwbLIST new aftr del is" + AwbLIST);
                                                //  resulttab.removeAllViews();

                                                serlcnt = 0;
                                                resulttab.removeViewAt(v.getId());
                                               // resulttab.removeAllViews();
                                                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                                                Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                                //  GetBagAWbs();

                                             //   RefreshTable();

                                            } else {

                                                if (delbagmsg.contentEquals("Invalid Session")) {
                                                    final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                                    alertDialog.setTitle("Invalid Session");
                                                    alertDialog.setMessage("Your session is expired. Please re-login to continue");
                                                    alertDialog.setCanceledOnTouchOutside(false);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    DonotInterruptKDCScan = false;
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
                                                                    Log.e("hee", "hu");
                                                                    getBaseContext().startActivity(intent);
                                                                    ManifestActivity.this.finish();
                                                                    Log.e("here", "hu");
                                                                    loginflag = false;
                                                                }
                                                            });
                                                    alertDialog.show();
                                                } else {
                                                    final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                                    alertDialog.setTitle("Alert");

                                                    alertDialog.setMessage(delbagmsg);
                                                    alertDialog.setCanceledOnTouchOutside(false);
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    // Edtrst.setText("");
                                                                    // Edtrst.setEnabled(true);
                                                                }
                                                            });
                                                    alertDialog.show();

                                                }
                                            }

                                            //  resulttab.removeViewAt(v.getId());

                                            //   System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                                            //  Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                                            rowid = -1;
                                            int tabcount = (resulttab.getChildCount());
                                            //  serlcnt=tabcount;
                                            for (int i = 0; i < tabcount; i++) {
                                                trmanft = (TableRow) resulttab.getChildAt(i);
                                                trmanft.setId(i);

                                                System.out.println("trmanft aftr del:" + trmanft.getId());
                                                //RefreshTable();
                                            }


                                            //textcount1.setText(String.valueOf(resulttab.getChildCount()));

                                        }

                                    }
                                }
                                // }
                            });


                            }

                            rowid = -1;
                            int tabcount = (resulttab.getChildCount());

                            for (int i = 0; i < tabcount; i++) {

                                trmanft = (TableRow) resulttab.getChildAt(i);
                                trmanft.setId(i);

                                // srnltxt.setText(String.valueOf(trbag.getId()));

                                System.out.println("srnltxt aftr del:" + trmanft.getId());
                            }


                            System.out.println("rstdetl tabl " + rstdetl + "Connte " + respconte + "respaccno " + respaccno + "respaccname " + respaccname);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                   // }
                 /*       else{
                        if (Geterrmsg.contentEquals("Invalid Session")) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                            alertDialog.setTitle("Invalid Session");
                            alertDialog.setMessage("Your session is expired. Please re-login to continue");
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            DonotInterruptKDCScan = false;
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
                                            Log.e("hee", "hu");
                                            getBaseContext().startActivity(intent);
                                            ManifestActivity.this.finish();
                                            Log.e("here", "hu");
                                            loginflag = false;
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                            alertDialog.setTitle("Alert");

                            alertDialog.setMessage(Geterrmsg);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            // Edtrst.setText("");
                                            // Edtrst.setEnabled(true);
                                        }
                                    });
                            alertDialog.show();

                        }
                    }*/
                }
                /// System.out.println("detailsarray are:" + detailsarray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Edtbagno.setEnabled(false);

        }



    }

    public class GetMnftAwbTask extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";



        public void onPreExecute() {

            Pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            getawb();
            return "";
        }

        private void getawb() {
            // TODO Auto-generated method stub
            try {



                // CheckcntbagResp = WebService.CheckBagconnote(logintokn,wabil,resultbagno);
                GetManftBagResp = WebService.GetManifestBagConnote(logintokn, Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase());
                {
                  //  resulttab.removeAllViews();
                 //  Edtmanfstno.setEnabled(false);
                //    Edtbagno.setEnabled(false);
                    //  serlcnt = 0;
                    try {
                        JSONObject json = (JSONObject) new JSONTokener(GetManftBagResp.toString()).nextValue();
                        JSONObject json2 = json.getJSONObject("d");

                        JSONObject jsonservres = json2.getJSONObject("ServiceResponse");
                        System.out.println("jsonservres is" + jsonservres);
                        Geterrmsg = jsonservres.getString("Message");
                        Getresult = jsonservres.getString("Result");
                        System.out.println("Geterrmsg is" + Geterrmsg);
                        System.out.println("Getresult is" + Getresult);
                        if (Getresult.contentEquals("FAILURE")) {
                            if (Geterrmsg.contentEquals("Invalid Session")) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                alertDialog.setTitle("Invalid Session");
                                alertDialog.setMessage("Your session is expired. Please re-login to continue");
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                DonotInterruptKDCScan = false;
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
                                                Log.e("hee", "hu");
                                                getBaseContext().startActivity(intent);
                                                ManifestActivity.this.finish();
                                                Log.e("here", "hu");
                                                loginflag = false;
                                            }
                                        });
                                alertDialog.show();
                            } else {
                                // Toast toast = Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_LONG);
                                // toast.setGravity(Gravity.CENTER, 0, 0);
                                //  toast.show();
                                // Edtrst.setText("");
                                final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                alertDialog.setTitle("Alert");

                                alertDialog.setMessage(Geterrmsg);
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Edtrst.setText("");
                                                // Edtrst.setEnabled(true);
                                            }
                                        });
                                alertDialog.show();


                            }


                        } else {
                            System.out.println("connotearray else text is" + connotearray);
                            //  if (connotearray!=null) {

                            connotearray = (JSONArray) json2.get("Connote");
                            System.out.println("connotearray aftr is" + connotearray);
                            int totlawbs = connotearray.length();
                         //   txtcnttotl.setText(String.valueOf(totlawbs));
                            ArrayList<String> temp = new ArrayList<String>();
                            System.out.println("connotearray text is" + connotearray);
                            //  for (int i = 0; i< detailsarray.length();i++){

                            JSONObject jsonResponse;
                            String Connte = null;
                            JSONObject rstdetl = null;
                            String respconte = null, respaccno = null, respaccname = null;

                            // }

                        }
                        /// System.out.println("detailsarray are:" + detailsarray.length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Edtbagno.setEnabled(false);

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

            Pb.setVisibility(View.INVISIBLE);

            trmanft = new TableRow(ManifestActivity.this);
            try {
                if (Build.MODEL.contains("SM-N")) {


                    lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                    trmanft.setId((resulttab.getChildCount()));
                    //   lp.setMargins(18, 2, 95, 2);
                    lp.setMargins(10, 5, 200, 0);
                    trmanft.setLayoutParams(lp);
                    System.out.println("trmanft id:" + trmanft.getId());

                } else {
                    lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                    trmanft.setLayoutParams(lp);
                    //lp.setMargins(0, 20, 5, 0);
                    //   lp.setMargins(0,20,70, 0);
                    lp.setMargins(0, 5, 70, 0);

                }
                System.out.println("connotearray post is" + connotearray);
                for (int i = 0; i < connotearray.length(); i++) {
                    trmanft = new TableRow(ManifestActivity.this);
                    final String connoteresp = connotearray.getString(i);
                    System.out.println("connoteresp text is" + connoteresp);
                    //  serlcnt++;

                    deltxt = new TextView(ManifestActivity.this);
                    deltxt.setWidth(120);
                    deltxt.setLayoutParams(lp);
                            /*deltxt.setLayoutParams(lp);
                            deltxt.setWidth(120);*/
                    deltxt.setText("DEL");
                    deltxt.setVisibility(View.VISIBLE);


                    bagtxt = new TextView(ManifestActivity.this);
                    bagtxt.setLayoutParams(lp);
                    // remaktxt.setWidth(120);
                    bagtxt.setText(Edtbagno.getText().toString());


                    waybilltxt = new TextView(ManifestActivity.this);

                    if (Build.MODEL.contains("SM-N")) {
                        waybilltxt.setLayoutParams(lp);
                    } else {
                        waybilltxt.setWidth(250);
                    }

                    waybilltxt.setText(connoteresp);
                    System.out.println("connoteresp text is" + connoteresp + "Taskwabill" + connoteresp);

                          /*  remaktxt = new TextView(ManifestActivity.this);
                            remaktxt.setLayoutParams(lp);
                            // remaktxt.setWidth(120);
                            remaktxt.setText(errmsgchkbag);*/

                    trmanft.addView(deltxt);
                    trmanft.addView(bagtxt);
                    trmanft.addView(waybilltxt);

                          /*  if(Geterrmsg!=null){
                                tr.addView(remaktxt);
                            }*/
                    resulttab.addView(trmanft, 0);
                    // bagsercnt++;

                    final int finalI = i;


                    final int finalI1 = i;
                    trmanft.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            System.out.println("v val:" + v);
                            System.out.println("Rowid bag:" + rowid + ",v.getID:" + v.getId());
                            if (rowid != v.getId()) {
                                rowid = v.getId();
                                Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                            } else if (rowid == v.getId()) {
                                System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());


                                if (resulttab.getChildCount() != 0) {
                                    delawbbag = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                                    System.out.println("delawbbag org are:" + delawbbag);

                                    DelBagcnntRsp = WebService.DeleteManifestBagconnote(logintokn, delawbbag.getText().toString(), Edtbagno.getText().toString(),Edtmanfstno.getText().toString().toUpperCase());

                                    System.out.println("DelBagcnntRsp are:" + DelBagcnntRsp);

                                    if (DelBagcnntRsp != null) {
                                        try {
                                            JSONObject jsondelres = (JSONObject) new JSONTokener(DelBagcnntRsp.toString()).nextValue();
                                            System.out.println("jsondelres is" + jsondelres);
                                            // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                                            //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                                            delbagmsg = jsondelres.getString("Message");
                                            delrslt = (String) jsondelres.get("Result");
                                            delstatus = (String) jsondelres.get("Status");

                                            // System.out.println("delbagmsg is" + delbagmsg );

                                        } catch (Exception e) {


                                        }

                                    }

                                    if (delstatus.contentEquals("SUCCESS")) {
                                        // System.out.println("delID is" + v.getId());
                                        //resulttab.removeViewAt(v.getId());
                                        // resulttab.refreshDrawableState();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            // connotearray.remove(v.getId());
                                            ArrayList<String> list = new ArrayList<String>();
                                            // JSONArray jsonArray = (JSONArray)jsonObject;
                                            int len = connotearray.length();
                                            if (connotearray != null) {
                                                for (int i = 0; i < len; i++) {
                                                    try {
                                                        Tablawb = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                                                        list.add(connotearray.getString(i));
                                                        // list.add(0,Tablawb.getText().toString());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                System.out.println("Tablawb new aftr del is" + Tablawb.getText().toString());
                                            }
//Remove the element from arraylist
                                            System.out.println("list befre are:"+list);
                                            // list.remove(finalI);
                                            connotearray.remove(finalI1);
                                            System.out.println("list  after are:"+list);
//Recreate JSON Array
                                            try {
                                                connotearray = new JSONArray(connotearray);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            System.out.println("updatd cnte arr are:"+list);
                                            //   AwbLIST.addAll(list);
                                            // AwbLIST.add(0,Tablawb.g);

                                        }
                                        //AwbLIST = new ArrayList<String>(new LinkedHashSet<String>(AwbLIST));
                                        System.out.println("connotearray new aftr del is" + connotearray);
                                        //  System.out.println("AwbLIST new aftr del is" + AwbLIST);
                                        //  resulttab.removeAllViews();

                                        serlcnt = 0;
                                        resulttab.removeViewAt(v.getId());
                                        // resulttab.removeAllViews();
                                        txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                                        Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                        //  GetBagAWbs();

                                        //   RefreshTable();

                                    } else {

                                        if (delbagmsg.contentEquals("Invalid Session")) {
                                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                            alertDialog.setTitle("Invalid Session");
                                            alertDialog.setMessage("Your session is expired. Please re-login to continue");
                                            alertDialog.setCanceledOnTouchOutside(false);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            DonotInterruptKDCScan = false;
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
                                                            Log.e("hee", "hu");
                                                            getBaseContext().startActivity(intent);
                                                            ManifestActivity.this.finish();
                                                            Log.e("here", "hu");
                                                            loginflag = false;
                                                        }
                                                    });
                                            alertDialog.show();
                                        } else {
                                            final AlertDialog alertDialog = new AlertDialog.Builder(ManifestActivity.this).create();
                                            alertDialog.setTitle("Alert");

                                            alertDialog.setMessage(delbagmsg);
                                            alertDialog.setCanceledOnTouchOutside(false);
                                            alertDialog.setCancelable(false);
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            // Edtrst.setText("");
                                                            // Edtrst.setEnabled(true);
                                                        }
                                                    });
                                            alertDialog.show();

                                        }
                                    }

                                    //  resulttab.removeViewAt(v.getId());

                                    //   System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                                    //  Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                                    rowid = -1;
                                    int tabcount = (resulttab.getChildCount());
                                    //  serlcnt=tabcount;
                                    for (int i = 0; i < tabcount; i++) {
                                        trmanft = (TableRow) resulttab.getChildAt(i);
                                        trmanft.setId(i);

                                        System.out.println("trmanft aftr del:" + trmanft.getId());
                                        //RefreshTable();
                                    }


                                    //textcount1.setText(String.valueOf(resulttab.getChildCount()));

                                }

                            }
                        }
                        // }
                    });


                }

                rowid = -1;
                int tabcount = (resulttab.getChildCount());

                for (int i = 0; i < tabcount; i++) {

                    trmanft = (TableRow) resulttab.getChildAt(i);
                    trmanft.setId(i);

                    // srnltxt.setText(String.valueOf(trbag.getId()));

                    System.out.println("srnltxt aftr del:" + trmanft.getId());
                }


             //   System.out.println("rstdetl tabl " + rstdetl + "Connte " + respconte + "respaccno " + respaccno + "respaccname " + respaccname);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

}
