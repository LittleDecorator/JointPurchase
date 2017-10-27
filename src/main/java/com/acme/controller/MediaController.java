package com.acme.controller;

import com.acme.enums.ImageSize;
import com.acme.handlers.Base64BytesSerializer;
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
//		writeResponse(ImageSize.RAW, contentId, Boolean.TRUE.equals(asWebp), response);
		writeResponse(ImageSize.ORIGINAL, contentId, Boolean.TRUE.equals(asWebp), response);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/image/{size}/{contentId}")
	public void getImage(@PathVariable(value = "size") String size,
						 @PathVariable(value = "contentId") String contentId,
						 @RequestParam(name = "asWebp", required = false) Boolean asWebp,
						 HttpServletResponse response) throws Exception {
		ImageSize imageSize = ImageSize.valueOf(size.toUpperCase());
		writeResponse(imageSize, contentId, Boolean.TRUE.equals(asWebp), response);
	}

	private void writeResponse(ImageSize size, String contentId, boolean asWebp, HttpServletResponse response) throws Exception {
		long start = System.currentTimeMillis();
		BufferedImage image;
		//System.out.println("Request: " + size.name());
		Content content = contentRepository.findOne(contentId);
		//System.out.println("After get content: " + (System.currentTimeMillis() - start) + "ms");
		String type = asWebp ? "webp" : content.getType();
		byte[] data = Base64BytesSerializer.deserialize(content.getContent());
		switch (size) {
			case GALLERY:
				image = resizeService.forGallery(data);
				break;
			case VIEW:
				image = resizeService.forView(data);
				break;
			case PREVIEW:
				image = resizeService.forPreview(data);
				break;
			case THUMB:
				image = resizeService.forThumb(data);
				break;
			case ORIGINAL:
				image = resizeService.forOrign(data);
				break;
			default:
				image = imageService.getImage(data);
		}
		//System.out.println("Size : w - " + image.getWidth() + " h - " + image.getHeight());
		byte[] result = imageService.toBytes(image, type);
		//System.out.println("After convert: " + (System.currentTimeMillis() - start) + "ms");
		response.setContentType(type);
		response.getOutputStream().write(result);
		//System.out.println("After write: " + (System.currentTimeMillis() - start) + "ms");
	}
}
