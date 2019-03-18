package com.example.matth.einkaufsliste;

import java.util.ArrayList;

public class Store {
    String name;
    ArrayList<Article> articles;
    public Store(String name){
        this.name=name;
        articles=new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public String toCsv() {
        String csv="";
        for (Article a:articles) {
            csv+=a.toCsv()+",";
        }
        return csv.substring(0,csv.length()-1);
    }
}
