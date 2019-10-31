package asimmughal.chilloutrecords.utils;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.List;

import asimmughal.chilloutrecords.BuildConfig;
import asimmughal.chilloutrecords.R;

public class MyApplication extends Application{

        private static Context context;

        public void onCreate() {
            super.onCreate();
            MyApplication.context = getApplicationContext();
        }

        public static Context getAppContext() {
            return MyApplication.context;
        }

    public static void initFireBase() {
        FirebaseOptions builder = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FB_APP_ID)
                .setApiKey(getAppContext().getString(R.string.FB_API_KEY))
                .setDatabaseUrl(BuildConfig.FB_DB_URL)
                .setStorageBucket(BuildConfig.FB_STORE)
                .build();

        List<FirebaseApp> fire_base_app_list = FirebaseApp.getApps(MyApplication.getAppContext());

        if (fire_base_app_list.size() < 1) {
            FirebaseApp.initializeApp(getAppContext(), builder);
        }

    }

}
