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
import retrofit2.http.Query;

public interface RestaurantService {

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


    //  GET RESTAURANT COLLECTION ==================================================================
    @GET("collections/restaurants/{id}")
    Call<JsonObject> getRestaurantCollection(
            @Path("id") String id,
            @Query("page") String page_number
    );

    //  GET ALL RESTAURANT COLLECTIONS =============================================================
    @GET("collections/")
    Call<JsonObject> getAllRestaurantCollections(
            @Query("featured") String featured,
            @Query("city_id") String city_id
    );


    //  GET ONE RESTAURANT =========================================================================
    @GET("restaurant/{id}")
    Call<JsonObject> getOneRestaurant(
            @Path("id") String id
    );


    // POST ONE RESTAURANTS REVIEW =================================================================
    @FormUrlEncoded
    @POST("post-review")
    Call<JsonObject> postRestaurantsReviews(
            @Field("rest_id") String rest_id,
            @Field("user_id") String user_id,
            @Field("rating_food") int rating_food,
            @Field("rating_service") int rating_service,
            @Field("rating_ambiance") int rating_ambiance,
            @Field("rating_value") int rating_value,
            @Field("rating_average") int rating_average,
            @Field("review_text") String review_text
    );


    // CLAIM OFFER FROM ONE RESTAURANT =============================================================
    @FormUrlEncoded
    @POST("claim-offer")
    Call<JsonObject> claimOffer(
            @Field("offer_id") int offer_id,
            @Field("user_id") String user_id,
            @Field("confirmation_code") String code
    );

    // FAVORITE ONE RESTAURANT =====================================================================
    @FormUrlEncoded
    @POST("favorite")
    Call<JsonObject> favoriteOneRestaurant(
            @Field("rest_id") String rest_id,
            @Field("user_id") String user_id
    );


    // GET ALL LIKED RESTAURANTS ===================================================================
    @GET("user/favorite-by-id/{id}")
    Call<JsonObject> getAllLikedRestaurants(
            @Path("id") String userId
    );


    // GET ONE RESTAURANTS REVIEWS ===================================================================
    @GET("restaurant/reviews/{id}")
    Call<JsonObject> getOneRestaurantsReviews(
            @Path("id") String id,
            @Query("page") String page_number
    );

    //  SEARCH RESTAURANT ==========================================================================
    @GET("restaurants/search/")
    Call<JsonObject> searchRestaurant(
            @Query("q") String searchTerm,
            @Query("page") String page_number,
            @Query("city") String city,
            @Query("area") String area,
            @Query("cuisines[]") String cuisines,
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("sort") String sort,
            @Query("order") String order
    );


    //  GET USERS FAVOURITES =======================================================================
    @GET("user/favorites/{id}")
    Call<JsonObject> getFavourites(
            @Path("id") String id
    );


    // UPDATE USER =================================================================================
    @FormUrlEncoded
    @POST("user/update")
    Call<JsonObject> update_user(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("city") String city,
            @Field("country_code") String country_code,
            @Field("password") String password,
            @Field("phone_number") String phone,
            @Field("date_of_birth") String date_of_birth,
            @Field("fb_id") String fb_id
    );
    // DIFFERENT CITIES CODES
    //    NAIROBI       1   KENYA
    //    MOMBASA       2   KENYA
    //    DAR ES SALAAM 14  TANZANIA
    //    KAMPALA       16  UGANDA
    //    KIGALI        20  RWANDA


    // GET ALL COUNTRIES ===========================================================================
    @GET("countries/")
    Call<JsonObject> getAllCountries(
    );


    // GET ALL AREAS FOR ONE CITY ==================================================================
    @GET("city/{id}/areas")
    Call<JsonObject> getAllAreas(
            @Path("id") String city_id
    );


    // GET ALL CUISINES FOR ========================================================================
    @GET("cuisines/")
    Call<JsonObject> getAllCuisines();


    // UPDATE FACEBOOK USER ID =====================================================================
    @FormUrlEncoded
    @POST("user/update-fb-id")
    Call<JsonObject> Update_FB_ID(
            @Field("user_id") String first_name,
            @Field("fb_id") String last_name
    );


}