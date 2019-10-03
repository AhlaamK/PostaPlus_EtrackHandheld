package util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ahlaam.kazi on 1/6/2018.
 */

public class ActivityNotification {
    public String getUserName(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        String sync_Obj = sharedPreferences.getString("username", "");
        return sync_Obj;
    }

    public void setUserName(Activity activity, String sotre_Obj ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", sotre_Obj );
        editor.commit();
    }
    public String getPassword(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        String sync_Obj = sharedPreferences.getString("password", "");
        return sync_Obj;
    }

    public void setPassword(Activity activity, String sotre_Obj ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", sotre_Obj );
        editor.commit();
    }
    public String getToken(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        String sync_Obj = sharedPreferences.getString("token", "");
        return sync_Obj;
    }

    public void setToken(Activity activity, String sotre_Obj ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", sotre_Obj );
        editor.commit();
    }
}
