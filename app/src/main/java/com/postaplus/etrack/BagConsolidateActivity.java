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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.LinkedHashSet;

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

public class BagConsolidateActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener, BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener {

    EditText Edtbagno;
    Button Btnsave, Btnclear, Btnsearch, BtnUpdadte;
    TextView txtcnttotl;
    BagConsolidateActivity _activity;
    KDCData ScannerData;
    KDCReader _kdcReader;
    public boolean DonotInterruptKDCScan = true;
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    KDCTask KDCTaskExecutable = new KDCTask();
    public int SCANNER_REQUEST_CODE = 123;
    String connote, waybill;
    TableLayout resulttab;
    String camerabill;
    TableRow tr, tr1, trbag;
    TableRow.LayoutParams lp;
    boolean flag = false;
    int rowid = -1;
    int countN = 0;
    TextView waybilltxt, serltxt, Tablawb, deltxt, remaktxt, delawb, awbtxt, srnltxt, bagntxt, bagdattimext, delawbbag;
    CheckBox CBShpmnt, CBFindawb, CBFindBag;
    int serlcnt = 0;
    TextView checkremartext, textView7, textView10, textView11, textbag;
    String tablwaybill, GetBagAwbResponse;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    String logintokn;
    ActivityNotification actNoty = new ActivityNotification();
    String CheckcntbagResp, bagResp, SetBagConteResp;
    String errmsgchkbag, resultchkbag, statsbag, resultbagno, consbagmsg, Geterrmsg, Getresult, msgbagconso, Getmsg, Getstst, delstatus, delbagmsg, delrslt;
    JSONObject GetBagconsResp, GetCnnteCnsldtBagResp;
    ProgressBar progressBarMain;
    String DelBagcnntRsp;
    JSONArray connotearray, Bagarray;
    int bagsercnt = 1;
    ProgressBar loadingPB;
    ArrayList<String> AwbLIST = new ArrayList<String>();
    String[] AwbArray = new String[AwbLIST.size()];
    ArrayList<String> Dellist= null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag_consolidate);

        InitializeViews();

        // code for clear button
        Btnclear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                resulttab.removeAllViewsInLayout();
                Btnsave.setVisibility(View.VISIBLE);
                BtnUpdadte.setVisibility(View.GONE);
                serlcnt = 0;
                AwbLIST = new ArrayList<String>();
                AwbLIST.clear();
                bagsercnt = 1;
                Edtbagno.setText("");
                Edtbagno.setEnabled(false);
                CBShpmnt.setEnabled(true);
                CBShpmnt.setChecked(false);
                CBFindawb.setEnabled(true);
                CBFindawb.setChecked(false);
                CBFindBag.setEnabled(true);
                CBFindBag.setChecked(false);
                textView7.setText("Serial Number");
                textView10.setText("AWB No.");
                textView11.setText("Remark");
                Edtbagno.setHint("Bag No.");
                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();


            }

        });


        // Afind bag checkBox
        CBFindBag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    CBFindBag.setChecked(true);
                    Edtbagno.setEnabled(true);
                    CBFindawb.setEnabled(false);

                    CBShpmnt.setEnabled(false);
                    Btnsave.setVisibility(View.GONE);
                    textView7.setText("Serial Number");
                    textView10.setText("AWB No.");
                    textView11.setText("Remark");
                    textView11.setVisibility(View.INVISIBLE);
                    textbag.setText("Bag No.");
                } else {
                    bagsercnt = 1;
                    CBFindBag.setChecked(false);
                    resulttab.removeAllViews();
                    txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                    Edtbagno.setText("");
                    CBFindawb.setEnabled(true);
                    CBShpmnt.setEnabled(true);
                    Edtbagno.setEnabled(false);
                    textView11.setVisibility(View.VISIBLE);
                    Btnsave.setVisibility(View.VISIBLE);
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        // Add new Shpmnt checkBox
        CBShpmnt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    CBShpmnt.setChecked(true);
                    Edtbagno.setEnabled(true);
                    CBFindawb.setEnabled(false);
                    CBFindBag.setEnabled(false);
                    Btnsave.setVisibility(View.GONE);
                    BtnUpdadte.setVisibility(View.VISIBLE);
                    textView7.setText("Serial Number");
                    textView10.setText("AWB No.");
                    textView11.setText("Remark");
                    textbag.setText("Bag No.");
                } else {

                    CBShpmnt.setChecked(false);
                    Btnsave.setVisibility(View.VISIBLE);
                    BtnUpdadte.setVisibility(View.GONE);
                    resulttab.removeAllViews();
                    Edtbagno.setText("");
                    CBFindawb.setEnabled(true);
                    CBFindBag.setEnabled(true);
                    Edtbagno.setEnabled(false);
                    txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

// Find AWB checkbox
        CBFindawb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    CBFindawb.setChecked(true);
                    //  Edtbagno.setEnabled(false);

                    // Edtbagno.setEnabled(true);
                    Edtbagno.setHint("AWB No.");
                    Btnsave.setVisibility(View.INVISIBLE);
                    CBShpmnt.setEnabled(false);
                    Edtbagno.setEnabled(true);
                    CBFindBag.setEnabled(false);
                    textView7.setText("Bag Number");

                    textView10.setText("         Bag Date.   ");
                    textView11.setText("         AWB No.");
                    textbag.setText("AirWay Bill");

                } else {

                    CBFindawb.setChecked(false);
                    CBFindBag.setEnabled(true);
                    resulttab.removeAllViews();
                    Edtbagno.setEnabled(false);
                    CBShpmnt.setEnabled(true);
                    Edtbagno.setText("");
                    textView7.setText("Serial Number");
                    textView10.setText("AWB No.");
                    textView11.setText("Remark");
                    Edtbagno.setHint("Bag No.");
                    textbag.setText("Bag No.");
                    Btnsave.setVisibility(View.VISIBLE);
                    txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        // code for search button
        Btnsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                //  Edtbagno.setEnabled(false);


              /*  if(Edtbagno.getText().toString().contentEquals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Scan or Enter Bag No.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Edtbagno.setEnabled(true);

                    //  jsonArray = new JSONArray();
                }*/

                _activity.runOnUiThread(new Runnable() {
                    public void run() {

                        if (CBFindawb.isChecked()) {

                            GetAwbBagDetails();

                        } else if (CBFindBag.isChecked()) {
                            GetBagAWbs();
                        } else if (CBShpmnt.isChecked()) {
                            GetBagAWbs();
                        } else {
                            // if(resulttab.getChildCount()==0){
                            Toast toast = Toast.makeText(getApplicationContext(), "There is no data to search.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Edtbagno.setEnabled(false);

                            //  jsonArray = new JSONArray();
                            //  }
                        }


                    }
                });


                // v.getBackground().clearColorFilter();
            }

        });


        // code for Update button
        BtnUpdadte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                if (resulttab.getChildCount() == 0) {
                    // progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);
                    Toast toast = Toast.makeText(getApplicationContext(), "No Data to Update", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    if (v.isActivated() && v.isSelected()) {
                        return;
                    } else
                        v.setActivated(true);
                    v.setSelected(true);

                    // if(!resultbagno.toString().trim().isEmpty()){
                    SetBagConteResp = WebService.SetConsolidateBag(logintokn, Edtbagno.getText().toString());
                    System.out.println("SetBagConteResp updte are:" + SetBagConteResp);

                    /// }

                    if (SetBagConteResp != null) {
                        try {
                            JSONObject jsonsetres = (JSONObject) new JSONTokener(SetBagConteResp.toString()).nextValue();
                            System.out.println("jsonsetres is" + jsonsetres);
                            // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                            //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                            statsbag = jsonsetres.getString("Status");
                            resultbagno = (String) jsonsetres.get("Result");
                            consbagmsg = (String) jsonsetres.getString("Message");
                            System.out.println("resultbagno is" + resultbagno + "statsbag are " + statsbag);

                        } catch (Exception e) {


                        }
                    }

                    if (statsbag.contentEquals("SUCCESS")) {
                        Btnsave.setVisibility(View.VISIBLE);
                        BtnUpdadte.setVisibility(View.GONE);
                        resulttab.removeAllViews();
                        Edtbagno.setText("");
                        Edtbagno.setEnabled(true);
                        serlcnt = 0;
                       // AwbLIST.clear();
                        CBShpmnt.setChecked(false);
                        Toast toast = Toast.makeText(getApplicationContext(), consbagmsg, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        v.setActivated(false);
                        v.setSelected(false);
                    } else {
                        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();

                        if (consbagmsg.contentEquals("Invalid Session")) {
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
                                            BagConsolidateActivity.this.finish();
                                            Log.e("here", "hu");
                                            loginflag = false;
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage(consbagmsg);


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

                            System.out.println("consbagmsg is" + consbagmsg);
                            //  Toast toast = Toast.makeText(getApplicationContext(),consbagmsg, Toast.LENGTH_LONG);
                            // toast.setGravity(Gravity.CENTER, 0, 0);
                            // toast.show();

                        }
                        v.setActivated(false);
                        v.setSelected(false);
                    }
                }

                txtcnttotl.setText(String.valueOf(resulttab.getChildCount()));

                // if(progrflag==resulttab.getChildCount()){
                // progressBarMain.setVisibility(View.INVISIBLE);
                v.setActivated(false);
                v.setSelected(false);
            }
            // v.getBackground().clearColorFilter();


        });


        // code for save button
        Btnsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));

                System.out.println("v:" + v.isSelected());


                if (resulttab.getChildCount() == 0) {
                    // progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);
                    Edtbagno.setText("");
                    Toast toast = Toast.makeText(getApplicationContext(), "No Data to Save", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                } else {

                    if (v.isActivated() && v.isSelected()) {
                        return;
                    } else
                        v.setActivated(true);
                    v.setSelected(true);

                    // progressBarMain.setVisibility(View.VISIBLE);

                }


                System.out.println("resulttab.getChildCount are:" + resulttab.getChildCount());

                SetBagConteResp = WebService.SetConsolidateBag(logintokn, Edtbagno.getText().toString());
                System.out.println("SetBagConteResp are:" + SetBagConteResp);

                if (SetBagConteResp != null) {
                    try {
                        JSONObject jsonsetres = (JSONObject) new JSONTokener(SetBagConteResp.toString()).nextValue();
                        System.out.println("jsonsetres is" + jsonsetres);
                        // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                        //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                        statsbag = jsonsetres.getString("Status");
                        resultbagno = (String) jsonsetres.get("Result");
                        msgbagconso = jsonsetres.getString("Message");
                        System.out.println("resultbagno is" + resultbagno + "statsbag are " + statsbag);

                    } catch (Exception e) {


                    }
                }

                if(statsbag!=null){
                if (statsbag.contentEquals("SUCCESS")) {
                    Edtbagno.setText(resultbagno);
                    Edtbagno.setEnabled(false);
                    Btnsave.setVisibility(View.GONE);
                    BtnUpdadte.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(), msgbagconso, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    v.setActivated(false);
                    v.setSelected(false);
                } else {
                    v.setActivated(false);
                    v.setSelected(false);

                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();

                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(msgbagconso);


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

                    System.out.println("consbagmsg is" + consbagmsg);
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

    }

    public void InitializeViews() {
        Edtbagno = (EditText) findViewById(R.id.edittxtbagno);
        Btnsave = (Button) findViewById(R.id.btnsave);
        Btnclear = (Button) findViewById(R.id.btnclear);
        Btnsearch = (Button) findViewById(R.id.btnsearch);
        txtcnttotl = (TextView) findViewById(R.id.txtvcountshpmt);
        resulttab = (TableLayout) findViewById(R.id.resulttable1);
        CBShpmnt = (CheckBox) findViewById(R.id.CBaddshpmt);
        CBFindawb = (CheckBox) findViewById(R.id.Findshpmt);
        CBFindBag = (CheckBox) findViewById(R.id.Findbag);
        textView7 = (TextView) findViewById(R.id.textView7);
        textView10 = (TextView) findViewById(R.id.textView10);
        textView11 = (TextView) findViewById(R.id.textView11);
        textbag = (TextView) findViewById(R.id.txtvbagno);
        logintokn = actNoty.getToken(BagConsolidateActivity.this);
        progressBarMain = (ProgressBar) findViewById(R.id.progressmain);
        BtnUpdadte = (Button) findViewById(R.id.btnupdte);
        loadingPB = (ProgressBar) findViewById(R.id.loadingPB);
        Edtbagno.setEnabled(false);
        if (barcodeReader != null) {
            barcodeReader.addBarcodeListener(BagConsolidateActivity.this);
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
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
            if (barcodeReader != null) {
                barcodeReader.removeBarcodeListener(BagConsolidateActivity.this);
                barcodeReader.release();

            }
            Intent int1 = new Intent(this, MainActivity.class);
            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC bagcnso Activity BarCodeReceived Block");
        System.out.print("KDCReader bagcnso Activity  BarCodeReceived Block");

        // if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();
            if (CBShpmnt.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtbagno.setText(waybill);
                        GetBagAWbs();
                    }
                });
                return;
            } else if (CBFindawb.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtbagno.setText(waybill);
                        GetAwbBagDetails();
                    }
                });
                return;
            } else if (CBFindBag.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Edtbagno.setText(waybill);
                        GetBagAWbs();
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
                            new BagConsolTask(connote).execute();
                        }

                        //  }
                        // wbilldata1=contents;

                    }
                });

            } else {

                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                        Log.e("222", "222");
                    }
                });
            }
        } else {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(_activity, "Invalid Waybill", Toast.LENGTH_LONG).show();
                    Log.e("111", "1");
                }
            });
        }
    }

    @Override
    public void ConnectionChanged(BluetoothDevice device, int state) {
        //ToDo Auto-generated method stub

        Log.i("KDCReader", "KDC bagonso Activity connection changed block");
        System.out.print("KDCReader bagonso Activity connection changed block");
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
    public void onPause() {
        super.onPause();
        System.out.println("KDCReader on BAGCONSO actvity While Pause : " + _kdcReader);
        if (!isActivityActiveFlag) isActivityActiveFlag = false;


        if (!DonotInterruptKDCScan) {
            System.out.println("KDCReader on BAGCONSO  While Pause : " + _kdcReader);
            if (_kdcReader != null) _kdcReader.Disconnect();
            if (ThrKdc != null) ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
        } else {
            DonotInterruptKDCScan = false;
        }

        if (barcodeReader != null) {
            barcodeReader.removeBarcodeListener(BagConsolidateActivity.this);
            barcodeReader.release();
        }
        //_activity.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("barcode1 bag onres:" + barcodeReader);
        if (barcodeReader != null) {
            try {

                barcodeReader.claim();
                System.out.println("barcode bag onres:" + barcodeReader);
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
                if (CBShpmnt.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtbagno.setText(camerabill);
                            GetBagAWbs();
                        }
                    });
                    return;
                } else if (CBFindawb.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtbagno.setText(camerabill);
                            GetAwbBagDetails();
                        }
                    });
                    return;
                } else if (CBFindBag.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Edtbagno.setText(camerabill);
                            GetBagAWbs();
                        }
                    });
                    return;
                }

                if (Check_ValidWaybill(camerabill) == true) {
                    connote = camerabill;
                    new BagConsolTask(camerabill).execute();
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
                    /*if(Edtbagno.getText().toString().contentEquals("")) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter Bag No ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }else {*/
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    startActivityForResult(intent, SCANNER_REQUEST_CODE);

                    // }
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (action == KeyEvent.ACTION_DOWN) {
                    DonotInterruptKDCScan = true;

                   /* if (Edtbagno.getText().toString().contentEquals("")) {

                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter Bagno ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {*/
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "SCAN_MODE");
                    startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    // }

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

            if (barcodeReader != null) {
                barcodeReader.removeBarcodeListener(BagConsolidateActivity.this);
                barcodeReader.release();
                BagConsolidateActivity.this.finish();
                Intent int1 = new Intent(this, MainActivity.class);
                // int1.putExtra("usrnamee", usrnamee);
                // int1.putExtra("passworde", passworde);
                startActivity(new Intent(int1));
            } else if (!isActivityActiveFlag) {
                Toast.makeText(getApplicationContext(), "Please wait for scanner to connect",
                        Toast.LENGTH_LONG).show();
                return false;
            } else {


                if (_kdcReader != null) _kdcReader.Disconnect();
                if (ThrKdc != null) ThrKdc.interrupt();
                KDCTaskExecutable.cancel(true);
                BagConsolidateActivity.this.finish();
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
    public void onBarcodeEvent(final BarcodeReadEvent honeywellevent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (honeywellevent != null) {

                    //ScannerData = honeywellevent;
                    waybill = honeywellevent.getBarcodeData();
                    if (CBShpmnt.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Edtbagno.setText(waybill);
                                GetBagAWbs();
                            }
                        });
                        return;
                    } else if (CBFindawb.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Edtbagno.setText(waybill);
                                GetAwbBagDetails();
                            }
                        });
                        return;
                    } else if (CBFindBag.isChecked() && Edtbagno.getText().toString().trim().isEmpty()) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Edtbagno.setText(camerabill);
                                GetBagAWbs();
                            }
                        });
                        return;
                    }


                    if (Check_ValidWaybill(honeywellevent.getBarcodeData()) == true) {

                        System.out.println("honeywellevent ID : ");
                        // System.out.println(R.id.WC_Frame);
                        System.out.println(" value for honeywellevent is : ");
                        System.out.println(honeywellevent);


                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (waybill != null) {
                                    connote = waybill;


                                    new BagConsolTask(connote).execute();

                                    //Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG).show();

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

    public class BagConsolTask extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public BagConsolTask(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;


            System.out.println("taskwaybill bagcon pre:" + Taskwabill);
        }

        public void onPreExecute() {

            progressBarMain.setVisibility(View.VISIBLE);
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
                CheckcntbagResp = WebService.CheckBagconnote(logintokn, wabil, Edtbagno.getText().toString());
                System.out.println("CheckcntbagResp val" + CheckcntbagResp);
                if (CheckcntbagResp != null) {
                    try {
                        JSONObject jsonchckres = (JSONObject) new JSONTokener(CheckcntbagResp.toString()).nextValue();
                        System.out.println("jsonchckres is" + jsonchckres);
                        // JSONObject jsnchkbagobj = jsonchckres.getJSONObject("d");
                        //  System.out.println("jsnchkbagobj is" + jsnchkbagobj);

                        errmsgchkbag = jsonchckres.getString("Message");
                        resultchkbag = (String) jsonchckres.get("Result");
                        System.out.println("errmsgchkbag is" + errmsgchkbag + "resultchkbag" + resultchkbag);

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

            progressBarMain.setVisibility(View.INVISIBLE);

            tr = new TableRow(BagConsolidateActivity.this);

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


            if (resultchkbag.contentEquals("SUCCESS")) {



                deltxt = new TextView(BagConsolidateActivity.this);
                deltxt.setLayoutParams(lp);
                //deltxt.setWidth(120);
                deltxt.setWidth(250);
                // deltxt.setText("Delete");
                deltxt.setText("DEL");

                serlcnt++;
                serltxt = new TextView(BagConsolidateActivity.this);
                serltxt.setLayoutParams(lp);
                //serltxt.setWidth(80);
                serltxt.setText(String.valueOf(serlcnt));

                waybilltxt = new TextView(BagConsolidateActivity.this);
                waybilltxt.setLayoutParams(lp);
                //  waybilltxt.setWidth(50);
                waybilltxt.setText(Taskwabill);
                System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);

                remaktxt = new TextView(BagConsolidateActivity.this);
                remaktxt.setLayoutParams(lp);
                // remaktxt.setWidth(120);
                remaktxt.setText(errmsgchkbag);


                //  System.out.println("cnametxt  is"+cname1);


                tr.addView(deltxt);
                tr.addView(serltxt);
                tr.addView(waybilltxt);
                tr.addView(remaktxt);
                // tr.addView(rsntxt);


                System.out.println("res bagcons chld:" + resulttab.getChildCount());

                if (resulttab.getChildCount() == 0) {
                    resulttab.addView(tr, 0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                   // tr.setBackgroundColor(Color.parseColor("#eb7a02"));

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
                        //tr.setBackgroundColor(Color.parseColor("#d73232"));
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
                            serlcnt = serlcnt - 1;
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
                AwbLIST.add(0,Taskwabill);
                ArrayList<String> TotalList = new ArrayList();
                if(connotearray!= null){
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
                }
                AwbLIST = new ArrayList<String>(new LinkedHashSet<String>(AwbLIST));
                System.out.println("AwbLIST  all is" +  AwbLIST);
                AwbArray = new String[AwbLIST.size()];
                AwbLIST.toArray(AwbArray);
                System.out.println("AwbLIST size is" +  AwbLIST.size());

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

                                DelBagcnntRsp = WebService.DeleteBagconnote(logintokn, delawb.getText().toString(), Edtbagno.getText().toString());

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

                                        System.out.println("delbagmsg is" + delbagmsg + "resultchkbag" + resultchkbag);

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
                                    System.out.println("AwbLIST befr del:" + AwbLIST);
                                    AwbLIST.remove(v.getId());
                                    System.out.println("AwbLIST aftr del:" + AwbLIST);
//Recreate JSON Array
                                    AwbArray = new String[AwbLIST.size()];
                                    txtcnttotl.setText(String.valueOf(AwbArray.length));

                                    System.out.println("AwbArray nrml refr is" + AwbArray.length+"v.getId()"+v.getId());
                                    // resulttab.removeAllViews();
                                    System.out.println("Awblist aftr del "+AwbLIST+"list are"+Dellist);
                                    //   connotearray.length();
                                    System.out.println("v was getId:"+v.getId()+"Tr");
                                    //resulttab.removeViewAt(v.getId());
                                    resulttab.removeViewAt(v.getId());
                                    resulttab.removeAllViews();
                                    txtcnttotl.setText(String.valueOf(AwbArray.length));
                                    serlcnt = 0;
                                    TableReseq();
                                    Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                }


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

                if (errmsgchkbag.contentEquals("Invalid Session")) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                    BagConsolidateActivity.this.finish();
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
        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
        alertDialog.setTitle("Alert");
        if (CBFindawb.isChecked()) {
            alertDialog.setMessage(errmsgchkbag);
        } else if (CBFindBag.isChecked()) {
            alertDialog.setMessage(Geterrmsg);
        } else if (CBShpmnt.isChecked()) {

        }
        alertDialog.setMessage(errmsgchkbag);
       /* if(errmsgchkbag.contentEquals("NOTFOUND")){
            alertDialog.setMessage("AWB NOTFOUND");
        }else{
            alertDialog.setMessage(errmsgchkbag);
        }*/

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

    public void GetBagAWbs() {

        GetBagconsResp = WebService.GetConsolidateBag(logintokn, Edtbagno.getText().toString());

        System.out.println("GetBagconsResp for bag:" + GetBagconsResp);


        if (GetBagconsResp != null) {
            resulttab.removeAllViews();
            serlcnt = 0;
            try {
                JSONObject json = (JSONObject) new JSONTokener(GetBagconsResp.toString()).nextValue();
                JSONObject json2 = json.getJSONObject("d");

                JSONObject jsonservres = json2.getJSONObject("ServiceResponse");
                System.out.println("jsonservres is" + jsonservres);
                Geterrmsg = jsonservres.getString("Message");
                Getresult = jsonservres.getString("Status");
                System.out.println("Geterrmsg is" + Geterrmsg);

                if (Getresult.contentEquals("FAILURE")) {
                    if (Geterrmsg.contentEquals("Invalid Session")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                        BagConsolidateActivity.this.finish();
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
                        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                    trbag = new TableRow(BagConsolidateActivity.this);
                    try {
                        if (Build.MODEL.contains("SM-N")) {


                            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                            trbag.setId((resulttab.getChildCount()));
                            //   lp.setMargins(18, 2, 95, 2);
                            lp.setMargins(10, 5, 200, 0);
                            trbag.setLayoutParams(lp);
                            System.out.println("trbag id:" + trbag.getId());

                        } else {
                            lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                            trbag.setLayoutParams(lp);
                            //lp.setMargins(0, 20, 5, 0);
                            //   lp.setMargins(0,20,70, 0);
                            lp.setMargins(0, 5, 70, 0);

                        }
                        for (int i = 0; i < connotearray.length(); i++) {
                            trbag = new TableRow(BagConsolidateActivity.this);
                            final String connoteresp = connotearray.getString(i);
                            System.out.println("connoteresp text is" + connoteresp);
                            serlcnt++;

                            deltxt = new TextView(BagConsolidateActivity.this);
                            deltxt.setWidth(120);
                            deltxt.setLayoutParams(lp);
                            /*deltxt.setLayoutParams(lp);
                            deltxt.setWidth(120);*/
                            deltxt.setText("DEL");
                            deltxt.setVisibility(View.VISIBLE);


                            srnltxt = new TextView(BagConsolidateActivity.this);
                            srnltxt.setWidth(250);
                            //srnltxt.setLayoutParams(lp);
                            //srnltxt.setText(String.valueOf(bagsercnt));
                            srnltxt.setText(String.valueOf(serlcnt));

                            waybilltxt = new TextView(BagConsolidateActivity.this);

                            if (Build.MODEL.contains("SM-N")) {
                                waybilltxt.setLayoutParams(lp);
                            } else {
                                waybilltxt.setWidth(250);
                            }

                            waybilltxt.setText(connoteresp);
                            System.out.println("connoteresp text is" + connoteresp + "Taskwabill" + connoteresp);

                            remaktxt = new TextView(BagConsolidateActivity.this);
                            remaktxt.setLayoutParams(lp);
                            // remaktxt.setWidth(120);
                            remaktxt.setText(errmsgchkbag);

                            trbag.addView(deltxt);
                            trbag.addView(srnltxt);
                            trbag.addView(waybilltxt);

                            if(errmsgchkbag!=null){
                                tr.addView(remaktxt);
                            }
                            resulttab.addView(trbag, 0);
                            bagsercnt++;

                            final int finalI = i;







                            trbag.setOnClickListener(new View.OnClickListener() {

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

                                            DelBagcnntRsp = WebService.DeleteBagconnote(logintokn, delawbbag.getText().toString(), Edtbagno.getText().toString());

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

                                                    System.out.println("delbagmsg is" + delbagmsg + "resultchkbag" + resultchkbag);

                                                } catch (Exception e) {


                                                }

                                            }

                                            if (delstatus.contentEquals("SUCCESS")) {
                                                System.out.println("delID is" + v.getId());
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
                                                    System.out.println("list are:"+list);
                                                   list.remove(finalI);
//Recreate JSON Array
                                                    connotearray = new JSONArray(list);
                                                 //   AwbLIST.addAll(list);
                                                   // AwbLIST.add(0,Tablawb.g);

                                                }
                                                AwbLIST = new ArrayList<String>(new LinkedHashSet<String>(AwbLIST));
                                                System.out.println("connotearray new aftr del is" + connotearray);
                                                System.out.println("AwbLIST new aftr del is" + AwbLIST);
                                                //  resulttab.removeAllViews();

                                                serlcnt = 0;
                                                resulttab.removeViewAt(v.getId());
                                                resulttab.removeAllViews();
                                                txtcnttotl.setText(String.valueOf(connotearray.length()));
                                                Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                                //  GetBagAWbs();

                                                RefreshTable();

                                            } else {

                                                if (delbagmsg.contentEquals("Invalid Session")) {
                                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                                                    BagConsolidateActivity.this.finish();
                                                                    Log.e("here", "hu");
                                                                    loginflag = false;
                                                                }
                                                            });
                                                    alertDialog.show();
                                                } else {
                                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                                trbag = (TableRow) resulttab.getChildAt(i);
                                                trbag.setId(i);

                                                System.out.println("trbagyu aftr del:" + trbag.getId());
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

                            trbag = (TableRow) resulttab.getChildAt(i);
                            trbag.setId(i);

                            // srnltxt.setText(String.valueOf(trbag.getId()));

                            System.out.println("srnltxt aftr del:" + srnltxt.getText().toString());
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
            Edtbagno.setEnabled(false);

        }


    }

    public void GetAwbBagDetails() {

        GetCnnteCnsldtBagResp = WebService.GetConnoteConsolidationBag(logintokn, Edtbagno.getText().toString());

        System.out.println("GetCnnteCnsldtBagResp for bag:" + GetCnnteCnsldtBagResp);


        if (GetCnnteCnsldtBagResp != null) {
            resulttab.removeAllViews();

            try {
                JSONObject json = (JSONObject) new JSONTokener(GetCnnteCnsldtBagResp.toString()).nextValue();
                JSONObject json2 = json.getJSONObject("d");

                JSONObject jsonservresp = json2.getJSONObject("ServiceResponse");
                System.out.println("jsonservresp is" + jsonservresp);
                Getmsg = jsonservresp.getString("Message");
                Getstst = jsonservresp.getString("Status");
                System.out.println("Getmsg is" + Getmsg);

                if (Getstst.contentEquals("FAILURE")) {
                    if (Getstst.contentEquals("Invalid Session")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                        BagConsolidateActivity.this.finish();
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
                        final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
                        alertDialog.setTitle("Alert");

                        alertDialog.setMessage(Getmsg);
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

                    Bagarray = (JSONArray) json2.get("B");
                    int totlawbs = Bagarray.length();
                    txtcnttotl.setText(String.valueOf(totlawbs));
                    ArrayList<String> temp = new ArrayList<String>();
                    System.out.println("Bagarray text is" + Bagarray);
                    //  for (int i = 0; i< detailsarray.length();i++){

                    JSONObject jsonResponse;
                    String Connte = null;
                    JSONObject rstdetl = null;
                    String respbagdate = null, respbagno = null, respaccname = null;
                    tr = new TableRow(BagConsolidateActivity.this);
                    try {
                        if (Build.MODEL.contains("SM-N")) {


                            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


                            //   lp.setMargins(18, 2, 95, 2);
                            lp.setMargins(10, 5, 200, 0);
                            tr.setLayoutParams(lp);
                            //  tr.setLayoutParams(lp);
                            //  System.out.println("tr id:" + tr.getId());

                        } else {
                            lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                            tr.setLayoutParams(lp);
                            //lp.setMargins(0, 20, 5, 0);
                            //   lp.setMargins(0,20,70, 0);
                            lp.setMargins(0, 5, 70, 0);

                        }
                        for (int i = 0; i < Bagarray.length(); i++) {
                            tr = new TableRow(BagConsolidateActivity.this);


                            JSONObject jsonobject = Bagarray.getJSONObject(i);

                            System.out.println("jsonobject text is" + jsonobject);
                            respbagdate = jsonobject.getString("BagDate");
                            respbagno = jsonobject.getString("Bagnumber");

                            System.out.println("respbagdate text is" + respbagdate + "respbagno " + respbagno);


                            deltxt = new TextView(BagConsolidateActivity.this);
                            // deltxt.setWidth(50);
                            deltxt.setWidth(120);
                            // deltxt.setLayoutParams(lp);
                            deltxt.setText("DEL");
                            deltxt.setVisibility(View.INVISIBLE);


                            bagntxt = new TextView(BagConsolidateActivity.this);
                            //bagntxt.setWidth(550);
                            // bagntxt.setGravity(Gravity.LEFT);
                            // bagntxt.setWidth(250);
                            // bagntxt.setPadding(40,5,15,0);
                            bagntxt.setLayoutParams(lp);
                            bagntxt.setText(respbagno);


                            bagdattimext = new TextView(BagConsolidateActivity.this);

                            if (Build.MODEL.contains("SM-N")) {
                                bagdattimext.setLayoutParams(lp);
                                // bagdattimext.setWidth(300);
                                // bagdattimext.setWidth(250);
                            } else {
                                bagdattimext.setWidth(250);
                            }

                            bagdattimext.setText(respbagdate);
                            System.out.println("respbagno text is" + respbagno + "respbagdate" + respbagdate);

                            awbtxt = new TextView(BagConsolidateActivity.this);
                            awbtxt.setLayoutParams(lp);
                            awbtxt.setText(Edtbagno.getText().toString());


                            // tr.addView(deltxt);
                            tr.addView(bagntxt);
                            tr.addView(bagdattimext);
                            tr.addView(awbtxt);
                            // tr.setBackgroundColor(Color.parseColor("#ffffff"));
                            resulttab.addView(tr, 0);
                            bagsercnt++;
                        }
                        System.out.println("respbagdate tabl " + respbagdate + "bagdattimext " + bagdattimext);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                /// System.out.println("detailsarray are:" + detailsarray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Edtbagno.setEnabled(false);
        }

    }

    public void RefreshTable() {
        // resulttab.removeAllViews();
        //  serlcnt=0;
        trbag = new TableRow(BagConsolidateActivity.this);
        if (Build.MODEL.contains("SM-N")) {


            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

            trbag.setId((resulttab.getChildCount()));
            //   lp.setMargins(18, 2, 95, 2);
            lp.setMargins(10, 5, 200, 0);
            trbag.setLayoutParams(lp);
            System.out.println("trbag id:" + trbag.getId());

        } else {
            lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


            //lp.setMargins(0, 20, 5, 0);
            //   lp.setMargins(0,20,70, 0);
            lp.setMargins(0, 5, 70, 0);
            trbag.setLayoutParams(lp);

        }
        System.out.println("connotearrays txt text is" + connotearray.length());
        String cntResp = null;
        for (int i = 0; i < connotearray.length(); i++) {
            trbag = new TableRow(BagConsolidateActivity.this);

            serlcnt++;
            System.out.println("connotearray text is" + connotearray.length() + "sercnt ai" + serlcnt);

            try {
                cntResp = connotearray.getString(i);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            deltxt = new TextView(BagConsolidateActivity.this);
            // deltxt.setWidth(250);
            //  deltxt.setLayoutParams(lp);

            deltxt.setWidth(120);
            deltxt.setLayoutParams(lp);
            deltxt.setText("DEL");
            deltxt.setVisibility(View.VISIBLE);


            srnltxt = new TextView(BagConsolidateActivity.this);
            srnltxt.setWidth(250);
            // srnltxt.setLayoutParams(lp);
            //srnltxt.setText(String.valueOf(bagsercnt));
            srnltxt.setText(String.valueOf(serlcnt));

            waybilltxt = new TextView(BagConsolidateActivity.this);

            if (Build.MODEL.contains("SM-N")) {
                waybilltxt.setLayoutParams(lp);
            } else {
                waybilltxt.setWidth(250);
            }

            waybilltxt.setText(cntResp);
            System.out.println("refresh text is" + cntResp);

            remaktxt = new TextView(BagConsolidateActivity.this);
            remaktxt.setLayoutParams(lp);
            // remaktxt.setWidth(120);
            remaktxt.setText(errmsgchkbag);

            trbag.addView(deltxt);
            trbag.addView(srnltxt);
            trbag.addView(waybilltxt);
            if(errmsgchkbag!=null){
                tr.addView(remaktxt);
            }
            //  resulttab.removeAllViews();
            resulttab.addView(trbag, 0);
            //  bagsercnt++;
            System.out.println("resulttab is:" + resulttab.getChildCount());

            final int finalI = i;
            trbag.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("v tab val:" + v);
                    System.out.println("Rowid tab:" + rowid + ",v.getID:" + v.getId());
                    if (rowid != v.getId()) {
                        rowid = v.getId();
                        Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                    } else if (rowid == v.getId()) {
                        System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());


                        if (resulttab.getChildCount() != 0) {
                            delawbbag = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                            System.out.println("delawbbag refresh are:" + delawbbag);

                            DelBagcnntRsp = WebService.DeleteBagconnote(logintokn, delawbbag.getText().toString(), Edtbagno.getText().toString());

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

                                    System.out.println("delbagmsg is" + delbagmsg + "resultchkbag" + resultchkbag);

                                } catch (Exception e) {


                                }

                            }
                            System.out.println("connotearray brfre is" + connotearray);
                            if (delstatus.contentEquals("SUCCESS")) {
                                System.out.println("dREfrt is" + v.getId());
                                System.out.println("trbag aftr del:" + trbag.getId());

                                // resulttab.refreshDrawableState();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    //  connotearray.remove(v.getId());
                                    ArrayList<String> list = new ArrayList<String>();
                                    // JSONArray jsonArray = (JSONArray)jsonObject;
                                    int len = connotearray.length();
                                    if (connotearray != null) {
                                        for (int i = 0; i < len; i++) {
                                            try {
                                                list.add(connotearray.getString(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
//Remove the element from arraylist
                                    list.remove(finalI);
//Recreate JSON Array
                                    connotearray = new JSONArray(list);
                                    txtcnttotl.setText(String.valueOf(connotearray.length()));
                                }
                                System.out.println("connotearray refr is" + connotearray);
                                // resulttab.removeAllViews();
                                serlcnt = 0;
                                //   connotearray.length();
                                resulttab.removeViewAt(v.getId());
                                resulttab.removeAllViews();
                                txtcnttotl.setText(String.valueOf(connotearray.length()));
                                Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                //  GetBagAWbs();
                                rowid = -1;
                                int tabcount = (resulttab.getChildCount());
                                //  serlcnt=tabcount;
                                for (int i = 0; i < tabcount; i++) {
                                    trbag = (TableRow) resulttab.getChildAt(i);
                                    trbag.setId(i);
                                    System.out.println("trbagds aftr del:" + trbag.getId());
                                    // RefreshTable();
                                }
                                resulttab.removeAllViews();
                                txtcnttotl.setText(String.valueOf(connotearray.length()));
                                RefreshTable();
                            } else {

                                if (delbagmsg.contentEquals("Invalid Session")) {
                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                                    BagConsolidateActivity.this.finish();
                                                    Log.e("here", "hu");
                                                    loginflag = false;
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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


                        }
                        rowid = -1;
                        int tabcount = (resulttab.getChildCount());
                        //  serlcnt=tabcount;
                        for (int i = 0; i < tabcount; i++) {
                            trbag = (TableRow) resulttab.getChildAt(i);
                            trbag.setId(i);
                            System.out.println("trbags de aftr del:" + trbag.getId());
                            // RefreshTable();
                        }
                    }
                }
                // }
            });
        }

    }

    public void TableReseq() {

        tr = new TableRow(BagConsolidateActivity.this);
        if (Build.MODEL.contains("SM-N")) {


            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

            tr.setId((resulttab.getChildCount()));
            //   lp.setMargins(18, 2, 95, 2);
            lp.setMargins(10, 5, 200, 0);
            tr.setLayoutParams(lp);
            System.out.println("tr id:" + tr.getId());

        } else {
            lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);


            //lp.setMargins(0, 20, 5, 0);
            //   lp.setMargins(0,20,70, 0);
            lp.setMargins(0, 5, 70, 0);
            tr.setLayoutParams(lp);

        }
       System.out.println("AwbLIST tablreqt text is" + AwbLIST.size()+"AwbLIST are"+AwbLIST);
        String cntTxt = null;
        for (int i = 0; i < AwbLIST.size(); i++) {
            tr = new TableRow(BagConsolidateActivity.this);

            serlcnt++;
          //  System.out.println("connotearray text is" + connotearray.length() + "sercnt ai" + serlcnt);

            cntTxt = AwbLIST.get(i);
            System.out.println("cntTxt txt is" + cntTxt);
            deltxt = new TextView(BagConsolidateActivity.this);
            // deltxt.setWidth(250);
            //  deltxt.setLayoutParams(lp);

            deltxt.setWidth(120);
            deltxt.setLayoutParams(lp);
            deltxt.setText("DEL");
            deltxt.setVisibility(View.VISIBLE);


            srnltxt = new TextView(BagConsolidateActivity.this);
            srnltxt.setWidth(250);
            // srnltxt.setLayoutParams(lp);
            //srnltxt.setText(String.valueOf(bagsercnt));
            srnltxt.setText(String.valueOf(serlcnt));

            waybilltxt = new TextView(BagConsolidateActivity.this);

            if (Build.MODEL.contains("SM-N")) {
                waybilltxt.setLayoutParams(lp);
            } else {
                waybilltxt.setWidth(250);
            }

            waybilltxt.setText(cntTxt);
           System.out.println("cntTxt text is" + cntTxt);


            remaktxt = new TextView(BagConsolidateActivity.this);
            remaktxt.setLayoutParams(lp);
            // remaktxt.setWidth(120);
            remaktxt.setText(errmsgchkbag);

            tr.addView(deltxt);
            tr.addView(srnltxt);
            tr.addView(waybilltxt);
            if(errmsgchkbag!=null){
                tr.addView(remaktxt);
            }
            //  resulttab.removeAllViews();
            resulttab.addView(tr, 0);
            //  bagsercnt++;
            System.out.println("resulttab is:" + resulttab.getChildCount());

            final int finalI = i;
            final int finalI1 = i;
            final int finalI2 = i;
            tr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    System.out.println("v tabseq val:" + v);
                    System.out.println("Rowid tabseq:" + rowid + ",v.getID:" + v.getId());
                    if (rowid != v.getId()) {
                        rowid = v.getId();
                        Toast.makeText(getApplicationContext(), "Press again to delete the row", Toast.LENGTH_LONG).show();

                    } else if (rowid == v.getId()) {
                        System.out.println("value of getId is:" + v.getId() + "rowid " + rowid + "new child:" + resulttab.getChildCount());


                        if (resulttab.getChildCount() != 0) {
                            delawbbag = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                            System.out.println("delawbbag refresh are:" + delawbbag);

                            DelBagcnntRsp = WebService.DeleteBagconnote(logintokn, delawbbag.getText().toString(), Edtbagno.getText().toString());

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

                                    System.out.println("delbagmsg is" + delbagmsg + "resultchkbag" + resultchkbag);

                                } catch (Exception e) {


                                }

                            }
                            System.out.println("connotearray brfre is" + connotearray);
                            if (delstatus.contentEquals("SUCCESS")) {
                                System.out.println("dREfrt is" + v.getId());
                                System.out.println("tr aftr del:" + tr.getId());



//Remove the element from arraylist
                                    AwbLIST.remove(finalI1);
//Recreate JSON Array
                                    AwbArray = new String[AwbLIST.size()];
                                    txtcnttotl.setText(String.valueOf(AwbArray.length));

                                System.out.println("AwbLIST tabseq is" + AwbLIST);
                                // resulttab.removeAllViews();
                                serlcnt = 0;
                                //   connotearray.length();
                                resulttab.removeViewAt(finalI2);
                                resulttab.removeAllViews();
                                txtcnttotl.setText(String.valueOf(AwbArray.length));
                                Toast.makeText(getApplicationContext(), delbagmsg, Toast.LENGTH_LONG).show();
                                //  GetBagAWbs();
                                rowid = -1;
                                int tabcount = (resulttab.getChildCount());
                                //  serlcnt=tabcount;
                                for (int i = 0; i < tabcount; i++) {
                                    tr = (TableRow) resulttab.getChildAt(i);
                                    tr.setId(i);
                                    System.out.println("trba aftr del:" + tr.getId());
                                    // RefreshTable();
                                }
                               // resulttab.removeViewAt(v.getId());
                                resulttab.removeAllViews();
                                serlcnt = 0;
                                txtcnttotl.setText(String.valueOf(AwbLIST.size()));
                                //RefreshTable();
                                TableReseq();
                            } else {

                                if (delbagmsg.contentEquals("Invalid Session")) {
                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                                                    BagConsolidateActivity.this.finish();
                                                    Log.e("here", "hu");
                                                    loginflag = false;
                                                }
                                            });
                                    alertDialog.show();
                                } else {
                                    final AlertDialog alertDialog = new AlertDialog.Builder(BagConsolidateActivity.this).create();
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
                           /*     rowid = -1;
                                int tabcount = (resulttab.getChildCount());
                                //  serlcnt=tabcount;
                                for (int i = 0; i < tabcount; i++) {
                                    trbag = (TableRow) resulttab.getChildAt(i);
                                    trbag.setId(i);
                                    System.out.println("trbag aftr del:" + trbag.getId());
                                   // RefreshTable();
                                }*/


                            //textcount1.setText(String.valueOf(resulttab.getChildCount()));
                               /* rowid = -1;
                                int tabcount = (resulttab.getChildCount());
                                //  serlcnt=tabcount;
                                for (int i = 0; i < tabcount; i++) {
                                    trbag = (TableRow) resulttab.getChildAt(i);
                                    trbag.setId(i);
                                    System.out.println("trbags de aftr del:" + trbag.getId());
                                    // RefreshTable();
                                }*/
                        }
                        rowid = -1;
                        int tabcount = (resulttab.getChildCount());
                        //  serlcnt=tabcount;
                        for (int i = 0; i < tabcount; i++) {
                            tr = (TableRow) resulttab.getChildAt(i);
                            tr.setId(i);
                            System.out.println("trbags de aftr del:" + tr.getId());
                            // RefreshTable();
                        }
                    }
                }
                // }
            });
        }

    }

}


