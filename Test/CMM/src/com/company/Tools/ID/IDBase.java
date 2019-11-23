package com.company.Tools.ID;

import java.net.IDN;
import java.util.ArrayList;

public class IDBase {
    private String kind;
    private String name;
    private boolean isArr=false;
    private boolean isInit=false;
    private boolean isFun=false;
    private int arr_dim=0;
    //private int fun_par_num=0;
    private ArrayList<Dim> fun_par=new ArrayList<Dim>();
    /**dist变量是用来区分是否是数组的，1是普通，2是数组*/
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

    /**多维数组*/
    public IDBase(String kind,String name,boolean isInit,int arr_dim){
        this.kind=kind;
        this.name=name;
        this.isInit=isInit;
        this.isArr=true;
        this.arr_dim=arr_dim;
    }
    public IDBase(String kind,String name,boolean isFun){
        this.kind=kind;
        this.name=name;
        this.isFun=true;
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
    public void setIsInit(boolean isInit){
        this.isInit=isInit;
    }
    public boolean getIsFun(){
        return isFun;
    }
    public ArrayList<Dim> getFun_par(){
        return fun_par;
    }

    public void setFun_par(ArrayList<Dim> fun_par_num) {
        this.fun_par = fun_par_num;
    }
    public void addFun_par(Dim id){
        this.fun_par.add(id);
    }
    public int getArr_dim(){
        return arr_dim;
    }

}
