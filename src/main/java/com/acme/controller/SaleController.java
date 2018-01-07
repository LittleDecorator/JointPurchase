package com.acme.controller;

import com.acme.model.Item;
import com.acme.model.Sale;
import com.acme.model.dto.SaleRequestDto;
import com.acme.repository.ItemRepository;
import com.acme.repository.SaleRepository;
import com.acme.repository.mapper.SaleMapper;
import com.acme.repository.specification.SaleSpecifications;
import com.google.common.base.Strings;
import com.ibm.icu.text.Transliterator;
import java.util.Date;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nikolay on 11.12.17.
 */

@RestController
@RequestMapping(value = "/api/sale")
public class SaleController {

    private static String RUSSIAN_TO_LATIN_BGN = "Russian-Latin/BGN";

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Получение списка акций
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Sale> getSales(@RequestParam(name = "active_only", required = false) boolean activeOnly) {
        if(activeOnly){
            return saleRepository.findAll(SaleSpecifications.active());
        }
        return saleRepository.findAll();
    }

    /**
     * Получение конкретной акции
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Sale getSale(@PathVariable("id") String id) {
        return saleRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/detail")
    @Transactional(readOnly = true)
    public Sale getByName(@RequestParam(name = "name") String transliteName){
        Sale sale = saleRepository.findOneByTransliteName(transliteName);
        for(Item item : sale.getItems()){
            item.setSalePrice(((Float)(item.getPrice() - (item.getSale().getDiscount() / 100f * item.getPrice()))).intValue());
        }
        return sale;
    }

    /**
     * Добавление новой акции
     *
     * @param sale
     * @return
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public Sale createSale(@RequestBody Sale sale) {
        sale.setTransliteName(translite(sale.getTitle()));
        return saleRepository.save(sale);
    }

    /**
     * Обновление существующей акции
     *
     * @param sale
     */
    @Transactional
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public void updateSale(@RequestBody Sale sale) {
        sale.setTransliteName(translite(sale.getTitle()));
        saleRepository.save(sale);
    }

    /**
     * Удаление клиента по ID
     *
     * @param id
     * @return
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteSale(@PathVariable("id") String id) {
        saleRepository.delete(id);
        return true;
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH)
    public void activateSale(@PathVariable("id") String id, @RequestParam("activate") boolean activate){
        Sale sale = saleRepository.findOne(id);
        sale.setActive(activate);
        saleRepository.save(sale);
    }

    /**
     *
     * @param all
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "translite")
    public void transliteItems(@RequestParam(name = "all", required = false, defaultValue = "true") Boolean all){
        Stream<Sale> sales = saleRepository.findAll().stream();
            if(!all){
                // фильтруем если нужно
                sales = sales.filter(item -> Strings.isNullOrEmpty(item.getTransliteName()));
            }
            // обновляем
        sales.forEach(sale -> {
                // транслит строиться по имени компании и названию товара
                sale.setTransliteName(translite(sale.getTitle()));
                saleRepository.save(sale);
            });
    }

    /**
     *
     * @param input
     * @return
     */
    private String translite(String input){
        Transliterator russianToLatinNoAccentsTrans = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
        return russianToLatinNoAccentsTrans.transliterate(input)
            .replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "")
            .replaceAll("\\*|\\s+","-")
            .toLowerCase();
    }
}
