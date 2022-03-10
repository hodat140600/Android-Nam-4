package com.example.giuaki.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.VanPhongPham;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class VanPhongPhamDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GiuaKi.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    public static final String TABLE_NAME = "VANPHONGPHAM";

    public static final String text = "text";
    public static final String COLUMN_MAVPP ="MAVPP";
    public static final String COLUMN_TENVPP = "TENVPP";
    public static final String COLUMN_DVT = "DVT";
    public static final String COLUMN_GIANHAP = "GIANHAP";
    public static final String COLUMN_HINH = "HINH";

    public VanPhongPhamDatabase(Context context)  {
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
    //    @Override
//    public synchronized void Close(){
//        if (mDataBase != null){
//            mDataBase.close();
//        }
//        super.close();
//    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script to create table.
        String script = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_MAVPP + " TEXT PRIMARY KEY,"
                + COLUMN_TENVPP + " TEXT NOT NULL,"
                + COLUMN_DVT + " TEXT NOT NULL,"
                + COLUMN_GIANHAP + " TEXT NOT NULL,"
                + COLUMN_HINH + " BLOB);";
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
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public List<VanPhongPham> reset(){
        dropTable();
        insert( new VanPhongPham("VPP1","Giấy A4","Gram","70000",null));
        insert( new VanPhongPham("VPP2","Kéo "   ,"Cái" ,"12000",null));
        insert( new VanPhongPham("VPP3","Bút"    ,"Cây" ,"5000" ,null));
        insert( new VanPhongPham("VPP4","Kim bấm","Hộp" ,"2000" ,null));
        insert( new VanPhongPham("VPP5","Đầu bấm","Cái" ,"18000",null));
        insert( new VanPhongPham("VPP6","Keo dán","Chai","7000" ,null));
        return select();
    }

    public List<VanPhongPham> select(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_MAVPP,
                COLUMN_TENVPP,
                COLUMN_DVT,
                COLUMN_GIANHAP,
                COLUMN_HINH
        };

        // How you want the results sorted in the resulting Cursor
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

        List<VanPhongPham> list_vanphongpham = new ArrayList<>();

        while(cursor.moveToNext()){
            list_vanphongpham.add(new VanPhongPham(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4)
            ));
        }

        return list_vanphongpham;
    }

    public long insert(VanPhongPham vanPhongPham){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAVPP, vanPhongPham.getMaVpp());
        values.put(COLUMN_TENVPP, vanPhongPham.getTenVpp());
        values.put(COLUMN_DVT, vanPhongPham.getDvt());
        values.put(COLUMN_GIANHAP, vanPhongPham.getGiaNhap());
        values.put(COLUMN_HINH, vanPhongPham.getHinh());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public long update(VanPhongPham vanPhongPham){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MAVPP, vanPhongPham.getMaVpp());
        values.put(COLUMN_TENVPP, vanPhongPham.getTenVpp());
        values.put(COLUMN_DVT, vanPhongPham.getDvt());
        values.put(COLUMN_GIANHAP, vanPhongPham.getGiaNhap());
        values.put(COLUMN_HINH, vanPhongPham.getHinh());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                VanPhongPhamDatabase.TABLE_NAME
                , values
                , VanPhongPhamDatabase.COLUMN_MAVPP +"=?"
                ,  new String[] { String.valueOf(vanPhongPham.getMaVpp()) }
        );
    }
    public long delete(VanPhongPham vanPhongPham){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        return db.delete(
                VanPhongPhamDatabase.TABLE_NAME
                ,VanPhongPhamDatabase.COLUMN_MAVPP +"=?"
                ,  new String[] { String.valueOf(vanPhongPham.getMaVpp()) }
        );
    }
    public long deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(VanPhongPhamDatabase.TABLE_NAME,null,null);
    }

}
