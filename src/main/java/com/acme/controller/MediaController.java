package com.acme.controller;

import com.acme.enums.ImageSize;
import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.ImageService;
import com.acme.service.ResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * Контроллер предназначен для получения подходящего изображения
 * Внутри себя должен использовать сервис возвращающий изображение.
 */
@RestController
@RequestMapping(value = "/media")
public class MediaController {


	@Autowired
	ContentRepository contentRepository;

	@Autowired
	ResizeService resizeService;

	@Autowired
	ImageService imageService;

	@RequestMapping(method = RequestMethod.GET, value = "/image/{contentId}")
	public void getImageForGallery(@PathVariable(value = "contentId") String contentId,
								   @RequestParam(name = "asWebp", required = false) Boolean asWebp,
								   HttpServletResponse response) throws Exception {
		if (asWebp == null) {
			asWebp = false;
		}
		writeResponse(ImageSize.RAW, contentId, asWebp, response);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/image/{size}/{contentId}")
	public void getImage(@PathVariable(value = "size") String size,
						 @PathVariable(value = "contentId") String contentId,
						 @RequestParam(name = "asWebp", required = false) Boolean asWebp,
						 HttpServletResponse response) throws Exception {
		if (asWebp == null) {
			asWebp = false;
		}
		ImageSize imageSize = ImageSize.valueOf(size.toUpperCase());
		writeResponse(imageSize, contentId, asWebp, response);
	}

	private void writeResponse(ImageSize size, String contentId, boolean asWebp, HttpServletResponse response) throws Exception {
		long start = System.currentTimeMillis();
		BufferedImage image;
		System.out.println("Request: " + size.name());
//		Content content = contentRepository.getById(contentId);
		Content content = null;
		System.out.println("After get content: " + (System.currentTimeMillis() - start) + "ms");
		String type = asWebp ? "webp" : content.getType();
		switch (size) {
			case GALLERY:
				image = resizeService.forGallery(content.getContent());
				break;
			case VIEW:
				image = resizeService.forView(content.getContent());
				break;
			case PREVIEW:
				image = resizeService.forPreview(content.getContent());
				break;
			case THUMB:
				image = resizeService.forThumb(content.getContent());
				break;
			case ORIGINAL:
				image = resizeService.forOrign(content.getContent());
				break;
			default:
				image = imageService.getImage(content.getContent());
		}
		System.out.println("Size : w - " + image.getWidth() + " h - " + image.getHeight());
		byte[] data = imageService.toBytes(image, type);
		System.out.println("After convert: " + (System.currentTimeMillis() - start) + "ms");
		response.setContentType(type);
		response.getOutputStream().write(data);
		System.out.println("After write: " + (System.currentTimeMillis() - start) + "ms");
	}
}
