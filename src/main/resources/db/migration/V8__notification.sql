-- таблица уведомлений для администратора
create table notification (
  id varchar(128) not null,
  title varchar(128) not null,
  text varchar(255) not null,
  type varchar(60) not null,
  /* id просмотревшего */
  viewed_subject_id varchar(128),
  /* дата просмотра уведомления */
  viewed_date timestamp,
  /* id целевого объекта уведомления */
  target_id varchar(128),
  /* имя целевого ресурса уведомления */
  target_resource varchar(60),
  is_root_only boolean not null default FALSE,
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (viewed_subject_id) references subject on delete cascade
);

/* NOTIFICATIONS */
insert into notification (id, title, text, type, is_root_only) values('4e426c7a-402b-4f63-914a-a558d65b30bf','Тестовое уведомление', 'Это простое уведомление для тестирования','NORMAL',false)
insert into notification (id, title, text, is_root_only) values('424991ea-031a-4383-8924-ec7694ed9a2d','Еще одно тестовое уведомление', 'Это второе простое уведомление для тестирования',false)