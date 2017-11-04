-- таблица подписчиков на рассылки
create table subscribers (
  id varchar(128) not null,
  email varchar(30) not null,
  subject_id varchar(128),
  active boolean not null default false,
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (subject_id) references subject
);

/* добавим флаг в таблицу media, говорящий о том, что данные взяты из instagram */
alter table content add column is_instagram boolean not null default false;
/* добавим флаг в таблицу media, говорящий о том, что данные являются профильными */
alter table content add column is_profile boolean not null default false;

-- таблица пользователей инстаграм
create table instagram_users (
	id varchar(128) not null,
	origin_id varchar(128) not null,
	subject_id varchar(128),
	name varchar(255) not null,
	full_name varchar(255) not null,
	profile_image_id varchar(255),
	description varchar(255),
	website varchar(255),
	followers_count integer,
	followed_by_count integer,
	date_add timestamp default current_timestamp,
	primary key (id),
	foreign key (subject_id) references subject,
	foreign key (profile_image_id) references content
);

-- таблица публикаций в инстаграм
create table instagram_posts (
	id varchar(128) not null,
	origin_id varchar(128) not null,
	content text,
	user_id varchar(128),
	external_url varchar(256),
	create_time timestamp,
	user_has_liked boolean default false,
	likes_count integer,
	tags text,
	date_add timestamp default current_timestamp,
	primary key (id),
	foreign key (user_id) references instagram_users
);

-- таблица связи публикации с медиа
create table instagram_post_content (
	id varchar(128) not null,
	post_id varchar(37) not null,
  content_id varchar(37) not null,
  show boolean not null default false,
	date_add timestamp default current_timestamp,
	primary key (id),
	foreign key (post_id) references instagram_posts,
	foreign key (content_id) references content
);

-- таблица отложенных и заказанных товаров клиентов
create table wish_lists (
  id varchar(128) not null,
  email varchar(30) not null,
  subject_id varchar(128),
  item_id varchar(128) not null,
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (subject_id) references subject,
  foreign key (item_id) references item
);

-- добавляем поле транслита в товар
alter table item add column translite_name varchar(128);
-- добавляем поле транслита, описания и ссылки на изображение в таблицу категорий
alter table category add column translite_name varchar(128);
--alter table category add column description varchar(2000);
alter table category add column description text;
alter table category add column content_id varchar(37);
--alter table category alter column description type text
-- добавляем ссылку на изображение в таблицу поставщиков
alter table company add column content_id varchar(37);
alter table company add column translite_name varchar(128);
alter table company alter column description type text

alter table item add column age varchar(5);
alter table item add column size varchar(128);
alter table item add column material varchar(60);
