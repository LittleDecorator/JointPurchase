package com.acme.model;

import java.util.List;

public interface Product extends BaseModel {

    void setUrl(String url);

    void setItemContents(List<ItemContent> itemContents);

    void setCategories(List<Category> categories);

    void setInWishlist(boolean inWishlist);
}
