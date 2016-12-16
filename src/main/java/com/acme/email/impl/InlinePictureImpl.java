package com.acme.email.impl;

import com.acme.email.ImageType;
import com.acme.email.InlinePicture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.File;

/**
 * Created by kobzev on 16.12.16.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InlinePictureImpl implements InlinePicture {

	@NonNull
	private ImageType imageType;

	private File file;

	private byte[] content;

	@NonNull
	private String templateName;

}
