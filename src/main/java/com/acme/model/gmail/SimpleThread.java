package com.acme.model.gmail;

import com.google.api.client.util.Lists;
import com.google.api.services.gmail.model.Thread;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by kobzev on 12.03.17.
 *
 *
 */
public class SimpleThread {

	private String id;
	private BigInteger historyId;
	private List<SimpleMessage> messages;

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

	public List<SimpleMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<SimpleMessage> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "SimpleThread{" +
			   "id='" + id + '\'' +
			   ", historyId=" + historyId +
			   ", messages=" + messages +
			   '}';
	}

	public static SimpleThread valueOf(Thread thread){
		SimpleThread simpleThread = new SimpleThread();
		simpleThread.setId(thread.getId());
		simpleThread.setHistoryId(thread.getHistoryId());
		simpleThread.setMessages(Lists.newArrayList());
		return simpleThread;
	}
}
