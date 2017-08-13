package com.acme;

import com.acme.enums.ItemStatus;
import com.acme.model.Item;
import com.acme.repository.ItemRepository;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nikolay on 11.02.17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class RepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    ItemRepository repository;

    @Test
    @Transactional(rollbackFor=RuntimeException.class)
    public void repositoryRunner() {
        // fetch all items
        log.info("Items found with findAll():");
        log.info("-------------------------------");
        for (Item item : repository.findAll()) {
            log.info(item.toString());
        }
        log.info("");

        // fetch an individual item by ID
        log.info("Item found with findOne(5e24b8ea-8a81-4984-ae92-fed630d5b859):");
        Item item = repository.findOne("5e24b8ea-8a81-4984-ae92-fed630d5b859");
        log.info("--------------------------------");
        log.info(item.toString());
        log.info("");

        // fetch items by companyId
        log.info("Customer found with findByCompanyId('40636ca3-6c19-4ecb-83a2-9027dcd5b22f'):");
        log.info("--------------------------------------------");
        for (Item grimms : repository.findByCompanyId("40636ca3-6c19-4ecb-83a2-9027dcd5b22f")) {
            log.info(grimms.toString());
        }
        log.info("");

        // fetch items by id list
        log.info("Items found with findByIdIn():");
        log.info("-------------------------------");
        for (Item byIn : repository.findByIdIn(Lists.newArrayList("b172e14f-33fd-4819-9831-09c54541feb4","5e24b8ea-8a81-4984-ae92-fed630d5b859"))) {
            log.info(byIn.toString());
        }
        log.info("");

        // save a couple of Items
        log.info("Items added with save():");
        log.info("-------------------------------");
        Item newItem = new Item();
        newItem.setName("15 друзей Васечки");
//        newItem.setCompanyId("40636ca3-6c19-4ecb-83a2-9027dcd5b22f");
        newItem.setArticle("10580");
        newItem.setDescription("Seven Friends in 7 bowls: Sorting and matching peg dolls in wooden frame (for each day). Tip: You can style the peg dolls with cloth or tape. Our Bus (09480) fits perfectly with the peg dolls. Each hand-painted peg doll is unique! Materials: alder and maple wood, non-toxic water based color stain/non-toxic plant based oil finish. Size: frame diameter 19cm, peg dolls height 6cm, diameter 3cm.");
        newItem.setPrice(1000);

//        newItem.setNotForSale(true);
//        newItem.setInStock(1);
//        newItem.setDateAdd(new Date());
//        newItem.setStatus(ItemStatus.AVAILABLE);

        Item saved = repository.save(newItem);
        log.info(saved.toString());
        log.info("");

        // update saved Item
        log.info("Items update with save():");
        log.info("-------------------------------");
        saved.setName("15 друзей Васечки");
        saved.setDescription("Seven Friends in 7 bowls: ");
        saved.setPrice(1500);

        saved.setNotForSale(false);
        saved.setInStock(10);
        saved.setStatus(ItemStatus.AWAIT);

        Item updated = repository.save(saved);
        log.info(updated.toString());
        log.info("");

        // delete updated Item
        log.info("Delete updated item with delete(ID):");
        log.info("-------------------------------");
        log.info("----------BF DELETE---------");
        for (Item bf : repository.findAllByOrderByDateAddAsc()) {
            log.info(bf.toString());
        }
        log.info("");
        log.info("----------DELETE---------");
//        repository.delete(updated.getId());
        repository.delete("5e24b8ea-8a81-4984-ae92-fed630d5b859");

        log.info("----------AF DELETE---------");
        for (Item af : repository.findAllByOrderByDateAddAsc()) {
            log.info(af.toString());
        }
        log.info("");
    }

}
