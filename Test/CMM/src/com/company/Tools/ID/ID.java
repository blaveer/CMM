package com.company.Tools.ID;

import com.company.Tools.Token;

import java.util.ArrayList;

public class ID {
    private String kind;
    private String name;
    private boolean isArr=false;
    private String content;
    private boolean isInit;//标识是否初始化的,这个似乎没用了，因为当时用到这个类的时候，已经不存在没初始化就使用的情况了，暂留，以备不时之需
    private int length=0;
    public String[] arr=null;
    private boolean isFun=false;
    private int[] dim;//标识每一维的长度

    /**普通变量声明*/
    public ID(String kind,String name){
        this.kind=kind;
        this.name=name;
        this.isInit=false;
    }

    /**普通变量声明并初始化*/
    public ID(String kind,String name,String content) {
        this.kind = kind;
        this.name = name;
        this.content=content;
        this.isInit=true;
    }

    /**数组声明*/
    public ID(String king,String name,boolean isArr,ArrayList<Integer> dim,int length){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.dim=new int[dim.size()];
        for(int i=0;i<dim.size();i++){
            this.dim[i]=dim.get(i);
        }

        this.length=length;
        this.isInit=false;
    }

    /**声明数组并初始化*/
    public ID(String king,String name,boolean isArr,ArrayList<Integer> dim,int length,ArrayList<String> arr){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.length=length;
        this.isInit=true;
        this.dim=new int[dim.size()];
        for(int i=0;i<dim.size();i++){
            this.dim[i]=dim.get(i);
        }
        this.arr=new String[arr.size()];
        for(int counter=0;counter<arr.size();counter++){
            this.arr[counter]=arr.get(counter);
        }
    }

    public ID(String kind,String name,boolean isFun){
        this.kind=kind;
        this.name=name;
        this.isFun=isFun;
    }

    public String get(int index){
        if(index<0||index>=arr.length){
            return null;
        }
        return arr[index];
    }
    public boolean set(String new_value,int index){
        if(index<0||index>=arr.length){
            return false;
        }
        else{
            arr[index]=new_value;
            return true;
        }
    }

    //region set和get函数，只添加了一部分，等有需要了再添加
    public String getKind(){
        return kind;
    }
    public String getName(){
        return name;
    }
    public String getContent(){
        return content;
    }
    public int getLength(){
        return length;
    }
    public boolean getIsArr(){
        return isArr;
    }
    public void setKind(String kind){this.kind=kind;}
    public void setName(String name){this.name=name;}
    public void setContent(String content){this.content=content;}
    public void setArr(ArrayList<String> arr){
        this.arr=new String[arr.size()];
        for(int counter=0;counter<arr.size();counter++){
            this.arr[counter]=arr.get(counter);
        }
    }

    public String getArr(){
        String r="";
        for(int counter=0;counter<length;counter++){
            r=r+arr[counter]+"   ";
        }
        return r;
    }

    public int getDim(int index){
        return dim[index];
    }
    public int getDimSize(){
        return dim.length;
    }

    //endregion

    //TODO 相信这里会有很多static 的方法


}
