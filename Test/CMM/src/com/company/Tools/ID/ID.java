package com.company.Tools.ID;

import com.company.Tools.Token;

import java.util.ArrayList;

public class ID {
    public String kind;
    public String name;
    public boolean isArr=false;
    public String content;
    public int length=0;
    public String[] arr=null;
    public ID(String kind,String name){
        this.kind=kind;
        this.name=name;
        switch (kind){
            case "int":
                content="0";
                break;
            case "real":
                content="0.0";
                break;
            case "bool":
                content="false";
                break;
            //待补充
            default:
                content="";
                break;
        }
    }
    public ID(String kind,String name,String content) {
        this.kind = kind;
        this.name = name;
        this.content=content;
    }
    public ID(String king,String name,boolean isArr,int length){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.length=length;
    }

    public ID(String king,String name,boolean isArr,int length,ArrayList<String> arr){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.length=length;
        this.arr=new String[arr.size()];
        for(int counter=0;counter<arr.size();counter++){
            this.arr[counter]=arr.get(counter);
        }
    }

    public String getKind(){
        return kind;
    }
    public String getName(){
        return name;
    }
    public String getContent(){
        return content;
    }

    //TODO 相信这里会有很多static 的方法


}
