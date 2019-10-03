package webservice.JsonFuncClasses.JsonFuncClasses;

/**
 * Created by ahlaam.kazi on 1/3/2018.
 */

public class CheckEvent {
    private String Connote;
    private String EventCode;

    public CheckEvent(){
        Connote="";
        EventCode="";

    }

    public CheckEvent( String Connote,String EventCode) {

        this.Connote=Connote;
        this.EventCode=EventCode;

    }
}
