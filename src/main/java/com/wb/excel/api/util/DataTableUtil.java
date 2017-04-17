package com.wb.excel.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2014/10/12.
 *
 * @author
 * @version 0.1.0
 */
public class DataTableUtil {

    /**
     * 测试用方法。从本地文件读取字节数据。
     *
     * @param url 本地文件路径
     * @return 读取文件的byte数组
     * @throws java.io.IOException 读取文件中可能会抛出异常
     */
    public static byte[] readFile(String url) throws IOException {
        File file = new File(url);
        InputStream is = new FileInputStream(file);
        Long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("文件过大，无法读取");
        }

        byte[] bytes = new byte[length.intValue()];
        int offset = 0;
        int numRead;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        //如果得到的字节长度和file实际的长度不一致就可能出错了
        if (offset < bytes.length) {
            System.out.println("文件长度不一致");
        }
        is.close();
        return bytes;
    }
}
