package com.acme.controller;

import com.acme.gen.domain.Item;

import java.io.Serializable;
import java.util.List;

public interface GoodController {

    /*public class GoodView implements Serializable{
        private Item item;
        private String companyName;
        private String categoryName;

        public GoodView(Item item, String companyName, String categoryName) {
            this.item = item;
            this.companyName = companyName;
            this.categoryName = categoryName;
        }

        public Item getItem() {
            return item;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }*/

    public List<Item> getGoods();

    public Item getGoodById(String id);

    public List<Item> getGoodsByOrderId(String orderId);

    public List<Item> getGoodsByCompanyId(String companyId);

    public Item addGood(Item item);

//    public Item updateGood(Item item);


}
