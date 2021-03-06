-- таблица акций
create table sale (
	id varchar(128) not null,
	title varchar(255) not null,
	description varchar(255),
	banner_id varchar(255),
	discount int,
	start_date timestamp,
	end_date timestamp,
	date_add timestamp default current_timestamp,
	primary key (id),
	foreign key (banner_id) references content
);

-- таблица связи акции с товаром
create table sale_item (
	sale_id varchar(37) not null,
  item_id varchar(37) not null,
	date_add timestamp default current_timestamp,
	primary key (sale_id, item_id),
	foreign key (sale_id) references sale,
	foreign key (item_id) references item
);