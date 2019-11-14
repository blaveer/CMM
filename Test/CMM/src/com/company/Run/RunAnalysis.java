package com.company.Run;

import com.company.Tools.ID.ID;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

/**遗留待解决的问题，数组的下标越界检查，还有待发现的*/
public class RunAnalysis {
    private int errorNum=0;
    private String errorInfo="";
    private String log="";
    private TokenTree root;
    private ArrayList<ID> ids=new ArrayList<ID>();
    public RunAnalysis(TokenTree root){
        this.root=root;
    }

    public void run(){
        runEvery(root);
    }
    private void runEvery(TokenTree tempRoot){
        int start=ids.size();
        int end=ids.size();
        TokenTree temp=null;
        for(int counter=0;counter<tempRoot.getChildSize();counter++){
            temp=tempRoot.get(counter);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                declare_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("read")){
                //read_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("write")){
                //write_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("assign")){
                assign_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("while")){
                //while_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("for")){
                //for_run(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("if")){
                //if_run(temp);
            }
            else if(temp.getKind().equals("finish")&&temp.getContent().equals("finish")){
                System.out.println("运行结束");
                break;
            }
            else{
                System.out.println("出现了不该出现的关键字");
            }
        }
    }

    private boolean assign_run(TokenTree temp){
        TokenTree assign=temp.get(0);//这个是等号的那个节点
        TokenTree id_assign=assign.get(0);//这个是标识符
        ID id_temp=findIDByName(id_assign.getContent());
        /**对数组的某一项赋值*/
        if(id_assign.hasChildren()){
            TokenTree array_index=id_assign.get(0);
            int index=run_int_result(array_index);
        }
        return true;
    }

    private boolean declare_run(TokenTree temp){
        String kind=temp.get(0).getContent();
        for(int counter=1;counter<temp.getChildSize();counter++){
            TokenTree one_declare=temp.get(counter);
            if(one_declare.getKind().equals("运算符")&&one_declare.getContent().equals("=")){
                TokenTree one_id=one_declare.get(0);/**这个变量代表的是声明的标识符*/
                if(one_id.hasChildren()){
                    TokenTree one_id_array_length=one_id.get(0);
                    TokenTree one_id_array_init=one_declare.get(1);
                    int length=run_int_result(one_id_array_length);
                    if(length<0){
                        addError("关于标识符"+one_id.getContent()+"的初始化的长度小于零");
                        return false;
                    }
                    if(length!=one_id_array_init.getChildSize()){
                        addError("关于标识符"+one_id.getContent()+"的初始化的长度与其声明的长度不符");
                        return false;
                    }
                    ArrayList<String> array_init=new ArrayList<String>();
                    for(int counter_length=0;counter_length<one_id_array_init.getChildSize();counter_length++){
                        array_init.add(run_all_result(one_id_array_init.get(counter_length),kind));
                    }
                    ids.add(new ID(kind,one_id.getContent(),true,length,array_init));
                }
                else{
                    TokenTree one_init=one_declare.get(1);
                    String init_ID=run_all_result(one_init,kind);
                    ids.add(new ID(kind,one_id.getContent(),init_ID));
                }
            }
            else{
                if(one_declare.hasChildren()){
                    TokenTree array_length=one_declare.get(0);
                    int length=run_int_result(array_length);
                    if(length<0){
                        addError("关于标识符"+one_declare.getContent()+"的标识的长度小于零");
                        return false;
                    }
                    ids.add(new ID(kind,one_declare.getContent(),true,length));
                }
                else{
                    ids.add(new ID(kind,one_declare.getContent()));
                }
            }
        }
        return true;
    }

    private String run_all_result(TokenTree temp,String kind){
        return null;
    }

    private int run_int_result(TokenTree temp){
        return 0;
    }


    private ID findIDByName(String name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(name)){
                return ids.get(counter);
            }
        }
        return null;
    }




    private void addError(String info){
        addLog(info);
        errorNum++;
        errorInfo=errorInfo+info+"\n";
    }
    private void addLog(String info){
        log=log+info+"\n";
    }
}
