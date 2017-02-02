package com.acme;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by kobzev on 02.02.17.
 * <code>WebPWriterTest</code> unit tests {WebPWriter}.
 */
public class WebPWriterTest {


	/**
	 * Tests writing WebP images using
	 * {@link ImageIO#write(RenderedImage, String, java.io.OutputStream)}.
	 * @param im         the image to write.
	 * @param outputName the name of the file to write.
	 * @throws IOException if unable to write the image.
	 */
	@Test(dataProvider = "createBasicImages", enabled = true)
	public void testImageIoWrite(final RenderedImage im, final String outputName) throws IOException {
		final ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
		ImageIO.write(im, "webp", baoStream);
		assertTrue(baoStream.size() > 0);
		final String testName = "ImageIo";
		final File file = createOutputFile(testName, outputName);
		final FileOutputStream foStream = new FileOutputStream(file);
		baoStream.writeTo(foStream);
		foStream.close();
	}

	/**
	 * The data provider for {@link #testImageIoWrite}.
	 * @return the test {@link BufferedImage}s.
	 * @throws IOException if unable to read one of the test images.
	 */
	@DataProvider
	public Object[][] createBasicImages() throws IOException {
		return new Object[][]{
				new Object[]{loadImage("./img/orig.jpg"), "orig.webp"},
				new Object[]{loadImage("./img/view.jpg"), "view.webp"},
				new Object[]{loadImage("./img/small.jpg"), "small.webp"},
		};
	}

	/**
	 * Loads the image with the given name.
	 * @param name the name of the image to load.
	 * @return the requested image.
	 * @throws IOException if unable to read the image.
	 */
	private RenderedImage loadImage(final String name) throws IOException {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return ImageIO.read(classLoader.getResource(name));
	}

	/**
	 * Creates the output file in the specified test directory.
	 * @param testName   the name of the test to use as the output directory name.
	 * @param outputName the name of the file to be created.
	 * @return the file destination.
	 */
	private File createOutputFile(final String testName, final String outputName) {
		final File parentDirectory = new File("target" + File.separator + testName);
		parentDirectory.mkdirs();
		return new File(parentDirectory, outputName);
	}

}
