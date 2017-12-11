package com.acme.controller;

import com.acme.model.Sale;
import com.acme.repository.SaleRepository;
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

    @Autowired
    SaleRepository saleRepository;

    /**
     * Получение списка акций
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Sale> getSales() {
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


    /**
     * Добавление новой акции
     *
     * @param sale
     * @return
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public Sale createSale(@RequestBody Sale sale) {
        return saleRepository.save(sale);
    }

    /**
     * Обновление существующей акции
     *
     * @param sale
     */
    @Transactional
    @RequestMapping(method = RequestMethod.PUT)
    public void updateSale(@RequestBody Sale sale) {
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
}
