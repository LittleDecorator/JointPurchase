package com.acme.enums;

import java.util.Collections;
import java.util.List;

/**
 * Created by kobzev on 14.03.17.
 *
 */
public enum GmailLabels {

	INBOX, SENT, DRAFT, UNREAD, TRASH, CLIENT;

	private GmailLabels label;

	GmailLabels() {
		this.label = this;
	}

	/**
	 * Получение елемента в виде списка с одним элементом
	 * @return
	 */
	public List<String> asSingleList(){
		return Collections.singletonList(label.name());
	}

}
