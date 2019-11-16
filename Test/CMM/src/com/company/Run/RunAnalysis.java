package com.company.Run;

import com.company.Tools.ID.ID;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

import static com.company.MyFrame.myFrame.read;
import static com.company.MyFrame.myFrame.write;

/**遗留待解决的问题，数组的下标越界检查，还有待发现的*/
public class RunAnalysis {
    private int errorNum=0;
    private String errorInfo="";
    private String log="";
    private TokenTree root;
    private ArrayList<ID> ids=new ArrayList<ID>();
    private String out="";
    private final int Max_Cir=10000;

    public RunAnalysis(TokenTree root){
        this.root=root;
    }


    public boolean run(){
        if(runEvery(root)){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean runEvery(TokenTree tempRoot){
        int start=ids.size();
        int end=ids.size();
        TokenTree temp=null;
        for(int counter=0;counter<tempRoot.getChildSize();counter++){
            temp=tempRoot.get(counter);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                if(!declare_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("read")){
                if(!read_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("write")){
                if(!write_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("assign")){
                if(!assign_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("while")){
                if(!while_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("for")){
                if(!for_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("if")){
                if(!if_run(temp)){
                    return false;
                }
            }
            else if(temp.getKind().equals("finish")&&temp.getContent().equals("finish")){
                System.out.println("运行结束");
                return true;
            }
            else{
                System.out.println("出现了不该出现的关键字");
                return false;
            }
        }
        end=ids.size();
        int counter=end-start;
        remove(start,counter);
        return true;
    }

    private boolean write_run(TokenTree temp_write){
        TokenTree temp=temp_write.get(0);
        ID id_temp=findIDByName(temp.getContent());
        boolean is_arr=id_temp.getIsArr();
        if(temp.hasChildren()){
            //这代表是输出的是数组的某一项
            int index=run_int_result(temp.get(0));
            if(index<0||index>=id_temp.getLength()){
                addError("数组溢出");
                return false;
            }
            String output=id_temp.get(index);
            write_output(output);
        }else{
            if(is_arr){
                String output=id_temp.getArr();
                write_output(output);
            }else{
                String output=id_temp.getContent();
                write_output(output);
            }
        }
        return true;
    }

    private boolean read_run(TokenTree tempRoot){
        TokenTree temp=tempRoot.get(0);
        ID id_temp=findIDByName(temp.getContent());
        String kind=id_temp.getKind();
//        if(id_temp==null){
//            addError("使用了未声明的标识符");
//            return false;
//        }
        System.out.println(temp.getContent());
        boolean isArr=id_temp.getIsArr();
        int index=0;
        if(temp.hasChildren()){
            index=run_int_result(temp.get(0));
            if(index<0||index>=id_temp.getLength()){
                return false;
            }
        }
        String input=read_input(temp.getContent());
        switch (kind){
            case "int":
                int input_int=0;
                try {
                    input_int=Integer.parseInt(input);
                }catch (Exception ex){
                    addError("输入的数据类型不是int");
                    return false;
                }
                if(isArr){
                    id_temp.set(String.valueOf(input_int),index);
                }
                else{
                    id_temp.setContent(String.valueOf(input_int));
                }
                return true;
            case "real":
                double input_real=0;
                try {
                    input_real=Double.parseDouble(input);
                }catch (Exception ex){
                    addError("输入的数据类型不是real");
                    return false;
                }
                if(isArr){
                    id_temp.set(String.valueOf(input_real),index);
                }
                else{
                    id_temp.setContent(String.valueOf(input_real));
                }
                return true;
            case "bool":
                boolean input_bool=false;
                try {
                    input_bool=Boolean.parseBoolean(input);
                }catch (Exception ex){
                    addError("输入的数据类型不是real");
                    return false;
                }
                if(isArr){
                    id_temp.set(String.valueOf(input_bool),index);
                }
                else{
                    id_temp.setContent(String.valueOf(input_bool));
                }
                return true;
            case "string":
                if(isArr){
                    id_temp.set(input,index);
                }
                else {
                    id_temp.setContent(input);
                }
                return true;
            default:return false;
        }

    }

    private boolean for_run(TokenTree temp){
        int counter=0;
        TokenTree init_front=temp.get(0);
        TokenTree check=temp.get(1);
        TokenTree init_back=temp.get(2);
        TokenTree for_main=temp.get(3);
        if(!assign_run(init_front.get(0))){
            return false;
        }
        boolean isContinue=run_bool_result(check.get(0));
        while(isContinue){
            counter++;
            if(counter>Max_Cir){
                addError("循环次数已经大于"+Max_Cir+"次");
                return false;
            }
            if(!runEvery(for_main)){
                return false;
            }
            if(!assign_run(init_back.get(0))){
                return false;
            }
            else{
                isContinue=run_bool_result(check.get(0));
            }
        }
        return true;
    }

    private boolean if_run(TokenTree temp){
        TokenTree if_check=temp.get(0);
        TokenTree if_main=temp.get(1);
        TokenTree else_main=null;
        if(temp.getChildSize()>2){
            else_main=temp.get(2);
        }
        boolean is_if=run_bool_result(if_check.get(0));
        if(is_if){
            return runEvery(if_main);
        }else{
            if(else_main!=null){
                return runEvery(else_main);
            }
        }
        return true;
    }

    private boolean while_run(TokenTree temp){
        int counter=0;
        TokenTree while_check=temp.get(0);
        TokenTree while_main=temp.get(1);
        boolean isContinue=run_bool_result(while_check.get(0));
        while(isContinue){
            counter++;
            if(counter>Max_Cir){
                addError("循环次数已经大于"+Max_Cir+"次");
                return false;
            }
            if(!runEvery(while_main)){
                return false;
            }
            isContinue=run_bool_result(while_check.get(0));
        }
        return true;
    }

    private boolean assign_run(TokenTree temp){
        TokenTree assign=temp.get(0);//这个是等号的那个节点
        TokenTree id_assign=assign.get(0);//这个是标识符
        TokenTree id_assign_init=assign.get(1);
        ID id_temp=findIDByName(id_assign.getContent());
        String kind=id_temp.getKind();
        /**对数组的某一项赋值*/
        if(id_assign.hasChildren()){
            TokenTree array_index=id_assign.get(0);
            int index=run_int_result(array_index);
            if(index<0||index>=id_temp.getLength()){
                addError("对于数组"+id_assign.getContent()+"的赋值数组下标溢出");
                return false;
            }
            String result=run_all_result(id_assign_init,kind);
            id_temp.set(result,index);
        }
        else{
            if(id_temp.getIsArr()){
                int length=id_assign_init.getChildSize();//这个获取到的是其真正赋值的数量
                if(length!=id_temp.getLength()){
                    addError("关于数组"+id_temp.getName()+"的整体赋值，赋值数量与定义的长度不符");
                    return false;
                }
                ArrayList<String> init_array=new ArrayList<String>();
                for(int counter=0;counter<length;counter++){
                    init_array.add(run_all_result(id_assign_init.get(counter),kind));
                }
                id_temp.setArr(init_array);
            }
            else {
                String result=run_all_result(id_assign_init,kind);
                id_temp.setContent(result);
            }
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
                    if(length<=0){
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
                    if(length<=0){
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

    /***
     * 关于这个第一个函数的参数，
     * 如果是运算符，那就运算，如果是标识符，再根据其有无子节点进行判断，如果是数字啥的，应该就这三种情况，
     * 当然，具体的实现就要放到其他三个函数里面了
     * 关于string 的赋值初始化的问题，一直没考虑，毕竟也不能进行运算*/
    private String run_all_result(TokenTree temp,String kind){
        switch (kind){
            case "int":
                return String.valueOf(run_int_result(temp));
            case "real":
                return String.valueOf(run_real_result(temp));
            case "bool":
                return String.valueOf(run_bool_result(temp));
            case "string":
                return run_string_result(temp);
            default:
                return null;
        }
    }

    private String run_string_result(TokenTree temp){
        if(temp.getKind().equals("标识符")){
            ID id_temp=findIDByName(temp.getContent());
            if(id_temp.getIsArr()){
                int index=run_int_result(temp.get(0));
                if(index<0||index>=id_temp.getLength()){
                    addError("对于数组"+id_temp.getName()+"的使用越界");
                    return null;
                }
                return id_temp.get(index);
            }
            else{
                return id_temp.getContent();
            }
        }
        else if(temp.getKind().equals("string")){
            return temp.getContent();
        }
        else {
            addError("语义分析还是有问题，不应该执行到这的");
            return null;
        }
    }

    private double run_real_result(TokenTree temp){
        if(temp.getKind().equals("运算符")){
            return real_arithmetic(String.valueOf(run_real_result(temp.get(0))),String.valueOf(run_real_result(temp.get(1))),temp.getContent());
        }
        else if(temp.getKind().equals("标识符")){
            ID id_temp=findIDByName(temp.getContent());
            if(id_temp.getIsArr()){
                int index=run_int_result(temp.get(0));
                if(index<0||index>=id_temp.getLength()){
                    addError("数组超过下标");
                    return 0;
                }
                return Double.valueOf(id_temp.get(index));
            }
            else{
                return Double.valueOf(id_temp.getContent());
            }
        }
        else if(temp.getKind().equals("real")){
            return Double.valueOf(temp.getContent());
        }
        else if(temp.getKind().equals("int")){
            return Double.valueOf(temp.getContent());
        }
        else{
            addError("错误的Token");
            return 0;
        }
    }

    private int run_int_result(TokenTree temp){
        if(temp.getKind().equals("运算符")){
            return int_arithmetic(String.valueOf(run_int_result(temp.get(0))),String.valueOf(run_int_result(temp.get(1))),temp.getContent());
        }
        else if(temp.getKind().equals("标识符")){
            ID id_temp=findIDByName(temp.getContent());
            if(id_temp.getIsArr()){
                int index=run_int_result(temp.get(0));
                if(index<0||index>=id_temp.getLength()){
                    addError("数组越界");
                    return 0;
                }
                return Integer.parseInt(id_temp.get(index));
            }
            return Integer.parseInt(id_temp.getContent());
        }
        else if(temp.getKind().equals("int")){
            return Integer.valueOf(temp.getContent());
        }
        else{
            addError("错误的Token");
            return 0;
        }
    }

    private boolean run_bool_result(TokenTree temp){
        if(temp.getContent().equals("&&")||temp.getContent().equals("||")){
            return log_arithmetic(String.valueOf(run_bool_result(temp.get(0))),String.valueOf(run_bool_result(temp.get(1))),temp.getContent());
        }
        else if(temp.getContent().equals("!")){
            return log_inverter_arithmetic(String.valueOf(run_bool_result(temp.get(0))));
        }
        else if(is_compare(temp.getContent())){
            return com_arithmetic(String.valueOf(run_real_result(temp.get(0))),String.valueOf(run_real_result(temp.get(1))),temp.getContent());
        }
        else if(temp.getKind().equals("标识符")){
            ID id_temp=findIDByName(temp.getContent());
            if(id_temp.getIsArr()){
                int index=run_int_result(temp.get(0));
                if(index<0||index>=id_temp.getLength()){
                    addError("数组越界");
                    return false;
                }
                return Boolean.parseBoolean(id_temp.get(index));
            }
            else{
                return Boolean.parseBoolean(id_temp.getContent());
            }
        }
        else if(temp.getKind().equals("bool")){
            return Boolean.parseBoolean(temp.getContent());
        }
        else {
            addError("有报错了？");
            return false;
        }
    }

    private boolean com_arithmetic(String com1,String com2,String op){
        double com_first=Double.parseDouble(com1);
        double com_second=Double.parseDouble(com2);
        switch (op){
            case ">":
                if(com_first>com_second){
                    return true;
                }
                else{
                    return false;
                }
            case "<":
                if(com_first<com_second){
                    return true;
                }
                else{
                    return false;
                }
            case "==":
                return is_equal(com_first,com_second);
            case "<>":
                return !is_equal(com_first,com_second);
            default:
                return false;
        }
    }

    private boolean is_equal(double d1,double d2){
        double r=d1-d2;
        if(r<0){
            r=-r;
        }
        if(r<0.0001){
            return true;
        }else {
            return false;
        }
    }

    private boolean is_compare(String op){
        if(op.equals("<")||op.equals(">")||op.equals("<>")||op.equals("==")){
            return true;
        }
        else {
            return false;
        }
    }

    private boolean log_inverter_arithmetic(String log){
        boolean log_inverter=Boolean.parseBoolean(log);
        return !log_inverter;
    }

    private boolean log_arithmetic(String log1,String log2,String op){
        boolean log_first=Boolean.parseBoolean(log1);
        boolean log_second=Boolean.parseBoolean(log2);
        switch (op){
            case "&&":
                return log_first&&log_second;
            case "||":
                return log_first||log_second;
            default:
                return false;
        }
    }

    private double real_arithmetic(String num1,String num2,String op){
        double num_first=Double.parseDouble(num1);
        double num_second=Double.parseDouble(num2);
        switch (op){
            case "*":
                return num_first*num_second;
            case "+":
                return num_first+num_second;
            case "-":
                return num_first-num_second;
            case "/":
                try {
                    return num_first/num_second;
                }catch (Exception ex){
                    addError("除法发生错误");
                    return 0;
                }
            default:
                addError("实数运算出现未识别的运算符");
                return 0;
        }
    }

    private int int_arithmetic(String num1,String num2,String op){
        int num_first=Integer.parseInt(num1);
        int num_second=Integer.parseInt(num2);
        switch (op){
            case "*":
                return num_first*num_second;
            case "+":
                return num_first+num_second;
            case "-":
                return num_first-num_second;
            case "/":
                try {
                    return num_first/num_second;
                }catch (Exception ex){
                    addError("除法发生错误");
                    return 0;
                }
            default:
                addError("整数数运算出现未识别的运算符");
                return 0;
        }
    }

    private ID findIDByName(String name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(name)){
                return ids.get(counter);
            }
        }
        return null;
    }

    private void remove(int start,int count){
        for(int i=0;i<count;i++){
            ids.remove(start);
        }
    }


    private void addError(String info){
        addLog(info);
        errorNum++;
        errorInfo=errorInfo+info+"\n";
    }
    private void addLog(String info){
        log=log+info+"\n";
    }

    /**下面这个函数是从某一个地方读一个字符串，具体待定*/
    public static String read_input(String name){
        return read(name);
    }
    public static void write_output(String output){
        write("\n"+output);
        System.out.println(output);
    }
    public String getErrorInfo(){
        return errorInfo;
    }
}
