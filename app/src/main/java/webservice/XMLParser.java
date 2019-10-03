package webservice;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import webservice.FuncClasses.Couriers;
import webservice.FuncClasses.Events;


/**
 * Created by lj on 19-09-2016.
 */
public class XMLParser {

    public static String Parser(String response) {

        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

        //Xml Parser

   //     System.out.println("Inputstream is"+is);
        myParser.setInput(new StringReader(response.toString()));
   //     System.out.println("Value in stringreader is"+response.toString());
   //     myParser.setInput(is,null);
   //     System.out.println("Value in myparser is"+myParser);
   //     String loginResult;
        int event = myParser.getEventType();
   //     System.out.println("Value in event is"+event);
        while (event != XmlPullParser.END_DOCUMENT)
        {

            if(event == XmlPullParser.TEXT)
            {
                response = myParser.getText();
                System.out.println("Value in xmlparser response is"+response);
            }

            event = myParser.next();
        }

        }
        catch (Exception e)
        {
            Log.e("XMLParser", "Error Printed Down");
            e.printStackTrace();
        }
        return response;
    }

    public static Events[] EventParser(String response) {

        Events[] ResultData=null;
        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            //Xml Parser
            myParser.setInput(new StringReader(response.toString()));
            //XmlPullParser myParser2 = myParser;

            int event = myParser.getEventType();
            int TagCount=0;

            while (event != XmlPullParser.END_DOCUMENT)
            {
                //System.out.println("InitWhile/NextTag : " + myParser.getName());
                if(event == XmlPullParser.END_TAG)
                    if(myParser.getName().equals("EVENTS"))
                        TagCount += 1;
                event = myParser.nextToken();
            }

            ResultData = new Events[TagCount];
            String TagFlag="";

            for (int i = 0; i < ResultData.length; i++) {
                ResultData[i]=new Events();
            }

            int DataCounter = 0;
            System.out.println("TagCount : " + TagCount);

            myParser.setInput(new StringReader(response));
            event = myParser.getEventType();
            System.out.println("Event Type Last : " + event);
            System.out.println("Event Type EndDocument : " + XmlPullParser.END_DOCUMENT);
            while (event != XmlPullParser.END_DOCUMENT)
            {
                //System.out.println("Parser Depth : " + myParser.getDepth());
                if(event == XmlPullParser.START_TAG)
                {
                    if(myParser.getName().equals("EventCode") || myParser.getName().equals("EventName"))
                    {
                        TagFlag = myParser.getName();
                        //Log.e("XML Tag/Start",TagFlag);
                    } else
                        TagFlag = "";
                }

                if(event == XmlPullParser.TEXT)
                {
                    //System.out.println("Tag Inner Text : " + myParser.getText());
                    //Log.e("DataCounter", String.valueOf(DataCounter));
                    if(TagFlag.equals("EventCode"))
                        ResultData[DataCounter].EventCode = myParser.getText();
                    if(TagFlag.equals("EventName"))
                        ResultData[DataCounter].EventName = myParser.getText();
                }

                if(event == XmlPullParser.END_TAG)
                {
                    if(myParser.getName().equals("EVENTS"))
                        DataCounter += 1;
                    //.e("XML Tag/End", String.valueOf(DataCounter));
                }

                event = myParser.next();
            }

        }
        catch (Exception e)
        {
            Log.e("XMLParserArray", "Error Printed Down");
            e.printStackTrace();
        }
        return ResultData;
    }

    public static String setconnoteeventsParser(String response) {

        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            //Xml Parser

            //     System.out.println("Inputstream is"+is);
            myParser.setInput(new StringReader(response.toString()));

            int event = myParser.getEventType();
            //     System.out.println("Value in event is"+event);
            while (event != XmlPullParser.END_DOCUMENT)
            {

                if(event == XmlPullParser.TEXT)
                {
                    response = myParser.getText();
                    //   System.out.println("Response in openRst is"+response);
                }

                event = myParser.next();
            }

        }
        catch (Exception e)
        {
            Log.e("XMLParser", "Error Printed Down");
            e.printStackTrace();
        }
        return response;
    }

    public static String checkconnoteeventParser(String response) {

        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            //Xml Parser

            //     System.out.println("Inputstream is"+is);
            myParser.setInput(new StringReader(response.toString()));

            int event = myParser.getEventType();
            //     System.out.println("Value in event is"+event);
            while (event != XmlPullParser.END_DOCUMENT)
            {

                if(event == XmlPullParser.TEXT)
                {
                    response = myParser.getText();
                    //   System.out.println("Response in openRst is"+response);
                }

                event = myParser.next();
            }

        }
        catch (Exception e)
        {
            Log.e("XMLParser", "Error Printed Down");
            e.printStackTrace();
        }
        return response;
    }

    public static Couriers[] CourierParser(String response) {

        Couriers[] ResultData=null;
        try
        {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            //Xml Parser
            myParser.setInput(new StringReader(response.toString()));
            //XmlPullParser myParser2 = myParser;

            int event = myParser.getEventType();
            int TagCount=0;

            while (event != XmlPullParser.END_DOCUMENT)
            {
                //System.out.println("InitWhile/NextTag : " + myParser.getName());
                if(event == XmlPullParser.END_TAG)
                    if(myParser.getName().equals("USERS"))
                        TagCount += 1;
                event = myParser.nextToken();
            }

            ResultData = new Couriers[TagCount];
            String TagFlag="";

            for (int i = 0; i < ResultData.length; i++) {
                ResultData[i]=new Couriers();
            }

            int DataCounter = 0;
            System.out.println("TagCount : " + TagCount);

            myParser.setInput(new StringReader(response));
            event = myParser.getEventType();
            System.out.println("Couriers Type Last : " + event);
            System.out.println("Couriers Type EndDocument : " + XmlPullParser.END_DOCUMENT);
            while (event != XmlPullParser.END_DOCUMENT)
            {
                //System.out.println("Parser Depth : " + myParser.getDepth());
                if(event == XmlPullParser.START_TAG)
                {
                    if(myParser.getName().equals("UserCode") || myParser.getName().equals("UserName"))
                    {
                        TagFlag = myParser.getName();
                        //Log.e("XML Tag/Start",TagFlag);
                    } else
                        TagFlag = "";
                }

                if(event == XmlPullParser.TEXT)
                {
                    //System.out.println("Tag Inner Text : " + myParser.getText());
                    //Log.e("DataCounter", String.valueOf(DataCounter));
                    if(TagFlag.equals("UserCode"))
                        ResultData[DataCounter].UserCode = myParser.getText();
                    if(TagFlag.equals("UserName"))
                        ResultData[DataCounter].UserName = myParser.getText();
                }

                if(event == XmlPullParser.END_TAG)
                {
                    if(myParser.getName().equals("USERS"))
                        DataCounter += 1;
                    //.e("XML Tag/End", String.valueOf(DataCounter));
                }

                event = myParser.next();
            }

        }
        catch (Exception e)
        {
            Log.e("XMLParserArray", "Error Printed Down");
            e.printStackTrace();
        }
        return ResultData;
    }

}
