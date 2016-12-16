package com.acme.email;

import lombok.Getter;
import lombok.NonNull;

/**
 * Created by kobzev on 16.12.16.
 */

@Getter
public enum ImageType {

	GIF("gif", "image/gif"),
	JPG("jpg", "image/jpeg"),
	JPEG("jpeg", "image/jpeg"),
	PNG("png", "image/png");

	private final String extension;
	private final String contentType;

	ImageType(@NonNull final String extension, @NonNull final String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

}
