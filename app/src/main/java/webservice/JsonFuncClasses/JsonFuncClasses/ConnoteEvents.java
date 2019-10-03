package webservice.JsonFuncClasses.JsonFuncClasses;

/**
 * Created by ahlaam.kazi on 1/4/2018.
 */

public class ConnoteEvents {
    private String Connote;
    private String RackNo;
    private String EventCode;
    private String EventNote;

    public ConnoteEvents(){
        Connote="";
        RackNo="";
        EventCode="";
        EventNote="";
    }

    public ConnoteEvents( String Connote,String RackNo,String EventCode,String EventNote) {
        this.Connote=Connote;
        this.RackNo=RackNo;
        this.EventCode=EventCode;
        this.EventNote=EventNote;

    }
}
