package com.company.Tools.ID;

public class Dim {
    private String kind;
    private String name;
    public Dim(String kind,String name){
        this.kind=kind;
        this.name=name;
    }

    public String getName() {
        return name;
    }
    public String getKind(){
        return kind;
    }
}
