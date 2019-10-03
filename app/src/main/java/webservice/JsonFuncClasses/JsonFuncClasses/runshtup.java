package webservice.JsonFuncClasses.JsonFuncClasses;

/**
 * Created by ahlaam.kazi on 2/5/2018.
 */

public class runshtup {
    private String CodeCourier;

    private String Identifier;

    private String Odometer;

    private String Verify;

    public runshtup(){


        CodeCourier="";
        Identifier="";
        Odometer="";
        Verify="";

    }

    public runshtup( String CodeCourier,String Identifier ,String Odometer,String Verify ) {



        this.CodeCourier=CodeCourier;
        this.Identifier=Identifier;
        this.Odometer=Odometer;
        this.Verify=Verify;
    }
}
