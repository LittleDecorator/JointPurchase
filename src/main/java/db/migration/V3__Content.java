package db.migration;

import com.acme.handlers.Base64BytesSerializer;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class V3__Content implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        try {
            insert_def_content(jdbcTemplate, new ClassPathResource("no_image_available.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void insert_def_content(JdbcTemplate jdbcTemplate, Resource resource) throws IOException {
        String contentId = java.util.UUID.randomUUID().toString();
        String fileName = resource.getFilename();
        String type = fileName.substring(fileName.indexOf(".")+1);
        String mime = "image/"+type;

        String fileContent = Base64BytesSerializer.serialize(resource.getInputStream());

        jdbcTemplate.execute("insert into content (id,content,file_name,is_default,mime,type) VALUES ('"+contentId+"','"+ fileContent+"','"+fileName+"','Y','"+ mime+"','"+type+"')");
    }
}
