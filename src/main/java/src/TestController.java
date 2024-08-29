package src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-db")
    public String testDb() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            return "数据库连接成功！测试查询结果: " + result;
        } catch (Exception e) {
            return "数据库连接失败：" + e.getMessage();
        }
    }
}
