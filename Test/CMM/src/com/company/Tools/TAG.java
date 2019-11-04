package com.company.Tools;

public class TAG {
    private String kind;
    private String name;
    private String content;
    public TAG(String kind,String name,String content){
        this.kind=kind;
        this.name=name;
        this.content=content;
    }
    public TAG(){
        this.kind=null;
        this.name=null;
        this.content=null;
    }
    public TAG(String kind,String name){
        this.kind=kind;
        this.name=name;
        this.content=null;
    }

    //region get set方法
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    //endregion
}
