package com.acme.model.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by kobzev on 18.02.17.
 *
 * Составной ключ таблицы связи между "Item" и "Content"
 */

@Embeddable
public class ItemContentId implements Serializable {

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "content_id")
	private String contentId;

	public ItemContentId() {
	}

	public ItemContentId(String itemId, String contentId) {
		this.itemId = itemId;
		this.contentId = contentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				 + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result
				 + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ItemContentId other = (ItemContentId) obj;

		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;

		if (contentId == null) {
			if (other.contentId != null)
				return false;
		} else if (!contentId.equals(other.contentId))
			return false;

		return true;
	}

}
