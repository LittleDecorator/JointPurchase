package db.migration;

import com.acme.db.handlers.Base64BytesSerializer;
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class V3__Content implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {

        try {
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/no_image.png"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-lollipop.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-lollipop-1.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-oceanblue.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-oceanblue-1.jpg"));
            insert_content(jdbcTemplate, new ClassPathResource("public/custom/images/snail-lollipop.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String insert_content(JdbcTemplate jdbcTemplate, Resource resource) throws IOException {
        String contentId = java.util.UUID.randomUUID().toString();

        jdbcTemplate.execute("insert into content (id,content,file_name) VALUES ('"+contentId+"','"+ Base64BytesSerializer.serialize(resource.getInputStream())+"','"+resource.getFilename()+"')");
        return contentId;
    }
}
