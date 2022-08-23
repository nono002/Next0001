package com.example.next0001;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE first(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
        //        "                    FeedDocs.COLUMN_NAME_DOC_ID + \" INTEGER PRIMARY KEY AUTOINCREMENT,\" +\n" +
        //        "                    FeedDocs.COLUMN_NAME_DOC_TYPE + \" TEXT");
        //db.execSQL(SQL_CREATE_DOCS);
        //db.execSQL(SQL_CREATE_DOC_UNITS);


        //Log.d("!!!! LOG !!!!", "--- onCreate database ---");
        // создаем таблицу с полями
        //db.execSQL(SQL_CREATE_DOCS);
        db.execSQL(SQL_CREATE_DOCS);
        Log.d("++++ CREATE TABLE ++++", "++++ CREATE DOCS ++++");
        db.execSQL(SQL_CREATE_DOC_UNITS);
        Log.d("++++ CREATE TABLE ++++", "++++ CREATE UNITS ++++");

        Log.d("!!!!!!! LOG !!!!!!!", "!!!!!! table create !!!!!!");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_DOCS);
        db.execSQL(SQL_DELETE_DOC_UNITS);
        onCreate(db);
    }

    /* Inner class that defines the table contents */
    public static class FeedDocs implements BaseColumns {
        public static final String TABLE_DOC = "docs";
        public static final String COLUMN_NAME_DOC_ID = "_id";
        public static final String COLUMN_NAME_DOC_TYPE = "DOC_TYPE";
        public static final String COLUMN_NAME_DOC_NUM = "DOC_NUM";
        public static final String COLUMN_NAME_DOC_GUID = "DOC_GUID";
        public static final String COLUMN_NAME_NUMBER = "NUMBER";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_CLIENT_NAME = "CLIENT_NAME";
        public static final String COLUMN_NAME_CLIENT_GUID = "CLIENT_GUID";
        public static final String COLUMN_NAME_CLOUD = "CLOUD";
        public static final String COLUMN_NAME_CLOUD_GUID = "CLOUD_GUID";
        public static final String COLUMN_NAME_COMMENT = "COMMENT";
        public static final String COLUMN_NAME_DOC_SUM = "DOC_SUM";

        // recvisites for 1) providenie - DOC_ACTIVE
        // & 2) number and date client doc - for 1C
        public static final String COLUMN_NAME_DOC_ACTIVE = "DOC_ACTIVE";
        public static final String COLUMN_NAME_CLIENT_NUM = "CLIENT_NUM";
        public static final String COLUMN_NAME_CLIENT_DATE = "CLIENT_DATE";
    }

    public static class FeedDocUnits implements BaseColumns {
        public static final String TABLE_DOC_UNITS = "units";
        //public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_DOC_ID = "DOC_ID";
        public static final String COLUMN_NAME_INV_NUMB = "INV_NUMB";
        public static final String COLUMN_NAME_INV_NAME = "INV_NAME";
        public static final String COLUMN_NAME_INV_NAME_DESC = "INV_NAME_DESC";
        public static final String COLUMN_NAME_INV_GUID = "INV_GUID";
        public static final String COLUMN_NAME_COST = "COST";
        public static final String COLUMN_NAME_COUNT = "COUNT";

        // inxoice fact for 1C
        public static final String COLUMN_NAME_FACT_COUNT = "FACT_COUNT";
    }
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedDocs6.db";

    private static final String SQL_CREATE_DOCS =
            "CREATE TABLE IF NOT EXISTS " + FeedDocs.TABLE_DOC + " (" +
                    FeedDocs.COLUMN_NAME_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedDocs.COLUMN_NAME_DOC_TYPE + " TEXT," +
                    FeedDocs.COLUMN_NAME_DOC_NUM + " TEXT," +
                    FeedDocs.COLUMN_NAME_DOC_GUID + " TEXT," +
                    FeedDocs.COLUMN_NAME_NUMBER + " TEXT," +
                    FeedDocs.COLUMN_NAME_DATE + " TEXT," +
                    FeedDocs.COLUMN_NAME_CLIENT_NAME + " TEXT," +
                    FeedDocs.COLUMN_NAME_CLIENT_GUID + " TEXT," +
                    FeedDocs.COLUMN_NAME_CLOUD + " TEXT," +
                    FeedDocs.COLUMN_NAME_CLOUD_GUID + " TEXT," +
                    FeedDocs.COLUMN_NAME_COMMENT + " TEXT," +
                    FeedDocs.COLUMN_NAME_DOC_ACTIVE + " INTEGER," +
                    FeedDocs.COLUMN_NAME_CLIENT_NUM + " TEXT," +
                    FeedDocs.COLUMN_NAME_CLIENT_DATE + " TEXT," +
                    FeedDocs.COLUMN_NAME_DOC_SUM + " TEXT)";

    private static final String SQL_CREATE_DOC_UNITS =
            "CREATE TABLE IF NOT EXISTS " + FeedDocUnits.TABLE_DOC_UNITS + " (_id INTEGER PRIMARY KEY," +
                    FeedDocUnits.COLUMN_NAME_DOC_ID + " INTEGER," +
                    FeedDocUnits.COLUMN_NAME_INV_NUMB + " TEXT," +
                    FeedDocUnits.COLUMN_NAME_INV_NAME + " TEXT," +
                    FeedDocUnits.COLUMN_NAME_INV_NAME_DESC + " TEXT," +
                    FeedDocUnits.COLUMN_NAME_INV_GUID + " TEXT," +
                    FeedDocUnits.COLUMN_NAME_COST + " REAL," +
                    FeedDocUnits.COLUMN_NAME_COUNT + " REAL," +
                    FeedDocUnits.COLUMN_NAME_FACT_COUNT + " REAL," +
        "FOREIGN KEY (" + FeedDocUnits.COLUMN_NAME_DOC_ID + ") REFERENCES " + FeedDocs.TABLE_DOC + "(" + FeedDocs.COLUMN_NAME_DOC_ID + "))";

    private static final String SQL_INSERT_DOC = "INSERT INTO docs (DOC_ID, DOC_TYPE) VALUES ('1', 'news01')";

    private static final String SQL_DELETE_DOCS =
            "DROP TABLE IF EXISTS " + FeedDocs.TABLE_DOC;

    private static final String SQL_DELETE_DOC_UNITS =
            "DROP TABLE IF EXISTS " + FeedDocUnits.TABLE_DOC_UNITS;

    //public FeedReaderDbHelper(MainActivity mainActivity, String databaseName, Context context, int databaseVersion) {
    //    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    //}

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    void addDoc(SQLiteDatabase db) {
        db.execSQL(SQL_INSERT_DOC);
        db.close(); // Closing database connection
    }

    public int getLastId(SQLiteDatabase db, String tableName) {
    Integer _id = 0;
    Cursor cursor = db.query(tableName, new String[]{"_id"}, null, null, null,null, "_id DESC", "1");
    if (cursor.moveToLast()){
        _id = cursor.getInt(0);
    }
        cursor.close();
        return _id;
    }

}
