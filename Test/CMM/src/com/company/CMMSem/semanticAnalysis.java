package com.company.CMMSem;


import com.company.Tools.ID.IDBase;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

/**
 * 关于建立这个类的原因，本来是想在上个类中直接实现检查内容和执行的效果，
 * 后来发现难度过大，所以就在这单独的先进行一个语义分析
 * 其实挺后悔在语法分析的时候没有将TokenTree的位置传过来
 * */
public class semanticAnalysis {
    private TokenTree root;/**用来继承其根节点*/
    private ArrayList<IDBase> ids=new ArrayList<>();/**用来动态的记录标识符的*/
    //private ArrayList<ID> idArrayList=new ArrayList<>();/**可以说这两个变量相辅相成，上面那个是用来监测是否有重复的标识符或者未声明巴拉巴拉，这个是用来记录每一个合法的标识符的*/
    private ArrayList<Integer> counter_id=new ArrayList<>();/**这个变量是记录在每个模块中增加的标识符的的数量的,为了能够实现变量的作用域，自己挖的坑，自己填吧*/
    private int errorNum=0;
    private String errorInfo="";
    private String log="";
    public semanticAnalysis(TokenTree root){
        this.root=root;
    }
    public void semantic(TokenTree tempRoot){
        TokenTree temp=null;
        int counter_id_start=ids.size();
        int counter_id_end=ids.size();
        for(int i=0;i<tempRoot.getChildSize();i++){
            //counter_id=0;
            temp=tempRoot.get(i);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                declare_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("read")){
                read_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("write")){
                write_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("assign")){
                assign_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("while")){
                while_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("for")){
                for_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("if")){
                if_sem(temp);
            }
            else if(temp.getKind().equals("finish")&&temp.getContent().equals("finish")){
                System.out.println("语义分析结束");
                break;
            }
            else{
                System.out.println("出现了不该出现的关键字");
            }
            //removeID(counter_id.get(counter_id.size()-1));
        }
        counter_id_end=ids.size();
        int add_num=counter_id_end-counter_id_start;
        if(add_num>0){
            removeID(counter_id_start,add_num);
        }else if(add_num==0){
            addLog("本次没有添加新的变量");
        }
        else{
            addLog("卧槽，跟胡扯一样");
        }
    }

    private void read_sem(TokenTree temp){

    }

    private void write_sem(TokenTree temp){

    }

    private void if_sem(TokenTree temp){
        TokenTree if_check=temp.get(0);
        TokenTree if_main=temp.get(1);
        TokenTree else_main=null;
        if(temp.getChildSize()>2){
            else_main=temp.get(2);
        }
        check_sem(if_check.get(0));
        semantic(if_main);
        if(else_main!=null){
            semantic(else_main);
        }
    }

    private void for_sem(TokenTree temp){
        TokenTree init_front=temp.get(0);
        TokenTree check=temp.get(1);
        TokenTree init_back=temp.get(2);
        TokenTree for_main=temp.get(3);
        assign_sem(init_front.get(0));
        check_sem(check.get(0));
        assign_sem(init_back.get(0));
        semantic(for_main);

    }

    private void while_sem(TokenTree temp){
        //只要语法分析没问题，就两个子节点
        TokenTree while_check=temp.get(0);
        TokenTree while_main=temp.get(1);
        check_sem(while_check.get(0));
        semantic(while_main);
    }

    private void assign_sem(TokenTree temp){
        TokenTree assign=temp.get(0);//这个是等号的那个节点
        TokenTree id_assign=assign.get(0);//这个是标识符
        String kind="";

        if(isExist(id_assign.getContent())){
            kind=findIDKindByName(id_assign.getContent());
        }
        else{
            addError("使用了未声明的变量"+id_assign.getContent());
            return;
        }
        /**对数组的某一项赋值*/
        if(id_assign.hasChildren()){
            TokenTree id_assign_array_length=id_assign.get(0);
            if(id_assign_array_length.getKind().equals("运算符")){
                if(express_type_check(id_assign_array_length,"int")){
                    findIDByName(id_assign.getContent()).setIsInit(true);
                }
            }
            else if(id_assign_array_length.getContent().equals("int")){
                findIDByName(id_assign.getContent()).setIsInit(true);
            }
            else if(id_assign_array_length.getKind().equals("标识符")){
                if(isExist(id_assign_array_length.getContent())){
                    IDBase temp_id=findIDByName(id_assign_array_length.getContent());
                    String kind_temp_id=temp_id.getKind();
                    boolean is_init=temp_id.getIsInit();
                    if(!is_init){
                        addError("使用了初始化的标识符"+id_assign_array_length.getContent());
                    }else if(!typeCompatibility("int",kind_temp_id)){
                        addError("使用了不是int的标识符"+id_assign_array_length.getContent()+"作为数组下标");
                    }
                    else{
                        findIDByName(id_assign.getContent()).setIsInit(true);
                        //将标识符设置为初始化了，无论之前是否初始化过
                    }
                }
                else{
                    addError("使用了未声明的标识符"+id_assign_array_length.getContent());
                }
            }
            TokenTree id_assign_array_one_init=assign.get(1);//这个是等号后面的
            if(id_assign_array_one_init.getKind().equals("标识符")){
                if(useID(id_assign_array_one_init.getContent(),kind)){
                    findIDByName(id_assign.getContent()).setIsInit(true);
                }
                else{
                    addError("使用了不合理的标识符来初始化"+kind+"类型的数组");
                }
            }
            else if(id_assign_array_one_init.getKind().equals("运算符")){
                if(express_type_check(id_assign_array_one_init,kind)){
                    findIDByName(id_assign.getContent()).setIsInit(true);
                }
                else{
                    addError("使用了不合理的算术表达式来初始化"+kind+"类型的数组");
                }
            }
            else if(typeCompatibility(kind,id_assign_array_one_init.getKind())){
                findIDByName(id_assign.getContent()).setIsInit(true);
            }
            else{
                addError("使用了不合理表达式来初始化"+kind+"类型的数组");
            }
        }
        else{
            TokenTree id_init=assign.get(1);
            IDBase id_temp=findIDByName(id_assign.getContent());
            boolean declare_id_array=id_temp.getIsArr();
            if(declare_id_array){
                if(id_init.getContent().equals("AllArrayInit")){
                    if(express_type_check_array_init(id_init,kind)){
                        id_temp.setIsInit(true);
                    }
                    else{
                        addError("在对数组"+id_assign.getContent()+"进行赋值时采用了不合理的数据类型");
                    }
                }
                else{
                    addError("对于被声明为数组的标识符"+id_assign.getContent()+"采用了不合理的整体赋值方式");
                }
            }
            else{
                if(id_init.getContent().equals("AllArrayInit")){
                    addError("对于变量"+id_assign.getContent()+"采用了不合理的赋值方式");
                }
                else{
                    if(id_init.getKind().equals("标识符")){
                        if(useID(id_init.getContent(),kind)){
                            findIDByName(id_assign.getContent()).setIsInit(true);
                        }
                        else{
                            addError("对变量"+id_assign+"的赋值采用了不合理的数据类型");
                        }
                    }
                    else if(id_init.getKind().equals("运算符")){
                        if(express_type_check(id_init,kind)){
                            findIDByName(id_assign.getContent()).setIsInit(true);
                        }
                        else{
                            addError("对变量"+id_assign+"的赋值采用了不合理的数据类型");
                        }
                    }
                    else if(typeCompatibility(kind,id_init.getKind())){
                        findIDByName(id_assign.getContent()).setIsInit(true);
                    }
                    else{
                        addError("对变量"+id_assign+"的赋值采用了不合理的数据类型");
                    }
                }
            }
        }
    }

    private void declare_sem(TokenTree temp){
        String kind=temp.get(0).getContent();/**获取到声明的数据类型*/
        /**之所以从1开始，是因为声明节点的第一个子节点都是其声明的数据类型*/
        for(int counter=1;counter<temp.getChildSize();counter++){
            TokenTree one_declare=temp.get(counter);/**这个变量代表的是单独的一整个声明，比如int a=1,b=9,它可以代表其中的一个*/
            /**声明加初始化*/
            if(one_declare.getKind().equals("运算符")&&one_declare.getContent().equals("=")){
                TokenTree one_id=one_declare.get(0);/**这个变量代表的是声明的标识符*/
                if(isExist(one_id.getContent())){
                    addError("在声明的标识符"+one_id.getContent()+"已经存在");
                    continue;
                }
                if(one_id.hasChildren()){
                    TokenTree one_id_array_length=one_id.get(0);
                    TokenTree one_id_array_init=one_declare.get(1);
                    if(!express_type_check_array_init(one_id_array_init,kind)){
                        addError("关于"+one_id.getContent()+"数组的初始化中数据类型不匹配");
                        continue;
                    }
                    if(one_id_array_length.getKind().equals("int")){
                        ids.add(new IDBase(kind,one_id.getContent(),2,true));
                    }
                    else if(one_id_array_length.getKind().equals("运算符")){
                        if(express_type_check(one_id_array_length,"int")){
                            ids.add(new IDBase(kind,one_id.getContent(),2,true));
                        }
                        else{
                            addError(one_id.getContent()+"的声明中数组长度不合法");
                        }
                    }
                    else{/**不允许使用标识符声明数组长度*/
                        addError(one_id.getContent()+"的声明中数组长度不合法");
                    }
                }
                else{
                    TokenTree one_init=one_declare.get(1);
                    if(one_init.getKind().equals("运算符")){
                        if(express_type_check(one_init,kind)){
                            ids.add(new IDBase(kind,one_id.getContent(),1));
                        }
                        else{
                            addError("关于"+one_id.getContent()+"的初始化类型不兼容");
                        }
                    }
                    else{
                        if(!typeCompatibility(kind,one_init.getKind())){
                            addError("关于"+one_id.getContent()+"的初始化类型不兼容");
                        }
                        else{
                            ids.add(new IDBase(kind,one_id.getContent(),1));
                        }
                    }
                }
            }/**只声明了*/
            else if(one_declare.getKind().equals("标识符")){
                if(isExist(one_declare.getContent())){
                    addError("要声明的标识符"+one_declare.getContent()+"已经存在");
                    continue;
                }
                if(one_declare.hasChildren()){
                    TokenTree one_id_array_length=one_declare.get(0);
                    if(one_id_array_length.getKind().equals("int")){
                        ids.add(new IDBase(kind,one_declare.getContent(),2));
                    }
                    else if(one_id_array_length.getKind().equals("运算符")){
                        if(express_type_check(one_id_array_length,"int")){
                            ids.add(new IDBase(kind,one_declare.getContent(),2));
                        }
                        else{
                            addError(one_declare.getContent()+"的声明中数组长度不合法");
                        }
                    }
                    else{/**不允许使用标识符声明数组长度*/
                        addError(one_declare.getContent()+"的声明中数组长度不合法");
                    }
                }
                else{
                    ids.add(new IDBase(kind,one_declare.getContent(),1));
                }

            }
            else{
                addError("语法分析有待完善");
            }
        }
    }

    private void check_sem(TokenTree temp){
        if(temp.getKind().equals("标识符")){
            if(useID(temp.getContent(),"bool")){
                addLog("check语句语义分析成功");
            }
            else{
                addError("check语句的返回结果不是bool类型");
                addLog("check语句的返回结果不是bool类型");
            }
        }
        else if(temp.getKind().equals("运算符")){
            if(express_type_check(temp,"bool")){
                addLog("check语句语义分析成功");
            }
            else{
                addError("check语句的返回结果不是bool类型");
                addLog("check语句的返回结果不是bool类型");
            }
        }
        else if(typeCompatibility("bool",temp.getKind())){
            addLog("check语句语义分析成功");
        }
        else{
            addError("check语句的返回结果不是bool类型");
            addLog("check语句的返回结果不是bool类型");
        }
    }

    /**这个函数主要是用来检测数组的初始胡时候的类型问题
     * 其中tempToot(写错了不改了)，是用来标识数组初始化的节点的其子节点就是全部要初始化的内容
     * 其中king是目标类型，，这个也写错了，，，*/
    private boolean express_type_check_array_init(TokenTree tempToot,String kind){
        //初始化这也要考虑类型兼容了
        if(!tempToot.getKind().equals("AllArrayInit")){
            return false;
        }
        for(int counter=0;counter<tempToot.getChildSize();counter++){
            if(!express_type_check(tempToot.get(counter),kind)){
                return false;
            }
        }
        return true;//TODO 待做
    }

    private boolean express_type_check(TokenTree express,String kind){
        switch (kind){
            case "int":
                return type_int_check(express);
            case "real":
                return type_real_check(express);
            case "bool":
                return type_bool_check(express);
            default:return false;
        }
    }

    private boolean type_bool_check(TokenTree express_bool){
        if(isLogic(express_bool)){
            if(express_bool.getKind().equals("&&")||express_bool.getKind().equals("||")){
                return (express_type_check(express_bool.get(0),"bool")&&express_type_check(express_bool.get(1),"bool"));
            }else{
                return (express_type_check(express_bool.get(0),"real")&&express_type_check(express_bool.get(1),"real"));
            }
        }
        else if(express_bool.getKind().equals("标识符")){
            //if(typeCompatibility("bool",findIDKindByName(express_bool.getContent()))){
            if(useID(express_bool.getContent(),"bool")){
                return true;
            }
            else{
                return false;
            }
        }
        else if(express_bool.getKind().equals("bool")&&(express_bool.getContent().equals("false")||express_bool.getContent().equals("true"))){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean type_real_check(TokenTree express_real){
        if(express_real.getKind().equals("标识符")){
            if(express_real.hasChildren()){
                if(!useID(express_real.getContent(),"real")){
                    //addError("标识符"+express_int.getContent()+"不存在");
                    return false;
                }
                if(express_type_check(express_real.get(0),"int")){
                    addLog("asasas");
                }
                else{
                    return false;
                }
            }
            else{
                if(!useID(express_real.getContent(),"real")){
                    return false;
                }
            }
        }
        else if(express_real.getKind().equals("real")||express_real.getKind().equals("int")){
            return true;
        }
        else if(express_real.getKind().equals("运算符")&&isArithmetic(express_real)){
            return (type_real_check(express_real.get(0))&&type_real_check(express_real.get(1)));
        }
        else {
            return false;
        }
        return true;
    }

    private boolean type_int_check(TokenTree express_int){
        if(express_int.getKind().equals("标识符")){
            if(express_int.hasChildren()){
                if(!useID(express_int.getContent(),"int")){
                    //addError("标识符"+express_int.getContent()+"不存在");
                    return false;
                }
                if(express_type_check(express_int.get(0),"int")){
                    addLog("asasas");
                }
                else{
                    return false;
                }
            }
            else{
                if(!useID(express_int.getContent(),"int")){
                    return false;
                }
            }
        }
        else if(express_int.getKind().equals("int")){
            return true;
        }
        else if(express_int.getKind().equals("运算符")&&isArithmetic(express_int)){
            return (type_int_check(express_int.get(0))&&type_int_check(express_int.get(1)));
        }
        else{
            return false;
        }
        return true;
    }

    private void removeID(int start, int counter){
        for(int i=0;i<counter;i++){
            ids.remove(start);
        }
        //TODO 待做
    }

    private boolean isLogic(TokenTree op){
        if(op.getKind().equals("运算符")&&(!isArithmetic(op))){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isArithmetic(TokenTree op){
        if(op!=null&&(op.getContent().equals("+")
                        ||op.getContent().equals("-")
                        ||op.getContent().equals("*")
                        ||op.getContent().equals("/"))){
            return true;
        }
        return false;
    }

    /**其中name是使用的标识符的名字，kind是希望的类型*/
    private boolean useID(String name,String kind){
        if(isExist(name)){
            IDBase temp_id=findIDByName(name);
            String kind_temp_id=temp_id.getKind();
            boolean is_init=temp_id.getIsInit();
            if(!is_init){
                addError("使用了初始化的标识符"+name);
            }else if(!typeCompatibility(kind,kind_temp_id)){
                addError("使用了类型不兼容的标识符"+name);
            }
            else{
//                findIDByName(name).setIsInit(true);
                return true;
                //将标识符设置为初始化了，无论之前是否初始化过
            }
        }
        else{
            addError("使用了未声明的标识符"+name);
        }
        return false;
    }

    /**下面这个函数是用来判断类型兼容的，第一个是声明的类型，第二个是赋值的类型**/
    private boolean typeCompatibility(String kindStatement,String kindAssignment){
        //TODO 在这做类型兼容的问题,暂时先实现这么多
        if(kindStatement.equals("real")){
            if(kindAssignment.equals("real")){
                return true;
            }else if(kindAssignment.equals("int")){
                return true;
            }else{
                return false;
            }
        }else if(kindStatement.equals("int")){
            if(kindAssignment.equals("int")){
                return true;
            }else{
                return false;
            }
        }else if(kindStatement.equals("string")){
            if(kindAssignment.equals("string")){
                return true;
            }
            else {
                return false;
            }
        }else if(kindStatement.equals("bool")){
            if(kindAssignment.equals("bool")){
                return true;
            }//TODO 这里想让bool兼容除了String之外的全部基本数据类型
            else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**那个int变量是为了区别哪个是标识符，其中1代表声明的那个是标识符，2代表等号后面是标识符，3代表全是标识符
     * kindAssignmentID是标识符的名字，这里的含义是指当赋值的时候用的是标识符的话，就将其名字传进来
     * */
    private boolean typeCompatibility(String kindStatementIDName,String kindAssignmentIDName,int IsID){
        switch (IsID){
            case 1:
                String kindStatement=findIDKindByName(kindStatementIDName);
                if(kindStatement==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatement,kindAssignmentIDName);
                }
            case 2:
                String kindAssignment=findIDKindByName(kindAssignmentIDName);
                if(kindAssignment==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatementIDName,kindAssignment);
                }
            case 3:
                String kindStatement1=findIDKindByName(kindStatementIDName);
                String kindAssignment1=findIDKindByName(kindAssignmentIDName);
                if(kindStatement1==null||kindAssignment1==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatement1,kindAssignment1);
                }
            default:
                return false;
        }

    }
    /**下面这个是用来根据变量的名字，也就是标识符的ID，来返回其数据类型*/
    private String findIDKindByName(String name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(name)){
                return ids.get(counter).getKind();
            }
        }
        return null;
    }

    private IDBase findIDByName(String name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(name)){
                return ids.get(counter);
            }
        }
        return null;
    }

    private boolean isExist(String id_name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(id_name)){
                return true;
            }
        }
        return false;
    }

    private void addError(String info){
        errorInfo=errorInfo+info+"\n";
        errorNum++;
    }
    private void addLog(String info){
        this.log=log+info+"\n";
    }

    public void outError(){
        System.out.println("错误的个数是"+errorNum);
        System.out.println(errorInfo);
    }
}
