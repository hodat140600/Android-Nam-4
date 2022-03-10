package com.example.giuaki.Statistics;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Databases.NhanVienDatabase;
import com.example.giuaki.Databases.PhongBanDatabase;
import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.Entities.Rows;
import com.example.giuaki.R;
import com.example.giuaki.XinchoLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaocaoVPPAllLayout extends AppCompatActivity {
    Button backBtn,
           printBtn;

    Spinner PBSpinner,
            NVSpinner;

    TableLayout table;

    TextView dateView,
        totalMoneyView;

    // CapPhat
    CapPhatDatabase capPhatDatabase;

    // NhanVien
    NhanVienDatabase nhanVienDatabase;
    List<NhanVien> nhanvienList;
    ArrayList<String> nhanvienStringList;
    NhanVien selectedNhanVien;

    // PhongBan
    PhongBanDatabase phongBanDatabase;
    List<PhongBan> phongbanList;
    ArrayList<String> tenPhongBanList;
    PhongBan selectedPhongBan;

    // Data
    float scale;
    Rows rowGenerator = null;
    List<TableRow> rows = null;
    //<!-- 40 / 100 / 100 / 80 / 80 -->
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baocaovpp2_layout);
        scale = this.getResources().getDisplayMetrics().density;
        Rows.scale = scale;
        Rows.tvtemplate = R.layout.tvtemplate;
        rowGenerator = new Rows(this);
        setControl();
        loadDatabase();
        setEvent();

    }
    private void setControl() {
        backBtn = findViewById(R.id.BC_All_backBtn);
        printBtn = findViewById(R.id.BC_All_printBtn);

        PBSpinner = findViewById(R.id.BC_All_PBSpinner);
        NVSpinner = findViewById(R.id.BC_All_NVSpinner);

        table    = findViewById(R.id.BC_All_table);

        dateView = findViewById(R.id.BC_All_date);
        totalMoneyView = findViewById(R.id.BC_All_totalMoney);
    }

    private void loadDatabase() {
        nhanVienDatabase = new NhanVienDatabase(this);
        phongBanDatabase = new PhongBanDatabase(this);
        capPhatDatabase  = new CapPhatDatabase( this);
        phongbanList = phongBanDatabase.select();
        tenPhongBanList = new ArrayList<>();
        for( PhongBan pb : phongbanList){
            tenPhongBanList.add(pb.getTenpb().trim());
        }
        PBSpinner.setAdapter( loadSpinnerAdapter(tenPhongBanList) );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDateView(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String str = dtf.format(now);
        String[] date ;
        date = str.split("/");
        String msg = "TPHCM, ngày "+ date[2] +" tháng "+ date[1] +" năm "+ date[0];
        dateView.setText(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if( resultCode == RESULT_OK ) {
                Toast.makeText(BaocaoVPPAllLayout.this, "In báo cáo thành công", Toast.LENGTH_LONG).show();
                int result = data.getIntExtra("result",0);
            } else {
                Toast.makeText(BaocaoVPPAllLayout.this, "In báo cáo thất bại", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setEvent(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaocaoVPPAllLayout.this, XinchoLayout.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivityForResult( intent, 1 );
            }
        });
        //        PBSpinner
        PBSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPhongBan = phongbanList.get(position);
                nhanvienList = nhanVienDatabase.select(selectedPhongBan);
                NVtoStringArray( nhanvienList );
                setNVSpinnerEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPhongBan = phongbanList.get(0);
                nhanvienList = nhanVienDatabase.select(selectedPhongBan);
                NVtoStringArray( nhanvienList );
                setNVSpinnerEvent();

            }
        });

        setDateView();
        //        NVSpinner

    }
    public void setTable( NhanVien nv){
//        <!-- 40 / 100 / 100 / 80 / 80 -->
        table.removeViews(1, table.getChildCount()-1);
        int[] sizeOfCell = {40,100,110,80,100};
        boolean[] isPaddingZero = {false, false, true, false, true};
        rowGenerator.setSizeOfCell(sizeOfCell);
        rowGenerator.setIsCellPaddingZero(isPaddingZero);
        rowGenerator.setData( rowGenerator.enhanceRowData( capPhatDatabase.BaocaoQuery( nv ), 5 ) );
        rows = rowGenerator.generateArrayofRows();
        if( rows == null ) return;
        for( TableRow row : rows ){
            table.addView(row);
        }

    }

    public void setTotalMoneyView( ArrayList<TableRow> rows){
        if(rows == null || rows.size() == 0) {
            totalMoneyView.setText("0");
            return;
        }
        int tongtien = 0;
        TextView tienView = null;
        for( TableRow row : rows){
            tienView = (TextView) row.getChildAt(4);
            tongtien += Integer.parseInt( tienView.getText().toString().trim() );
        }
        if( tongtien <= 0 ) return;
        totalMoneyView.setText( MoneyFormat(tongtien) );
    }

    public void setNVSpinnerEvent(){
        NVSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNhanVien = nhanvienList.get(position);
                setTable(selectedNhanVien);
                setTotalMoneyView((ArrayList<TableRow>) rows);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedNhanVien = nhanvienList.get(0);
                setTable(selectedNhanVien);
                setTotalMoneyView((ArrayList<TableRow>) rows);
            }
        });
    }

    public void NVtoStringArray( List<NhanVien> list ){
        nhanvienStringList = new ArrayList<>();
        for( NhanVien nv : list){
            nhanvienStringList.add( nv.toSpinnerString() );
        }
        NVSpinner.setAdapter( loadSpinnerAdapter( nhanvienStringList) );
    }

    public ArrayAdapter<String> loadSpinnerAdapter(ArrayList<String> str) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, str);
        return adapter;
    }

    public String MoneyFormat( int money ){
        if( money == 0) return "0 đ";
        int temp_money = money;
        String moneyFormat = "";
        if( money < 1000) return String.valueOf(money) +" đ";
        else {
            int count = 0;
            while (temp_money != 0) {
                moneyFormat += (temp_money % 10) + "";
                if ((count + 1) % 3 == 0 && temp_money > 10) moneyFormat += ".";
                count++;
                temp_money /= 10;
            }
        }
        return new StringBuilder(moneyFormat).reverse().toString() +" đ";
    }



}