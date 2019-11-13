package com.company.Tools.ID;

public class IDBase {
    private String kind;
    private String name;
    private boolean isArr=false;
    private boolean isInit=false;
    public IDBase(String kind,String name,int dist){
        this.kind=kind;
        this.name=name;
        if(dist==1){
            isArr=false;
        }
        else if(dist==2){
            isArr=true;
        }
    }
    public IDBase(String kind,String name,int dist,boolean isInit){
        this.kind=kind;
        this.name=name;
        this.isInit=isInit;
        if(dist==1){
            isArr=false;
        }
        else if(dist==2){
            isArr=true;
        }
    }
    public String getKind(){
        return kind;
    }
    public String getName(){
        return name;
    }
    public boolean getIsArr(){
        return isArr;
    }
    public boolean getIsInit(){
        return isInit;
    }
}