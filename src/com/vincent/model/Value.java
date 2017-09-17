package com.vincent.model;


/**
 * 用于存储符号表中符号对应的值,包括int real 以及数组符号对应的值
 */
public class Value {
    
    /**
     * 存储值对象的类型,常量存储在Symbol中
     */
    private int mType;
    
    private int mInt;
    private double mReal;
    private int[] mArrayInt;
    private double[] mArrayReal;
    
    /**
     * 创建一个type型值对象
     * @param type
     * @param value
     */
    public Value(int type) {
        this.mType = type;
    }
    
    /**
     * 存储boolean值用的Value对象
     * @param bool
     */
    public Value(boolean bool) {
        if (bool) {
            this.mType = Symbol.TRUE;
        } else {
            this.mType = Symbol.FALSE;
        }
    }
    
    public int getType() {
        return mType;
    }
    /**
     * 尽量不要中途改变type,除非你明确知道可能发生什么
     * @param mType
     */
    public void setType(int mType) {
        this.mType = mType;
    }
    public int getInt() {
        return mInt;
    }
    public void setInt(int mInt) {
        this.mInt = mInt;
    }
    public double getReal() {
        return mReal;
    }
    public void setReal(double mReal) {
        this.mReal = mReal;
    }
    public int[] getArrayInt() {
        return mArrayInt;
    }
    public void setArrayInt(int[] mArrayInt) {
        this.mArrayInt = mArrayInt;
    }
    public double[] getArrayReal() {
        return mArrayReal;
    }
    public void setArrayReal(double[] mArrayReal) {
        this.mArrayReal = mArrayReal;
    }
    
    public void initArray(int dim) {
        if (mType == Symbol.ARRAY_INT) {
            mArrayInt = new int[dim];
        } else {
            mArrayReal = new double[dim];
        }
    }
    
    public Value PLUS(Value value) {
        if (this.mType == Symbol.SINGLE_REAL) {
            Value rv = new Value(Symbol.SINGLE_REAL);
            if (value.mType == Symbol.SINGLE_INT) {
                rv.setReal(this.mReal + value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                rv.setReal(this.mReal + value.mReal);
                return rv;
            }
        } else if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                Value rv = new Value(Symbol.SINGLE_INT);
                rv.setInt(this.mInt + value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                Value rv = new Value(Symbol.SINGLE_REAL);
                rv.setReal(this.mInt + value.mReal);
                return rv;
            }
        }
		return value;
    }
    
    public Value MINUS(Value value){
        if (this.mType == Symbol.SINGLE_REAL) {
            Value rv = new Value(Symbol.SINGLE_REAL);
            if (value.mType == Symbol.SINGLE_INT) {
                rv.setReal(this.mReal - value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                rv.setReal(this.mReal - value.mReal);
                return rv;
            }
        } else if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                Value rv = new Value(Symbol.SINGLE_INT);
                rv.setInt(this.mInt - value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                Value rv = new Value(Symbol.SINGLE_REAL);
                rv.setReal(this.mInt - value.mReal);
                return rv;
            }
        }
		return value;
       
    }
    
    public Value MUL(Value value){
        if (this.mType == Symbol.SINGLE_REAL) {
            Value rv = new Value(Symbol.SINGLE_REAL);
            if (value.mType == Symbol.SINGLE_INT) {
                rv.setReal(this.mReal * value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                rv.setReal(this.mReal * value.mReal);
                return rv;
            }
        } else if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                Value rv = new Value(Symbol.SINGLE_INT);
                rv.setInt(this.mInt * value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                Value rv = new Value(Symbol.SINGLE_REAL);
                rv.setReal(this.mInt * value.mReal);
                return rv;
            }
        }
		return value;
       
    }
    
    public Value DIV(Value value) {
        if (this.mType == Symbol.SINGLE_REAL) {
            Value rv = new Value(Symbol.SINGLE_REAL);
            if (value.mType == Symbol.SINGLE_INT) {
                if (value.getInt() == 0) {
                   
                }
                rv.setReal(this.mReal / value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                if (value.getReal() == 0) {
                  
                }
                rv.setReal(this.mReal / value.mReal);
                return rv;
            }
        } else if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                if (value.getInt() == 0) {
                }
                Value rv = new Value(Symbol.SINGLE_INT);
                rv.setInt(this.mInt / value.mInt);
                return rv;
            } else if (value.mType == Symbol.SINGLE_REAL) {
                if (value.getReal() == 0) {
                }
                Value rv = new Value(Symbol.SINGLE_REAL);
                rv.setReal(this.mInt / value.mReal);
                return rv;
            }
        }
		return value;
    }
    
    public Value GT(Value value) {
        if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                return new Value(this.mInt > value.mInt);
            } else if (value.mType == Symbol.SINGLE_REAL) {
                return new Value(this.mInt > value.mReal);
            }
        } else if (this.mType == Symbol.SINGLE_REAL) {
            if (value.mType == Symbol.SINGLE_INT) {
                return new Value(this.mReal > value.mInt);
            } else if (value.mType == Symbol.SINGLE_REAL) {
                return new Value(this.mReal > value.mReal);
            }
        }
		return value;
    }
    
    public Value EQ(Value value) {
        if (this.mType == Symbol.SINGLE_INT) {
            if (value.mType == Symbol.SINGLE_INT) {
                return new Value(this.mInt == value.mInt);
            } else if (value.mType == Symbol.SINGLE_REAL) {
                return new Value(this.mInt == value.mReal);
            }
        } else if (this.mType == Symbol.SINGLE_REAL) {
            if (value.mType == Symbol.SINGLE_INT) {
                return new Value(this.mReal == value.mInt);
            } else if (value.mType == Symbol.SINGLE_REAL) {
                return new Value(this.mReal == value.mReal);
            }
        }
		return value;
    }
    
    public Value OR(Value value) {
        if (this.mType == Symbol.TRUE || value.mType == Symbol.TRUE) {
            return new Value(Symbol.TRUE);
        } else {
            return new Value(Symbol.FALSE);
        }
    }
    
    public Value GET(Value value) {
        return this.GT(value).OR(this.EQ(value));
    }
    
    public Value LT(Value value){
        return NOT(this.GET(value));
    }
    
    public Value LET(Value value){
        return NOT(this.GT(value));
    }
    
    public Value NEQ(Value value){
        return NOT(this.EQ(value));
    }
    
    public static Value NOT(Value value){
        if (value.mType == Symbol.TRUE) {
            return new Value(Symbol.FALSE);
        } else if (value.mType == Symbol.FALSE) {
            return new Value(Symbol.TRUE);
        } else if (value.mType == Symbol.SINGLE_INT) {
            Value rv = new Value(Symbol.SINGLE_INT);
            rv.setInt(value.mInt * -1);
            return rv;
        } else if (value.mType == Symbol.SINGLE_REAL) {
            Value rv = new Value(Symbol.SINGLE_REAL);
            rv.setReal(value.mReal * -1);
            return rv;
        }
		return value;
        
    }

    @Override
    public String toString() {
        switch (mType) {
        case Symbol.SINGLE_INT:
            return mInt + "";
        case Symbol.SINGLE_REAL:
            return mReal + "";
        case Symbol.TRUE:
            return "true";
        case Symbol.FALSE:
            return "false";
        default:
            return "array can't be write";
        }
    }

}
