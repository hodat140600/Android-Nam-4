package com.example.giuaki.Statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Databases.NhanVienDatabase;
import com.example.giuaki.Databases.PhongBanDatabase;
import com.example.giuaki.Databases.VanPhongPhamDatabase;
import com.example.giuaki.Entities.CapPhat;
import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.Entities.Rows;
import com.example.giuaki.Entities.VanPhongPham;
import com.example.giuaki.Main.ThongkeLayout;
import com.example.giuaki.R;

import java.util.ArrayList;
import java.util.List;

public class CapphatVPPLayout extends AppCompatActivity {
    public static PhongBan selectedPB = null;
    public static int totalMoney = 0;
    // Main Layout
    Button backBtn;

    Spinner PBSpinner;

    LinearLayout cp_tablesall_container;
    LinearLayout cp_tablesindex_container;
    TableLayout cp_tablevpp_list;
    TableLayout cp_tablenv_list;
    TableLayout cp_tablecp_list;

    TextView cp_totalCount;
    TextView cp_totalPrice;
    TextView warningLabel;
    TextView labelVPP;
    TextView noteVPPLabel;
    TextView noteTotalLabel;


    Button previewVPPBtn;
    Button navBC;
    Button navTK;

    // Data
    CapPhatDatabase capphatDB ;
    NhanVienDatabase nhanvienDB;
    VanPhongPhamDatabase vanphongphamDB;

    List<CapPhat> capphat_list;
    List<NhanVien> nhanvien_list;
    List<VanPhongPham> vanphongpham_list;
    List<PhongBan> phongban_list;

    int totalPrice = 0;
    int VPPCount = 0;

    // Dialog
    Dialog dialog;
        // Preview Image Layout
        TextView VPP_IP_maVPP;
        TextView VPP_IP_tenVPP;
        TextView VPP_IP_DVT;
        TextView VPP_IP_Gia;
        ImageView VPP_IP_Hinh;

    // Focus
    TableRow focusRow;
    TextView focusSP;
    TextView focusDate;
    TextView focusMaVPP;
    TextView focusMaNV;
    TextView focusSL;
    String dataMaPBSpinner;

    // Other
    float scale;
    int indexofRow = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capphatvpp_layout);
        scale = this.getResources().getDisplayMetrics().density;
        Rows.scale = scale;
        Rows.tvtemplate = R.layout.tvtemplate;

        setControl();
        loadDatabase();
        setEvent();
        setNavigation();

    }

    // --------------- MAIN HELPER -----------------------------------------------------------------
    public void setControl() {
//        Log.d("process", "setControl");
        backBtn = findViewById(R.id.CP_backBtn);

        PBSpinner = findViewById(R.id.CP_PBSpinner);

        cp_tablesall_container = findViewById( R.id.CP_tablesAll_container );
        cp_tablesindex_container = findViewById( R.id.CP_tablesIndex_container );
        cp_tablevpp_list = findViewById(R.id.CP_tableVPP);
        cp_tablenv_list = findViewById(R.id.CP_tableNV);
        cp_tablecp_list = findViewById(R.id.CP_tableCP);

        warningLabel = findViewById(R.id.CP_warningLabel);
        labelVPP = findViewById(R.id.CP_labelVPP);

        previewVPPBtn = findViewById(R.id.CP_previewVPPBtn);
        navBC = findViewById(R.id.CP_navbar_baocao);
        navTK = findViewById(R.id.CP_navbar_thongke);
    }

    public void loadDatabase(){
        //   Log.d("process", "loadDatabase");
        // 1.  Load Spinner ra tr?????c
        capphatDB = new CapPhatDatabase(CapphatVPPLayout.this);
        capphat_list = capphatDB.select();
        for( int i = 0; i < capphat_list.size(); i++){
            CapPhat cp = capphat_list.get(i);
            TableRow tr = createRow(CapphatVPPLayout.this,cp);
            tr.setId( i+1 );
            cp_tablecp_list.addView( tr );
//            Log.d("data",cp.toString()+"");
        }
        nhanvienDB = new NhanVienDatabase( CapphatVPPLayout.this );
        nhanvien_list = nhanvienDB.select();
        for( int i = 0; i < nhanvien_list.size(); i++){
            NhanVien nv = nhanvien_list.get(i);
            TableRow tr = createRow(CapphatVPPLayout.this,nv);
            tr.setId( i+1 );
            cp_tablenv_list.addView( tr );
//            Log.d("data",nv.toString()+"");
        }
        vanphongphamDB = new VanPhongPhamDatabase( CapphatVPPLayout.this );
        vanphongpham_list = vanphongphamDB.select();
        PBSpinner.setAdapter( loadPBSpinner() );

    }

    public void setEvent(){
        labelVPP.setVisibility(View.INVISIBLE);
        previewVPPBtn.setVisibility( View.INVISIBLE );
        // 1. Set Event cho Spinner
        setEventPBSpinner();
        // 2. Set Event Table Rows cho Table _CP v?? Table _NV simple
        for (int i = 0; i < cp_tablecp_list.getChildCount(); i++) {
            setEventTableRows((TableRow) cp_tablecp_list.getChildAt(i), cp_tablecp_list);
        }
//        for (int i = 0; i < cp_tablenv_list.getChildCount(); i++) {
//              setEventTableRows((TableRow) cp_tablenv_list.getChildAt(i), cp_tablenv_list);
//        }

    }

    public void setNavigation(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if( selectedPB != null )
                 intent = new Intent(CapphatVPPLayout.this, BaocaoVPPLayout.class);
                else
                 intent = new Intent(CapphatVPPLayout.this, BaocaoVPPAllLayout.class);
                if( totalPrice != 0 ) totalMoney = totalPrice;  // with selectedPB
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }
        });
        navTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CapphatVPPLayout.this, ThongkeLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );
            }
        });
    }

    public void transferLayout( String maPB ){
        if( maPB.trim().equalsIgnoreCase("")) return;
        // 1. maPB l?? all th?? chuy???n sang layout maPB
        switch (maPB){
            case "All" : {
                warningLabel.setText("Khi ch???n ph??ng ban c??? th???, c???u tr??c b???ng s??? kh??c");
                // All : show
                cp_tablesall_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT )
                );
                // Index : hide
                cp_tablesindex_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0 )
                );
                selectedPB = null;
                totalMoney = 0;
            };
                break;
            default: {
                warningLabel.setText("Khi ch???n t???t c??? ph??ng ban, c???u tr??c b???ng s??? kh??c");
                // All : hide
                cp_tablesall_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0 )
                );
                // Index : show
                cp_tablesindex_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT )
                );
                noteVPPLabel = cp_tablesindex_container.findViewById(R.id.CP_noteVppLabel);
                noteVPPLabel.setVisibility(View.INVISIBLE);
                createCPLayout_fromPB( maPB );
            }; break;
        }
    }

    public void createCPLayout_fromPB( String maPB ) {
        if( maPB.trim().equalsIgnoreCase("All") ) return;
        // Init Variables and Control
        Rows rowGenarator = new Rows(this );
        TableLayout cp_table1 = cp_tablesindex_container.findViewById(R.id.CP_tableVPP);
                    cp_table1.removeViews(1, cp_table1.getChildCount() -1);
                    int[] sizeOfCell = {85,180,50,80};
                    boolean[] isPaddingZero = {false, true, true, true};
        TableLayout cp_table2 = cp_tablesindex_container.findViewById(R.id.CP_tableNV2);
                    cp_table2.removeViews(1,cp_table2.getChildCount()-1);
                    int[] sizeOfCell2 = {90, 240, 100};
                    boolean[] isPaddingZero2 = {false,false,false};
        cp_totalCount = cp_tablesindex_container.findViewById(R.id.CP_totalCount);
        cp_totalPrice = cp_tablesindex_container.findViewById(R.id.CP_totalPrice);

        noteTotalLabel = cp_tablesindex_container.findViewById(R.id.CP_noteTotalLabel);
        for( PhongBan pb : phongban_list ){
            if( maPB.equalsIgnoreCase(pb.getMapb().trim()) )
            {
                selectedPB = pb;
                noteTotalLabel.setText("T???ng Chi ph?? trong "+pb.getTenpb()+" ???????c c???p :");
                break;
            }
        }
        totalPrice = 0;
        // Create List<TableRow> for TableList
        // TABLE CP INDEX 01 ----------------------------------------------------------------------------------------
        rowGenarator.setData( rowGenarator.enhanceRowData( capphatDB.select_listVPP_withPB(maPB), 4 ) );
            rowGenarator.setSizeOfCell(sizeOfCell);
            rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();
        if( rows == null) {
            cp_totalCount.setText("0");
            cp_totalPrice.setText("0");
            return;
        }
            for( TableRow row : rows) {
                cp_table1.addView(row);
                TextView totalpriceofVPPView = (TextView) row.getChildAt( row.getChildCount() -1);
                int totalpriceofVPP = Integer.parseInt(totalpriceofVPPView.getText().toString().trim());
                totalPrice += totalpriceofVPP;
            }
            rowGenarator.setSizeOfCell(sizeOfCell2);
            rowGenarator.setIsCellPaddingZero(isPaddingZero2);

        for( int i =1; i< cp_table1.getChildCount(); i++) {
            TableRow row = (TableRow) cp_table1.getChildAt(i);

            // T??? th???ng VPP ???????c b???m gen ra th???ng nh??n vi??n ???? m?????n n??
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // TABLE CP INDEX 02 ----------------------------------------------------------------------------------------
                    // Set text for noteVPPLabel -------------------------------------------------------------------------
                    TextView tenVPPView = (TextView) row.getChildAt( 1);
                    noteVPPLabel.setVisibility(View.VISIBLE);
                    noteVPPLabel.setText(tenVPPView.getText().toString().trim() + " ???????c c???p cho c??c nh??n vi??n d?????i ????y");
                    // ----------------------------------------------------------------------------------------------------
                    TextView maVPPView = (TextView) row.getChildAt(0);
                    for( TableRow row : rows) {
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    row.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                    rowGenarator.setData( rowGenarator.enhanceRowData(
                            capphatDB.select_listNV_withVPP_andPB(
                                    maPB,
                                    maVPPView.getText().toString().trim()
                            ), 3 ) );
                    cp_table2.removeViews(1,cp_table2.getChildCount()-1);
                    List<TableRow> rows2 = rowGenarator.generateArrayofRows();
                        for( TableRow row2 : rows2) {
                            cp_table2.addView(row2);
                        }
                }
            });
        }
        // CP_totalCount : T???ng s??? c??c VPP ???????c c???p
        cp_totalCount.setText( (cp_table1.getChildCount() -1) + "" );
        // CP_totalPrice : T???ng s??? ti???n VPP  = s??? l?????ng lo???i VPP m?? NV m?????n * s??? ti???n c???a m???i lo???i VPP
        cp_totalPrice.setText(MoneyFormat(totalPrice) );

    }
    public String MoneyFormat( int money ){
        if( money == 0) return "0 ??";
        int temp_money = money;
        String moneyFormat = "";
        if( money < 1000) return String.valueOf(money) +" ??";
        else {
            int count = 0;
            while (temp_money != 0) {
                moneyFormat += (temp_money % 10) + "";
                if ((count + 1) % 3 == 0 && temp_money > 10) moneyFormat += ".";
                count++;
                temp_money /= 10;
            }
        }
        return new StringBuilder(moneyFormat).reverse().toString() +" ??";
    }
    public ArrayAdapter<String> loadPBSpinner(){
        // 1. T???o list Phong ban // 2. ????? Phong_ban.getTenPB() ra 1 List // 3. setAdapter cho c??i list getTenPB() ????
        phongban_list = new PhongBanDatabase(CapphatVPPLayout.this).select();
        ArrayList<String> phongbanNames_list = new ArrayList<>();
        phongbanNames_list.add("T???t c??? ph??ng ban");
        // Ph???c v??? cho vi???c x??? ra Option cho Spinner
        for ( PhongBan pb : phongban_list){
            phongbanNames_list.add(pb.getTenpb());
//            Log.d("data", pb.getTenpb());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phongbanNames_list);
        return adapter;
    }

    public void setEventPBSpinner(){
        PBSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) dataMaPBSpinner = "All";
                else {
                    // 1.
                    dataMaPBSpinner = phongban_list.get(position - 1).getMapb();

                }
                transferLayout( dataMaPBSpinner );
//                Toast.makeText( CapphatVPPLayout.this, dataMaPBSpinner+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dataMaPBSpinner = "All";
            }
        });
    }

    // To set all rows to normal state, set focusRowid = -1
    public void setNormalBGTableRows(TableLayout list) {
        // 0: l?? th???ng example ???? INVISIBLE
        // N??n b???t ?????u t??? 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
//             Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
//        Toast.makeText(CapphatVPPLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
    }

    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusSP = (TextView) focusRow.getChildAt(0);
                String sp = focusSP.getText().toString().trim();
                focusDate = (TextView) focusRow.getChildAt(1);
                focusMaVPP = (TextView) focusRow.getChildAt(2);
                String mvpp = focusMaVPP.getText().toString().trim();
                focusMaNV = (TextView) focusRow.getChildAt(3);
                focusSL = (TextView) focusRow.getChildAt(4);
                setNormalBGTableRows(list);
                setEventTableRowsHelper( cp_tablenv_list );
                setEventDisplayVPP( focusMaVPP.getText().toString().trim() );
                // Testing to get id of focusable row
                //  Toast.makeText( PhongbanLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    public int findMaNVinTableNV( TableLayout list ){
        TableRow tr = null;
        TextView maNV = null;
        if( focusMaNV == null) return -1;
        Log.d("focus",focusMaNV.getText()+"");
        for( int i = 1; i < list.getChildCount(); i++){
            tr = (TableRow) list.getChildAt(i);
            maNV = (TextView) tr.getChildAt(0);
            if( maNV.getText().toString().trim().equalsIgnoreCase(focusMaNV.getText().toString().trim()+""))
                return i;
        }
        return -1;
    }

    public VanPhongPham findVPPinListVPP ( String maVPP ){
        for( VanPhongPham vpp : vanphongpham_list){
            if( vpp.getMaVpp().trim().equalsIgnoreCase( maVPP ))
                return vpp;
        }
        return null;
    }

    // H??m n??y gi??p h??m tr??n b???ng c??ch d???n t???i nh???ng d??? li???u c?? th??? c??? th??? h??a d??? li???u c???a h??m tr??n
    public void setEventTableRowsHelper( TableLayout sublist) {
        // Ki???m tra focus MaNv
        if( focusMaNV == null || focusMaNV.getText().toString().trim().equalsIgnoreCase("")
                || sublist.getChildCount() == 0 )
        {
            Toast.makeText(CapphatVPPLayout.this, "Sorry can't help with no input data",Toast.LENGTH_LONG);
            return ;
        }

        // Rect l?? 1 rect t??ng h??nh
        int index = findMaNVinTableNV(sublist);
        TableRow tr = (TableRow) sublist.getChildAt( index );
        Log.d("focus",index+"");
        Rect rc = new Rect(0, 0, tr.getWidth(), tr.getHeight());
        // Khi g???i t???i th???ng TableRow s??? v??? 1 Rectangle t??ng h??nh ??? th???ng TableRow ??ang ch??? ?????nh
        tr.getDrawingRect( rc );
        tr.requestRectangleOnScreen( rc );
        tr.setBackgroundColor(getResources().getColor(R.color.selectedColor));
        // Reset background white for others
        for (int i = 1; i < cp_tablenv_list.getChildCount(); i++) {
            TableRow row = (TableRow) cp_tablenv_list.getChildAt((int) i);
            if (index != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public void setDataImageView(ImageView imageView, byte[] imageBytes){
        if (imageBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setEventDisplayVPP( String maVPP ){
        // 1. G???i VPP _ Database ????? tr??? v??? List th??ng tin // 2. D???a tr??n list ???? ????? d?? maVPP sau ???? get 1 h??ng trong ????
        labelVPP.setVisibility(View.VISIBLE);
            VanPhongPham vpp = findVPPinListVPP( maVPP );
            if(vpp == null) return;
            String label = vpp.getMaVpp() + ":    " + vpp.getTenVpp();
            labelVPP.setText(label);
        // 1. C?? VanPhongPham r???i th?? set on click // 2. G???i Dialog ????? xem
        previewVPPBtn.setVisibility(View.VISIBLE);
        previewVPPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Image from Database is handled to load here
                createDialog(R.layout.popup_vpp_previewimage);
                // Custom set Control
                VPP_IP_maVPP = dialog.findViewById(R.id.VPP_IP_maVPP);
                VPP_IP_tenVPP = dialog.findViewById(R.id.VPP_IP_tenVPP);
                VPP_IP_DVT = dialog.findViewById(R.id.VPP_IP_DVT);
                VPP_IP_Gia = dialog.findViewById(R.id.VPP_IP_Gia);
                VPP_IP_Hinh = dialog.findViewById(R.id.VPP_IP_Hinh);
                // Load Data
                setDataImageView( VPP_IP_Hinh, vpp.getHinh() );
                VPP_IP_maVPP.setText( vpp.getMaVpp().toString().trim());
                VPP_IP_tenVPP.setText( vpp.getTenVpp().toString().trim());
                VPP_IP_DVT.setText( vpp.getDvt().toString().trim());
                VPP_IP_Gia.setText( vpp.getGiaNhap().toString().trim());
            }
        });

    }


    // DIALOG HELPER ----------------------------------------------------------------------------
    public void createDialog(int layout) {
        dialog = new Dialog(CapphatVPPLayout.this);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setControlDialog( ){
        // 1 Form c???a CP
    }
    public void setEventDialog( ){
        // Them/Xoa/Sua CP
    }

    // LAYOUT 01 -----------------------------------------------
    // V??n ph??ng ph???m khi init th?? select theo th???ng CP, sau ???? focus v??o th???ng ?????u ti??n c???a VPP
    public void setEventTableVPP(){

    }
    // khi 1 h??ng v??n ph??ng ph???m ???????c focus th?? m???i c?? nh??n vi??n
    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
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

    // Table 3
    // <!-- 80 / 150 / 60 / 60 / 60 -->
    public TableRow createRow(Context context, CapPhat cp) {
    TableRow tr = new TableRow(context);

    //  So phieu
    TextView soPhieu = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maVpp ?????t t???i max width th?? n?? s??? t??ng height cho b??n maNV lu??n
    // L??u ??!! : khi ?????t LayoutParams th?? ph???i theo th???ng c??? n???i v?? ph???i c?? weight
    soPhieu.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    soPhieu.setMaxWidth(DPtoPix(80));
    soPhieu.setText(cp.getSoPhieu());

    //   Ngay cap
    TextView ngayCap = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maNV ?????t t???i max width th?? n?? s??? t??ng height cho b??n maVpp lu??n
    ngayCap.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    ngayCap.setText(formatDate(cp.getNgayCap(), false));
    ngayCap.setMaxWidth(DPtoPix(150));

    //  VPP
    TextView maVpp = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maVpp ?????t t???i max width th?? n?? s??? t??ng height cho b??n maNV lu??n
    // L??u ??!! : khi ?????t LayoutParams th?? ph???i theo th???ng c??? n???i v?? ph???i c?? weight
    maVpp.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maVpp.setMaxWidth(DPtoPix(60));
    maVpp.setText(cp.getMaVpp());

    //   NV
    TextView maNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maNV ?????t t???i max width th?? n?? s??? t??ng height cho b??n maVpp lu??n
    maNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maNV.setText(cp.getMaNv());
    maNV.setMaxWidth(DPtoPix(60));

    //   SL
    TextView soLuong = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maNV ?????t t???i max width th?? n?? s??? t??ng height cho b??n maVpp lu??n
    soLuong.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    soLuong.setText(cp.getSl() + "");
    soLuong.setMaxWidth(DPtoPix(60));

    tr.setBackgroundColor(getResources().getColor(R.color.white));
    // Add 2 th??? v??o row
    tr.addView(soPhieu);
    tr.addView(ngayCap);
    tr.addView(maVpp);
    tr.addView(maNV);
    tr.addView(soLuong);

    return tr;
}

    // Table 4
    // <!-- 80 / 300 -->
    public TableRow createRow(Context context, NhanVien nv) {
    TableRow tr = new TableRow(context);
    // Id

    //   Ma PB
    TextView maNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? maNV ?????t t???i max width th?? n?? s??? t??ng height cho b??n tenNV lu??n
    // L??u ??!! : khi ?????t LayoutParams th?? ph???i theo th???ng c??? n???i v?? ph???i c?? weight
    maNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maNV.setMaxWidth(DPtoPix(80));
    maNV.setText(nv.getMaNv());

    //   Ten PB
    TextView tenNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // C???n c??i n??y ????? khi m?? tenNV ?????t t???i max width th?? n?? s??? t??ng height cho b??n maNV lu??n
    tenNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    tenNV.setText(nv.getHoTen());
    tenNV.setMaxWidth(DPtoPix(300));

    tr.setBackgroundColor(getResources().getColor(R.color.white));
    // Add 2 th??? v??o row
    tr.addView(maNV);
    tr.addView(tenNV);

    return tr;
}

}

// Quy tr??nh t??nh s??? ti???n t???ng
//            for( TableRow row : rows) {
//                // L???y s??? ti???n m???i lo???i : VD 60.000
//                TextView maVPPView = (TextView) row.getChildAt(0);
//                String maVPP = maVPPView.getText().toString().trim();
//                TextView pricetypeView = (TextView) row.getChildAt( row.getChildCount() -1 );
//                int priceofType = Integer.parseInt( pricetypeView.getText().toString().trim() );
//                // Gen l???i c??c TableRow
//                rowGenarator.setData( rowGenarator.enhanceRowData(
//                        capphatDB.select_listNV_withVPP_andPB(
//                                maPB,
//                                maVPP
//                        ), 3 ) );
//                List<TableRow> rows2 = rowGenarator.generateArrayofRows();
//                for( TableRow row2 : rows2 ) {
//                    // L???y s??? l?????ng m?????n m???i lo???i
//                    TextView numberofTypeView = (TextView) row2.getChildAt( row2.getChildCount() -1 );
//                    int numberofType = Integer.parseInt( numberofTypeView.getText().toString().trim() );
//                    // T???ng += gi?? * s??? l?????ng m?????n
//                    totalPrice += priceofType*numberofType;
//                }
//            }