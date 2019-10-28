package com.company.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class SymbolTable {
	/* 存放SymbolTableElement */
	private Vector<SymbolTableElement> symbolTableVactor = new Vector<SymbolTableElement>();
	private static final String TEMP_PREFIX = "*temp";

	private static SymbolTable symbolTable = new SymbolTable();
	private static LinkedList<Symbol> tempNames;

	private ArrayList<Symbol> symbolList;
	
	/**
	 * 根据索引查找SymbolTableElement对象
	 * 
	 * @param index
	 *            提供的索引
	 * @return 返回SymbolTableElement对象
	 */
	public SymbolTableElement get(int index) {
		return symbolTableVactor.get(index);
	}

	/**
	 * 根据SymbolTableElement对象的名字对所有作用域查找
	 * 
	 * @param name
	 *            SymbolTableElement名字
	 * @param level
	 *            SymbolTableElement作用域
	 * @return 如果存在,则返回SymbolTableElement对象;否则返回null
	 */
	public SymbolTableElement getAllLevel(String name, int level) {
		while (level > -1) {
			for (SymbolTableElement element : symbolTableVactor) {
				if (element.getName().equals(name) && element.getLevel() == level) {
					return element;
				}
			}
			level--;
		}
		return null;
	}

	/**
	 * 根据SymbolTableElement对象的名字对当前作用域查找
	 * 
	 * @param name
	 *            SymbolTableElement名字
	 * @param level
	 *            SymbolTableElement作用域
	 * @return 如果存在,则返回SymbolTableElement对象;否则返回null
	 */
	public SymbolTableElement getCurrentLevel(String name, int level) {
		for (SymbolTableElement element : symbolTableVactor) {
			if (element.getName().equals(name) && element.getLevel() == level) {
				return element;
			}
		}
		return null;
	}

	/**
	 * 向symbolTable中添加SymbolTableElement对象,放在末尾
	 * 
	 * @param element
	 *            要添加的元素
	 * @return 如果添加成功则返回true,否则返回false
	 */
	public boolean add(SymbolTableElement element) {
		return symbolTableVactor.add(element);
	}

	/**
	 * 在symbolTable中指定的索引处添加SymbolTableElement对象
	 * 
	 * @param index
	 *            制定的索引
	 * @param element
	 *            要添加的元素
	 */
	public void add(int index, SymbolTableElement element) {
		symbolTableVactor.add(index, element);
	}

	/**
	 * 从symbolTable中移除指定索引处的元素
	 * 
	 * @param index
	 *            指定的索引
	 */
	public void remove(int index) {
		symbolTableVactor.remove(index);
	}

	/**
	 * 从symbolTable中移除指定名字和作用域的元素
	 * 
	 * @param name
	 *            指定的名字
	 * @param level
	 *            指定的作用域
	 */
	public void remove(String name, int level) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getName().equals(name) && get(i).getLevel() == level) {
				remove(i);
				return;
			}
		}
	}

	/**
	 * 清空symbolTable中的元素,将其大小设为0
	 */
	public void removeAll() {
		symbolTableVactor.clear();
	}

	/**
	 * 当level减小时更新符号表,去除无用的元素
	 */
	public void update(int level) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getLevel() > level) {
				remove(i);
			}
		}
	}

	/**
	 * 判断是否包含指定的元素
	 * 
	 * @param element
	 *            指定的SymbolTableElement元素
	 * @return 如果包含则返回true,否则返回false
	 */
	public boolean contains(SymbolTableElement element) {
		return symbolTableVactor.contains(element);
	}

	/**
	 * 判断是否为空
	 * 
	 * @return 如果为空则返回true,否则返回false
	 */
	public boolean isEmpty() {
		return symbolTableVactor.isEmpty();
	}

	/**
	 * 计算元素个数
	 * 
	 * @return 返回对象中元素的个数
	 */
	public int size() {
		return symbolTableVactor.size();
	}

	public static SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void newTable() {
		symbolList = new ArrayList<Symbol>();
		tempNames = new LinkedList<Symbol>();
	}

	public void deleteTable() {
		if (symbolList != null) {
			symbolList.clear();
			symbolList = null;
		}
		if (tempNames != null) {
			tempNames.clear();
			tempNames = null;
		}
	}

	public void register(Symbol symbol) {
		for (int i = 0; i < symbolList.size(); i++) {
			if (symbolList.get(i).getName().equals(symbol.getName())) {
				if (symbolList.get(i).getLevel() < symbol.getLevel()) {
					symbol.setNext(symbolList.get(i));
					symbolList.set(i, symbol);
					return;
				} else {
				}
			}
		}
		symbolList.add(symbol);
	}

	public void deregister(int level) {
		for (int i = 0; i < symbolList.size(); i++) {
			if (symbolList.get(i).getLevel() == level) {
				symbolList.set(i, symbolList.get(i).getNext());
			}
		}
		for (int i = symbolList.size() - 1; i >= 0; i--) {
			if (symbolList.get(i) == null) {
				symbolList.remove(i);
			}
		}
	}

	public void setSymbolValue(String name, Value value) {
		getSymbol(name).setValue(value);
	}

	public void setSymbolValue(String name, int value, int index) {
		if (getSymbol(name).getValue().getArrayInt().length > index) {
			getSymbol(name).getValue().getArrayInt()[index] = value;
		} else {

		}

	}

	public void setSymbolValue(String name, double value, int index) {
		getSymbol(name).getValue().getArrayReal()[index] = value;
	}

	/**
	 * 返回Symbol中的类型
	 */
	public int getSymbolType(String name) {
		return getSymbol(name).getType();
	}

	/**
	 * 取单值用这个函数
	 */
	public Value getSymbolValue(String name) {
		return getSymbolValue(name, -1);
	}

	/**
	 * 取值用这个函数
	 */
	public Value getSymbolValue(String name, int index) {
		Symbol s = getSymbol(name);
		if (index == -1) {// 单值
			return s.getValue();
		} else {
			if (s.getValue().getArrayInt().length < index + 1) {

			}
			if (s.getType() == Symbol.ARRAY_INT) {
				Value rv = new Value(Symbol.SINGLE_INT);
				rv.setInt(s.getValue().getArrayInt()[index]);
				return rv;
			} else {
				Value rv = new Value(Symbol.SINGLE_REAL);
				rv.setReal(s.getValue().getArrayReal()[index]);
				return rv;
			}
		}
	}

	private Symbol getSymbol(String name) {
		for (Symbol s : symbolList) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		for (Symbol s : tempNames) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		if (name.startsWith(TEMP_PREFIX)) {
			Symbol s = new Symbol(name, Symbol.TEMP, -1);
			tempNames.add(s);
			return s;
		}
		return null;
	}

	/**
	 * 获取一个没有使用的临时符号名
	 */
	public Symbol getTempSymbol() {
		String temp = null;
		for (int i = 1;; i++) {
			temp = TEMP_PREFIX + i;
			boolean exist = false;
			for (Symbol s : tempNames) {
				if (s.getName().equals(temp)) {
					exist = true;
					break;
				}
			}
			for (Symbol s : symbolList) {
				if (s.getName().equals(temp)) {
					exist = true;
					break;
				}
			}
			if (exist) {
				continue;
			}
			Symbol s = new Symbol(temp, Symbol.TEMP, -1);
			tempNames.add(s);
			return s;
		}
	}

	/**
	 * 清空临时符号名
	 */
	public void clearTempNames() {
		tempNames.clear();
	}
}
