package com.chilloutrecords.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "EatOutDataBase.db";
    public static final int DATABASE_VERSION = 1;


    // Cuisine Details stored in the Database ======================================================
    public static final String CUISINE_TABLE_NAME = "CUISINE_TABLE";
    public static final String CUISINE_TABLE_ID = "ID";
    public static final String CUISINE_NAME = "NAME";

    // Area Details stored in the Database =========================================================
    public static final String RETRIEVE_ALL_RESTAURANTS = "RETRIEVE_ALL_RESTAURANTS";
    public static final String RETRIEVE_LIKED_RESTAURANTS = "RETRIEVE_LIKED_RESTAURANTS";
    public static final String RETRIEVE_COLLECTION_RESTAURANTS = "RETRIEVE_COLLECTION_RESTAURANTS";
    public static final String RETRIEVE_ALL_OFFERS = "RETRIEVE_ALL_OFFERS";

    // Area Details stored in the Database =========================================================
    public static final String AREA_TABLE_NAME = "AREA_TABLE";
    public static final String AREA_TABLE_ID = "ID";
    public static final String AREA_NAME = "NAME";

    // Liked Restaurants stored in the Database ====================================================
    public static final String LIKED_RESTAURANTS_TABLE_NAME = "LIKED_RESTAURANTS";
    public static final String LIKED_RESTAURANT_TABLE_ID = "RESTAURANT_ID";

    // Collection Restaurant stored in the Database ================================================
    public static final String COLLECTION_RESTAURANT_TABLE_NAME = "COLLECTION_RESTAURANTS";
    public static final String COLLECTION_RESTAURANT_TABLE_ID = "TABLE_ID";
    public static final String COLLECTION_RESTAURANT_COLLECTION_ID = "COLLECTION_ID";
    public static final String COLLECTION_RESTAURANT_ID = "RESTAURANT_ID";

    // Restaurants stored in the Database =========================================================
    public static final String RESTAURANT_TABLE_NAME = "RESTAURANT_TABLE";
    public static final String RESTAURANT_ID = "RESTAURANT_ID";
    public static final String RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String RESTAURANT_AVERAGE_RATING = "RESTAURANT_AVERAGE_RATING";
    public static final String RESTAURANT_IMAGE = "RESTAURANT_IMAGE";
    public static final String RESTAURANT_CITY_NAME = "RESTAURANT_CITY_NAME";
    public static final String RESTAURANT_AREA_NAME = "RESTAURANT_AREA_NAME";
    public static final String RESTAURANT_CUISINE_NAME = "RESTAURANT_CUISINE_NAME";
    public static final String RESTAURANT_OFFER_ICON = "RESTAURANT_OFFER_ICON";
    public static final String RESTAURANT_DISTANCE = "RESTAURANT_DISTANCE";
    public static final String RESTAURANT_CITY_ID = "RESTAURANT_CITY_ID";

    public Database() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + CUISINE_TABLE_NAME +
                "(" + CUISINE_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                CUISINE_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + AREA_TABLE_NAME +
                "(" + AREA_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                AREA_NAME + " TEXT)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + LIKED_RESTAURANTS_TABLE_NAME +
                "(" + LIKED_RESTAURANT_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + COLLECTION_RESTAURANT_TABLE_NAME +
                "(" + COLLECTION_RESTAURANT_TABLE_ID + " INTEGER PRIMARY KEY NOT NULL," +
                COLLECTION_RESTAURANT_COLLECTION_ID + " TEXT," +
                COLLECTION_RESTAURANT_ID + " TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + RESTAURANT_TABLE_NAME +
                "(" + RESTAURANT_ID + " INTEGER PRIMARY KEY NOT NULL," +
                RESTAURANT_NAME + " TEXT," +
                RESTAURANT_AVERAGE_RATING + " TEXT," +
                RESTAURANT_IMAGE + " TEXT," +
                RESTAURANT_CITY_NAME + " TEXT," +
                RESTAURANT_AREA_NAME + " TEXT," +
                RESTAURANT_CUISINE_NAME + " TEXT," +
                RESTAURANT_OFFER_ICON + " TEXT," +
                RESTAURANT_DISTANCE + " TEXT," +
                RESTAURANT_CITY_ID + " TEXT" +
                ")"
        );

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


    // DELETE ALL FUNCTIONS ========================================================================

    public void deleteCuisineTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CUISINE_TABLE_NAME);

    }

    public void deleteAreaTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + AREA_TABLE_NAME);

    }

    public void deleteLikeTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LIKED_RESTAURANTS_TABLE_NAME);

    }

    public void deleteCollectionTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COLLECTION_RESTAURANT_TABLE_NAME);
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CUISINE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AREA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_RESTAURANT_TABLE_NAME);

        onCreate(db);
    }


    // CUISINE FUNCTIONS ===========================================================================

    public void setCuisine(String CuisineID, String CuisineName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUISINE_TABLE_ID, CuisineID);
        contentValues.put(CUISINE_NAME, CuisineName);
        db.insert(CUISINE_TABLE_NAME, null, contentValues);
    }

    public List<String> getAllCuisines() {
        final List<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CUISINE_TABLE_NAME, null);
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(CUISINE_NAME)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    public String getCuisineIDByName(String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = CUISINE_NAME + " = ?";
        String[] whereArgs = new String[]{Name};
        Cursor cursor = db.query(CUISINE_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(CUISINE_TABLE_ID));
    }


    // AREA FUNCTIONS ==============================================================================

    public void setArea(String AreaID, String AreaName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AREA_TABLE_ID, AreaID);
        contentValues.put(AREA_NAME, AreaName);
        db.insert(AREA_TABLE_NAME, null, contentValues);
    }

    public List<String> getAllAreas() {
        final List<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AREA_TABLE_NAME, null);
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                arrayList.add(cursor.getString(cursor.getColumnIndex(AREA_NAME)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    public String getAreaIDByName(String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = AREA_NAME + " = ?";
        String[] whereArgs = new String[]{Name};
        Cursor cursor = db.query(AREA_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(AREA_TABLE_ID));
    }


    // LIKED RESTAURANT FUNCTIONS ==================================================================

    public void setLikedRestaurant(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LIKED_RESTAURANT_TABLE_ID, RestaurantID);

        String whereClause = LIKED_RESTAURANT_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{RestaurantID};
        int no_of_rows_affected = db.update(LIKED_RESTAURANTS_TABLE_NAME, contentValues, whereClause, whereArgs);

        if (no_of_rows_affected == 0) {
            db.insert(LIKED_RESTAURANTS_TABLE_NAME, null, contentValues);
        }
    }

    public Boolean getLikedRestaurantIDMatch(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = LIKED_RESTAURANT_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(RestaurantID)};
        Cursor cursor = db.query(LIKED_RESTAURANTS_TABLE_NAME, null, whereClause, whereArgs,
                null, null, null);
        int count = cursor.getCount();
        return count > 0;
    }

    public void deleteLikedRestaurant(String RestaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = LIKED_RESTAURANT_TABLE_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(RestaurantID)};
        db.delete(LIKED_RESTAURANTS_TABLE_NAME, whereClause, whereArgs);
    }

    // COLLECTION RESTAURANT =======================================================================

    public void setCollectionRestaurants(String collectionID, String restaurantID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLLECTION_RESTAURANT_COLLECTION_ID, collectionID);
        contentValues.put(COLLECTION_RESTAURANT_ID, restaurantID);
        db.insert(COLLECTION_RESTAURANT_TABLE_NAME, null, contentValues);
    }

    // RESTAURANT FUNCTIONS ========================================================================

    private String formatDistance(Double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.round(new MathContext(4));
        double rounded = bd.doubleValue();
        if (rounded < 1) {
            BigDecimal bd2 = new BigDecimal(rounded*1000);
            bd2 = bd2.round(new MathContext(4));
            return String.valueOf(bd2) + " Meters";
        } else {
            return String.valueOf(rounded) + " Kilometers";
        }
    }

//    public GeneralModel setRestaurants(JSONObject json_data) {
//
//        GeneralModel artistModel = new GeneralModel();
//        try {
//            artistModel.id = json_data.getString("id");
//            artistModel.name = json_data.getString("name");
//            artistModel.average_rating = json_data.getString("average_rating");
//            artistModel.image = json_data.getString("full_thumb_image_url");
//
//            JSONObject city_name = json_data.getJSONObject("city");
//            if (!city_name.isNull("name")) {
//                artistModel.city_name = city_name.getString("name");
//            }
//
//            if (json_data.getDouble("distance") == 0) {
//                artistModel.distance = "Unknown";
//            } else {
//                artistModel.distance = formatDistance(json_data.getDouble("distance"));
//            }
//
//            JSONObject area_name = json_data.getJSONObject("area");
//            if (!area_name.isNull("name")) {
//                artistModel.area_name = area_name.getString("name");
//            }
//
//            JSONObject city_id = json_data.getJSONObject("city");
//            if (!area_name.isNull("name")) {
//                artistModel.city_id = city_id.getString("id");
//            }
//
//            artistModel.offer_icon = json_data.getString("offer_icon");
//
////            JSONArray active_offersArray = json_data.getJSONArray("active_offers");
////            for (int v = 0; v < active_offersArray.length(); v++) {
////                if (!active_offersArray.isNull(v)) {
////                    JSONObject active_offers = active_offersArray.getJSONObject(v);
////                    if (!active_offers.isNull("offer_category")) {
////                        JSONObject offer_category = active_offers.getJSONObject("offer_category");
////                        int OFFER_CATEGORY_ID = offer_category.getInt("id");
//////                        if (OFFER_CATEGORY_ID == PIZZA_CARD) {
////                        artistModel.offer_id = active_offers.getString("id");
////                        artistModel.offer_category_id = String.valueOf(OFFER_CATEGORY_ID);
//////                        }
////                    }
////                }
////            }
//
////            if(!COLLECTION_ID.equals("")){
////                artistModel.offer_collection_id = COLLECTION_ID;
////            }
//
//            JSONArray cuisine_name_array = json_data.getJSONArray("cuisines");
//            for (int v = 0; v < cuisine_name_array.length(); v++) {
//                JSONObject cuisine_name = cuisine_name_array.getJSONObject(v);
//                if (!cuisine_name.isNull("name")) {
//                    artistModel.cuisine_name = artistModel.cuisine_name.concat(cuisine_name.getString("name").concat(" | "));
//                }
//            }
//
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(RESTAURANT_ID, artistModel.id);
//            contentValues.put(RESTAURANT_NAME, artistModel.name);
//            contentValues.put(RESTAURANT_AVERAGE_RATING, artistModel.average_rating);
//            contentValues.put(RESTAURANT_IMAGE, artistModel.image);
//            contentValues.put(RESTAURANT_CITY_NAME, artistModel.city_name);
//            contentValues.put(RESTAURANT_AREA_NAME, artistModel.area_name);
//            contentValues.put(RESTAURANT_CUISINE_NAME, artistModel.cuisine_name);
//            contentValues.put(RESTAURANT_OFFER_ICON, artistModel.offer_icon);
//            contentValues.put(RESTAURANT_DISTANCE, artistModel.distance);
//            contentValues.put(RESTAURANT_CITY_ID, artistModel.city_id);
//
//            String whereClause = RESTAURANT_ID + " = ?";
//            String[] whereArgs = new String[]{artistModel.id};
//            int no_of_rows_affected = db.update(RESTAURANT_TABLE_NAME, contentValues, whereClause, whereArgs);
//
//            if (no_of_rows_affected == 0) {
//                db.insert(RESTAURANT_TABLE_NAME, null, contentValues);
//            }
//
//
//        } catch (JSONException e) {
//            Helper.LogThis("DATABASE: "+e.toString());
//            return artistModel;
//        }
//        return artistModel;
//    }

//    public ArrayList<GeneralModel> getAllRestaurants(String RetrievalType, String COLLECTION_ID) {
//
//        ArrayList<GeneralModel> list = new ArrayList<>();
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String whereClause = "";
//        String[] whereArgs = new String[]{};
//        Cursor cursor = null;
//
//        if (RetrievalType.equals(RETRIEVE_ALL_RESTAURANTS)) {
//            whereClause = RESTAURANT_CITY_ID + " = ?";
//            whereArgs = new String[]{SharedPrefs.getCityCode()};
//            cursor = db.query(RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
//                    null, null, RESTAURANT_NAME);
//
//        } else if (RetrievalType.equals(RETRIEVE_LIKED_RESTAURANTS)) {
//            cursor = db.rawQuery(CreateJoin(LIKED_RESTAURANTS_TABLE_NAME, LIKED_RESTAURANT_TABLE_ID), null);
//
//        } else if (RetrievalType.equals(RETRIEVE_COLLECTION_RESTAURANTS)) {
//            cursor = db.rawQuery(CreateJoinWithClause(COLLECTION_RESTAURANT_TABLE_NAME, COLLECTION_RESTAURANT_ID, COLLECTION_ID), null);
//
//
//        } else if (RetrievalType.equals(RETRIEVE_ALL_OFFERS)) {
//            whereClause = RESTAURANT_CITY_ID + " = ? AND " + RESTAURANT_OFFER_ICON + " != ?";
//            whereArgs = new String[]{SharedPrefs.getCityCode(), ""};
//            cursor = db.query(RESTAURANT_TABLE_NAME, null, whereClause, whereArgs,
//                    null, null, RESTAURANT_NAME);
//        }
//
//        int count = cursor.getCount();
//        if (count > 0) {
//            cursor.moveToFirst();
//            do {
//                GeneralModel artistModel = new GeneralModel();
//                artistModel.id = cursor.getString(cursor.getColumnIndex(RESTAURANT_ID));
//                artistModel.name = cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME));
//                artistModel.average_rating = cursor.getString(cursor.getColumnIndex(RESTAURANT_AVERAGE_RATING));
//                artistModel.image = cursor.getString(cursor.getColumnIndex(RESTAURANT_IMAGE));
//                artistModel.city_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_CITY_NAME));
//                artistModel.area_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_AREA_NAME));
//                artistModel.cuisine_name = cursor.getString(cursor.getColumnIndex(RESTAURANT_CUISINE_NAME));
//                artistModel.offer_icon = cursor.getString(cursor.getColumnIndex(RESTAURANT_OFFER_ICON));
//                artistModel.distance = cursor.getString(cursor.getColumnIndex(RESTAURANT_DISTANCE));
//
//                list.add(artistModel);
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }

    private String CreateJoin(String NewTableName, String NewId) {
        return "SELECT * FROM " + NewTableName + " l INNER JOIN " + RESTAURANT_TABLE_NAME + " a ON l." + NewId + " = a." + RESTAURANT_ID;
    }

    private String CreateJoinWithClause(String NewTableName, String NewId, String CollectionID) {
        return "SELECT * FROM " + NewTableName + " l INNER JOIN " + RESTAURANT_TABLE_NAME + " a ON l." + NewId + " = a." + RESTAURANT_ID + " WHERE " + COLLECTION_RESTAURANT_COLLECTION_ID + " = " + CollectionID;
    }

    public Boolean checkRestaurantTableCount() {
        boolean empty = true;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + RESTAURANT_TABLE_NAME, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt(0) != 0);
        }
        cur.close();

        return empty;
    }

}