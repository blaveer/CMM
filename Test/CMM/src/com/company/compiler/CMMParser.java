package com.company.compiler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import com.company.model.ConstVar;
import com.company.model.Token;
import com.company.model.TokenList;
import com.company.model.TreeNode;


/**
 * CMM语法分析器
 * 
 */
public class CMMParser {
    //region 过程中用到的一些变量
	// 词法分析得到的tokens向量
	private ArrayList<Token> tokens;
	// 标记当前token的游标
	private int index = 0;
	// 存放当前token的值
	private Token currentToken = null;
	// 错误个数
	private int errorNum = 0;
	// 错误信息
	private String errorInfo = "";
	// 语法分析根结点
	private static TreeNode root;
	//TreeNode List
	private static LinkedList<TokenList> treeNodeList;
	//下面这个初始化，放在构造函数中整个代码的就够会更好
	private static Token current = null;
	//token迭代器
	private static ListIterator<Token> iterator = null;
	//关于ListIterator的一些理解：①课双向移动②可结合next插入元素
	//https://blog.csdn.net/weixin_39241397/article/details/79687789 一个博客
    //endregion


	public CMMParser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		if (tokens.size() != 0)
			currentToken = tokens.get(0);
	}
	//region getter 和setter
	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	//endregion

	/**
	 * 语法分析主方法
	 * 
	 * @return TreeNode
	 */
	public TreeNode execute() {
		root = new TreeNode("PROGRAM");
		for (; index < tokens.size();) {
			root.add(statement());
		}
		return root;
	}

	/**
	 * 取出tokens中的下一个token
	 * 
	 */
	private void nextToken() {
		index++;
		if (index > tokens.size() - 1) {
			currentToken = null;
			if (index > tokens.size())
				index--;
			return;
		}
		currentToken = tokens.get(index);
	}

	/**
	 * 出错处理函数
	 * 
	 * @param error 出错信息
	 */
	private void error(String error) {
		String line = "    ERROR:第 ";
		Token previous = tokens.get(index - 1);
		if (currentToken != null
				&& currentToken.getLine() == previous.getLine()) {
			line += currentToken.getLine() + " 行,第 " + currentToken.getCulomn()
					+ " 列：";
		} else
			line += previous.getLine() + " 行,第 " + previous.getCulomn() + " 列：";
		errorInfo += line + error;
		errorNum++;
	}

	//region 正真语法分析执行的函数
	/**
	 * statement: if_stm | while_stm | read_stm | write_stm | assign_stm |
	 * declare_stm | for_stm;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode statement() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		// 赋值语句
		if (currentToken != null && currentToken.getKind().equals("标识符")) {
			tempNode = assign_stm(false);
		}
		// 声明语句
		//有整型、实数、布尔、字符串
		else if (currentToken != null
				&& (currentToken.getContent().equals(ConstVar.INT)
						|| currentToken.getContent().equals(ConstVar.REAL) || currentToken
						.getContent().equals(ConstVar.BOOL))
				|| currentToken.getContent().equals(ConstVar.STRING)) {
			tempNode = declare_stm();
		}
		// For循环语句
		else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.FOR)) {
			tempNode = for_stm();
		}
		// If条件语句
		else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.IF)) {
			tempNode = if_stm();
		}
		// While循环语句
		else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.WHILE)) {
			tempNode = while_stm();
		}
		// read语句
		else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.READ)) {
			TreeNode readNode = new TreeNode("关键字", ConstVar.READ, currentToken
					.getLine());
			readNode.add(read_stm());
			tempNode = readNode;
		}
		// write语句
		else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.WRITE)) {
			TreeNode writeNode = new TreeNode("关键字", ConstVar.WRITE,
					currentToken.getLine());
			writeNode.add(write_stm());
			tempNode = writeNode;
		}
		// 出错处理
		else {
			String error = " 语句以错误的token开始" + "\n";
			error(error);
			tempNode = new TreeNode(ConstVar.ERROR + "语句以错误的token开始");
			nextToken();
		}
		return tempNode;
	}

	/**
	 * for_stm :FOR LPAREN (assign_stm) SEMICOLON condition SEMICOLON assign_stm
	 * RPAREN LBRACE statement RBRACE;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode for_stm() {
		// 是否有大括号,默认为true
		boolean hasBrace = true;
		// if函数返回结点的根结点
		TreeNode forNode = new TreeNode("关键字", "for", currentToken.getLine());
		nextToken();
		// 匹配左括号(
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) {
			nextToken();
		} else { // 报错
			String error = " for循环语句缺少左括号\"(\"" + "\n";
			error(error);
			forNode.add(new TreeNode(ConstVar.ERROR + "for循环语句缺少左括号\"(\""));
		}
		// initialization
		TreeNode initializationNode = new TreeNode("initialization",
				"Initialization", currentToken.getLine());
		initializationNode.add(assign_stm(true));
		forNode.add(initializationNode);
		// 匹配分号;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
			nextToken();
		} else {
			String error = " for循环语句缺少分号\";\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "for循环语句缺少分号\";\"");
		}
		// condition
		TreeNode conditionNode = new TreeNode("condition", "Condition",
				currentToken.getLine());
		conditionNode.add(condition());
		forNode.add(conditionNode);
		// 匹配分号;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
			nextToken();
		} else {
			String error = " for循环语句缺少分号\";\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "for循环语句缺少分号\";\"");
		}
		// change
		TreeNode changeNode = new TreeNode("change", "Change", currentToken
				.getLine());
		changeNode.add(assign_stm(true));
		forNode.add(changeNode);
		// 匹配右括号)
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RPAREN)) {
			nextToken();
		} else { // 报错
			String error = " if条件语句缺少右括号\")\"" + "\n";
			error(error);
			forNode.add(new TreeNode(ConstVar.ERROR + "if条件语句缺少右括号\")\""));
		}
		// 匹配左大括号{
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LBRACE)) {
			nextToken();
		} else {
			hasBrace = false;
		}
		// statement
		TreeNode statementNode = new TreeNode("statement", "Statements",
				currentToken.getLine());
		forNode.add(statementNode);
		if(hasBrace) {
			while (currentToken != null) {
				if (!currentToken.getContent().equals(ConstVar.RBRACE))
					statementNode.add(statement());
				else if (statementNode.getChildCount() == 0) {
					forNode.remove(forNode.getChildCount() - 1);
					statementNode.setContent("EmptyStm");
					forNode.add(statementNode);
					break;
				} else {
					break;
				}
			}
			// 匹配右大括号}
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.RBRACE)) {
				nextToken();
			} else { // 报错
				String error = " if条件语句缺少右大括号\"}\"" + "\n";
				error(error);
				forNode.add(new TreeNode(ConstVar.ERROR + "if条件语句缺少右大括号\"}\""));
			}
		} else {
			statementNode.add(statement());
		}
		return forNode;
	}

	 /**
     * 获取TreeNode List 
     * @param tokenList
     * @return
     */
    public static LinkedList<TokenList> getTreeNodeList(LinkedList<Token> tokenList){
        treeNodeList = new LinkedList<TokenList>();
        iterator = tokenList.listIterator();
        while (iterator.hasNext()) {
            treeNodeList.add(getStmt());
        }
        return treeNodeList;
    }
    

	
	/**
	 * if_stm: IF LPAREN condition RPAREN LBRACE statement RBRACE (ELSE LBRACE
	 * statement RBRACE)?;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode if_stm() {
		// if语句是否有大括号,默认为true
		boolean hasIfBrace = true;
		// else语句是否有大括号,默认为true
		boolean hasElseBrace = true;
		// if函数返回结点的根结点
		TreeNode ifNode = new TreeNode("关键字", "if", currentToken.getLine());
		nextToken();
		// 匹配左括号(
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) {
			nextToken();
		} else { // 报错
			String error = " if条件语句缺少左括号\"(\"" + "\n";
			error(error);
			ifNode.add(new TreeNode(ConstVar.ERROR + "if条件语句缺少左括号\"(\""));
		}
		// condition
		TreeNode conditionNode = new TreeNode("condition", "Condition",
				currentToken.getLine());
		ifNode.add(conditionNode);
		conditionNode.add(condition());
		// 匹配右括号)
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RPAREN)) {
			nextToken();
		} else { // 报错
			String error = " if条件语句缺少右括号\")\"" + "\n";
			error(error);
			ifNode.add(new TreeNode(ConstVar.ERROR + "if条件语句缺少右括号\")\""));
		}
		// 匹配左大括号{
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LBRACE)) {
			nextToken();
		} else {
			hasIfBrace = false;
		}
		// statement
		TreeNode statementNode = new TreeNode("statement", "Statements",
				currentToken.getLine());
		ifNode.add(statementNode);
		if (hasIfBrace) {
			while (currentToken != null) {
				if (!currentToken.getContent().equals(ConstVar.RBRACE))
					statementNode.add(statement());
				else if (statementNode.getChildCount() == 0) {
					ifNode.remove(ifNode.getChildCount() - 1);
					statementNode.setContent("EmptyStm");
					ifNode.add(statementNode);
					break;
				} else {
					break;
				}
			}
			// 匹配右大括号}
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.RBRACE)) {
				nextToken();
			} else { // 报错
				String error = " if条件语句缺少右大括号\"}\"" + "\n";
				error(error);
				ifNode.add(new TreeNode(ConstVar.ERROR + "if条件语句缺少右大括号\"}\""));
			}
		} else {
			if (currentToken != null)
				statementNode.add(statement());
		}
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.ELSE)) {
			TreeNode elseNode = new TreeNode("关键字", ConstVar.ELSE, currentToken
					.getLine());
			ifNode.add(elseNode);
			nextToken();
			// 匹配左大括号{
			if (currentToken.getContent().equals(ConstVar.LBRACE)) {
				nextToken();
			} else {
				hasElseBrace = false;
			}
			if (hasElseBrace) {
				// statement
				while (currentToken != null
						&& !currentToken.getContent().equals(ConstVar.RBRACE)) {
					elseNode.add(statement());
				}
				// 匹配右大括号}
				if (currentToken != null
						&& currentToken.getContent().equals(ConstVar.RBRACE)) {
					nextToken();
				} else { // 报错
					String error = " else语句缺少右大括号\"}\"" + "\n";
					error(error);
					elseNode.add(new TreeNode(ConstVar.ERROR
							+ "else语句缺少右大括号\"}\""));
				}
			} else {
				if (currentToken != null)
				elseNode.add(statement());
			}
		}
		return ifNode;
	}

	/**
	 * while_stm: WHILE LPAREN condition RPAREN LBRACE statement RBRACE;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode while_stm() {
		// 是否有大括号,默认为true
		boolean hasBrace = true;
		// while函数返回结点的根结点
		TreeNode whileNode = new TreeNode("关键字", ConstVar.WHILE, currentToken
				.getLine());
		nextToken();
		// 匹配左括号(
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) {
			nextToken();
		} else { // 报错
			String error = " while循环缺少左括号\"(\"" + "\n";
			error(error);
			whileNode.add(new TreeNode(ConstVar.ERROR + "while循环缺少左括号\"(\""));
		}
		// condition
		TreeNode conditionNode = new TreeNode("condition", "Condition",
				currentToken.getLine());
		whileNode.add(conditionNode);
		conditionNode.add(condition());
		// 匹配右括号)
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RPAREN)) {
			nextToken();
		} else { // 报错
			String error = " while循环缺少右括号\")\"" + "\n";
			error(error);
			whileNode.add(new TreeNode(ConstVar.ERROR + "while循环缺少右括号\")\""));
		}
		// 匹配左大括号{
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LBRACE)) {
			nextToken();
		} else {
			hasBrace = false;
		}
		// statement
		TreeNode statementNode = new TreeNode("statement", "Statements",
				currentToken.getLine());
		whileNode.add(statementNode);
		if(hasBrace) {
		while (currentToken != null
				&& !currentToken.getContent().equals(ConstVar.RBRACE)) {
			if (!currentToken.getContent().equals(ConstVar.RBRACE))
				statementNode.add(statement());
			else if (statementNode.getChildCount() == 0) {
				whileNode.remove(whileNode.getChildCount() - 1);
				statementNode.setContent("EmptyStm");
				whileNode.add(statementNode);
				break;
			} else {
				break;
			}
		}
		// 匹配右大括号}
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RBRACE)) {
			nextToken();
		} else { // 报错
			String error = " while循环缺少右大括号\"}\"" + "\n";
			error(error);
			whileNode.add(new TreeNode(ConstVar.ERROR + "while循环缺少右大括号\"}\""));
		}
		} else {
			if(currentToken != null)
				statementNode.add(statement());
		}
		return whileNode;
	}

	/**
	 * read_stm: READ LPAREN ID RPAREN SEMICOLON;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode read_stm() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		nextToken();
		// 匹配左括号(
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) {
			nextToken();
		} else {
			String error = " read语句缺少左括号\"(\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "read语句缺少左括号\"(\"");
		}
		// 匹配标识符
		if (currentToken != null && currentToken.getKind().equals("标识符")) {
			tempNode = new TreeNode("标识符", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
			// 判断是否是为数组赋值
			//TODO 这个功能实在可以省掉，等看完代码去掉这个功能，食之无味，弃之不惜
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.LBRACKET)) {
				tempNode.add(array());
			}
		} else {
			String error = " read语句左括号后不是标识符" + "\n";
			error(error);
			nextToken();
			return new TreeNode(ConstVar.ERROR + "read语句左括号后不是标识符");
		}
		// 匹配右括号)
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RPAREN)) {
			nextToken();
		} else {
			String error = " read语句缺少右括号\")\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "read语句缺少右括号\")\"");
		}
		// 匹配分号;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
			nextToken();
		} else {
			String error = " read语句缺少分号\";\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "read语句缺少分号\";\"");
		}
		return tempNode;
	}

	/**
	 * write_stm: WRITE LPAREN expression RPAREN SEMICOLON;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode write_stm() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		nextToken();
		// 匹配左括号(
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) {
			nextToken();
		} else {
			String error = " write语句缺少左括号\"(\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "write语句缺少左括号\"(\"");
		}
		// 调用expression函数匹配表达式
		tempNode = expression();
		// 匹配右括号)
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RPAREN)) {
			nextToken();
		} else {
			String error = " write语句缺少右括号\")\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "write语句缺少右括号\")\"");
		}
		// 匹配分号;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
			nextToken();
		} else {
			String error = " write语句缺少分号\";\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "write语句缺少分号\";\"");
		}
		return tempNode;
	}

	/**
	 * assign_stm: (ID | ID array) ASSIGN expression SEMICOLON;
	 * 
	 * @param isFor  是否是在for循环中调用
	 * @return TreeNode
	 */
	//赋值语句的调用，
	private final TreeNode assign_stm(boolean isFor) {
		// assign函数返回结点的根结点
		TreeNode assignNode = new TreeNode("运算符", ConstVar.ASSIGN, currentToken
				.getLine());//运算符，等号，行数
		TreeNode idNode = new TreeNode("标识符", currentToken.getContent(),//content是标识符的内容
				currentToken.getLine());
		assignNode.add(idNode);
		nextToken();//更新了index和currentToken两个值
		//上面只是往队列中加入了一个标识符

		// 判断是否是为数组赋值
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LBRACKET)) {
			idNode.add(array());
		}

		// 匹配赋值符号=
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.ASSIGN)) {
			nextToken();
		} else { // 报错
			String error = " 赋值语句缺少\"=\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "赋值语句缺少\"=\"");
		}

		// expression
		assignNode.add(condition());
		// 如果不是在for循环语句中调用声明语句,则匹配分号
		if (!isFor) {
			// 匹配分号;
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
				nextToken();
			} else { // 报错
				String error = " 赋值语句缺少分号\";\"" + "\n";
				error(error);
				assignNode.add(new TreeNode(ConstVar.ERROR + "赋值语句缺少分号\";\""));
			}
		}
		return assignNode;
	}

	/**
	 * declare_stm: (INT | REAL | BOOL | STRING) declare_aid(COMMA declare_aid)*
	 * SEMICOLON;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode declare_stm() {
		TreeNode declareNode = new TreeNode("关键字", currentToken.getContent(),
				currentToken.getLine());
		nextToken();
		// declare_aid
		declareNode = declare_aid(declareNode);
		// 处理同时声明多个变量的情况
		String next = null;
		while (currentToken != null) {
			next = currentToken.getContent();
			if (next.equals(ConstVar.COMMA)) {
				nextToken();
				declareNode = declare_aid(declareNode);
			} else {
				break;
			}
			if (currentToken != null)
				next = currentToken.getContent();
		}
		// 匹配分号;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.SEMICOLON)) {
			nextToken();
		} else { // 报错
			String error = " 声明语句缺少分号\";\"" + "\n";
			error(error);
			declareNode.add(new TreeNode(ConstVar.ERROR + "声明语句缺少分号\";\""));
		}
		return declareNode;
	}

	/**
	 * declare_aid: (ID|ID array)(ASSIGN expression)?;
	 * 
	 * @param root
	 *            根结点
	 * @return TreeNode
	 */
	private final TreeNode declare_aid(TreeNode root) {
		if (currentToken != null && currentToken.getKind().equals("标识符")) {
			TreeNode idNode = new TreeNode("标识符", currentToken.getContent(),
					currentToken.getLine());
			root.add(idNode);
			nextToken();
			// 处理array的情况
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.LBRACKET)) {
				idNode.add(array());
			}//等号，分号，逗号
			else if (currentToken != null
					&& !currentToken.getContent().equals(ConstVar.ASSIGN)
					&& !currentToken.getContent().equals(ConstVar.SEMICOLON)
					&& !currentToken.getContent().equals(ConstVar.COMMA)) {
				String error = " 声明语句出错,标识符后出现不正确的token" + "\n";
				error(error);
				root.add(new TreeNode(ConstVar.ERROR + "声明语句出错,标识符后出现不正确的token"));
				nextToken();
				//如果后面是这些符号的话，并不往下读取，而是由合适的地方重新解读这些token
			}
		}
		else { // 报错
			String error = " 声明语句中标识符出错" + "\n";
			error(error);
			root.add(new TreeNode(ConstVar.ERROR + "声明语句中标识符出错"));
			nextToken();
		}
		// 匹配赋值符号=
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.ASSIGN)) {
			TreeNode assignNode = new TreeNode("分隔符", ConstVar.ASSIGN,
					currentToken.getLine());
			root.add(assignNode);
			nextToken();
			assignNode.add(condition());
		}
		return root;
	}

	/**
	 * condition: expression (comparison_op expression)? | ID;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode condition() {
		// 记录expression生成的结点
		TreeNode tempNode = expression();
		// 如果条件判断为比较表达式
		if (currentToken != null
				&& (currentToken.getContent().equals(ConstVar.EQUAL)
						|| currentToken.getContent().equals(ConstVar.NEQUAL)
						|| currentToken.getContent().equals(ConstVar.LT) || currentToken
						.getContent().equals(ConstVar.GT))) {
			TreeNode comparisonNode = comparison_op();
			comparisonNode.add(tempNode);
			comparisonNode.add(expression());
			return comparisonNode;
		}
		// 如果条件判断为bool变量
		return tempNode;
	}

	/**
	 * expression: term (add_op term)?;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode expression() {
		// 记录term生成的结点
		TreeNode tempNode = term();

		// 如果下一个token为加号或减号
		while (currentToken != null
				&& (currentToken.getContent().equals(ConstVar.PLUS) || currentToken
						.getContent().equals(ConstVar.MINUS))) {
			// add_op
			TreeNode addNode = add_op();
			addNode.add(tempNode);
			tempNode = addNode;
			tempNode.add(term());
		}
		return tempNode;
	}

	/**
	 * term : factor (mul_op factor)?;
	 * 
	 * @return TreeNode
	 */
	//term有代数式的意思
	private final TreeNode term() {
		// 记录factor生成的结点
		TreeNode tempNode = factor();

		// 如果下一个token为乘号或除号
		while (currentToken != null
				&& (currentToken.getContent().equals(ConstVar.TIMES) || currentToken
						.getContent().equals(ConstVar.DIVIDE))) {
			// mul_op
			TreeNode mulNode = mul_op();
			mulNode.add(tempNode);
			tempNode = mulNode;
			tempNode.add(factor());
		}
		return tempNode;
	}

	/**
	 * factor : TRUE | FALSE | REAL_LITERAL | INTEGER_LITERAL | ID | LPAREN
	 * expression RPAREN | DQ string DQ | ID array;
	 * 
	 * @return TreeNode
	 */
	//算术因子
	private final TreeNode factor() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		if (currentToken != null && currentToken.getKind().equals("整数")) {
			tempNode = new TreeNode("整数", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
		} else if (currentToken != null && currentToken.getKind().equals("实数")) {
			tempNode = new TreeNode("实数", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.TRUE)) {
			tempNode = new TreeNode("布尔值", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.FALSE)) {
			tempNode = new TreeNode("布尔值", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
		} else if (currentToken != null && currentToken.getKind().equals("标识符")) {
			tempNode = new TreeNode("标识符", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
			// array
			//region标识符可以是单独的标识符，也可以是代表数组的
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.LBRACKET)) {
				tempNode.add(array());
			}
			//endregion
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LPAREN)) { // 匹配左括号(
			nextToken();
			//算数因子以左括号开始话，就是还是一个表达式
			tempNode = expression();
			// 匹配右括号)
			if (currentToken != null
					&& currentToken.getContent().equals(ConstVar.RPAREN)) {
				nextToken();
			} else { // 报错
				String error = " 算式因子缺少右括号\")\"" + "\n";
				error(error);
				return new TreeNode(ConstVar.ERROR + "算式因子缺少右括号\")\"");
			}
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.DQ)) { // 匹配双引号
			nextToken();
			tempNode = new TreeNode("字符串", currentToken.getContent(),
					currentToken.getLine());
			nextToken();
			//如果是双引号开始话，下面开始就是一个字符串，可以直接略过
			// 匹配另外一个双引号
			nextToken();
		} else { // 报错
			String error = " 算式因子存在错误" + "\n";
			error(error);
			if (currentToken != null
					&& !currentToken.getContent().equals(ConstVar.SEMICOLON)) {
				nextToken();
			}
			return new TreeNode(ConstVar.ERROR + "算式因子存在错误");
		}
		return tempNode;
	}

	/**
	 * array : LBRACKET (expression) RBRACKET;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode array() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LBRACKET)) {
			nextToken();
		} else {
			String error = " 缺少左中括号\"[\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "缺少左中括号\"[\"");
		}
		//没有将 [ 加入TreeNode中
		// 调用expression函数匹配表达式
		tempNode = expression();
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.RBRACKET)) {
			nextToken();
		} else { // 报错
			String error = " 缺少右中括号\"]\"" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "缺少右中括号\"]\"");
		}
		return tempNode;
	}

	/**
	 * add_op : PLUS | MINUS;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode add_op() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.PLUS)) {
			tempNode = new TreeNode("运算符", ConstVar.PLUS, currentToken
					.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.MINUS)) {
			tempNode = new TreeNode("运算符", ConstVar.MINUS, currentToken
					.getLine());
			nextToken();
		} else { // 报错
			String error = " 加减符号出错" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "加减符号出错");
		}
		return tempNode;
	}

	
	
	/**
	 * mul_op : TIMES | DIVIDE;
	 * 
	 * @return TreeNode
	 */
	private final TreeNode mul_op() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.TIMES)) {
			tempNode = new TreeNode("运算符", ConstVar.TIMES, currentToken
					.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.DIVIDE)) {
			tempNode = new TreeNode("运算符", ConstVar.DIVIDE, currentToken
					.getLine());
			nextToken();
		} else { // 报错
			String error = " 乘除符号出错" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "乘除符号出错");
		}
		return tempNode;
	}

	//endregion
	/**
	 *获取TreeNode
	 */
	private static TokenList getStmt(){
		switch (getNextTokenType()) {
			case Token.IF:
			{
				TokenList node = new TokenList(TokenList.IF_STMT);
				consumeNextToken(Token.IF);
				consumeNextToken(Token.LPARENT);
				node.setLeft(getExp());
				consumeNextToken(Token.RPARENT);
				node.setMiddle(getStmt());
				if (getNextTokenType() == Token.ELSE) {
					consumeNextToken(Token.ELSE);
					node.setRight(getStmt());
				}
				return node;
			}
			case Token.WHILE: {
				TokenList node = new TokenList(TokenList.WHILE_STMT);
				consumeNextToken(Token.WHILE);
				consumeNextToken(Token.LPARENT);
				node.setLeft(getExp());
				consumeNextToken(Token.RPARENT);
				node.setMiddle(getStmt());
				return node;
			}
			case Token.READ:
			{
				TokenList node = new TokenList(TokenList.READ_STMT);
				consumeNextToken(Token.READ);
				node.setLeft(variableName());
				consumeNextToken(Token.SEMI);
				return node;
			}
			case Token.WRITE:{
				TokenList node = new TokenList(TokenList.WRITE_STMT);
				consumeNextToken(Token.WRITE);
				node.setLeft(getExp());
				consumeNextToken(Token.SEMI);
				return node;
			}
			case Token.INT:
			case Token.REAL: {
				TokenList node = new TokenList(TokenList.DECLARE_STMT);
				TokenList varNode = new TokenList(TokenList.VAR);
				if (checkNextTokenType(Token.INT, Token.REAL)) {
					current = iterator.next();
					int type = current.getType();
					if (type == Token.INT) {
						varNode.setDataType(Token.INT);
					} else {//type == Token.REAL
						varNode.setDataType(Token.REAL);
					}
				} else {
				}
				if (checkNextTokenType(Token.ID)) {
					current = iterator.next();
					varNode.setValue(current.getValue());
				} else {
				}
				if (getNextTokenType() == Token.ASSIGN) {
					consumeNextToken(Token.ASSIGN);
					node.setMiddle(getExp());
				} else if (getNextTokenType() == Token.LBRACKET) {
					consumeNextToken(Token.LBRACKET);
					varNode.setLeft(getExp());
					consumeNextToken(Token.RBRACKET);
				}
				consumeNextToken(Token.SEMI);
				node.setLeft(varNode);
				return node;
			}
			case Token.LBRACE:
			{
				TokenList node = new TokenList(TokenList.NULL);
				TokenList header = node;
				TokenList temp = null;
				consumeNextToken(Token.LBRACE);
				while (getNextTokenType() != Token.RBRACE) {//允许语句块中没有语句
					temp = getStmt();
					node.setNext(temp);
					node = temp;
				}
				consumeNextToken(Token.RBRACE);
				return header;
			}
			case Token.ID:
			{
				TokenList node = new TokenList(TokenList.ASSIGN_STMT);
				node.setLeft(variableName());
				consumeNextToken(Token.ASSIGN);
				node.setMiddle(getExp());
				consumeNextToken(Token.SEMI);
				return node;
			}
			default:
		}
		return null;
	}

	/**
     * 获取项TreeNode
     * @throws //ParserException
     */
    private static TokenList getTerm(){
        TokenList node = new TokenList(TokenList.EXP);
        node.setDataType(Token.TERM_EXP);
        if (iterator.hasNext()) {
            TokenList expNode = new TokenList(TokenList.FACTOR);
            switch (getNextTokenType()) {
            case Token.LITERAL_INT:
            case Token.LITERAL_REAL:
                expNode.setLeft(getLitreal());
                break;
            case Token.LPARENT:
                consumeNextToken(Token.LPARENT);
                expNode = getExp();
                consumeNextToken(Token.RPARENT);
                break;
            case Token.MINUS:
                expNode.setDataType(Token.MINUS);
                current = iterator.next();
                expNode.setLeft(getTerm());
                break;
            case Token.PLUS:
                current = iterator.next();
                expNode.setLeft(getTerm());
                break;
            default:
                //返回的不是expNode
                return variableName();
            }
            return expNode;
        }
        TokenList leftNode = getFactor();
        if (checkNextTokenType(Token.MUL, Token.DIV)) {
            node.setLeft(leftNode);
            node.setMiddle(multiplyOp());
            node.setRight(getTerm());
        } else {
            return leftNode;
        }
        return node;
    }
    
    /**
     * 因子
     */
    private static TokenList getFactor(){
        if (iterator.hasNext()) {
            TokenList expNode = new TokenList(TokenList.FACTOR);
            switch (getNextTokenType()) {
            case Token.LITERAL_INT:
            case Token.LITERAL_REAL:
                expNode.setLeft(getLitreal());
                break;
            case Token.LPARENT:
                consumeNextToken(Token.LPARENT);
                expNode = getExp();
                consumeNextToken(Token.RPARENT);
                break;
            case Token.MINUS:
                expNode.setDataType(Token.MINUS);
                current = iterator.next();
                expNode.setLeft(getTerm());
                break;
            case Token.PLUS:
                current = iterator.next();
                expNode.setLeft(getTerm());
                break;
            default:
                //返回的不是expNode
                return variableName();
            }
            return expNode;
        }
        return null;
    }
    
    private static TokenList getLitreal(){
        if (iterator.hasNext()) {
            current = iterator.next();
            int type = current.getType();
            TokenList node = new TokenList(TokenList.LITREAL);
            node.setDataType(type);
            node.setValue(current.getValue());
            if (type == Token.LITERAL_INT || type == Token.LITERAL_REAL) {
                return node;
            } else {
                // continue execute until throw
            }
        }
        return null;
    }
    
    /**
     * 逻辑运算符
     * @throws //ParserException
     */
    private static TokenList logicalOp(){
        if (iterator.hasNext()) {
            current = iterator.next();
            int type = current.getType();
            if (type == Token.EQ
                    || type == Token.GET
                    || type == Token.GT
                    || type == Token.LET
                    || type == Token.LT
                    || type == Token.NEQ) {
                TokenList node = new TokenList(TokenList.OP);
                node.setDataType(type);
                return node;
            }
        }
        return null;
    }
    
    /**
     * 加减运算符
     * @throws //ParserException
     */
    private static TokenList addtiveOp(){
        if (iterator.hasNext()) {
            current = iterator.next();
            int type = current.getType();
            if (type == Token.PLUS || type == Token.MINUS) {
                TokenList node = new TokenList(TokenList.OP);
                node.setDataType(type);
                return node;
            }
        }
        return null;
    }
    
    /**
     * 乘除运算符
     */
    private static TokenList multiplyOp(){
        if (iterator.hasNext()) {
            current = iterator.next();
            int type = current.getType();
            if (type == Token.MUL || type == Token.DIV) {
                TokenList node = new TokenList(TokenList.OP);
                node.setDataType(type);
                return node;
            }
        }
        return null;
    }
    
    /**
     * 变量名,可能是单个的变量,也可能是数组的一个元素
     */
    private static TokenList variableName(){
        TokenList node = new TokenList(TokenList.VAR);
        if (checkNextTokenType(Token.ID)) {
            current = iterator.next();
            node.setValue(current.getValue());
        } else {
        }
        if (getNextTokenType() == Token.LBRACKET) {
            consumeNextToken(Token.LBRACKET);
            node.setLeft(getExp());
            consumeNextToken(Token.RBRACKET);
        }
        return node;
    }
    
    /**
     * 消耗掉下一个token,要求必须是type类型,消耗之后current值将停在最后消耗的token上
     */
    private static void consumeNextToken(int type) {
        if (iterator.hasNext()) {
            current = iterator.next();
            if (current.getType() == type) {
                return;
            }
        }
    }
    
    /**
     * 检查下一个token的类型是否和type中的每一个元素相同,调用此函数current位置不会移动
     */
    private static boolean checkNextTokenType(int ... type) {
        if (iterator.hasNext()) {
            int nextType = iterator.next().getType();
            iterator.previous();
            for (int each : type) {
                if (nextType == each) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 获取下一个token的type,如果没有下一个token,则返回{@link Token#NULL}
     */
    private static int getNextTokenType() {
        if (iterator.hasNext()) {
            int type = iterator.next().getType();
            iterator.previous();
            return type;
        }
        return Token.NULL;
    }
	
    /**
     * 获取表达式Tree
     */
    private static TokenList getExp(){
        TokenList node = new TokenList(TokenList.EXP);
        node.setDataType(Token.LOGIC_EXP);
        TokenList leftNode = addtiveExp();
        if (checkNextTokenType(Token.EQ, Token.NEQ, Token.GT, Token.GET, Token.LT, Token.LET)) {
            node.setLeft(leftNode);
            node.setMiddle(logicalOp());
            node.setRight(addtiveExp());
        } else {
            return leftNode;
        }
        return node;
    }
    
    /**
     * 获取多项式Tree
     */
    private static TokenList addtiveExp(){
        TokenList node = new TokenList(TokenList.EXP);
        node.setDataType(Token.ADDTIVE_EXP);
        TokenList leftNode = getTerm();
        if (checkNextTokenType(Token.PLUS)) {
            node.setLeft(leftNode);
            node.setMiddle(addtiveOp());
            node.setRight(addtiveExp());
        } else if (checkNextTokenType(Token.MINUS)) {
            node.setLeft(leftNode);
            TokenList opnode = new TokenList(TokenList.OP);
            opnode.setDataType(Token.PLUS);
            node.setMiddle(opnode);
            node.setRight(addtiveExp());
        } else {
            return leftNode;
        }
        return node;
    }
    
    
	/**
	 * comparison_op: LT | GT | EQUAL | NEQUAL;
	 * 
	 * @return TreeNode
	 */
	//等于不等于大于小于这些比较符号
	private final TreeNode comparison_op() {
		// 保存要返回的结点
		TreeNode tempNode = null;
		if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.LT)) {
			tempNode = new TreeNode("运算符", ConstVar.LT, currentToken.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.GT)) {
			tempNode = new TreeNode("运算符", ConstVar.GT, currentToken.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.EQUAL)) {
			tempNode = new TreeNode("运算符", ConstVar.EQUAL, currentToken
					.getLine());
			nextToken();
		} else if (currentToken != null
				&& currentToken.getContent().equals(ConstVar.NEQUAL)) {
			tempNode = new TreeNode("运算符", ConstVar.NEQUAL, currentToken
					.getLine());
			nextToken();
		} else { // 报错
			String error = " 比较运算符出错" + "\n";
			error(error);
			return new TreeNode(ConstVar.ERROR + "比较运算符出错");
		}
		return tempNode;
	}

}