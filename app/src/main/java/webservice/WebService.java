package webservice;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import webservice.FuncClasses.Events;
import webservice.JsonFuncClasses.JsonFuncClasses.CheckEvent;
import webservice.JsonFuncClasses.JsonFuncClasses.ConnoteEvents;
import webservice.JsonFuncClasses.JsonFuncClasses.JsonResponses.StringResponse;
import webservice.JsonFuncClasses.JsonFuncClasses.UserInfo;

import static com.postaplus.etrack.ScreenActivity.url;


/**
 * Created by ahlaam.kazi on 10/20/2016.
 */

public class WebService {
    //public static String url ="";

    public static String invokeLoginWS(String password,String rememberme , String username ) {
     /*   Map<String,String> map = new HashMap<String,String>();
        map.put("Password",password);
        map.put("UserName",username);
        System.out.println("map web"+map);
        Map<String, String> headermap = new HashMap<>();
        headermap.put("UserInfo",String.valueOf(map));*/
        UserInfo mInfo=new UserInfo(password ,rememberme,username );
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        String requestuserinfo= new Gson().toJson(mInfo);
        System.out.println("requestuserinfo web"+requestuserinfo);
        //Here the json data is add to a hash map with key data
       /* Map<String,String> params = new HashMap<String, String>();
        params.put(" \"UserInfo\" ", requestuserinfo);*/
        String request = "{\"UserInfo\""+":"+requestuserinfo+"}";
        System.out.println("Password web"+password+"usernme:"+username+"request is"+request);
        JSONObject resp = null;
        String jsonlognresp = null;
        try {
            JSONObject obj = new JSONObject(request);
            resp = JsonService.jsonnewpostreq(url,"UserAuthentication",obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

      // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsonlognresp=json.getString("d");
            System.out.println("jsonlognresp delis:"+jsonlognresp);
        } catch (JSONException e) {
            e.printStackTrace();
        }


     //   StringResponse ActResp = (StringResponse) resp.getd();
     //   Log.e("invokeLoginWS/Response", ActResp.toString());
      //  return ActResp.getd();
//        Log.e("String jsonlognresp", jsonlognresp);
        return  jsonlognresp;

    }


    public static Events[] GetEventOperations(String LoginToken) {
        Events[] Responsevnts= null;
        JSONObject respeventObj = null;
       // UserInfo mInfo=new UserInfo(password,username);

       // GsonBuilder builder = new GsonBuilder();
       // builder.disableHtmlEscaping();
        Gson gson = new Gson();
       // String requestuserinfo= gson.toJson(mInfo);
      //  System.out.println("requestuserinfo web"+requestuserinfo);

      //  String requestgeteve = "{\"UserInfo\""+":"+requestuserinfo+"}";
        String requestgeteve = "{\"vs_token\""+":"+LoginToken+"}";
        System.out.println("requestgeteve eve"+requestgeteve);
        try {
            JSONObject obj = new JSONObject(requestgeteve);
            System.out.println("obj eve"+obj);
            respeventObj = JsonService.jsonnewpostreq(url,"GetEventOperations",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("respeventObj",String.valueOf(respeventObj));
        System.out.println("ResponsePostReqev ev:"+respeventObj.length());
        String EventResponse = null;
        if(respeventObj != null){
            try {

                EventResponse = respeventObj.getString("d");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Responsevnts = gson.fromJson(EventResponse,Events[].class);

             Log.e("Responsevnts/succ",String.valueOf(Responsevnts));
            Log.e("EventsResponse",String.valueOf(Responsevnts));


    }
        return  Responsevnts;
    }

    public static String SetConnoteEvents(String LoginToken, String connote,String eventcode,String eventnote,String rackno) {
        //UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
       // String requestuserinfo= new Gson().toJson(mInfo);
       // System.out.println("requestuserinfo web"+requestuserinfo);

        ConnoteEvents connotevnt = new ConnoteEvents(connote,rackno,eventcode,eventnote);
        String reqconnoteve= new Gson().toJson(connotevnt);
        System.out.println("reqconnoteve:"+reqconnoteve+"eventcode:"+eventcode);
       // String ConnoteEvnt = "{\"UserInfo\""+":"+requestuserinfo+","+"\"ConnoteEvents\""+":"+reqconnoteve+"}";
        String ConnoteEvnt = "{\"vs_token\""+":"+LoginToken+","+"\"ConnoteEvents\""+":"+reqconnoteve+"}";
        System.out.println("SetConnoteEvents"+ConnoteEvnt);
        JSONObject resp = null;
        String jsonconotevntResp = null;
        try {
            JSONObject obj = new JSONObject(ConnoteEvnt);
            System.out.println("obj setcnnt"+obj);
            resp = JsonService.jsonnewpostreq(url,"SetConnoteEvents",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsonconotevntResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String jsoncon", jsonconotevntResp);
        return  jsonconotevntResp;

    }

    public static String CheckConnoteEvent(String LoginToken, String connote, String eventcode ){

       // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
       // String requestuserinfo= new Gson().toJson(mInfo);
       // System.out.println("requestuserinfo web"+requestuserinfo);

        CheckEvent chckevnt = new CheckEvent(connote,eventcode);
        String reqchckeve= new Gson().toJson(chckevnt);
        System.out.println("reqchckeve:"+reqchckeve+"eventcode:"+eventcode);
       // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        String CHECKEVENREQ = "{\"vs_token\""+":"+LoginToken+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        System.out.println("CHECKEVENREQ"+CHECKEVENREQ);
        JSONObject resp = null;
        String jsoncheckcnteResp = null;
        try {
            JSONObject obj = new JSONObject(CHECKEVENREQ);
            System.out.println("obj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"CheckConnoteEvent",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckcnteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String checkcnteResp", jsoncheckcnteResp);
        return  jsoncheckcnteResp;


    }

    public static String AuthSessionLoggOut(String LoginToken) {

        JSONObject LogoutObj = null;

        Gson gson = new Gson();

        String requestlogout = "{\"vs_token\"" + ":" + LoginToken + "}";
        System.out.println("requestlogout eve" + requestlogout);
        try {
            JSONObject obj = new JSONObject(requestlogout);
            System.out.println("obj logout" + obj);
            LogoutObj = JsonService.jsonnewpostreq(url, "AuthSessionLoggOut", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("LogoutObj", String.valueOf(LogoutObj));
        System.out.println("LogoutObj ev:" + LogoutObj);
        String LogoutResponse = null;
        if (LogoutObj != null) {
            try {

                LogoutResponse = LogoutObj.getString("d");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return LogoutResponse;
    }

    public static String CheckConnoteWHStockTrans(String LoginToken,String whtype, String connote,String Transtype, String frmloc,String toloc ){

        JSONObject jsonObj = new JSONObject();


        try {

            jsonObj.put("vs_token",LoginToken);
            jsonObj.put("v_whtype",whtype);
            jsonObj.put("v_connote",connote);
            jsonObj.put("v_transtype",Transtype);
            jsonObj.put("v_fromloc",frmloc);
            jsonObj.put("v_toloc",toloc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resputl= null;
        String jsonutlresp = null;
        System.out.println("jsonobj reqts:"+jsonObj);

        resputl = JsonService.jsonnewpostreq(url,"CheckConnoteWHStockTrans",jsonObj).toString();


        try {
            JSONObject json = new JSONObject(String.valueOf(resputl));
            jsonutlresp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonutlresp are"+jsonutlresp);




        return  jsonutlresp;


    }


    public static String SetConnoteWHStockTrans(String LoginToken,String whtype,String Transtype,JSONArray jarray ){
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject postedJSON = new JSONObject();

        System.out.println("jsonArray setreqts:"+jsonArray);
        try {

            jsonObj.put("vs_token",LoginToken);
            jsonObj.put("v_whtype",whtype);
            jsonObj.put("v_stktranstype",Transtype);
            jsonObj.put("v_whstocktrans",jarray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resputl= null;
        String jsonutlresp = null;
        System.out.println("jsonobj setreqts:"+jsonObj);

        resputl = JsonService.jsonnewpostreq(url,"SetConnoteWHStockTrans",jsonObj).toString();


        try {
            JSONObject json = new JSONObject(String.valueOf(resputl));
            jsonutlresp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonutlresp are"+jsonutlresp);




        return  jsonutlresp;


    }

   /* public static Couriers[] GetCouriers(String password, String username) {

        Couriers[] Responsgetcour= null;
        JSONObject respgetcourObj = null;
        UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        Gson gson = new Gson();
        String requestuserinfo= gson.toJson(mInfo);
        System.out.println("requestuserinfo web"+requestuserinfo);

        String requestgetcour = "{\"UserInfo\""+":"+requestuserinfo+"}";
        System.out.println("requestgetcour eve"+requestgetcour);
        try {
            JSONObject obj = new JSONObject(requestgetcour);
            respgetcourObj = JsonService.jsonnewpostreq(url,"GetCouriers",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("respgetcourObj",String.valueOf(respgetcourObj));
        System.out.println("respgetcourObj ev:"+respgetcourObj.length());
        String CourierResponse = null;
        if(respgetcourObj != null){
            try {

                CourierResponse = respgetcourObj.getString("d");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Responsgetcour = gson.fromJson(CourierResponse,Couriers[].class);

            Log.e("Responsgetcour/succ",String.valueOf(Responsgetcour));
            Log.e("Responsgetcour",String.valueOf(Responsgetcour));


        }
        return  Responsgetcour;

    }

    public static List<D> GetRunsheetVerify(String password, String username, String date, String courcode ){

        UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        String requestuserinfo= new Gson().toJson(mInfo);
        System.out.println("requestuserinfo web"+requestuserinfo+"mInfo"+mInfo);



        JSONObject jsonObj = new JSONObject();


        try {
            String senddate = "/Date("+date+")/";
            jsonObj.put("CodeCourier",courcode);
            jsonObj.put("date",senddate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String GETRSTVERFYREQ = "{\"UserInfo\""+":"+requestuserinfo+","+jsonObj.toString().substring(1, jsonObj.toString().length() - 1)+"}";
        System.out.println("GETRSTVERFYREQ"+GETRSTVERFYREQ);
        String resp = null;
        String jsoncheckcnteResp = null;
        try {
            JSONObject obj = new JSONObject(GETRSTVERFYREQ);
           resp = JsonService.jsonnewpostreq(url,"GetRunsheetVerify",obj).toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);
       System.out.println("ResponsePostReq delis:"+resp);

       *//* try {
            JSONObject json = new JSONObject(String.valueOf(resp));
          jsoncheckcnteResp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsoncheckcnteResp are"+jsoncheckcnteResp);
*//*

     // return  jsoncheckcnteResp;


        System.out.println("resp with d are:"+resp);

        GetRunsheetVerifyResponse  getrstverObjResp = new Gson().fromJson(resp, GetRunsheetVerifyResponse.class);

        System.out.println("getrstverObjResp are"+getrstverObjResp.getd());

        return  getrstverObjResp.getd();

    }*/

    public static boolean GET_SERVICE_STATUS(){


        String JsonParam="";

        JsonParam +="ServiceStatus";

        Object ResponseData = JsonService.jsonreq(JsonParam,StringResponse.class);

        Log.e("getservstsreq", JsonParam);
        Log.e("getservstsRespObject", ResponseData.toString());
        StringResponse ResponseJson = (StringResponse)ResponseData;

        Log.e("getservstsResponse", ResponseJson.getd().toString());

        return  Boolean.valueOf(ResponseJson.getd());

  /*   //   return1 true;
        String ResultData = SoapService.soapResult(XMLData);
        if(ResultData==null)  ResultData = SoapService.soapResult(XMLData);
        if(ResultData!=null)     Log.e("ResultData is",ResultData);
        else return false;

        String Results = XMLParser.setservicestatusParser(ResultData);
        System.out.println("Connect result in get service status"+Results);
        Log.e("SET_SERVSTAT/END", "Success");
     //  return1 Results;
        if(Results.equals("TRUE")) return true;
        else return false;*/
    }

   /* public static GetRunsheetVerifyDet[] GetRunsheetVerifyDet(String password, String username,String identifier) {
        String JsonParam ="";


        UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        String requestuserinfo= new Gson().toJson(mInfo);
        System.out.println("requestuserinfo web"+requestuserinfo+"mInfo"+mInfo);



        JSONObject jsonObj = new JSONObject();


        try {

            jsonObj.put("Identifier",identifier);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+jsonObj.toString().substring(1, jsonObj.toString().length() - 1)+"}";
        System.out.println("CHECkrstdetNREQ"+CHECKEVENREQ);
        JSONObject resp = null;
        String jsoncheckcnteResp = null;

        List<webservice.JsonFuncClasses.JsonFuncClasses.GetRunsheetVerifyDet> ResponseData =  JsonService.JsonListrstdetail("GetRunsheetVerifyDet",CHECKEVENREQ,GetRunsheetVerifyDetResponse.class);

        System.out.println("ResponseData resp pckp iis:"+ResponseData.size());
        GetRunsheetVerifyDet[] Arrayrstverfydetal = new GetRunsheetVerifyDet[ResponseData.size()];

        int i = 0;

        for (webservice.JsonFuncClasses.JsonFuncClasses.GetRunsheetVerifyDet rec:ResponseData
                ) {

            Arrayrstverfydetal[i] = new GetRunsheetVerifyDet();
            Arrayrstverfydetal[i].Amount = rec.Amount;
            Arrayrstverfydetal[i].Area = rec.Area;
            Arrayrstverfydetal[i].Connote = rec.Connote;
            Arrayrstverfydetal[i].ConsigneeName = rec.ConsigneeName;
            Arrayrstverfydetal[i].ErrMessage = rec.ErrMessage;
            Arrayrstverfydetal[i].LastEvent= rec.LastEvent;
            Arrayrstverfydetal[i].PODDate= rec.PODDate;
            Arrayrstverfydetal[i].PODName= rec.PODName;
            Arrayrstverfydetal[i].RecepitDate= rec.RecepitDate;
            Arrayrstverfydetal[i].payType= rec.payType;

            i+=1;
            Log.e("Data Connote:/suc ",rec.Connote);
        }
        Log.e("Connote:/suc ", String.valueOf(Arrayrstverfydetal));

        return Arrayrstverfydetal;


    }

    public static String UpdateRunsheetVerify(String password, String username, String curcode,String identifier,String odometr, String verifysts ){

        UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        String requestuserinfo= new Gson().toJson(mInfo);
        System.out.println("requestuserinfo upd web"+requestuserinfo+"mInfo"+mInfo);


        runshtup ReqRstUpdt = new runshtup(curcode,identifier,odometr,verifysts);
        String reqrstup= new Gson().toJson(ReqRstUpdt);
        System.out.println("reqrstup:"+reqrstup+"identifier:"+identifier);
        JSONObject jsonObj = new JSONObject();
        String UPDATERST = "{\"UserInfo\""+":"+requestuserinfo+","+"\"runshtup\""+":"+reqrstup+"}";
        System.out.println("UPDATERST"+UPDATERST);
        JSONObject resp = null;
      *//*  String jsoncheckcnteResp = null;
        try {
            JSONObject obj = new JSONObject(CHECKEVENREQ);
            resp = JsonService.jsonnewpostreq(url,"CheckConnoteEvent",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            jsonObj.put("runshtup",runshtupdt);
            System.out.println("runshtupdt"+runshtupdt);
        } catch (JSONException e) {
            e.printStackTrace();
        }*//*

    *//*    String UPDATERST = "{\"UserInfo\""+":"+requestuserinfo+","+jsonObj.toString().substring(1, jsonObj.toString().length() - 1)+"}";
        System.out.println("UPDATERST"+UPDATERST);
        JSONObject resp = null;*//*
        String jsoncheckcnteResp = null;
        try {
            JSONObject obj = new JSONObject(UPDATERST);
            resp = JsonService.jsonnewpostreq(url,"UpdateRunsheetVerify",obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);
        System.out.println("UPDATERST delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckcnteResp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.e("String UPDATERST", String.valueOf(jsoncheckcnteResp));

       return  jsoncheckcnteResp;


    }

*/
   public static  JSONObject GetRunsheetDetails(String LoginToken, String rstnumber ){

       // UserInfo mInfo=new UserInfo(password,username);
       GsonBuilder builder = new GsonBuilder();
       builder.disableHtmlEscaping();

       String GetRstDETREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_runsheetnum\""+":"+rstnumber+"}";
       System.out.println("GetRstDETREQ"+GetRstDETREQ);
       JSONObject resp = null;
       String jsoncheckcnteResp = null;
       try {
           JSONObject obj = new JSONObject(GetRstDETREQ);
           System.out.println("obj  rst req"+obj);
           resp = JsonService.jsonnewpostreq(url,"GetRunsheetDetails",obj);
       } catch (JSONException e) {
           e.printStackTrace();
       }

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

       System.out.println("ResponsePostReqgetdelis:"+resp);

       try {
           JSONObject json = new JSONObject(String.valueOf(resp));
           jsoncheckcnteResp=json.getString("d");
       } catch (JSONException e) {
           e.printStackTrace();
       }

       Log.e("String checkcnteResp", jsoncheckcnteResp);
       return  resp;


   }


    public static  JSONObject CheckConnoteVerify(String LoginToken, String rstnumber ,String connote){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();

        String CHCKCNTVERFYREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_runsheetnum\""+":"+rstnumber+","+"\"v_connote\""+":"+connote+"}";
        System.out.println("CHCKCNTVERFYREQ"+CHCKCNTVERFYREQ);
        JSONObject resp = null;
        String jsoncheckcnteResp = null;
        try {
            JSONObject obj = new JSONObject(CHCKCNTVERFYREQ);
            System.out.println("obj  chckverf req"+obj);
            resp = JsonService.jsonnewpostreq(url,"CheckConnoteVerify",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReqgetdelis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckcnteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String checkcnteResp", jsoncheckcnteResp);
        return  resp;


    }


    public static String SetRunsheetVerify(String LoginToken,String Rstnumbr ,JSONArray jarray ){
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject postedJSON = new JSONObject();

        System.out.println("jsonArray setreqts:"+jsonArray);
        try {

            jsonObj.put("vs_token",LoginToken);
            jsonObj.put("v_runsheetnum",Rstnumbr);
            jsonObj.put("v_connote",jarray);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resputl= null;
        String jsonrstvefresp = null;
        System.out.println("jsonobj setrstvfey:"+jsonObj);

        resputl = JsonService.jsonnewpostreq(url,"SetRunsheetVerify",jsonObj).toString();


        try {
            JSONObject json = new JSONObject(String.valueOf(resputl));
            jsonrstvefresp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonrstsave are"+jsonrstvefresp);




        return  jsonrstvefresp;


    }

    public static String CheckConnoteMaxWC(String LoginToken, String connote ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);

       // CheckEvent chckevnt = new CheckEvent(connote);
       // String reqchckeve= new Gson().toJson(chckevnt);
//        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        String CHECKWCREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+"}";
        System.out.println("CHECKWCREQ"+CHECKWCREQ);
        JSONObject resp = null;
        String jsoncheckwcResp = null;
        try {
            JSONObject obj = new JSONObject(CHECKWCREQ);
            System.out.println("obj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"CheckConnoteMaxWC",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckwcResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String checkcnteResp", jsoncheckwcResp);
        return  jsoncheckwcResp;


    }

    public static String GetConnoteMaxWC(String LoginToken, String connote ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);

        // CheckEvent chckevnt = new CheckEvent(connote);
        // String reqchckeve= new Gson().toJson(chckevnt);
//        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        String GETWCREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+"}";
        System.out.println("GETWCREQ"+GETWCREQ);
        JSONObject resp = null;
        String jsongetwcResp = null;
        try {
            JSONObject obj = new JSONObject(GETWCREQ);
            System.out.println("obj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"GetConnoteMaxWC",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsongetwcResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String jsongetwcResp", jsongetwcResp);
        return  jsongetwcResp;


    }

    public static String UpdateConnoteMaxWC(String LoginToken,JSONArray jarray,String Reason ){
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject postedJSON = new JSONObject();

        System.out.println("jsonArray setreqts:"+jsonArray);
        try {

            jsonObj.put("vs_token",LoginToken);
            jsonObj.put("v_maxwc",jarray);
            jsonObj.put("v_reason",Reason);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resputl= null;
        String jsonrstvefresp = null;
        System.out.println("jsonobj updtecnteWC:"+jsonObj);

        resputl = JsonService.jsonnewpostreq(url,"UpdateConnoteMaxWC",jsonObj).toString();


        try {
            JSONObject json = new JSONObject(String.valueOf(resputl));
            jsonrstvefresp=json.getString("d");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonupdteWC are"+jsonrstvefresp);




        return  jsonrstvefresp;


    }

    public static String CheckBagconnote(String LoginToken, String connote, String bagno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        System.out.println("bagno inweberv:"+bagno);
        if(bagno.contentEquals("")){
            bagno=null;
        }
        String CHECKCONBAGREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+","+"\"v_bagNo\""+":"+bagno+"}";
        System.out.println("CHECKCONBAGREQ"+CHECKCONBAGREQ);
        JSONObject resp = null;
        String jsoncheckcntebagResp = null;
        try {
            JSONObject obj = new JSONObject(CHECKCONBAGREQ);
            System.out.println("bagobj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"CheckBagconnote",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckcntebagResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String checkcntebagResp", jsoncheckcntebagResp);
        return  jsoncheckcntebagResp;


    }

    public static String SetConsolidateBag(String LoginToken, String bagno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";

        if(bagno.contentEquals("")){
            bagno=null;
        }
        String SETCONBAGREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_bagNo\""+":"+bagno+"}";
        System.out.println("SETCONBAGREQ"+SETCONBAGREQ);
        JSONObject resp = null;
        String jsonsetcntebagResp = null;
        try {
            JSONObject obj = new JSONObject(SETCONBAGREQ);
            System.out.println("setbagobj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"SetConsolidateBag",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsonsetcntebagResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.e("String setcntebagResp", jsonsetcntebagResp);
        return  jsonsetcntebagResp;


    }

    public static  JSONObject GetConsolidateBag(String LoginToken, String bagNo ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();

        String GetBagConsREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_bagNo\""+":"+bagNo+"}";
        System.out.println("GetBagConsREQ"+GetBagConsREQ);
        JSONObject resp = null;
        String jsongetbagconteResp = null;
        try {
            JSONObject obj = new JSONObject(GetBagConsREQ);
            System.out.println("obj  rst req"+obj);
            resp = JsonService.jsonnewpostreq(url,"GetConsolidateBag",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReqgetdelis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsongetbagconteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.e("String getbagcnteResp", jsongetbagconteResp);
        return  resp;


    }

    public static  JSONObject GetConnoteConsolidationBag(String LoginToken, String awbno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();

        String GetBagconteREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+awbno+"}";
        System.out.println("GetBagconteREQ"+GetBagconteREQ);
        JSONObject resp = null;
        String jsongetawbconteResp = null;
        try {
            JSONObject obj = new JSONObject(GetBagconteREQ);
            System.out.println("obj  rst req"+obj);
            resp = JsonService.jsonnewpostreq(url,"GetConnoteConsolidationBag",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReqgetdelis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsongetawbconteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String getBagawbResp", jsongetawbconteResp);
        return  resp;


    }

    public static String DeleteBagconnote(String LoginToken, String connote, String bagno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        System.out.println("bagno inweberv:"+bagno);
        if(bagno.contentEquals("")){
            bagno=null;
        }
        String DELCONBAGREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+","+"\"v_bagNo\""+":"+bagno+"}";
        System.out.println("DELCONBAGREQ"+DELCONBAGREQ);
        JSONObject resp = null;
        String jsondelcntebagResp = null;
        try {
            JSONObject obj = new JSONObject(DELCONBAGREQ);
            System.out.println("obj del req"+obj);
            resp = JsonService.jsonnewpostreq(url,"DeleteBagconnote",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsondelcntebagResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String delcntebagResp", jsondelcntebagResp);
        return  jsondelcntebagResp;


    }

    public static String CheckManifestConnote(String LoginToken, String connote, String bagno,String manifstno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        System.out.println("bagno inweberv:"+bagno);
        if(bagno.contentEquals("")){
            bagno=null;
        }
        String CHECKMANICONREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+","+"\"v_bagNo\""+":"+bagno+","+"\"v_manifestNo\""+":"+manifstno+"}";
        System.out.println("CHECKMANICONREQ"+CHECKMANICONREQ);
        JSONObject resp = null;
        String jsoncheckmanicnteResp = null;
        try {
            JSONObject obj = new JSONObject(CHECKMANICONREQ);
            System.out.println("MNFTobj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"CheckManifestConnote",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsoncheckmanicnteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String checkmanicntResp", jsoncheckmanicnteResp);
        return  jsoncheckmanicnteResp;


    }

    public static String SetManifest(String LoginToken, String manifestNo ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";

        if(manifestNo.contentEquals("")){
            manifestNo=null;
        }
        String SETMANIREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_manifestNo\""+":"+manifestNo+"}";
        System.out.println("SETMANIREQ"+SETMANIREQ);
        JSONObject resp = null;
        String jsonsetmfstResp = null;
        try {
            JSONObject obj = new JSONObject(SETMANIREQ);
            System.out.println("setmfstobj req"+obj);
            resp = JsonService.jsonnewpostreq(url,"SetManifest",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsonsetmfstResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log.e("String setcntebagResp", jsonsetcntebagResp);
        return  jsonsetmfstResp;


    }

    public static  JSONObject GetManifestBagConnote(String LoginToken, String bagNo ,String manifstno ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();

        String GetMnftBgCntREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_bagNo\""+":"+bagNo+","+"\"v_manifestNo\""+":"+manifstno+"}";
        System.out.println("GetMnftBagcntREQ"+GetMnftBgCntREQ);
        JSONObject resp = null;
        String jsongetbagconteResp = null;
        try {
            JSONObject obj = new JSONObject(GetMnftBgCntREQ);
            System.out.println("obj  mftbgcnt req"+obj);
            resp = JsonService.jsonnewpostreq(url,"GetManifestBagConnote",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("Responsmnfttdelis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsongetbagconteResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log.e("String getbagcnteResp", jsongetbagconteResp);
        return  resp;


    }

    public static String DeleteManifestBagconnote(String LoginToken, String connote, String bagno , String mnfstNo ){

        // UserInfo mInfo=new UserInfo(password,username);
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // String requestuserinfo= new Gson().toJson(mInfo);
        // System.out.println("requestuserinfo web"+requestuserinfo);


        // String CHECKEVENREQ = "{\"UserInfo\""+":"+requestuserinfo+","+"\"CheckEvent\""+":"+reqchckeve+"}";
        System.out.println("bagno del inweberv:"+bagno);
        if(bagno.contentEquals("")){
            bagno=null;
        }
        String DELMNSTREQ = "{\"vs_token\""+":"+LoginToken+","+"\"v_connote\""+":"+connote+","+"\"v_bagNo\""+":"+bagno+","+"\"v_manifestNo\""+":"+mnfstNo+"}";
        System.out.println("DELmnfstREQ"+DELMNSTREQ);
        JSONObject resp = null;
        String jsondelmfstResp = null;
        try {
            JSONObject obj = new JSONObject(DELMNSTREQ);
            System.out.println("obj delmnft req"+obj);
            resp = JsonService.jsonnewpostreq(url,"DeleteManifestBagconnote",obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ResponseEntity<?> ResponsePostReq = JsonService.jsonpostreq("UserAuthentication",request,StringResponse.class);

        System.out.println("ResponsePostReq delis:"+resp);

        try {
            JSONObject json = new JSONObject(String.valueOf(resp));
            jsondelmfstResp=json.getString("d");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("String delmnfstesp", jsondelmfstResp);
        return  jsondelmfstResp;


    }

}