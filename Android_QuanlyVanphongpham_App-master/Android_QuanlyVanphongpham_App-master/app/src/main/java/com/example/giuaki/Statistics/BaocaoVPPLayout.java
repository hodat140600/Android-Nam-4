package com.example.giuaki.Statistics;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Databases.NhanVienDatabase;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.Entities.Rows;
import com.example.giuaki.Main.ThongkeLayout;
import com.example.giuaki.R;
import com.example.giuaki.XinchoLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BaocaoVPPLayout extends AppCompatActivity {
    Button backBtn,
           printBtn;
    TableLayout table;
    TextView tenPBView,
           countNVView,
         countVPPView,
       totalMoneyView,
             dateView;
    PhongBan pb = CapphatVPPLayout.selectedPB;
    CapPhatDatabase capPhatDatabase;
    NhanVienDatabase nhanVienDatabase;
    float scale;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baocaovpp_layout);

        scale = this.getResources().getDisplayMetrics().density;
        Rows.scale = scale;
        Rows.tvtemplate = R.layout.tvtemplate;

        setControl();
        setEvent();
    }

    private void setControl() {
        backBtn = findViewById(R.id.BC_index_backBtn);

        table = findViewById(R.id.BC_index_table);

        tenPBView = findViewById(R.id.BC_index_tenPB);
        countNVView = findViewById(R.id.BC_index_countNV);
        countVPPView = findViewById(R.id.BC_index_countVPP);
        totalMoneyView = findViewById(R.id.BC_index_totalMoney);
        dateView = findViewById(R.id.BC_index_date);

        printBtn = findViewById(R.id.BC_index_printBtn);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setEvent() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaocaoVPPLayout.this, XinchoLayout.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity( intent );
            }
        });
        setPhongBanView();
        setTableLayout();
        setDateView();
        setTotal();
    }

    private void setTotal() {
        List<String> countVPP = capPhatDatabase.countVPPfromPB(pb);
        countVPPView.setText( countVPP.get(1).trim() );
        totalMoneyView.setText( MoneyFormat( CapphatVPPLayout.totalMoney ) );
    }

    public void setTableLayout(){
        //        <!-- 40 / 80 p0 / 50 / 90 p0 / 67 p0 / 63 p0 -->
        capPhatDatabase = new CapPhatDatabase(this);
        Rows rowGenarator = new Rows(this );
        int[] sizeOfCell = {40,80,50,90,67,63};
        boolean[] isPaddingZero = {false, false, true, true ,true, true};
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        rowGenarator.setData( rowGenarator.enhanceRowData( capPhatDatabase.BaocaoQuery( pb ), 6 ) );
        rowGenarator.setSizeOfCell(sizeOfCell);
        rowGenarator.setIsCellPaddingZero(isPaddingZero);
        List<TableRow> rows = rowGenarator.generateArrayofRows();
        for( TableRow row : rows ){
            table.addView(row);
        }
    }

    private void setPhongBanView() {
        if( pb != null ) {
            tenPBView.setText( pb.getTenpb());
            nhanVienDatabase = new NhanVienDatabase(this);
            List<String> list = nhanVienDatabase.CountNVfromPB( pb );
            countNVView.setText( list.get(1) );
        }
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