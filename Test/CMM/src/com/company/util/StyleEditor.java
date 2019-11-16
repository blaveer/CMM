package com.company.util;

import com.company.FrameT.CompilerFrame;
import com.sun.rowset.internal.Row;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import static com.company.FrameT.CompilerFrame.promptPanel;
import static com.company.FrameT.CompilerFrame.promptlist;

public class StyleEditor extends JTextPane implements DocumentListener {

	private static final long serialVersionUID = 1L;
	// 表明是否启用解释器来解析数据,默认使用
	private boolean cmmCompiler = true;

	//private String[] promptS;
	private LinkedList<String> promptS = new LinkedList<String>();

	private int caretStartPosiont = 0;

	private CodeStyle codeStyle = new CodeStyle();

	private StyleContext context = new StyleContext();

	private Style style = context.getStyle(StyleContext.DEFAULT_STYLE);

	private static String OldText=null;
	private static String NewText=null;

	public StyleEditor() {
		//initPrompt();
		setDocument(new DefaultStyledDocument());
		getDocument().addDocumentListener(this);
		setFont(new Font(("Courier New"), Font.PLAIN, 15));
		setTabs(this, 4);
		initStyleContext();
		setMargin(new Insets(2, 0, 0, 0));
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_END) {
					caretStartPosiont = getCaretPosition();
					setEditable(false);
				} else if (e.getKeyCode() == KeyEvent.VK_HOME) {
					caretStartPosiont = getCaretPosition();
					setEditable(false);
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_END) {
					setEditable(true);
					setCaretPosition(getStyledDocument().getParagraphElement(
							caretStartPosiont).getEndOffset() - 1);

				} else if (e.getKeyCode() == KeyEvent.VK_HOME) {
					setEditable(true);
					setCaretPosition(getStyledDocument().getParagraphElement(
							caretStartPosiont).getStartOffset());
				}
			}
		});
	}

	public StyleEditor(boolean cmm) {
		this();
		cmmCompiler = false;
	}

	public int getLineOfOffset(int paramInt) throws BadLocationException {
		Document localDocument = getDocument();
		if (paramInt < 0)
			throw new BadLocationException("Can't translate offset to line", -1);
		if (paramInt > localDocument.getLength()) {
			throw new BadLocationException("Can't translate offset to line",
					localDocument.getLength() + 1);
		}
		Element localElement = getDocument().getDefaultRootElement();
		return localElement.getElementIndex(paramInt);
	}

	public int getLineCount() {
		Element localElement = getDocument().getDefaultRootElement();
		return localElement.getElementCount();
	}

	public int getLineStartOffset(int paramInt) throws BadLocationException {
		int i = getLineCount();
		if (paramInt < 0)
			throw new BadLocationException("Negative line", -1);
		if (paramInt >= i) {
			throw new BadLocationException("No such line", getDocument()
					.getLength() + 1);
		}
		Element localElement1 = getDocument().getDefaultRootElement();
		Element localElement2 = localElement1.getElement(paramInt);
		return localElement2.getStartOffset();
	}

	public void initStyleContext() {
	}

	public void setTabs(JTextPane textPane, int charactersPerTab) {
		FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
		int charWidth = fm.charWidth('w');
		//System.out.println("字符宽度是:"+charWidth);
		int tabWidth = charWidth * charactersPerTab;
		codeStyle.setCharWidth(charWidth);
		TabStop[] tabs = new TabStop[1000];

		for (int j = 0; j < tabs.length; j++) {
			int tab = j + 1;
			tabs[j] = new TabStop(tab * tabWidth);
		}
		TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(style, tabSet);
	}

	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
		update();
	}

	public void removeUpdate(DocumentEvent e) {
		update();
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public void paint(Graphics g) {
		super.paint(g);
		codeStyle.drawWaveLine(g);
	}

	public void setSize(Dimension d) {
		int parentWidth = this.getParent().getWidth();
		if (parentWidth > d.width) {
			d.width = parentWidth;
		}
		super.setSize(d);
	}

	//TODO 我估摸着呀，下面这个函数是DocumentListen接口中的函数，每次输入那个地方有改变，就执行这个函数
	private void update() {
		StyledDocument oDoc = getStyledDocument();
		StyledDocument nDoc = new DefaultStyledDocument(context);
		try {
			String text = oDoc.getText(0, oDoc.getLength());
			codeStyle.markStyle(text, nDoc, cmmCompiler);//只有一个地方将其转化为false，但是那个函数没有被使用过
//			OldText=NewText;
//			NewText=text;
			//TODO 关于提示输入的一点想法，获取当前光标所在行的，这一行前面的一整行的字符，然后以分割（只要不是字母、数字、下划线，其他的都可以作为分割的字符），取最后一个字符串
			//TODO 将其与提示符集中的进行比对，前缀相同的排前面，中间有的排中间、后缀相同的排最后，然后默认选择排行最靠前的字符串、用户通过上下选择来选取自己想要的
			//TODO 以已经输入了i为例吧，以选择int作为替换，将光标前1（i的长度）个字符删除，然后再插入int。
			//TODO 现在的关键就是解决用什么来显示提示符和如何获取用来被提示的字符以及如何删除和替换其中的字串。
			oDoc.removeDocumentListener(this);
			nDoc.addDocumentListener(this);
//			//System.out.println(this.getCaretPosition());
//			String input=getInput(text,this.getCaretPosition());
//			System.out.println(input);
//			if(input!=null){
//				LinkedList<String> prompt=getPrompt(input);
//				System.out.println(prompt);
//				if(prompt.size()>0){
//                    int   c=this.getCaretPosition();
//                    int   line =  this.getLineOfOffset(c)   +   1;   //行
//                    System.out.println("行高是："+line);
//                    int   col  =  c - this.getLineStartOffset(line-1)   +   1;//列
//                    //System.out.println("其坐标是：（"+x+","+y+")");
//                    String[] promptArr=prompt.toArray(new String[0]);
//                    DefaultListModel dlm=new DefaultListModel();
//                    dlm.clear();
//                    promptlist.setModel(dlm);
//                    for(int i=0;i<promptArr.length;i++){
//                        dlm.addElement(promptArr[i]);
//                    }
//                    promptlist.setModel(dlm);
//                    int CaretPosition=this.getCaretPosition();
//                    promptlist.addMouseListener(new MouseAdapter() {
//                        @Override
//                        public void mouseClicked(MouseEvent evt) {
//                            JList list = (JList)evt.getSource();
//                            if (evt.getClickCount() == 2) {          // Double-click
//                                // Get item index
//                                int promptIndex = list.locationToIndex(evt.getPoint());
//
//                                replaceText(text, promptlist.getSelectedValuesList(),nDoc,CaretPosition);
//                                try{
//                                    //nDoc.insertString(CaretPosition-input.length(),promptlist.getSelectedValue().toString().trim(),style);
//                                    nDoc.insertString(CaretPosition,promptlist.getSelectedValue().toString().trim().replace(input,""),style);
//                                    promptPanel.setVisible(false);
//                                    //nDoc.remove(CaretPosition+promptlist.getSelectedValue().toString().trim().length()-input.length(),input.length());
//                                    //nDoc.remove(0,1);
//                                    System.out.println("去除函数也执行了");
//                                }catch (Exception ex){
//                                    System.out.println("v胡vu哦了");
//                                }
//
//                            }
//                        }
//                    });
//                    //promptlist.setBounds(new Rectangle(0,0));
//                    promptlist.setSelectedIndex(0);
//					promptPanel.setBounds(205+col*9,82+line*17,promptlist.getWidth()+10,promptlist.getHeight()+5);
//                    //promptPanel.setBounds(205+col*9,82+line*17,100,100);
//					promptPanel.setVisible(true);
//				}
//				else{
//				    try{
//                        //promptlist=null;
//                        //promptPanel.remove(promptlist);
//                    }catch (Exception ex){
//				        System.out.println("没有这个控件");
//                    }
//					promptPanel.setVisible(false);
//				}
//			}
//            else{
//                try{
//                    //promptlist=null;
//                    //promptPanel.remove(promptlist);
//                }catch (Exception ex){
//                    System.out.println("没有这个控件");
//                }
//                promptPanel.setVisible(false);
//            }

			int off = getCaretPosition();
			//System.out.println(nDoc.getText(0,nDoc.getLength()));
			setDocument(nDoc);

			setCaretPosition(off);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} finally {
			oDoc = null;
		}
	}

    private void replaceText(String text, List selectedValuesList, StyledDocument nDoc, int caretPosition) {
    }

    private String getInput(String text,int CarePosition){
		String prompt=null;
		String[] split = text.split("\\n");
		String RowText=null;
		int sumLength=0;
		for(int i=0;i<split.length;i++){
			sumLength=sumLength+split[i].length()+1;
			if(sumLength>CarePosition){
				RowText=split[i];
				break;
			}
		}
		if(RowText==null){
			return null;
		}
		int start=sumLength-RowText.length()-1;
		int end=CarePosition-start;
		RowText=RowText.substring(0,end);
		//int numOfQuotation=0;
		//System.out.println("这一行光标前的字符是："+RowText);
		String[] tempSplit=RowText.split("\"");
		if((tempSplit.length%2)==0){  //如果被分割成了偶数个，那也就是代表其中有奇数个双引号，也就可以认为此时光标处于字符串中，就不提示了
			return null;
		}
		//System.out.println("end的值是："+end);

		for(int i=end-1;i>=0;i--){
			char temp=RowText.charAt(i);
			if(!IDChar(temp)){
				prompt=RowText.substring(i+1,end);
				return prompt;
			}
			if(i==0){
				return RowText;
			}
		}
		return null;
	}

	private boolean IDChar(char c){
		if (c >= '0' && c <= '9'){
			return true;
		}
		else if(((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')){
			return true;
		}
		return false;
	}








	private LinkedList<String>getPrompt(String input){
	    //System.out.println("待提示的字符串是"+input+"换行绘");
		LinkedList<String> prompt=new LinkedList<String>();
        if(input.equals("")){
            return prompt;
        }
		for(int i=0;i<promptS.size();i++){
			if(promptS.get(i).startsWith(input)){
				prompt.add(promptS.get(i));
			}
		}
		for(int i=0;i<promptS.size();i++){
			if(promptS.get(i).contains(input)&&(!promptS.get(i).startsWith(input))){
				prompt.add(promptS.get(i));
			}
		}
		return prompt;
	}
	private void initPrompt(){
		promptS.add("int");
		promptS.add("string");
		promptS.add("bool");
		promptS.add("real");
		promptS.add("if");
		promptS.add("else");
		promptS.add("for");
		promptS.add("while");
		promptS.add("read");
		promptS.add("write");
		promptS.add("true");
		promptS.add("false");
		//promptS.add("abcdefghijklmnopqrstuvwxyz");
	}

}