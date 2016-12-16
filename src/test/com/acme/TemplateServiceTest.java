package com.acme;

import com.acme.constant.Constants;
import com.acme.email.Email;
import com.acme.email.EmailToMimeMessage;
import com.acme.email.ImageType;
import com.acme.email.InlinePicture;
import com.acme.email.impl.EmailImpl;
import com.acme.email.impl.InlinePictureImpl;
import com.acme.exception.TemplateException;
import com.acme.service.TemplateService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by kobzev on 15.12.16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
@IntegrationTest("server.port:0")
public class TemplateServiceTest {

	private EmailToMimeMessage emailToMimeMessage;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private JavaMailSender mailSender;

	@Before
	public void setUp() {
		emailToMimeMessage = new EmailToMimeMessage(mailSender);
	}

	@Test
	public void buildItemTemplate() throws IOException, TemplateException {
		System.out.println(createEmail());
	}

	@Test
	public void sendOrderConformationWithImageHtml() throws MessagingException, IOException, TemplateException {

		final Email email = EmailImpl.builder()
				.from(new InternetAddress("robot.grimmstory@gmail.com", "GrimmStory"))
				.to(Lists.newArrayList(new InternetAddress("npkobzev@mail.ru", "Nikolay")))
				.subject("Your order")
				.body("")//Empty body
				.encoding(Charset.forName("UTF-8")).build();

		email.setSentAt(new Date());
		final MimeMessage mimeMessage = toMimeMessage(email);

		Multipart multipart = new MimeMultipart("related");

		String emailContent = createEmail();

		emailContent = addImages(multipart, emailContent);

		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(emailContent, email.getEncoding().displayName(), "html");
		multipart.addBodyPart(textPart);

		mimeMessage.setContent(multipart);

		mailSender.send(mimeMessage);
	}

	/**
	 * Добавим изображения в письмо
	 * @param multipart
	 * @param emailContent
	 * @throws IOException
	 * @throws MessagingException
	 */
	private String addImages(Multipart multipart, String emailContent) throws IOException, MessagingException {
		for (final InlinePicture inlinePicture : collectPics(ImmutableList.of("IMG_3164.JPG","IMG_3178.JPG","IMG_3217.JPG","IMG_3287.JPG"))) {
			final String cid = UUID.randomUUID().toString();

			//Set the cid in the template
			emailContent = emailContent.replace(inlinePicture.getTemplateName(), "cid:" + cid);

			//Set the image part
			final MimeBodyPart imagePart = new MimeBodyPart();
//			imagePart.attachFile(inlinePicture.getFile());
			ByteArrayDataSource dataSource = new ByteArrayDataSource( inlinePicture.getContent(), "image/jpg" );
			imagePart.setDataHandler(new DataHandler(dataSource));
			imagePart.setContentID('<' + cid + '>');
			imagePart.setDisposition(MimeBodyPart.INLINE);
			imagePart.setHeader("Content-Type", inlinePicture.getImageType().getContentType());
			multipart.addBodyPart(imagePart);
		}
		return emailContent;
	}

	/**
	 * Создадим обертки для изображений
	 * @param imgNames
	 * @return
	 */
	private List<InlinePicture> collectPics(List<String> imgNames) throws IOException {
		List<InlinePicture> pictures = Lists.newArrayList();
		for(String imageName : imgNames){
			pictures.add(InlinePictureImpl.builder()
					.content(readImgAsByte(imageName))
					.imageType(ImageType.JPG)
					.templateName(imageName).build());
		}
		return pictures;
	}

	private String createEmail() throws IOException, TemplateException {
		final String template = "order_email_template.html";

		final Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
				/* основной текст письма */
				.put("order_number", "#1472199774972")
				.put("isAuth", true)
				.put("cabinet_link", Constants.CABINET_LINK)
				/* информация о заказе */
				.put("orderDate", "12.12.2016 23:50:13")
				.put("orderDelivery", "Самовывоз")
				.put("orderPayment", "5000 руб")
				/* данные о товаре */
				.put("item", getItems())
				.build();

		return templateService.mergeTemplateIntoString(template, data);
	}

	/**
	 * Чтение файла
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String readFile(final File file) throws IOException {
		final byte[] encoded = Files.readAllBytes(file.toPath());
		return new String(encoded, Charset.forName("UTF-8"));
	}

	/**
	 * Чтение изображения из ресурсов
	 * @param imageName
	 * @return
	 */
	private File readImg(String imageName){
		final File inlineImageFile = new File(getClass().getClassLoader()
				.getResource("img" + File.separator + imageName).getFile());
		return inlineImageFile;
	}

	private byte[] readImgAsByte(String imageName) throws IOException {
		return  ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("img" + File.separator + imageName));
	}

	private MimeMessage toMimeMessage(@NotNull Email email) {
		return emailToMimeMessage.apply(email);
	}

	private List getItems(){
		List<Map<String, Object>> list = Lists.newArrayList();

		final Map<String, Object> item1 = new ImmutableMap.Builder<String, Object>()
				.put("imageName", "IMG_3164.JPG")
				.put("itemName", "7 друзей в стаканчиках")
				.put("itemCost", "1000 руб")
				.put("itemCount", "2шт")
				.build();

		final Map<String, Object> item2 = new ImmutableMap.Builder<String, Object>()
				.put("imageName", "IMG_3178.JPG")
				.put("itemName", "Радуга (малая)")
				.put("itemCost", "1650 руб")
				.put("itemCount", "1шт")
				.build();

		final Map<String, Object> item3 = new ImmutableMap.Builder<String, Object>()
				.put("imageName", "IMG_3217.JPG")
				.put("itemName", "Радуга (12 частей)")
				.put("itemCost", "3500 руб")
				.put("itemCount", "30шт")
				.build();

		final Map<String, Object> item4 = new ImmutableMap.Builder<String, Object>()
				.put("imageName", "IMG_3287.JPG")
				.put("itemName", "Радуга (6 частей)")
				.put("itemCost", "2000 руб")
				.put("itemCount", "100шт")
				.build();

		list.add(item1);
		list.add(item2);
		list.add(item3);
		list.add(item4);
		return list;
	}
}
