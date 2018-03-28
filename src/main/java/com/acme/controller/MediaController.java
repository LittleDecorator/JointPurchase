package com.acme.controller;

import com.acme.enums.ImageSize;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.ImageService;
import com.acme.service.ResizeService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.DeferredResult;

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

	//fake last modify date
	private long lastModifyDate = getResourceLastModified();

	private static final ExecutorService ex = Executors.newFixedThreadPool(100);

	// generate fake last modify date
	private long getResourceLastModified () {
		ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("GMT"));
		return zdt.toInstant().toEpochMilli();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/image/{contentId}")
	public void getImageForGallery(@PathVariable(value = "contentId") String contentId, @RequestParam(name = "asWebp", required = false) Boolean asWebp, ServletWebRequest request, HttpServletResponse response) throws Exception {
		long lastModifiedFromBrowser = ((HttpServletRequest)request.getNativeRequest()).getDateHeader("If-Modified-Since");
		long lastModifiedFromServer = lastModifyDate;

		if (lastModifiedFromBrowser != -1 && lastModifiedFromServer <= lastModifiedFromBrowser) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		writeResponse(ImageSize.ORIGINAL, contentId, Boolean.TRUE.equals(asWebp), response);
	}


	@RequestMapping(method = RequestMethod.GET, value = "/image/{size}/{contentId}")
	public DeferredResult<byte[]> getImage(@PathVariable(value = "size") String size, @PathVariable(value = "contentId") String contentId, @RequestParam(name = "asWebp", required = false) Boolean asWebp, ServletWebRequest request, HttpServletResponse response) throws Exception {
		long lastModifiedFromBrowser = ((HttpServletRequest)request.getNativeRequest()).getDateHeader("If-Modified-Since");
		long lastModifiedFromServer = lastModifyDate;

		if (lastModifiedFromBrowser != -1 && lastModifiedFromServer <= lastModifiedFromBrowser) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return null;
		}

		DeferredResult<byte[]> result = new DeferredResult<>();//TODO add timeout
		CompletableFuture.supplyAsync(() -> {
			ImageSize imageSize = ImageSize.valueOf(size.toUpperCase());
			byte[] c = new byte[]{};
			try {
				c = writeResponse(imageSize, contentId, Boolean.TRUE.equals(asWebp), response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return c;
		}, ex).thenAccept(result::setResult);
		return result;
	}


	private byte[] writeResponse(ImageSize size, String contentId, boolean asWebp, HttpServletResponse response) throws Exception {
		BufferedImage image;
		Content entity = contentRepository.findOne(contentId);
		String content = contentRepository.getContentData(contentId);
		String type = asWebp ? "webp" : entity.getType();
		byte[] data = Base64BytesSerializer.deserialize(content);
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
		byte[] result = imageService.toBytes(image, type);
		response.setContentType(type);
		response.addDateHeader("Expires", lastModifyDate + 24*60*60*1000);
		response.addDateHeader("Last-Modified", getResourceLastModified());
		return result;
	}


}
