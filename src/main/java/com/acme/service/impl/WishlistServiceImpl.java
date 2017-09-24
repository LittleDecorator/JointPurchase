package com.acme.service.impl;

import com.acme.model.Wishlist;
import com.acme.model.dto.WishlistCountDto;
import com.acme.model.dto.WishlistDto;
import com.acme.repository.WishlistRepository;
import com.acme.service.ItemService;
import com.acme.service.WishlistService;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by nikolay on 24.09.17.
 */

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    WishlistRepository repository;

    @Autowired
    ItemService itemService;

    @Override
    public List<WishlistCountDto> getCounted() {
        List<WishlistCountDto> result = Collections.emptyList();
        List<Wishlist> wishlists = repository.findAll();
        if(!wishlists.isEmpty()){
            Map<String, List<Wishlist>> groups = wishlists.stream().collect(Collectors.groupingBy(Wishlist::getEmail));
            result = groups.entrySet().stream().map(entry -> {
                WishlistCountDto dto = new WishlistCountDto();
                dto.setEmail(entry.getKey());
                Optional<Wishlist> first = entry.getValue().stream().filter(stashed -> !Strings.isNullOrEmpty(stashed.getSubjectId())).findFirst();
                if(first.isPresent()){
                    dto.setSubjectId(first.get().getSubjectId());
                }
                dto.setCount(groups.get(entry.getKey()).size());
                return dto;
            }).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public WishlistDto getClientWish(String email) {
        WishlistDto result = null;
        List<Wishlist> wishlists = repository.findAllByEmail(email);
//        wishlists = repository.findByEmail(email);
        if(!wishlists.isEmpty()){
            Wishlist first = wishlists.get(0);
            result = new WishlistDto();
            result.setEmail(first.getEmail());
            result.setSubjectId(first.getSubjectId());
            List<String> itemIds = wishlists.stream().map(Wishlist::getItemId).collect(Collectors.toList());
            result.setItems(itemService.getAllByIdList(itemIds));
        }
        return result;
    }

    @Override
    public long getCount() {
        return repository.count();
    }

    @Override
    public Wishlist addItemToWishlist(Wishlist wishlist) {
        return repository.save(wishlist);
    }

    @Override
    public void deleteWishlist(String email) {
        repository.deleteAllByEmail(email);
    }

    @Override
    public void removeItemFromWishlist(String id) {
        repository.delete(id);
    }

    @Override
    public List<String> getWishedItems(String email) {
        List<String> result = Collections.emptyList();
        List<Wishlist> wishlists = repository.findAllByEmail(email);
        if(!wishlists.isEmpty()){
            result = wishlists.stream().map(Wishlist::getItemId).collect(Collectors.toList());
        }
        return result;
    }
}
