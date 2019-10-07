package com.postaplus.etrack;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.ArrayList;

import koamtac.kdc.sdk.KDCBarcodeDataReceivedListener;
import koamtac.kdc.sdk.KDCConnectionListener;
import koamtac.kdc.sdk.KDCConstants;
import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCDataReceivedListener;
import koamtac.kdc.sdk.KDCReader;
import util.ActivityNotification;
import utils.Utils;
import webservice.FuncClasses.Events;
import webservice.WebService;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.postaplus.etrack.LoginActivity.PREFS_NAME;
import static com.postaplus.etrack.LoginActivity.PREF_loginstatus;
import static com.postaplus.etrack.LoginActivity.loginval;
import static com.postaplus.etrack.MainActivity.barcodeReader;
import static com.postaplus.etrack.ScreenActivity.loginflag;

public class EventActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener, BarcodeReader.BarcodeListener,BarcodeReader.TriggerListener{

    //  private TextView eventlist;
    private String[] arraySpinner, evtIdarr;
    Button clear, save;
    String evntIde, note, evtlist, rckno, wght, evtname;
    String[] eventcode, eventname ,eventerr;
    SQLiteDatabase sqldb = null;
    DatabaseHandler db;
    String set_connoteevents, checkconnote_status;
    String password, username, connote, eventnote, rackno, evtcode;
    EditText noteet, racket, weightet;
    TableLayout resulttab;
    TableRow tr;
    String usrnamee, passworde;
    int rowid = -1;

    TableRow.LayoutParams lp;
    TextView conntxt, eventxt, evtcodetxt, notetxt, deltxt, racktxt, wghtxt, chkrmktxt;

    KDCData ScannerData;
    KDCReader _kdcReader;
    public EventActivity MYActivity;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag = false;
    Thread ThrKdc;
    EventActivity _activity;
    String waybill;
    TextView waybilltxt, evnttxt, evntcodetxt, racktext, notetext, chckremaktxt, checkremartext;
    ProgressBar Pb;
    TextView textcount1, textcount2, textcount3;
    int invaldcnt = 0, valdcnt = 0;
    boolean flag = false;
    int countN = 0;

    String camerabill;
    public int SCANNER_REQUEST_CODE = 123;
    ActivityNotification actNoty = new ActivityNotification();

    Events[] evtresponse;
    ArrayList<Events> Evntarrlist = new ArrayList<Events>();
    Events eventslist;
    int i = 0;
    String spinevtcode, spinevtname;
    AutoCompleteTextView eventlist;
    TextView checkremartextdel, checktxtdel;
    String textviewString;
    ArrayList<String> listevt;
    String slectdevent;
    Button clrawbbtn;
    String tablwaybill;
    ProgressBar progressBarMain;
    Handler progressBarHandler = new Handler();
    int progrflag=0 ;
    String chectext;
    String setcnnteResp;
    static boolean DonotInterruptKDCScan = true;
    String logintoken;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);*/
        // usrnamee = getIntent().getExtras().getString("username");
        // passworde = getIntent().getExtras().getString("password");
        usrnamee = actNoty.getUserName(EventActivity.this);
        passworde = actNoty.getPassword(EventActivity.this);
        eventlist = (AutoCompleteTextView) findViewById(R.id.eventspinner);
        clear = (Button) findViewById(R.id.btnclear);
        save = (Button) findViewById(R.id.btnsave);
        noteet = (EditText) findViewById(R.id.editNote);
        racket = (EditText) findViewById(R.id.editRackno);
        weightet = (EditText) findViewById(R.id.editWeight);
        resulttab = (TableLayout) findViewById(R.id.resulttable1);
        Pb = (ProgressBar) findViewById(R.id.progressBar);
        textcount1 = (TextView) findViewById(R.id.textcount1);
        textcount2 = (TextView) findViewById(R.id.textcount2);
        textcount3 = (TextView) findViewById(R.id.textcount3);
        clrawbbtn = (Button) findViewById(R.id.clrawbbtn);
        progressBarMain = (ProgressBar) findViewById(R.id.progressBarMain);

        weightet.setEnabled(false);
        weightet.setClickable(false);
        weightet.setText("0.000");
        logintoken=actNoty.getToken(EventActivity.this);
        getevents();
        // KDCTaskExecutable.execute();
        // barcodeReader.addBarcodeListener(_activity);
        System.out.println("username in evtact is" + usrnamee + "password in evtact is" + passworde);
        progressBarMain.setVisibility(View.INVISIBLE);

        if(barcodeReader!= null){
            barcodeReader.addBarcodeListener(EventActivity.this);
        }

        // Event Spinner
        final ArrayAdapter adapter = new
                ArrayAdapter(EventActivity.this, android.R.layout.simple_list_item_1, listevt);
        eventlist.setAdapter(adapter);


        eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                noteet.setText("");
                racket.setText("");
                weightet.setText("0.000");
                weightet.setEnabled(false);
                weightet.setClickable(false);
                spinevtname = adapter.getItem(position).toString();
                for (int j = 0; j < Evntarrlist.size(); j++) {
                    System.out.println("Evntarrlist" + Evntarrlist.get(j).EventName + "spinevtname:" + spinevtname);
                    if (Evntarrlist.get(j).EventName == spinevtname) {
                        System.out.println("position befr is" + position + "j:" + j);
                        position = j;
                        System.out.println("position aftr is" + position + "j:" + j);
                        break;

                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                System.out.println("spinevtname is" + spinevtname + "spincode:" + spinevtcode);
                spinevtcode = Evntarrlist.get(position).EventCode;

                if (spinevtcode.matches("AS")) {
                    weightet.setEnabled(true);
                    weightet.setClickable(true);
                }

            }
        });

        eventlist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                eventlist.setText("");
                //eventlist.clearListSelection();
                new AlertDialog.Builder(EventActivity.this)

                        .setTitle("Select Event")

                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int i) {
                                System.out.println("i frm click" + i);
                                spinevtname = String.valueOf(Evntarrlist.get(i).EventName);
                                System.out.println("spinevtname is" + spinevtname);
                                spinevtcode = String.valueOf(Evntarrlist.get(i).EventCode);
                                //
                                dialog.dismiss();
                                eventlist.setText(spinevtname);
                                System.out.println("spinevtname frm click" + spinevtname + "spinevtcode frm click" + spinevtcode);
                            }

                        }).create().show();

            }

        });

        // code for clear button
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                resulttab.removeAllViews();
                valdcnt = 0;
                invaldcnt = 0;
                noteet.setText("");
                racket.setText("");
                weightet.setText("0.000");
                weightet.setEnabled(false);
                weightet.setEnabled(false);
                weightet.setClickable(false);
                eventlist.setText("");
                textcount1.setText(String.valueOf(0));
                textcount2.setText(String.valueOf(0));
                textcount3.setText(String.valueOf(0));


                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();

                v.getBackground().clearColorFilter();
            }

        });


        // button to clear invalid awb
        clrawbbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));


                //   System.out.println("resultab val:" + resulttab.getChildCount() + "v id:" + v.getId() + "tr val" + tr.getId());


                if(resulttab.getChildCount()==0){
                    Toast toast = Toast.makeText(getApplicationContext(), "There are no waybills to delete.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }else {
                    int tab = resulttab.getChildCount();
                    for (int j = 0; j < tab; j++) {

                        System.out.println("resultab val j:" + j);
                        checktxtdel = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);
                        System.out.println("checktxtdel are:" + checktxtdel.getText().toString() + "tr id :" + tr.getId());
                        if (!checktxtdel.getText().toString().matches("VALID")) {
                            invaldcnt = invaldcnt - 1;
                            textcount3.setText(String.valueOf(invaldcnt));
                            resulttab.removeViewAt(j);
                            j = j - 1;
                            tab = tab - 1;

                            System.out.println("tab new val " + tab + "new j" + j);
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
        // code for save button
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
               /* LongOperation longOperation = new LongOperation(getBaseContext());
                longOperation.execute();*/
                // progrflag=resulttab.getChildCount();
                // v.setBackgroundColor(Color.parseColor("#fff44f"));
                System.out.println("v:"+v.isSelected());

                if(resulttab.getChildCount()==0){
                    progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);

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

                    System.out.println("tabcount is:" + tabcount + "j val " + j);
                    waybilltxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(1);
                    checkremartext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);
                    evnttxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(5);
                    notetext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(6);
                    racktext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(7);
                    System.out.println("checkremartext is:" + checkremartext.getText().toString() + "j val:" + j);
                    System.out.println("waybilltxt is:" + waybilltxt.getText().toString());
                    tablwaybill = waybilltxt.getText().toString();
                    waybilltxt.setId(v.getId());
                    tr = (TableRow) resulttab.getChildAt(j);
                    checkremartext.setId(j);
                   /* note = notetext.getText().toString();
                    rackno = racktext.getText().toString();
                    evtcode = evnttxt.getText().toString();
                    chectext = checkremartext.getText().toString();
*/
                    if (!checkremartext.getText().toString().matches("VALID")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        progressBarMain.setVisibility(View.INVISIBLE);

                        v.setActivated(false);
                        v.setSelected(false);
                        //dialog.dismiss();
                        //  return;
                    } else {

                      /*  try {
                            set_connoteevents = new SaveTask(tablwaybill, evtcode, note, rackno, chectext).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }*/


                        set_connoteevents = WebService.SetConnoteEvents(logintoken, tablwaybill, evnttxt.getText().toString(), notetext.getText().toString(), racktext.getText().toString());
                        progrflag++;

                        // set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, tablwaybill, evnttxt.getText().toString(), notetext.getText().toString(), racktext.getText().toString());
                        System.out.println("set_connoteevents on trn:" + set_connoteevents+"progrflag"+progrflag);
                        // System.out.println("loppre"+ Looper.myLooper() == Looper.getMainLooper().toString());
                        if (set_connoteevents != null) {

                            if (set_connoteevents.matches("TRUE")) {
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
                                if (set_connoteevents.matches("FALSE")) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Please Check again", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    progressBarMain.setVisibility(View.INVISIBLE);
                                    v.setActivated(false);
                                    v.setSelected(false);
                                    return;
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), set_connoteevents, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    progressBarMain.setVisibility(View.INVISIBLE);
                                    v.setActivated(false);
                                    v.setSelected(false);
                                    return;
                                }
                            }
                      /*  if (set_connoteevents.matches("Insufficient Privileges")) {
                                Toast toast = Toast.makeText(getApplicationContext(), set_connoteevents, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            } else {
                                resulttab.removeViewAt(j);
                            }*/


                        }
                        //  dialog.dismiss();
                    }


                    // textcount1.setText(String.valueOf(resulttab.getChildCount()));
                    // textcount2.setText(String.valueOf(resulttab.getChildCount()));
                    //      System.out.println("setconnResp:" + set_connoteevents);



                }
                textcount1.setText(String.valueOf(resulttab.getChildCount()));
                //  textcount2.setText(String.valueOf(valdcnt=valdcnt-1));
                // valdcnt=0;
                // valdcnt=valdcnt-1;
                System.out.println("prgr val:"+progrflag+"reslt "+resulttab.getChildCount());

                if(progrflag==resulttab.getChildCount()){
                    // progressBarMain.setVisibility(View.INVISIBLE);
                    v.setActivated(false);
                    v.setSelected(false);

                }


                System.out.println("resulttab.getChildCount()"+resulttab.getChildCount());


            }

        });
  /*      save.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //Button Pressed
                    save.setBackgroundColor(Color.parseColor("#fff44f"));
                    progressBarMain.setVisibility(View.VISIBLE);
                    v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                    v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                    int tabcount = resulttab.getChildCount();
                    for (int j = 0; j < tabcount; j++) {
                        System.out.println("tabcount is:" + tabcount + "j val " + j);
                        waybilltxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(1);
                        checkremartext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(3);
                        evnttxt = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(5);
                        notetext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(6);
                        racktext = (TextView) ((TableRow) resulttab.getChildAt(j)).getChildAt(7);
                        System.out.println("checkremartext is:" + checkremartext.getText().toString() + "j val:" + j);
                        System.out.println("waybilltxt is:" + waybilltxt.getText().toString());
                        tablwaybill = waybilltxt.getText().toString();
                        waybilltxt.setId(v.getId());
                        tr = (TableRow) resulttab.getChildAt(j);
                        checkremartext.setId(j);
                   *//* note = notetext.getText().toString();
                    rackno = racktext.getText().toString();
                    evtcode = evnttxt.getText().toString();
                    chectext = checkremartext.getText().toString();
*//*
                        if (!checkremartext.getText().toString().matches("VALID")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            progressBarMain.setVisibility(View.INVISIBLE);
                           // v.setBackgroundColor(Color.parseColor("#e0e0e0"));
                            //dialog.dismiss();
                            return false;
                        } else {

                      *//*  try {
                            set_connoteevents = new SaveTask(tablwaybill, evtcode, note, rackno, chectext).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }*//*
                            set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, tablwaybill, evnttxt.getText().toString(), notetext.getText().toString(), racktext.getText().toString());
                            System.out.println("set_connoteevents on trn:" + set_connoteevents+"setcnnteResp"+setcnnteResp);
                            progrflag++;
                            System.out.println("progrflag:"+progrflag);
                            if (set_connoteevents != null) {

                                if (set_connoteevents.matches("TRUE")) {
                                    resulttab.removeViewAt(j);
                                    j = j - 1;
                                    tabcount = tabcount - 1;
                                    valdcnt=valdcnt-1;
                                    textcount2.setText(String.valueOf(valdcnt));
                                    System.out.println("new result tab count:" + tabcount);

                                } else {
                                    if (set_connoteevents.matches("FALSE")) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please Check again", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        progressBarMain.setVisibility(View.INVISIBLE);
                                        return false;
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), setcnnteResp, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        progressBarMain.setVisibility(View.INVISIBLE);
                                        return false;
                                    }
                                }



                            }

                        }

                    }
                    textcount1.setText(String.valueOf(resulttab.getChildCount()));

                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    v.setBackgroundColor(Color.parseColor("#e0e0e0"));
                    v.getBackground().clearColorFilter();

                    if(progrflag==resulttab.getChildCount()-1){
                        progressBarMain.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }


        });*/
        // progressBarMain.setVisibility(View.INVISIBLE);



    }

    @Override
    public void BarcodeDataReceived(KDCData kdcbarcodedata) {

        Log.i("KDCReader", "KDC evt Activity BarCodeReceived Block");
        System.out.print("KDCReader evt Activity  BarCodeReceived Block");

        if (Validations() == false) return;
        if (kdcbarcodedata != null) {

            ScannerData = kdcbarcodedata;
            waybill = ScannerData.GetData();


            if (Utils.checkValidWaybill(kdcbarcodedata.GetData()) == true) {

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

                                new checkEventwaybill(connote).execute();
                            }
                       /*     set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, connote, evntIde, noteet.getText().toString(), racket.getText().toString());
                            System.out.println("setconnResp:"+set_connoteevents);

                            Toast toast = Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
*/
                            //Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG).show();
                        }
                        // wbilldata1=contents;

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

    @Override
    public void ConnectionChanged(BluetoothDevice device, int state) {
        //ToDo Auto-generated method stub

        Log.i("KDCReader", "KDC EVEMNT Activity connection changed block");
        System.out.print("KDCReader EVEMNT Activity connection changed block");
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Validations() == false) return;
                if (honeywellevent != null) {

                    //ScannerData = honeywellevent;
                    waybill = honeywellevent.getBarcodeData();


                    if (Utils.checkValidWaybill(honeywellevent.getBarcodeData()) == true) {

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

                                        new checkEventwaybill(connote).execute();

                                        //Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG).show();
                                    }
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(barcodeReader!=null){
                barcodeReader.release();
                barcodeReader.removeBarcodeListener(_activity);
                Intent int1 = new Intent(this, MainActivity.class);
                int1.putExtra("usrnamee", usrnamee);
                int1.putExtra("passworde", passworde);
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
                EventActivity.this.finish();
                Intent int1 = new Intent(this, MainActivity.class);
                int1.putExtra("usrnamee", usrnamee);
                int1.putExtra("passworde", passworde);
                startActivity(new Intent(int1));
                return true;
            }

        }
        return false;
    }

    public class checkEventwaybill extends AsyncTask<Void, Void, String> {
        //String response = "";
        String Taskwabill = "";

        public checkEventwaybill(String TakWaybill) {
            super();
            Taskwabill = TakWaybill;

            //masterawbtxt.setText(TaskMasterwabill);

            System.out.println("taskwaybill pre:" + Taskwabill);
        }

        public void onPreExecute() {
            Pb.setVisibility(View.VISIBLE);
            // super.onPreExecute();

        /*    tr = new TableRow(EventActivity.this);

            if(Build.MODEL.contains("SM-N"))
            {
					
				*//*	lp = new LayoutParams(420,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
					tr.setLayoutParams(lp);
					lp.setMargins(0, 10, 40, 0);*//*
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
           //   lp.setMargins(18, 2, 95, 2);
                 lp.setMargins(20,5,150,0);

                tr.setLayoutParams(lp);
                System.out.println("tr id:"+tr.getId());

            }
            else
            {
                lp = new TableRow.LayoutParams(150, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tr.setId(resulttab.getChildCount());
                tr.setLayoutParams(lp);
                lp.setMargins(0, 20, 10, 0);
            }*/
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
                // checkconnote_status = WebService.CheckConnoteEvent(passworde, usrnamee, connote, spinevtcode);
                //checkconnote_status = WebService.CheckConnoteEvent(passworde, usrnamee, wabil, spinevtcode);
                checkconnote_status = WebService.CheckConnoteEvent(logintoken, wabil, spinevtcode);
                System.out.println("checkconnote_status is:" + checkconnote_status);

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

            tr = new TableRow(EventActivity.this);

            if (Build.MODEL.contains("SM-N")) {

				/*	lp = new LayoutParams(420,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    tr.setLayoutParams(lp);
					lp.setMargins(0, 10, 40, 0);*/
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
                //   lp.setMargins(18, 2, 95, 2);
                lp.setMargins(20, 5, 200, 0);

                tr.setLayoutParams(lp);
                System.out.println("tr id:" + tr.getId());

            } else {
                lp = new TableRow.LayoutParams(200, TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                tr.setId((resulttab.getChildCount()));
                //lp.setMargins(0, 20, 5, 0);
                //lp.setMargins(0,5,70,0);
                lp.setMargins(0,5,70,0);
                tr.setLayoutParams(lp);

            }

            //	System.out.println("response : " + response.toString());


            deltxt = new TextView(EventActivity.this);
            deltxt.setLayoutParams(lp);
            deltxt.setText("Delete");

            waybilltxt = new TextView(EventActivity.this);
            waybilltxt.setLayoutParams(lp);
            waybilltxt.setText(Taskwabill);
            System.out.println("connote text is" + connote + "Taskwabill" + Taskwabill);


            wghtxt = new TextView(EventActivity.this);
            // wghtxt.setLayoutParams(lp);
            wghtxt.setWidth(150);
            wghtxt.setText(weightet.getText().toString());


            chckremaktxt = new TextView(EventActivity.this);
            // chckremaktxt.setLayoutParams(lp);
            chckremaktxt.setWidth(350);
            chckremaktxt.setText(checkconnote_status);

            evnttxt = new TextView(EventActivity.this);
            //evnttxt.setLayoutParams(lp);
            evnttxt.setWidth(350);
            // evnttxt.setText(evtname);
            evnttxt.setText(spinevtname);
            // System.out.println("rnametxt  is"+rname1);


            evntcodetxt = new TextView(EventActivity.this);
            // evntcodetxt.setLayoutParams(lp);
            evntcodetxt.setWidth(250);
            //  evntcodetxt.setText(evntIde);
            evntcodetxt.setText(spinevtcode);
            //  System.out.println("cnametxt  is"+cname1);

            notetext = new TextView(EventActivity.this);
            //notetext.setLayoutParams(lp);
            notetext.setWidth(350);
            notetext.setText(noteet.getText().toString());
            //  System.out.println("amounttxt  is"+amount1);

            racktext = new TextView(EventActivity.this);
            //racktext.setLayoutParams(lp);
            // racktext.setWidth(350);
            racktext.setText(racket.getText().toString());
            //  System.out.println("phone  is"+phone);







         /*   if(checkconnote_status.matches("Invalid AirwayBill")){
                invaldcnt++;
                textcount3.setText(String.valueOf(invaldcnt));
            }else if(checkconnote_status.matches("Already In AS")){
                setBackgroundColor(Color.parseColor("#d73232"));
                invaldcnt++;
                textcount3.setText(String.valueOf(invaldcnt));
            }else if(checkconnote_status.matches("VALID")){

                valdcnt++;

                textcount2.setText(String.valueOf(valdcnt));
            }else if(checkconnote_status.matches("No Arrival Scan")){
                Toast.makeText(getApplicationContext(), checkconnote_status, Toast.LENGTH_LONG).show();
                return;
            }*/
            if (!checkconnote_status.matches("VALID")) {
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
            tr.addView(wghtxt);
            tr.addView(chckremaktxt);
            tr.addView(evnttxt);
            tr.addView(evntcodetxt);
            tr.addView(notetext);
            tr.addView(racktext);

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
                    resulttab.addView(tr,0, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    System.out.println("tr  on add id:" + tr.getId());
                } else {
                    System.out.println("value of counttxt in else" + countN);
                    System.out.println("value of INVALID in else" + String.valueOf(invaldcnt - 1));
                    countN = countN - 1;
                    textcount1.setText(String.valueOf(resulttab.getChildCount()));

                    if (!checkconnote_status.matches("VALID")) {
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
                        System.out.println("value of getId is:" + v.getId()+"rowid "+rowid+"new child:"+resulttab.getChildCount());
                        if ( resulttab.getChildCount() != 0) {

                            checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(v.getId())).getChildAt(3);
                            System.out.println("checkremartextdel are:" + checkremartextdel.getText().toString());

                            //   checkremartextdel = (TextView) ((TableRow) resulttab.getChildAt(rowid)).getChildAt(3);
                            //   System.out.println("checkremartextdel are:" + checkremartextdel.getText().toString());
                            if (!checkremartextdel.getText().toString().matches("VALID")) {
                                invaldcnt = invaldcnt - 1;
                                textcount3.setText(String.valueOf(invaldcnt));
                            } else {
                                valdcnt = valdcnt - 1;
                                textcount2.setText(String.valueOf(valdcnt));
                            }


                            resulttab.removeViewAt(v.getId());
                            // resulttab.removeView(resulttab.getChildAt(v.getId()));

                            System.out.println("value of textcount1 update delete" + textcount1.getText().toString());

                            Toast.makeText(getApplicationContext(), "Row deleted successfully", Toast.LENGTH_LONG).show();
                            rowid = -1;
                            int tabcount = (resulttab.getChildCount());
                            for (int i = 0; i < tabcount; i++) {
                                tr = (TableRow) resulttab.getChildAt(i);
                                tr.setId(i);
                                System.out.println("tr aftr  awb del:" + tr.getId());
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
                System.out.println("tr aftr add:" + tr.getId());
            }

        }

    }

    public boolean Validations() {
        if (eventlist.getText().toString().equals("")) {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            });
            return false;
        }/*else if (noteet.getText().toString().equals("")||noteet.getText().toString().equals(null)){
            _activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
        Toast toast = Toast.makeText(getApplicationContext(),"Please enter note", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
                }});
        return false;
    } else if (racket.getText().toString().equals("")||racket.getText().toString().equals(null)){
            _activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
        Toast toast = Toast.makeText(getApplicationContext(),"Please enter rackno", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        }});
        return false;
        }else if (weightet.getText().toString().equals("")||weightet.getText().toString().equals(null)){
            _activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
        Toast toast = Toast.makeText(getApplicationContext(),"Please enter weight", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        }});
        return false;
        }*/
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!eventlist.getText().toString().equals("")) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                } else if (eventlist.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!eventlist.getText().toString().equals("")) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        DonotInterruptKDCScan = true;
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "SCAN_MODE");
                        startActivityForResult(intent, SCANNER_REQUEST_CODE);
                    }
                } else if (eventlist.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields To Proceed!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }

    }

    //Return scan result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent

            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");

                camerabill = contents;
                if (Utils.checkValidWaybill(camerabill) == false) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid Waybill", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                connote = camerabill;
                //set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, connote, evntIde, noteet.getText().toString(), racket.getText().toString());
                /*System.out.println("setconnResp:"+set_connoteevents);

                Toast toast = Toast.makeText(getApplicationContext(),set_connoteevents, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
*/
              /*  if(Validations()==true){
                    new checkEventwaybill().execute();
                }*/

                new checkEventwaybill(camerabill).execute();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        } else {
            // Handle other intents
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

            DonotInterruptKDCScan = false;
            if(barcodeReader!= null){
            barcodeReader.removeBarcodeListener(_activity);
            barcodeReader.release();
            }
            Intent int1 = new Intent(this, MainActivity.class);
            int1.putExtra("usrnamee", usrnamee);
            int1.putExtra("passworde", passworde);
            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
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

            if (barcodeReader != null) {
                try {

                    barcodeReader.claim();
                    System.out.println("barcode evt onres:" + barcodeReader);
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        }

        System.out.println("Resume activate in rst");
    }

    public void getevents() {
        try {

            listevt = new ArrayList<String>();

            System.out.println("passworde is:" + passworde + "usrnamee:" + usrnamee);
            evtresponse = WebService.GetEventOperations(logintoken);
            System.out.println("evtresponse is:" + evtresponse.length);
            if (evtresponse != null) {
                System.out.println("evtresponse val  is:" + String.valueOf(evtresponse[i].EventName));
                // int postn=0;
                for (int i = 0; i < evtresponse.length; i++) {

                    eventslist = new Events();
                    eventslist.EventCode = evtresponse[i].EventCode;
                    eventslist.EventName = evtresponse[i].EventName;
                    eventslist.ErrMessage=evtresponse[i].ErrMessage;
                    System.out.println("eventslist in for looppp is" + evtresponse[i].EventName);

                    Evntarrlist.add(eventslist);


                }

                System.out.println("Evntarrlist out for looppp is" + Evntarrlist);

                int arraysize = Evntarrlist.size();
                System.out.println("Evntarrlist array size outside spinselect is" + arraysize);

                eventcode = new String[arraysize];
                eventname = new String[arraysize];
                eventerr = new String[arraysize];

                System.out.println("eventcode are" + eventcode);


                for (int i = 0; i < arraysize; i++) {


                    eventcode[i] = (Evntarrlist.get(i).EventCode);
                    eventname[i] = (Evntarrlist.get(i).EventCode);
                    eventerr[i] = (Evntarrlist.get(i).ErrMessage);
                    // System.out.println("i="+i+eventname[i]);

                }

                System.out.println("eventname are" + eventname[i]+"eventerr ae"+eventerr);

                for (int i = 0; i < arraysize; i++) {

                    listevt.add(i, String.valueOf(Evntarrlist.get(i).EventName));
                    System.out.println("EventName in et is" + String.valueOf(Evntarrlist.get(i).EventName));
                    System.out.println("EventCode in et is" + String.valueOf(Evntarrlist.get(i).EventCode));


                }

                if(Evntarrlist.get(i).ErrMessage.equals("Invalid Session")){
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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
                                    Log.e("loginval//",loginval);
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    // Add new Flag to start new Activity
                                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Log.e("hee","hu");
                                    getBaseContext().startActivity(intent);
                                    EventActivity.this.finish();
                                    Log.e("here","hu");
                                    loginflag= false;
                                }
                            });
                    alertDialog.show();


                }

                System.out.println("eventlistis" + String.valueOf(eventlist.getTag()));

            }


          /*  ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_dropdown_item,listevt);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventlist.setAdapter(adapter);*/

 /*      final ArrayAdapter<String> evntspinner = new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_dropdown_item, listevt);

            eventlist.setGravity(Gravity.CENTER);

            eventlist.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {



                new AlertDialog.Builder(EventActivity.this)

                            .setTitle("Select Event")

                            .setAdapter(evntspinner, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int i) {

                                    spinevtname = String.valueOf(Evntarrlist.get(i).EventName);
                                    System.out.println("spinevtname is" + spinevtname);
                                    spinevtcode = String.valueOf(Evntarrlist.get(i).EventCode);
                                    eventlist.setText(spinevtname);
                                    dialog.dismiss();
                                }
                            }).create().show();


                }
            });
*/


            // eventlist.setThreshold(0);


        } catch (Exception e) {

        }

    }


    public void onPostExecute() {

        progressBarMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                progressBarMain.setVisibility(View.INVISIBLE);
                progressBarMain.postDelayed(this, 5000);
            }
        }, 5000);

    }


    /* public class SaveTask extends AsyncTask<String, String, String> {

         String SaveTaskwbl="",SaveTaskevtcode="",SaveTasknote="",SaveTaskrack="",SaveTaskcheck="";

        public SaveTask(String WAB,String evtcodes,String notes,String racknos,String chckremarks){
             SaveTaskwbl=WAB;
            SaveTaskevtcode=evtcodes;
            SaveTasknote=notes;
            SaveTaskrack=racknos;
            SaveTaskcheck=chckremarks;
            System.out.println("SaveTaskcheck are:"+SaveTaskcheck);
            if (!SaveTaskcheck.matches("VALID")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please delete the rows in red", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                progressBarMain.setVisibility(View.INVISIBLE);
                new SaveTask(tablwaybill,evtcode,note,rackno,chectext).cancel(true);
                return;
            }

         }


         @Override
         protected void onPreExecute() {
             progressBarMain.setVisibility(View.VISIBLE);


         }

         @Override
         protected String doInBackground(String... params) {

             set_connoteevents = WebService.SetConnoteEvents(passworde, usrnamee, SaveTaskwbl, SaveTaskevtcode,SaveTasknote, SaveTaskrack);
             System.out.println("set_connoteevents on back:"+set_connoteevents);
             return set_connoteevents;
         }

         @Override
         protected void onPostExecute(String result) {
             super.onPostExecute(result);

             result=set_connoteevents;
             System.out.println("result ae"+result+"set_connoteevents  pos:"+set_connoteevents);
             setcnnteResp=set_connoteevents;
             progressBarMain.setVisibility(View.INVISIBLE);
             System.out.println("setcnnteResp ae"+setcnnteResp);
             textcount1.setText(String.valueOf(resulttab.getChildCount()));
             textcount2.setText(String.valueOf(resulttab.getChildCount()));
            *//* int tabcount = resulttab.getChildCount();
            for (int k = 0; k < tabcount; k++) {


                if (set_connoteevents != null) {

                    if (set_connoteevents.matches("TRUE")) {
                        resulttab.removeViewAt(k);
                        k = k - 1;
                        tabcount = tabcount - 1;
                        System.out.println("new result tab count:" + tabcount);

                    } else {
                        if (set_connoteevents.matches("FALSE")) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please Check again", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            progressBarMain.setVisibility(View.INVISIBLE);
                            return;
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), set_connoteevents, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            progressBarMain.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }


                }

            }

            progressBarMain.setVisibility(View.INVISIBLE);*//*
        }
}*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    /*   InputMethodManager imm = (InputMethodManager)getSystemService(Context.
               INPUT_METHOD_SERVICE);
       System.out.println("imm are:"+imm);
       if(imm != null ){
       imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
       }
       if (spinevtcode.matches("AS")) {
           weightet.setEnabled(true);
           weightet.setClickable(true);
       }else{
           weightet.setEnabled(false);
           weightet.setClickable(false);
       }
       return true;
   }
   */
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Log.e("TouchTest", "Touch down");
        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            Log.e("TouchTest", "Touch up");
        }
        if(spinevtcode != null){
            if (spinevtcode.matches("AS")) {
                weightet.setEnabled(true);
                weightet.setClickable(true);
            }else{
                weightet.setEnabled(false);
                weightet.setClickable(false);
                weightet.setText("0.000");
            }
        }
        return true;
    }

}




