package com.acme.model.gmail;

import com.google.api.services.gmail.model.Draft;

/**
 * Created by kobzev on 12.03.17.
 *
 *
 */
public class SimpleDraft {

	private String id;
	private SimpleMessage message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SimpleMessage getMessage() {
		return message;
	}

	public void setMessage(SimpleMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SimpleDraft{" +
			   "id='" + id + '\'' +
			   ", message=" + message +
			   '}';
	}

	public static SimpleDraft valueOf(Draft draft){
		SimpleDraft simpleDraft = new SimpleDraft();
		simpleDraft.setId(draft.getId());
		SimpleMessage simpleMessage = new SimpleMessage();
		simpleMessage.setId(draft.getMessage().getId());
		simpleDraft.setMessage(simpleMessage);
		return simpleDraft;
	}
}
