package com.example.giuaki.Entities;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.giuaki.Main.PhongbanLayout;
import com.example.giuaki.R;

import org.w3c.dom.Text;

public class PhongBan {

//    private long id;
    private String mapb;
    private String tenpb;

    public PhongBan(String mapb, String tenpb) {
        this.mapb = mapb;
        this.tenpb = tenpb;
    }

//    public PhongBan(long id, String mapb, String tenpb) {
//        this.id = id;
//        this.mapb = mapb;
//        this.tenpb = tenpb;
//    }

    @Override
    public String toString() {
        return "PhongBan{" +
//                "id=" + id +
                ", mapb='" + mapb + '\'' +
                ", tenpb='" + tenpb + '\'' +
                '}';
    }

    public String toIDandName(){
        return mapb+"-"+tenpb;

    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getMapb() {
        return mapb;
    }

    public void setMapb(String mapb) {
        this.mapb = mapb;
    }

    public String getTenpb() {
        return tenpb;
    }

    public void setTenpb(String tenpb) {
        this.tenpb = tenpb;
    }


}
