package asimmughal.chilloutrecords.services;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import asimmughal.chilloutrecords.BuildConfig;
import asimmughal.chilloutrecords.utils.Helpers;
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
import retrofit2.http.Path;

public interface MpesaPaymentService {

    OkHttpClient okClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("X-Auth-Token", SharedPrefs.getToken())
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);
                    Helpers.LogThis("Request URL: " + request.url());
                    if (response.code() == 401) {
                        Helpers.LogThis("Response Error Code: " + response.code());
                        Helpers.LogThis("Response Error Message: " + response.message());
                        Helpers.sessionExpiryBroadcast();
                    } else if (response.code() > 300) {
                        Helpers.LogThis("Response Error Code: " + response.code());
                        Helpers.LogThis("Response Error Message: " + response.message());
                    }
                    return response;
                }
            })
            .connectTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.connection_timeOut, TimeUnit.SECONDS)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.MAIN_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // REQUEST THE MPESA CHECKOUT ==================================================================
    @FormUrlEncoded
    @POST("request-mpesa-checkout/")
    Call<JsonObject> requestMpesaCheckout(
            @Field("date") String date,         // dd-mm-yyyy
            @Field("time") String time,         //H:i
            @Field("rest_id") String rest_id,
            @Field("user_id") String user_id,
            @Field("offer_id") String offer_id,
            @Field("amount") int amount,        // Ksh
            @Field("phone") int phone           // 254722111111
    );
    //returns merchant_transaction_id


    // PROCESS THE MPESA CHECKOUT ==================================================================
    @FormUrlEncoded
    @POST("process-mpesa-checkout/")
    Call<JsonObject> processMpesaCheckout(
            @Field("merchant_transaction_id") String merchant_transaction_id
    );

    // QUERY THE MPESA CHECKOUT ====================================================================
    @FormUrlEncoded
    @POST("query-mpesa-checkout/")
    Call<JsonObject> queryMpesaCheckout(
            @Field("merchant_transaction_id") String merchant_transaction_id
    );


    // GET ALL LIKED RESTAURANTS ===================================================================
    @GET("user/favorite-by-id/{id}")
    Call<JsonObject> getAllLikedRestaurants(
            @Path("id") String userId
    );


}