package asimmughal.chilloutrecords.services;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import asimmughal.chilloutrecords.BuildConfig;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface LoginService {

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(
                    new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("X-Auth-Token", SharedPrefs.getTemporaryToken())
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
            .connectTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .build();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MAIN_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    // AUTHENTICATION ==============================================================================
    @FormUrlEncoded
    @POST("user-token")
    Call<JsonObject> login(
            @Field("email") String username,
            @Field("password") String password
    );

    //  SEARCH RESTAURANT ==========================================================================
    @GET("restaurants/")
    Call<JsonObject> downloadAllRestaurants(
            @Query("page") String page_number
    );


    // REGISTER ====================================================================================
    @FormUrlEncoded
    @POST("register")
    Call<JsonObject> register(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("country_code") String country_code,
            @Field("phone_number") String phone_number,
            @Field("fb_id") String fb_id
    );

    // FORGOT PASSWORD =============================================================================
    @FormUrlEncoded
    @POST("user/reset-password")
    Call<JsonObject> reset_password(
            @Field("email") String email
    );

    //  AUTHENTICATION FACEBOOK USER ===============================================================
    @GET("get-fb-user")
    Call<JsonObject> getFBUser(
            @Query("fb_id") String fb_id,
            @Query("fb_email") String email
    );

    //  GET ALL COUNTRIES AND THEIR CITIES =========================================================
    @GET("countries/")
    Call<JsonObject> getCountries();

}