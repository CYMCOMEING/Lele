package lele;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import static javax.swing.SpringLayout.*;

public class Main {

    private static String root = "."; // 文件输出路径
    private static String config_path = "config.ini"; // 配置文件路径

    private static JFrame mainFrame;
    private static JButton btn_start;
    private static JButton btn_choice;
    private static JTextField txt_path;
    private static JTextArea txt_log;

    public static void main(String[] args) {
        // 创建 JFrame 实例
        mainFrame = new JFrame("Java");
        // Setting the width and height of frame
        mainFrame.setSize(400, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 禁止改变大小
//        frame.setResizable(false);

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

    private static void laodConfig() {

        // 读取上次使用的路径
        if (Utils.fileIsExists(config_path)){
            String readPath = Utils.readFileLine(1, new File(config_path).getAbsolutePath());
            if (!"".equals(readPath)){
                root = readPath;
            }
        }


        // 初始化文本框的内容
        if (txt_path != null){
            File file = new File(root);
            txt_path.setText(file.getAbsolutePath());
        }
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

        // 清空消息按钮
        JButton btn_clean = new JButton("清空消息");
        btn_clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txt_log != null){
                    txt_log.setText("");
                }
            }
        });
        panel.add(btn_clean);

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
                if (result == JFileChooser.APPROVE_OPTION){
                    // 点击了确定
                    File file = fc.getSelectedFile();
                    if (txt_path != null){
                        Utils.wiriteFile(file.getAbsolutePath(), config_path);
                        txt_path.setText(file.getAbsolutePath());
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
        txt_log.setForeground(Color.BLACK);
        // 给文本框添加滚动条
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(txt_log);
        panel.add(scrollPane);

        // 组件约束关系 /////////////////////////////////////////////////////////////////////////////

        // 开始按钮
        // 设置标签的左上角坐标为 (5, 5)
        SpringLayout.Constraints cons_start = layout.getConstraints(btn_start);
        cons_start.setX(Spring.constant(5));
        cons_start.setY(Spring.constant(5));

        // 清除按钮
        // 设置左上角 水平坐标为5, 垂直坐标为 开始按钮的东边坐标+5
        SpringLayout.Constraints cons_clean = layout.getConstraints(btn_clean);
        cons_clean.setX(Spring.sum(cons_start.getConstraint(EAST), Spring.constant(5)));
        cons_clean.setY(Spring.constant(5));

        // 选择目录按钮
        // 设置左上角 水平坐标为5  垂直坐标为 开始按钮的南坐标+5
        SpringLayout.Constraints cons_choice = layout.getConstraints(btn_choice);
        cons_choice.setX(Spring.constant(5));
        cons_choice.setY(Spring.sum(cons_start.getConstraint(SOUTH),Spring.constant(5)));

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
        btn_start.setEnabled(false);
        btn_start.setText("获取中...");
        btn_choice.setEnabled(false);

        int errCounts = 0;
        LeLe leLe = new LeLe();
        // 1.获取pickup所有url
        appendLog("正在获取pick up的连接\n");
        java.util.List<String> pickupUrls = leLe.getPickUpUrl();
        appendLog("已经获取" + pickupUrls.size() + "个连接\n");

        // 2. 获取每个pickup里面所有图片url
        String path = "";
        for (String pickupUrl : pickupUrls) {
            appendLog("开始获取 "+pickupUrl+" 所有图片下载地址\n");
            List<String> picturl = leLe.getPictureUrls(pickupUrl);
            if (picturl.size() == 0){
                errCounts++;
                appendError("获取 "+pickupUrl+" 所有图片地址失败\n");
                continue;
            }
            for (int i = 0; i < picturl.size(); i++) {
                if (i == 0) {
                    path = root + "\\" + leLe.formatString(picturl.get(i));
                    if (!Utils.createDir(path)) {
                        appendError("创建路径失败:" + path +"\n");
                        errCounts++;
                        break;
                    }
                    appendLog("获取到" + (picturl.size() - 1) + "个图片\n");
                } else {
                    if (!"".equals(path)) {
                        if (!Utils.fileIsExists(path + "\\" + Utils.urlToFileNeme(picturl.get(i)))) {
                            if (FileDownload.download(picturl.get(i), path + "\\" + Utils.urlToFileNeme(picturl.get(i)))) {
                                appendLog("下载成功：" + path + "\\" + Utils.urlToFileNeme(picturl.get(i))+ "\n");
                            }else{
                                appendError("下载失败：" + path + "\\" + Utils.urlToFileNeme(picturl.get(i))+ "\n");
                                errCounts++;
                                // 删除下载失败的文件
                                Utils.deleteFile(path + "\\" + Utils.urlToFileNeme(picturl.get(i)));
                            }
                        } else {
                            appendLog("文件已经存在: " + path + "\\" + Utils.urlToFileNeme(picturl.get(i))+ "\n");
                        }
                    }
                }
            }
        }
        appendLog("下载完成, 失败数" + errCounts + "个\n");


        btn_start.setText("开始");
        btn_start.setEnabled(true);
        btn_choice.setEnabled(true);
    }

    private static void appendLog(String log){
        if (txt_log != null) {
            txt_log.append(log);
        }
    }
    private static void appendError(String error){
        if (txt_log != null) {
            txt_log.append(error);
        }
    }

    private static void Log(Object o) {
        System.out.println(o);
    }
}
