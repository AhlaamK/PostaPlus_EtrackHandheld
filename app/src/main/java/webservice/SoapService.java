package webservice;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lj on 17-09-2016.
 */
public class SoapService {


    public static String soapResult(String urlParameters)
    {
       // String WebServUrl="";
        String rscanSub= urlParameters.substring(2);
        String[] rscanSp=urlParameters.split(":");
        String[] rscanSp2=rscanSp[1].split(">");

        String soapAction="http://tempuri.org/IHHSrv/"+rscanSp2[0];
        String WebServUrl="http://172.53.1.34:8080/EtrackHandHeld/HHSrv.svc?wsdl";
        URL url;
        HttpURLConnection connection = null;


        try {
            //Create connection

            urlParameters = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:hhs=\"http://schemas.datacontract.org/2004/07/HHSrv\">\n"+
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    urlParameters  +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            Log.e("ExecutePost",urlParameters);

            //Log.e("Postaservice/mastety",MasterActivity.URL);
            StringBuilder text = new StringBuilder();
/*

            try {
                File ipaddfile = new File(Environment.getExternalStorageDirectory(), "Postaplus/Data/ipaddress.txt");
                BufferedReader br = new BufferedReader(new FileReader(ipaddfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    //text.append('\n');
                }
                //}

                br.close();
                WebServUrl = text.toString();
                //ipaddress = text.toString();
                //System.out.println(ipaddress);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
*/

            url = new URL(WebServUrl);
         //   url = new URL("http://172.53.1.34:8095/EtrackHandHeld/HHSrv.svc?wsdl");
            connection = (HttpURLConnection)url.openConnection();

            Log.e("ExecutePost/Conn","Connection Pass");

          //  connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","text/xml;charset=UTF-8");
            connection.setRequestProperty("SOAPAction", soapAction);
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            //connection.setRequestProperty("Content-Language", "en-US");
            //connection.setRequestProperty("Connection","Close");keep-alive
           connection.setRequestProperty("Connection","keep-alive");
           // connection.setRequestProperty("Connection","Close");
            connection.setConnectTimeout(10000); //set timeout to 10 seconds
            connection.setReadTimeout(20000);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            Log.e("Connection is","Request Send"+connection);


            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            Log.e("ExecutePost","Request Send");

            //Get Response
            InputStream is = null;
            int status = connection.getResponseCode();
            Log.e("ExecutePost","Response : "+ status);
            if(status >= HttpURLConnection.HTTP_OK)
                //in = connection.getErrorStream();
                is = new BufferedInputStream(connection.getInputStream());
            else
                is = new BufferedInputStream(connection.getErrorStream());

            Log.e("ExecutePost","Response is : "+ is);

            BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            String response = "";
            StringBuffer response2 = new StringBuffer();
            while((line = rd.readLine()) != null) {
                //  response += line;
                response2.append(line);
                //    response2.append("\r");
                //  response += "\r";
                //System.out.println("\nResponse BY Line: " + line);
            }
            //   rd.close();

            //Log.e("ExecutePost","Response Recieved");
            Log.e("ExecutePost/Resp",response2.toString());

            rd.close();
            return response2.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {
            System.out.println("Soap Final Block Called!");
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

//  private boolean callSOAPWebService() {

}
