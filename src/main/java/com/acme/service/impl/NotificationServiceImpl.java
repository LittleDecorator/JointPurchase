package com.acme.service.impl;

import com.acme.model.Notification;
import com.acme.model.Order;
import com.acme.model.Subject;
import com.acme.service.NotificationService;
import com.acme.service.SmsService;
import com.acme.service.SubjectService;
import com.google.common.base.Joiner;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

/**
 * Created by nikolay on 07.05.17.
 */

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${app.home}")
    private String appHomePage;

    @Autowired
    SmsService smsService;

    @Autowired
    SubjectService subjectService;

    private String glKey="AIzaSyB7dIyZQNIMdM7xrsTR8SJmWTYknu9Q-1Q";

//    private final String dateTimeFormatPattern = "dd.MM.yyyy HH:mm:ss ";
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormatPattern);
//    private final Format format = formatter.toFormat();

    @Override
    public void sendOrderNotification(Order order) {
        String receivers = Joiner.on(",").skipNulls().join(subjectService.getAdmins().stream().map(Subject::getPhoneNumber).collect(Collectors.toList()));
        String orderLink = appHomePage+"/order/"+order.getId();
        String smsText ="";
        switch (order.getStatus()){
            case NEW:
//                smsText = "Создан новый заказ! "+ getGlLink(orderLink); break;
                smsText = "Создан новый заказ! "+ orderLink; break;
            case CANCELED:
                smsText = "Заказ отменен клиентом! "+ getGlLink(orderLink); break;
        }
        System.out.println(smsText);
        smsService.sendSimple(receivers, smsText);
    }

    @Override
    public void createNotification() {

    }

    @Override
    public void sendNotification(Notification notification) {

    }

    /**
     * Преобразование длинных URL в короткие
     * @param url
     * @return
     */
    //TODO: перенести в отдельный сервис
    private String getGlLink(String url){
        String glUrl = "https://www.googleapis.com/urlshortener/v1/url?key="+glKey;
        RestTemplate restTemplate = new RestTemplate();
        JSONObject object = new JSONObject();
        object.put("longUrl", url);
        GlDto gl = restTemplate.postForObject(glUrl, object, GlDto.class);
        return gl.getId();
    }

    /*============================ INNER ================================*/

    class GlDto {

        private String kind;
        private String id;
        private String longUrl;

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLongUrl() {
            return longUrl;
        }

        public void setLongUrl(String longUrl) {
            this.longUrl = longUrl;
        }
    }
}
