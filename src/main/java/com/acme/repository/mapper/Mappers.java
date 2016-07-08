package com.acme.repository.mapper;

import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.*;
import com.acme.model.dto.ItemView;
import com.acme.model.dto.Product;
import com.acme.util.MapperHelper;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.util.Objects;

public class Mappers {

    public final static RowMapper<Item> itemMapper = (rs,num) -> {
        Item item = new Item();
        item.setId(rs.getString("id"));
        item.setName(rs.getString("name"));
        item.setCompanyId(rs.getString("company_id"));
        item.setArticle(rs.getString("article"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getInt("price"));
        item.setNotForSale(rs.getString("not_for_sale").charAt(0)=='Y');
        item.setInStock(rs.getInt("in_stock"));
        item.setDateAdd(rs.getDate("date_add"));
        return item;
    };

    public final static RowMapper<ItemView> itemViewMapper = (rs,num) -> {
        ItemView item = new ItemView();
        item.setId(rs.getString("id"));
        item.setName(rs.getString("name"));
        item.setCompanyId(rs.getString("company_id"));
        item.setCompanyId(rs.getString("company_id"));
        item.setCompanyName(rs.getString("company_name"));
        item.setArticle(rs.getString("article"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getInt("price"));
        item.setNotForSale(rs.getString("not_for_sale").charAt(0)=='Y');
        item.setInStock(rs.getInt("in_stock"));
        item.setDateAdd(rs.getDate("date_add"));
        return item;
    };

//    public final static RowMapper<ItemSearchResult> itemSearchMapper = (rs,num) -> {
//        Item item = new Item();
//        item.setId(rs.getString("id"));
//        item.setName(rs.getString("name"));
//        item.setCompanyId(rs.getString("company_id"));
//        item.setArticle(rs.getString("article"));
//        item.setDescription(rs.getString("description"));
//        item.setPrice(rs.getBigDecimal("price"));
//        item.setNotForSale(rs.getBoolean("not_for_sale"));
//        item.setInStock(rs.getInt("in_stock"));
//        item.setDateAdd(rs.getDate("date_add"));
//
//        ItemSearchResult searchResult = new ItemSearchResult();
//        searchResult.setItem(item);
//        searchResult.setCategoryId(rs.getString("category_id"));
//        searchResult.setContentId(rs.getString("content_id"));
//
//        return searchResult;
//    };

    public final static RowMapper<CategoryItem> categoryItemMapper = (rs,num) -> {
        CategoryItem categoryItem = new CategoryItem();
        categoryItem.setId(rs.getString("id"));
        categoryItem.setCategoryId(rs.getString("category_id"));
        categoryItem.setCategoryName(MapperHelper.getExistString(rs,"name"));
        categoryItem.setItemId(rs.getString("item_id"));
        categoryItem.setDateAdd(rs.getDate("date_add"));
        return categoryItem;
    };

    public final static RowMapper<Category> categoryMapper = (rs,num) -> {
        Category category = new Category();
        category.setId(rs.getString("id"));
        category.setName(rs.getString("name"));
        category.setDateAdd(rs.getTimestamp("date_add"));
        category.setParentId(rs.getString("parent_id"));
        return category;
    };

    public final static RowMapper<Company> companyMapper = (rs,num) -> {
        Company company = new Company();
        company.setId(rs.getString("id"));
        company.setName(rs.getString("name"));
        company.setDescription(rs.getString("description"));
        company.setAddress(rs.getString("address"));
        company.setEmail(rs.getString("email"));
        company.setPhone(rs.getString("phone"));
        company.setUrl(rs.getString("url"));
        company.setBik(rs.getString("bik"));
        company.setInn(rs.getString("inn"));
        company.setKs(rs.getString("ks"));
        company.setRs(rs.getString("rs"));
        company.setDateAdd(rs.getDate("date_add"));
        return company;
    };

    public final static RowMapper<Content> contentMapper = (rs,num) -> {
        Content content = new Content();
        try {
            content.setId(rs.getString("id"));
            content.setContent(Base64BytesSerializer.deserialize(rs.getString("content")));
            content.setFileName(rs.getString("file_name"));
            content.setMime(rs.getString("mime"));
            content.setType(rs.getString("type"));
            content.setIsDefault(rs.getBoolean("is_default"));
            content.setDateAdd(rs.getDate("date_add"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(content);
        return content;
    };

    public final static RowMapper<Credential> credentialMapper = (rs,num) -> {
        Credential credential = new Credential();
        credential.setSubjectId(rs.getString("subject_id"));
        credential.setPassword(rs.getString("password"));
        credential.setRoleId(rs.getString("role_id"));
        credential.setDateAdd(rs.getDate("date_add"));
        return credential;
    };

    public final static RowMapper<Product> productMapper = (rs,num) -> {
        Product product = new Product();
        product.setContentId(rs.getString("content_id"));
        product.setId(rs.getString("id"));
        product.setName(rs.getString("name"));
        product.setCompanyId(rs.getString("company_id"));
        product.setCompanyName(MapperHelper.getExistString(rs,"company_name"));
        product.setArticle(rs.getString("article"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getInt("price"));
        product.setNotForSale(rs.getString("not_for_sale").charAt(0)=='Y');
        product.setInStock(rs.getInt("in_stock"));
        product.setDateAdd(rs.getDate("date_add"));
        return product;
    };

    public final static RowMapper<CategorizeItem> itemCategoryLinkMapper = (rs,num) -> {
        CategorizeItem link = new CategorizeItem();
        link.setId(rs.getString("id"));
        link.setArticle(rs.getString("article"));
        link.setName(rs.getString("name"));
        link.setInStock(rs.getInt("in_stock"));
        link.setNotForSale(rs.getString("not_for_sale").charAt(0)=='Y');
        link.setPrice(rs.getInt("price"));
        link.setDescription(rs.getString("description"));
        link.setCompanyId(rs.getString("company_id"));
        return link;
    };

    public final static RowMapper<ItemContent> itemContentMapper = (rs, num) -> {
        ItemContent itemContent = new ItemContent();
        itemContent.setId(rs.getString("id"));
        itemContent.setItemId(rs.getString("item_id"));
        itemContent.setContentId(rs.getString("content_id"));
        itemContent.setShow(rs.getBoolean("show"));
        itemContent.setMain(rs.getBoolean("main"));
        itemContent.setDateAdd(rs.getDate("date_add"));
        return itemContent;
    };

    public final static RowMapper<OrderItem> orderItemMapper = (rs,num) -> {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getString("id"));
        orderItem.setCou(rs.getInt("cou"));
        orderItem.setItemId(rs.getString("item_id"));
        orderItem.setOrderId(rs.getString("order_id"));
        orderItem.setDateAdd(rs.getDate("date_add"));
        return orderItem;
    };

    public final static RowMapper<PurchaseOrder> purchaseOrderMapper = (rs,num) -> {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(rs.getString("id"));
        order.setSubjectId(rs.getString("subject_id"));
        order.setUid(rs.getLong("uid"));
        order.setRecipientFname(rs.getString("RECIPIENT_FNAME"));
        order.setRecipientMname(rs.getString("RECIPIENT_MNAME"));
        order.setRecipientLname(rs.getString("RECIPIENT_LNAME"));
        order.setRecipientPhone(rs.getString("RECIPIENT_PHONE"));
        order.setRecipientEmail(rs.getString("RECIPIENT_EMAIL"));
        order.setRecipientAddress(rs.getString("RECIPIENT_ADDRESS"));
        order.setCloseOrderDate(rs.getDate("CLOSE_ORDER_DATE"));
        order.setComment(rs.getString("COMMENT"));
        order.setStatus(rs.getString("STATUS"));
        order.setDelivery(rs.getString("DELIVERY"));
        order.setPayment(rs.getInt("PAYMENT"));
        order.setDateAdd(rs.getDate("date_add"));
        return order;
    };

    public final static RowMapper<Subject> subjectMapper = (rs,num) -> {
        Subject subject = new Subject();
        subject.setId(rs.getString("id"));
        subject.setEnabled(Objects.equals(rs.getString("enabled"), "Y"));
        subject.setFirstName(rs.getString("first_name"));
        subject.setMiddleName(rs.getString("middle_name"));
        subject.setLastName(rs.getString("last_name"));
        subject.setPhoneNumber(rs.getString("phone_number"));
        subject.setEmail(rs.getString("email"));
        subject.setAddress(rs.getString("address"));
        subject.setPhoneNumber(rs.getString("post_address"));
        subject.setDateAdd(rs.getDate("date_add"));
        return subject;
    };
}
