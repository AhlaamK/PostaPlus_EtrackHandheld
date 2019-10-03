package webservice.JsonFuncClasses.JsonFuncClasses;

/**
 * Created by ahlaam.kazi on 12/31/2017.
 */

public class UserInfo  {
    private String UserName;

    private String Password;

    private String RememberMe;

    public UserInfo(){

        Password="";
        UserName="";
        RememberMe="";

    }

    public UserInfo( String Password , String RememberMe,String UserName) {


        this.Password=Password;

        this.RememberMe = RememberMe;
        this.UserName=UserName;
}

}
