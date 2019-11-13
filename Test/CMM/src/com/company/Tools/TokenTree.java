package com.company.Tools;

import java.util.ArrayList;

public class TokenTree {
    public ArrayList<TokenTree> children=new ArrayList<TokenTree>();//存储每个节点下的其子节点

    //private int childSize=0;

    //private int level;//区别其在哪。位置作用域啥的
    //public ArrayList<TAG> tagTable=new ArrayList<TAG>();//存储每个节点下的标识符

    private String kind;//这个节点的种类
    private String content;//节点的内容
    //TODO 如何与算术表达式的节点区分开来


    public TokenTree(String kind,String content){
        this.kind=kind;
        this.content=content;
    }
    public boolean hasChildren(){
        if(children.size()>0){
            return true;
        }else{
            return false;
        }
    }
    public int getChildSize() {
        return children.size();
    }

    public void outTokenTree(String kong){
        System.out.println(kong+"|"+kind+"   "+content);
       //System.out.println(children.size());
        for(int i=0;i<children.size();i++){
            children.get(i).outTokenTree(kong+"|     ");
            //System.out.println(kong+kind+(i+1));
        }
    }


    public String getKind(){
        return kind;
    }
    public String getContent(){
        return content;
    }
    public TokenTree get(int index){
        return children.get(index);
    }

//    public void addChild(TokenTree child){
//        children.add(child);
//        child.childSize++;
//    }
//    public TokenTree getChild(int index){
//        if(index<childSize){
//            return children.get(index);
//        }else{
//            return null;
//        }
//    }
//    public boolean remove(int index){
//        if(index<childSize){
//            children.remove(index);
//            return true;
//        }else{
//            return false;
//        }
//    }
//

}
