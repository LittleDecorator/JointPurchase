-- таблица для бинарных данных
create table content (
	id varchar(37) not null,
	content text,
	file_name varchar(255),
	path varchar(1000),
	mime varchar(255),
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
--   payment_information varchar
  bik varchar(10),
  inn varchar(10),
  ks varchar(10),
  rs varchar(10),
  primary key (id)
);

-- таблица покупателей
create table person (
  id varchar(37) not null,
  company_id varchar(37),
  first_name varchar(30) not null,
  middle_name varchar(30),
  last_name varchar(30),
  job varchar(37),
  phone_number varchar(20),
  email varchar(30),
  address varchar(255),
  post_address integer,
  primary key (id),
  foreign key (company_id) references company
);

-- таблица дополнительной информации по покупателям
-- представлят из себя пары ключ-значение
-- create table bla ^)

-- таблица заказов
create table purchase_order (
  id varchar(37) not null,      --id
  name varchar(255) not null,   -- название заказа
  person_id varchar(37) not null,        -- ссылка на покупателя
  create_order_date timestamp default current_timestamp,    --дата создания заказа
  close_order_date timestamp,     --дата закрытия заказа
  comment varchar(255),     -- комментарий
  status varchar(30) default 'new',     --статус заказа
  payment decimal(20,2),      -- сумма к оплате
  parent_order_id varchar(37),   -- ссылка на родительский заказ, в случае объединения заказов
  primary key (id),
  foreign key (person_id) references person,
  foreign key (parent_order_id) references purchase_order
);



--служащие компании, люди с которыми ведется переписка
create table company_employers (
  id varchar(37) not null,
  company_id varchar(37) not null,
  person_id varchar(37) not null,
  primary key(id),
  foreign key (company_id) references company,
  foreign key (person_id) references person
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
  in_stock int default 0, --количество товара, который уже есть у нас
  in_order int DEFAULT 0, --количество заказов данного товара
  primary key (id),
  foreign key (company_id) references company,
  foreign key (category_id) references category
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

create table credentials(
  id varchar(37) not null,
  user_id varchar(37) not null,
  login varchar(37) not null,
  password varchar(37) not null,
  role_id varchar(37),
  primary key (id),
  foreign key (user_id) references person
);