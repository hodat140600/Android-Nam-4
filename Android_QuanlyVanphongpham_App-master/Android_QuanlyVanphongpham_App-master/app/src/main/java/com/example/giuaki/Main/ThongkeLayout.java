package com.example.giuaki.Main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.giuaki.Chart.ThongkeChart;
import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Entities.Rows;
import com.example.giuaki.R;

import java.util.List;

public class ThongkeLayout extends AppCompatActivity {
    // Main Layouts
    LinearLayout wrapper_tableCau1,
                 wrapper_tableCau2,
                 wrapper_tableCau3,
                 wrapper_tableCau4,
                 wrapper_tableDefault;

    TableLayout  tableCau1,
                 tableCau2,
                 tableCau3,
                 tableCau4,
                 tableDefault;

    Button backBtn,
           chartBtn;
    Button   btn_cau1,
             btn_cau2,
             btn_cau3,
             btn_cau4;
    // Data
    int index = 0;
    float scale;
    CapPhatDatabase capphatDB = new CapPhatDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongke_layout);
        scale = this.getResources().getDisplayMetrics().density;
        Rows.scale = scale;
        Rows.tvtemplate = R.layout.tvtemplate;
        setControl();
        setAnim();
        handleEvent(index);
        setEvent();

    }

    private void setControl() {
        // LinearLayouts
        wrapper_tableCau1 = findViewById(R.id.TK_wrapper_tableCau1);
        wrapper_tableCau2 = findViewById(R.id.TK_wrapper_tableCau2);
        wrapper_tableCau3 = findViewById(R.id.TK_wrapper_tableCau3);
        wrapper_tableCau4 = findViewById(R.id.TK_wrapper_tableCau4);
        wrapper_tableDefault = findViewById(R.id.TK_wrapper_tableDefault);
        // TableLayouts
        tableCau1 = findViewById(R.id.TK_tableCau1);
        tableCau2 = findViewById(R.id.TK_tableCau2);
        tableCau3 = findViewById(R.id.TK_tableCau3);
        tableCau4 = findViewById(R.id.TK_tableCau4);
        tableDefault = findViewById(R.id.TK_tableDefault);
        // Buttons
        backBtn = findViewById(R.id.TK_backBtn);
        chartBtn = findViewById(R.id.TK_chartBtn);
        btn_cau1 = findViewById(R.id.TK_btn_cau1);
        btn_cau2 = findViewById(R.id.TK_btn_cau2);
        btn_cau3 = findViewById(R.id.TK_btn_cau3);
        btn_cau4 = findViewById(R.id.TK_btn_cau4);
        btn_cau1.setVisibility(View.INVISIBLE);
        btn_cau2.setVisibility(View.INVISIBLE);
        btn_cau3.setVisibility(View.INVISIBLE);
        btn_cau4.setVisibility(View.INVISIBLE);
    }

    private void setEvent() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        chartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThongkeLayout.this, ThongkeChart.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }
        });
        setButtonClick( btn_cau1 );
        setButtonClick( btn_cau2 );
        setButtonClick( btn_cau3 );
        setButtonClick( btn_cau4 );

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void normalAllButton() {
        btn_cau1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor( R.color.disable_color )));
        btn_cau2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor( R.color.disable_color )));
        btn_cau3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor( R.color.disable_color )));
        btn_cau4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor( R.color.disable_color )));

        btn_cau1.setTextColor(getResources().getColor(R.color.black));
        btn_cau2.setTextColor(getResources().getColor(R.color.black));
        btn_cau3.setTextColor(getResources().getColor(R.color.black));
        btn_cau4.setTextColor(getResources().getColor(R.color.black));
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void activeButton(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor( R.color.thongke_activeBtn )));
        btn.setTextColor(getResources().getColor(R.color.white));
    }

    private void setButtonClick(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                normalAllButton();
                activeButton(btn);
                index = Integer.parseInt(btn.getTag()+"");
                Toast.makeText(ThongkeLayout.this, index+"",Toast.LENGTH_LONG).show();
                handleEvent(index);
            }
        });
    }

    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    private void handleEvent(int index) {
        hideAllTableWrappers();
        switch (index){
            case 0 :
                showTableWrapper(wrapper_tableCau1);
                loadDataRows( tableCau1 );
                break;
            case 1 :
                showTableWrapper(wrapper_tableCau2);
                loadDataRows( tableCau2 );
                break;
            case 2 :
                showTableWrapper(wrapper_tableCau3);
                loadDataRows( tableCau3 );
                break;
            case 3 :
                showTableWrapper(wrapper_tableCau4);
                loadDataRows( tableCau4 );
              break;
            default:
                showTableWrapper(wrapper_tableDefault);
                loadDataRows( tableDefault );
                break;
        }
    }

    private void showTableWrapper( LinearLayout layout ) {
        layout.setVisibility(View.VISIBLE);
        layout.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DPtoPix(250) ));
    }

    private void loadDataRows(TableLayout table) {
        Rows rowGenarator = new Rows(this);
        List<TableRow> rows = null;
        table.removeViews(1, table.getChildCount() -1);
        switch (index){
            case 0:{
                //  <!-- 70 p0 / 160 / 60 / 60 / <= 46 -->
                int[] sizeOfCell = {70, 160, 60, 60, 46};
                boolean[] isPaddingZero = {true, false, false, false, false};
                rowGenarator.setData( rowGenarator.enhanceRowData( capphatDB.thongKeCau2a() , 5 ) );
                rowGenarator.setSizeOfCell(sizeOfCell);
                rowGenarator.setIsCellPaddingZero(isPaddingZero);
            };
                break;
            case 1:{
//                <!-- 130 / 160 / <= 122  -->
                int[] sizeOfCell = {130, 160, 122};
                boolean[] isPaddingZero = {false, false, false};
                rowGenarator.setData( rowGenarator.enhanceRowData( capphatDB.thongKeCau2b() , 3 ) );
                rowGenarator.setSizeOfCell(sizeOfCell);
                rowGenarator.setIsCellPaddingZero(isPaddingZero);
            };
                break;
            case 2:{
//               <!-- 70 p0 / 150 p0 / 90 p0 / <= 80 p0 -->
                int[] sizeOfCell = {70, 150, 90, 80};
                boolean[] isPaddingZero = {true, true, true, true};
                rowGenarator.setData( rowGenarator.enhanceRowData(  capphatDB.thongKeCau2c(), 4 ) );
                rowGenarator.setSizeOfCell(sizeOfCell);
                rowGenarator.setIsCellPaddingZero(isPaddingZero);
            };
                break;
            case 3:{
//              <!-- 90 / 160 / 80 -->
                int[] sizeOfCell = {90, 160, 80};
                boolean[] isPaddingZero = {false, false, false};
                rowGenarator.setData( rowGenarator.enhanceRowData( capphatDB.thongKeCau2d() , 3 ) );
                rowGenarator.setSizeOfCell(sizeOfCell);
                rowGenarator.setIsCellPaddingZero(isPaddingZero);
            };
                break;
            default:
//              <!-- 80 p0 / 80 / 80 / 80 / <= 80 -->
                int[] sizeOfCell = {80, 80, 80, 80, 80};
                boolean[] isPaddingZero = {true, false, false, false, false};
//                rowGenarator.setData( rowGenarator.enhanceRowData(  , 5 ) );
                rowGenarator.setSizeOfCell(sizeOfCell);
                rowGenarator.setIsCellPaddingZero(isPaddingZero);
                break;
        }
        rows = rowGenarator.generateArrayofRows();
        if( rows == null) return;
        Log.d("data","here");
        for( TableRow row : rows){
            table.addView(row);
        }

    }

    private void hideAllTableWrappers() {
        wrapper_tableCau1.setVisibility(View.INVISIBLE);
        wrapper_tableCau1.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0 ));
        wrapper_tableCau2.setVisibility(View.INVISIBLE);
        wrapper_tableCau2.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0 ));
        wrapper_tableCau3.setVisibility(View.INVISIBLE);
        wrapper_tableCau3.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0 ));
        wrapper_tableCau4.setVisibility(View.INVISIBLE);
        wrapper_tableCau4.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0 ));
        wrapper_tableDefault.setVisibility(View.INVISIBLE);
        wrapper_tableDefault.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0 ));
    }




    private void setAnim() {
        Animation animationLeft1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left);
        Animation animationLeft2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left);
        Animation animationLeft3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left);
        Animation animationLeft4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                btn_cau1.setVisibility(View.VISIBLE);
                btn_cau1.startAnimation(animationLeft1);
            }
        }, 350);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                btn_cau2.setVisibility(View.VISIBLE);
                btn_cau2.startAnimation(animationLeft2);
            }
        }, 450);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                btn_cau3.setVisibility(View.VISIBLE);
                btn_cau3.startAnimation(animationLeft3);
            }
        }, 550);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                btn_cau4.setVisibility(View.VISIBLE);
                btn_cau4.startAnimation(animationLeft4);
            }
        }, 650);


    }

}