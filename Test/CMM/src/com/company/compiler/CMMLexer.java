package com.company.compiler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;

import com.company.model.ConstVar;
import com.company.model.Token;
import com.company.model.TreeNode;




public class CMMLexer {
	// 注释的标志
	private boolean isNotation = false;
	// 错误个数
	private int errorNum = 0;
	// 错误信息
	private String errorInfo = "";
	// 分析后得到的tokens集合，用于其后的语法及语义分析
	private ArrayList<Token> tokens = new ArrayList<Token>();
	// 分析后得到的所有tokens集合，包含注释、空格等
	private ArrayList<Token> displayTokens = new ArrayList<Token>();
	// 读取CMM文件文本
	private BufferedReader reader;
	//文本缓存
	private static BufferedReader mBufferedReader;
    private static int currentInt;
    private static char currentChar;
    private static int lineNo;

	//region set、get函数
	public boolean isNotation() {
		return isNotation;
	}

	public void setNotation(boolean isNotation) {
		this.isNotation = isNotation;
	}

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

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public ArrayList<Token> getDisplayTokens() {
		return displayTokens;
	}

	public void outAllToken(){
		for(int i=0;i<displayTokens.size();i++){
			System.out.println(displayTokens.get(i).getKind()+"    "+displayTokens.get(i).getContent());
		}
	}

	public void setDisplayTokens(ArrayList<Token> displayTokens) {
		this.displayTokens = displayTokens;
	}

	/**
	 * 识别字母
	 * 
	 * @param c
	 *            要识别的字符
	 * @return
	 */
	//endregion

    //region 几个功能函数
	private static boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')
			return true;
		return false;
	}

	/**
	 * 识别数字
	 * 
	 * @param c
	 *            要识别的字符
	 * @return
	 */
	private static boolean isDigit(char c) {
		if (c >= '0' && c <= '9')
			return true;
		return false;
	}

	/**
	 * 识别正确的整数：排除多个零的情况
	 * 
	 * @param input
	 *            要识别的字符串
	 * @return 布尔值
	 */
	private static boolean matchInteger(String input) {
		if (input.matches("^-?\\d+$") && !input.matches("^-?0{1,}\\d+$"))
			return true;
		else
			return false;
	}

	/**
	 * 识别正确的浮点数：排除00.000的情况
	 * 
	 * @param input
	 *            要识别的字符串
	 * @return 布尔值
	 */
	private static boolean matchReal(String input) {
		if (input.matches("^(-?\\d+)(\\.\\d+)+$")
				&& !input.matches("^(-?0{2,}+)(\\.\\d+)+$"))
			return true;
		else
			return false;
	}

	/**
	 * 识别正确的标识符：有字母、数字、下划线组成，必须以字母开头，不能以下划线结尾
	 * 
	 * @param input
	 *            要识别的字符串
	 * @return 布尔值
	 */
	private static boolean matchID(String input) {
		if (input.matches("^\\w+$") && !input.endsWith("_") //两个斜杠是因为在“”中要转义
				&& input.substring(0, 1).matches("[A-Za-z]"))
			return true;
		else
			return false;
	}

	/**
	 * 识别保留字
	 * 
	 * @param str 要分析的字符串
	 * @return 布尔值
	 */
	private static boolean isKey(String str) {
		if (str.equals(ConstVar.IF) || str.equals(ConstVar.ELSE)
				|| str.equals(ConstVar.WHILE) || str.equals(ConstVar.READ)
				|| str.equals(ConstVar.WRITE) || str.equals(ConstVar.INT)
				|| str.equals(ConstVar.REAL) || str.equals(ConstVar.BOOL)
				|| str.equals(ConstVar.STRING) || str.equals(ConstVar.TRUE)
				|| str.equals(ConstVar.FALSE) || str.equals(ConstVar.FOR))
			return true;
		return false;
	}

	private static int find(int begin, String str) {
		if (begin >= str.length())
			return str.length();
		for (int i = begin; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n' || c == ',' || c == ' ' || c == '\t' || c == '{'
					|| c == '}' || c == '(' || c == ')' || c == ';' || c == '='
					|| c == '+' || c == '-' || c == '*' || c == '/' || c == '['
					|| c == ']' || c == '<' || c == '>')
				return i - 1;
		}
		return str.length();
	}

	//endregion

	/**
	 * 分析一行CMM程序，并返回分析一行得到的TreeNode
	 * 
	 * @param cmmText
	 *            当前行字符串
	 * @param lineNum
	 *            当前行号
	 * @return 分析生成的TreeNode
	 */
	private TreeNode executeLine(String cmmText, int lineNum) {
		// 创建当前行根结点
		String content = "第" + lineNum + "行： " + cmmText;
		TreeNode node = new TreeNode(content);
		// 词法分析每行结束的标志
		cmmText += "\n";
		int length = cmmText.length();
        //DO 下面这三个变量的含义等待理解,易理解
		// switch状态值
		int state = 0;    //0代表 1代表 2代表 3代表
		// 记录token开始位置
		int begin = 0;
		// 记录token结束位置
		int end = 0;
		// 逐个读取当前行字符，进行分析，如果不能判定，向前多看k位
		for (int i = 0; i < length; i++) {
			char ch = cmmText.charAt(i);
			if (!isNotation) {
				if (ch == '(' || ch == ')' || ch == ';' || ch == '{'
						|| ch == '}' || ch == '[' || ch == ']' || ch == ','
						|| ch == '+' || ch == '-' || ch == '*' || ch == '/'
						|| ch == '=' || ch == '<' || ch == '>' || ch == '"'   //这些是运算符
						|| isLetter(ch) || isDigit(ch)                    //这些事数字、字符
						|| String.valueOf(ch).equals(" ")
						|| String.valueOf(ch).equals("\n")                        //其他无关符号，
						|| String.valueOf(ch).equals("\r")
						|| String.valueOf(ch).equals("\t")) {
					switch (state) {
                    //region case0
					case 0:
						// 分隔符直接打印
						if (ch == '(' || ch == ')' || ch == ';' || ch == '{'
								|| ch == '}' || ch == '[' || ch == ']'
								|| ch == ',') {
							state = 0;
							node.add(new TreeNode("分隔符 ： " + ch));
							tokens.add(new Token(lineNum, i + 1, "分隔符", String
									.valueOf(ch)));
							displayTokens.add(new Token(lineNum, i + 1, "分隔符",
									String.valueOf(ch)));
						}

						//region 一些运算符
						// 加号+
						else if (ch == '+')
							state = 1;
						// 减号-
						else if (ch == '-')
							state = 2;
						// 乘号*
						else if (ch == '*')
							state = 3;
						// 除号/
						else if (ch == '/')
							state = 4;
						// 赋值符号==或者等号=
						else if (ch == '=')
							state = 5;
						// 小于符号<或者不等于<>
						else if (ch == '<')
							state = 6;
						// 大于>
						else if (ch == '>')
							state = 9;
						// 关键字或者标识符
						else if (isLetter(ch)) {
							state = 7;
							begin = i;
						}
						// 整数或者浮点数
						else if (isDigit(ch)) {
							begin = i;
							state = 8;
						}
						// 双引号"
						else if (String.valueOf(ch).equals(ConstVar.DQ)) {
							begin = i + 1;
							state = 10;
							node.add(new TreeNode("分隔符 ： " + ch));
							tokens.add(new Token(lineNum, begin, "分隔符",
									ConstVar.DQ));
							displayTokens.add(new Token(lineNum, begin, "分隔符",
									ConstVar.DQ));
						}
						//endregion

						//region 空白符、换行符、回车符、制表符忽略掉的，没有加入到token中
						// 空白符
						else if (String.valueOf(ch).equals(" ")) {
							state = 0;
							displayTokens.add(new Token(lineNum, i + 1, "空白符",
									" "));
						}
						// 换行符
						else if (String.valueOf(ch).equals("\n")) {
							state = 0;
							displayTokens.add(new Token(lineNum, i + 1, "换行符",
									"\n"));
						}
						// 回车符
						else if (String.valueOf(ch).equals("\r")) {
							state = 0;
							displayTokens.add(new Token(lineNum, i + 1, "回车符",
									"\r"));
						}
						// 制表符
						else if (String.valueOf(ch).equals("\t")) {
							state = 0;
							displayTokens.add(new Token(lineNum, i + 1, "制表符",
									"\t"));
						}
						//endregion

						break;
					//endregion
                    //region case1
					case 1:  //当前一个符号是+时候调到这里，将+加入队列中，并且将这个字符以state0重新解读
						node.add(new TreeNode("运算符 ： " + ConstVar.PLUS));
						tokens.add(new Token(lineNum, i, "运算符", ConstVar.PLUS));
						displayTokens.add(new Token(lineNum, i, "运算符",
								ConstVar.PLUS));
						i--;
						state = 0;
						break;
					//endregion
                    //region case2
					case 2://当前一个符号是-时候调到这里，由于减号并没有直接接入token，所以如果前面是数字或者标识符这些，就将其作为运算符加入token，并且重新扫描该字符，也就是减号后面的字符
						String temp = tokens.get(tokens.size() - 1).getKind();
						String c = tokens.get(tokens.size() - 1).getContent();
						if (temp.equals("整数") || temp.equals("标识符")
								|| temp.equals("实数") || c.equals(")")
								|| c.equals("]")) {
							node.add(new TreeNode("运算符 ： " + ConstVar.MINUS));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.MINUS));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.MINUS));
							i--;
							state = 0;
						} //负号不能作为一行代码的开头，在这个语言中没有实际的意义，就将其认定为错误加入token，可能会在语法分析中再次分析
						else if (String.valueOf(ch).equals("\n")) {
							displayTokens.add(new Token(lineNum, i - 1, "错误",
									ConstVar.MINUS));
						}//如果不是以上两种情况，就可以认定负号是用来判度负数的
						else {
							begin = i - 1;
							state = 8;
						}
						break;
					//endregion
                    //region case3
					case 3://当前面是*号，并且不是注释的时候跳到这里，因为如果监测出来是/*这样的话，这里也不会执行。在不是注释的情况下，*的作用就是用来作为运算符
						if (ch == '/') {
							errorNum++;
							errorInfo += "    ERROR:第 " + lineNum + " 行,第 " + i
									+ " 列：" + "运算符\"" + ConstVar.TIMES
									+ "\"使用错误  \n";
							node.add(new TreeNode(ConstVar.ERROR + "运算符\""
									+ ConstVar.TIMES + "\"使用错误"));
							displayTokens.add(new Token(lineNum, i, "错误",
									cmmText.substring(i - 1, i + 1)));
						}//这里认定*是运算符，并且加入token队列，重新扫描该字符，此时这行代码的扫描已经到*后面的那个字符了，这里在下次扫描中重新扫描这个字符
						else {
							node.add(new TreeNode("运算符 ： " + ConstVar.TIMES));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.TIMES));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.TIMES));
							i--;
						}
						state = 0;
						break;
					//endregion
                    //region case4
					case 4://当上一个字符是/的时候调到这里，/的作用较多
                        //如果/后面紧接着还是/号，就可以认定这一行后面的代码全是注释了，并且直接将i的值符到/n前面，经过for的叠加，也就是下一个就扫描换行符，直接结束这一行
						if (ch == '/') {
							node.add(new TreeNode("单行注释 //"));
							displayTokens.add(new Token(lineNum, i, "单行注释符号",
									"//"));
							begin = i + 1;
							displayTokens.add(new Token(lineNum, i, "注释",
									cmmText.substring(begin, length - 1)));
							i = length - 2;  //这里直接让i跳到末尾了
							state = 0;
						} //如果/后面是*，就可以认为是多行注释的开始，接下来的一段时间都不会执行这个switch了，直到结束，这里将isNotation变量置为true,代表着注释状态开始了
						else if (ch == '*') {
							node.add(new TreeNode("多行注释 /*"));
							displayTokens.add(new Token(lineNum, i, "多行注释开始符号",
									"/*"));
							begin = i + 1;
							isNotation = true;
						}//如果不是注释，那么/的作用也就是除法了
						else {
							node.add(new TreeNode("运算符 ： " + ConstVar.DIVIDE));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.DIVIDE));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.DIVIDE));
							i--;
							state = 0;
						}
						break;
					//endregion
                    //region case5
					case 5://如果前面是=则跳到这里，如果这个符号也是=，就可以认定为是等于的判断符号
						if (ch == '=') {
							node.add(new TreeNode("运算符 ： " + ConstVar.EQUAL));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.EQUAL));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.EQUAL));
							state = 0;
						}//如果这个符号不是=，则可以认定为前一个符号的作用仅仅是赋值符号，那么，将上一个符号加入token，重新读取这个符号
						else {
							state = 0;
							node.add(new TreeNode("运算符 ： " + ConstVar.ASSIGN));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.ASSIGN));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.ASSIGN));
							i--;
						}
						break;
                    //endregion
                    //region case6
					case 6://如果上一个符号是<的时候跳到这，
                        //如果是>，则可以和上一个符号一个认定为不等于，并且将这两个符号一起加入token，扫描下一个字符
						if (ch == '>') {
							node.add(new TreeNode("运算符 ： " + ConstVar.NEQUAL));
							tokens.add(new Token(lineNum, i, "运算符",
									ConstVar.NEQUAL));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.NEQUAL));
							state = 0;
						} //如果这个符号不是>,由于该语言也并没有实现<=这样的符号，所以此时就可以认定其是小于号了，将小于号加入token，并且重新扫描这个符号
						else {
							state = 0;
							node.add(new TreeNode("运算符 ： " + ConstVar.LT));
							tokens.add(new Token(lineNum, i, "运算符",
											ConstVar.LT));
							displayTokens.add(new Token(lineNum, i, "运算符",
									ConstVar.LT));
							i--;
						}
						break;
					//endregion
					//region case7
					case 7://当上一个符号是字母的时候跳到这里，由于标识符只能只能以字符开头，这就节省了下划线的麻烦，在扫描到字母的时候，已经用begin记录了其开始的位置
                        //如果这个还是字母或者数字，,因为_被装在isLetter中了，值于在检查不能下划线开头的时候是在matchID函数中进行的
						if (isLetter(ch) || isDigit(ch)) {
							state = 7;
						}
						//如果不是字母或者数字了，也就意味着标识符结束了
						else {
							end = i;
							String id = cmmText.substring(begin, end);
							//如果是关键字，巴拉巴拉小魔仙
							if (isKey(id)) {
								node.add(new TreeNode("关键字 ： " + id));
								tokens.add(new Token(lineNum, begin + 1, "关键字",
										id));
								displayTokens.add(new Token(lineNum, begin + 1,
										"关键字", id));
							}//如果是标识符，就加入token
							else if (matchID(id)) {
								node.add(new TreeNode("标识符 ： " + id));
								tokens.add(new Token(lineNum, begin + 1, "标识符",
										id));
								displayTokens.add(new Token(lineNum, begin + 1,
										"标识符", id));
							}//如果不是上述，那估计就是下划线开头或者或者数字结尾了
							else {
								errorNum++;
								errorInfo += "    ERROR:第 " + lineNum + " 行,第 "
										+ (begin + 1) + " 列：" + id + "是非法标识符\n";
								node.add(new TreeNode(ConstVar.ERROR + id
										+ "是非法标识符"));
								displayTokens.add(new Token(lineNum, begin + 1,
										"错误", id));
							}
							//重新扫描这个字符
							i--;      //代表使用这个字符继续在state0中继续执行
							state = 0;
						}
						break;
					//endregion
	                //region case8
					case 8://如果是上一个是数字，就到这
						if (isDigit(ch) || String.valueOf(ch).equals(".")) {
							state = 8;
						}//如果一直是数字或者小数点，就一直徘徊在这
						else {
						    //如果在数字之后紧接着字母了，那必然是报错的
							if (isLetter(ch)) {
								errorNum++;
								errorInfo += "    ERROR:第 " + lineNum + " 行,第 "
										+ i + " 列：" + "数字格式错误或者标志符错误\n";
								node.add(new TreeNode(ConstVar.ERROR
										+ "数字格式错误或者标志符错误"));
								displayTokens.add(new Token(lineNum, i, "错误",
										cmmText.substring(begin, find(begin,
												cmmText) + 1)));
								i = find(begin, cmmText);//这里用这个函数到还能理解，可以跳过这一片错误的重灾区，最小程度上影响下面的代码
							}//可能是情况太多的原因，这里仅仅列举了字母的错误，反正不是字母和数字就跳到下面
							else {
								end = i;
								String id = cmmText.substring(begin, end);
								//不包含小数点就不是小数
								if (!id.contains(".")) {
									if (matchInteger(id)) {
										node.add(new TreeNode("整数    ： " + id));
										tokens.add(new Token(lineNum,
												begin + 1, "整数", id));
										displayTokens.add(new Token(lineNum,
												begin + 1, "整数", id));
									} else {
										errorNum++;
										errorInfo += "    ERROR:第 " + lineNum
												+ " 行,第 " + (begin + 1) + " 列："
												+ id + "是非法整数\n";
										node.add(new TreeNode(ConstVar.ERROR
												+ id + "是非法整数"));
										displayTokens.add(new Token(lineNum,
												begin + 1, "错误", id));
									}
								}
								else {
									if (matchReal(id)) {
										node.add(new TreeNode("实数    ： " + id));
										tokens.add(new Token(lineNum,
												begin + 1, "实数", id));
										displayTokens.add(new Token(lineNum,
												begin + 1, "实数", id));
									} else {
										errorNum++;
										errorInfo += "    ERROR:第 " + lineNum
												+ " 行,第 " + (begin + 1) + " 列："
												+ id + "是非法实数\n";
										node.add(new TreeNode(ConstVar.ERROR
												+ id + "是非法实数"));
										displayTokens.add(new Token(lineNum,
												begin + 1, "错误", id));
									}
								}
								i = find(i, cmmText);    //TODO 这个函数的作用可能在输入框那里，有待考究，刚才在尝试的好时候发现如果没有这个函数，第一行语句结束后，按分号会显示到下一行
							}
							state = 0;
						}
						break;
					//endregion
                    //region case9
					case 9://这个就很见到了
						node.add(new TreeNode("运算符 ： " + ConstVar.GT));
						tokens.add(new Token(lineNum, i, "运算符", ConstVar.GT));
						displayTokens.add(new Token(lineNum, i, "运算符",
								ConstVar.GT));
						i--;
						state = 0;
						break;
					//endregion
                    //region case10
					case 10://再遇到“就结束，并且将“加入分隔符
						if (ch == '"') {
							end = i;
							String string = cmmText.substring(begin, end);
							node.add(new TreeNode("字符串 ： " + string));
							tokens.add(new Token(lineNum, begin + 1, "字符串",
									string));
							displayTokens.add(new Token(lineNum, begin + 1,
									"字符串", string));
							node.add(new TreeNode("分隔符 ： " + ConstVar.DQ));
							tokens.add(new Token(lineNum, end + 1, "分隔符",
									ConstVar.DQ));
							displayTokens.add(new Token(lineNum, end + 1,
									"分隔符", ConstVar.DQ));
							state = 0;
						} //如果到结尾了，就报缺少引号的错误
						else if (i == length - 1) {
							String string = cmmText.substring(begin);
							errorNum++;
							errorInfo += "    ERROR:第 " + lineNum + " 行,第 "
									+ (begin + 1) + " 列：" + "字符串 " + string
									+ " 缺少引号  \n";
							node.add(new TreeNode(ConstVar.ERROR + "字符串 "
									+ string + " 缺少引号  \n"));
							displayTokens.add(new Token(lineNum, i + 1, "错误",
									string));
						}
						//这里完全可以再加一个else{state=10；}，但没有也行
					//endregion
					}
				}
				else {//不可识别的标识符
					if (ch > 19967 && ch < 40870 || ch == '\\' || ch == '~'
							|| ch == '`' || ch == '|' || ch == '、' || ch == '^'
							|| ch == '?' || ch == '&' || ch == '^' || ch == '%'
							|| ch == '$' || ch == '@' || ch == '!' || ch == '#'
							|| ch == '；' || ch == '【' || ch == '】' || ch == '，'
							|| ch == '。' || ch == '“' || ch == '”' || ch == '‘'
							|| ch == '’' || ch == '？' || ch == '（' || ch == '）'
							|| ch == '《' || ch == '》' || ch == '·') {
						errorNum++;
						errorInfo += "    ERROR:第 " + lineNum + " 行,第 "
								+ (i + 1) + " 列：" + "\"" + ch + "\"是不可识别符号  \n";
						node.add(new TreeNode(ConstVar.ERROR + "\"" + ch
								+ "\"是不可识别符号"));
						if (state == 0)
							displayTokens.add(new Token(lineNum, i + 1, "错误",
									String.valueOf(ch)));
					}
				}
			}
			else {//处于多行注释状态
				if (ch == '*') {
					state = 3;
				}
				else if (ch == '/' && state == 3) {
					node.add(new TreeNode("多行注释 */"));
					displayTokens.add(new Token(lineNum, begin + 1, "注释",
							cmmText.substring(begin, i - 1)));
					displayTokens.add(new Token(lineNum, i, "多行注释结束符号", "*/"));
					state = 0;
					isNotation = false;
				}
				else if (i == length - 2) {
					displayTokens.add(new Token(lineNum, begin + 1, "注释",
							cmmText.substring(begin, length - 1)));
					displayTokens.add(new Token(lineNum, length - 1, "换行符",
							"\n"));
					state = 0;
				}
				else {
					state = 0;
				}
			}
		}
		return node;
	}

	/**
	 * 分析CMM程序，并返回词法分析结果的根结点
	 * 
	 * @param cmmText
	 *            CMM程序文本
	 * @return 分析生成的TreeNode
	 */
	public TreeNode execute(String cmmText) {
		setErrorInfo("");
		setErrorNum(0);
		setTokens(new ArrayList<Token>());
		setDisplayTokens(new ArrayList<Token>());
		setNotation(false);
		StringReader stringReader = new StringReader(cmmText);
		String eachLine = "";
		int lineNum = 1;
		TreeNode root = new TreeNode("PROGRAM");
		reader = new BufferedReader(stringReader);
		while (eachLine != null) {
			try {
				eachLine = reader.readLine();
				if (eachLine != null) {
					if (isNotation() && !eachLine.contains("*/")) {      //如果是已经是多行注释，且没有结尾
						eachLine += "\n";
						TreeNode temp = new TreeNode(eachLine);
						temp.add(new TreeNode("多行注释"));   //多行注释得内容下只有一个子节点，就是多行注释这个词
						displayTokens.add(new Token(lineNum, 1, "注释", eachLine
								.substring(0, eachLine.length() - 1)));
						displayTokens.add(new Token(lineNum,
								eachLine.length() - 1, "换行符", "\n"));  //TODO 暂时没理解这个东西的作用，有几个猜想，等待后面验证
						root.add(temp); //以每一行为单位加到根节点中
						lineNum++;
						continue;
					} else {
						root.add((executeLine(eachLine, lineNum)));
					}
				}
				lineNum++;
			} catch (IOException e) {
				System.err.println("读取文本时出错了！");
			}
		}
		return root;
	}
	
	/**
     * 获取TokenList
     */
    public static LinkedList<Token> getTokenList(BufferedReader br) throws IOException {
        lineNo = 1;
        mBufferedReader = br;
        LinkedList<Token> tokenList = new LinkedList<Token>();
        StringBuilder sb = new StringBuilder();
        readChar();
        while(currentInt != -1) {
            //消耗空白字符
            if (currentChar == '\n'
                    || currentChar == '\r'
                    || currentChar == '\t'
                    || currentChar == '\f'
                    || currentChar == ' ') {
                readChar();
                continue;
            }
            //简单特殊符号
            switch (currentChar) {
            case ';':
                tokenList.add(new Token(Token.SEMI, lineNo));
                readChar();
                continue;
            case '+':
                tokenList.add(new Token(Token.PLUS, lineNo));
                readChar();
                continue;
            case '-':
                tokenList.add(new Token(Token.MINUS, lineNo));
                readChar();
                continue;
            case '*':
                tokenList.add(new Token(Token.MUL, lineNo));
                readChar();
                continue;
            case '(':
                tokenList.add(new Token(Token.LPARENT, lineNo));
                readChar();
                continue;
            case ')':
                tokenList.add(new Token(Token.RPARENT, lineNo));
                readChar();
                continue;
            case '[':
                tokenList.add(new Token(Token.LBRACKET, lineNo));
                readChar();
                continue;
            case ']':
                tokenList.add(new Token(Token.RBRACKET, lineNo));
                readChar();
                continue;
            case '{':
                tokenList.add(new Token(Token.LBRACE, lineNo));
                readChar();
                continue;
            case '}':
                tokenList.add(new Token(Token.RBRACE, lineNo));
                readChar();
                continue;
            // no default:
            }
            //复合特殊符号
            if (currentChar == '/') {
                readChar();
                if (currentChar == '*') {//多行注释
//                    tokenList.add(new Token(Token.LCOM, lineNo));
                    readChar();
                    while (true) {//使用死循环消耗多行注释内字符
                        if (currentChar == '*') {//如果是*,那么有可能是多行注释结束的地方
                            readChar();
                            if (currentChar == '/') {//多行注释结束符号
//                                tokenList.add(new Token(Token.RCOM, lineNo));
                                readChar();
                                break;
                            }
                        } else {//如果不是*就继续读下一个,相当于忽略了这个字符
                            readChar();
                        }
                    }
                    continue;
                } else if (currentChar == '/') {//单行注释
//                    tokenList.add(new Token(Token.SCOM, lineNo));
                    while (currentChar != '\n') {//消耗这一行之后的内容
                        readChar();
                    }
                    continue;
                } else {//是除号
                    tokenList.add(new Token(Token.DIV, lineNo));
                    continue;
                }
            } else if (currentChar == '=') {
                readChar();
                if (currentChar == '=') {
                    tokenList.add(new Token(Token.EQ, lineNo));
                    readChar();
                } else {
                    tokenList.add(new Token(Token.ASSIGN, lineNo));
                }
                continue;
            } else if (currentChar == '>') {
                readChar();
                if (currentChar == '=') {
                    tokenList.add(new Token(Token.GET, lineNo));
                    readChar();
                } else {
                    tokenList.add(new Token(Token.GT, lineNo));
                }
                continue;
            } else if (currentChar == '<') {
                readChar();
                if (currentChar == '=') {
                    tokenList.add(new Token(Token.LET, lineNo));
                    readChar();
                } else if (currentChar == '>') {
                    tokenList.add(new Token(Token.NEQ, lineNo));
                    readChar();
                } else {
                    tokenList.add(new Token(Token.LT, lineNo));
                }
                continue;
            }
            //数字
            if (currentChar >= '0' && currentChar <= '9') {
                boolean isReal = false;//是否小数
                while ((currentChar >= '0' && currentChar <= '9') || currentChar == '.') {
                    if (currentChar == '.') {
                        if (isReal) {
                            break;
                        } else {
                            isReal = true;
                        }
                    }
                    sb.append(currentChar);
                    readChar();
                }
                if (isReal) {
                    tokenList.add(new Token(Token.LITERAL_REAL, sb.toString(), lineNo));
                } else {
                    tokenList.add(new Token(Token.LITERAL_INT, sb.toString(), lineNo));
                }
                sb.delete(0, sb.length());
                continue;
            }
            //字符组成的标识符,包括保留字和ID
            if ((currentChar >= 'a' && currentChar <= 'z') || currentChar == '_') {
                //取剩下的可能是的字符
                while ((currentChar >= 'a' && currentChar <= 'z')
                        || (currentChar >= 'A' && currentChar <= 'Z')
                        || currentChar == '_'
                        || (currentChar >= '0' && currentChar <= '9'))
                {
                    sb.append(currentChar);
                    readChar();
                }
                Token token = new Token(lineNo);
                String sbString = sb.toString();
                if (sbString.equals("if")) {
                    token.setType(Token.IF);
                } else if (sbString.equals("else")) {
                    token.setType(Token.ELSE);
                } else if (sbString.equals("while")) {
                    token.setType(Token.WHILE);
                } else if (sbString.equals("read")) {
                    token.setType(Token.READ);
                } else if (sbString.equals("write")) {
                    token.setType(Token.WRITE);
                } else if (sbString.equals("int")) {
                    token.setType(Token.INT);
                } else if (sbString.equals("real")) {
                    token.setType(Token.REAL);
                } else {
                    token.setType(Token.ID);
                    token.setValue(sbString);
                }
                sb.delete(0, sb.length());
                tokenList.add(token);
                continue;
            }
            readChar();
        }
        return tokenList;
    }
    
    /**
     * 这个方法也会统计换行,但是方法本身不会改变字符流的读取
     */
    private static void readChar() throws IOException {
        currentChar = (char) (currentInt = mBufferedReader.read());
        if (currentChar == '\n') {
            lineNo++;
        }
    }
}