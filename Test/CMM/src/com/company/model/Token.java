package com.company.model;

/**
 * 识别序列类
 *
 */
public class Token {
	// token类型
	private String kind;
	// token所在行
	private int line;
	// token所在列
	private int culomn;
	// token内容
	private String content;
	// 标识符类型
	private String idKind;
	// 错误Token标志,没用过
	public static final int ERROR = -1;
	// 初始化时默认Token没有类型
	public static final int NULL = 0;
	// if
	public static final int IF = 1;
	// else
	public static final int ELSE = 2;
	// while
	public static final int WHILE = 3;
	// read
	public static final int READ = 4;
	// write
	public static final int WRITE = 5;
	// int
	public static final int INT = 6;
	// real
	public static final int REAL = 7;
	// +
	public static final int PLUS = 8;
	// -
	public static final int MINUS = 9;
	// *
	public static final int MUL = 10;
	// /
	public static final int DIV = 11;
	// =
	public static final int ASSIGN = 12;
	// <
	public static final int LT = 13;
	// ==
	public static final int EQ = 14;
	// <>
	public static final int NEQ = 15;
	// (
	public static final int LPARENT = 16;
	// )
	public static final int RPARENT = 17;
	public static final int SEMI = 18;
	// {
	public static final int LBRACE = 19;
	// }
	public static final int RBRACE = 20;
	// [
	public static final int LBRACKET = 24;
	// ]
	public static final int RBRACKET = 25;
	// <=
	public static final int LET = 26;
	// >
	public static final int GT = 27;
	// >=
	public static final int GET = 28;
	// 标识符,由数字,字母或下划线组成,第一个字符不能是数字
	public static final int ID = 29;
	// int型字面值
	public static final int LITERAL_INT = 30;
	// real型字面值
	public static final int LITERAL_REAL = 31;
	// 逻辑表达式
	public static final int LOGIC_EXP = 32;
	// 多项式
	public static final int ADDTIVE_EXP = 33;
	// 项
	public static final int TERM_EXP = 34;
	private int type;
	/*
	 * 如果一个token需要值,则使用这个存储,比如ID,LITERAL_INT,LITERAL_REAL,LITERAL_BOOL
	 */
	private String value;
	private int lineNo;

	// 构造方法
	public Token(int l, int c, String k, String con) {
		this.line = l;  //行
		this.culomn = c; //列
		this.kind = k;   //Token的种类
		this.content = con;//内容
	}

	public Token(int lineNo) {
		this(0, lineNo);
	}

	public Token(int type, int lineNo) {
		this(type, null, lineNo);
	}

	public Token(int type, String value, int lineNo) {
		super();
		this.type = type;
		this.value = value;
		this.lineNo = lineNo;
	}

	// region getter和setter
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getCulomn() {
		return culomn;
	}

	public void setCulomn(int culomn) {
		this.culomn = culomn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIdKind() {
		return idKind;
	}

	public void setIdKind(String idKind) {
		this.idKind = idKind;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	//endregion

	public String toStringWithLine() {
		switch (type) {
		case IF:
			return "LINE." + this.lineNo + ": IF";
		case ELSE:
			return "LINE." + this.lineNo + ": ELSE";
		case WHILE:
			return "LINE." + this.lineNo + ": WHILE";
		case READ:
			return "LINE." + this.lineNo + ": READ";
		case WRITE:
			return "LINE." + this.lineNo + ": WRITE";
		case INT:
			return "LINE." + this.lineNo + ": INT";
		case REAL:
			return "LINE." + this.lineNo + ": REAL";
		case PLUS:
			return "LINE." + this.lineNo + ": +";// return "PLUS";
		case MINUS:
			return "LINE." + this.lineNo + ": -";// return "MINUS";
		case MUL:
			return "LINE." + this.lineNo + ": *";// return "MUL";
		case DIV:
			return "LINE." + this.lineNo + ": /";// return "DIV";
		case ASSIGN:
			return "LINE." + this.lineNo + ": =";// return "ASSIGN";
		case LT:
			return "LINE." + this.lineNo + ": <";// return "LT";
		case EQ:
			return "LINE." + this.lineNo + ": ==";// return "EQ";
		case NEQ:
			return "LINE." + this.lineNo + ": <>";// return "NEQ";
		case LPARENT:
			return "LINE." + this.lineNo + ": (";// return "LPARENT";
		case RPARENT:
			return "LINE." + this.lineNo + ": )";// return "RPARENT";
		case SEMI:
			return "LINE." + this.lineNo + ": ;";// return "SEMI";
		case LBRACE:
			return "LINE." + this.lineNo + ": {";// return "LBRACE";
		case RBRACE:
			return "LINE." + this.lineNo + ": }";// return "RBRACE";
		// case LCOM: return "LCOM";
		// case RCOM: return "RCOM";
		// case SCOM: return "SCOM";
		case LBRACKET:
			return "LINE." + this.lineNo + ": [";// return "LBRACKET";
		case RBRACKET:
			return "LINE." + this.lineNo + ": ]";// return "RBRACKET";
		case LET:
			return "LINE." + this.lineNo + ": <=";// return "LET";
		case GT:
			return "LINE." + this.lineNo + ": >";// return "GT";
		case GET:
			return "LINE." + this.lineNo + ": >=";// return "GET";
		case ID:// return "ID";
		case LITERAL_INT:// return "LITERAL_INT";
		case LITERAL_REAL:
			return "LINE." + this.lineNo + ": " + this.value;// return
																// "LITERAL_REAL";
		default:
			return "LINE." + this.lineNo + ": UNKNOWN";
		}
	}

	@Override
	public String toString() {
		switch (type) {
		case IF:
			return "IF";
		case ELSE:
			return "ELSE";
		case WHILE:
			return "WHILE";
		case READ:
			return "READ";
		case WRITE:
			return "WRITE";
		case INT:
			return "INT";
		case REAL:
			return "REAL";
		case PLUS:
			return "+";// return "PLUS";
		case MINUS:
			return "-";// return "MINUS";
		case MUL:
			return "*";// return "MUL";
		case DIV:
			return "/";// return "DIV";
		case ASSIGN:
			return "=";// return "ASSIGN";
		case LT:
			return "<";// return "LT";
		case EQ:
			return "==";// return "EQ";
		case NEQ:
			return "<>";// return "NEQ";
		case LPARENT:
			return "(";// return "LPARENT";
		case RPARENT:
			return ")";// return "RPARENT";
		case SEMI:
			return ";";// return "SEMI";
		case LBRACE:
			return "{";// return "LBRACE";
		case RBRACE:
			return "}";// return "RBRACE";
		// case LCOM: return "LCOM";
		// case RCOM: return "RCOM";
		// case SCOM: return "SCOM";
		case LBRACKET:
			return "[";// return "LBRACKET";
		case RBRACKET:
			return "]";// return "RBRACKET";
		case LET:
			return "<=";// return "LET";
		case GT:
			return ">";// return "GT";
		case GET:
			return ">=";// return "GET";
		case ID:// return "ID";
		case LITERAL_INT:// return "LITERAL_INT";
		case LITERAL_REAL:
			return "" + this.value;// return "LITERAL_REAL";
		default:
			return "UNKNOWN";
		}
	}

}