-- таблица для бинарных данных
create table content (
	id varchar(37) not null,
	content text,
	file_name varchar(255),
	mime varchar(25),
	type varchar(25),
	is_default char(1) not null default 'N',
	primary key (id)
);

-- таблица компании производителя
create table company (
  id varchar(37) not null,
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
  primary key (id)
);

-- роли
create table role (
  id varchar(37) not null,
  description varchar(255),
  parent_role_id varchar(37),
  primary key (id),
  foreign key (parent_role_id) references role on delete cascade
);

-- таблица покупателей
create table subject (
  id varchar(37) not null,
  enabled char(1) not null default 'Y' check(enabled in ('Y', 'N')),
  first_name varchar(30) not null,
  middle_name varchar(30),
  last_name varchar(30),
  phone_number varchar(20),
  email varchar(30),
  address varchar(255),
  post_address integer,
  primary key (id)
);

-- удостоверение покупателя
create table credential(
  subject_id varchar(37) not null,
  password varchar(37) not null,
  role_id varchar(37),
  primary key (subject_id),
  foreign key (subject_id) references subject,
  foreign key (role_id) references role
);

-- таблица заказов
create table purchase_order (
  id varchar(37) not null,      --id
  subject_id varchar(37) not null,        -- ссылка на покупателя
  create_order_date timestamp default current_timestamp,    --дата создания заказа
  close_order_date timestamp,     --дата закрытия заказа
  comment varchar(255),     -- комментарий
  status varchar(30) default 'new',     --статус заказа
  payment decimal(20,2),      -- сумма к оплате
  primary key (id),
  foreign key (subject_id) references subject
);

--категории товаров (дерево которое содержит также и подкатегорию)
create table category (
   id varchar(37) not null,
   name varchar(37) not null,
   parent_id varchar(37),
   PRIMARY key (id),
   foreign key (parent_id) references category
);

-- таблица товара
create table item (
  id varchar(37) not null,
  name varchar(255) not null,
  company_id varchar(37) not null,
  category_id varchar(37) not null,
  article varchar(30),
  description varchar(255),
  price decimal(20,2) not null, --цена еденицы товара
  primary key (id),
  foreign key (company_id) references company,
  foreign key (category_id) references category
);

-- таблица связи товара с добавляющим, для строгого разграничения кураторов товара
create table item_owner (
    id varchar(37) not null,
    item_id varchar(37) not null,
    count int default 0,
    user_add varchar(37) not null,
    date_add timestamp default CURRENT_TIMESTAMP,
    primary key (id),
    foreign key (item_id) references item,
    foreign key (user_add) references subject
);

-- таблица связи заказа и товара
create table order_items (
  id varchar(37) not null,
  order_id varchar(37) not null,
  item_id varchar(37) not null,
  cou int not null,       --количество наименования в заказе
  primary key (id),
  foreign key (order_id) REFERENCES purchase_order,
  FOREIGN key (item_id) REFERENCES item
);

-- таблица связи товара и изображений
create table item_content(
  id varchar(37) not null,
  item_id varchar(37) not null,
  content_id varchar(37) not null,
  primary key (id),
  foreign key (item_id) references item,
  foreign key (content_id) references content
);