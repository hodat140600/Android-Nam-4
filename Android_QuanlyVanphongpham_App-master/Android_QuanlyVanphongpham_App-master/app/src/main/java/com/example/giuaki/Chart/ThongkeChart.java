package com.example.giuaki.Chart;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Databases.PhongBanDatabase;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ThongkeChart extends AppCompatActivity {
    CapPhatDatabase capPhatDatabase = new CapPhatDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongke_chart);
        List<String> datas = capPhatDatabase.countVPPfromPB();
        PieChart pieChart = findViewById(R.id.TK_chart);
        ArrayList<PieEntry> countVPPfromPB = new ArrayList<>();
        String tenPB = "";
        int chiso = 0;
        if( datas.size() == 0) return;
        for( int i =0 ; i < datas.size(); i++){
            if( i % 2 == 0 ){
                tenPB = datas.get(i);
            }else if( i % 2 != 0){
                if( tenPB.equalsIgnoreCase("")) break;
                chiso = Integer.parseInt(datas.get(i));
                countVPPfromPB.add(new PieEntry(chiso, tenPB));
                tenPB = "";
                chiso = 0;
            }
        }
        PieDataSet pieDataSet = new PieDataSet(countVPPfromPB, "Phòng");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("THỐNG KÊ SỐ LƯỢNG VPP ĐƯỢC CẤP CHO CÁC PHÒNG BAN");
        pieChart.animate();
    }
}