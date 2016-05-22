package com.acme.config;

public interface Queue {

    String CATEGORY_NAME_MAP = "SELECT id, name FROM public.category";
    String CATEGORY_FIND_ALL = "SELECT * FROM public.category";
    String CATEGORY_FIND_ALL_ROOTS = "SELECT id, name FROM public.category WHERE parent_id is NULL";
    String CATEGORY_FIND_BY_ID = "SELECT * FROM public.category WHERE id = ? ";
    String CATEGORY_FIND_BY_PARENT_ID = "SELECT * FROM public.category WHERE parent_id = ? ";
    String CATEGORY_FIND_BY_ID_LIST = "SELECT * FROM public.category WHERE id in (:ids) ";
    String CATEGORY_UPDATE_BY_ID = "UPDATE public.category set name = ?, parent_id = ?, date_add = ? WHERE id = ? ";
    String CATEGORY_INSERT = "INSERT INTO public.category(id, name, parent_id, date_add) values(?,?,?,?)";
    String CATEGORY_DELETE = "DELETE FROM public.category WHERE id = ? OR parent_id = ?";

    String CATEGORY_ITEM_INSERT = "INSERT INTO public.category_item (id, category_id, item_id, date_add) values (?, ?, ?, ?) ";
    String CATEGORY_ITEM_FIND_BY_CATEGORY_ID = "SELECT * FROM public.category_item WHERE category_id = ? ";
    String CATEGORY_ITEM_FIND_BY_ITEM_ID = "SELECT * FROM public.category_item WHERE item_id = ? ";
    String CATEGORY_ITEM_DELETE_BY_ITEM_ID = "DELETE FROM public.category_item WHERE item_id = ?";
    String CATEGORY_ITEM_DELETE_BY_ITEM_LIST = "DELETE FROM public.category_item WHERE item_id in (:itemIdList)";
    String CATEGORY_ITEM_DELETE_BY_EXCLUDE_ITEM_LIST = "DELETE FROM public.category_item WHERE item_id not in (:itemIdList)";
    String CATEGORY_ITEM_DELETE_BY_CATEGORY_AND_EXCLUDE_ITEM_LIST = "DELETE FROM public.category_item WHERE category_id = :categoryId and item_id not in (:itemIdList)";
    String CATEGORY_ITEM_DELETE_BY_CATEGORY_ID = "DELETE FROM public.category_item WHERE category_id = ?";

    String ITEM_FIND_ALL = "SELECT * FROM public.item ORDER BY date_add asc";
    String ITEM_FIND_BY_SEARCH = "SELECT * FROM public.item i " +
            "WHERE lower(i.name) LIKE :criteria OR i.article LIKE :criteria OR i.description = :criteria " +
            "ORDER BY i.date_add ASC";
    String ITEM_FIND_BY_ID = "SELECT * FROM public.item WHERE id = ?";
    String ITEM_FIND_BY_ID_LIST = "SELECT * FROM public.item WHERE id in (:idList)";
    String ITEM_BY_COMPANY_FOR_SALE = "SELECT * FROM public.item WHERE company_id = ? AND not_for_sale = 'N'";
    String ITEM_BY_COMPANY_ID = "SELECT * FROM public.item WHERE company_id = ? ";
    String ITEM_DELETE_BY_ID = "DELETE FROM public.item WHERE id = ? ";

    String COMPANY_FIND_ALL = " SELECT * FROM public.company";
    String COMPANY_FIND_BY_ID = " SELECT * FROM public.company WHERE id = ? ";
    String COMPANY_DELETE_BY_ID = "DELETE FROM public.company WHERE id= ?";
    String COMPANY_INSERT = "INSERT INTO public.company (id, name, description, address, email, phone, url, bik, inn, ks, rs, date_add) " +
            "values (:id, :name, :description, :address, :email, :phone, :url, :bik, :inn, :ks, :rs, :dateAdd)";

    String CREDENTIAL_FIND_BY_ID = "SELECT * FROM public.credential where subject_id = ? ";
    String CREDENTIAL_UPDATE_BY_ID = "UPDATE public.credential SET password = ?, role_id = ?, date_add = ? WHERE subject_id = ? ";
    String CREDENTIAL_INSERT = "INSERT INTO public.credential (subject_id, password, role_id, date_Add) " +
            " values (:subjectId, :password, :roleId, :dateAdd)";

    String SUBJECT_FIND_ALL = "SELECT * FROM public.subject ";
    String SUBJECT_DELETE_BY_ID = "DELETE FROM public.subject WHERE id=?";
    String SUBJECT_FIND_BY_ID = "SELECT * FROM public.subject WHERE id=?";
    String SUBJECT_FIND_BY_EMAIL = "SELECT * FROM public.subject WHERE email = ? ";
    String SUBJECT_INSERT = "INSERT INTO public.subject (id, enabled, first_name, middle_name, last_name, phone_number, " +
            " email, address, post_address, date_add) " +
            " values (:id, :enabled, :firstName, :middleName, :lastName, :phoneNumber, :email, :address, :postAddress, :dateAdd) ";

    String ITEM_CONTENT_SHOW_BY_ITEM = "SELECT * FROM public.item_content WHERE item_id = ? AND show = 'Y' ORDER BY date_add ASC";
    String ITEM_CONTENT_COUNT_BY_ITEM_ID = "SELECT count(*) FROM public.item_content WHERE item_id = ? ";
    String ITEM_CONTENT_INSERT = "INSERT INTO public.item_content (id, item_id, content_id, show, main, date_add) " +
            " values (:id, :itemId, :contentId, :show, :main, :dateAdd) ";
    String ITEM_CONTENT_FIND_BY_ITEM_ID = "SELECT * FROM public.item_content WHERE item_id = ? ORDER BY date_add ASC";
    String ITEM_CONTENT_DELETE_BY_ITEM_ID_AND_CONTENT_ID = "DELETE FROM public.item_content WHERE item_id = ? AND content_id = ? ";

    String CONTENT_FIND_DEFAULT = "SELECT * FROM public.content WHERE is_default = 'Y' ";
    String CONTENT_FIND_BY_ID = "SELECT * FROM public.content WHERE id = ?";
    String CONTENT_INSERT = "INSERT INTO public.content (id, file_name, mime, type, is_default, date_add, content) " +
            " values (:id, :fileName, :mime, :type, :isDefault, :dateAdd, :content) ";
    String CONTENT_DELETE_BY_ID = "DELETE FROM public.content where id = ? ";

    String ITEM_CATEGORY_LINK_FIND_ITEM_CATEGORIES = "SELECT i.id as item_id,i.name as item_name,i.company_id,i.article,i.description,i.price,i.not_for_sale,i.in_stock,c.id,c.name " +
            " FROM item i " +
            " RIGHT JOIN category_item ci ON ci.item_id=i.id " +
            " INNER JOIN category c ON ci.category_id=c.id " +
            " ORDER BY i.date_add asc ";

    String ITEM_GET_ORDERED_COU = "SELECT sum(cou) FROM order_item WHERE item_id = ?";

    String ORDER_ITEM_FIND_BY_ORDER_ID = "SELECT * FROM public.order_item where order_id = ? ";
    String ORDER_ITEM_DELETE_BY_ITEM_ID = "DELETE FROM public.order_item where item_id = ? ";
    String ORDER_ITEM_DELETE_BY_ORDER_ID = "DELETE FROM public.order_item where order_id = ? ";

    String PURCHASE_ORDER_FIND_ALL = "SELECT * FROM public.purchase_order";
    String PURCHASE_ORDER_FIND_BY_ID = "SELECT * FROM public.purchase_order WHERE id = ?";
    String PURCHASE_ORDER_DELETE_BY_ID = "DELETE FROM public.purchase_order WHERE id = ?";
    String PURCHASE_ORDER_DELETE_BY_SUBJECT_ID = "DELETE FROM public.purchase_order WHERE subject_id = ?";
    String PURCHASE_ORDER_FIND_BY_SUBJECT_ID = "SELECT * FROM public.purchase_order WHERE subject_id = ? ";

    String ITEM_CATEGORY_LINK_FIND_FILTERED_ITEMS = "SELECT i.id , i.name , i.company_id, i.article, i.description, i.price, i.not_for_sale, i.in_stock, c.id as category_id, c.name as category_name " +
            "FROM item i " +
            "RIGHT JOIN category_item ci ON ci.item_id=i.id " +
            "INNER JOIN category c ON ci.category_id=c.id " +
            "ORDER BY i.date_add asc ";

    String CUSTOM_FIND_ITEMS_BY_FILTER = "select t.* from ( " +
            "select i.*,c.id as content_id from item i " +
            "inner join item_content ic on i.id=ic.item_id " +
            "inner join content c on ic.content_id=c.id " +
            "where ic.main='Y' and ic.show='Y' " +
            "union all " +
            "select i.*,c.id from item i, content c " +
            "where c.is_default='Y' and not exists (select item_id from item_content ic where ic.item_id=i.id)) t " +
            "order by t.name ";

}
