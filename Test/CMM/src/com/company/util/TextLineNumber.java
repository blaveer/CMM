package com.company.util;

import java.awt.*;
import java.beans.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * 定义文本行数
 */
public class TextLineNumber extends JPanel implements CaretListener,
		DocumentListener, PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	public final static float LEFT = 0.0f;
	public final static float CENTER = 0.5f;
	public final static float RIGHT = 0.5f;

	private final static Border OUTER = new MatteBorder(0, 0, 0, 1, new Color(
			236, 235, 235));
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

	//同步化输入的代码和文本函数
	private JTextComponent component;

	// 可以改变的属性
	private boolean updateFont;
	private int borderGap;
	private Color currentLineForeground;
	private float digitAlignment;
	private int minimumDisplayDigits;

	// 保存历史信息用于减少行数
	private int lastDigits;
	private int lastHeight;
	private int lastLine;
	private HashMap<String, FontMetrics> fonts;

	/**为文本组件创建一个代码行数组件，最小显示宽度将被限制在3位数
	 * 
	 * @param component 关联的文本组件
	 */
	public TextLineNumber(JTextComponent component) {
		this(component, 3);
	}

	/**
	 * 为文本组件创建一个代码行数组件
	 * 
	 * @param component 关联的文本组件
	 * @param minimumDisplayDigits 用于设置最小显示宽度
	 */
	public TextLineNumber(JTextComponent component, int minimumDisplayDigits) {
		this.component = component;

		setFont(component.getFont());
		setForeground(new Color(120, 120, 120));
		setBackground(Color.white);
		setBorderGap(5);
		setCurrentLineForeground(Color.magenta);
		setDigitAlignment(RIGHT);
		setMinimumDisplayDigits(minimumDisplayDigits);

		component.getDocument().addDocumentListener(this);
		component.addCaretListener(this);
		component.addPropertyChangeListener("font",
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getNewValue() instanceof Font) {
							Font newFont = (Font) evt.getNewValue();
							setFont(newFont);
							lastDigits = 0;
							setPreferredWidth();
						}
					}
				});
	}

	/**
	 * 获取更改后的字体设置
	 * 
	 * @return 用于返回更改后的字体设置
	 */
	public boolean getUpdateFont() {
		return updateFont;
	}

	/**
	 * 设置更改后的字体属性，并用于更改相关的文本组件的字体
	 * 
	 * @param updateFont 如果为true更改字体并重绘了行数
	 */
	public void setUpdateFont(boolean updateFont) {
		this.updateFont = updateFont;
	}

	/**
	 * 获取边界间隔
	 * 
	 * @return 边框间隙的像素值
	 */
	public int getBorderGap() {
		return borderGap;
	}

	/**
	 * 边界间隙用于计算左侧和右侧的插入边界
	 * 
	 * @param borderGap 边框间隙的像素值
	 */
	public void setBorderGap(int borderGap) {
		this.borderGap = borderGap;
		Border inner = new EmptyBorder(0, borderGap, 0, borderGap);
		setBorder(new CompoundBorder(OUTER, inner));
		lastDigits = 0;
		setPreferredWidth();
	}

	/**
	 * 获取当前代码行数的颜色
	 * 
	 * @return 当前代码行数的颜色
	 */
	public Color getCurrentLineForeground() {
		return currentLineForeground == null ? getForeground()
				: currentLineForeground;
	}

	/**
	 * 获取当前代码行数数字的颜色，默认值为 Color。RED
	 * 
	 * @param currentLineForeground 当前代码行数的颜色
	 */
	public void setCurrentLineForeground(Color currentLineForeground) {
		this.currentLineForeground = currentLineForeground;
	}

	/**
	 * 获取数字的对齐方式
	 * 
	 * @return 绘制数字的对齐方式
	 */
	public float getDigitAlignment() {
		return digitAlignment;
	}

	/**
	 *用于设置组件的数字水平对齐
	 * 
	 * @param //currentLineForeground 函数所使用的颜色
	 */
	public void setDigitAlignment(float digitAlignment) {
		this.digitAlignment = digitAlignment > 1.0f ? 1.0f
				: digitAlignment < 0.0f ? -1.0f : digitAlignment;
	}

	/**
	 * 获取最小显示数字
	 * 
	 * @return 最小显示数字
	 */
	public int getMinimumDisplayDigits() {
		return minimumDisplayDigits;
	}

	/**
	 * 制定用于计算组件所指向的宽度大小的最小数字，默认值为3
	 * 
	 * @param minimumDisplayDigits 用于计算最小宽度的数字
	 */
	public void setMinimumDisplayDigits(int minimumDisplayDigits) {
		this.minimumDisplayDigits = minimumDisplayDigits;
		setPreferredWidth();
	}

	/**
	 * 计算用于绘制行数所需的最小宽度
	 */
	private void setPreferredWidth() {
		Element root = component.getDocument().getDefaultRootElement();
		int lines = root.getElementCount();
		int digits = Math.max(String.valueOf(lines).length(),
				minimumDisplayDigits);

		// 当所在行数的数字改变时更新大小
		if (lastDigits != digits) {
			lastDigits = digits;
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int width = fontMetrics.charWidth('0') * digits;
			Insets insets = getInsets();
			int preferredWidth = insets.left + insets.right + width;

			Dimension d = getPreferredSize();
			d.setSize(preferredWidth, HEIGHT);
			setPreferredSize(d);
			setSize(d);
		}
	}

	/**
	 * 绘制行数
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// 获取某行还可使用的宽度
		FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
		Insets insets = getInsets();
		int availableWidth = getSize().width - insets.left - insets.right;
		
		Rectangle clip = g.getClipBounds();
		int rowStartOffset = component.viewToModel(new Point(0, clip.y));
		int endOffset = component
				.viewToModel(new Point(0, clip.y + clip.height));

		while (rowStartOffset <= endOffset) {
			try {
				if (isCurrentLine(rowStartOffset))
					g.setColor(getCurrentLineForeground());
				else
					g.setColor(getForeground());

				// 以字符串的形式获取行数并定义x和y偏移量 
				String lineNumber = getTextLineNumber(rowStartOffset);
				int stringWidth = fontMetrics.stringWidth(lineNumber);
				int x = getOffsetX(availableWidth, stringWidth) + insets.left;
				int y = getOffsetY(rowStartOffset, fontMetrics);
				g.drawString(lineNumber, x, y);

				// 移动到下一行
				rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 用于判断鼠标光标是否在目前正在写代码的行数
	 * 以便把该行的行数设置成高亮
	 *  
	 * @param rowStartOffset
	 */
	private boolean isCurrentLine(int rowStartOffset) {
		int caretPosition = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();

		if (root.getElementIndex(rowStartOffset) == root
				.getElementIndex(caretPosition))
			return true;
		else
			return false;
	}

	/**
	 * 获取正在写的行数
	 * @param rowStartOffset
	 */
	protected String getTextLineNumber(int rowStartOffset) {
		Element root = component.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(rowStartOffset);
		Element line = root.getElement(index);

		if (line.getStartOffset() == rowStartOffset)
			return String.valueOf(index + 1);
		else
			return "";
	}

	/**
	 * 定义所在行数的x偏移量
	 * 
	 * @param availableWidth 可使用的宽度
	 * @param stringWidth 代码的宽度
	 */
	private int getOffsetX(int availableWidth, int stringWidth) {
		return (int) ((availableWidth - stringWidth) * digitAlignment);
	}

	/**
	 *定义所在行数的y偏移量
	 * 
	 * @param rowStartOffset 所在行数的起始偏移量
	 * @param fontMetrics 字体设置矩阵
	 */
	private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
			throws BadLocationException {
		
		// 获取所在行的边界矩形
		// 代码文本需要放置在下边界的上方
		Rectangle r = component.modelToView(rowStartOffset);
		int lineHeight = fontMetrics.getHeight();
		int y = r.y + r.height;
		int descent = 0;
		
		// 如果使用的是默认字体设置
		if (r.height == lineHeight) {
			descent = fontMetrics.getDescent();
		} else { //监听所有字体属性的改变
			if (fonts == null)
				fonts = new HashMap<String, FontMetrics>();

			Element root = component.getDocument().getDefaultRootElement();
			int index = root.getElementIndex(rowStartOffset);
			Element line = root.getElement(index);

			for (int i = 0; i < line.getElementCount(); i++) {
				Element child = line.getElement(i);
				AttributeSet as = child.getAttributes();
				String fontFamily = (String) as
						.getAttribute(StyleConstants.FontFamily);
				Integer fontSize = (Integer) as
						.getAttribute(StyleConstants.FontSize);
				String key = fontFamily + fontSize;

				FontMetrics fm = fonts.get(key);

				if (fm == null) {
					Font font = new Font(fontFamily, Font.PLAIN, fontSize);
					fm = component.getFontMetrics(font);
					fonts.put(key, fm);
				}

				descent = Math.max(descent, fm.getDescent());
			}
		}

		return y - descent;
	}

	// 监听鼠标光标所在位置
	public void caretUpdate(CaretEvent e) {
		// 获取鼠标光标所在的行数
		int caretPosition = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();
		int currentLine = root.getElementIndex(caretPosition);

		// Need to repaint so the correct line number can be highlighted
		if (lastLine != currentLine) {
			repaint();
			lastLine = currentLine;
		} else if (currentLine == 0) {
			repaint();
			lastLine = currentLine;
		}
	}

	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	/**
	 * 文档的改变可能会触发文本行数的改变
	 * 
	 */
	private void documentChanged() {
		// 构建指向的大小在这个时间还没有改变
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int preferredHeight = component.getPreferredSize().height;

				// 代码高度改变会引起文本行数的改变
				if (lastHeight != preferredHeight) {
					setPreferredWidth();
					repaint();
					lastHeight = preferredHeight;
				}
			}
		});
	}

	// 用于监听状态改变
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof Font) {
			if (updateFont) {
				Font newFont = (Font) evt.getNewValue();
				setFont(newFont);
				lastDigits = 0;
				setPreferredWidth();
			} else {
				repaint();
			}
		}
	}
}
