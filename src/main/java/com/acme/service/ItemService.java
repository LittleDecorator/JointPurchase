package com.acme.service;


import com.acme.model.Item;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemUrlTransfer;

import java.util.List;

public interface ItemService {

    List<ItemUrlTransfer> getItemUrlTransfers(List<Item> items);
    ItemMediaTransfer getItemMediaTransfers(Item item);
}
