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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

public class UTLWarehouseActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener,BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener {

    RadioButton BtnStckIn, BtnStckOut, BtnMove;
    TextView textView10, textView11;
    RadioGroup UtlRadioBtngp;
    EditText EdtToLoc, EdtFrmLoc;
    UTLWarehouseActivity _activity;
    KDCData ScannerData;
    KDCReader _kdcReader;
    String waybill;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    public boolean DonotInterruptKDCScan = true;
    String camerabill;
    public int SCANNER_REQUEST_CODE = 123;
    String connote;
    TableRow tr;
    TableRow.LayoutParams lp;
    TableLayout resulttab,resulttabmove,resulttabin,resulttabout;
    TextView waybilltxt, stckstattxt, toloctxt, frmloctxt, notetext, chckremaktxt, deltxt, wghtxt;
    TextView textcount3, textcount2, textcount1, currentdate, txtvtoloctn, txtvfrmloctn;
    ProgressBar Pb;
    int invaldcnt = 0, valdcnt = 0;
    boolean flag = false;
    int rowid = -1;
    int countN = 0;
    String StockStatus;
    Button clear, save, clrawbbtn;
    String todaydate;
    String checkconnote_WHstatus;
    String logintokn;
    ActivityNotification actNoty = new ActivityNotification();
    String whtype = "UTL";
    TextView checktxtdel,checkremartextdel,chcktxt,frmtxt,totxt,checkremartexts;
    ProgressBar   progressBarMain;
    String SetConnoteWHStockTransResp;
    int progrflag=0 ;
    String tablwaybill;
    JSONArray jsonArray = new JSONArray();
    boolean focusfljsonArrayag = false;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utlwarehouse);

        InitializeView();

        // KDCTaskExecutable.execute();

        UtlRadioBtngp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btnstckin:
                        // do operations specific to this selection
                        Log.e("11", "11ee");
                        EdtFrmLoc.setEnabled(false);
                        EdtToLoc.setEnabled(true);
                        StockStatus = "IN";
                        textView10.setVisibility(View.VISIBLE);
                        textView11.setVisibility(View.GONE);
                        txtvfrmloctn.setTextColor(Color.parseColor("#C0C0C0"));
                        txtvtoloctn.setTextColor(Color.parseColor("#000000"));
                        txtvtoloctn.setText("To\nLocation");
                        txtvfrmloctn.setText("Location");
                        EdtFrmLoc.setText("");
                        EdtToLoc.setText("");
                        resulttab.removeAllViews();
                        invaldcnt=0;valdcnt=0;
                        textcount1.setText(String.valueOf(resulttab.getChildCount()));
                        textcount2.setText(String.valueOf(valdcnt));
                        textcount3.setText(String.valueOf(invaldcnt));
                        jsonArray = new JSONArray();
                        System.out.println("jsonArray btn:" + jsonArray);
                        break;
                    case R.id.btnstckout:
                        Log.e("22", "22ee");
                        EdtFrmLoc.setEnabled(false);
                        EdtToLoc.setEnabled(false);
                        EdtFrmLoc.setText("");
                        EdtToLoc.setText("");
                        StockStatus = "OUT";
                        textView10.setVisibility(View.GONE);
                        textView11.setVisibility(View.GONE);
                        txtvfrmloctn.setTextColor(Color.parseColor("#C0C0C0"));
                        txtvtoloctn.setTextColor(Color.parseColor("#C0C0C0"));
                        txtvtoloctn.setText("To\nLocation");
                        txtvfrmloctn.setText("From\nLocation");
                        resulttab.removeAllViews();
                        invaldcnt=0;valdcnt=0;
                        textcount1.setText(String.valueOf(resulttab.getChildCount()));
                        textcount2.setText(String.valueOf(valdcnt));
                        textcount3.setText(String.valueOf(invaldcnt));
                        jsonArray = new JSONArray();
                        System.out.println("jsonArray btn:" + jsonArray);
                        // do operations specific to this selection
                        break;
                    case R.id.btnmove:
                        // do operations specific to this selection
                        Log.e("33", "3333");
                        EdtFrmLoc.setEnabled(true);
                        EdtToLoc.setEnabled(true);
                        StockStatus = "MOVE";
                        textView10.setVisibility(View.VISIBLE);
                        textView11.setVisibility(View.VISIBLE);
                        txtvfrmloctn.setTextColor(Color.parseColor("#000000"));
                        txtvtoloctn.setTextColor(Color.parseColor("#000000"));
                        EdtFrmLoc.setText("");
                        EdtToLoc.setText("");
                        txtvtoloctn.setText("From\nLocation");
                        txtvfrmloctn.setText("To\nLocation");
                        resulttab.removeAllViews();
                        invaldcnt=0;valdcnt=0;
                        textcount1.setText(String.valueOf(resulttab.getChildCount()));
                        textcount2.setText(String.valueOf(valdcnt));
                        textcount3.setText(String.valueOf(invaldcnt));
                        jsonArray = new JSONArray();
                        System.out.println("jsonArray btn:" + jsonArray);
                        break;
                }
            }
        });

     /*   // editext focus
        EdtToLoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focusflag = true;
                    Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                    focusflag = false;
                }
            }
        });*/

        // button to clear invalid awb
        clrawbbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));


                //   System.out.println("resultab val:" + resulttab.getChildCount() + "v id:" + v.getId() + "tr val" + tr.getId());


                if (resulttab.getChildCount() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "There are no waybills to delete.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                } else {
                    int tab = resulttab.getChildCount();
                    for (int l = 0; l < tab; l++) {

                        System.out.println("resultab val l:" + l);
                        checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(2);

                      /*  if(BtnMove.isChecked()){
                            checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(5);
                        }else if(BtnStckIn.isChecked()){
                            checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(4);
                        }else if(BtnStckOut.isChecked()){
                            checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(3);
                        }*/
                        System.out.println("checktxtdel are:" + checktxtdel.getText().toString() + "tr id :" + tr.getId());
                        if (!checktxtdel.getText().toString().matches("Valid")) {
                            invaldcnt = invaldcnt - 1;
                            textcount3.setText(String.valueOf(invaldcnt));
                            resulttab.removeViewAt(l);
                            l = l - 1;
                            tab = tab - 1;
                            System.out.println("tab new val " + tab + "new l" + l);
                        }


                    }

                    textcount1.setText(String.valueOf(resulttab.getChildCount()));
                    textcount3.setText(String.valueOf(invaldcnt));
                    for (int k = 0; k < resulttab.getChildCount(); k++) {
                        tr = (TableRow) resulttab.getChildAt(k);
                        tr.setId(k);
                        System.out.println("tr aftr del:" + tr.getId());
                    }

                }
            }

        });
        // button to To Location
        txtvtoloctn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                EdtToLoc.setText("");

            }

        });


        // button to from Location
        txtvfrmloctn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                EdtFrmLoc.setText("");

            }

        });

        // code for save button
        save.setOnClickListener(new View.OnClickListener() {

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
                    tablwaybill = waybilltxt.getText().toString();
                    tr = (TableRow) resulttab.getChildAt(j);

                    waybilltxt.setId(j);
                    tr.setId(j);
                    checkremartexts = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(2);
                    if(BtnStckIn.isChecked()){
                        // checkremartexts = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(4);
                        frmtxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(4);
                    }else  if(BtnStckOut.isChecked()){
                        // checkremartexts = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);
                    }else  if(BtnMove.isChecked()){
                        // checkremartexts = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(5);
                        totxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(5);
                        frmtxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(4);
                    }

                    System.out.println("checkremartexts on trn:" + checkremartexts.getText().toString());
                    if (!checkremartexts.getText().toString().matches("Valid")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {
                        try {
                            postedJSON.put("Connote", tablwaybill);
                            if(BtnStckIn.isChecked()){
                                postedJSON.put("LocFrom", frmtxt.getText().toString());
                                postedJSON.put("LocTo","");
                            }else if(BtnStckOut.isChecked()){
                                postedJSON.put("LocFrom", "");
                                postedJSON.put("LocTo","");
                            }else if(BtnMove.isChecked()){
                                postedJSON.put("LocFrom", frmtxt.getText().toString());
                                postedJSON.put("LocTo",totxt.getText().toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                        jsonArray.put(postedJSON);
                        progrflag ++;


                    }

                    System.out.println("jsonArray on trn:" + jsonArray+ "counter:"+jsonArray.length()+"progrflag"+progrflag+ "logintokn"+logintokn);
/*
                        SetConnoteWHStockTransResp = WebService.SetConnoteWHStockTrans(logintokn, whtype,StockStatus,jsonArray);
                        progrflag++;

                        // set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, tablwaybill, evnttxt.getText().toString(), notetext.getText().toString(), racktext.getText().toString());
                        System.out.println("SetConnoteWHStockTransResp on trn:" + SetConnoteWHStockTransResp+"progrflag"+progrflag);
                        // System.out.println("loppre"+ Looper.myLooper() == Looper.getMainLooper().toString());
                        if (SetConnoteWHStockTransResp != null) {

                            if (SetConnoteWHStockTransResp.matches("TRUE")) {
                                resulttab.removeViewAt(j);
                                j = j - 1;
                                tabcount = tabcount - 1;
                                System.out.println("new result tab count:" + tabcount);
                                progrflag=progrflag-1;

                                System.out.println("progrflag:" + progrflag);
                                Toast toast = Toast.makeText(getApplicationContext(), "Airwaybill saved successfully", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                textcount2.setText(String.valueOf(valdcnt=valdcnt-1));
                                textcount1.setText(String.valueOf(resulttab.getChildCount()));
                                for (int k = 0; k < resulttab.getChildCount(); k++) {
                                    tr = (TableRow) resulttab.getChildAt(k);
                                    tr.setId(k);

                                    System.out.println("tr save add:" + tr.getId());
                                }

                            } else {
                                if (SetConnoteWHStockTransResp.matches("FALSE")) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please Check again", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    //   progressBarMain.setVisibility(View.INVISIBLE);
                                    v.setActivated(false);
                                    v.setSelected(false);
                                    return;
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), SetConnoteWHStockTransResp, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    // progressBarMain.setVisibility(View.INVISIBLE);
                                    v.setActivated(false);
                                    v.setSelected(false);
                                    return;
                                }
                            }


                        }*/

                    //   }






                }

                System.out.println(" tr tag"+tr.getId());
                if(jsonArray.length()!= 0){
                    SetConnoteWHStockTransResp = WebService.SetConnoteWHStockTrans(logintokn, whtype,StockStatus,jsonArray);
                }


                //set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, tablwaybill, evnttxt.getText().toString(), notetext.getText().toString(), racktext.getText().toString());
                System.out.println("SetConnoteWHStockTransResp on trn:" + SetConnoteWHStockTransResp+"progrflag"+progrflag);



                if (SetConnoteWHStockTransResp != null) {

                    if (SetConnoteWHStockTransResp.matches("TRUE")) {
                        v.setActivated(false);
                        v.setSelected(false);

                        int tab = resulttab.getChildCount();
                        for (int l = 0; l < tab; l++) {

                            System.out.println("resultab val l:" + l);
                            checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(2);
                               /* if(BtnMove.isChecked()){
                                    checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(5);
                                }else if(BtnStckIn.isChecked()){
                                    checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(4);
                                }else if(BtnStckOut.isChecked()){
                                    checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(l)).getChildAt(3);
                                }*/
                            System.out.println("checktxtdel save are:" + checktxtdel.getText().toString() + "tr save id :" + tr.getId());
                            if (checktxtdel.getText().toString().matches("Valid")) {
                                valdcnt = valdcnt - 1;
                                textcount2.setText(String.valueOf(valdcnt));
                                resulttab.removeViewAt(l);
                                l = l - 1;
                                tab = tab - 1;
                                progrflag = progrflag - 1;

                                Toast toast = Toast.makeText(getApplicationContext(), "Airwaybills saved successfully", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                jsonArray = new JSONArray();

                                System.out.println("jsonArray onsave :" + jsonArray.length());
                                System.out.println("tab new val " + tab + "new l" + l);
                            }/* else if (!checkremartexts.getText().toString().matches("Valid")) {
                                  //  Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_SHORT);
                                  //  toast.setGravity(Gravity.CENTER, 0, 0);
                                  //  toast.show();
                                }*/

                        }

                        textcount1.setText(String.valueOf(resulttab.getChildCount()));
                        textcount3.setText(String.valueOf(invaldcnt));
                        for (int k = 0; k < resulttab.getChildCount(); k++) {
                            tr = (TableRow) resulttab.getChildAt(k);
                            tr.setId(k);
                            System.out.println("tr aftr del:" + tr.getId());
                        }

                        System.out.println("progrflag:" + progrflag);




                    } else {
                        if (SetConnoteWHStockTransResp.matches("FALSE")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please Check again", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            //   progressBarMain.setVisibility(View.INVISIBLE);
                            v.setActivated(false);
                            v.setSelected(false);
                            return;
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), SetConnoteWHStockTransResp, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            // progressBarMain.setVisibility(View.INVISIBLE);
                            v.setActivated(false);
                            v.setSelected(false);
                            return;
                        }
                    }
                    //SetConnoteWHStockTransResp = null;


                }
                textcount1.setText(String.valueOf(resulttab.getChildCount()));
                //  textcount2.setText(String.valueOf(valdcnt=valdcnt-1));
                // valdcnt=0;
                // valdcnt=valdcnt-1;
                System.out.println("prgr val:" + progrflag + "reslt " + resulttab.getChildCount()+"vald"+valdcnt+"i b"+invaldcnt);

                if(progrflag==valdcnt){
                    // progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);
                }


                System.out.println("resulttab.getChildCount()"+resulttab.getChildCount());


            }

        });

        // code for clear button
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                resulttab.removeAllViews();
                valdcnt = 0;
                invaldcnt = 0;
                EdtToLoc.setText("");
                EdtFrmLoc.setText("");

                EdtFrmLoc.setEnabled(false);
                textcount1.setText(String.valueOf(0));
                textcount2.setText(String.valueOf(0));
                textcount3.setText(String.valueOf(0));

                BtnStckIn.setChecked(true);
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();

                // v.getBackground().clearColorFilter();
            }

        });
        if(barcodeReader!= null){
            barcodeReader.addBarcodeListener(UTLWarehouseActivity.this);
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent honeywellevent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (honeywellevent != null) {

                    // ScannerData = honeywellevent;
                    waybill = honeywellevent.getBarcodeData();

                    //  if (Check_ValidWaybill(kdcbarcodedata.GetData()) == false) {
                    if (BtnStckIn.isChecked() && EdtToLoc.getText().toString().contentEquals("")) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                EdtToLoc.setText(honeywellevent.getBarcodeData());
                            }
                        });
                        return;
                    } else if (BtnStckOut.isChecked()) {
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                EdtToLoc.setText("");
                                EdtFrmLoc.setText("");
                            }
                        });
                        //  return;
                    } else if (BtnMove.isChecked()) {
                        if (EdtToLoc.getText().toString().contentEquals("")) {
                            _activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    EdtToLoc.setText(honeywellevent.getBarcodeData());


                                }
                            });
                            return;
                        } else if (EdtFrmLoc.getText().toString().contentEquals("")) {
                            _activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    EdtFrmLoc.setText(honeywellevent.getBarcodeData());


                                }
                            });
                            return;
                        }

                    }


                    // }else
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


                                    if (Validations() == true) {

                                        new checkUTLwaybill(connote).execute();
                                    }

                                }
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

            /*    if (!EdtToLoc.getText().toString().equals("")) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                } else if (BtnStckIn.isChecked()&&EdtToLoc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter To Location!", Toast.LENGTH_SHORT).show();
                }else if (BtnMove.isChecked()&&EdtToLoc.getText().toString().equals("")&& EdtFrmLoc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter To and From Location!", Toast.LENGTH_SHORT).show();
                }else if(BtnStckOut.isChecked()) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                }*/
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
         /*       if (!EdtToLoc.getText().toString().equals("")) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                } else if (EdtToLoc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_SHORT).show();
                }*/
                if (!EdtToLoc.getText().toString().equals("")) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                } else if (BtnStckIn.isChecked()&&EdtToLoc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter To Location!", Toast.LENGTH_SHORT).show();
                }else if (BtnMove.isChecked()&&EdtToLoc.getText().toString().equals("")&& EdtFrmLoc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter To and From Location!", Toast.LENGTH_SHORT).show();
                }else if(BtnStckOut.isChecked()) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(barcodeReader!=null){
                barcodeReader.release();
                barcodeReader.removeBarcodeListener(_activity);
                UTLWarehouseActivity.this.finish();
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
                UTLWarehouseActivity.this.finish();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isActivityActiveFlag) isActivityActiveFlag = false;
        _activity = this;
        //   getcouriers();

        if (!KDCTaskExecutable.getStatus().equals(AsyncTask.Status.RUNNING) && !KDCTaskExecutable.getStatus().equals(AsyncTask.Status.FINISHED)) {
            //KDCTaskExecutable.cancel(true);
            KDCTaskExecutable.execute();
            System.out.println(" KDCTask Executed");
        }
        if (barcodeReader != null) {
            try {

                barcodeReader.claim();
                System.out.println("barcode evt onres:" + barcodeReader);
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        System.out.println("Resume activate in rst");
    }

    public void InitializeView() {

        BtnStckIn = (RadioButton) findViewById(R.id.btnstckin);
        BtnStckOut = (RadioButton) findViewById(R.id.btnstckout);
        BtnMove = (RadioButton) findViewById(R.id.btnmove);
        clear = (Button) findViewById(R.id.btnclear);
        save = (Button) findViewById(R.id.btnsave);
        textView10 = (TextView) findViewById(R.id.textView10);
        textView11 = (TextView) findViewById(R.id.textView11);
        UtlRadioBtngp = (RadioGroup) findViewById(R.id.utlradiobtn);
        EdtToLoc = (EditText) findViewById(R.id.edttoloc);
        EdtFrmLoc = (EditText) findViewById(R.id.edtfrmloc);
        EdtFrmLoc.setEnabled(false);
        resulttab = (TableLayout) findViewById(R.id.resulttable1);
        Pb = (ProgressBar) findViewById(R.id.progressBar);
        textcount1 = (TextView) findViewById(R.id.textcount1);
        textcount2 = (TextView) findViewById(R.id.textcount2);
        textcount3 = (TextView) findViewById(R.id.textcount3);
        currentdate = (TextView) findViewById(R.id.datespinner);
        txtvfrmloctn = (TextView) findViewById(R.id.txtvfrmloctn);
        txtvtoloctn = (TextView) findViewById(R.id.txtvtoloctn);
        clrawbbtn = (Button) findViewById(R.id.clrawbbtn);
        resulttabmove =(TableLayout)findViewById(R.id.resulttable1);
        resulttabin =(TableLayout)findViewById(R.id.resulttable1);
        resulttabout =(TableLayout)findViewById(R.id.resulttable1);
        // resulttab =(TableLayout)findViewById(R.id.resulttable1);
        BtnStckIn.setChecked(true);
        txtvfrmloctn.setTextColor(Color.parseColor("#C0C0C0"));
        txtvtoloctn.setText("To\nLocation");
        txtvfrmloctn.setText("From\nLocation");
        Calendar myCalendar = Calendar.getInstance();
        textView10.setVisibility(View.VISIBLE);
        StockStatus = "IN";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
        todaydate = format.format(myCalendar.getTime());
        currentdate.setText(todaydate);
        progressBarMain = (ProgressBar) findViewById(R.id.progressBarMain);
        logintokn= actNoty.getToken(UTLWarehouseActivity.this);
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

            if(barcodeReader!= null){
                barcodeReader.removeBarcodeListener(UTLWarehouseActivity.this);
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


    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC UTL Activity BarCodeReceived Block");
        System.out.print("KDCReader UTL Activity  BarCodeReceived Block");

        // if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();

            //  if (Check_ValidWaybill(kdcbarcodedata.GetData()) == false) {
            if (BtnStckIn.isChecked() && EdtToLoc.getText().toString().contentEquals("")) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        EdtToLoc.setText(ScannerData.GetData());
                    }
                });
                return;
            } else if (BtnStckOut.isChecked()) {
                _activity.runOnUiThread(new Runnable() {
                    public void run() {
                        EdtToLoc.setText("");
                        EdtFrmLoc.setText("");
                    }
                });
                //  return;
            } else if (BtnMove.isChecked()) {
                if (EdtToLoc.getText().toString().contentEquals("")) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            EdtToLoc.setText(ScannerData.GetData());


                        }
                    });
                    return;
                } else if (EdtFrmLoc.getText().toString().contentEquals("")) {
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            EdtFrmLoc.setText(ScannerData.GetData());


                        }
                    });
                    return;
                }

            }


            // }else
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


                            if (Validations() == true) {

                                new checkUTLwaybill(connote).execute();
                            }

                        }
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

        Log.i("KDCReader", "KDC UTL Activity connection changed block");
        System.out.print("KDCReader UTL Activity connection changed block");
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

        if (s.length() == 10 || s.length() == 12) {
            return StringUtils.isNumeric(s) == true;
        } else if (s.length() == 18) {
            return StringUtils.isAlphanumeric(s) == true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent

            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");

                camerabill = contents;

                if( BtnStckIn.isChecked()&&EdtToLoc.getText().toString().contentEquals("")){
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            EdtToLoc.setText(camerabill);
                        }
                    });
                    return;
                }else if(BtnStckOut.isChecked()){
                    _activity.runOnUiThread(new Runnable() {
                        public void run() {
                            EdtToLoc.setText("");
                            EdtFrmLoc.setText("");
                        }
                    });
                }else if(BtnMove.isChecked()){
                    if(EdtToLoc.getText().toString().contentEquals("")){
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                EdtToLoc.setText(camerabill);

                            }
                        });
                        return;
                    }else if(EdtFrmLoc.getText().toString().contentEquals("")){
                        _activity.runOnUiThread(new Runnable() {
                            public void run() {
                                EdtFrmLoc.setText(camerabill);

                            }
                        });
                        return;
                    }

                }
              /*  if (Check_ValidWaybill(camerabill) == false) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid Waybill", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }*/
                if (Check_ValidWaybill(camerabill) == true) {
                    connote = camerabill;
                    new checkUTLwaybill(camerabill).execute();
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

    public class checkUTLwaybill extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public checkUTLwaybill(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;


            System.out.println("taskwaybill pre:" + Taskwabill);
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
            /*  if(BtnMove.isChecked()){
                    checkconnote_WHstatus = WebService.CheckConnoteWHStockTrans(logintokn,whtype ,wabil,StockStatus,EdtFrmLoc.getText().toString(),EdtToLoc.getText().toString());
                }else{
                    checkconnote_WHstatus = WebService.CheckConnoteWHStockTrans(logintokn,whtype ,wabil,StockStatus,EdtToLoc.getText().toString(),EdtFrmLoc.getText().toString());
                }*/

                checkconnote_WHstatus = WebService.CheckConnoteWHStockTrans(logintokn,whtype ,wabil,StockStatus,EdtToLoc.getText().toString(),EdtFrmLoc.getText().toString());
                System.out.println("checkconnote_WHstatus is:" + checkconnote_WHstatus);

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

            tr = new TableRow(UTLWarehouseActivity.this);

            if (Build.MODEL.contains("SM-N")) {


                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
                //   lp.setMargins(18, 2, 95, 2);
                lp.setMargins(20, 5, 200, 0);

                tr.setLayoutParams(lp);
                System.out.println("tr id:" + tr.getId());

            } else {
                lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tr.setId(resulttab.getChildCount());
                tr.setLayoutParams(lp);
                lp.setMargins(0, 5, 70, 0);
            }

            //	System.out.println("response : " + response.toString());


            deltxt = new TextView(UTLWarehouseActivity.this);
            deltxt.setLayoutParams(lp);
            deltxt.setText("Delete");

            waybilltxt = new TextView(UTLWarehouseActivity.this);
            waybilltxt.setLayoutParams(lp);
            waybilltxt.setText(Taskwabill);
            System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);


            stckstattxt = new TextView(UTLWarehouseActivity.this);
            // stckstattxt.setLayoutParams(lp);
            stckstattxt.setWidth(250);
            stckstattxt.setText(StockStatus);



            toloctxt = new TextView(UTLWarehouseActivity.this);
            // toloctxt.setLayoutParams(lp);
            toloctxt.setWidth(350);
            toloctxt.setText(EdtToLoc.getText().toString());


            frmloctxt = new TextView(UTLWarehouseActivity.this);
            // frmloctxt.setLayoutParams(lp);
            frmloctxt.setWidth(350);
            frmloctxt.setText(EdtFrmLoc.getText().toString());

            //  System.out.println("cnametxt  is"+cname1);


            chckremaktxt = new TextView(UTLWarehouseActivity.this);
            chckremaktxt.setLayoutParams(lp);
            // chckremaktxt.setWidth(50);
            if(checkconnote_WHstatus!=null) {
                if (checkconnote_WHstatus.contentEquals("TRUE")) {
                    chckremaktxt.setText("Valid");
                } else if(checkconnote_WHstatus.contentEquals("Invalid Session")) {


                    final AlertDialog alertDialog = new AlertDialog.Builder(UTLWarehouseActivity.this).create();
                    alertDialog.setTitle("Invalid Session");
                    alertDialog.setMessage("Your session is expired. Please re-login to continue");
                    alertDialog.setCanceledOnTouchOutside(false);
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
                                    UTLWarehouseActivity.this.finish();
                                    Log.e("here","hu");
                                    loginflag= false;
                                }
                            });
                    alertDialog.show();



                    chckremaktxt.setText(checkconnote_WHstatus);
                }else{
                    chckremaktxt.setText(checkconnote_WHstatus);
                }

            }


            if (!checkconnote_WHstatus.matches("TRUE")) {
                System.out.println("invaldcnt valid" + invaldcnt);
                invaldcnt = invaldcnt + 1;
                System.out.println("invaldcnt after" + invaldcnt);
                tr.setBackgroundColor(Color.parseColor("#d73232"));
            } else {
                System.out.println("valdcnt vef" + valdcnt);
                valdcnt = valdcnt + 1;
                System.out.println("valdcnt after" + valdcnt);

            }



            tr.addView(deltxt);
            tr.addView(waybilltxt);
            tr.addView(chckremaktxt);
            tr.addView(stckstattxt);
            if( BtnMove.isChecked()) {
                tr.addView(toloctxt);
                tr.addView(frmloctxt);
            }else if(BtnStckIn.isChecked()){
                tr.addView(toloctxt);
            }



            System.out.println("res chld:" + resulttab.getChildCount());

            if (resulttab.getChildCount() == 0) {
                resulttab.addView(tr,0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            } else {
                //	counttxt.setText(String.valueOf(resulttab.getChildCount()));
                //	System.out.println("value of counttxt in pickup update"+counttxt.toString());

                String wb;
                for (int i = 0; i < resulttab.getChildCount(); i++) {

                    TextView wbill = (TextView) ((TableRow) resulttab.getChildAt(i)).getChildAt(1);
                    wb = wbill.getText().toString();

                    System.out.println("wb wal:" + wb + "waybill is:" + connote);
                    if (!connote.equals(wb)) {
                        flag = true;

                    } else if (connote.equals(wb)) {
                        flag = false;
                        System.out.println(flag);

                        break;

                    }
                }

                if (flag) {
                    resulttab.addView(tr,0,new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    System.out.println("value of counttxt in else" + countN);
                    System.out.println("value of INVALID in else" + String.valueOf(invaldcnt - 1));
                    countN = countN - 1;
                    textcount1.setText(String.valueOf(resulttab.getChildCount()));

                    if (!checkconnote_WHstatus.matches("TRUE")) {
                        textcount3.setText(String.valueOf(invaldcnt = invaldcnt - 1));
                        tr.setBackgroundColor(Color.parseColor("#d73232"));
                        Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        textcount2.setText(String.valueOf(valdcnt = valdcnt - 1));
                        Toast.makeText(getApplicationContext(), "Duplicate WayBill not allowed,Please Scan again", Toast.LENGTH_LONG).show();
                        return;
                    }


                }


                // Pb.setVisibility(View.INVISIBLE);


            }

            System.out.println("text tot valid" + resulttab.getChildCount());
            textcount1.setText(String.valueOf(resulttab.getChildCount()));
            textcount3.setText(String.valueOf(invaldcnt));
            textcount2.setText(String.valueOf(valdcnt));

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
                            checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(2);
                           /* if(BtnMove.isChecked()){
                                checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(5);
                            }else if(BtnStckIn.isChecked()){
                                checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(4);
                            }else if(BtnStckOut.isChecked()){
                                checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(3);
                            }
*/
                            System.out.println("checkremartextdel are:" + checkremartextdel.getText().toString());

                            //   checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(rowid)).getChildAt(3);
                            //   System.out.println("checkremartextdel are:" + checkremartextdel.getText().toString());
                            if (!checkremartextdel.getText().toString().matches("Valid")) {
                                invaldcnt = invaldcnt - 1;
                                textcount3.setText(String.valueOf(invaldcnt));
                            } else {
                                valdcnt = valdcnt - 1;
                                textcount2.setText(String.valueOf(valdcnt));
                            }
                            resulttab.removeViewAt(v.getId());

                            System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                            Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                            rowid = -1;
                            int tabcount = (resulttab.getChildCount());
                            for (int i = 0; i < tabcount; i++) {
                                tr = (TableRow) resulttab.getChildAt(i);
                                tr.setId(i);
                                System.out.println("tr aftr del:" + tr.getId());
                            }


                            textcount1.setText(String.valueOf(resulttab.getChildCount()));

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

       /* if(BtnStckIn.isChecked()){
            StockinView(Taskwabill);
            }else if(BtnStckOut.isChecked()){
                StockoutView(Taskwabill);
            }else if(BtnMove.isChecked()){
                StockmoveView(Taskwabill);
            }*/
        }

    }

    public boolean Validations() {

        if (BtnStckIn.isChecked()) {
            if (EdtToLoc.getText().toString().equals("")) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
                return false;
            }
            else if (EdtToLoc.getText().toString().equals("")) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
                return false;
            }

        } else if (BtnMove.isChecked()) {
            if (EdtToLoc.getText().toString().equals("") || EdtFrmLoc.getText().toString().equals("")) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter the To & From Location!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
                return false;

            }else if (EdtToLoc.getText().toString().matches(EdtFrmLoc.getText().toString())) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "To and From Location cannot be the same!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
                return false;

            }


        }
        return true;
    }




}