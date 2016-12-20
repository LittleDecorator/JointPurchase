package com.acme.constant;

public interface Constants {

    String MIME_IMG = "image/jpeg";

    String ORIG_URL = "media/image/";
    String VIEW_URL = "media/image/view/";
    String THUMB_URL = "media/image/thumb/";
    String GALLERY_URL = "media/image/gallery/";
    String PREVIEW_URL = "media/image/preview/";

    String ROBOT_NAME = "no-reply";

    String REGISTRATION_CONFIRM = "<a href='http://grimmstory.ru/public/auth/confirm'>Confirm your registration</a>";
//    String CABINET_LINK = "<a href='http://grimmstory.ru/public/auth/confirm'>Confirm your registration</a>";
    String CABINET_LINK = "http://192.168.86.196:7777/#/cabinet/history/0cfd5029-0f0d-47d1-b66b-09f4e853b09e";
    String REGISTRATION_DONE = "Congrats with registration. Here will be link to your Private-Cabinet and link to Catalog";
    String ORDER_CREATE = "Thank you for purchase! Here will be your order details";

    /* TEMPLATES */
    String ORDER_EMAIL_TEMPLATE = "order_email_template.html";

}
