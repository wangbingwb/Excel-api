import com.wb.excel.api.annotation.Description;
import com.wb.excel.api.annotation.Name;

public class UserPO {

    @Description("用户姓名")
    @Name("用户姓名")
    private String name;
    @Name("用户密码")
    @Description("用户密码")
    private String password;

    public UserPO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public UserPO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
