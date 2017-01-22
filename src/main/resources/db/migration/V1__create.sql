-- таблица для бинарных данных
create table content (
	id varchar(128) not null,
	content text,
	file_name varchar(255),
	mime varchar(25),
	type varchar(25),
	is_default char(1) not null default 'N',
	date_add timestamp default current_timestamp,
	primary key (id)
);

-- таблица компании производителя
create table company (
  id varchar(128) not null,
  name varchar(50) not null,
  description varchar(255),
  address varchar(255),
  email varchar(30),
  phone varchar(30),
  url varchar(30),
  bik varchar(10),
  inn varchar(10),
  ks varchar(10),
  rs varchar(10),
  date_add timestamp default current_timestamp,
  primary key (id)
);

-- роли
create table role (
  id varchar(128) not null,
  description varchar(255),
  parent_role_id varchar(128),
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (parent_role_id) references role on delete cascade
);

-- таблица покупателей
create table subject (
  id varchar(128) not null,
  enabled char(1) not null default 'N' check(enabled in ('Y', 'N')),
  first_name varchar(30) not null,
  middle_name varchar(30),
  last_name varchar(30) not null,
  phone_number varchar(20),
  email varchar(30) not null,
  address varchar(255),
  post_address integer,
  date_add timestamp default current_timestamp,
  primary key (id)
);

-- удостоверение покупателя
create table credential(
  subject_id varchar(128) not null,
  password varchar(255) not null,
  role_id varchar(37),
  date_add timestamp default current_timestamp,
  primary key (subject_id),
  foreign key (subject_id) references subject,
  foreign key (role_id) references role
);

-- таблица типов доставок
create table delivery(
  id varchar(37) not null,
  name varchar(37) not null,
  hint text not null,
  date_add timestamp default current_timestamp,
  primary key (id)
);

-- таблица заказов
create table purchase_order (
  id varchar(37) not null,                --id
  subject_id varchar(37),        -- ссылка на оформителя (может отсутствовать для незарегистрированных)
  uid bigint not null,                    -- номер заказа
  recipient_fname varchar(37) not null,     -- имя получателя
  recipient_lname varchar(37) not null,     -- фамилия получателя
  recipient_mname varchar(37),     -- отчество получателя
  recipient_email varchar(37) not null,     -- email получателя
  recipient_phone varchar(37),              -- телефон получателя
  recipient_address varchar(255) not null,        -- адрес доставки
  date_add timestamp default current_timestamp,    --дата создания заказа
  close_order_date timestamp,     --дата закрытия заказа
  comment varchar(255),     -- комментарий
  status varchar(30) default 'new',     --статус заказа
  delivery_id varchar(37) not null,
  payment int not null,      -- сумма к оплате
  primary key (id),
  foreign key (subject_id) references subject,
  foreign key (delivery_id) references delivery
);

--категории товаров (дерево которое содержит также и подкатегорию) - меню
create table category (
   id varchar(37) not null,
   name varchar(128) not null,
   parent_id varchar(37),
   date_add timestamp default current_timestamp,
   PRIMARY key (id),
   foreign key (parent_id) references category
);

-- таблица товара
create table item (
  id varchar(37) not null,
  name varchar(255) not null,
  company_id varchar(37) not null,
  article varchar(30),
  description varchar(2000),
  price int not null, --цена еденицы товара
  date_add timestamp default current_timestamp,
  not_for_sale char(1) not null default 'N' check(not_for_sale in ('Y', 'N')),
  status varchar(30) default 'available',
  in_stock int not null default 0,
  primary key (id),
  foreign key (company_id) references company
);

--связь категорий и товара
create table category_item(
  id varchar(37) not null,
  category_id varchar(37) not null,
  item_id varchar(37) not null,
  date_add timestamp default current_timestamp,
  PRIMARY key (id),
  foreign key (category_id) REFERENCES category,
  FOREIGN key (item_id) REFERENCES item
);

-- таблица связи заказа и товара
create table order_item (
  id varchar(37) not null,
  order_id varchar(37) not null,
  item_id varchar(37) not null,
  cou int not null,       --количество наименования в заказе
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (order_id) REFERENCES purchase_order,
  FOREIGN key (item_id) REFERENCES item
);

-- таблица связи товара и изображений
create table item_content(
  id varchar(37) not null,
  item_id varchar(37) not null,
  content_id varchar(37) not null,
  show char(1) not null default 'N' check(show in ('Y', 'N')),
  main char(1) not null default 'N' check(main in ('Y', 'N')),
  date_add timestamp default current_timestamp,
  primary key (id),
  foreign key (item_id) references item,
  foreign key (content_id) references content
);

--список email аккаунтов
create table email (
	id varchar(37) not null,
	smtp_server varchar(255), --email smtp server: smtp.gmail.com
	smtp_port integer, --email port
	username varchar(255) not null, --email username: qwerty@qwerty.ru
	password varchar(255) not null, --email password: 12345678
	compromised char(1) not null default 'N' check(compromised in ('Y', 'N')),
	primary key (id)
);

--список sms аккаунтов
create table sms (
	id varchar(37) not null,
	service_name varchar(255) not null,-- sms24x7.ru, ...
	email_id varchar(37) not null,
	credential_login varchar(255) not null,
	credential_password varchar(255) not null,
	active char(1) not null default 'Y' check(active in ('Y', 'N')),
	balance varchar(255),
	currency varchar(255),
	compromised char(1) not null default 'N' check(compromised in ('Y', 'N')),
	primary key (id),
	foreign key (email_id) references email
);

--global settings
create table settings (
	id integer,

	--параметры html clipper
	clipper_id varchar(37), -- ссылка на бинарник javascript скрипта для грабления шаблона html страницы
	clipper_jquery_url text, --ссылка на jquery (используется скриптом грабления)
	clipper_xpath_wait varchar(255), -- xpath string for wait page render

	--параметры генерации сертификата для первого захода в систему
	certificate_gen char(1) not null default 'N' check(certificate_gen in ('Y', 'N')),

	--параметры для билдера
	builder_host varchar(255),   --ip for host builder
	builder_ssh_port integer default 22,   --ssh port for host builder
	builder_ssh_login varchar(255),   --ssh login for host builder
	builder_ssh_password varchar(255),   --ssh password for host builder
	builder_cmd_run text,   --command builder run
	builder_exe text,   --exe file (full path) of builder (result of build)
-- 	builder_bat text,   --batch file content
	builder_build_command varchar(255),     --builder create command
	builder_encrypt_command varchar(255),    --encrypting command
	builder_sign_command varchar(255),    --singing command
	builder_app_host varchar(255),      --server app url
	builder_upload_path text,         -- remote path for batch file
	builder_wait_timeout integer,

	--параметры для обложки
	cover_url_local text, --локальный url обложки (всегда постоянный - не менять - для информации)
	cover_url text, --внешний/реальный/проксированный url обложки

	--email account for smtp server
	smtp_id varchar(37),

	--активный sms аккаунт
	sms_id varchar(37),

	--цепочка активных серверов для анонимизатора
	vps1_id varchar(37),
	vps2_id varchar(37),
	vps3_id varchar(37),
	vps4_id varchar(37),
	resources_domain varchar(255) not null default 'localhost',
	resources_template_context_external varchar(255) not null default 'http://{{domain}}:8080/pub',
	resources_template_context_internal varchar(255) not null default 'http://{{domain}}:8080/pub',

	auto_check_blacklist integer default 30,--интервал автопроверки по blacklist-ам
	blacklist_check_on char(1) not null default 'N' check(blacklist_check_on in ('Y', 'N')),

        auto_check_search integer default 30, --интервал автопроверки поисковыми системами
	search_check_on char(1) not null default 'N' check(search_check_on in ('Y', 'N')),
        search_pattern varchar(255) default 'xer_takoe_naidesh',

        auto_check_antivirus integer default 30,--интервал автопроверки по антивирусам
	virus_check_on char(1) not null default 'N' check(virus_check_on in ('Y', 'N')),

        auto_check_getlog integer default 30,--интервал автопроверки по антивирусам
	getlog_check_on char(1) not null default 'N' check(getlog_check_on in ('Y', 'N')),

	primary key (id),
    foreign key (clipper_id) references content,
    foreign key (sms_id) references sms,
    foreign key (smtp_id) references email
);