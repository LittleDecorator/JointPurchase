package com.acme.email;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.requireNonNull;
import static org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY;

/**
 * Created by kobzev on 16.12.16.
 *
 * A convenience singleton class that uses ApacheTika to guess the MIME type.
 */
public class TikaDetector {

	private final Detector detector;

	private TikaDetector() {
		final TikaConfig config = TikaConfig.getDefaultConfig();
		detector = config.getDetector();
	}

	public static TikaDetector tikaDetector() {
		return TikaDetectorSingletonHolder.tikaDetector;
	}

	private static org.springframework.http.MediaType toSpringMediaType(final MediaType mediaType) {
		return org.springframework.http.MediaType.valueOf(mediaType.toString());
	}

	/**
	 * Detect the MediaType tikaDetector the given input stream
	 *
	 * @param inputStream the stream for which the content has to be detected
	 * @param fileName    filename with extension
	 * @return the guessed media type
	 */
	public org.springframework.http.MediaType detect(final InputStream inputStream, final String fileName) throws IOException {
		final TikaInputStream stream = TikaInputStream.get(inputStream);
		return detect(stream, fileName);
	}

	private org.springframework.http.MediaType detect(final TikaInputStream stream, final String fileName) throws IOException {
		final Metadata metadata = new Metadata();
		metadata.add(RESOURCE_NAME_KEY, fileName);
		final MediaType mediaType = detector.detect(requireNonNull(stream), metadata);
		return toSpringMediaType(mediaType);
	}

	public static class TikaDetectorSingletonHolder {
		public static TikaDetector tikaDetector = new TikaDetector();
	}

}
