package com.company.Tools;

import java.util.ArrayList;

public class TokenTree {
    private ArrayList<TokenTree> children=new ArrayList<TokenTree>();

    private int childSize=0;

    private int level;//区别其在哪。位置作用域啥的
    private String kind;//这个节点的种类
    private String content;//节点的内容
    //TODO 如何与算术表达式的节点区分开来


    public TokenTree(){

    }

    public void addChild(TokenTree child){
        children.add(child);
        child.childSize++;
    }
    public TokenTree getChild(int index){
        if(index<childSize){
            return children.get(index);
        }else{
            return null;
        }
    }
    public boolean remove(int index){
        if(index<childSize){
            children.remove(index);
            return true;
        }else{
            return false;
        }
    }

    public int getChildSize() {
        return childSize;
    }
}
