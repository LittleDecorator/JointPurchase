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
            insert_def_content(jdbcTemplate, new ClassPathResource("public/custom/images/no_image_available.png"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-lollipop.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-lollipop-1.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-oceanblue.jpg"));
            insert_content(jdbcTemplate,new ClassPathResource("public/custom/images/petal-oceanblue-1.jpg"));
            insert_content(jdbcTemplate, new ClassPathResource("public/custom/images/snail-lollipop.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void insert_content(JdbcTemplate jdbcTemplate, Resource resource) throws IOException {
        String contentId = java.util.UUID.randomUUID().toString();
        String fileName = resource.getFilename();
        String path = "http://localhost:7979/custom/images/"+fileName;
        String mime = "image/"+fileName.substring(fileName.indexOf(".")+1);
        String fileContent = Base64BytesSerializer.serialize(resource.getInputStream());

        jdbcTemplate.execute("insert into content (id,content,file_name,mime,path) VALUES ('"+contentId+"','"+ fileContent +"','"+fileName+"','"+mime+"','"+path+"')");
    }

    private void insert_def_content(JdbcTemplate jdbcTemplate, Resource resource) throws IOException {
        String contentId = java.util.UUID.randomUUID().toString();
        String fileName = resource.getFilename();
        String path = "http://localhost:7979/custom/images/"+fileName;
        String mime = "image/"+fileName.substring(fileName.indexOf(".")+1);
        String fileContent = Base64BytesSerializer.serialize(resource.getInputStream());

        jdbcTemplate.execute("insert into content (id,content,file_name,is_default,mime,path) VALUES ('"+contentId+"','"+ fileContent+"','"+fileName+"','Y','"+ mime+"','"+path+"')");
    }
}
