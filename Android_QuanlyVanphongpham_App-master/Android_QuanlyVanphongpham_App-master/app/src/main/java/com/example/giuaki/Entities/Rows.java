package com.example.giuaki.Entities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Đầu vào là context, ArrayList<String>, số ô trong 1 hàng
public class Rows {
    public Context context = null;
    public List<String> data = null;
    public int numberOfCells = 0;
    public int[] sizeOfCell = null;
    public boolean[] isCellPaddingZero;
    // -------------------------------
    private List<TableRow> rows = null;
    // BẮT BUỘC PHẢI SET -------------------------------
    public static float scale ;
    public static int tvtemplate ;
    // 1. Mình truyền vào cho Rows ( this, dữ liệu chuẩn đã qua xử lí ArrayList<String>, kích cỡ của từng Cell trong Rows mong muốn, hỏi Cell đó có cần padding hay không ? )
    public Rows(Context context, List<String> data, int numberOfCells, int[] sizeOfCell, boolean[] isCellPaddingZero) {
        this.context = context;
        this.data = data;
        this.numberOfCells = numberOfCells;
        this.sizeOfCell = sizeOfCell;
        this.isCellPaddingZero = isCellPaddingZero;
        this.rows = new ArrayList<>();
    }

    public Rows( Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public int getNumberOfCells() {
        return numberOfCells;
    }

    public void setNumberOfCells(int numberOfCells) {
        this.numberOfCells = numberOfCells;
    }

    public int[] getSizeOfCell() {
        return sizeOfCell;
    }

    public void setSizeOfCell(int[] sizeOfCell) {
        this.sizeOfCell = sizeOfCell;
    }

    public boolean[] getIsCellPaddingZero() {
        return isCellPaddingZero;
    }

    public void setIsCellPaddingZero(boolean[] isCellPaddingZero) {
        this.isCellPaddingZero = isCellPaddingZero;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }

    public static float getScale() {
        return scale;
    }

    public static void setScale(float scale) {
        Rows.scale = scale;
    }

    public static int getTvtemplate() {
        return tvtemplate;
    }

    public static void setTvtemplate(int tvtemplate) {
        Rows.tvtemplate = tvtemplate;
    }

    // 2. Hàm giúp xử lí dữ liệu thô => dữ liệu theo khuôn Row có số cell nhất định
    /* VD:
           NV1
           Nguyễn Thành Nam
           PB01

           => NV1,Nguyễn Thành Nam,PB01.
      -- Name of Function :  enhanceRowData( List<String> raw );
    */
    public List<String> enhanceRowData( List<String> raw , int numberOfCells) {
        this.data = new ArrayList<>();
        this.numberOfCells = numberOfCells;
        String row = "";
        for (int i = 0; i < raw.size(); i++) {
            row += raw.get(i).trim() + ",";
            if ( (i + 1) % numberOfCells == 0) {
                row = row.substring(0, row.length() - 1);
                Log.d("Rows_toString()", row + "\n");
                this.data.add(row);
                row = "";
            }
        }
        return this.data;
    }

    // Đổi từ DPs sang Pixels
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    // 3. Đổ dữ liệu đã xử lý thành những Row theo những lựa chọn cho trước
    public List<TableRow> generateArrayofRows() {
        TableRow tr = new TableRow(context);
        TextView tv = null;
        this.rows = new ArrayList<>();
        tr.setBackgroundColor( Color.parseColor( "#FFFFFF" ) );
        String rowCells[] = null;
        if( this.data == null ) return null;
        if( this.data.size() == 0) return null;
        for( String row : this.data){
            // VD: "NV1,Nguyễn Thành Nam,PB01,...,34" -> ["NV1","Nguyễn Thành Nam","PB01",...,"34"]
            rowCells = row.split(",");
            // VD: ["NV1","Nguyễn Thành Nam","PB01",...,34"] -> TextView()
            for( int i = 0; i < rowCells.length; i++){
                tv = createTextView( rowCells[ i ], sizeOfCell[ i ] , isCellPaddingZero[ i ] );
                tr.addView(tv);
            }
            // VD: (TableRow) tr => [ tr, tr, tr, ... ]
            this.rows.add(tr);
            tr = new TableRow(context);
        }
        return this.rows;
    }

    // 4. Tạo TextView theo String và MaxWidth và isPadding
    public TextView createTextView( String text, int maxwidth, boolean isPaddingZero ){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        TextView textView = (TextView) inflater.inflate( tvtemplate , null);
        textView.setText(text);
        textView.setMaxWidth( this.DPtoPix( maxwidth ) );
        textView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        if(isPaddingZero) textView.setPadding(0,0,0,0);
        return textView;
    }

    // 5. Test Data
    public void testData(){
        String row = "";
        for( int i = 0 ; i < data.size(); i++){
                Log.d("data", data.get(i) +"\n");
        }
    }

}
