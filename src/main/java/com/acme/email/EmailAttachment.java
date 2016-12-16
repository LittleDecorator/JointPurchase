package com.acme.email;

import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * Created by kobzev on 16.12.16.
 */
public interface EmailAttachment {

	String getAttachmentName();

	byte[] getAttachmentData();

	MediaType getContentType() throws IOException;

}
