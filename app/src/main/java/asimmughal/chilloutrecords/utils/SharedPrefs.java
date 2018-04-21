package asimmughal.chilloutrecords.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefs {
    private static Context context;
    private static final String MYPREFS = "CHILLOUT_SHARED_PREFS";

    static {
        context = MyApplication.getAppContext();
    }


// USER ID INFORMATION =============================================================================

    public static String getUserID() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("USERID", "");
    }
    public static void setUSerID(String userID) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("USERID", userID);
        editor.apply();
    }


// USER INFORMATION ================================================================================

    public static String getUserFullName() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("FIRSTNAME", "") + " " + mySharedPreferences.getString("LASTNAME", "");
    }
    public static void setUserFullName(String full_name) {
        if (full_name.contains(" ")) {
            String[] names = full_name.split(" ");
            setUserFirstName(names[0]);
            setUserLastName(names[1]);
        } else {
            setUserFirstName(full_name);
        }
    }

    public static String getUserFirstName() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("FIRSTNAME", "");
    }
    public static void setUserFirstName(String name) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("FIRSTNAME", name);
        editor.apply();
    }

    public static String getUserLastName() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("LASTNAME", "");
    }
    public static void setUserLastName(String name) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("LASTNAME", name);
        editor.apply();
    }

    public static String getUserPic() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("USERPIC", "");
    }
    public static void setUserPic(String userPIC) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("USERPIC", userPIC);
        editor.apply();
    }

    public static String getUserDOB() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("USERDOB", "");
    }
    public static void setUserDOB(String userDOB) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("USERDOB", userDOB);
        editor.apply();
    }

    public static String getUserPhone() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("USERPHONE", "");
    }
    public static void setUserPhone(String tel) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("USERPHONE", tel);
        editor.apply();
    }

    public static String getUserEmail() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("EMAIL", "");
    }
    public static void setUserEmail(String email) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("EMAIL", email);
        editor.apply();
    }


// OTHER FUNCTIONS =================================================================================

    public static String getDownLoadURL() {
    SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
    return mySharedPreferences.getString("DLURL", "");
}
    public static void setDownLoadURL(String dl_url) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("DLURL", dl_url);
        editor.apply();
    }

    public static void deleteAllSharedPrefs(){
        SharedPreferences settings = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

}
