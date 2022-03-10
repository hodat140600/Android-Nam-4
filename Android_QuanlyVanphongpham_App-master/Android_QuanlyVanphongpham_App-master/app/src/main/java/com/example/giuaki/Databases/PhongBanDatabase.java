package com.example.giuaki.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.giuaki.Entities.PhongBan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhongBanDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GiuaKi.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    public static final String TABLE_NAME = "PHONGBAN";

    //public static final String COLUMN_ID ="ID";
    public static final String COLUMN_MAPB = "MAPB";
    public static final String COLUMN_TENPB = "TENPB";

    public PhongBanDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }
    private Boolean checkDataBase(){
        File dbFile = new File( DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }
    private void copyDataBase() throws Exception{
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0){
            mOutput.write(mBuffer,0,mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    public void createDataBase(){
        //if Db doesn't exist then copy it from assets.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist){
            this.getReadableDatabase();
            this.close();
            try {
                //copy Db from assets
                copyDataBase();
                Log.e("TAG", "Create DataBase");
            }catch (IOException mIOException){
                throw new Error("ErrorCopyDataBase");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private SQLiteDatabase mDataBase;
    //open DB so can query it
    public boolean openDataBase() throws Exception{
        String mPath = DATABASE_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        return mDataBase != null;
    }

//    public synchronized void Close(){
//        if (mDataBase != null){
//            mDataBase.close();
//        }
//        super.close();
//    }
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script to create table.
        String script = "CREATE TABLE " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_MAPB + " TEXT PRIMARY KEY NOT NULL ,"
                + COLUMN_TENPB + " TEXT NOT NULL" + ");";
        // Execute script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate

        onCreate(db);
    }

    public List<PhongBan> reset() {
        dropTable();
        insert(new PhongBan("PB01", "Phòng Giám đốc"));
        insert(new PhongBan("PB02", "Phòng Kinh doanh"));
        insert(new PhongBan("PB03", "Phòng Kỹ thuật"));
        insert(new PhongBan("PB04", "Phòng Kế toán"));
        return select();
    }

    public List<PhongBan> select() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
//        String[] projection = {
//                PhongBanDatabase.COLUMN_ID,
//                PhongBanDatabase.COLUMN_MAPB,
//                PhongBanDatabase.COLUMN_TENPB
//        };
        String[] projection = {
//                PhongBanDatabase.COLUMN_ID,
                COLUMN_MAPB,
                COLUMN_TENPB
        };

        // Filter results WHERE "mapb" = '......'
//        String selection = PhongBanDatabase.COLUMN_MAPB + " = ?";
//        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder = PhongBanDatabase.COLUMN_ID + " ASC";
        String sortOrder = null;

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<PhongBan> list_phongban = new ArrayList<>();

//        while(cursor.moveToNext()){
//            list_phongban.add(new PhongBan(cursor.getLong(0),
//                    cursor.getString(1), cursor.getString(2)));
//        }
        while (cursor.moveToNext()) {
            list_phongban.add(new PhongBan(
                    cursor.getString(0), cursor.getString(1)));
        }
        return list_phongban;
    }

    public long insert(PhongBan phongban) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PhongBanDatabase.COLUMN_MAPB, phongban.getMapb());
        values.put(PhongBanDatabase.COLUMN_TENPB, phongban.getTenpb());

        return db.insert(PhongBanDatabase.TABLE_NAME, null, values);
    }

    public long update(PhongBan phongban) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PhongBanDatabase.COLUMN_MAPB, phongban.getMapb());
        values.put(PhongBanDatabase.COLUMN_TENPB, phongban.getTenpb());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                PhongBanDatabase.TABLE_NAME
                , values
                , PhongBanDatabase.COLUMN_MAPB + "=?"
                , new String[]{String.valueOf(phongban.getMapb())}
        );
    }

    public long delete(PhongBan phongban) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        return db.delete(
                PhongBanDatabase.TABLE_NAME
                , COLUMN_MAPB + "=?"
                , new String[]{String.valueOf(phongban.getMapb())}
        );
    }

    public long deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PhongBanDatabase.TABLE_NAME, null, null);
    }


}
