package asimmughal.chilloutrecords.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefs {
    private static Context context;
    private static final String MYPREFS = "CHILLOUT_SHARED_PREFS";
    private static final String NON_DELETABLE_PREFS = "CHILLOUT_SHARED_PREFS2";
    private static final String LOGIN_PREFS = "FIRST_TIME_LOGIN";

    static {
        SharedPrefs.context = MyApplication.getAppContext();
    }

// TOKEN INFORMATION ===============================================================================

    public static String getToken() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("TOKEN", "");
    }
    public static void setToken(String token) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("TOKEN", token);
        editor.apply();
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

    public static String getCountryCode() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("COUNTRYCODE", "");
    }
    public static void setCountryCode(String countryCode) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("COUNTRYCODE", countryCode);
        editor.apply();
    }

    public static String getCityCode() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("CITYCODE", "1");
    }
    public static void setCityCode(String cityCode) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("CITYCODE", cityCode);
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

    public static String getUserFacebookID() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("FACEBOOKID", "");
    }
    public static void setUserFacebookID(String fb_id) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("FACEBOOKID", fb_id);
        editor.apply();
    }

// OTHER FUNCTIONS =================================================================================

    public static void deleteAllSharedPrefs(){
        SharedPreferences settings = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public static String getWelcomeTitle() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("WELCOME", "");
    }
    public static void setWelcomeTitle(String welcomeTitle) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("WELCOME", welcomeTitle);
        editor.apply();
    }

    public static String getWelcomeTitleDescription() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("WELCOMEDESC", "");
    }
    public static void setWelcomeTitleDescription(String welcomeTitle) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("WELCOMEDESC", welcomeTitle);
        editor.apply();
    }

    public static Boolean getFirstTimeLogin() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getBoolean("FIRST_TIME_LOGIN", true);
    }
    public static void setFirstTimeLogin(Boolean loginFirstTime) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("FIRST_TIME_LOGIN", loginFirstTime);
        editor.apply();
    }


    // NON_DELETABLE_PREFS =========================================================================
    // DATABASE VERSION NUMBER =====================================================================

    public static String getSupportNumber() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("SUPPORTNUMBER", "");
    }
    public static void setSupportNumber(String support_number) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("SUPPORTNUMBER", support_number);
        editor.apply();
    }

    public static Integer getOldDataBaseVersion() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("DATABASE_VERSION", 0);
    }
    public static void setNewDataBaseVersion(int db_version) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("DATABASE_VERSION", db_version);
        editor.apply();
    }

    public static String getTemporaryToken() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("TEMPTOKEN", "");
    }
    public static void setTemporaryToken(String token) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(NON_DELETABLE_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("TEMPTOKEN", token);
        editor.apply();
    }



}
