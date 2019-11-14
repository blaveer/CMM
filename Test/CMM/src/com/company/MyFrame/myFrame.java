package com.company.MyFrame;

import com.company.CMMGra.GraAnalysis;
import com.company.CMMLex.LexAnalysis;
import com.company.CMMSem.semanticAnalysis;
import com.company.util.JCloseableTabbedPane;
import com.company.util.StyleEditor;
import com.company.util.TextLineNumber;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

import static com.company.Tools.MyColor.*;
import static com.company.Tools.MyFont.*;

//TODO 做一个界面

public class myFrame extends JFrame {

    private LexAnalysis lexAnalysis;
    private GraAnalysis graAnalysis;
    private semanticAnalysis semanticAnalysis;



    //当前文本编辑区字符串
    private static String text = null;
    //当前选择的文本的位置
    private static int position;

    //region 对于界面的一些定义
    private static final JMenuBar MENUBAR = new JMenuBar();//菜单栏
    private final static JToolBar TOOLBAR = new JToolBar();//工具栏

    //默认字体
    private final static Font LABELFONT = new Font("幼圆", Font.BOLD, 13);
    //编辑区字体
    private Font font = new Font("Courier New", Font.PLAIN, 15);
    //控制台和错误列表字体
    private Font conAndErrFont = new Font("微软雅黑", Font.PLAIN, 14);

    //region 菜单栏
    private static JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;

    private static JMenu runMenu;
    private JMenuItem lexItem;
    private JMenuItem parseItem;
    private JMenuItem runItem;
    //endregion
    //region 工具栏
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton runButton;
    private JButton lexButton;
    private JButton parseButton;
    private JButton helpButton;
    //endregion

    //编辑区右键菜单
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem item1;
    private JMenuItem item2;
    private JMenuItem item3;
    private JMenuItem item4;

    //CMM程序文本编辑区
    private static JCloseableTabbedPane editTabbedPane;
    private static HashMap<JScrollPane, StyleEditor> map = new HashMap<JScrollPane, StyleEditor>();
    //控制台(输入与输出)
    public static JTextPane consoleArea = new JTextPane();
    //错误显示区
    public static JTextArea problemArea = new JTextArea();
    //控制台和错误信息
    public static JTabbedPane proAndConPanel;

    //保存和打开对话框
    private FileDialog filedialog_save, filedialog_load;

    //endregion

    public myFrame(){
        super("CMM解释器");
        setLayout(null);
        setJMenuBar(MENUBAR);
        init();
    }
    private void init(){
        this.setSize(1240,702);
        this.setBackground(MainColor);
        this.setLocation(300,200);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setJMenuBar(MENUBAR);
        fileMenu = new JMenu("文 件(F)");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        MENUBAR.add(fileMenu);
        newItem = new JMenuItem("新 建", new ImageIcon(
                "images/new.png"));
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK));
        openItem = new JMenuItem("打 开", new ImageIcon(
                "images/open.png"));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        saveItem = new JMenuItem("保 存", new ImageIcon(
                "images/save.png"));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        exitItem = new JMenuItem("退 出", new ImageIcon(
                "images/exit.png"));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                ActionEvent.CTRL_MASK));
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
//        editMenu=new JMenu("编辑(E)");
//        MENUBAR.add(editMenu);

        runMenu=new JMenu("运 行(R)");
        runMenu.setMnemonic(KeyEvent.VK_R);
        MENUBAR.add(runMenu);
        lexItem = new JMenuItem("词法分析", new ImageIcon(
                "images/lex.png"));
        lexItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        parseItem = new JMenuItem("语法分析", new ImageIcon(
                "images/parse.png"));
        parseItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        runItem = new JMenuItem("运    行", new ImageIcon(
                "images/run.png"));
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        runMenu.add(lexItem);
        runMenu.add(parseItem);
        runMenu.addSeparator();
        runMenu.add(runItem);

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
        helpButton = new JButton(new ImageIcon(
                "images/help.png"));
        helpButton.setToolTipText("帮助");
        //endregion
        //region 添加到工具箱
        TOOLBAR.setFloatable(false);
        TOOLBAR.add(newButton);
        TOOLBAR.add(openButton);
        TOOLBAR.add(saveButton);
        TOOLBAR.addSeparator();
        TOOLBAR.addSeparator();
        TOOLBAR.addSeparator();
        TOOLBAR.add(lexButton);
        TOOLBAR.add(parseButton);
        TOOLBAR.add(runButton);
        TOOLBAR.addSeparator();
        TOOLBAR.addSeparator();
        TOOLBAR.addSeparator();
        TOOLBAR.add(helpButton);
        //endregion
        add(TOOLBAR);
        TOOLBAR.setBounds(0, 0, 1240, 33);
        TOOLBAR.setPreferredSize(getPreferredSize());

        // 设置右键菜单
        item1 = new JMenuItem("复 制    ", new ImageIcon(
                "images/copy.png"));
        item2 = new JMenuItem("剪 切    ", new ImageIcon(
                "images/cut.png"));
        item3 = new JMenuItem("粘 贴    ", new ImageIcon(
                "images/paste.png"));
        item4 = new JMenuItem("全 选    ", new ImageIcon(
                "images/all.png"));
        popupMenu.add(item1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.addSeparator();
        popupMenu.add(item4);

        //代码编辑区
        editTabbedPane = new JCloseableTabbedPane();
        editTabbedPane.setFont(TreeFont);
        final StyleEditor editor = new StyleEditor();
        editor.setFont(font);
        JScrollPane scrollPane = new JScrollPane(editor);
        TextLineNumber tln = new TextLineNumber(editor);
        scrollPane.setRowHeaderView(tln);
        editor.addMouseListener(new DefaultMouseAdapter());
        editor.addCaretListener(new StatusListener());
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent evt) {
                editor.requestFocus();
            }
        });
        map.put(scrollPane, editor);
        editTabbedPane.add(scrollPane, "CMMTest" + ".cmm");

        JPanel editPanel = new JPanel(null);
        editPanel.setBackground(getBackground());
        editPanel.setForeground(new Color(238, 238, 238));
        JLabel editLabel = new JLabel("|CMM程序文本编辑区");
        JPanel editLabelPanel = new JPanel(new BorderLayout());
        editLabel.setFont(LABELFONT);
        editLabelPanel.add(editLabel, BorderLayout.WEST);
        editLabelPanel.setBackground(Color.LIGHT_GRAY);

        //  region控制条和错误列表区
        consoleArea.setEditable(false);
        problemArea.setRows(6);
        problemArea.setEditable(false);
        consoleArea.setFont(font);
        problemArea.setFont(conAndErrFont);
        proAndConPanel = new JTabbedPane();
        proAndConPanel.setFont(TreeFont);
        proAndConPanel.add(new JScrollPane(consoleArea), "控制台");
        proAndConPanel.add(new JScrollPane(problemArea), "错误列表");

        editPanel.add(editLabelPanel);//头部
        editPanel.add(editTabbedPane);//代码区
        editPanel.add(proAndConPanel);//输出区域，错误输出区域
        editLabelPanel.setBounds(2, 0, this.getWidth()-10, 15);
        editTabbedPane.setBounds(2, 15, this.getWidth()-10, 462);
        proAndConPanel.setBounds(2, 475, this.getWidth()-10, 160);
        add(editPanel);
        editPanel.setBounds(2, TOOLBAR.getHeight(), this.getWidth()-8,
                this.getHeight() - TOOLBAR.getHeight());


        // region文件保存和打开对话框
        filedialog_save = new FileDialog(this, "保存文件", FileDialog.SAVE);
        filedialog_save.setVisible(false);
        filedialog_load = new FileDialog(this, "打开文件", FileDialog.LOAD);
        filedialog_load.setVisible(false);
        filedialog_save.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                filedialog_save.setVisible(false);
            }
        });
        filedialog_load.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                filedialog_load.setVisible(false);
            }
        });
        //endregion

        //region 为工具条按钮添加事件监听器
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent paramActionEvent) {
                create(null);
            }
        });
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                open();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                save();
            }
        });
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                run();
            }
        });
        lexButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                lex();
            }
        });
        parseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                parse();
            }
        });
    }

    public void run() {

    }

    private void lex(){
        lexAnalysis=new LexAnalysis();
        StyleEditor textArea = map.get(editTabbedPane.getSelectedComponent());
        String text = textArea.getText();
        if (text.equals("")) {
            JOptionPane.showMessageDialog(new JPanel(), "请确认输入CMM程序不为空！");
            return;
        }
        lexAnalysis.lex(text);
        problemArea.setText("**********词法分析结果**********\n");
        problemArea.append(lexAnalysis.getErrorInfo());
        problemArea.append("该程序中共有" + lexAnalysis.getErrorNum() + "个词法错误！\n");
        proAndConPanel.setSelectedIndex(1);
    }
    private void parse(){
        lex();
        if (lexAnalysis.getErrorNum() != 0) {
            JOptionPane.showMessageDialog(new JPanel(),
                    "词法分析出现错误！请先修改程序再进行语法分析！", "语法分析",
                    JOptionPane.ERROR_MESSAGE);//关于JOptionPane,就是一个弹窗
            return;
        }
        graAnalysis=new GraAnalysis(lexAnalysis.getTokens());
        graAnalysis.gra();
        problemArea.append("\n");
        problemArea.append("**********语法分析结果**********\n");
        if(graAnalysis.getErrorNum()!=0){
            problemArea.append(graAnalysis.getErrorInfo());
            problemArea.append("该程序中共有" + graAnalysis.getErrorNum() + "个词法错误！\n");
        }else{
            semanticAnalysis=new semanticAnalysis(graAnalysis.getRoot());
            semanticAnalysis.semantic(graAnalysis.getRoot());
            if(semanticAnalysis.getErrorNum()==0){
                problemArea.append("语法分析没问题");

            }
            else{
                problemArea.append(semanticAnalysis.getErrorInfo());
                problemArea.append("该程序中共有" + semanticAnalysis.getErrorNum() + "个词法错误！\n");
                semanticAnalysis.outError();
            }
        }
        proAndConPanel.setSelectedIndex(1);

    }

    // 新建
    private void create(String filename) {
        if (filename == null) {
            filename = JOptionPane.showInputDialog("请输入新建文件的名字.(后缀名为.cmm)");
            if (filename == null || filename.equals("")) {
                JOptionPane.showMessageDialog(null, "文件名不能为空!");
                return;
            }
        }
        filename += ".cmm";
        StyleEditor editor = new StyleEditor();
        editor.setFont(font);
        JScrollPane scrollPane = new JScrollPane(editor);
        TextLineNumber tln = new TextLineNumber(editor);
        scrollPane.setRowHeaderView(tln);

        editor.addMouseListener(new DefaultMouseAdapter());
        editor.addCaretListener(new StatusListener());
        map.put(scrollPane, editor);
        editTabbedPane.add(scrollPane, filename);
        editTabbedPane.setSelectedIndex(editTabbedPane.getTabCount() - 1);
    }

    // 打开
    private void open() {
        boolean isOpened = false;
        String str = "", fileName = "";
        File file = null;
        StringBuilder text = new StringBuilder();
        filedialog_load.setVisible(true);
        if (filedialog_load.getFile() != null) {
            try {
                file = new File(filedialog_load.getDirectory(), filedialog_load
                        .getFile());
                fileName = file.getName();
                FileReader file_reader = new FileReader(file);
                BufferedReader in = new BufferedReader(file_reader);
                while ((str = in.readLine()) != null)
                    text.append(str + '\n');
                in.close();
                file_reader.close();
            } catch (IOException e2) {
            }
            for (int i = 0; i < editTabbedPane.getComponentCount(); i++) {
                if (editTabbedPane.getTitleAt(i).equals(fileName)) {
                    isOpened = true;
                    editTabbedPane.setSelectedIndex(i);
                }
            }
            if (!isOpened) {
                create(fileName);
                editTabbedPane.setTitleAt(
                        editTabbedPane.getComponentCount() - 1, fileName);
                map.get(editTabbedPane.getSelectedComponent()).setText(
                        text.toString());
            }

        }
    }

    // 保存
    private void save() {
        File currentPath = new File(new File(this.getClass().getClassLoader().getResource("").getPath()).getParent());
        JFileChooser chooser = new JFileChooser(currentPath);
        //后缀名过滤器
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "CMM文件 (*.cmm)", "cmm");
        chooser.setFileFilter(filter);
        int option = chooser.showSaveDialog(null);
        File file = null;
        if(option==JFileChooser.APPROVE_OPTION){    //假如用户选择了保存
            file = chooser.getSelectedFile();

            String fname = chooser.getName(file);   //从文件名输入框中获取文件名

            //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
            if(fname.indexOf(".con")==-1){
                file=new File(chooser.getCurrentDirectory(),fname+".cmm");
            }
        }

        StyleEditor temp = map.get(editTabbedPane.getSelectedComponent());
        if (temp.getText() != null) {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(map.get(editTabbedPane.getSelectedComponent())
                        .getText());
                fw.close();
            } catch (IOException e2) {
            }
        }
    }





    /**下面是一些内部类*/
    //region
    // 内部类：监听鼠标右键
    class DefaultMouseAdapter extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    // 内部类：控制状态条的显示
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
    //endregion
}
