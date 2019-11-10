package com.company.CMMSem;

import com.company.Tools.ID.ID;
import com.company.Tools.RunError.BaseError;
import com.company.Tools.Token;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

import static com.company.Tools.ID.ID.isStatement;

public class semAnalysis {
    private ArrayList<ID> id = new ArrayList<ID>();
    private TokenTree root=null;
    public semAnalysis(TokenTree root){
        this.root=root;
    }
    public void sem(){
        TokenTree temp=null;
        for(int i=0;i<root.getChildSize();i++){
            temp=root.children.get(i);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                run_declare(temp);
            }
        }
    }
    private BaseError run_declare(TokenTree declare){
        //获取到目前声明的数据类型
        String kind=declare.get(0).getContent();
        TokenTree temp=null;
        for(int i=1;i<declare.getChildSize();i++){
            temp=declare.get(i);
            //这个是声明并初始化
            if(temp.getContent().equals("=")){
                //数组的声明  temp的第一个子节点就是要声明的标识符，如果其有子节点，那就是数组的长度，也就是下标
                if(isStatement(id,temp.get(0).getContent())){
                    return new BaseError("标识符"+temp.get(0).getContent()+"已经存在");
                }
                if(temp.get(0).hasChildren()){
                    int length=evaluationExpression(temp.get(0).get(0));
                    ArrayList<String> init=new ArrayList<>();
                    TokenTree tempInit=temp.get(1);
                    for(int counter=0;counter<tempInit.getChildSize();counter++){
                        if(tempInit.get(counter).getKind().equals(kind)){
                            init.add(tempInit.get(counter).getContent());
                        }
                        else{
                            return new BaseError("数组初始化时第"+(counter+1)+"个数据类型与声明的数据类型不符");
                        }
                    }
                    if(length!=init.size()){
                        return new BaseError("数组实例化的长度与声明的长度不符");
                    }
                    ID tempID=new ID(kind,temp.get(0).getContent(),true,length,init);
                    id.add(tempID);
                }
                //普通的声明初始化
                else{
                    if(isStatement(temp.get(0).getContent())){
                        return new BaseError("标识符"+temp.get(0).getContent()+"已经存在");
                    }

                    ID tempID=new ID(kind,temp.get(0).getContent(),temp.get(1).getContent());
                    id.add(tempID);
                }
            }
            //这个就只是声明
            else{
                if(temp.hasChildren()){

                }
                else{

                }
            }
        }
        return null;
    }



    private int evaluationExpression(TokenTree express){
        //TODO 待做
        return 0;
    }

    private boolean typeCompatibility(String kindStatement,String kindAssignment){
        //TODO 在这做类型兼容的问题
        if()
    }
    /**那个布尔变量只是为了区分两个函数
     * kindAssignmentID是标识符的名字，这里的含义是指当赋值的时候用的是标识符的话，就将其名字传进来
     * */
    private boolean typeCompatibility(String kindStatement,String kindAssignmentIDName,boolean d){
        String kindAssignment=findIDKindByName(kindAssignmentIDName);
    }

    private String findIDKindByName(String name){
        for(int counter=0;counter<id.size();counter++){
            if(id.get(counter).getName().equals(name)){
                return id.get(counter).getKind();
            }
        }
        return null;
    }
    public boolean isStatement(String tempId){
        for(int i=0;i<id.size();i++){
            if(tempId.equals(id.get(i).name)){
                return true;
            }
        }
        return false;
    }
}
