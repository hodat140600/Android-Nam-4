package com.example.giuaki.Entities;

import androidx.annotation.NonNull;

public class CapPhat {

    private String soPhieu;
    private String ngayCap;
    private String maVpp;
    private String maNv;
    private long sl;

    public CapPhat( String soPhieu, String ngayCap, String maVpp, String maNv, long sl) {
        this.soPhieu = soPhieu;
        this.ngayCap = ngayCap;
        this.maVpp = maVpp;
        this.maNv = maNv;
        this.sl = sl;
    }

    @Override
    public String toString() {
        return "CapPhat{" +
                "soPhieu='" + soPhieu + '\'' +
                ", ngayCap='" + ngayCap + '\'' +
                ", maVpp='" + maVpp + '\'' +
                ", maNv='" + maNv + '\'' +
                ", sl=" + sl +
                '}';
    }

    public String getSoPhieu() {
        return soPhieu;
    }

    public void setSoPhieu(String soPhieu) {
        this.soPhieu = soPhieu;
    }

    public String getNgayCap() {
        return ngayCap;
    }

    public void setNgayCap(String ngayCap) {
        this.ngayCap = ngayCap;
    }

    public String getMaVpp() {
        return maVpp;
    }

    public void setMaVpp(String maVpp) {
        this.maVpp = maVpp;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public long getSl() {
        return sl;
    }

    public void setSl(long sl) {
        this.sl = sl;
    }
}
