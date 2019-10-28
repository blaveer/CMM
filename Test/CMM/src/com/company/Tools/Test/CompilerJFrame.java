//package com.company.Tools.Test;
//
//import com.sun.glass.ui.CommonDialogs;
//
//import javax.swing.*;
//import javax.swing.event.CaretEvent;
//import javax.swing.event.CaretListener;
//import javax.swing.event.UndoableEditEvent;
//import javax.swing.event.UndoableEditListener;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.Element;
//import javax.swing.text.StyledDocument;
//import javax.swing.tree.DefaultTreeCellRenderer;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.undo.UndoManager;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//
//public class CompilerJFrame extends JFrame{
//        private static final long serialVersionUID = 14L;
//        private static final JMenuBar MENUBAR = new JMenuBar();
//        private static final JToolBar TOOLBAR = new JToolBar();
//        private static final JFileTree FILETREE = new JFileTree(new CommonDialogs.ExtensionFilter("lnk"));
//        private static final Font LABELFONT = new Font("幼圆", 1, 13);
//        private Font font = new Font("Courier New", 0, 15);
//        private Font conAndErrFont = new Font("微软雅黑", 0, 14);
//        private Font treeFont = new Font("微软雅黑", 0, 12);
//        private static JMenu fileMenu;
//        private static JMenu editMenu;
//        private static JMenu runMenu;
//        private static JMenu windowMenu;
//        private static JMenu helpMenu;
//        public static JTabbedPane proAndConPanel;
//        private static JCloseableTabbedPane editTabbedPane;
//        private static HashMap<JScrollPane, StyleEditor> map = new HashMap();
//        public static JTextPane consoleArea = new JTextPane();
//        public static JTextArea problemArea = new JTextArea();
//        private FileDialog filedialog_save;
//        private FileDialog filedialog_load;
//        private final UndoManager undo = new UndoManager();
//        private UndoableEditListener undoHandler = new CompilerFrame.UndoHandler();
//        private JPopupMenu popupMenu = new JPopupMenu();
//        private JMenuItem item1;
//        private JMenuItem item2;
//        private JMenuItem item3;
//        private JMenuItem item4;
//        private JMenuItem newItem;
//        private JMenuItem openItem;
//        private JMenuItem saveItem;
//        private JMenuItem exitItem;
//        private JMenuItem undoItem;
//        private JMenuItem redoItem;
//        private JMenuItem copyItem;
//        private JMenuItem cutItem;
//        private JMenuItem pasteItem;
//        private JMenuItem allItem;
//        private JMenuItem searchItem;
//        private JMenuItem deleteItem;
//        private JMenuItem lexItem;
//        private JMenuItem parseItem;
//        private JMenuItem runItem;
//        private JMenuItem startPageItem;
//        private JMenuItem newWindowItem;
//        private JMenuItem helpItem;
//        private JButton newButton;
//        private JButton openButton;
//        private JButton saveButton;
//        private JButton runButton;
//        private JButton lexButton;
//        private JButton parseButton;
//        private JButton undoButton;
//        private JButton redoButton;
//        private JButton copyButton;
//        private JButton cutButton;
//        private JButton pasteButton;
//        private JButton searchButton;
//        private JButton helpButton;
//        private JPanel fileScanPanel;
//        FileFilter filter = new FileFilter() {
//            public String getDescription() {
//                return "CMM程序文件(*.cmm)";
//            }
//
//            public boolean accept(File file) {
//                String tmp = file.getName().toLowerCase();
//                return tmp.endsWith(".cmm") || tmp.endsWith(".CMM");
//            }
//        };
//        private static String findStr = null;
//        private static String text = null;
//        private static int position;
//        private static int time = 0;
//        private CMMLexer lexer = new CMMLexer();
//        private CMMParser parser;
//        private CMMSemanticAnalysis semanticAnalysis;
//        private JTabbedPane tabbedPanel;
//        private String userInput;
//        private static int columnNum;
//        private static int rowNum;
//        private static int presentMaxRow;
//        private static int[] index = new int[2];
//        private static StyledDocument doc = null;
//
//        public CompilerJFrame(String title) {
//            this.setLayout((LayoutManager)null);
//            this.setTitle(title);
//            this.setJMenuBar(MENUBAR);
//            this.setDefaultCloseOperation(3);
//
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                SwingUtilities.updateComponentTreeUI(FILETREE);
//            } catch (Exception var17) {
//                var17.printStackTrace();
//            }
//
//            fileMenu = new JMenu("文件(F)");
//            editMenu = new JMenu("编辑(E)");
//            runMenu = new JMenu("运行(R)");
//            windowMenu = new JMenu("窗口(W)");
//            helpMenu = new JMenu("帮助(H)");
//            fileMenu.setMnemonic(70);
//            editMenu.setMnemonic(69);
//            runMenu.setMnemonic(82);
//            windowMenu.setMnemonic(87);
//            helpMenu.setMnemonic(72);
//            MENUBAR.add(fileMenu);
//            MENUBAR.add(editMenu);
//            MENUBAR.add(runMenu);
//            MENUBAR.add(windowMenu);
//            MENUBAR.add(helpMenu);
//            this.newItem = new JMenuItem("新 建", new ImageIcon("images/new.png"));
//            this.newItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
//            this.openItem = new JMenuItem("打 开", new ImageIcon("images/open.png"));
//            this.openItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
//            this.saveItem = new JMenuItem("保 存", new ImageIcon("images/save.png"));
//            this.saveItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
//            this.exitItem = new JMenuItem("退 出", new ImageIcon("images/exit.png"));
//            this.exitItem.setAccelerator(KeyStroke.getKeyStroke(69, 2));
//            fileMenu.add(this.newItem);
//            fileMenu.add(this.openItem);
//            fileMenu.add(this.saveItem);
//            fileMenu.addSeparator();
//            fileMenu.add(this.exitItem);
//            this.undoItem = new JMenuItem("撤  销", new ImageIcon("images/undo.png"));
//            this.undoItem.setAccelerator(KeyStroke.getKeyStroke(90, 2));
//            this.redoItem = new JMenuItem("重  做", new ImageIcon("images/redo.png"));
//            this.redoItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
//            this.copyItem = new JMenuItem("复  制", new ImageIcon("images/copy.png"));
//            this.copyItem.setAccelerator(KeyStroke.getKeyStroke(67, 2));
//            this.cutItem = new JMenuItem("剪  切", new ImageIcon("images/cut.png"));
//            this.cutItem.setAccelerator(KeyStroke.getKeyStroke(88, 2));
//            this.pasteItem = new JMenuItem("粘  贴", new ImageIcon("images/paste.png"));
//            this.pasteItem.setAccelerator(KeyStroke.getKeyStroke(86, 2));
//            this.allItem = new JMenuItem("全  选", new ImageIcon("images/all.png"));
//            this.allItem.setAccelerator(KeyStroke.getKeyStroke(65, 2));
//            this.searchItem = new JMenuItem("查  找", new ImageIcon("images/search.png"));
//            this.searchItem.setAccelerator(KeyStroke.getKeyStroke(70, 2));
//            this.deleteItem = new JMenuItem("删  除", new ImageIcon("images/delete.png"));
//            this.deleteItem.setAccelerator(KeyStroke.getKeyStroke(68, 2));
//            editMenu.add(this.undoItem);
//            editMenu.add(this.redoItem);
//            editMenu.addSeparator();
//            editMenu.add(this.copyItem);
//            editMenu.add(this.cutItem);
//            editMenu.add(this.pasteItem);
//            editMenu.add(this.deleteItem);
//            editMenu.add(this.allItem);
//            editMenu.addSeparator();
//            editMenu.add(this.searchItem);
//            this.lexItem = new JMenuItem("词法分析", new ImageIcon("images/lex.png"));
//            this.lexItem.setAccelerator(KeyStroke.getKeyStroke(114, 0));
//            this.parseItem = new JMenuItem("语法分析", new ImageIcon("images/parse.png"));
//            this.parseItem.setAccelerator(KeyStroke.getKeyStroke(115, 0));
//            this.runItem = new JMenuItem("运    行", new ImageIcon("images/run.png"));
//            this.runItem.setAccelerator(KeyStroke.getKeyStroke(116, 0));
//            runMenu.add(this.lexItem);
//            runMenu.add(this.parseItem);
//            runMenu.addSeparator();
//            runMenu.add(this.runItem);
//            this.startPageItem = new JMenuItem("开始页", new ImageIcon("images/startpage.png"));
//            this.startPageItem.setAccelerator(KeyStroke.getKeyStroke(78, 8));
//            this.newWindowItem = new JMenuItem("新建窗口", new ImageIcon("images/window.png"));
//            this.newWindowItem.setAccelerator(KeyStroke.getKeyStroke(87, 2));
//            windowMenu.add(this.startPageItem);
//            windowMenu.add(this.newWindowItem);
//            this.helpItem = new JMenuItem("帮 助", new ImageIcon("images/help.png"));
//            this.helpItem.setAccelerator(KeyStroke.getKeyStroke(112, 0));
//            helpMenu.add(this.helpItem);
//            this.item1 = new JMenuItem("复 制    ", new ImageIcon("images/copy.png"));
//            this.item2 = new JMenuItem("剪 切    ", new ImageIcon("images/cut.png"));
//            this.item3 = new JMenuItem("粘 贴    ", new ImageIcon("images/paste.png"));
//            this.item4 = new JMenuItem("全 选    ", new ImageIcon("images/all.png"));
//            this.popupMenu.add(this.item1);
//            this.popupMenu.add(this.item2);
//            this.popupMenu.add(this.item3);
//            this.popupMenu.addSeparator();
//            this.popupMenu.add(this.item4);
//            this.newButton = new JButton(new ImageIcon("images/new.png"));
//            this.newButton.setToolTipText("新建");
//            this.openButton = new JButton(new ImageIcon("images/open.png"));
//            this.openButton.setToolTipText("打开");
//            this.saveButton = new JButton(new ImageIcon("images/save.png"));
//            this.saveButton.setToolTipText("保存");
//            this.lexButton = new JButton(new ImageIcon("images/lex.png"));
//            this.lexButton.setToolTipText("词法分析");
//            this.parseButton = new JButton(new ImageIcon("images/parse.png"));
//            this.parseButton.setToolTipText("语法分析");
//            this.runButton = new JButton(new ImageIcon("images/run.png"));
//            this.runButton.setToolTipText("运行");
//            this.undoButton = new JButton(new ImageIcon("images/undo.png"));
//            this.undoButton.setToolTipText("撤销");
//            this.redoButton = new JButton(new ImageIcon("images/redo.png"));
//            this.redoButton.setToolTipText("重做");
//            this.copyButton = new JButton(new ImageIcon("images/copy.png"));
//            this.copyButton.setToolTipText("复制");
//            this.cutButton = new JButton(new ImageIcon("images/cut.png"));
//            this.cutButton.setToolTipText("剪切");
//            this.pasteButton = new JButton(new ImageIcon("images/paste.png"));
//            this.pasteButton.setToolTipText("粘贴");
//            this.searchButton = new JButton(new ImageIcon("images/search.png"));
//            this.searchButton.setToolTipText("查找");
//            this.helpButton = new JButton(new ImageIcon("images/help.png"));
//            this.helpButton.setToolTipText("帮助");
//            TOOLBAR.setFloatable(false);
//            TOOLBAR.add(this.newButton);
//            TOOLBAR.add(this.openButton);
//            TOOLBAR.add(this.saveButton);
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.add(this.lexButton);
//            TOOLBAR.add(this.parseButton);
//            TOOLBAR.add(this.runButton);
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.add(this.undoButton);
//            TOOLBAR.add(this.redoButton);
//            TOOLBAR.add(this.copyButton);
//            TOOLBAR.add(this.cutButton);
//            TOOLBAR.add(this.pasteButton);
//            TOOLBAR.add(this.searchButton);
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.addSeparator();
//            TOOLBAR.add(this.helpButton);
//            this.add(TOOLBAR);
//            TOOLBAR.setBounds(0, 0, 1240, 33);
//            TOOLBAR.setPreferredSize(this.getPreferredSize());
//            this.filedialog_save = new FileDialog(this, "保存文件", 1);
//            this.filedialog_save.setVisible(false);
//            this.filedialog_load = new FileDialog(this, "打开文件", 0);
//            this.filedialog_load.setVisible(false);
//            this.filedialog_save.addWindowListener(new WindowAdapter() {
//                public void windowClosing(WindowEvent e) {
//                    CompilerFrame.this.filedialog_save.setVisible(false);
//                }
//            });
//            this.filedialog_load.addWindowListener(new WindowAdapter() {
//                public void windowClosing(WindowEvent e) {
//                    CompilerFrame.this.filedialog_load.setVisible(false);
//                }
//            });
//            this.fileScanPanel = new JPanel(new BorderLayout());
//            JLabel fileLabel = new JLabel("项目管理器");
//            JPanel fileLabelPanel = new JPanel(new BorderLayout());
//            JTextArea introductionArea = new JTextArea(8, 3);
//            introductionArea.setFont(new Font("幼圆", 1, 14));
//            introductionArea.setBackground(this.getBackground());
//            introductionArea.setEditable(false);
//            fileLabel.setFont(LABELFONT);
//            fileLabelPanel.add(fileLabel, "West");
//            fileLabelPanel.setBackground(Color.LIGHT_GRAY);
//            this.fileScanPanel.add(fileLabelPanel, "North");
//            this.fileScanPanel.add(new JScrollPane(FILETREE), "Center");
//            this.fileScanPanel.add(introductionArea, "South");
//            this.add(this.fileScanPanel);
//            this.fileScanPanel.setBounds(0, TOOLBAR.getHeight(), 195, 768 - TOOLBAR.getHeight() - 98);
//            editTabbedPane = new JCloseableTabbedPane();
//            editTabbedPane.setFont(this.treeFont);
//            final StyleEditor editor = new StyleEditor();
//            editor.setFont(this.font);
//            JScrollPane scrollPane = new JScrollPane(editor);
//            TextLineNumber tln = new TextLineNumber(editor);
//            scrollPane.setRowHeaderView(tln);
//            editor.addMouseListener(new CompilerFrame.DefaultMouseAdapter());
//            editor.addCaretListener(new CompilerFrame.StatusListener());
//            editor.getDocument().addUndoableEditListener(this.undoHandler);
//            this.addWindowListener(new WindowAdapter() {
//                public void windowOpened(WindowEvent evt) {
//                    editor.requestFocus();
//                }
//            });
//            map.put(scrollPane, editor);
//            editTabbedPane.add(scrollPane, "CMMTest.cmm");
//            JPanel editPanel = new JPanel((LayoutManager)null);
//            editPanel.setBackground(this.getBackground());
//            editPanel.setForeground(new Color(238, 238, 238));
//            JLabel editLabel = new JLabel("|CMM程序文本编辑区");
//            JPanel editLabelPanel = new JPanel(new BorderLayout());
//            editLabel.setFont(LABELFONT);
//            editLabelPanel.add(editLabel, "West");
//            editLabelPanel.setBackground(Color.LIGHT_GRAY);
//            consoleArea.setEditable(false);
//            problemArea.setRows(6);
//            problemArea.setEditable(false);
//            consoleArea.setFont(this.font);
//            problemArea.setFont(this.conAndErrFont);
//            proAndConPanel = new JTabbedPane();
//            proAndConPanel.setFont(this.treeFont);
//            proAndConPanel.add(new JScrollPane(consoleArea), "控制台");
//            proAndConPanel.add(new JScrollPane(problemArea), "错误列表");
//            editPanel.add(editLabelPanel);
//            editPanel.add(editTabbedPane);
//            editPanel.add(proAndConPanel);
//            editLabelPanel.setBounds(0, 0, 815, 15);
//            editTabbedPane.setBounds(0, 15, 815, 462);
//            proAndConPanel.setBounds(0, 475, 815, 160);
//            this.add(editPanel);
//            editPanel.setBounds(this.fileScanPanel.getWidth(), TOOLBAR.getHeight(), 815, 768 - TOOLBAR.getHeight() - 98);
//            JScrollPane lexerPanel = new JScrollPane((Component)null);
//            JScrollPane parserPanel = new JScrollPane((Component)null);
//            JScrollPane interCodePanel = new JScrollPane((Component)null);
//            this.tabbedPanel = new JTabbedPane(1, 1);
//            this.tabbedPanel.setFont(this.treeFont);
//            this.tabbedPanel.add(lexerPanel, "词法分析");
//            this.tabbedPanel.add(parserPanel, "语法分析");
//            this.tabbedPanel.add(interCodePanel, "中间代码");
//            JPanel resultPanel = new JPanel(new BorderLayout());
//            JLabel resultLabel = new JLabel("|分析结果显示区");
//            JPanel resultLabelPanel = new JPanel(new BorderLayout());
//            resultLabel.setFont(LABELFONT);
//            resultLabelPanel.add(resultLabel, "West");
//            resultLabelPanel.setBackground(Color.LIGHT_GRAY);
//            resultPanel.add(resultLabelPanel, "North");
//            resultPanel.add(this.tabbedPanel, "Center");
//            this.add(resultPanel);
//            resultPanel.setBounds(this.fileScanPanel.getWidth() + editPanel.getWidth(), TOOLBAR.getHeight(), 1200 - this.fileScanPanel.getWidth() - editPanel.getWidth() + 38, 768 - TOOLBAR.getHeight() - 98);
//            FILETREE.setFont(this.treeFont);
//            FILETREE.addMouseListener(new MouseAdapter() {
//                public void mouseClicked(MouseEvent e) {
//                    if (e.getClickCount() == 2) {
//                        String str = "";
//                        String fileName = "";
//                        StringBuilder text = new StringBuilder();
//                        File file = CompilerFrame.FILETREE.getSelectFile();
//                        fileName = file.getName();
//                        if (file.isFile() && (fileName.endsWith(".cmm") || fileName.endsWith(".CMM") || fileName.endsWith(".txt") || fileName.endsWith(".TXT") || fileName.endsWith(".java"))) {
//                            try {
//                                FileReader file_reader = new FileReader(file);
//                                BufferedReader in = new BufferedReader(file_reader);
//
//                                while((str = in.readLine()) != null) {
//                                    text.append(str + '\n');
//                                }
//
//                                in.close();
//                                file_reader.close();
//                            } catch (IOException var8) {
//                            }
//
//                            CompilerFrame.this.create(fileName);
//                            CompilerFrame.editTabbedPane.setTitleAt(CompilerFrame.editTabbedPane.getComponentCount() - 1, fileName);
//                            ((StyleEditor)CompilerFrame.map.get(CompilerFrame.editTabbedPane.getSelectedComponent())).setText(text.toString());
//                        }
//
//                        CompilerFrame.this.setSize(CompilerFrame.this.getWidth(), CompilerFrame.this.getHeight());
//                    }
//
//                }
//            });
//            doc = consoleArea.getStyledDocument();
//            consoleArea.addKeyListener(new KeyAdapter() {
//                public void keyPressed(KeyEvent e) {
//                    CompilerFrame.this.getCurrenRowAndCol();
//                    if (CompilerFrame.rowNum > CompilerFrame.presentMaxRow) {
//                        CompilerFrame.presentMaxRow = CompilerFrame.rowNum;
//                    }
//
//                    if (CompilerFrame.rowNum < CompilerFrame.presentMaxRow) {
//                        CompilerFrame.consoleArea.setCaretPosition(CompilerFrame.doc.getLength());
//                        CompilerFrame.this.getCurrenRowAndCol();
//                    }
//
//                    if (e.getKeyChar() == '\n') {
//                        CompilerFrame.consoleArea.setCaretPosition(CompilerFrame.doc.getLength());
//                    }
//
//                    if (e.getKeyCode() == 8 && CompilerFrame.columnNum == 1) {
//                        CompilerFrame.setControlArea(Color.BLACK, false);
//                    }
//
//                }
//
//                public void keyReleased(KeyEvent e) {
//                    CompilerFrame.this.getCurrenRowAndCol();
//                    if (CompilerFrame.rowNum > CompilerFrame.presentMaxRow) {
//                        CompilerFrame.presentMaxRow = CompilerFrame.rowNum;
//                    }
//
//                    if (e.getKeyCode() == 10) {
//                        int pos = CompilerFrame.consoleArea.getCaretPosition();
//                        CompilerFrame.index[0] = CompilerFrame.index[1];
//                        CompilerFrame.index[1] = pos;
//
//                        try {
//                            CompilerFrame.this.userInput = CompilerFrame.doc.getText(CompilerFrame.index[0], CompilerFrame.index[1] - 1 - CompilerFrame.index[0]);
//                            CompilerFrame.this.semanticAnalysis.setUserInput(CompilerFrame.this.userInput);
//                        } catch (BadLocationException var4) {
//                            var4.printStackTrace();
//                        }
//
//                        CompilerFrame.setControlArea(Color.BLACK, false);
//                    }
//
//                    if (e.getKeyCode() == 8 && CompilerFrame.rowNum <= CompilerFrame.presentMaxRow) {
//                        CompilerFrame.consoleArea.setEditable(true);
//                    }
//
//                }
//            });
//            this.newItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent paramActionEvent) {
//                    CompilerFrame.this.create((String)null);
//                }
//            });
//            this.openItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.open();
//                }
//            });
//            this.saveItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.save();
//                }
//            });
//            this.exitItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    System.exit(0);
//                }
//            });
//            this.undoItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.undo();
//                }
//            });
//            this.redoItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.redo();
//                }
//            });
//            this.copyItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.copy();
//                }
//            });
//            this.cutItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.cut();
//                }
//            });
//            this.pasteItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.paste();
//                }
//            });
//            this.allItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.selectAll();
//                }
//            });
//            this.searchItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.search();
//                }
//            });
//            this.deleteItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.delete();
//                }
//            });
//            this.lexItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.lex();
//                }
//            });
//            this.parseItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.parse();
//                }
//            });
//            this.runItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.run();
//                }
//            });
//            this.helpItem.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    JOptionPane.showMessageDialog(new JOptionPane(), "这是一个简单的CMM语言编译器，可以对CMM\n程序文件进行编辑、词法分析、语法分析，并可以\n进行编译、运行和输出程序结果， 同时还实现了\n对程序进行出错检查的功能.", "帮助", 1);
//                }
//            });
//            this.item1.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.copy();
//                }
//            });
//            this.item2.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.cut();
//                }
//            });
//            this.item3.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.paste();
//                }
//            });
//            this.item4.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.selectAll();
//                }
//            });
//            this.newButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent paramActionEvent) {
//                    CompilerFrame.this.create((String)null);
//                }
//            });
//            this.openButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.open();
//                }
//            });
//            this.saveButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.save();
//                }
//            });
//            this.runButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.run();
//                }
//            });
//            this.lexButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.lex();
//                }
//            });
//            this.parseButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.parse();
//                }
//            });
//            this.undoButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.undo();
//                }
//            });
//            this.redoButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.redo();
//                }
//            });
//            this.copyButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.copy();
//                }
//            });
//            this.cutButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.cut();
//                }
//            });
//            this.pasteButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.paste();
//                }
//            });
//            this.searchButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    CompilerFrame.this.search();
//                }
//            });
//            this.helpButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent arg0) {
//                    JOptionPane.showMessageDialog(new JOptionPane(), "这是一个简单的CMM语言编译器，可以对CMM\n程序文件进行编辑、词法分析、语法分析，并可以\n进行编译、运行和输出程序结果， 同时还实现了\n对程序进行出错检查的功能.", "帮助", 1);
//                }
//            });
//        }
//
//        public void lex() {
//            StyleEditor textArea = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            String text = textArea.getText();
//            if (text.equals("")) {
//                JOptionPane.showMessageDialog(new JPanel(), "请确认输入CMM程序不为空！");
//            } else {
//                TreeNode root = this.lexer.execute(text);
//                DefaultTreeModel model = new DefaultTreeModel(root);
//                JTree lexerTree = new JTree(model);
//                lexerTree.setCellRenderer(new DefaultTreeCellRenderer());
//                lexerTree.setShowsRootHandles(true);
//                lexerTree.setRootVisible(true);
//                lexerTree.setFont(this.treeFont);
//                this.tabbedPanel.setComponentAt(0, new JScrollPane(lexerTree));
//                this.tabbedPanel.setSelectedIndex(0);
//                problemArea.setText("**********词法分析结果**********\n");
//                problemArea.append(this.lexer.getErrorInfo());
//                problemArea.append("该程序中共有" + this.lexer.getErrorNum() + "个词法错误！\n");
//                proAndConPanel.setSelectedIndex(1);
//            }
//
//        }
//
//        public TreeNode parse() {
//            this.lex();
//            if (this.lexer.getErrorNum() != 0) {
//                JOptionPane.showMessageDialog(new JPanel(), "词法分析出现错误！请先修改程序再进行语法分析！", "语法分析", 0);
//                return null;
//            } else {
//                this.parser = new CMMParser(this.lexer.getTokens());
//                this.parser.setIndex(0);
//                this.parser.setErrorInfo("");
//                this.parser.setErrorNum(0);
//                TreeNode root = this.parser.execute();
//                DefaultTreeModel model = new DefaultTreeModel(root);
//                JTree parserTree = new JTree(model);
//                parserTree.setCellRenderer(new DefaultTreeCellRenderer());
//                parserTree.setShowsRootHandles(true);
//                parserTree.setRootVisible(true);
//                parserTree.setFont(this.treeFont);
//                problemArea.append("\n");
//                problemArea.append("**********语法分析结果**********\n");
//                if (this.parser.getErrorNum() != 0) {
//                    problemArea.append(this.parser.getErrorInfo());
//                    problemArea.append("该程序中共有" + this.parser.getErrorNum() + "个语法错误！\n");
//                    JOptionPane.showMessageDialog(new JPanel(), "程序进行语法分析时发现错误，请修改！", "语法分析", 0);
//                } else {
//                    problemArea.append("该程序中共有" + this.parser.getErrorNum() + "个语法错误！\n");
//                }
//
//                this.tabbedPanel.setComponentAt(1, new JScrollPane(parserTree));
//                this.tabbedPanel.setSelectedIndex(1);
//                proAndConPanel.setSelectedIndex(1);
//                return root;
//            }
//        }
//
//        public void generateCode() {
//            StyleEditor textArea = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            String text = textArea.getText();
//            SymbolTable symbolTable = SymbolTable.getSymbolTable();
//            symbolTable.newTable();
//            LinkedList<FourCode> codes = CodeGenerater.generateCode(text);
//            symbolTable.deleteTable();
//            StringBuilder sb = new StringBuilder();
//            Iterator var7 = codes.iterator();
//
//            while(var7.hasNext()) {
//                FourCode code = (FourCode)var7.next();
//                sb.append(code.toString() + "\r\n");
//            }
//
//            JTextArea ta_log = new JTextArea(10, 10);
//            ta_log.setLineWrap(true);
//            ta_log.setWrapStyleWord(true);
//            ta_log.setEditable(false);
//            this.tabbedPanel.setComponentAt(2, new JScrollPane(ta_log));
//            this.tabbedPanel.setSelectedIndex(2);
//            proAndConPanel.setSelectedIndex(1);
//            ta_log.append(sb.toString());
//        }
//
//        public void run() {
//            this.generateCode();
//            consoleArea.setText((String)null);
//            columnNum = 0;
//            rowNum = 0;
//            presentMaxRow = 0;
//            index = new int[2];
//            TreeNode node = this.parse();
//            if (this.lexer.getErrorNum() == 0) {
//                if (this.parser.getErrorNum() == 0 && node != null) {
//                    this.semanticAnalysis = new CMMSemanticAnalysis(node);
//                    this.semanticAnalysis.start();
//                }
//            }
//        }
//
//        private void create(String filename) {
//            if (filename == null) {
//                filename = JOptionPane.showInputDialog("请输入新建文件的名字.(后缀名为.cmm)");
//                if (filename == null || filename.equals("")) {
//                    JOptionPane.showMessageDialog((Component)null, "文件名不能为空!");
//                    return;
//                }
//            }
//
//            filename = filename + ".cmm";
//            StyleEditor editor = new StyleEditor();
//            editor.setFont(this.font);
//            JScrollPane scrollPane = new JScrollPane(editor);
//            TextLineNumber tln = new TextLineNumber(editor);
//            scrollPane.setRowHeaderView(tln);
//            editor.addMouseListener(new CompilerFrame.DefaultMouseAdapter());
//            editor.addCaretListener(new CompilerFrame.StatusListener());
//            editor.getDocument().addUndoableEditListener(this.undoHandler);
//            map.put(scrollPane, editor);
//            editTabbedPane.add(scrollPane, filename);
//            editTabbedPane.setSelectedIndex(editTabbedPane.getTabCount() - 1);
//        }
//
//        private void open() {
//            boolean isOpened = false;
//            String str = "";
//            String fileName = "";
//            File file = null;
//            StringBuilder text = new StringBuilder();
//            this.filedialog_load.setVisible(true);
//            if (this.filedialog_load.getFile() != null) {
//                try {
//                    file = new File(this.filedialog_load.getDirectory(), this.filedialog_load.getFile());
//                    fileName = file.getName();
//                    FileReader file_reader = new FileReader(file);
//                    BufferedReader in = new BufferedReader(file_reader);
//
//                    while((str = in.readLine()) != null) {
//                        text.append(str + '\n');
//                    }
//
//                    in.close();
//                    file_reader.close();
//                } catch (IOException var8) {
//                }
//
//                for(int i = 0; i < editTabbedPane.getComponentCount(); ++i) {
//                    if (editTabbedPane.getTitleAt(i).equals(fileName)) {
//                        isOpened = true;
//                        editTabbedPane.setSelectedIndex(i);
//                    }
//                }
//
//                if (!isOpened) {
//                    this.create(fileName);
//                    editTabbedPane.setTitleAt(editTabbedPane.getComponentCount() - 1, fileName);
//                    ((StyleEditor)map.get(editTabbedPane.getSelectedComponent())).setText(text.toString());
//                }
//            }
//
//        }
//
//        private void save() {
//            File currentPath = new File((new File(this.getClass().getClassLoader().getResource("").getPath())).getParent());
//            JFileChooser chooser = new JFileChooser(currentPath);
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("CMM文件 (*.cmm)", new String[]{"cmm"});
//            chooser.setFileFilter(filter);
//            int option = chooser.showSaveDialog((Component)null);
//            File file = null;
//            if (option == 0) {
//                file = chooser.getSelectedFile();
//                String fname = chooser.getName(file);
//                if (fname.indexOf(".con") == -1) {
//                    file = new File(chooser.getCurrentDirectory(), fname + ".cmm");
//                }
//            }
//
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            if (temp.getText() != null) {
//                try {
//                    FileWriter fw = new FileWriter(file);
//                    fw.write(((StyleEditor)map.get(editTabbedPane.getSelectedComponent())).getText());
//                    fw.close();
//                } catch (IOException var8) {
//                }
//            }
//
//        }
//
//        private void undo() {
//            if (this.undo.canUndo()) {
//                try {
//                    this.undo.undo();
//                } catch (Exception var2) {
//                    var2.printStackTrace();
//                }
//            }
//
//        }
//
//        private void redo() {
//            if (this.undo.canRedo()) {
//                try {
//                    this.undo.redo();
//                } catch (Exception var2) {
//                    var2.printStackTrace();
//                }
//            }
//
//        }
//
//        private void copy() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            temp.copy();
//        }
//
//        private void cut() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            temp.cut();
//        }
//
//        private void paste() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            temp.paste();
//        }
//
//        private void search() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            if (text == null) {
//                text = temp.getText();
//            }
//
//            if (findStr == null) {
//                findStr = JOptionPane.showInputDialog(this, "请输入要找的字符串!");
//            }
//
//            if (findStr != null) {
//                position = text.indexOf(findStr);
//                if (text.equals("")) {
//                    JOptionPane.showMessageDialog(this, "没有你要查找的字符串！");
//                    findStr = null;
//                } else if (position != -1) {
//                    temp.select(position + findStr.length() * time, position + findStr.length() * (time + 1));
//                    temp.setSelectedTextColor(Color.RED);
//                    text = new String(text.substring(position + findStr.length()));
//                    ++time;
//                } else {
//                    JOptionPane.showMessageDialog(this, "没有你要查找的字符串！");
//                    time = 0;
//                    text = null;
//                    findStr = null;
//                }
//            }
//
//        }
//
//        private void selectAll() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            temp.selectAll();
//        }
//
//        private void delete() {
//            StyleEditor temp = (StyleEditor)map.get(editTabbedPane.getSelectedComponent());
//            temp.replaceSelection("");
//        }
//
//        private void getCurrenRowAndCol() {
//            int row = 0;
//            int col = 0;
//            int pos = consoleArea.getCaretPosition();
//            Element root = consoleArea.getDocument().getDefaultRootElement();
//            int index = root.getElementIndex(doc.getParagraphElement(pos).getStartOffset());
//
//            try {
//                col = pos - doc.getText(0, doc.getLength()).substring(0, pos).lastIndexOf("\n");
//            } catch (BadLocationException var8) {
//                var8.printStackTrace();
//            }
//
//            try {
//                row = Integer.parseInt(String.valueOf(index + 1));
//            } catch (Exception var7) {
//                var7.printStackTrace();
//            }
//
//            rowNum = row;
//            columnNum = col;
//            presentMaxRow = root.getElementIndex(doc.getParagraphElement(doc.getLength()).getStartOffset()) + 1;
//        }
//
//        public static void setControlArea(Color c, boolean edit) {
//            proAndConPanel.setSelectedIndex(0);
//            consoleArea.setFocusable(true);
//            consoleArea.setForeground(c);
//            consoleArea.setEditable(edit);
//        }
//
//        public static void main(String[] args) {
//            CompilerFrame frame = new CompilerFrame("CMM解释器");
//            frame.setBounds(60, 0, 1240, 702);
//            frame.setResizable(false);
//            frame.setVisible(true);
//        }
//
//        class DefaultMouseAdapter extends MouseAdapter {
//            DefaultMouseAdapter() {
//            }
//
//            public void mouseReleased(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e)) {
//                    CompilerFrame.this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
//                }
//
//            }
//        }
//
//        class StatusListener implements CaretListener {
//            StatusListener() {
//            }
//
//            public void caretUpdate(CaretEvent e) {
//                StyleEditor temp = (StyleEditor)CompilerFrame.map.get(CompilerFrame.editTabbedPane.getSelectedComponent());
//
//                try {
//                    int row = temp.getLineOfOffset(e.getDot());
//                    int var4 = e.getDot() - temp.getLineStartOffset(row);
//                } catch (Exception var5) {
//                    var5.printStackTrace();
//                }
//
//            }
//        }
//
//        class UndoHandler implements UndoableEditListener {
//            UndoHandler() {
//            }
//
//            public void undoableEditHappened(UndoableEditEvent e) {
//                CompilerFrame.this.undo.addEdit(e.getEdit());
//            }
//        }
//}
