package com.example.matth.einkaufsliste;

public class Article {
    String name;
    int amount;
    public Article(String name,int amount){
        this.name=name;
        this.amount=amount;
    }

    @Override
    public String toString() {
        return name+" "+amount+"Stk";
    }

    public String toCsv() {
        return name+"#"+amount;
    }
}
