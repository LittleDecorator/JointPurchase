package com.acme.controller;

import com.acme.model.Wishlist;
import com.acme.model.dto.WishlistCountDto;
import com.acme.model.dto.WishlistDto;
import com.acme.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by nikolay on 24.09.17.
 */

@RestController
@RequestMapping(value = "/api/wishlist")
public class WishlistController {

    @Autowired
    WishlistService service;

    /**
     * Возвращаем список воспользовавшихся списком желаемого
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<WishlistCountDto> getWishLists() {
        return service.getCounted();
    }

    /**
     * Получения отложенного с группировкой по клиенту
     * @param email
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{email:.+}")
    public WishlistDto getGroupLists(@PathVariable("email") String email) {
        return service.getClientWish(email);
    }

    /**
     * Колво отложенного по клиенту
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/count")
    public long getCount() {
        return service.getCount();
    }

    /**
     * Создание заказа
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Wishlist createOrder(@RequestBody Wishlist request) {
        return service.addItemToWishlist(request);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{email}")
    @Transactional
    public void deleteList(@PathVariable("email") String email) {
        service.deleteWishlist(email);
    }

    /**
     * Удаление заказа по ID
     *
     * @param id - order ID
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{email}/{id}")
    public void deleteItemFromList(@PathVariable("id") String id) {
        service.removeItemFromWishlist(id);
    }
}
