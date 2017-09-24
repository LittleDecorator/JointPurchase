package com.acme.service;

import com.acme.model.Wishlist;
import com.acme.model.dto.WishlistCountDto;
import com.acme.model.dto.WishlistDto;

import java.util.List;

/**
 * Created by nikolay on 24.09.17.
 */
public interface WishlistService {

    List<WishlistCountDto> getCounted();

    WishlistDto getClientWish(String email);

    long getCount();

    Wishlist addItemToWishlist(Wishlist wishlist);

    void deleteWishlist(String email);

    void removeItemFromWishlist(String id);

    List<String> getWishedItems(String email);
}
