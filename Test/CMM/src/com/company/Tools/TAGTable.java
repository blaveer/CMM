package com.company.Tools;

import java.util.ArrayList;

public class TAGTable {
    public static ArrayList<TAGTable> TAG=new ArrayList<TAGTable>();
    public static void addTAG(TAGTable tag){
        TAG.add(tag);
    }
    public static boolean checkExist(String content,int level){
        for(int counter=0;counter<TAG.size();counter++){
            if(TAG.get(counter).getLevel()>level){
                ;;;;;;;;;;;;;;//
            }
        }
        return true;
    }

    /*****************************************************************************************************************************
    上面是static的，下面是类的
    /******************************************************************************************************************************/
    private int level;
    public TAGTable(String kind,String content,int level){

    }
    public int getLevel(){
        return level;
    }

}
