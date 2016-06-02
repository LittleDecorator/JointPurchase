package com.acme;

import com.acme.repository.CategoryItemRepository;
import com.acme.repository.CategoryRepository;
import com.acme.repository.ItemRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
@IntegrationTest("server.port:0")
public class ItemControllerTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CategoryItemRepository categoryItemRepository;
    @Autowired
    CategoryRepository categoryRepository;

    String key = "5e24b8ea-8a81-4984-ae92-fed630d5b859";

    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void canFetchItem() {
        Response response = when().get("/item/{id}", key);
        System.out.println(response.asString());

        response.then().assertThat().
                statusCode(HttpStatus.SC_OK).
                and().body("companyId", Matchers.is("40636ca3-6c19-4ecb-83a2-9027dcd5b22f")).
                and().body("id", Matchers.is(key));
    }

}
