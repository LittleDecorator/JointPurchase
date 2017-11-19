package com.acme.controller;

import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Company;
import com.acme.model.Content;
import com.acme.model.InstagramPost;
import com.acme.model.InstagramPostContent;
import com.acme.model.ItemContent;
import com.acme.model.dto.CompanyContentDto;
import com.acme.repository.CompanyRepository;
import com.acme.repository.ContentRepository;
import com.acme.repository.InstagramPostContentRepository;
import com.acme.repository.InstagramPostRepository;
import com.acme.repository.ItemContentRepository;
import com.acme.constant.Constants;
import com.acme.service.ImageService;
import com.acme.service.ResizeService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RestController
@RequestMapping(value = "/api/content")
public class ContentController{

    @Autowired
    ItemContentRepository itemContentRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ResizeService resizeService;

    @Autowired
    ImageService imageService;

    @Autowired
    InstagramPostRepository postRepository;

    @Autowired
    InstagramPostContentRepository postContentRepository;

//    /**
//     * Кропирование уже загруженного изображения
//     * @param request
//     * @param itemId
//     * @param imageId
//     * @throws Exception
//     */
//    @RequestMapping(value = "/upload/crop", method = RequestMethod.POST)
//    public void itemImageUpload(MultipartHttpServletRequest request ,
//                                     @RequestParam(value = "itemId") String itemId,
//                                     @RequestParam(value = "imageId") String imageId) throws Exception {
//        Content content;
//        ItemContent itemContent;
//
//        Map<String, MultipartFile> fileMap = request.getFileMap();
//
//        for(MultipartFile file : fileMap.values()) {
//            if (!file.isEmpty()) {
//
//                String fileName = file.getOriginalFilename();
//                String type = fileName.substring(fileName.indexOf(".") + 1);
//
//                content = new Content();
//                content.setFileName(file.getOriginalFilename());
//                content.setContent(Base64BytesSerializer.serialize(imageService.toBytes(resizeService.forOrign(file.getBytes()), type)));
//                content.setType(type);
//                content.setMime("image/" + type);
//                contentRepository.save(content);
//
//                itemContent = itemContentRepository.findByItemIdAndContentId(itemId, imageId);
//                itemContent.setCropId(content.getId());
//                itemContentRepository.save(itemContent);
//            }
//        }
//    }

    /**
     * Добавление изображений для товара.
     * Возвращает список добавленых товаров.
     * @param request
     * @param itemId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload/item", method = RequestMethod.POST)
    public List<ItemContent> itemImageUpload(MultipartHttpServletRequest request, @RequestParam(value = "itemId") String itemId) throws Exception {
        Content content;
        ItemContent itemContent;
        List<ItemContent> result = Lists.newArrayList();

        //check if any image for item present
        int i = itemContentRepository.countByItemId(itemId);

        Map<String, MultipartFile> fileMap = request.getFileMap();

        for(MultipartFile file : fileMap.values()){
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                String type = fileName.substring(fileName.indexOf(".")+1);

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(Base64BytesSerializer.serialize(file.getBytes()));
                content.setType(type);
                content.setMime("image/" + type);
                contentRepository.save(content);

                itemContent = new ItemContent();
                itemContent.setItemId(itemId);
                itemContent.setContentId(content.getId());
                itemContent.setShow(true);
                if(i==0){
                    itemContent.setMain(true);
                } else {
                    itemContent.setMain(false);
                }
                itemContent = itemContentRepository.save(itemContent);
                itemContent.setUrl(Constants.PREVIEW_URL + content.getId());
                result.add(itemContent);

                i++;
            }
        }
        return result;
    }

    @RequestMapping(value = "/upload/company", method = RequestMethod.POST)
    public void companyImageUpload(MultipartHttpServletRequest request, @RequestParam(value = "companyId") String companyId) throws Exception {
        //TODO: либо загружать одну, либо добавить таблицу связи
        Content content;

        Map<String, MultipartFile> fileMap = request.getFileMap();

        for(MultipartFile file : fileMap.values()){
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                String type = fileName.substring(fileName.indexOf(".")+1);

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(Base64BytesSerializer.serialize(file.getBytes()));
                content.setType(type);
                content.setMime("image/" + type);
                contentRepository.save(content);

                Company company = companyRepository.findOne(companyId);
                company.setContentId(content.getId());
                companyRepository.save(company);
            }
        }
    }

    /**
     * Возвращает ID изображений инстаграмма
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/instagram", method = RequestMethod.GET)
    public List<String> getInstgaramImages() throws Exception {
        List<String> postIds = postRepository.findAllByWrongPostFalse().stream().filter(InstagramPost::isShowOnMain).map(InstagramPost::getId).collect(Collectors.toList());
        List<String> contentIds = postContentRepository.findAllByPostIdIn(postIds).stream().map(InstagramPostContent::getContentId).collect(Collectors.toList());
		// TODO: Добавить проверки на существование записей
        List<Content> photos = contentRepository.findAllByIdIn(contentIds);
        if(photos.isEmpty()) return Collections.emptyList();

        int limit = photos.size() > 15 ? 15 : photos.size();
        return photos.stream().map(Content::getId).limit(limit).collect(Collectors.toList());
    }

    /**
     * Получение списка изображений по конкретному товару.
     * @param itemId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/items", method = RequestMethod.GET)
    public List<ItemContent> getPreviewImages(@RequestParam(value = "itemId") String itemId) throws Exception {
        List<ItemContent> result = Lists.newArrayList();
        for(ItemContent itemContent : itemContentRepository.findAllByItemId(itemId)){
            itemContent.setUrl(Constants.GALLERY_URL+ (itemContent.getCropId()== null ? itemContent.getContentId() : itemContent.getCropId()));
            result.add(itemContent);
        }
        return result;
    }

    /**
     * Получение списка изображений по конкретному производителю.
     * @param companyId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/company/{id}", method = RequestMethod.GET)
    public CompanyContentDto getCompanyPreviewImage(@PathVariable(value = "id") String companyId) throws Exception {
        CompanyContentDto result = null;
        Company company = companyRepository.findOne(companyId);
        if(company!=null && !Strings.isNullOrEmpty(company.getContentId())){
            result = new CompanyContentDto();
            result.setContentId(company.getContentId());
            result.setUrl(Constants.GALLERY_URL + company.getContentId());
        }
        return result;
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.DELETE)
    public void deleteCompanyImage(@PathVariable(value = "id") String companyId, @RequestParam(value = "contentId") String contentId) throws Exception {
        Company company = companyRepository.findOne(companyId);
        company.setContentId(null);
        companyRepository.save(company);

        contentRepository.delete(contentId);
    }

    /**
     * Удаление изображения по ID
     * @param id
     */
    @RequestMapping(value = "/remove/{id}",method = RequestMethod.DELETE)
    public void contentDelete(@PathVariable(value = "id") String id){
        ItemContent itemContent = itemContentRepository.findOne(id);
        itemContentRepository.delete(id);
        //delete content
        contentRepository.delete(itemContent.getContentId());
    }

    /**
     * Установка / Снятие изображения как основное
     * @param input
     * @throws ParseException
     */
    @RequestMapping(value = "/set/main",method = RequestMethod.PUT)
    public void setAsMain(@RequestBody ItemContent input) throws ParseException {
        for(ItemContent itemContent : itemContentRepository.findAllByItemId(input.getItemId())){
            itemContent.setMain(itemContent.getContentId().contentEquals(input.getContentId()));
            itemContentRepository.save(itemContent);
        }
    }

    /**
     * Скрывать / Показывать изображение
     * @param input
     * @throws ParseException
     */
    @RequestMapping(value = "/set/show",method = RequestMethod.PUT)
    public void setAsShown(@RequestBody ItemContent input) throws ParseException {
        itemContentRepository.save(input);
    }
}
