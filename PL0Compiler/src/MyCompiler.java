import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Iterator;
import java.util.List;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

public class MyCompiler extends JFrame {
    private JTextArea jTextArea;
    private JScrollPane jScrollPane;
    private JButton compileButton;

    private FileDialog open,save;
    private File file;

    private JPanel jPanel;
    private JLabel codeLabel,outputLabel,pcodeLabel;

    private JMenuBar menuBar;

    private JMenuItem openItem,saveItem,aboutItem;
    private JMenu fileMenu;
    private JMenu helpMenu;

    private JScrollPane pcodePanel;
    private JTable pcodeTable;
    String[] pcodeColumnNames = {"序号","操作","层数","地址"};
    private TableModel pcodeTableModel = new DefaultTableModel(pcodeColumnNames,30);

    private JTextArea outputArea;
    private JScrollPane outputScrollPanel;

    private String consoleMessage;
    private Syntax syntax;

    private boolean success = false;



    public MyCompiler() { init(); }
    private void init() {

        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF(); //使用BeautyEye UI框架
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("RootPane.setupButtonVisible",false);



        JFrame frame = new JFrame("PL0compiler");
        frame.setBounds(200,200,1200,780);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();//菜单栏
        fileMenu = new JMenu("  文件  ");
        helpMenu = new JMenu("  帮助  ");

        openItem = new JMenuItem("打开     ");
        saveItem = new JMenuItem("保存     ");
        aboutItem = new JMenuItem("关于     ");

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        helpMenu.add(aboutItem);

        jPanel = new JPanel();
        frame.add(jPanel);
        jPanel.setLayout(null);

        compileButton = new JButton("编译");
        compileButton.setFont(new Font("微软雅黑",0,12));
        compileButton.setFocusPainted(false);
        compileButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

        codeLabel = new JLabel("PL0代码");
        outputLabel = new JLabel("输出");
        pcodeLabel = new JLabel("Pcode序列");

        jTextArea = new JTextArea(12,40);
        jTextArea.setFont(new Font("Monospaced",0,16));
        jTextArea.setLineWrap(false); //换行
        jTextArea.setText("");

        jScrollPane = new JScrollPane(jTextArea);
        //设置滚动条自动出现
        jScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setViewportView(jTextArea);

        outputArea = new JTextArea(4,40);
        outputArea.setFont(new Font("Monospaced",0,16));
        outputArea.setLineWrap(false); //换行
        outputArea.setText("");
        outputScrollPanel = new JScrollPane(outputArea);
        //设置滚动条自动出现
        outputScrollPanel.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputScrollPanel.setViewportView(outputArea);

        //pcode表格
        pcodeTable = new JTable(pcodeTableModel);
        pcodeTable.setPreferredScrollableViewportSize(new Dimension(400,500));
        pcodeTable.setFillsViewportHeight(true);
        pcodePanel = new JScrollPane(pcodeTable);


        codeLabel.setFont(new Font("Monospaced",1,18));
        outputLabel.setFont(new Font("Monospaced",1,18));
        pcodeLabel.setFont(new Font("Monospaced",1,18));

        codeLabel.setBounds(30,0,100,50);
        compileButton.setBounds(550,6,60,38);
        jScrollPane.setBounds(30,50,600,400);
        outputLabel.setBounds(30,450,100,50);
        outputScrollPanel.setBounds(30,500,600,140);
        pcodeLabel.setBounds(700,0,100,50);
        pcodePanel.setBounds(700,50,420,590);

        jPanel.add(codeLabel);
        jPanel.add(compileButton);

        jPanel.add(jScrollPane);
        jPanel.add(outputLabel);
        jPanel.add(outputScrollPanel);
        jPanel.add(pcodeLabel);
        jPanel.add(pcodePanel);

        open = new FileDialog(frame,"打开文档",FileDialog.LOAD);
        save = new FileDialog(frame,"保存文档",FileDialog.SAVE);

        Event();

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

    }
    private void Event() {
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "16211115_刘天一");
            }
        });

        openItem.addActionListener(new ActionListener() {//菜单条目监听：打开
            public void actionPerformed(ActionEvent e) {
                open.setVisible(true);

                String dirPath = open.getDirectory();
                String fileName = open.getFile();
                if (dirPath == null || fileName == null) {
                    return;
                }
                file = new File(dirPath, fileName);

                jTextArea.setText("");//打开文件之前清空文本区域

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        jTextArea.append(line + "\r\n");
                    }
                }
                catch (IOException ex) {
                    throw new RuntimeException("读取失败！");
                }
            }
        });

        saveItem.addActionListener(new ActionListener() {//菜单条目监听：保存
            public void actionPerformed(ActionEvent e) {
                if (file == null) {
                    newFile();
                }
                saveFile();
            }
        });

        compileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compile();
            }
        });

    }
    private void compile() {
        if (file == null) {
            JOptionPane.showMessageDialog(null, "请先保存文件");
            newFile();
        }
        saveFile();
        syntax = new Syntax(file);
        clean();
        if (success = syntax.program()) {
            displayAllPcode();
            consoleMessage += "compile succeed!\n";
            outputArea.setText(consoleMessage);
        } else {
            displayErrorMessage();
            consoleMessage += "compile failed!\n\n"+ syntax.getErr().getErrorTimes()+ " errors found.\nYou are so good.";
            outputArea.setText(consoleMessage);
        }
    }

    private void clean() {
        flushTable(pcodeTable);
        outputArea.setText("");
        consoleMessage = "";
        success = false;
    }

    private void displayErrorMessage() {
        consoleMessage = syntax.getErrorMessage().toString();
    }

    private void displayAllPcode() {
        DefaultTableModel model = (DefaultTableModel)pcodeTable.getModel();
        List<Pcode> pcodeList = syntax.getAllPcode().getAllPcode();
        int readNum = 0;
        Iterator<Pcode> ii = pcodeList.iterator();
        for (int i = 0; i < syntax.getAllPcode().getPcodePtr(); i++) {
            Pcode pcode = ii.next();
            if (pcode.getF() == Operator.OPR && pcode.getA() == 16) {
                readNum++;
            }
            Object[] rowValues = {i,pcode.getF(), pcode.getL(), pcode.getA()};
            model.addRow(rowValues);
        }
    }

    private void flushTable(JTable table) {
        ((DefaultTableModel) table.getModel()).getDataVector().clear();   //清除表格数据
        ((DefaultTableModel) table.getModel()).fireTableDataChanged();//通知模型更新
        table.updateUI();//刷新表格
    }

    private void newFile() {
        if (file == null) {
            save.setVisible(true);
            String dirPath = save.getDirectory();
            String fileName = save.getFile();
            if(dirPath == null || fileName == null) {
                return;
            }
            file = new File(dirPath, fileName);
        }
    }
    private void saveFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            String text = jTextArea.getText();
            bw.write(text);
            bw.close();
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

}
