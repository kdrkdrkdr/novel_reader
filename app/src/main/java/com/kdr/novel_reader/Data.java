package com.kdr.novel_reader;

public class Data {
    String ja_name;
    String ko_name;

    public Data(String ja_name, String ko_name) {
        this.ja_name = ja_name;
        this.ko_name = ko_name;
    }

    public String getJa_name() {
        return ja_name;
    }

    public void setJa_name(String ja_name) {
        this.ja_name = ja_name;
    }

    public String getKo_name() {
        return ko_name;
    }

    public void setKo_name(String ko_name) {
        this.ko_name = ko_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Data)) {
            return false;
        }
        Data d = (Data) obj;
        return (this.ja_name == d.ja_name && this.ko_name == d.ko_name);
    }
}
