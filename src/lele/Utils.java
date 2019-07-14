package lele;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * 从url获取文件名
     *
     * @param url url
     * @return 文件名
     */
    public static String urlToFileNeme(String url) {
        // 正则表达式“.+/(.+)$”的含义就是：被匹配的字符串以任意字符序列开始，后边紧跟着字符“/”，
        // 最后以任意字符序列结尾，“()”代表分组操作，这里就是把文件名做为分组，匹配完毕我们就可以通过Matcher
        // 类的group方法取到我们所定义的分组了。需要注意的这里的分组的索引值是从1开始的，所以取第一个分组的方法是m.group(1)而不是m.group(0)。
        String regEx = ".+/(.+)$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        if (!m.find()) {
            return "";
        }
        return m.group(1);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return true 成功 false 失败
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 创建目录
     *
     * @param dir 目录
     * @return true 存在 false 不存在
     */
    public static boolean createDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 判断问文件是否存在
     *
     * @param strFile 文件名
     * @return true 成功 fasle 失败
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 读取按行号读取文件内容
     *
     * @param num      行号
     * @param filePath 文件名
     * @return 文件内容
     */
    public static String readFileLine(int num, String filePath) {
        String retStr = null;
        int index = 0;

        FileReader fr = null;
        BufferedReader bf = null;
        try {
            fr = new FileReader(filePath);
            bf = new BufferedReader(fr);


            while ((retStr = bf.readLine()) != null) {
                if (index+1 == num) {
                    break;
                }
                index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return retStr == null ? "" : retStr;
    }

    /**
     * 保存内容到文件
     *
     * @param txt      文本
     * @param append   是否追加
     * @param filePath 文件路径
     */
    public static void writeFile(String txt, boolean append, String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, append);
            fileWriter.write(txt);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int stringToInt(String string) {
        if ("".equals(string)) {
            return 0;
        }
        // 判断是否为纯数字
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!pattern.matcher(string).matches()) {
            return 0;
        }
        return Integer.parseInt(string.trim());
    }

    public static String intToString(int i) {
        return String.valueOf(i);
    }
}
