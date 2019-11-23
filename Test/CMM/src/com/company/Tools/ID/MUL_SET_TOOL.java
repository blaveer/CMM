package com.company.Tools.ID;

public class MUL_SET_TOOL {
    int index;
    boolean run_suc;
    public MUL_SET_TOOL(int index,boolean run_suc){
        this.index=index;
        this.run_suc=run_suc;
    }

    public MUL_SET_TOOL(boolean run_suc){
        run_suc=false;
    }
    public int getIndex() {
        return index;
    }

    public boolean getRun_suc() {
        return run_suc;
    }
}
