package com.acme.enums;

/**
 * Created by kobzev on 06.02.17.
 */
public enum ImageSize {

	//используется для отображения на странице каталога. Оптимальный размер 400x225
	PREVIEW(400,225),
	//используется в карточке товара. Оптимальный размер 800x600
	VIEW(800, 600),
	//используется в списках. Оптимальный размер 100x100
	THUMB(100,100),
	//используется на странице галереи. Оптимальный размер x500
	GALLERY(500,500),
	//используется в качестве оригинала. Оптимальный размер 1000x1000
	ORIGINAL(1000,1000),
	//по хорошему, нигде не должен использоваться явно, а только выступать в качестве источника
	RAW;

	int width, height;

	ImageSize() {
	}

	ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
