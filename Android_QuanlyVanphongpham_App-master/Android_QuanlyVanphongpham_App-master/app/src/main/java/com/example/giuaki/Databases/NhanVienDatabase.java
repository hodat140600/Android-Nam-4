package com.example.giuaki.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.PhongBan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDatabase extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 2;

    // Database Name
    public static final String DATABASE_NAME = "GiuaKi.db";

    // Database Path
    public static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    public static final String TABLE_NAME = "NHANVIEN";

    //    public static final String COLUMN_ID ="ID";
    public static final String COLUMN_MANV = "MANV";
    public static final String COLUMN_HOTEN = "HOTEN";
    public static final String COLUMN_NGAYSINH = "NGAYSINH";
    public static final String COLUMN_MAPB = "MAPB";

    public NhanVienDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    private Boolean checkDataBase() {
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws Exception {
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public void createDataBase() {
        //if Db doesn't exist then copy it from assets.
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //copy Db from assets
                copyDataBase();
                Log.e("TAG", "Create DataBase");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyDataBase");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SQLiteDatabase mDataBase;

    //open DB so can query it
    public boolean openDataBase() throws Exception {
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


    @Override
    public void onCreate(SQLiteDatabase db) {
            // Script to create table.
            String script = "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGERAUTOINCREMENT NOT NULL,"
                    + COLUMN_MANV + " TEXT  PRIMARY KEY NOT NULL ,"
                    + COLUMN_HOTEN + " TEXT NOT NULL,"
                    + COLUMN_NGAYSINH + " TEXT NOT NULL,"
                    + COLUMN_MAPB + " TEXT NOT NULL,"
                    + "FOREIGN KEY(" + COLUMN_MAPB + ") REFERENCES PHONGBAN(" + COLUMN_MAPB + ") )";
            // Execute script.
        db.execSQL(script);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + NhanVienDatabase.TABLE_NAME);
        onCreate(db);
    }

    public List<NhanVien> reset() {
        dropTable();
        insert(new NhanVien("NV1", "Nguyễn Thành Nam", "1982-08-01", "PB01"));
        insert(new NhanVien("NV2", "Vũ Thị Thắm", "1992-08-12", "PB01"));
        insert(new NhanVien("NV3", "Hồ Thanh Tâm", "1990-06-05", "PB02"));
        insert(new NhanVien("NV4", "Ngô Đức Trung", "1990-08-04", "PB02"));
        insert(new NhanVien("NV5", "Vũ Văn Nam", "1992-12-02", "PB02"));
        insert(new NhanVien("NV6", "Trần Văn Thắng", "1991-08-23", "PB03"));
        insert(new NhanVien("NV7", "Hà Quang Dự", "1985-08-07", "PB03"));
        insert(new NhanVien("NV8", "Ngô Phương Lan", "1990-02-01", "PB04"));
        return select();
    }

    public List<NhanVien> select() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
//                COLUMN_ID,
                COLUMN_MANV,
                COLUMN_HOTEN,
                COLUMN_NGAYSINH,
                COLUMN_MAPB
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

        List<NhanVien> list_nhanvien = new ArrayList<>();

        while (cursor.moveToNext()) {
            list_nhanvien.add(new NhanVien(
//                    cursor.getLong(0),
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
        }

        return list_nhanvien;
    }

    public long insert(NhanVien nhanvien) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_MANV, nhanvien.getMaNv());
        values.put(COLUMN_HOTEN, nhanvien.getHoTen());
        values.put(COLUMN_NGAYSINH, nhanvien.getNgaySinh());
        values.put(COLUMN_MAPB, nhanvien.getMaPb());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public long update(NhanVien nhanvien) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MANV, nhanvien.getMaNv());
        values.put(COLUMN_HOTEN, nhanvien.getHoTen());
        values.put(COLUMN_NGAYSINH, nhanvien.getNgaySinh());
        values.put(COLUMN_MAPB, nhanvien.getMaPb());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                NhanVienDatabase.TABLE_NAME
                , values
                , NhanVienDatabase.COLUMN_MANV + "=?"
                , new String[]{String.valueOf(nhanvien.getMaNv())}
        );
    }

    public long delete(NhanVien nhanvien) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        return db.delete(
                NhanVienDatabase.TABLE_NAME
                , NhanVienDatabase.COLUMN_MANV + "=?"
                , new String[]{String.valueOf(nhanvien.getMaNv())}
        );
    }

    public long deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(NhanVienDatabase.TABLE_NAME, null, null);
    }

    public List<String> getListResult(Cursor cursor) {
        List<String> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                results.add(cursor.getString(i));
            }
        }
        return results;
    }

    public List<String> CountNVfromPB(PhongBan pb) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MAPB, COUNT(*) FROM NHANVIEN " +
                " WHERE MAPB = '" + pb.getMapb() + "' " +
                " GROUP BY MAPB ";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public List<NhanVien> select(PhongBan pb) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
//                COLUMN_ID,
                COLUMN_MANV,
                COLUMN_HOTEN,
                COLUMN_NGAYSINH,
                COLUMN_MAPB
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = "MAPB = ?";
        String[] selectionArgs = new String[]{pb.getMapb()};

        Cursor cursor = db.query(
                NhanVienDatabase.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<NhanVien> list_nhanvien = new ArrayList<>();

        while (cursor.moveToNext()) {
            list_nhanvien.add(new NhanVien(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));
        }

        return list_nhanvien;
    }

}
