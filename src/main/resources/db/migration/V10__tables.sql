-- таблица подписчиков на рассылки
create table subscribers (
  id varchar(128) not null,
  email varchar(30),
  subject_id varchar(128) not null,
  active boolean not null default false,
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (subject_id) references subject
);

/* добавим флаг в таблицу media, говорящий о том, что данные взяты из instagram */
alter table content add column is_instagram boolean not null default false;