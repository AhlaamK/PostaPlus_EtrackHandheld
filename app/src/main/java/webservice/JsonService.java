package webservice;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.postaplus.etrack.ScreenActivity.url;

/**
 * Created by ahlaam.kazi on 10/2/2017.
 */

public class JsonService {

   // public static String url ="http://172.53.1.34:8080/EtrackHandHeld/HHSrvAj.svc/";
    public static Object jsonreq(String jsonparams, Class responseclasstype) {
        System.out.println("JsonService called");

        String WebServUrl = "";
        //URL url;
        HttpURLConnection connection = null;
        // TeST URL


        try {
            //Create connection
            StringBuilder text = new StringBuilder();
// Test URL
            String url1 = url + jsonparams;
            Log.e("url1 is", url1);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            // ResponseJson=String.valueOf(restTemplate.getForObject(url1,LogStatus.class));
            System.out.println("respcls:"+responseclasstype+"restTemplate"+restTemplate);
            return restTemplate.getForObject(url1, responseclasstype);


        } catch (Exception e) {

            System.out.println("e:"+e);

            e.printStackTrace();
            return null;

        } finally {
           /* System.out.println("json Final Block Called!");
            if (connection != null) {
                connection.disconnect();
            }*/
        }//return ResponseJson;
    }
    public static ResponseEntity<?> jsonpostreq(String MethodName, final String MethodArgs, Class responseclasstype) {
        System.out.println("JsonServicepost called");
        // Message result=null;
        try {
            //RestTemplate restTemplate = new RestTemplate(factory);
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("url1 : "+url + MethodName);
            final String url1 = url+ MethodName;

            Log.e("POST REQUEST CALLED",url1);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            //  HttpEntity<Map<String,String>> request = new HttpEntity<Map<String,String>>(MethodArgs);
          /*    HttpHeaders headers = new HttpHeaders();
            headers.getCacheControl();
              HttpEntity<String> entity = new HttpEntity<String>(MethodArgs,headers);
            System.out.println("entity is:"+entity);
           String answer = restTemplate.postForObject(url1, entity, String.class);
            System.out.println(answer+"answer");*/
            System.out.println("request sync: "+MethodArgs+"\n"+" - heardesr: ");

            ResponseEntity<?> response = restTemplate.postForEntity( url1, MethodArgs , responseclasstype);
           // System.out.println("response sync :"+response.toString()+" - resp0: "+response.getBody());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
    public static JSONObject jsonnewpostreq(String url,String MethodName, JSONObject json) {
        JSONObject jsonObjectResp = null;

        try {

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
           // OkHttpClient client = new OkHttpClient();
            // Changed to avoid socket timeout exception
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(42, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            okhttp3.RequestBody body = RequestBody.create(JSON, json.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url+ MethodName)
                    .post(body)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            System.out.println("requestjson is:"+request+"body"+body);
            String networkResp = response.body().string();
            if (!networkResp.isEmpty()) {
                jsonObjectResp = parseJSONStringToJSONObject(networkResp);

            }
        } catch (Exception ex) {
            String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
            ex.printStackTrace();
            jsonObjectResp = parseJSONStringToJSONObject(null);
            //jsonObjectResp=null;
        }
    Log.e("jsonObjectResp/",String.valueOf(jsonObjectResp));


        return jsonObjectResp;
    }
    private static JSONObject parseJSONStringToJSONObject(final String strr) {

        JSONObject response = null;
        try {
            response = new JSONObject(strr);
        } catch (Exception ex) {

            try {
                response = new JSONObject();
                response.put("result", "failed");
                response.put("data", strr);
                response.put("error", ex.getMessage());
            } catch (Exception exx) {

            }
        }
        Log.e("parse response" ,String.valueOf(response));
        return response;
    }
    /*public static List<GetRunsheetVerifyDet> JsonListrstdetail(String MethodName, Object map, Class responseclasstype) {


        ResponseEntity<GetRunsheetVerifyDetResponse> response=null;
        System.out.println("JsonService called");
        try {
            final String url1 = url+ MethodName;
            Log.e("url2 is", url1);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            Log.e("req RSTDET", map.toString());

            HttpEntity<?> request = new HttpEntity<>(map);
            System.out.println("request sync : "+request.getBody()+"\n"+" - heardesr: "+request.getHeaders());
            response = restTemplate.postForEntity( url1, request , GetRunsheetVerifyDetResponse.class);
            //  ResponseEntity<PickUpWaybillsDT> response = restTemplate.postForEntity( url1, request ,PickUpWaybillsDT.class);

            //ResponseEntity<?> response = restTemplate.postForEntity( url1,req,GET_PICKUP_WAYBILLS_DTResponse.class);
            System.out.println("response RStDET: "+response);
            Log.e("Responsey rstverdet", response.toString());
            Log.e("ResponseEntitySize: ", String.valueOf(response));

            return response.getBody().GetRSTVERFYLDET;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getBody().GetRSTVERFYLDET;
    }*/


}