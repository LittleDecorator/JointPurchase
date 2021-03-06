/* COMPANIES*/
insert into company(id,name,description,url)
        values ('5e895423-3d9d-4415-a136-558762e7ac02','Томик','Производитель настоящих деревянных игрушек','http://tomik.ru/');
insert into company(id,name,description,url)
        values ('1ef9e65a-31f5-4581-809a-ade51f8fd3b2','Умная бумага','Интернет-магазин товаров компании','http://umbum.ru/');
insert into company(id,name,description,url)
        values ('288a0fb1-f49e-4cd9-b339-e995a86c1376','Мелик-Пашаев','Издательство выпускающее книги для детей от 0 до 10 лет','http://www.melik-pashaev.ru/');
insert into company(id,name,description,url)
        values ('f837c608-40de-42e6-99c4-e389c5f6a5be','Речь','Издательство','http://rech-deti.ru/');
insert into company(id,name,description,url)
        values ('40636ca3-6c19-4ecb-83a2-9027dcd5b22f','Grimms','Производитель настоящих деревянных игрушек','http://www.grimms.eu/en/');

/* ROLES */
insert into role(id,description)
        values('admin','admin role description');
insert into role(id,description,parent_role_id)
        values('user','user role description','admin');

/* PERSON */
insert into subject(id,first_name,last_name,middle_name,phone_number,email,address,enabled)
        values ('4e426c7a-402b-4f63-914a-a558d65b30bf','test', 'customer', 'name','1234567','test@email.ru','Some where on the north','N');
insert into subject(id,first_name,last_name,phone_number,email,address,enabled)
        values ('424991ea-031a-4383-8924-ec7694ed9a2d','Guzel', 'Kobzeva', '9258552096','fazylovagz@mail.ru','Moscow','Y');
insert into subject(id,first_name,last_name,email,enabled)
        values ('587ccc2a-a912-41ab-8edf-abf8eb986b86','Nikolay','Kobzev','npkobzev@mail.ru','Y');
insert into subject(id,first_name,last_name,email,enabled)
        values ('9d5443a0-9b7d-4ceb-9ce4-b792c63b0a6d','Olga','Yaremchishin','chudodrevo@mail.ru','Y');

/*CREDENTIALS*/
--password
insert into credential(subject_id,password,role_id)
        values('4e426c7a-402b-4f63-914a-a558d65b30bf','56cc66bac5b167db62297f5a6ee06af3b5efce10aa8bcc6a70ea2ffc785961c7e852257a1796e4ab9fd9737928c5bbea40225564868341cf8db69e2ae599dcc8','user');
--nina21032013
insert into credential(subject_id,password,role_id)
        values('424991ea-031a-4383-8924-ec7694ed9a2d','094de90aaea4750489a2f8c7a8cefd5a50a135cb12e4970d84586f3f1e883872640ff304b9ace30adcda5d8a2c6e92063e9875983788cd9829ca62a972465429','admin');
--25oct87!
insert into credential(subject_id,password,role_id)
        values('587ccc2a-a912-41ab-8edf-abf8eb986b86','1f9c15a7de0090c35d6693b76533819231d2adae494d70db74d3c12a9c0cdf189560c3623cbae47b2bc13a8ed0973aa47d9f94474fc706095786b3183da6ca20','admin');
--123QWE
insert into credential(subject_id,password,role_id)
        values('9d5443a0-9b7d-4ceb-9ce4-b792c63b0a6d','c04a2d154984e3eecc9f9fa612106ed851e14a9af7bcfdd18abc2a7ae0807b4289898bb6d7f119b0306d361573531e82938d317c795cad3a61a9c774ec5b421b','admin');

/* CATEGORY */
insert into category(id,name)
        values ('ce449af7-6ef5-4457-9ecc-9abb40e9044c','Малышам (первые игрушки)');
insert into category(id,name,parent_id)
        values ('8318fcf0-5885-462b-abf5-6916f273f86b','Мобили погремушки грызунки','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('7dab5183-219d-436a-9aa2-8a576596d9fc','Каталки, машинки','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('876d17c2-647c-4b00-9f5e-195d5e57b0b7','Пирамидки','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('259a8b87-358a-44f3-8878-9fb53437921c','Первые кубики','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('d400fe53-8918-4ece-821b-07c9cd5932f8','Куколки','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('a00ce058-a855-407c-ac37-641f8f46fa43','Сортеры','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('bd1c8d73-d3a5-476b-a116-4321e058332a','Творчество','ce449af7-6ef5-4457-9ecc-9abb40e9044c');

insert into category(id,name)
        values ('8ae88b67-02e1-4b29-98b8-e5c8df2cab9b','Строительные наборы');
insert into category(id,name,parent_id)
        values ('7b5ba523-0128-4619-97e3-0b8bb44d1d37','Первые кубики','7dab5183-219d-436a-9aa2-8a576596d9fc');
insert into category(id,name,parent_id)
        values ('34a56bf2-f7b5-4649-b087-cf1b34c5e739','Большие конструкторы','7dab5183-219d-436a-9aa2-8a576596d9fc');
insert into category(id,name,parent_id)
        values ('39b79b6f-095c-4d5b-a861-fbd1c7dd4770','Настольные конструкторы','7dab5183-219d-436a-9aa2-8a576596d9fc');
insert into category(id,name,parent_id)
        values ('ed7cffb0-dbd4-420a-a1f6-15425fd1585f','Наборы "Фэнтэзи" и неправильные формы','7dab5183-219d-436a-9aa2-8a576596d9fc');
insert into category(id,name,parent_id)
        values ('15226981-a954-40a0-9763-89c6dbdf6884','Большие мозаики','7dab5183-219d-436a-9aa2-8a576596d9fc');
insert into category(id,name,parent_id)
        values ('2c6f60b6-a289-47a7-8557-134c32b026e0','Маленькие мозаики','7dab5183-219d-436a-9aa2-8a576596d9fc');

insert into category(id,name)
        values ('6c34bb23-9702-41ec-a4b4-506616c3e0aa','Бусины');
insert into category(id,name,parent_id)
        values ('4171ca0f-db80-42c1-8dd1-4a0bc2bfdbdc','Бусины','6c34bb23-9702-41ec-a4b4-506616c3e0aa');
insert into category(id,name,parent_id)
        values ('b89b3849-6a19-44e0-9d7c-f97170bbfff2','Диски','6c34bb23-9702-41ec-a4b4-506616c3e0aa');
insert into category(id,name,parent_id)
        values ('2f01a4b9-f145-4321-a63d-ff2915817291','Ожерелье','6c34bb23-9702-41ec-a4b4-506616c3e0aa');


insert into category(id,name)
        values ('4fe67e25-6557-41a8-b59d-187e19f121b2','Транспорт');
insert into category(id,name,parent_id)
        values ('cd564aa1-d3d6-4761-94f9-31ace8394c3c','Машинки, поезда, лодки','4fe67e25-6557-41a8-b59d-187e19f121b2');
insert into category(id,name,parent_id)
        values ('f679c54c-bd8d-4e5c-9a8d-f9e2a546d096','Лошади и повозки','4fe67e25-6557-41a8-b59d-187e19f121b2');

insert into category(id,name)
        values ('4e09566a-73a4-4274-8681-fafa52e6fa9b','Домики и куклы');
insert into category(id,name,parent_id)
        values ('9ac57c37-3914-473d-bdfa-9dd7724b0796','Первые куклы','4e09566a-73a4-4274-8681-fafa52e6fa9b');
insert into category(id,name,parent_id)
        values ('3d021e55-4c08-4b93-95eb-9db447ea3cee','Куклы','4e09566a-73a4-4274-8681-fafa52e6fa9b');
insert into category(id,name,parent_id)
        values ('74e92d18-007e-4896-96b3-0d11c21279cb','Домики и мебель','4e09566a-73a4-4274-8681-fafa52e6fa9b');

insert into category(id,name)
        values ('0e7b3010-74cd-4f93-acd5-ecc883737828','Фигурки для ролевых игр');
insert into category(id,name,parent_id)
        values ('f1d3d600-3206-40e8-89bd-48f0ad58f4b0','Животные и птицы','0e7b3010-74cd-4f93-acd5-ecc883737828');
insert into category(id,name,parent_id)
        values ('e88d2030-a352-448f-bad4-0996880302a5','Деревья','0e7b3010-74cd-4f93-acd5-ecc883737828');
insert into category(id,name,parent_id)
        values ('cfe35ede-468b-4ded-9bce-d0ebf7efca74','Люди, сказочные персонажи','0e7b3010-74cd-4f93-acd5-ecc883737828');
insert into category(id,name,parent_id)
        values ('757ab974-d336-4c5c-9f4e-48f88509e875','Игровой шёлк','0e7b3010-74cd-4f93-acd5-ecc883737828');
insert into category(id,name,parent_id)
        values ('02d4a7ad-efc6-4616-8de7-e6a386ac685f','Аксессуары','0e7b3010-74cd-4f93-acd5-ecc883737828');

insert into category(id,name)
        values ('1f8d4232-01d2-473d-ba44-4a61b1e408c3','Учимся играя');
insert into category(id,name,parent_id)
        values ('e38e77a8-afe8-4a65-bc8e-342731d303ee','Буквы, цифры','1f8d4232-01d2-473d-ba44-4a61b1e408c3');
insert into category(id,name,parent_id)
        values ('8470062a-7521-4a14-a916-845f1b1f4422','Игры, головоломки','1f8d4232-01d2-473d-ba44-4a61b1e408c3');
insert into category(id,name,parent_id)
        values ('fd78f25e-b1cf-4b07-8b44-89e08140abe6','Карточки','1f8d4232-01d2-473d-ba44-4a61b1e408c3');

insert into category(id,name)
        values ('a597eaa5-b74a-45ef-af60-18c7c7d3e140','Любимые книги');
insert into category(id,name,parent_id)
        values ('a9c1f075-658d-41d8-a721-5d302f880f44','Виммельбуки','a597eaa5-b74a-45ef-af60-18c7c7d3e140');
insert into category(id,name,parent_id)
        values ('049f1393-3454-4dd1-ae50-82fa6cc329c0','Книги на простом языке','a597eaa5-b74a-45ef-af60-18c7c7d3e140');
insert into category(id,name,parent_id)
        values ('9c70fde0-86b3-4aab-9fe8-b1b22515697b','Открытки','a597eaa5-b74a-45ef-af60-18c7c7d3e140');

/* ITEMS */
insert into item(id,name,company_id,article,description,price)
        values ('5e24b8ea-8a81-4984-ae92-fed630d5b859', '7 друзей в стаканчиках','40636ca3-6c19-4ecb-83a2-9027dcd5b22f','10580','Seven Friends in 7 bowls: Sorting and matching peg dolls in wooden frame (for each day). Tip: You can style the peg dolls with cloth or tape. Our Bus (09480) fits perfectly with the peg dolls. Each hand-painted peg doll is unique! Materials: alder and maple wood, non-toxic water based color stain/non-toxic plant based oil finish. Size: frame diameter 19cm, peg dolls height 6cm, diameter 3cm.',1000);
insert into item(id,name,company_id,article,price,in_stock)
        values ('b172e14f-33fd-4819-9831-09c54541feb4', 'Радуга (малая)','40636ca3-6c19-4ecb-83a2-9027dcd5b22f','10760',1650,1);
insert into item(id,name,company_id,article,description,price,in_stock)
        values ('72fab541-593e-48ef-882e-e226775f0b6f', 'Радуга (12 частей)','40636ca3-6c19-4ecb-83a2-9027dcd5b22f','10670','The large Rainbow is really versatile and ideal already for small children! Toddlers stack, sort and build and as the children get older they will use it as a cradle for dolls, as fence for animals, like a tunnel or bridge for vehicles, as house for dwarfs and dollhouse dolls, build amazing sculptures... this rainbow will always be integrated in playing with a lot of fantasy. Materials: lime wood, non-toxic water based color stain. Size: length 38cm, height 18cm.',3500,2);
insert into item(id,name,company_id,article,price)
        values ('6a20e159-7951-4f65-88f8-3b5696e411fd', 'Радуга (6 частей)','40636ca3-6c19-4ecb-83a2-9027dcd5b22f','10700',2000);

/* CATEGORY ITEMS */
insert into category_item(id,category_id,item_id)
        values ('5e24b8ea-8a81-4984-ae92-fed630d5b859','a00ce058-a855-407c-ac37-641f8f46fa43','5e24b8ea-8a81-4984-ae92-fed630d5b859');
insert into category_item(id,category_id,item_id)
        values ('b172e14f-33fd-4819-9831-09c54541feb4','876d17c2-647c-4b00-9f5e-195d5e57b0b7','b172e14f-33fd-4819-9831-09c54541feb4');
insert into category_item(id,category_id,item_id)
        values ('72fab541-593e-48ef-882e-e226775f0b6f','34a56bf2-f7b5-4649-b087-cf1b34c5e739','72fab541-593e-48ef-882e-e226775f0b6f');
insert into category_item(id,category_id,item_id)
        values ('6a20e159-7951-4f65-88f8-3b5696e411fd','a00ce058-a855-407c-ac37-641f8f46fa43','6a20e159-7951-4f65-88f8-3b5696e411fd');

/* DELIVERY */
insert into delivery(id,name,hint)
        values ('5e24b8ea-8a81-4984-ae92-fed630d5b859', 'Самовывоз','Стоимость доставки заказа в пункт самовывоза - 100 руб. Вы также можете самостоятельно забрать заказ в Пункте Самовывоза, расположенном по адресу г. Москва, Огородный проезд, 20, стр. 38 с понедельника по субботу с 10-30 до 20-00.');
insert into delivery(id,name,hint)
        values ('b172e14f-33fd-4819-9831-09c54541feb4', 'Курьерская доставка','Курьерская доставка по Москве — 350руб. Если Вы находитесь в Москве мы доставим Вам заказ, сделанный до 14.00, на следующий день после его оформления. Оплата – наличными курьеру при получении заказа.Стоимость доставки в пределах МКАД 350 руб. Доплата за доставку за МКАД(не далее 15 км от МКАД) + 50 руб. за каждые полные/неполные 5 км. от МКАД. Доставка осуществляется ежедневно c 10:00 до 20:00. В день доставки за полчаса или час курьер свяжется с Вами. При заказе от 7000 руб. доставка осуществляется бесплатно.');
insert into delivery(id,name,hint)
        values ('72fab541-593e-48ef-882e-e226775f0b6f', 'Доставка по России','Примерные тарифы и варианты доставки в Ваш регион Вы можете увидеть в разделе "Оплата и доставка". После получения заказа, мы дополнительно свяжемся с Вами для согласования точной стоимости доставки.');

--smtp account
-- insert into email(id,smtp_server,smtp_port,username,password) values('29e2dbc6-85d7-4be7-b61b-f6e7e9ace893','smtp.mail.ru',587,'ru_vatnik@mail.ru','vatnik');
-- insert into email(id,smtp_server,smtp_port,username,password) values('c0846837-6614-4171-aac4-52bda5d74cab','smtp.gmail.com',587,'uglarius@gmail.com','1qaz@wsx3EDC');
--sms account
-- insert into email(id,username,password) values('d7651309-e8f4-47d5-a5a7-e4930456eae9','alskdjf.lsdkjf@mail.ru','Bfp6Gkd');
-- insert into sms(id,service_name,email_id,credential_login,credential_password) values('1f3eb80b-8357-4f64-955e-15d4f7fba30a','sms24x7.ru','d7651309-e8f4-47d5-a5a7-e4930456eae9','alskdjf.lsdkjf@mail.ru','Bfp6Gkd');

insert into settings values ('send_sms', 'Boolean', 'true', 'Признак необходимости отпраылять sms-уведомление о заказе')