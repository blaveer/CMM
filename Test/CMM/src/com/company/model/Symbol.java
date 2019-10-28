package com.company.model;

/**
 * 符号表中的元素,符号表的组织形式就是一个linkdedList,但是作为list中元素的symbol自身也可以组成链表,但是这个链只在同名不同层的符号出现时使用
 * 不同名的符号存储在linkedlist中
 */
public class Symbol {
    public static final int TEMP = -1;
    public static final int SINGLE_INT = 0;
    public static final int SINGLE_REAL = 1;
    public static final int ARRAY_INT = 2;
    public static final int ARRAY_REAL = 3;
    //仅供value使用
    public static final int TRUE = 4;
    public static final int FALSE = 5;
    
    private String name;
    private int type;
    private Value value;
    private int level;
    private Symbol next;
    
    /**
     * type是ARRAY_*的时候务必要调用value的initArray方法来初始化数组
     */
    public Symbol(String name, int type, int level) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.next = null;
        this.value = new Value(type);
    }
    
    /**
     * 一定要是SINGLE_INT
     */
    public Symbol(String name, int type, int level, int value) {
        this(name, type, level);
        this.value.setInt(value);
    }
    
    /**
     * 一定要是SINGLE_REAL
     */
    public Symbol(String name, int type, int level, double value) {
        this(name, type, level);
        this.value.setReal(value);
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Value getValue() {
        return value;
    }
    public void setValue(Value value) {
        this.value = value;
    }
    public int getLevel() {
        return level;
    }

    /**
     * 获取下一个同名Symbol
     */
    public Symbol getNext() {
        return next;
    }

    /**
     * 设置下一个同名symbol
     */
    public void setNext(Symbol next) {
        this.next = next;
    }
}
