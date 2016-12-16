package com.acme.email;

import java.io.File;

/**
 * Created by kobzev on 16.12.16.
 */
public interface InlinePicture {

	ImageType getImageType();

	File getFile();

	/**
	 * Return the name used in the template with the path to the resource.
	 * The name has to be replaced by a proper cid.
	 *
	 * @return the name used in the template, included any path to folders.
	 */
	String getTemplateName();

	byte[] getContent();

}
