package org.designer.tongrong_property_company_2nd.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/operate")
public class GetInformationToFile {
   @Autowired
    private JdbcTemplate jdbcTemplate;

   @GetMapping("/select/database/name")
    public String getInformationOfPointedDatabase(@RequestParam String nameOfPointedDatabaseName) {
        //白名单校验：只允许合法的表名
        List<String> validTableNames = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", String.class);

        if (!validTableNames.contains(nameOfPointedDatabaseName))
            throw new IllegalArgumentException("非法的表名: " + nameOfPointedDatabaseName);
        //安全拼接（经过校验后）
        String sql = "select * from " + nameOfPointedDatabaseName;
        return jdbcTemplate.queryForList(sql).toString();
    }
    @GetMapping("/write")
    public ResponseEntity<String> writeToFile(@RequestParam String path,
                                              @RequestParam String nameOfPointedDatabase) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true))) {
            //从数据库中读取数据
            String readFile = getInformationOfPointedDatabase(nameOfPointedDatabase);
            //写入文件并换行
            bufferedWriter.write(readFile);
            bufferedWriter.newLine();

            //返回成功信息
            return ResponseEntity.ok("数据已成功写入：" + path);

        } catch (IllegalArgumentException e) {
            //表名非法异常
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            // 文件IO异常
            return ResponseEntity.internalServerError()
                    .body("文件写入失败：" + e.getMessage());
        }
    }
}
