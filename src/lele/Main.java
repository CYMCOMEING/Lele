package lele;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.swing.SpringLayout.*;

public class Main {

    private static String config_path = "config.ini"; // 配置文件路径
    // 配置文件读出来的参数
    private static String root = ""; // 文件输出路径
    private static int startPage = 1;   //
    private static int pageCount = 1;

    private static JFrame mainFrame;
    private static JButton btn_start;
    private static JButton btn_stop;
    private static JTextField txt_startpage;
    private static JTextField txt_pageCount;
    private static JButton btn_choice;
    private static JTextField txt_path;
    private static JTextArea txt_log;

    private static boolean isForcedExit = false;

    public static void main(String[] args) {
        // 创建 JFrame 实例
        mainFrame = new JFrame("Java");
        // Setting the width and height of frame
        mainFrame.setSize(500, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);//在屏幕中居中显示

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        mainFrame.setContentPane(panel);

        // 添加组件到面板
        placeComponents(panel);

        // 加载配置
        laodConfig();

        // 设置界面可见
        mainFrame.setVisible(true);

    }

    /**
     * 初始化控件里面的参数
     */
    private static void laodConfig() {
        readConfigFile();

        // 初始化文本框的内容
        if (txt_path != null) {
            File file = new File(root);
            txt_path.setText(file.getAbsolutePath());
            root = file.getAbsolutePath();
        }

        // 开始页数
        if (txt_startpage != null) {
            txt_startpage.setText(Utils.intToString(startPage));
        }

        // 爬取页数
        if (txt_pageCount != null) {
            txt_pageCount.setText(Utils.intToString(pageCount));
        }
    }

    /**
     * 从配置文件读取参数保存到变量
     */
    private static void readConfigFile() {
        File file = new File(config_path);
        if (!Utils.fileIsExists(config_path)) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存配置文件的绝对路径
        config_path = file.getAbsolutePath();
        String str;
        // 路径
        str = Utils.readFileLine(1, config_path);
        if ("".equals(str)) {
            root = new File("").getAbsolutePath();
        }else{
            root = str;
        }
        // 开始页数
        str = Utils.readFileLine(2, config_path);
        if (!"".equals(str)) {
            startPage = Utils.stringToInt(str);
        }

        // 爬取页数
        str = Utils.readFileLine(3, config_path);
        if (!"".equals(str)) {
            pageCount = Utils.stringToInt(str);
        }
    }

    /**
     * 保存参数到配置文件
     */
    private static void saveConfigFile() {
        String string = root + "\r\n" + startPage + "\r\n" + pageCount;
        Utils.writeFile(string, false, config_path);
    }

    /**
     * 创建界面
     *
     * @param panel 画板
     */
    private static void placeComponents(JPanel panel) {

        // 使用 弹性布局
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        // 开始按钮
        btn_start = new JButton("开始");
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                }).start();
            }
        });
        panel.add(btn_start);

        // 停止按钮

        btn_stop = new JButton("停止");
        btn_stop.setEnabled(false);
        btn_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn_stop.setEnabled(false);
                btn_stop.setText("正在停止中");
                isForcedExit = true;
            }
        });
        panel.add(btn_stop);

        // 清空消息按钮
        JButton btn_clean = new JButton("清空消息");
        btn_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txt_log != null) {
                    txt_log.setText("");
                }
            }
        });
        panel.add(btn_clean);

        // 开始页数标签
        JLabel lab_startPage = new JLabel("开始页数");
        panel.add(lab_startPage);

        // 开始页数文本框
        txt_startpage = new JTextField(3);
        txt_startpage.setEnabled(true);
        txt_startpage.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                Log("insertUpdate");
                startPage = Utils.stringToInt(txt_startpage.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                Log("removeUpdate");
                startPage = Utils.stringToInt(txt_startpage.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                Log("changedUpdate");
            }
        });
        panel.add(txt_startpage);

        // 爬取页数标签
        JLabel lab_pageCount = new JLabel("获取多少页");
        panel.add(lab_pageCount);

        // 爬取页数文本框
        txt_pageCount = new JTextField(3);
        txt_pageCount.setEnabled(true);
        txt_pageCount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                pageCount = Utils.stringToInt(txt_pageCount.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pageCount = Utils.stringToInt(txt_pageCount.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        panel.add(txt_pageCount);

        // 选择目录按钮
        btn_choice = new JButton("选择保存目录");
        btn_choice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                // 设置默认显示的文件夹为当前文件夹
                fc.setCurrentDirectory(new File(txt_path.getText()));
                // 只选文件夹
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // 只能单选
                fc.setMultiSelectionEnabled(false);

                int result = fc.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // 点击了确定
                    File file = fc.getSelectedFile();
                    if (txt_path != null) {
                        root = file.getAbsolutePath();
                        txt_path.setText(root);
                    }
                }
            }
        });
        panel.add(btn_choice);

        // 显示目录的文本框
        txt_path = new JTextField();
        txt_path.setEnabled(false); // 不可编辑
        txt_path.setForeground(Color.BLACK);
        panel.add(txt_path);

        // 用于输出信息文本框
        txt_log = new JTextArea();
        txt_log.setEditable(false);
        txt_log.setForeground(Color.BLUE);
        // 给文本框添加滚动条
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(txt_log);
        panel.add(scrollPane);

        // 组件约束关系 /////////////////////////////////////////////////////////////////////////////

        // 开始按钮
        // 设置左上角坐标为 (5, 5)
        SpringLayout.Constraints cons_start = layout.getConstraints(btn_start);
        cons_start.setX(Spring.constant(5));
        cons_start.setY(Spring.constant(5));

        // 停止按钮
        // 设置左上角坐 水平坐标为 开始按钮的北坐标, 垂直坐标为 开始按钮的东边坐标+5
        SpringLayout.Constraints cons_stop = layout.getConstraints(btn_stop);
        cons_stop.setX(Spring.sum(cons_start.getConstraint(EAST), Spring.constant(5)));
        cons_stop.setY(cons_start.getConstraint(NORTH));

        // 清除按钮
        // 设置左上角 水平坐标为5, 垂直坐标为 停止按钮的东边坐标+5
        SpringLayout.Constraints cons_clean = layout.getConstraints(btn_clean);
        cons_clean.setX(Spring.sum(cons_stop.getConstraint(EAST), Spring.constant(5)));
        cons_clean.setY(cons_start.getConstraint(NORTH));

        // 开始页数标签
        // 设置左上角 水平坐标为5, 垂直坐标为 清除按钮的东边坐标+5
        SpringLayout.Constraints cons_lab_startPage = layout.getConstraints(lab_startPage);
        cons_lab_startPage.setX(Spring.sum(cons_clean.getConstraint(EAST), Spring.constant(5)));
        cons_lab_startPage.setY(Spring.constant(5));

        // 开始页数文本框
        // 设置左上角 水平坐标为5, 垂直坐标为 开始页数标签的东边坐标+5
        SpringLayout.Constraints cons_txt_startPage = layout.getConstraints(txt_startpage);
        cons_txt_startPage.setX(cons_lab_startPage.getConstraint(EAST));
        cons_txt_startPage.setY(Spring.constant(5));

        // 爬取页数标签
        // 设置左上角 水平坐标为5, 垂直坐标为 开始页数文本框的东边坐标+5
        SpringLayout.Constraints cons_lab_pageCount = layout.getConstraints(lab_pageCount);
        cons_lab_pageCount.setX(Spring.sum(cons_txt_startPage.getConstraint(EAST), Spring.constant(5)));
        cons_lab_pageCount.setY(Spring.constant(5));

        // 爬取页数文本框
        // 设置左上角 水平坐标为5, 垂直坐标为 爬取页数标签的东边坐标+5
        SpringLayout.Constraints cons_txt_pageCount = layout.getConstraints(txt_pageCount);
        cons_txt_pageCount.setX(cons_lab_pageCount.getConstraint(EAST));
        cons_txt_pageCount.setY(Spring.constant(5));

        // 选择目录按钮
        // 设置左上角 水平坐标为5  垂直坐标为 开始按钮的南坐标+5
        SpringLayout.Constraints cons_choice = layout.getConstraints(btn_choice);
        cons_choice.setX(Spring.constant(5));
        cons_choice.setY(Spring.sum(cons_start.getConstraint(SOUTH), Spring.constant(5)));

        // 显示目录的文本框
        // 设置左上角 水平坐标为 选择目录按钮的东坐标+5 垂直坐标为 选择目录按钮的北坐标(居中)
        // 设置东侧与容器绑定
        SpringLayout.Constraints cons_path = layout.getConstraints(txt_path);
        cons_path.setX(Spring.sum(cons_choice.getConstraint(EAST), Spring.constant(5)));
        cons_path.setY(cons_choice.getConstraint(NORTH));
        layout.putConstraint(EAST, txt_path, -5, EAST, panel);

        // 输出信息文本框 (文本框在JScrollPane里面)
        // 设置左上角 水平坐标为5 垂直坐标为 选择目录按钮的南坐标+5
        // 设置东侧和南侧和容器绑定
        SpringLayout.Constraints cons_log = layout.getConstraints(scrollPane);
        cons_log.setX(Spring.constant(5));
        cons_log.setY(Spring.sum(cons_choice.getConstraint(SOUTH), Spring.constant(5)));
        layout.putConstraint(EAST, scrollPane, -5, EAST, panel);
        layout.putConstraint(SOUTH, scrollPane, -5, SOUTH, panel);
        /*
          putConstraint(String e1, Component c1, int pad, String e2, Component c2)可以为各个边之间建立约束

          e1：需要参考的组件对象的具体需要参考的边

          c1：需要参考的组件对象

          pad：两条边之间的距离（两个组件的间距）

          e2：被参考的组件对象的具体被参考的边

          c2：被参考的组件对象（也可以是需要参考的组件对象所属的容器对象）

          （注意：当e2在e1的北侧或西侧的时，pad应为正数；当e2在e1的南侧或东侧的时，pad应为负数）

          e1和e2的静态常量：EAST（组件东侧的边）、WEST（组件西侧的边）、NORTH（组件北侧的边）、SOUTH（组件南侧的边）
         */
    }

    /**
     * 开始执行爬虫，线程下调用
     */
    private static void start() {
        // 判断参数是否正确
        if (startPage <= 0 || pageCount <= 0) {
            appendError("页数填写不正确\n");
            return;
        }
        if ("".equals(root)){
            appendError("保存目录不能为空\n");
            return;
        }

        saveConfigFile();

        isForcedExit = false; // 每个循环后面都要判断是否强制退出
        btn_start.setEnabled(false);
        btn_start.setText("获取中...");
        btn_stop.setEnabled(true);
        btn_choice.setEnabled(false);
        txt_startpage.setEnabled(false);
        txt_pageCount.setEnabled(false);

        int errCounts = 0;
        int succsCount = 0;
        LeLe leLe = new LeLe();

        String pageRootUrl = "http://retoys.net/pickup/page/";
        for (int index = startPage; index < startPage + pageCount; index++) {
            String pageUrl = pageRootUrl + index + "/";
            // 1.获取pickup所有url
            appendLog("\n正在获取第" + index + "页下的所有作品连接\n\n");
            java.util.List<String> pickupUrls = leLe.getAllPickupUrl(pageUrl);
            appendLog("已经获取" + pickupUrls.size() + "个作品的链接\n");

            // 2. 获取每个pickup里面所有图片url
            String path = "";
            for (String pickupUrl : pickupUrls) {
                appendLog("\n开始获取第"+ index+ "页 " + pickupUrl + " 所有图片下载地址\n");
                List<String> picturl = leLe.getPictureUrls(pickupUrl);
                if (picturl.size() == 0) {
                    errCounts++;
                    appendError("获取 " + pickupUrl + " 所有图片地址失败\n");
                    continue;
                }
                for (int i = 0; i < picturl.size(); i++) {
                    if (i == 0) {
                        path = root + "\\" + leLe.formatString(picturl.get(i));
                        if (!Utils.createDir(path)) {
                            appendError("创建路径失败:" + path + "\n");
                            errCounts++;
                            break;
                        }
                        appendLog("获取到" + (picturl.size() - 1) + "个图片\n");
                    } else {
                        if (!"".equals(path)) {
                            if (!Utils.fileIsExists(path + "\\" + Utils.urlToFileNeme(picturl.get(i)))) {
                                if (FileDownload.download(picturl.get(i), path + "\\" + Utils.urlToFileNeme(picturl.get(i)))) {
                                    appendLog("下载成功：" + path + "\\" + Utils.urlToFileNeme(picturl.get(i)) + "\n");
                                    succsCount++;
                                } else {
                                    appendError("下载失败：" + path + "\\" + Utils.urlToFileNeme(picturl.get(i)) + "\n");
                                    errCounts++;
                                    // 删除下载失败的文件
                                    Utils.deleteFile(path + "\\" + Utils.urlToFileNeme(picturl.get(i)));
                                }
                            } else {
                                appendLog("文件已经存在: " + path + "\\" + Utils.urlToFileNeme(picturl.get(i)) + "\n");
                            }
                        }
                    }
                    if (isForcedExit){
                        break;
                    }
                }
                if (isForcedExit){
                    break;
                }
            }
            if (isForcedExit){
                break;
            }
        }
        appendError("\n  完成, 下载" + succsCount +"个, 失败" + errCounts + "个\n");


        if (isForcedExit){
            btn_stop.setText("停止");
            isForcedExit = false;
        }
        btn_stop.setEnabled(false);
        btn_start.setText("开始");
        btn_start.setEnabled(true);
        btn_choice.setEnabled(true);
        txt_startpage.setEnabled(true);
        txt_pageCount.setEnabled(true);
    }

    private static void appendLog(String log) {
        if (txt_log != null) {
            txt_log.append(log);
            txt_log.setSelectionStart(txt_log.getText().length());
        }
    }

    private static void appendError(String error) {
        if (txt_log != null) {
            txt_log.append(error);
            txt_log.setSelectionStart(txt_log.getText().length());
        }
    }

    private static void Log(Object o) {
        System.out.println(o);
    }
}
