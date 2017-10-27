package com.acme.controller;

import com.acme.constant.Constants;
import com.acme.exception.TemplateException;
import com.acme.model.Item;
import com.acme.model.dto.RouteDto;
import com.acme.service.ItemService;
import com.acme.service.RouteService;
import com.acme.service.TemplateService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.samskivert.mustache.Mustache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acme.model.Content_.fileName;

/**
 * Created by nikolay on 22.10.17.
 */
@RestController
@RequestMapping(value = "/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/download", method = RequestMethod.GET )
    public void createSiteMap(HttpServletResponse response) {
        try{
            List<RouteDto> routes = routeService.getSimpleRoutes();
            // пока берем только item point'ы
            Map<String, RouteDto> map = Maps.uniqueIndex(routes.stream().filter(routeDto -> routeDto.getName().contains("catalog")).collect(Collectors.toList()), RouteDto::getName);

            List<String> itemMap = itemService.getAll().stream().map(Item::getId).collect(Collectors.toList());

            List<Map<String, String>> routesMap = Lists.newArrayList();
            /* перебираем товар */
            if(itemMap!=null && !itemMap.isEmpty()){
                itemMap.forEach(itemId -> {

                    //2017-10-08T01:00:00-17:00
                    LocalDateTime localDate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedString = localDate.format(formatter);

                    routesMap.add(new ImmutableMap.Builder<String, String>()
                            .put("URL", "catalog/card/"+itemId)
                            .put("LAST_MOD_DATE", formattedString)
                            .put("FREQUENCY", "weekly")
                            .put("PRIORITY", "0.8")
                            .build());
                });
            }

            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("route",routesMap);
            String content = templateService.mergeXmlTemplateIntoString(Constants.SITEMAP, paramMap);
            InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
            org.apache.commons.io.IOUtils.copy(contentStream, response.getOutputStream());
            response.setContentType("application/xml");
            response.setContentLength(content.length());
            response.setHeader("Expires:", "0"); // eliminates browser caching
            response.setHeader("Content-Disposition", "attachment; filename=site_map.xml");
//            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

//    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
//    public ResponseEntity<InputStreamResource> download() throws IOException, TemplateException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_XML_VALUE));
//        headers.add("Access-Control-Allow-Origin", "*");
//        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
//        headers.add("Access-Control-Allow-Headers", "Content-Type");
//        headers.add("Content-Disposition", "filename=site_map.xml");
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//
//        List<RouteDto> routes = routeService.getSimpleRoutes();
//        // пока берем только item point'ы
//        Map<String, RouteDto> map = Maps.uniqueIndex(routes.stream().filter(routeDto -> routeDto.getName().contains("catalog")).collect(Collectors.toList()), RouteDto::getName);
//        System.out.println(map.keySet());
//
//        List<String> itemMap = itemService.getAll().stream().map(Item::getId).collect(Collectors.toList());
//
//        List<Map<String, String>> routesMap = Lists.newArrayList();
//            /* перебираем товар */
//        if(itemMap!=null && !itemMap.isEmpty()){
//            itemMap.forEach(itemId -> {
//
//                //2017-10-08T01:00:00-17:00
//                LocalDateTime localDate = LocalDateTime.now();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                String formattedString = localDate.format(formatter);
//
//                routesMap.add(new ImmutableMap.Builder<String, String>()
//                        .put("URL", "catalog/card/"+itemId)
//                        .put("LAST_MOD_DATE", formattedString)
//                        .put("FREQUENCY", "weekly")
//                        .put("PRIORITY", "0.8")
//                        .build());
//            });
//        }
//
//        Map<String, Object> paramMap = Maps.newHashMap();
//        paramMap.put("route",routesMap);
//        String content = templateService.mergeXmlTemplateIntoString(Constants.SITEMAP, paramMap);
//        InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
//
//        headers.setContentLength(content.length());
//        return new ResponseEntity<>(new InputStreamResource(contentStream), headers, HttpStatus.OK);
//
//    }

}
