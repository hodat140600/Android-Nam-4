package com.example.giuaki.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giuaki.Databases.VanPhongPhamDatabase;
import com.example.giuaki.Entities.VanPhongPham;
import com.example.giuaki.R;
import com.example.giuaki.Statistics.CapphatVPPLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VanphongphamLayout extends AppCompatActivity {
    // Main Layout
    TableLayout vpp_table_list;

    List<VanPhongPham> vpplist;

    Button insertBtn;
    Button editBtn;
    Button delBtn;
    Button exitBtn;
    Button previewBtn; // <- Nút này để set Preview cái hàng đó, bấm vào là hiên Pop up chỉ để coi, ko chỉnh sửa
    // Preview Image Layout
        TextView VPP_IP_maVPP;
        TextView VPP_IP_tenVPP;
        TextView VPP_IP_DVT;
        TextView VPP_IP_Gia;
        ImageView VPP_IP_Hinh;
    // --------------

    // Navigation
    Button navPB;
    Button navNV;
    Button navVPP;
    Button navCP;

    EditText search;

    // Dialog Layout
    Dialog vppdialog;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    EditText inputMaVPP;
    EditText inputTenVPP;
    EditText inputDVT;
    EditText inputGia;
    ImageView inputHinh;

    TextView showMVPPError;
    TextView showTVPPError;
    TextView showDVTError;
    TextView showGiaError;

    TextView showResult;
    TextView showConfirm;
    TextView showLabel;

    // Database Controller
    VanPhongPhamDatabase vanphongphamDB;

    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusMaVPP;
    TextView focusTenVPP;
    TextView focusDVT;
    TextView focusGia;
    ArrayList<byte[]> image_list = new ArrayList<>();
    byte[] focusDataHinh = null;

    // Other
    float scale;

    // Key Code
    int IMAGE_FOLDER = 1000;
    int PERMISSION_GRANTED = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vanphongpham_layout);
        scale = this.getResources().getDisplayMetrics().density;
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
    }

    public void setControl(){
        vpp_table_list = findViewById(R.id.VPP_table_list);
        insertBtn = findViewById(R.id.VPP_insertBtn);
        editBtn = findViewById(R.id.VPP_editBtn);
        delBtn = findViewById(R.id.VPP_delBtn);
        exitBtn = findViewById(R.id.VPP_exitBtn);
        previewBtn = findViewById(R.id.VPP_previewBtn);

        navPB = findViewById(R.id.VPP_navbar_phongban);
        navNV = findViewById(R.id.VPP_navbar_nhanvien);
        navVPP = findViewById(R.id.VPP_navbar_VPP);
        navCP = findViewById(R.id.VPP_navbar_capphat);

        search = findViewById(R.id.VPP_searchEdit);

    }

    public void setCursorWindowImageSize( int B ){
        // Khai báo một field mới cho khả năng lưu hình độ phân giải lớn
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, B); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDatabase(){
        vanphongphamDB = new VanPhongPhamDatabase(this);
        vpplist = new ArrayList<>();
        TableRow tr = null;
        // Nếu không có dòng này thì nó sẽ báo lỗi Row quá to không thể nhét vào Cursor trong select
        setCursorWindowImageSize(100 * 1024 * 1024); // 100 MB max
        vpplist = vanphongphamDB.select();
        // Tag sẽ bắt đầu ở 1 vì phải cộng thêm thằng example đã có sẵn
        for (int i = 0; i < vpplist.size(); i++) {
            tr = createRow(this, vpplist.get(i));
            tr.setId((int) i + 1);
            vpp_table_list.addView(tr);
        }
    }

    public void setEvent(){
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        previewBtn.setVisibility(View.INVISIBLE);
        setEventTable(vpp_table_list);

    }

    public void setNavigation(){
        // navVPP onclick none
        // navPB
        navPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(VanphongphamLayout.this, PhongbanLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );

            }
        });
        // navNV
        navNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(VanphongphamLayout.this, NhanvienLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );

            }
        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VanphongphamLayout.this, CapphatVPPLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );
            }

        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setEventTable(TableLayout list) {
        // Log.d("count", list.getChildCount()+""); // số table rows + 1
        // Không cần thay đổi vì đây chỉ mới set Event
        // Do có thêm 1 thằng example để làm gốc, nên số row thì luôn luôn phải + 1
        // Có example thì khi thêm row thì nó sẽ theo khuôn
        for (int i = 0; i < list.getChildCount(); i++) {
            setEventTableRows((TableRow) list.getChildAt(i), list);
        }
        // Có thêm 1 thằng Preview để xem trước thông tin của hàng đang chỉ định ( focusRow )

        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Image from Database is handled to load here
                createDialog(R.layout.popup_vpp_previewimage);
                // Custom set Control
                VPP_IP_maVPP = vppdialog.findViewById(R.id.VPP_IP_maVPP);
                VPP_IP_tenVPP = vppdialog.findViewById(R.id.VPP_IP_tenVPP);
                VPP_IP_DVT = vppdialog.findViewById(R.id.VPP_IP_DVT);
                VPP_IP_Gia = vppdialog.findViewById(R.id.VPP_IP_Gia);
                VPP_IP_Hinh = vppdialog.findViewById(R.id.VPP_IP_Hinh);
                // Load Data
                setDataImageView( VPP_IP_Hinh, image_list.get(indexofRow -1));
                VPP_IP_maVPP.setText(focusMaVPP.getText().toString().trim());
                VPP_IP_tenVPP.setText(focusTenVPP.getText().toString().trim());
                VPP_IP_DVT.setText(focusDVT.getText().toString().trim());
                VPP_IP_Gia.setText(focusGia.getText().toString().trim());
            }
        });

        // Khi tạo, dùng n làm tag để thêm row
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // H bấm 1 cái là hiện ra cái pop up
                createDialog(R.layout.popup_vpp);
                // Control
                setControlDialog();
                // Event
                setEventDialog(v);
                setEventImagePicker();
            }
        });
        // Khi edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    // Test
                    // Toast.makeText( VanphongphamLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
                    createDialog(R.layout.popup_vpp);
                    // Control
                    setControlDialog();
                    showLabel.setText("Sửa văn phòng phẩm");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);
                    setEventImagePicker();
                    setDataImageView(inputHinh, focusDataHinh);
                    inputMaVPP.setText(focusMaVPP.getText());
                    inputTenVPP.setText(focusTenVPP.getText());
                    inputDVT.setText(focusDVT.getText());
                    inputGia.setText(focusGia.getText());
                    inputMaVPP.setEnabled(false);
                }
            }
        });
        // Khi delete, có 3 TH : nằm ở cuối hoặc nằm ở đầu hoặc chính giữa
        // Nằm ở cuối thì chỉ cần xóa cuối
        // Còn lại thì sau khi xóa xong thì phải cập nhật lại tag cho toàn bộ col
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    // Test
                    // Toast.makeText( VanphongphamLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
                    createDialog(R.layout.popup_vpp);
                    // Control
                    setControlDialog();
                    showLabel.setText("Xóa văn phòng phẩm");
                    showConfirm.setText("Bạn có muốn sửa hàng này không?");
                    // Event
                    setEventDialog(v);
                    setDataImageView(inputHinh,focusDataHinh);
                    inputMaVPP.setText(focusMaVPP.getText());
                    inputTenVPP.setText(focusTenVPP.getText());
                    inputDVT.setText(focusDVT.getText());
                    inputGia.setText(focusGia.getText());

                    inputMaVPP.setEnabled(false);
                    inputTenVPP.setEnabled(false);
                    inputDVT.setEnabled(false);
                    inputGia.setEnabled(false);
                    inputHinh.setEnabled(false);
                }
            }
        });

    }

    public void setNormalBGTableRows(TableLayout list) {
        // 0: là thằng example đã INVISIBLE
        // Nên bắt đầu từ 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
//             Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
//        Toast.makeText(VanphongphamLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
    }

    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.VISIBLE);
                previewBtn.setVisibility(View.VISIBLE);
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusMaVPP = (TextView) focusRow.getChildAt(0);
                focusTenVPP = (TextView) focusRow.getChildAt(1);
                focusDVT = (TextView) focusRow.getChildAt(2);
                focusGia = (TextView) focusRow.getChildAt(3);
                focusDataHinh = image_list.get( focusRow.getId() - Integer.parseInt("1") );
                setNormalBGTableRows(list);
                // Testing to get id of focusable row
                //  Toast.makeText( PhongbanLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --------------- IMAGE PICKER/ LOADER HELPER ---------------------------------------------------
    // ! Nhớ thêm dòng <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> trong file Manifest.xml
    public void setDataImageView(ImageView imageView, byte[] imageBytes){
        if (imageBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void pickImageFromStorage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_FOLDER);
    }

    public void setEventImagePicker(){
        inputHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_GRANTED);
                    }
                    else{
                        pickImageFromStorage();
                    }
                }
                else{
                    pickImageFromStorage();
                }
            }
        });
    }

    public byte[] getImageDataPicker(){
        byte[] imageBytes = null;
        try {
            Bitmap bitmap = ((BitmapDrawable) inputHinh.getDrawable()).getBitmap();
            if (bitmap != null){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageBytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            }
        }
        catch (IOException e){
            Toast.makeText(VanphongphamLayout.this,"Failed loading image",
                    Toast.LENGTH_SHORT).show();
        }
        finally {
            return imageBytes;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_GRANTED){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImageFromStorage();
            }
        }
        else{
            Toast.makeText(VanphongphamLayout.this,"Permission's denied....", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_FOLDER && resultCode == RESULT_OK && data != null){
            inputHinh.setImageURI(data.getData());
            Toast.makeText(VanphongphamLayout.this,"Thêm hình thành công", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        vppdialog = new Dialog(VanphongphamLayout.this);
        vppdialog.setContentView(layout);
        vppdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        vppdialog.show();
    }

    public void setControlDialog() {
        backBtn = vppdialog.findViewById(R.id.VPP_backBtn);
        yesBtn = vppdialog.findViewById(R.id.VPP_yesInsertBtn);
        noBtn = vppdialog.findViewById(R.id.VPP_noInsertBtn);

        inputMaVPP = vppdialog.findViewById(R.id.VPP_inputMaVPP);
        inputTenVPP = vppdialog.findViewById(R.id.VPP_inputTenVPP);
        inputDVT = vppdialog.findViewById(R.id.VPP_inputDVT);
        inputGia = vppdialog.findViewById(R.id.VPP_inputGia);
        inputHinh = vppdialog.findViewById(R.id.VPP_inputHinh);

        showMVPPError = vppdialog.findViewById(R.id.VPP_showMVPPError);
        showTVPPError = vppdialog.findViewById(R.id.VPP_showTVPPError);
        showDVTError = vppdialog.findViewById(R.id.VPP_showDVTError);
        showGiaError = vppdialog.findViewById(R.id.VPP_showGiaError);

        showResult = vppdialog.findViewById(R.id.VPP_showResult);
        showConfirm = vppdialog.findViewById(R.id.VPP_showConfirm);
        showLabel = vppdialog.findViewById(R.id.VPP_showLabel);
    }

    public void setEventDialog(View view) {
        //  Toast.makeText( PhongbanLayout.this, (view.getId() == R.id.PB_editBtn)+"", Toast.LENGTH_LONG).show();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vppdialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vppdialog.dismiss();
            }
        });
        // Dựa vào các nút mà thằng yesBtn sẽ có event khác
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  showMPBError.setVisibility(View.VISIBLE);
                //  showTPBError.setVisibility(View.VISIBLE);
                //  showResult.setVisibility(View.VISIBLE);
                boolean success = false;
                switch (view.getId()) {
                    case R.id.VPP_insertBtn: {
                        if (!isSafeDialog( false )) break;
//                        Log.d("process","1True");
                        VanPhongPham vpp = new VanPhongPham(
                                inputMaVPP.getText().toString().trim() + "",
                                inputTenVPP.getText().toString().trim() + "",
                                inputDVT.getText().toString().trim() + "",
                                inputGia.getText().toString().trim()+"",
                                getImageDataPicker());
                        if (vanphongphamDB.insert(vpp) == -1) break;
//                        Log.d("process","2True");
                        TableRow tr = createRow(VanphongphamLayout.this, vpp);
                        int n = vpp_table_list.getChildCount();
                        tr.setId(n);
                        vpp_table_list.addView(tr);
                        Log.d("insert",image_list.size()+"");
                        image_list.add(vpp.getHinh());
                        Log.d("insert",image_list.size()+"");
                        setEventTableRows((TableRow) vpp_table_list.getChildAt(n), vpp_table_list);
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaVPP = null;
                        focusTenVPP = null;
                        focusDVT = null;
                        focusGia = null;
                        success = true;
                    }
                    break;
                    case R.id.VPP_editBtn: {
                        if (!isSafeDialog( true )) break;
                        TableRow tr = (TableRow) vpp_table_list.getChildAt(indexofRow);
                        TextView id = (TextView) tr.getChildAt(0);
                        VanPhongPham vpp = new VanPhongPham(
                                id.getText().toString().trim()+"",
                                inputTenVPP.getText().toString().trim() + "",
                                inputDVT.getText().toString().trim() + "",
                                inputGia.getText().toString().trim()+"",
                                getImageDataPicker() );
                        if( vanphongphamDB.update( vpp ) == -1 ) break;
                        focusTenVPP.setText( inputTenVPP.getText().toString().trim() + "");
                        focusDVT.setText( inputDVT.getText().toString().trim() + "");
                        focusGia.setText( inputGia.getText().toString().trim() + "");
                        image_list.set( indexofRow-1, vpp.getHinh() );
                        focusDataHinh = vpp.getHinh();
                        success = true;
                    }
                    break;
                    case R.id.VPP_delBtn: {
                        if( vanphongphamDB.delete(
                                new VanPhongPham(
                                        focusMaVPP.getText().toString().trim()+"",
                                        focusTenVPP.getText().toString().trim()+"",
                                        focusDVT.getText().toString().trim() + "",
                                        focusGia.getText().toString().trim()+"",
                                        focusDataHinh
                                ))
                                == -1 ) break;
                        if (indexofRow == vpp_table_list.getChildCount() - 1) {
                            vpp_table_list.removeViewAt(indexofRow);
                        } else {
                            vpp_table_list.removeViewAt(indexofRow);
                            for (int i = 0; i < vpp_table_list.getChildCount(); i++) {
                                vpp_table_list.getChildAt(i).setId((int) i);
                            }
                        }
                        Log.d("del",image_list.size()+"");
                        image_list.remove(indexofRow -1);
                        Log.d("del",image_list.size()+"");
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        previewBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaVPP = null;
                        focusTenVPP = null;
                        focusDVT = null;
                        focusGia = null;
                        focusDataHinh = null;
                        success = true;
                    }
                    break;
                    default:
                        break;
                }
                if (success) {
                    showResult.setText(showLabel.getText() + " thành công !");
                    showResult.setTextColor(getResources().getColor(R.color.yes_color));
                    showResult.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputMaVPP.setText("");
                            inputTenVPP.setText("");
                            inputDVT.setText("");
                            inputGia.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            vppdialog.dismiss();
                        }
                    }, 1000);
                } else {
                    showResult.setTextColor(getResources().getColor(R.color.thoatbtn_bgcolor));
                    showResult.setText(showLabel.getText() + " thất bại !");
                    showResult.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public boolean isSafeDialog( boolean allowSameID ) {
        String id, mavpp, tenvpp, dvt, gia;
        // Mã PB không được trùng với Mã PB khác và ko để trống
        mavpp = inputMaVPP.getText().toString().trim();
        boolean noError = true;
        if (mavpp.equals("")) {
            showMVPPError.setText("Mã VPP không được trống ");
            showMVPPError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showMVPPError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // Tên PB không được để trống và không trùng
        tenvpp = inputTenVPP.getText().toString().trim();
        if (tenvpp.equals("")) {
            showTVPPError.setText("Tên VPP không được trống ");
            showTVPPError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showTVPPError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // dvt không được để trống và không chữ số
        boolean hasDigits = false;
        dvt = inputDVT.getText().toString().trim();
        hasDigits = dvt.matches(".*\\d.*");
        if (dvt.equals("")) {
            showDVTError.setText("DVT không được trống ");
            showDVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else if( hasDigits ){
            showDVTError.setText("DVT không được chứa số ");
            showDVTError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showDVTError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // Gia không được để trống và không chữ cái
        gia = inputGia.getText().toString().trim();
        if( gia.charAt(0) == '0') if( gia.length() > 1) gia = gia.substring(1,gia.length()-1);
        if (gia.equals("")) {
            showGiaError.setText("Giá không được trống ");
            showGiaError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showGiaError.setVisibility(View.INVISIBLE);
            noError = true;
        }

//        Log.d("mapb_text",mavpp+ "");
//        Log.d("tenpb_text",tenvpp+ "");

        if( noError ) {
            for (int i = 1; i < vpp_table_list.getChildCount(); i++) {
                TableRow tr = (TableRow) vpp_table_list.getChildAt(i);
                TextView mavpp_data = (TextView) tr.getChildAt(0);
                TextView tenvpp_data = (TextView) tr.getChildAt(1);

//            Log.d("mavpp",mavpp_data.getText()+ "");
//            Log.d("tenvpp",tenvpp_data.getText()+ "");
//            Log.d("mapb_comp",(mavpp.equals(mavpp_data.getText().toString()))+ "");
//            Log.d("tenpb_comp",(tenvpp.equals(tenvpp_data.getText().toString()))+ "");

                if (!allowSameID)
                    if (mavpp.equalsIgnoreCase(mavpp_data.getText().toString())) {
                        showMVPPError.setText("Mã PB không được trùng ");
                        showMVPPError.setVisibility(View.VISIBLE);
                        return noError = false;
                    }
                // Trường hợp chỉ đổi ảnh, những thông tin khác ngoại trừ tên thì phải đổi tên luôn à
                if (tenvpp.equalsIgnoreCase(tenvpp_data.getText().toString())
                        && !tenvpp_data.getText().toString().equalsIgnoreCase(
                                focusTenVPP.getText().toString().trim()
                        )
                    ) {
                    showTVPPError.setText("Tên PB không được trùng");
                    showTVPPError.setVisibility(View.VISIBLE);
                    return noError = false;
                }
            }
            showMVPPError.setVisibility(View.INVISIBLE);
            showTVPPError.setVisibility(View.INVISIBLE);
            showDVTError.setVisibility(View.INVISIBLE);
            showGiaError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    // This Custom Columns' Max Width : 70 p0 / 200 / 55 p0 / <= 55 p0
    public TableRow createRow(Context context, VanPhongPham vpp) {
        TableRow tr = new TableRow(context);
        // Id
        //   Ma
        TextView maVPP = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà maVPP đạt tới max width thì nó sẽ tăng height cho bên tenVPP luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        maVPP.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maVPP.setMaxWidth(DPtoPix(70));
        maVPP.setPadding(0,0,0,0);
        maVPP.setText(vpp.getMaVpp());

        //   Ten PB
        TextView tenVPP = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenVPP đạt tới max width thì nó sẽ tăng height cho bên maVPP luôn
        tenVPP.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenVPP.setText(vpp.getTenVpp());
        tenVPP.setMaxWidth(DPtoPix(200));

        TextView dvt = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenVPP đạt tới max width thì nó sẽ tăng height cho bên maVPP luôn
        dvt.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        dvt.setText(vpp.getDvt());
        dvt.setPadding(0,0,0,0);
        dvt.setMaxWidth(DPtoPix(55));

        TextView gianhap = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà tenVPP đạt tới max width thì nó sẽ tăng height cho bên maVPP luôn
        gianhap.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        gianhap.setText(vpp.getGiaNhap());
        gianhap.setPadding(0,0,0,0);
        gianhap.setMaxWidth(DPtoPix(55));

        image_list.add(vpp.getHinh());

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        tr.addView(maVPP);
        tr.addView(tenVPP);
        tr.addView(dvt);
        tr.addView(gianhap);
        return tr;
    }


}