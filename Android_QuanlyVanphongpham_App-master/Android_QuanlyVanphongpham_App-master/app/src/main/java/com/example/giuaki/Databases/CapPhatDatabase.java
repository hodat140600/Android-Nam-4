package com.example.giuaki.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.giuaki.Entities.CapPhat;
import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.Entities.VanPhongPham;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CapPhatDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GiuaKi.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    private static final String TABLE_NAME = "CAPPHAT";

    public static final String COLUMN_SOPHIEU ="SOPHIEU";
    public static final String COLUMN_NGAYCAP = "NGAYCAP";
    public static final String COLUMN_MAVPP = "MAVPP";
    public static final String COLUMN_MANV = "MANV";
    public static final String COLUMN_SOLUONG = "SOLUONG";

    public CapPhatDatabase(Context context)  {
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
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script to create table.
        String script = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_SOPHIEU + " TEXT PRIMARY KEY,"
                + COLUMN_NGAYCAP + " TEXT NOT NULL,"
                + COLUMN_MAVPP + " TEXT NOT NULL,"
                + COLUMN_MANV + " TEXT NOT NULL,"
                + COLUMN_SOLUONG + " INTEGER NOT NULL,"
                + "FOREIGN KEY("+COLUMN_MAVPP+") REFERENCES VANPHONGPHAM("+COLUMN_MAVPP+"), "
                + "FOREIGN KEY("+COLUMN_MANV+") REFERENCES NHANVIEN("+COLUMN_MANV+") );";

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

    public List<CapPhat> reset(){
        dropTable();
        insert(new CapPhat("PHIEU1","2018-08-25","VPP1","NV1",10));
        insert(new CapPhat("PHIEU2","2018-08-25","VPP2","NV2",15));
        insert(new CapPhat("PHIEU3","2018-08-25","VPP1","NV1",24));
        insert(new CapPhat("PHIEU4","2019-02-24","VPP3","NV3",4));
        insert(new CapPhat("PHIEU5","2018-10-30","VPP5","NV5",7));
        insert(new CapPhat("PHIEU6","2020-05-07","VPP1","NV3",16));
        insert(new CapPhat("PHIEU7","2020-05-07","VPP2","NV1",15));
        insert(new CapPhat("PHIEU8","2020-02-07","VPP6","NV4",16));
        insert(new CapPhat("PHIEU9","2018-02-09","VPP1","NV5",14));
        return select();
    }

    public List<CapPhat> select(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_SOPHIEU,
                COLUMN_NGAYCAP,
                COLUMN_MAVPP,
                COLUMN_MANV,
                COLUMN_SOLUONG
        };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder = PhongBanDatabase.COLUMN_ID + " DESC";
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

        List<CapPhat> list_capphat = new ArrayList<>();

        while(cursor.moveToNext()){
            list_capphat.add(new CapPhat(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4)
            ));
        }

        return list_capphat;
    }

    public long insert(CapPhat capphatVPP){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOPHIEU, capphatVPP.getSoPhieu());
        values.put(COLUMN_NGAYCAP, capphatVPP.getNgayCap());
        values.put(COLUMN_MAVPP, capphatVPP.getMaVpp());
        values.put(COLUMN_MANV, capphatVPP.getMaNv());
        values.put(COLUMN_SOLUONG, capphatVPP.getSl());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public long update(CapPhat capphatVPP){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SOPHIEU, capphatVPP.getSoPhieu());
        values.put(COLUMN_NGAYCAP, capphatVPP.getNgayCap());
        values.put(COLUMN_MAVPP, capphatVPP.getMaVpp());
        values.put(COLUMN_MANV, capphatVPP.getMaNv());
        values.put(COLUMN_SOLUONG, capphatVPP.getSl());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                CapPhatDatabase.TABLE_NAME
                , values
                , CapPhatDatabase.COLUMN_SOPHIEU +"=?"
                ,  new String[] { String.valueOf(capphatVPP.getMaVpp()) }
        );
    }
    public long delete(VanPhongPham vanPhongPham){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        return db.delete(
                CapPhatDatabase.TABLE_NAME
                ,CapPhatDatabase.COLUMN_SOPHIEU +"=?"
                ,  new String[] { String.valueOf(vanPhongPham.getMaVpp()) }
        );
    }
    public long deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CapPhatDatabase.TABLE_NAME,null,null);
    }

    public List<String> getListResult(Cursor cursor){
        List<String> results = new ArrayList<>();
        while(cursor.moveToNext()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                results.add(cursor.getString(i));
            }
        }
        return results;
    }
    //  THONG KE ------------------------------
    public List<String> thongKeCau2a(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                " SELECT L.MANV, L.HOTEN, L.MAPB, R.TENVPP, SUM(R.SOLUONG) AS TONGSL FROM \n" +
                "( SELECT * FROM NHANVIEN ) AS L\n" +
                "JOIN\n" +
                "( SELECT CP.MANV, VPP.MAVPP, VPP.TENVPP, CP.SOLUONG from CAPPHAT CP join VANPHONGPHAM VPP on CP.maVPP = VPP.maVPP ) as R\n" +
                "ON L.MANV = R.MANV\n" +
                "WHERE R.TENVPP = 'Giấy A4'\n" +
                "GROUP BY L.MANV\n" +
                "ORDER BY TONGSL DESC\n" +
                "LIMIT 2";
        Cursor cursor = db.rawQuery(sql,null);
        return getListResult(cursor);
    }

    public List<String> thongKeCau2b(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT DISTINCT CP.MAVPP, VPP.TENVPP, CP.NGAYCAP FROM \n" +
                "( SELECT L.MAVPP, L.NGAYCAP FROM\n" +
                "( SELECT MAVPP, NGAYCAP FROM CAPPHAT ) AS L\n" +
                "JOIN\n" +
                "( SELECT MAVPP, NGAYCAP, COUNT(NGAYCAP) AS SOLUONG FROM CAPPHAT GROUP BY NGAYCAP \n" +
                "HAVING COUNT(NGAYCAP) > 1 ) AS R\n" +
                "WHERE  L.NGAYCAP = R.NGAYCAP ) AS CP\n" +
                "JOIN VANPHONGPHAM VPP\n" +
                "ON CP.MAVPP = VPP.MAVPP\n" +
                "ORDER BY CP.NGAYCAP";
        Cursor cursor = db.rawQuery(sql, null);
        List<String> results = new ArrayList<>();
        while(cursor.moveToNext()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                if( i == 2) results.add(formatDate(cursor.getString(i), false));
                else
                    results.add(cursor.getString(i));
            }
        }
        return results;
    }

    public List<String> thongKeCau2c(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT NV.MANV, NV.HOTEN, NV.NGAYSINH , PB.TENPB FROM NHANVIEN NV JOIN PHONGBAN PB\n" +
                "ON NV.MAPB = PB.MAPB\n" +
                "WHERE MANV NOT IN\n" +
                "( \n" +
                "SELECT MANV FROM \n" +
                "( SELECT MANV, NGAYCAP FROM CAPPHAT WHERE NGAYCAP BETWEEN '2018-01-01' AND '2018-12-31' )\n" +
                ")";
        Cursor cursor = db.rawQuery(sql,null);
        List<String> results = new ArrayList<>();
        while(cursor.moveToNext()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                if( i == 2) results.add(formatDate(cursor.getString(i), false));
                else
                    results.add(cursor.getString(i));
            }
        }
        return results;
    }
    public List<String> thongKeCau2d(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT L.MAPB, L.TENPB , COALESCE(SUM(R.SOLUONG), 0 ) AS SOLUONG FROM \n" +
                "PHONGBAN AS L\n" +
                "LEFT JOIN \n" +
                "(\n" +
                "SELECT CP.*,NV.MAPB FROM CAPPHAT CP JOIN NHANVIEN NV ON CP.MANV = NV.MANV \n" +
                ") AS R \n" +
                " ON L.MAPB = R.MAPB\n" +
                " GROUP BY L.MAPB";
        Cursor cursor = db.rawQuery(sql,null);
        return getListResult(cursor);
    }
    public List<String> thongKeDefault(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT B.TENVPP, A.MAPB , A.TENPB, A.TONGSL " +
                "FROM " +
                "(SELECT * FROM " +
                VanPhongPhamDatabase.TABLE_NAME +
                ") as B " +
                "LEFT JOIN " +
                "(SELECT CP.SOPHIEU, CP.NGAYCAP, NV.MANV, NV.HOTEN, PB.MAPB ,PB.TENPB, VPP.MAVPP, VPP.TENVPP " +
                ", SUM(SOLUONG) AS TONGSL " +
                "FROM " +
                CapPhatDatabase.TABLE_NAME + " CP, " +
                NhanVienDatabase.TABLE_NAME + " NV, " +
                PhongBanDatabase.TABLE_NAME + " PB, " +
                VanPhongPhamDatabase.TABLE_NAME + " VPP " +
                "WHERE CP.MAVPP = VPP.MAVPP " +
                "AND CP.MANV = NV.MANV " +
                "AND NV.MAPB = PB.MAPB " +
                "AND PB.MAPB = 'PB01' " +
                "GROUP BY CP.MAVPP) as A " +
                "ON A.MAVPP = B.MAVPP";
        Cursor cursor = db.rawQuery(sql,null);
        return getListResult(cursor);
    }

    public List<String> selectwithVPPandNVwherePB( String maPB ){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT VPP.MAVPP, VPP.TENVPP, VPP.DVT, VPP.GIANHAP, NV.MANV, NV.HOTEN, NV.MAPB ,CP.SOLUONG \n" +
                "FROM " +
                VanPhongPhamDatabase.TABLE_NAME + " VPP, " +
                CapPhatDatabase.TABLE_NAME + " CP, " +
                NhanVienDatabase.TABLE_NAME + " NV\n" +
                "WHERE CP.MAVPP = VPP.MAVPP\n " +
                "AND CP.MANV = NV.MANV " +
                "AND NV.MAPB = '"+maPB+"'";
        sql =   "SELECT DISTINCT  R.MANV , R.HOTEN, R.MAPB, L.MAVPP, L.TENVPP, L.DVT, L.GIANHAP, SUM(SOLUONG) AS SOLUONGMUON FROM \n" +
                "( SELECT * FROM VANPHONGPHAM ) AS L\n" +
                "JOIN\n" +
                "-- NÀY LÀ TÌM NHỮNG NHÂN VIÊN CÓ MẶT TRONG CẤP PHÁT ( KÈM THEO MAPB )\n" +
                " (\tSELECT CP.MAVPP, CP.SOLUONG, CP.MANV ,NV.HOTEN,NV.MAPB FROM CAPPHAT AS CP JOIN NHANVIEN AS NV ON CP.MANV = NV.MANV ) AS R\n" +
                "ON L.MAVPP = R.MAVPP WHERE R.MAPB = 'PB01'\n" +
                "GROUP BY R.MAVPP, R.MANV";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public List<String> select_listVPP_withPB( String maPB ){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT DISTINCT  L.MAVPP, L.TENVPP, L.DVT, L.GIANHAP*SUM(R.SOLUONG) AS TRIGIA FROM \n" +
                "( SELECT * FROM VANPHONGPHAM ) AS L\n" +
                "JOIN\n" +
                "-- NÀY LÀ TÌM NHỮNG NHÂN VIÊN CÓ MẶT TRONG CẤP PHÁT ( KÈM THEO MAPB )\n" +
                " (\tSELECT CP.MAVPP, CP.SOLUONG, CP.MANV ,NV.HOTEN,NV.MAPB FROM CAPPHAT AS CP JOIN NHANVIEN AS NV ON CP.MANV = NV.MANV ) AS R\n" +
                "ON L.MAVPP = R.MAVPP WHERE R.MAPB = '"+maPB+"'\n" +
                "GROUP BY R.MAVPP";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public List<String> select_listNV_withVPP_andPB( String maPB, String maVPP ){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT  R.MANV , R.HOTEN, SUM(SOLUONG) AS SOLUONGMUON FROM \n" +
                "( SELECT * FROM VANPHONGPHAM ) AS L\n" +
                "JOIN\n" +
                "-- NÀY LÀ TÌM NHỮNG NHÂN VIÊN CÓ MẶT TRONG CẤP PHÁT ( KÈM THEO MAPB )\n" +
                " (\tSELECT CP.MAVPP, CP.SOLUONG, CP.MANV ,NV.HOTEN,NV.MAPB FROM CAPPHAT AS CP JOIN NHANVIEN AS NV ON CP.MANV = NV.MANV ) AS R\n" +
                "ON L.MAVPP = R.MAVPP WHERE R.MAPB = '"+maPB+"' AND R.MAVPP = '"+maVPP+"'"  +
                "GROUP BY R.MAVPP, R.MANV";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public List<String> countVPPfromPB( PhongBan pb ){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT NV.MAPB, SUM(SOLUONG) AS TONGSL \n" +
                "FROM CAPPHAT CP JOIN NHANVIEN NV \n" +
                "ON CP.MANV = NV.MANV \n" +
                "AND NV.MAPB = '"+pb.getMapb()+"' \n" +
                "GROUP BY MAPB";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public List<String> BaocaoQuery( PhongBan pb){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT BC.SOPHIEU,BC.MANV,BC.NGAYCAP,BC.TENVPP,BC.TRIGIA FROM  \n" +
                "(\n" +
                "SELECT R.SOPHIEU,R.NGAYCAP,R.TENVPP,R.TRIGIA,L.MANV,L.MAPB FROM\n" +
                "\t(SELECT * FROM NHANVIEN NV JOIN PHONGBAN PB ON NV.MAPB = PB.MAPB ) AS L\n" +
                "\tJOIN\n" +
                "\t(SELECT CP.SOPHIEU, CP.NGAYCAP, VPP.TENVPP, CP.SOLUONG*VPP.GIANHAP AS TRIGIA ,CP.MANV \n" +
                "\tFROM CAPPHAT CP JOIN VANPHONGPHAM VPP \n" +
                "\tON CP.MAVPP = VPP.MAVPP) AS R \n" +
                "\tON L.MANV = R.MANV \n" +
                ") BC\n" +
                "\tWHERE BC.MAPB = '"+pb.getMapb()+"'";
        Cursor cursor = db.rawQuery(sql, null);
        List<String> results = new ArrayList<>();
        int count = 1;
        while(cursor.moveToNext()){
            results.add(count++ + "");
            for(int i = 0; i < cursor.getColumnCount(); i++){
                if( i == 2) results.add(formatDate(cursor.getString(i), false));
                else
                results.add(cursor.getString(i));
            }
        }
        return results;
    }

    public List<String> BaocaoQuery( NhanVien nv ){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT SOPHIEU, NGAYCAP, TENVPP, TRIGIA FROM (\n" +
                "SELECT CP.SOPHIEU, NGAYCAP, TENVPP, SOLUONG * GIANHAP AS TRIGIA, MANV\n" +
                " FROM CAPPHAT CP JOIN VANPHONGPHAM VPP \n" +
                " ON CP.MAVPP = VPP.MAVPP \n" +
                " WHERE CP.MANV = '"+nv.getMaNv()+"'\n" +
                ") ";
        Cursor cursor = db.rawQuery(sql, null);
        List<String> results = new ArrayList<>();
        int count = 1;
        while(cursor.moveToNext()){
            results.add(count++ + "");
            for(int i = 0; i < cursor.getColumnCount(); i++){
                if( i == 1) results.add(formatDate(cursor.getString(i), false));
                else
                    results.add(cursor.getString(i));
            }
        }
        return results;
    }

    public List<String> countVPPfromPB(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT L.TENPB, COALESCE(SUM(R.SOLUONG), 0 ) AS SOLUONG FROM \n" +
                "PHONGBAN AS L\n" +
                "LEFT JOIN \n" +
                "(\n" +
                "SELECT CP.*,NV.MAPB FROM CAPPHAT CP JOIN NHANVIEN NV ON CP.MANV = NV.MANV \n" +
                ") AS R \n" +
                " ON L.MAPB = R.MAPB\n" +
                " GROUP BY L.MAPB";
        Cursor cursor = db.rawQuery(sql, null);
        return getListResult(cursor);
    }

    public String formatDate(String str, boolean toSQL ){
        String[] date ;
        String result = "";
        if( toSQL ){
            date = str.split("/");
            result = date[2] +"-"+ date[1] +"-"+ date[0];
        }else{
            date = str.split("-");
            result = date[2] +"/"+ date[1] +"/"+ date[0];
        }

        return result;
    }

}
