package com.postaplus.etrack;


import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import koamtac.kdc.sdk.KDCBarcodeDataReceivedListener;
import koamtac.kdc.sdk.KDCConnectionListener;
import koamtac.kdc.sdk.KDCConstants;
import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCDataReceivedListener;
import koamtac.kdc.sdk.KDCReader;
import util.ActivityNotification;
import webservice.FuncClasses.Couriers;
import webservice.FuncClasses.GetRunsheetVerifyDet;
import webservice.JsonFuncClasses.JsonFuncClasses.D;
//import webservice.JsonFuncClasses.JsonFuncClasses.GetRstVerify;
//import webservice.JsonFuncClasses.JsonFuncClasses.JsonResponses.GetRunsheetVerifyResponse;


public class RstVerificationActivity extends AppCompatActivity implements KDCConnectionListener,KDCDataReceivedListener,KDCBarcodeDataReceivedListener{
    private TextView eventdate;
    private String[] sheetnoarr=null;
    private String[] arraySpinner1;
    Button clear, update;
    EditText editOdo;
    String usn1,uscode1,err1;
    String usrnamee,passworde;
    KDCData ScannerData;
    KDCReader _kdcReader;
    public RstVerificationActivity MYActivity;
    KDCTask KDCTaskExecutable = new KDCTask();
    public boolean isActivityActiveFlag=false;
    Thread ThrKdc;
    RstVerificationActivity _activity;
    String waybill,wbill;
    String curname,curcode;

    String[] COURARRAY=null;
    Couriers[] couresponse;
    ArrayList<Couriers> Courierarrlist = new ArrayList<Couriers>();
    Couriers courierlist;
    int i=0;
    ActivityNotification actNoty = new ActivityNotification();
    String usernam,usercde;
    String[] Courname;
    TextView eventcourier,eventrst;

    List<String> listcurcode = null;
    //String GetrstverifyResp;
    String dateevnt;
    ArrayList<String> listcur;
    List<D> GetrstverifyResp;
  //  ArrayList<GetRstVerify> RSTARRAYLST = new ArrayList<GetRstVerify>();
    String errmsg ,Identifier,SheetNo;
    ArrayList<String> SheetArr;
    String sheetno;
    Calendar myCalendar = Calendar.getInstance();
    Integer caldate,calmonth,calyear;
    GetRunsheetVerifyDet[] RstverfydetResponse;
    String todaydate;
    String UpdtRstVerfyResp;
   // List<GetRunsheetVerifyResponse> GetrstList = null;
  CheckBox checkBoxverify;
    String OdometerVal,Verifysts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rst_verification);


     /*   android.support.v7.app.ActionBar localActionBar = getSupportActionBar();
        localActionBar.setCustomView(R.layout.cust_actionbar);

        localActionBar.setDisplayShowCustomEnabled(false);
        localActionBar.setDisplayUseLogoEnabled(false);
        localActionBar.setDisplayShowHomeEnabled(true);
        localActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eb7a02")));
        localActionBar.setIcon(R.drawable.ic_arrow_back_black_24dp);
        localActionBar.setDisplayShowTitleEnabled(true);*/

        eventdate = (TextView) findViewById(R.id.datespinner);
        eventcourier = (TextView) findViewById(R.id.courierspinner);
        eventrst = (TextView) findViewById(R.id.rstspinner);
        clear = (Button) findViewById(R.id.btnclear);
        update = (Button) findViewById(R.id.btnupdate);
        editOdo=(EditText)findViewById(R.id.editOdo);
        checkBoxverify= (CheckBox)findViewById(R.id.checkBoxverify);
        // usrnamee = getIntent().getExtras().getString("username");
        //   passworde = getIntent().getExtras().getString("password");
        usrnamee=actNoty.getUserName(RstVerificationActivity.this);
        passworde=actNoty.getPassword(RstVerificationActivity.this);

        getcouriers();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //  getLayoutInflater().inflate(R.layout.activity_main, drawer);
        System.out.println("dawer is:"+drawer);
        System.out.println("usrnamee rst is:"+usrnamee+"passworde rst:"+passworde);
        final ArrayAdapter adapter = new
                ArrayAdapter(RstVerificationActivity.this, android.R.layout.simple_list_item_1, listcur);
        // eventcourier.se(adapter);

        System.out.println("myCalendar is:"+myCalendar.getTime());



        final Calendar myCalendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
        todaydate = format.format(myCalendar.getTime());
        dateevnt=todaydate;
        eventdate.setText(todaydate);
        eventdate.setEnabled(false);





        // code for date spinner

        eventdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));

                //eventlist.clearListSelection();
              // RSTARRAYLST= new ArrayList<GetRstVerify>();
                DatePickerDialog.OnDateSetListener datePickerListener
                        = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int selectedMonth, int selectedDay) {
                        calyear = selectedYear;
                        calmonth = selectedMonth;
                        caldate = selectedDay;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(calyear, calmonth, caldate);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
                        String strselctDate = format.format(calendar.getTime());
                        dateevnt=strselctDate;
                        // set selected date into textview
                        eventdate.setText(strselctDate);
                        System.out.println("strselctDate are"+strselctDate+"curcode"+curcode);

                        if(curcode!=null) {

                        //    GetrstverifyResp = WebService.GetRunsheetVerify(passworde, usrnamee, dateevnt, curcode);
                            System.out.println("GetrstverifyResp is:"+GetrstverifyResp.get(i).getErrMessage()+"Getrstveify List size:"+GetrstverifyResp.size());
                            if(GetrstverifyResp != null){
                                GETRSRVERFYRESP();
                            }


                        }
                    }



                };

                new DatePickerDialog(RstVerificationActivity.this, datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();



            }

        });


        checkBoxverify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    checkBoxverify.setChecked(true);

                } else {


                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }

        });
     /*   eventdate.setAdapter(new CalendarSpinnerAdapter(getBaseContext(),90));


        eventdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Calendar cal = Calendar.getInstance();
               // cal.add(Calendar.DAY_OF_MONTH,1);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                Calendar selectedDate = (Calendar) eventdate.getSelectedItem();
               // return new SimpleDateFormat("d MMM yyyy").format(selectedDate.getTimeInMillis());
              //  System.out.println("item rst is"+cal.get(position));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
                String formattedDate = df.format(selectedDate.getTime());
                ArrayList<String> temp = null;
                dateevnt=formattedDate;
                if(curcode!=null) {
                    GetrstverifyResp = WebService.GetRunsheetVerify(passworde, usrnamee, dateevnt, curcode);
                    System.out.println("GetrstverifyResp is"+GetrstverifyResp);
                    GETRSRVERFYRESP();

                }
                System.out.println("GetrstverifyResp frm date are:"+GetrstverifyResp);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

      /*  ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RstVerificationActivity.this, android.R.layout.simple_spinner_item, SheetArr);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventrst.setAdapter(adapter1);*/
        // code for clear button
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();
                checkBoxverify.setChecked(false);
                editOdo.setText("");
                eventdate.setText(todaydate);
                eventdate.setEnabled(false);
                eventcourier.setText("");
                eventrst.setText("");
             //   RSTARRAYLST= new ArrayList<GetRstVerify>();

            }

        });

        // code for Update button
        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                OdometerVal=editOdo.getText().toString();
                System.out.println("OdometerVal are:"+OdometerVal);
                if(checkBoxverify.isChecked()){
                    Verifysts="Y";
                }else{
                    Verifysts="N";
                }
                if(Validate()==true){
                  //  UpdtRstVerfyResp = WebService.UpdateRunsheetVerify(passworde,usrnamee,curcode,Identifier="KWI:RST13275",OdometerVal,Verifysts);
                    System.out.println("UpdtRstVerfyResp"+UpdtRstVerfyResp);
                    if(UpdtRstVerfyResp.contains("InternalError")){
                        Toast.makeText(getApplicationContext(), "Please Try again later.", Toast.LENGTH_LONG).show();
                        return;
                    }
                   /* if(UpdtRstVerfyResp != null){
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Please Try again later.", Toast.LENGTH_LONG).show();
                        return;
                    }*/


                }
                System.out.println("UpdtRstVerfyResp are"+UpdtRstVerfyResp);

            }

        });


/*        eventrst.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                eventrst.setText("");
                //eventlist.clearListSelection();
                new AlertDialog.Builder(RstVerificationActivity.this)

                        .setTitle("Select RST")

                        .setAdapter(adapter1, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int k) {
                                System.out.println("k frm click" +k);
                                sheetno = String.valueOf(SheetArr.get(k));

                                dialog.dismiss();
                                eventrst.setText(sheetno);

                            }
                        }).create().show();

            }

        });*/


        // EventCourier Spinner for item selection
        eventcourier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                eventcourier.setText("");
                curname=null;
                curcode=null;
               // RSTARRAYLST= new ArrayList<GetRstVerify>();
                //eventlist.clearListSelection();
                new AlertDialog.Builder(RstVerificationActivity.this)

                        .setTitle("Select Courier")

                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int k) {
                                System.out.println("k frm click" +k);
                                curname = String.valueOf(Courierarrlist.get(k).UserName);
                                curcode = String.valueOf(Courierarrlist.get(k).UserCode);
                                //
                                dialog.dismiss();
                                eventcourier.setText(curname);
                              //  GetrstverifyResp=WebService.GetRunsheetVerify(passworde,usrnamee,dateevnt,curcode);
                                System.out.println("curname is" + curname +"curcide:"+curcode+"GetrstverifyResp"+GetrstverifyResp);
                                System.out.println("curname frm click" + curname + "curcode frm click" + curcode+"GetrstverifyResp"+GetrstverifyResp);
                                eventdate.setEnabled(true);
                                System.out.println("GetrstverifyResp adap"+GetrstverifyResp);
                                if(GetrstverifyResp != null){
                                    GETRSRVERFYRESP();
                                }


                            }

                        }).create().show();

            }

        });

     /*   eventcourier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int k, long l) {


                curname = String.valueOf(Courierarrlist.get(k).UserName);
                curcode = String.valueOf(Courierarrlist.get(k).UserCode);

                GetrstverifyResp=WebService.GetRunsheetVerify(passworde,usrnamee,dateevnt,curcode);
                System.out.println("curname is" + curname +"curcide:"+curcode+"GetrstverifyResp"+GetrstverifyResp);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/


    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("KDCReader on hold actvity While Pause : " + _kdcReader);
        if(!isActivityActiveFlag) isActivityActiveFlag=false;

        if(ThrKdc!=null) {
            if(_kdcReader!=null)_kdcReader.Disconnect();
            if(ThrKdc!=null)ThrKdc.interrupt();
            KDCTaskExecutable.cancel(true);
            System.out.println("THRKDC in pause activated on"+ThrKdc);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(!isActivityActiveFlag) isActivityActiveFlag=false;
        _activity = this;
        //   getcouriers();

        if(!KDCTaskExecutable.getStatus().equals(AsyncTask.Status.RUNNING) && !KDCTaskExecutable.getStatus().equals(AsyncTask.Status.FINISHED)){
            //KDCTaskExecutable.cancel(true);
            KDCTaskExecutable.execute();
            System.out.println(" KDCTask Executed");
        }

        System.out.println("Resume activate in rst");
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
            Intent int1 = new Intent(this,MainActivity.class);
            int1.putExtra("usrnamee",usrnamee);
            int1.putExtra("passworde",passworde);
            startActivity(new Intent(int1));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void BarcodeDataReceived(KDCData barcodedata) {
        if (barcodedata != null) {

            ScannerData = barcodedata;
            waybill = ScannerData.GetData();


            if (Check_ValidWaybill(barcodedata.GetData()) == true) {


                System.out.println(" value for barcodedata is : ");
                System.out.println(barcodedata);

                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (waybill != null) {
                            //String contents = wbill;


                            wbill = waybill;
                            //transcourier = String.valueOf(drivcode[courierlist.getSelectedItemPosition()]);
                            // _activity.ScannerExecutions();
                            System.out.println("Value wbill in hold" + wbill);
                          /*  if (transcourier.isEmpty()) {
                                Toast.makeText(_activity, "Please select any courier!", Toast.LENGTH_SHORT).show();

                                return;
                            }*/
                            // new checkholdwaybill().execute();
                        }


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
    public void ConnectionChanged(BluetoothDevice device,int state){
        //ToDo Auto-generated method stub

        Log.i("KDCReader", "KDC EVEMNT Activity connection changed block");
        System.out.print("KDCReader EVEMNT Activity connection changed block");
        System.out.print("State is "+state);
        switch(state){

            case KDCConstants.CONNECTION_STATE_CONNECTED:
                _activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){

                        Toast.makeText(_activity, "Scanner Connected", Toast.LENGTH_LONG).show();
                        isActivityActiveFlag=true;
                    }
                });
                break;

            case KDCConstants.CONNECTION_STATE_LOST:
                _activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){

                        Toast.makeText(_activity, "Scanner Connection Lost", Toast.LENGTH_LONG).show();
                        isActivityActiveFlag=true;
                    }
                });
                break;
        }
    }

    @Override
    public void DataReceived(KDCData kdcData) {

    }

    public class KDCTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...arg0){

            if(ThrKdc!=null) {
                ThrKdc.run();
            }
            else {

                ThrKdc = new Thread() {
                    @Override
                    public void run() {
                        _kdcReader = new KDCReader(null, _activity, _activity, null, null, null, _activity, false);
                        MainActivity.ScannerDevice = _kdcReader.GetBluetoothDevice();
                        //_kdcReader.EnableBluetoothAutoPowerOn(false);
                        //_kdcReader.EnableAutoReconnect(false);
                        //_kdcReader.EnableBluetoothWakeupNull(false);
                        _kdcReader.EnableBluetoothWakeupNull(true);
                        if(isCancelled()) ThrKdc.interrupt();
                    }
                };
                ThrKdc.start();
                if(isCancelled()) ThrKdc.interrupt();
            }
            return "";
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!isActivityActiveFlag)
            {
                Toast.makeText(getApplicationContext(), "Please wait for scanner to connect",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            else{

                if(_kdcReader!=null) _kdcReader.Disconnect();
                if(ThrKdc!=null)ThrKdc.interrupt();
                KDCTaskExecutable.cancel(true);
                RstVerificationActivity.this.finish();
                Intent int1 = new Intent(this,MainActivity.class);
                int1.putExtra("usrnamee",usrnamee);
                int1.putExtra("passworde",passworde);
                startActivity(new Intent(int1));
                return true;
            }

        }
        return false;
    }

    public static boolean Check_ValidWaybill (String s){

        if (s.length() == 10 || s.length() == 12)
        {
            return StringUtils.isNumeric(s) == true;
        }
        else if (s.length() == 18)
        {
            return StringUtils.isAlphanumeric(s) == true;
        }
        return false;
    }

    public void getcouriers() {
        try {

            listcur = new ArrayList<String>();

            //couresponse = WebService.GetCouriers(passworde,usrnamee);
            System.out.println("couresponse is:"+couresponse.length);
            if (couresponse != null) {
                System.out.println("couresponse val  is:"+String.valueOf(couresponse[i].UserName));

                for (int i = 0; i < couresponse.length; i++) {

                    courierlist = new Couriers();
                    courierlist.UserCode = couresponse[i].UserCode;
                    courierlist.UserName = couresponse[i].UserName;
                    System.out.println("couresponse in for looppp is" + couresponse[i].UserName);

                    Courierarrlist.add(courierlist);

                }

                System.out.println("Courierarrlist out for looppp is" + Courierarrlist);




                int arraysize = Courierarrlist.size();
                System.out.println("Courierarrlist array size outside spinselect is" + arraysize);


                for (int i = 0; i < arraysize; i++) {


                    listcur.add(String.valueOf(Courierarrlist.get(i).UserName));
                    System.out.println("UserName in et is" + String.valueOf(Courierarrlist.get(i).UserName));
                    System.out.println("UserCode in et is" + String.valueOf(Courierarrlist.get(i).UserCode));
                }


            }




        }catch (Exception e) {

        }

    }

    public  void GETRSRVERFYRESP(){

        System.out.println("GetrstverifyResp are"+GetrstverifyResp.size());

        for (int i=0;i<GetrstverifyResp.size();i++){
            //GetRstVerify GetRstVerifyObj = new GetRstVerify();
            SheetArr = new ArrayList<String>();
           // GetRstVerifyObj.ErrMessage=errmsg=GetrstverifyResp.get(i).getErrMessage();
          //  GetRstVerifyObj.Identifier=Identifier=GetrstverifyResp.get(i).getIdentifier();
          //  GetRstVerifyObj.SheetNo=SheetNo=GetrstverifyResp.get(i).getSheetNo();
           // System.out.println("GetRstVerifyObjErrMessage are"+GetRstVerifyObj.ErrMessage);
            //System.out.println("GetRstVerifyObjIdentifier are"+GetRstVerifyObj.Identifier);
            //System.out.println("GetRstVerifyObjSheetNo are"+GetRstVerifyObj.SheetNo);
          //  RSTARRAYLST.add(GetRstVerifyObj);
          // SheetArr.add(GetRstVerifyObj.SheetNo);
            //System.out.println("GetRstVerifyObj err are"+GetRstVerifyObj.ErrMessage);


        }
        System.out.println("SheetArr err are"+SheetArr+"Identifier"+Identifier+"SheetNo-"+SheetNo);
        Log.e("errmsg",errmsg);
        Log.e("Identifier",String.valueOf(Identifier));
        Log.e("SheetNo",String.valueOf(SheetNo));
       /* if(errmsg.matches("No Runsheets Found")){
            Toast toast = Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }*/
        System.out.println("SheetArr are:"+SheetArr);
        if(SheetArr.contains(null)){
            Toast toast = Toast.makeText(getApplicationContext(), errmsg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            eventrst.setEnabled(false);
            return;
        }else {

            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RstVerificationActivity.this, android.R.layout.simple_spinner_item, SheetArr);
          adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            System.out.println("adapter1 are:"+adapter1);
            eventrst.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                    eventrst.setText("");
                    //eventlist.clearListSelection();
                    new AlertDialog.Builder(RstVerificationActivity.this)

                            .setTitle("Select RST")

                            .setAdapter(adapter1, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int k) {
                                    System.out.println("k frm click" + k);
                                    sheetno = String.valueOf(SheetArr.get(k));

                                    dialog.dismiss();
                                    eventrst.setText(sheetno);
                                  //  RstverfydetResponse = WebService.GetRunsheetVerifyDet(passworde, usrnamee, Identifier = "ACK:ACKKWI306");

                                    System.out.println("RSTVerfy dt:" + RstverfydetResponse);

                                }
                            }).create().show();

                }

            });

       /* try {
            JSONObject Jasonobject = new JSONObject(GetrstverifyResp);
          //  ArrayList<GetRstVerify>  rstlist= new ArrayList<>();;
          JSONArray Jarray = Jasonobject.getJSONArray("d"); // replace response with your response string
            System.out.println("jsonArray are:"+Jarray.length()+"jArrt"+Jarray);

            for (int i = 0; i < Jarray.length(); i++) {

                JSONObject jsonObject = Jarray.getJSONObject(i);
                GetRstVerify  getrstverfy= new GetRstVerify();
                getrstverfy.ErrMessage=jsonObject.getString("ErrMessage");
               errmsg =jsonObject.getString("ErrMessage");
                Identifier = jsonObject.getString("Identifier");
               SheetNo = jsonObject.getString("SheetNo");
                Log.e("ErrMessage", jsonObject.getString("ErrMessage") + "");
                Log.e("Identifier", jsonObject.getString("Identifier"));
                Log.e("SheetNo", jsonObject.getString("SheetNo"));
                SheetArr = new ArrayList<String>();
              //  rstlist.add(getrstObj);
                System.out.println("SheetNo is"+SheetNo);


                if(SheetNo==(null)){
                    eventrst.setClickable(false);
                    eventrst.setEnabled(false);
                    System.out.println("Sheet block is" + SheetNo);
                    return;

                }

                SheetArr.add(SheetNo);


                System.out.println("SheetArr ae" + SheetArr);

                final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RstVerificationActivity.this, android.R.layout.simple_spinner_item, SheetArr);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                eventrst.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                        eventrst.setText("");
                        //eventlist.clearListSelection();
                        new AlertDialog.Builder(RstVerificationActivity.this)

                                .setTitle("Select RST")

                                .setAdapter(adapter1, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int k) {
                                        System.out.println("k frm click" + k);
                                        sheetno = String.valueOf(SheetArr.get(k));

                                        dialog.dismiss();
                                        eventrst.setText(sheetno);
                                        //   RstverfydetResponse = WebService.GetRunsheetVerifyDet(passworde,usrnamee,Identifier);

                                        System.out.println("RSTVErfy dt:" + RstverfydetResponse);

                                    }
                                }).create().show();

                    }

                });





                *//*                if(SheetNo==null)return; else {
                    SheetArr.add(SheetNo);


                    System.out.println("SheetArr ae" + SheetArr);

                    final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RstVerificationActivity.this, android.R.layout.simple_spinner_item, SheetArr);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    eventrst.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                            eventrst.setText("");
                            //eventlist.clearListSelection();
                            new AlertDialog.Builder(RstVerificationActivity.this)

                                    .setTitle("Select RST")

                                    .setAdapter(adapter1, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int k) {
                                            System.out.println("k frm click" + k);
                                            sheetno = String.valueOf(SheetArr.get(k));

                                            dialog.dismiss();
                                            eventrst.setText(sheetno);
                                            //   RstverfydetResponse = WebService.GetRunsheetVerifyDet(passworde,usrnamee,Identifier);

                                            System.out.println("RSTVErfy dt:" + RstverfydetResponse);

                                        }
                                    }).create().show();

                        }

                    });

                }*//*


                }

    }catch (JSONException e) {
            e.printStackTrace();
        }

*/






        }
    }

    public boolean Validate(){
        System.out.println("editOdo.getText()"+editOdo.getText().toString());
        if(curcode==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Please select Courier", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        if(editOdo.getText().toString().matches("")){
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter Odometer Value", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }
}





