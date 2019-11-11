package com.company.Tools.ID;

import com.company.Tools.Token;

import java.util.ArrayList;

public class ID {
    private String kind;
    private String name;
    public boolean isArr=false;
    private String content;
    private boolean isInit;//标识是否初始化的
    public int length=0;
    public String[] arr=null;

    /**普通变量声明*/
    public ID(String kind,String name){
        this.kind=kind;
        this.name=name;
        this.isInit=false;
//        switch (kind){
//            case "int":
//                content="0";
//                break;
//            case "real":
//                content="0.0";
//                break;
//            case "bool":
//                content="false";
//                break;
//            //待补充
//            default:
//                content="";
//                break;
//        }
    }

    /**普通变量声明并初始化*/
    public ID(String kind,String name,String content) {
        this.kind = kind;
        this.name = name;
        this.content=content;
        this.isInit=true;
    }

    /**数组声明*/
    public ID(String king,String name,boolean isArr,int length){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.length=length;
        this.isInit=false;
    }

    /**声明数组并初始化*/
    public ID(String king,String name,boolean isArr,int length,ArrayList<String> arr){
        this.kind=king;
        this.name=name;
        this.isArr=true;
        this.length=length;
        this.isInit=true;
        this.arr=new String[arr.size()];
        for(int counter=0;counter<arr.size();counter++){
            this.arr[counter]=arr.get(counter);
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
    public void setKind(String kind){this.kind=kind;}
    public void setName(String name){this.name=name;}
    public void setContent(String content){this.content=content;}

    //endregion

    //TODO 相信这里会有很多static 的方法


}
