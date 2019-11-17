package com.company.CMMSem;


import com.company.Tools.ID.ID;
import com.company.Tools.ID.IDBase;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

/**
 * 关于建立这个类的原因，本来是想在上个类中直接实现检查内容和执行的效果，
 * 后来发现难度过大，所以就在这单独的先进行一个语义分析
 * 其实挺后悔在语法分析的时候没有将TokenTree的位置传过来
 * return语句不支持在if_while_for中写
 * */
public class semanticAnalysis {
    private TokenTree root;/**用来继承其根节点*/
    private ArrayList<IDBase> ids=new ArrayList<>();/**用来动态的记录标识符的*/
    //private ArrayList<ID> idArrayList=new ArrayList<>();/**可以说这两个变量相辅相成，上面那个是用来监测是否有重复的标识符或者未声明巴拉巴拉，这个是用来记录每一个合法的标识符的*/
    private ArrayList<Integer> counter_id=new ArrayList<>();/**这个变量是记录在每个模块中增加的标识符的的数量的,为了能够实现变量的作用域，自己挖的坑，自己填吧*/
    private int errorNum=0;
    private int global_v=0;
    private String errorInfo="";
    private String log="";
    //private boolean can_b_c=false;
    public semanticAnalysis(TokenTree root){
        this.root=root;
        add_global_v(root);
        global_v=ids.size();
    }

    public void semantic(TokenTree tempRoot,int ia){
        TokenTree temp=null;
        for(int i=0;i<tempRoot.getChildSize();i++){
            temp=tempRoot.get(i);
            if(temp.getKind().equals("关键字")){
                /**这些是声明的全局变量，在初始话的时候已经检查过*/
                continue;
            }
            else if(temp.getKind().equals("标识符")){
                fun_sem(temp);
            }
            else{

            }
        }
    }

    private void fun_sem(TokenTree tempRoot){
        int start=ids.size();
        String kind=tempRoot.get(0).getContent();//函数的返回值类型
        TokenTree par_list=tempRoot.get(1);
        IDBase id_temp=findIDByName(tempRoot.getContent());
        for(int i=0;i<par_list.getChildSize();i++){
            TokenTree par_one=par_list.get(i);
            String par_kind=par_one.getContent();
            String par_id=par_one.get(0).getContent();
            if(isExist(par_id)){
                addError("关于函数的参数"+par_id+"已经被声明为全局变量");
                continue;
            }
            /**在这是一个一个的将函数的参数存进去，所以出现当上面的函数调用下面的函数时候，会报错，因为此时下面的函数的参数还为0**/
            id_temp.addFun_par(new IDBase(par_kind,par_id,1));/**关于这个1是随便写的，只是不想再写一个构造函数了*/
            ids.add(new IDBase(par_kind,par_id,1,true));//同时将这些变量加入到总的变量表中去
        }
        TokenTree fun_main=tempRoot.get(2);
        new_semantic(fun_main,kind);
        int end=ids.size();
        int counter=end-start;
        removeID(start,counter);
    }

    private void new_semantic(TokenTree tempRoot,String kind){
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
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("call")){
                fun_call(temp,null);
            }
            else if(temp.getContent().equals("return")){
                if(!express_type_check(temp.get(0),kind)){
                    addError("函数返回值类型不匹配,不是"+kind);
                }
                return;
            }
            else if(temp.getKind().equals("finish")&&temp.getContent().equals("finish")){
                System.out.println("语义分析结束");
                break;
            }
            else{
                addError("出现了不该出现的关键字");
                System.out.println("出现了不该出现的关键字");
            }
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

    public void semantic(TokenTree tempRoot,boolean can_b_c){
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
            else if(temp.getContent().equals("break")||temp.getContent().equals("continue")) {
                if (can_b_c) {
                    return;
                }
                else{
                    addError("这里不该有break或者continue");
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("call")){
                fun_call(temp,null);
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

    private void add_global_v(TokenTree root){
        for(int i=0;i<root.getChildSize();i++){
            TokenTree temp=root.get(i);
            if(temp.getContent().equals("declare")){
                declare_sem(temp);
            }
            else if(temp.getKind().equals("标识符")){
                String name=temp.getContent();
                if(isExist(name)){
                    addError("标识符"+name+"已经存在");
                    continue;
                }
                String kind=temp.get(0).getContent();
                ids.add(new IDBase(kind,name,true));
            }
            else if(temp.getKind().equals("finish")){

            }
            else{
                addError("语法分析还是有问题");
            }
        }
    }

    private void fun_call(TokenTree tempRoot,String kind){
        String name=tempRoot.get(0).getContent();
        IDBase id_temp=findIDByName(name);
        if(id_temp==null){
            addError("调用了并不存在的函数"+name);
            return;
        }
        int goal_par_num=id_temp.getFun_par().size();
        int real_par_num=tempRoot.getChildSize()-1;/**建一是因为第一个子节点是调用的函数的名字*/
        if(goal_par_num!=real_par_num){
            addError("关于函数"+name+"的调用参数数量不符");
            return;
        }
        for(int i=0;i<goal_par_num;i++){
            TokenTree temp_express=tempRoot.get(i+1);
            if(!express_type_check(temp_express,id_temp.getFun_par().get(i).getKind())){
                addError("关于函数"+name+"的调用第"+(i+1)+"个参数数据类型不兼容");
            }
        }
        if(kind!=null){
            if(!typeCompatibility(kind,id_temp.getKind())){
                addError("在调用函数"+id_temp.getName()+"时候类型不兼容");
            }
        }
    }

    private void read_sem(TokenTree temp){
        TokenTree temp_read=temp.get(0);
        if(temp_read.getKind().equals("标识符")){
            if(isExist(temp_read.getContent())){
                IDBase id=findIDByName(temp_read.getContent());
                if(id.getIsFun()){
                    addError("该标识符被标识为函数了");
                    return;
                }
                if(temp_read.hasChildren()){
                    if(!id.getIsInit()){
                        addError("数组"+temp_read.getContent()+"还未整体初始化");
                        return;
                    }
                    if(!id.getIsArr()){
                        addError("把非数组变量"+temp_read.getContent()+"当作数组使用了");
                        return;
                    }
                    if(express_type_check(temp_read.get(0),"int")){
                        addError("数组下标不是整数");
                        return;
                    }
                }
                else if(id.getIsArr()){
                    addError("暂不支持对数组的整体读入");
                    return;
                }
                else{
                    id.setIsInit(true);
                }
            }
            else{
                addError("使用了未声明的标识符"+temp_read.getContent());
            }
        }
        else{
            addError("read函数的参数只能是标识符");
        }
    }

    private void write_sem(TokenTree temp){
        TokenTree write_temp=temp.get(0);
        if(write_temp.getKind().equals("标识符")){
            IDBase id=findIDByName(write_temp.getContent());
            if(id==null){
                addError("使用了未声明的的标识符"+id.getName());
            }
            else if(!id.getIsInit()){
                addError("使用了未初始化的标识符"+id.getName());
            }
        }
        else{
            addError("暂不支持除标识符之外的其他形式的输出");
        }
    }

    private void if_sem(TokenTree temp){
        TokenTree if_check=temp.get(0);
        TokenTree if_main=temp.get(1);
        TokenTree else_main=null;
        if(temp.getChildSize()>2){
            else_main=temp.get(2);
        }
        check_sem(if_check.get(0));
        semantic(if_main,true);
        if(else_main!=null){
            semantic(else_main,true);
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
        semantic(for_main,true);

    }

    private void while_sem(TokenTree temp){
        //只要语法分析没问题，就两个子节点
        TokenTree while_check=temp.get(0);
        TokenTree while_main=temp.get(1);
        check_sem(while_check.get(0));
        semantic(while_main,true);
    }

    private void assign_sem(TokenTree temp){
        TokenTree assign=temp.get(0);//这个是等号的那个节点
        TokenTree id_assign=assign.get(0);//这个是标识符
        String kind="";

        //IDBase id_temp_assign=findIDBaseByName(id_assign.getContent());
        if(isExist(id_assign.getContent())){
            kind=findIDKindByName(id_assign.getContent());
        }
        else{
            addError("使用了未声明的变量"+id_assign.getContent());
            return;
        }
        /**对数组的某一项赋值*/
        if(id_assign.hasChildren()){
            //TODO 数组是由先整体赋过值之后，才能个别再赋值
            //TODO 数组应当先整体赋值
            IDBase id_temp_assign=findIDByName(id_assign.getContent());
            if(!id_temp_assign.getIsArr()){
                addError("把非数组变量"+id_assign.getContent()+"当作数组变量赋值了");
                return;
            }
            if(!id_temp_assign.getIsInit()){
                addError("对于数组变量"+id_assign.getContent()+"只有对其整体赋值之后才能个别赋值");
                return;
            }
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
                /**这里应该用那个函数的*/
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
            else if(id_assign_array_length.getContent().equals("call")&&id_assign_array_length.getKind().equals("关键字")){
                IDBase id_temp=findIDByName(id_assign_array_length.get(0).getContent());
                if(!id_temp.getKind().equals("int")){
                    addError("使用了返回值不是int的函数"+id_assign_array_length.get(0).getContent()+"作为数组下标");
                }
            }
            TokenTree id_assign_array_one_init=assign.get(1);//这个是等号后面的
            if(id_assign_array_one_init.getKind().equals("标识符")){
                //if(useID(id_assign_array_one_init.getContent(),kind)){ TODO
                if(useID(id_assign_array_one_init,kind)){
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
            else if(id_assign_array_one_init.getContent().equals("call")&&id_assign_array_one_init.getKind().equals("关键字")){
                IDBase id_temp=findIDByName(id_assign_array_one_init.get(0).getContent());
                if(!id_temp.getKind().equals(kind)){
                    addError("使用了返回值不符合的函数"+id_assign_array_one_init.get(0).getContent()+"作为赋值");
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
                        //if(useID(id_init.getContent(),kind)){
                        if(useID(id_init,kind)){
                            //TODO 这里应当添加用数组的某一项为其赋值的情况，包括声明的那个地方也应当改一下，15日凌晨写，望白天能改
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
                    else if(id_init.getKind().equals("关键字")&&id_init.getContent().equals("call")){
                        fun_call(id_init,id_temp.getKind());
                        System.out.println("kjnknkbnkj");
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
                if(one_id.getContent().equals("main")){
                    addError("变量不能声明未main");
                    continue;
                }
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
                            ids.add(new IDBase(kind,one_id.getContent(),1,true));
                        }
                        else{
                            addError("关于"+one_id.getContent()+"的初始化类型不兼容");
                        }
                    }
                    else if(one_init.getContent().equals("call")){
                        fun_call(one_init,kind);
                    }
                    else if(one_init.getKind().equals("标识符")){
                        //if(useID(one_init.getContent(),kind)){ //TODO
                        if(useID(one_init,kind)){
                            ids.add(new IDBase(kind,one_id.getContent(),1,true));
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
                            ids.add(new IDBase(kind,one_id.getContent(),1,true));
                        }
                    }
                }
            }/**只声明了*/
            else if(one_declare.getKind().equals("标识符")){
                if(one_declare.getContent().equals("main")){
                    addError("变量不能声明为main");
                    continue;
                }
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
            //if(useID(temp.getContent(),"bool")){
            if(useID(temp,"bool")){
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
        if(!tempToot.getContent().equals("AllArrayInit")){
            return false;
        }
        for(int counter=0;counter<tempToot.getChildSize();counter++){
            if(!express_type_check(tempToot.get(counter),kind)){
                return false;
            }
        }
        return true;
    }

    private boolean express_type_check(TokenTree express,String kind){
        switch (kind){
            case "int":
                return type_int_check(express);
            case "real":
                return type_real_check(express);
            case "bool":
                return type_bool_check(express);
            case "string":
                return type_string_check(express);
            case "void":
                return type_void_check(express);
            default:return false;
        }
    }

    private boolean type_void_check(TokenTree express_void){
        if(express_void.getContent().equals("void")){
            return true;
        }
        return false;
    }

    private boolean type_string_check(TokenTree express_string){
        if(express_string.getKind().equals("string")){
            return true;
        }
        else if(express_string.getKind().equals("标识符")){
            if(useID(express_string,"string")){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    private boolean type_bool_check(TokenTree express_bool){
        if(isLogic(express_bool)){
            if(express_bool.getContent().equals("&&")||express_bool.getContent().equals("||")){
                return (express_type_check(express_bool.get(0),"bool")&&express_type_check(express_bool.get(1),"bool"));
            }
            else if(express_bool.getContent().equals("!")){
                return express_type_check(express_bool.get(0),"bool");
            }
            else{
                return (express_type_check(express_bool.get(0),"real")&&express_type_check(express_bool.get(1),"real"));
            }
        }
        else if(express_bool.getKind().equals("标识符")){
            //if(typeCompatibility("bool",findIDKindByName(express_bool.getContent()))){
            //if(useID(express_bool.getContent(),"bool")){
            if(useID(express_bool,"bool")){
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
                //if(!useID(express_real.getContent(),"real")){
                if(!useID(express_real,"real")){
                    //addError("标识符"+express_int.getContent()+"不存在");
                    return false;
                }
                if(express_type_check(express_real.get(0),"int")){
                    addLog("asasas");/**????????这是啥玩意**/
                }
                else{
                    return false;
                }
            }
            else{
                //if(!useID(express_real.getContent(),"real")){
                if(!useID(express_real,"real")){
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
                //if(!useID(express_int.getContent(),"int")){
                if(!useID(express_int,"int")){
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
                //if(!useID(express_int.getContent(),"int")){
                if(!useID(express_int,"int")){
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


    /**
     * 关于这个函数，是因为发现比如int a=b[1]
     * 这样的赋值方式没有检查1这个东西是否符合int类型，所以单独添加了这个函数，
     * 再添加后更改的函数是type检查中int2处，real两处，bool一处，在赋值和声明函数中也都有，check_sem一处
     * 如有报错，待解决
     * */
    private boolean useID(TokenTree temp,String kind){
        String name=temp.getContent();
        if(useID(name,kind)){
            IDBase temp_id=findIDByName(name);
            boolean isArr=temp_id.getIsArr();
            if(temp.hasChildren()){
                if(!isArr){
                    addError("把非数组变量"+temp_id.getName()+"当作数组变量使用");
                    return false;
                }
                if(type_int_check(temp.get(0))){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return true;
            }
        }else{
            return false;
        }
    }
    /**其中name是使用的标识符的名字，kind是希望的类型*/
    private boolean useID(String name,String kind){
        if(isExist(name)){
            IDBase temp_id=findIDByName(name);
            String kind_temp_id=temp_id.getKind();
            boolean is_init=temp_id.getIsInit();
            if(!is_init){
                addError("使用了未初始化的标识符"+name);
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
        int m=0;
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

    public int getErrorNum(){
        return errorNum;
    }
    public String getErrorInfo(){
        return errorInfo;
    }
}
