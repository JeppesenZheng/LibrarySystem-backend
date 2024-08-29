package src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "src.repository")
@EntityScan(basePackages = {"src.model", "src.User"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Bean
	public CommandLineRunner testDatabaseConnection() {
		return args -> {
			try {
				String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
				System.out.println("数据库连接成功！测试查询结果: " + result);
			} catch (Exception e) {
				System.err.println("数据库连接失败：" + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}
