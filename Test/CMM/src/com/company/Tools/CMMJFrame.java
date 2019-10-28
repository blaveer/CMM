package com.company.Tools;


import com.company.util.JCloseableTabbedPane;
import com.company.util.JFileTree;
import com.company.util.StyleEditor;
import com.company.util.TextLineNumber;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import static com.company.Tools.MyColor.*;
import static com.company.Tools.MyFont.*;

public class CMMJFrame extends JFrame {
    private static final JMenuBar MENUBAR = new JMenuBar();//菜单栏
    private final static JToolBar TOOLBAR = new JToolBar();//工具栏
    private final static JFileTree FILETREE=new JFileTree(new JFileTree.ExtensionFilter("lnk"));

    //region 菜单栏

    private static JMenu fileMenu;
    private static JMenuItem fileMenu_open;
    private static JMenuItem fileMenu_new;
    private static JMenu editMenu;
    private static JMenu runMenu;
    private static JMenuItem runMenu_lex;
    private static JMenuItem runMenu_gra;
    //endregion

    //region 工具栏
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton runButton;
    private JButton lexButton;
    private JButton parseButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton copyButton;
    private JButton cutButton;
    private JButton pasteButton;
    private JButton searchButton;
    private JButton helpButton;
    private JButton separator;
    //endregion

    // region面板
    private static JPanel fileScanPanel;  //项目管理器
    //region 中间编译区
    private static JPanel editPanel;      //程序编辑区面板
    private static JCloseableTabbedPane editTabbedPane;  //用于可以多开文件的
    private static HashMap<JScrollPane, StyleEditor> map = new HashMap<JScrollPane, StyleEditor>();//TODO 待写StyleEditor类
    public static JTextPane consoleArea = new JTextPane();//控制台(输入与输出)
    public static JTextArea problemArea = new JTextArea();    //错误显示区
    public static JTabbedPane proAndConPanel;    //控制台和错误信息
    //endregion
    private static JPanel resultPanel;
    private JTabbedPane tabbedPanel_Result;    //词法分析语法分析结果显示面板
    //endregion


    public CMMJFrame(String title){
        super(title);
        setLayout(null);
        setJMenuBar(MENUBAR);
        init();

    }
    private void init(){
        this.setSize(1240,702);
        this.setBackground(MainColor);
        this.setLocation(300,200);
        this.setVisible(true);
        // 设置按下右上角X号后关闭
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setJMenuBar(MENUBAR);
        //region 菜单栏
        fileMenu = new JMenu("文件(F)");
        MENUBAR.add(fileMenu);
        fileMenu_open=new JMenuItem("打  开");
        fileMenu.add(fileMenu_open);
        fileMenu_new=new JMenuItem("新  建",new ImageIcon(
                "images/new.png"));
        fileMenu.add(fileMenu_new);

        //编辑菜单栏
        editMenu=new JMenu("编辑(E)");
        MENUBAR.add(editMenu);

        runMenu=new JMenu("运行(R)");
        MENUBAR.add(runMenu);
        runMenu_lex=new JMenuItem("词法分析");
        runMenu.add(runMenu_lex);
        runMenu_gra=new JMenuItem("语法分析");
        runMenu.add(runMenu_gra);
        //endregion
        //region 工具栏
        //region 工具箱内容初始化
        newButton = new JButton(new ImageIcon(
                "images/new.png"));
        newButton.setToolTipText("新建");
        openButton = new JButton(new ImageIcon(
                "images/open.png"));
        openButton.setToolTipText("打开");
        saveButton = new JButton(new ImageIcon(
                "images/save.png"));
        saveButton.setToolTipText("保存");
        lexButton = new JButton(new ImageIcon(
                "images/lex.png"));
        lexButton.setToolTipText("词法分析");
        parseButton = new JButton(new ImageIcon(
                "images/parse.png"));
        parseButton.setToolTipText("语法分析");
        runButton = new JButton(new ImageIcon(
                "images/run.png"));
        runButton.setToolTipText("运行");
        undoButton = new JButton(new ImageIcon(
                "images/undo.png"));
        undoButton.setToolTipText("撤销");
        redoButton = new JButton(new ImageIcon(
                "images/redo.png"));
        redoButton.setToolTipText("重做");
        copyButton = new JButton(new ImageIcon(
                "images/copy.png"));
        copyButton.setToolTipText("复制");
        cutButton = new JButton(new ImageIcon(
                "images/cut.png"));
        cutButton.setToolTipText("剪切");
        pasteButton = new JButton(new ImageIcon(
                "images/paste.png"));
        pasteButton.setToolTipText("粘贴");
        searchButton = new JButton(new ImageIcon(
                "images/search.png"));
        searchButton.setToolTipText("查找");

        helpButton = new JButton(new ImageIcon(
                "images/help.png"));
        helpButton.setToolTipText("帮助");
        separator=new JButton(new ImageIcon("images/separator.png"));
        //endregion
        //region 添加到工具箱
        TOOLBAR.setFloatable(false);
        TOOLBAR.add(newButton);
        TOOLBAR.add(openButton);
        TOOLBAR.add(saveButton);
//        TOOLBAR.add(separator);
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
        TOOLBAR.add(lexButton);
        TOOLBAR.add(parseButton);
        TOOLBAR.add(runButton);
//        TOOLBAR.add(separator);
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
        TOOLBAR.add(undoButton);
        TOOLBAR.add(redoButton);
        TOOLBAR.add(copyButton);
        TOOLBAR.add(cutButton);
        TOOLBAR.add(pasteButton);
        TOOLBAR.add(searchButton);
//        TOOLBAR.add(separator);
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
//        TOOLBAR.addSeparator();
        TOOLBAR.add(helpButton);
        //endregion
        //region 为工具箱添加点击事件
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent paramActionEvent) {
                create(null);
            }
        });
        lexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lex();
            }
        });
        //endregion
        add(TOOLBAR);
        TOOLBAR.setBounds(0, 0, 1240, 33);
        TOOLBAR.setPreferredSize(getPreferredSize());
        //endregion
        //region 项目管理器面板
        fileScanPanel=new JPanel(new BorderLayout());

        JLabel fileScanPanel_name=new JLabel("项目管理器");
        fileScanPanel_name.setFont(LabelFont);

        JPanel fileScanPanel_LabelPanel=new JPanel(new BorderLayout());

        JTextArea introductionArea = new JTextArea(8,3);
        introductionArea.setFont(new Font("幼圆", Font.BOLD, 14));
        introductionArea.setBackground(MainColor);
        introductionArea.setEditable(false);

        fileScanPanel_LabelPanel.add(fileScanPanel_name,BorderLayout.WEST);
        fileScanPanel_LabelPanel.setBackground(HeaderColor);
        fileScanPanel.add(fileScanPanel_LabelPanel,BorderLayout.NORTH);
        fileScanPanel.add(new JScrollPane(FILETREE),BorderLayout.CENTER);
        fileScanPanel.add(introductionArea,BorderLayout.SOUTH);
        this.add(fileScanPanel);
        fileScanPanel.setBounds(0, TOOLBAR.getHeight(), 195, this.getHeight() - TOOLBAR.getHeight());
        //endregion
        //region 程序编辑区及错误列表
        //region 程序编辑区
        editTabbedPane = new JCloseableTabbedPane();
        editTabbedPane.setFont(TreeFont);


        final StyleEditor editor=new StyleEditor();  //TODO 待理解
        editor.setFont(EditFont);
        JScrollPane editTabbedPane_editPanel=new JScrollPane(editor);
        TextLineNumber tln = new TextLineNumber(editor);
        editTabbedPane_editPanel.setRowHeaderView(tln);
        editor.addMouseListener(new DefaultMouseAdapter());
        editor.addCaretListener(new StatusListener());
        //editor.getDocument().addUndoableEditListener(undoHandler);  TODO 待理解之后再添加
        // 获得默认焦点
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent evt) {
                editor.requestFocus();
            }
        });
        map.put(editTabbedPane_editPanel, editor);
        editTabbedPane.add(editTabbedPane_editPanel, "CMMTest" + ".cmm");
        //endregion
        editPanel=new JPanel(null); //程序编译去面板
        //region 头部
        editPanel.setBackground(MainColor);
        editPanel.setForeground(EditColor);
        JLabel editPanel_name=new JLabel("|CMM程序编译区");
        JPanel editPanel_header=new JPanel(new BorderLayout());
        editPanel.setFont(LabelFont);
        editPanel_header.add(editPanel_name,BorderLayout.WEST);
        editPanel_header.setBackground(HeaderColor);
        editPanel.add(editPanel_header);
        //endregion
        // region 控制条和错误列表区
        consoleArea.setEditable(false);
        problemArea.setRows(6);
        problemArea.setEditable(false);
        consoleArea.setFont(EditFont);
        problemArea.setFont(ErrFont);
        proAndConPanel = new JTabbedPane();
        proAndConPanel.setFont(TreeFont);
        proAndConPanel.add(new JScrollPane(consoleArea), "控制台");
        proAndConPanel.add(new JScrollPane(problemArea), "错误列表");
        editPanel.add(editTabbedPane);  //这行代码是程序编译区的那个，因为顺序的原因放在这了
        editPanel.add(proAndConPanel);
        //endregion
        editPanel_header.setBounds(0, 0, 815, 15);
        editTabbedPane.setBounds(0, 15, 815, 462);
        proAndConPanel.setBounds(0, 475, 815, 160);
        add(editPanel);
        editPanel.setBounds(fileScanPanel.getWidth(), TOOLBAR.getHeight(), 815,
                768 - TOOLBAR.getHeight() );
        //endregion
        //region 分析结果展示区
        JScrollPane lexerPanel = new JScrollPane(null);
        JScrollPane parserPanel = new JScrollPane(null);
        JScrollPane interCodePanel = new JScrollPane(null);
        tabbedPanel_Result = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPanel_Result.setFont(TreeFont);
        tabbedPanel_Result.add(lexerPanel, "词法分析");
        tabbedPanel_Result.add(parserPanel, "语法分析");
        tabbedPanel_Result.add(interCodePanel,"中间代码");

        resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("|分析结果显示区");
        JPanel resultLabelPanel = new JPanel(new BorderLayout());
        resultLabel.setFont(LabelFont);
        resultLabelPanel.add(resultLabel, BorderLayout.WEST);
        resultLabelPanel.setBackground(HeaderColor);
        resultPanel.add(resultLabelPanel, BorderLayout.NORTH);
        resultPanel.add(tabbedPanel_Result, BorderLayout.CENTER);
        add(resultPanel);
        resultPanel.setBounds(fileScanPanel.getWidth() + editPanel.getWidth(),
                TOOLBAR.getHeight(), 1200 - fileScanPanel.getWidth()
                        - editPanel.getWidth() + 38, 768 - TOOLBAR.getHeight());
        //endregion
    }

    //region 内部类部分
    // 内部类：控制状态条的显示   TODO 待理解
    class StatusListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            StyleEditor temp = map.get(editTabbedPane.getSelectedComponent());
            try {
                int row = temp.getLineOfOffset(e.getDot());
                int column = e.getDot() - temp.getLineStartOffset(row);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    // 内部类：监听鼠标右键
    class DefaultMouseAdapter extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                //popupMenu.show(e.getComponent(), e.getX(), e.getY());  TODO 待完成部分
            }
        }
    }
    //endregion
    //region 各个点击事件
    //region 新建点击事件
    private void create(String fileName){
        if(fileName==null){
            fileName=JOptionPane.showInputDialog("请输入新建文件的名字(后缀名是。cmm)");
            if(fileName==null||fileName.equals("")){
                JOptionPane.showMessageDialog(null,"文件名不能为空");
                return ;
            }
        }
        fileName+=".cmm";
        StyleEditor editor=new StyleEditor();
        editor.setFont(EditFont);
        JScrollPane scrollPane=new JScrollPane(editor);
        TextLineNumber tln = new TextLineNumber(editor);
        scrollPane.setRowHeaderView(tln);

        editor.addMouseListener(new DefaultMouseAdapter());
        editor.addCaretListener(new StatusListener());
        //editor.getDocument().addUndoableEditListener(undoHandler); TODO 代做
        map.put(scrollPane, editor);
        editTabbedPane.add(scrollPane, fileName);
        editTabbedPane.setSelectedIndex(editTabbedPane.getTabCount() - 1);
    }
    //endregion
    //region 此法分析
    public void lex() {
        StyleEditor textArea = map.get(editTabbedPane.getSelectedComponent());
        String text = textArea.getText();

        if (text.equals("")) {
            JOptionPane.showMessageDialog(new JPanel(), "请确认输入CMM程序不为空！");
        } else {

        }
    }
    //endregion
    //endregion
}
