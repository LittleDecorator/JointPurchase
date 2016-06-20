package com.acme.model.dto;

import com.acme.model.Item;

public class ItemView extends Item {

    String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "ItemView{" +
                "companyName='" + companyName + '\'' +
                "} " + super.toString();
    }
}
