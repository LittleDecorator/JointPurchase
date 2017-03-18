package com.acme.model.gmail;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by kobzev on 12.03.17.
 *
 * Упрощенная форма Message сущности от GMail
 */
public class SimpleMessage {

	private String id;
	private BigInteger historyId;
	private String threadId;

	private List<String> labels;
	private Integer size;

	private String subject;
	private String from;
	private String to;
	private String date;
	private String snippet;
	private String body;

	private boolean unread;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigInteger getHistoryId() {
		return historyId;
	}

	public void setHistoryId(BigInteger historyId) {
		this.historyId = historyId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	@Override
	public String toString() {
		return "SimpleMessage{" +
			   "id='" + id + '\'' +
			   ", historyId='" + historyId + '\'' +
			   ", threadId='" + threadId + '\'' +
			   ", labels=" + labels +
			   ", size=" + size +
			   ", subject='" + subject + '\'' +
			   ", from='" + from + '\'' +
			   ", to='" + to + '\'' +
			   ", date='" + date + '\'' +
			   ", snippet='" + snippet + '\'' +
			   ", body='" + body + '\'' +
			   '}';
	}

	/**
	 * Обновление данных сообщения
	 * @param message
	 */
	public void merge(SimpleMessage message){
		this.setSubject(message.getSubject());
		this.setBody(message.getBody());
		this.setTo(message.getTo());
		this.setDate(message.getDate());
		this.setSize(message.getSize());
	}

	/**
	 * Получение нового объекта для Message тело которого представлено ввиде простого текста
	 * @param message
	 * @return
	 */
	public static SimpleMessage valueOf(Message message) {
		return valueOf(message, false);
	}

	/**
	 * Получение нового объекта для Message
	 * @param message
	 * @param asPlain
	 * @return
	 */
	public static SimpleMessage valueOf(Message message, boolean asPlain) {
		SimpleMessage simpleMessage = new SimpleMessage();
		if (message != null) {
			List<MessagePartHeader> headers = message.getPayload().getHeaders();
			headers.forEach(header -> {
				switch (header.getName()) {
					case "From":
						simpleMessage.setFrom(header.getValue());
						break;
					case "Date":
						simpleMessage.setDate(header.getValue());
						break;
					case "Subject":
						simpleMessage.setSubject(header.getValue());
						break;
					case "To":
						simpleMessage.setTo(header.getValue());
						break;
				}
			});
			simpleMessage.setSnippet(message.getSnippet());
			simpleMessage.setThreadId(message.getThreadId());
			simpleMessage.setHistoryId(message.getHistoryId());
			simpleMessage.setSize(message.getSizeEstimate());
			simpleMessage.setId(message.getId());
			simpleMessage.setLabels(message.getLabelIds());
			simpleMessage.setUnread(message.getLabelIds().contains("UNREAD"));

			String body = getMessageBody(message.getPayload());
			if (Strings.isNullOrEmpty(body)) {
				for (MessagePart part : message.getPayload().getParts()) {
					body = getMessageBody(part);
					if ((part.getMimeType().contentEquals("text/plain") && asPlain) || (part.getMimeType().contentEquals("text/html") && !asPlain)) {
						break;
					}
				}
			}
			simpleMessage.setBody(body);
		}
		return simpleMessage;
	}

	private static String getMessageBody(MessagePart part){
		String data = part.getBody().getData();
		String result = null;
		if(!Strings.isNullOrEmpty(data)){
			result = new String(Base64.decodeBase64(data));

		}
		return result;
	}
}


/*  Original message example
{
  "historyId" : "3269",
  "id" : "15ab360b3f297274",
  "internalDate" : "1489068142000",
  "labelIds" : [ "UNREAD", "IMPORTANT", "CATEGORY_PERSONAL", "INBOX" ],
  "payload" : {
    "body" : {
      "size" : 0
    },
    "filename" : "",
    "headers" : [ {
      "name" : "Delivered-To",
      "value" : "robot.grimmstory@gmail.com"
    }, {
      "name" : "Received",
      "value" : "by 10.129.89.212 with SMTP id n203csp144683ywb;        Thu, 9 Mar 2017 06:02:23 -0800 (PST)"
    }, {
      "name" : "X-Received",
      "value" : "by 10.46.19.9 with SMTP id 9mr4085426ljt.89.1489068143224;        Thu, 09 Mar 2017 06:02:23 -0800 (PST)"
    }, {
      "name" : "Return-Path",
      "value" : "<npkobzev@mail.ru>"
    }, {
      "name" : "Received",
      "value" : "from f406.i.mail.ru (f406.i.mail.ru. [185.5.136.77])        by mx.google.com with ESMTPS id g1si3226889ljd.180.2017.03.09.06.02.22        for <robot.grimmstory@gmail.com>        (version=TLS1_2 cipher=ECDHE-RSA-AES128-GCM-SHA256 bits=128/128);        Thu, 09 Mar 2017 06:02:23 -0800 (PST)"
    }, {
      "name" : "Received-SPF",
      "value" : "pass (google.com: domain of npkobzev@mail.ru designates 185.5.136.77 as permitted sender) client-ip=185.5.136.77;"
    }, {
      "name" : "Authentication-Results",
      "value" : "mx.google.com;       dkim=pass header.i=@mail.ru;       spf=pass (google.com: domain of npkobzev@mail.ru designates 185.5.136.77 as permitted sender) smtp.mailfrom=npkobzev@mail.ru;       dmarc=pass (p=REJECT sp=REJECT dis=NONE) header.from=mail.ru"
    }, {
      "name" : "DKIM-Signature",
      "value" : "v=1; a=rsa-sha256; q=dns/txt; c=relaxed/relaxed; d=mail.ru; s=mail2; h=Content-Type:Message-ID:Reply-To:Date:MIME-Version:Subject:To:From; bh=H0nOvtAlmxp5ilHsNcgwZ825SNmEbVS8pVLTG0Q+Zwc=; b=U/vQtQwBkqjsSBtEiMnzXN1/z2a2f/EygTUvvzBcv6DjE5TesuqT0nkQFiUqtsLcxei5uRceabVTCNUnpvF+V9nXoRJdJrZvuTB6RCwVJMr1c0bGhzGd3v/BCYanz7OOJeX2BGS9LgDkUzJ4s82yY9JRW8RHeiB0awDG4jjEBdw=;"
    }, {
      "name" : "Received",
      "value" : "by f406.i.mail.ru with local (envelope-from <npkobzev@mail.ru>) id 1clye6-0002Lc-JX for robot.grimmstory@gmail.com; Thu, 09 Mar 2017 17:02:22 +0300"
    }, {
      "name" : "Received",
      "value" : "by e.mail.ru with HTTP; Thu, 09 Mar 2017 17:02:22 +0300"
    }, {
      "name" : "From",
      "value" : "Nikolay Kobzev <npkobzev@mail.ru>"
    }, {
      "name" : "To",
      "value" : "robot.grimmstory@gmail.com"
    }, {
      "name" : "Subject",
      "value" : "test"
    }, {
      "name" : "MIME-Version",
      "value" : "1.0"
    }, {
      "name" : "X-Mailer",
      "value" : "Mail.Ru Mailer 1.0"
    }, {
      "name" : "Date",
      "value" : "Thu, 09 Mar 2017 17:02:22 +0300"
    }, {
      "name" : "Reply-To",
      "value" : "Nikolay Kobzev <npkobzev@mail.ru>"
    }, {
      "name" : "X-Priority",
      "value" : "3 (Normal)"
    }, {
      "name" : "Message-ID",
      "value" : "<1489068142.649890450@f406.i.mail.ru>"
    }, {
      "name" : "Content-Type",
      "value" : "multipart/alternative; boundary=\"--ALT--fCmKV7FLm1HJcx71ofHE8dZ8yG0Mkipz1489068142\""
    }, {
      "name" : "Authentication-Results",
      "value" : "f406.i.mail.ru; auth=pass smtp.auth=npkobzev@mail.ru smtp.mailfrom=npkobzev@mail.ru"
    }, {
      "name" : "X-7FA49CB5",
      "value" : "0D63561A33F958A582D59CBA5FB960812EAB07DFBE62D84E345BF3F10E7974A69F18ECD7E95F35E948A7AB6D24A08DFFEA720A51FE43ED2CA3D52C174E07B1960BF2EBBBDD9D6B0F7FFC5772FDE91EBA"
    }, {
      "name" : "X-Mailru-Sender",
      "value" : "F16D9CAFEEA6770EA54DAF61179BB6A01A70C85905FE182E6AEEC9620F04E64E736A89BC5EC3D31C5D45EEA94BFF022957F696DF389E70C60358257F944E299D1752C749FAB18CA39BD716E8BDEEAC79D4677E6DD1508D675FEEDEB644C299C0ED14614B50AE0675"
    }, {
      "name" : "X-Mras",
      "value" : "OK"
    }, {
      "name" : "X-Spam",
      "value" : "undefined"
    } ],
    "mimeType" : "multipart/alternative",
    "parts" : [ {
      "body" : {
        "data" : "VEVzdGluZyBoaXN0b3J5CgoK0KEg0YPQstCw0LbQtdC90LjQtdC8LCDQndC40LrQvtC70LDQuSDQmtC-0LHQt9C10LI=",
        "size" : 68
      },
      "filename" : "",
      "headers" : [ {
        "name" : "Content-Type",
        "value" : "text/plain; charset=utf-8"
      }, {
        "name" : "Content-Transfer-Encoding",
        "value" : "base64"
      } ],
      "mimeType" : "text/plain",
      "partId" : "0"
    }, {
      "body" : {
        "data" : "CjxIVE1MPjxCT0RZPlRFc3RpbmcgaGlzdG9yeTxicj48YnI-PGJyPtChINGD0LLQsNC20LXQvdC40LXQvCwg0J3QuNC60L7Qu9Cw0Lkg0JrQvtCx0LfQtdCyPC9CT0RZPjwvSFRNTD4K",
        "size" : 105
      },
      "filename" : "",
      "headers" : [ {
        "name" : "Content-Type",
        "value" : "text/html; charset=utf-8"
      }, {
        "name" : "Content-Transfer-Encoding",
        "value" : "base64"
      } ],
      "mimeType" : "text/html",
      "partId" : "1"
    } ]
  },
  "sizeEstimate" : 3187,
  "snippet" : "TEsting history С уважением, Николай Кобзев",
  "threadId" : "15ab360b3f297274"
}
*/