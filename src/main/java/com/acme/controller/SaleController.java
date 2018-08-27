package com.acme.controller;

import com.acme.model.Item;
import com.acme.model.Sale;
import com.acme.model.dto.SaleDto;
import com.acme.model.dto.SaleRequestDto;
import com.acme.model.dto.mapper.SaleMapper;
import com.acme.repository.ItemRepository;
import com.acme.repository.SaleRepository;
import com.acme.repository.specification.SaleSpecifications;
import com.google.common.base.Strings;
import com.ibm.icu.text.Transliterator;
import java.util.Set;
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

    @Autowired
    private SaleMapper saleMapper;

    /**
     * Получение списка акций
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<SaleDto> getSales(@RequestParam(name = "active_only", required = false) boolean activeOnly) {
        List<Sale> sales = activeOnly ? saleRepository.findAll(SaleSpecifications.active()) : saleRepository.findAll();
        return saleMapper.toSimpleDto(sales);
    }

    /**
     * Получение конкретной акции
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public SaleDto getSale(@PathVariable("id") String id) {
        return saleMapper.toDto(saleRepository.findOne(id));
    }

    /**
     *
     * @param transliteName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/detail")
    @Transactional(readOnly = true)
    public SaleDto getByName(@RequestParam(name = "name") String transliteName){
        Sale sale = saleRepository.findOneByTransliteName(transliteName);
        return saleMapper.toDto(sale);
    }

    /**
     * Добавление новой акции | Обновление существующей акции
     *
     * @param dto
     */
    @Transactional
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, value = {"","/{id}"})
    public SaleDto updateSale(@RequestBody SaleRequestDto dto) {
        Set<Item> items = itemRepository.findAllByIdIn(dto.getItems());
        Sale sale = saleMapper.requestToEntity(dto);
        sale.setTransliteName(translite(dto.getTitle()));
        sale.setItems(items);
        saleRepository.save(sale);
        return saleMapper.toDto(sale);
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
