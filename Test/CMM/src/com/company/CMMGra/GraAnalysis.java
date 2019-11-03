package com.company.CMMGra;

import com.company.Tools.Token;

import java.util.ArrayList;

public class GraAnalysis {
    // 错误个数
    private int errorNum = 0;
    // 错误信息
    private String errorInfo = "";

    // 分析后得到的tokens集合，用于其后的语法及语义分析
    private ArrayList<Token> tokens = new ArrayList<Token>();

    private int counter=0;
    public void gra(ArrayList<Token> lexToken){
        for(;counter<lexToken.size();){

        }
    }
    private void statement(){

    }

    //region setget函数
    public int getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
    //endregion
}
