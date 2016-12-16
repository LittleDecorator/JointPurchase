package com.acme.email.impl;

import com.acme.email.EmailAttachment;
import com.acme.email.TikaDetector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Optional.ofNullable;

/**
 * Created by kobzev on 16.12.16.
 */

@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "attachmentData")
@EqualsAndHashCode
@Slf4j
public class EmailAttachmentImpl implements EmailAttachment {

	private static final long serialVersionUID = -3307831714212032363L;

	@Getter
	private
	@NonNull
	String attachmentName;

	@Getter
	private
	@NonNull
	byte[] attachmentData;

	private MediaType mediaType;

	public ByteArrayResource getInputStream() {
		return new ByteArrayResource(attachmentData);
	}

	public MediaType getContentType() throws IOException {
		final InputStream attachmentDataStream = new ByteArrayInputStream(attachmentData);

		final MediaType mediaType;
		try {
			mediaType = ofNullable(this.mediaType)
					.orElse(TikaDetector.tikaDetector().detect(attachmentDataStream, attachmentName));
		} catch (IOException e) {
			log.error("The MimeType is not set. Tried to guess it but something went wrong.", e);
			throw e;
		}
		return mediaType;
	}

}
