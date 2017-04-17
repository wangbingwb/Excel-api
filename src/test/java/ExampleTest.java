import com.wb.excel.api.Excel;
import com.wb.excel.api.datatable.DataTable;
import com.wb.excel.api.exception.TemplateNotMatchException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实例
 */
public class ExampleTest {

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        testExport();
//        testImport();
    }

    public static void testExport() {

        try {
            //第一步,准备数据模型及数据，模型需要打上注解
            List<UserPO> pos = new ArrayList();
            pos.add(new UserPO("张三", "zs123"));
            pos.add(new UserPO("李四", "ls123"));

            //第二步,初始化数据
            DataTable dataTable = new DataTable(pos);

            //第三步,初始化Excel
            Excel excel = new Excel(false, dataTable);

            //第四步，导出xlsx文件
            output("user.xlsx", excel.getBytes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static void testImport() {
        try {
            File file = new File("user.xlsx");
            FileInputStream stream = new FileInputStream(file);

            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);

            DataTable dataTable = new DataTable(bytes, UserPO.class);

            if (dataTable.hasError()) {
                Excel excel = new Excel(true, dataTable);
                output("user_err.xlsx", excel.getBytes());
            } else {
                List<UserPO> list = dataTable.transferList(UserPO.class);
                System.out.println("本次读取数据" + list.size() + "条!");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (TemplateNotMatchException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void output(String path, byte[] bytes) {
        try {
            File file = new File(path);
            file.createNewFile();

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
            } finally {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
