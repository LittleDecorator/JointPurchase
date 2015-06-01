/* COMPANIES*/
insert into company(id,name,description,url)
        values ('5e895423-3d9d-4415-a136-558762e7ac02','Томик','Производитель настоящих деревянных игрушек','http://tomik.ru/');
insert into company(id,name,description,url)
        values ('1ef9e65a-31f5-4581-809a-ade51f8fd3b2','Умная бумага','Интернет-магазин товаров компании','http://umbum.ru/');
insert into company(id,name,description,url)
        values ('288a0fb1-f49e-4cd9-b339-e995a86c1376','Мелик-Пашаев','Издательство выпускающее книги для детей от 0 до 10 лет','http://www.melik-pashaev.ru/');
insert into company(id,name,description,url)
        values ('f837c608-40de-42e6-99c4-e389c5f6a5be','Речь','Издательство','http://rech-deti.ru/');

/* PERSON */
insert into person(id,first_name,last_name,middle_name,phone_number,email,address)
        values ('d7651309-e8f4-47d5-a5a7-e4930456eae9','test', 'customer', 'name','1234567','test@email.ru','Some where on the north');
insert into person(id,first_name,last_name,middle_name,phone_number,email,address)
        values ('c0846837-6614-4171-aac4-52bda5d74cab','another', 'customer', 'nickname','1122334455','some@mail.ru','live in town');
insert into person(id,company_id,first_name,last_name,middle_name,phone_number,email,address,job)
        values ('557799db-2eec-4796-b416-b7f6169f7496','5e895423-3d9d-4415-a136-558762e7ac02','Test', 'Company', 'Employer','13572469','employer@mail.ru','live in town','slave');

/* CATEGORY */

insert into category(id,name)
        values ('a922dd31-fd80-48e8-9861-8030b387e060','Книги');
insert into category(id,name)
        values ('ce449af7-6ef5-4457-9ecc-9abb40e9044c','Игрушки');
insert into category(id,name,parent_id)
        values ('7dab5183-219d-436a-9aa2-8a576596d9fc','Детские игрушки','ce449af7-6ef5-4457-9ecc-9abb40e9044c');
insert into category(id,name,parent_id)
        values ('b796c68a-fbd3-4c56-8729-aaaa4c549b36','Деревянные детские игрушки','7dab5183-219d-436a-9aa2-8a576596d9fc');

/* ITEMS */
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('622323dd-00eb-4a94-b7f0-31b91d91fefa', 'Кубики «Алфавит» английский','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',150,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('1a05535f-c373-404d-9117-e920792cecf0', 'Кубики «Алфавит» русский','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',150,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('23f9a890-525e-401d-9499-cd51dedc4103', 'Кубики «Цифры»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',150,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('91972e13-1983-4a74-9b48-c8e4909dd324', 'Кубики «Алфавит с цифрами» русский','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',200,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('bd55b526-a698-41f9-8bf4-85fc8eb061f9', 'Кубики «Сложи рисунок: животные»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('2d808a5c-cbec-4481-a273-9127c8caa326', 'Кубики «Сложи рисунок: Фрукты-ягоды»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('5e5ec03d-c084-430e-93dc-3571600cfa87', 'Кубики «Сложи рисунок: игрушки»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('99085e3e-3f6e-4ccd-9c12-81c4ada6c161', 'Кубики «Сложи рисунок: посуда»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('ab5333b1-2b57-4c6f-9ca0-43dc251a79aa', 'Кубики «Сложи рисунок: мебель»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);
insert into item(id,name,company_id,category_id,price,in_stock,in_order)
        values ('846ffe25-bc3a-4188-8ef1-be6c3e9e04e3', 'Кубики «Сложи рисунок: овощи»','5e895423-3d9d-4415-a136-558762e7ac02','b796c68a-fbd3-4c56-8729-aaaa4c549b36',90,0,0);

/* ORDERS */
insert into purchase_order(id,name,person_id,payment)
        values ('f266904c-ecc7-4b8b-97d6-ef61571cde66','test purchase','d7651309-e8f4-47d5-a5a7-e4930456eae9',350);
insert into purchase_order(id,name,person_id,payment)
        values ('ad7d2c80-fb59-4968-9862-a10282a5477d','another test purchase','d7651309-e8f4-47d5-a5a7-e4930456eae9',1350);

/* ORDER_ITEMS */
insert into order_items(id,order_id,item_id,cou)
        values ('074a766a-cf0b-4666-94fb-ffad98a4dcdf','f266904c-ecc7-4b8b-97d6-ef61571cde66','622323dd-00eb-4a94-b7f0-31b91d91fefa',1);
insert into order_items(id,order_id,item_id,cou)
        values ('a7cbd408-b108-4b3b-8b6e-434f8be0da07','f266904c-ecc7-4b8b-97d6-ef61571cde66','1a05535f-c373-404d-9117-e920792cecf0',1);
insert into order_items(id,order_id,item_id,cou)
        values ('8846694c-6c74-481b-bdda-9120d6f8c0b9','f266904c-ecc7-4b8b-97d6-ef61571cde66','23f9a890-525e-401d-9499-cd51dedc4103',1);
insert into order_items(id,order_id,item_id,cou)
        values ('64a72ecd-7fc1-40b8-b89f-46554479a253','f266904c-ecc7-4b8b-97d6-ef61571cde66','91972e13-1983-4a74-9b48-c8e4909dd324',1);

/*CREDENTIALS*/
insert into credentials(id,user_id,login,password,role_id)
        values('05ca1e6a-e665-498f-ac79-2762517b05d5','d7651309-e8f4-47d5-a5a7-e4930456eae9','testUser','testuser','USER');
insert into credentials(id,user_id,login,password,role_id)
        values('2d724eaf-9c81-49b4-a0b2-f20f6b7cb52f','c0846837-6614-4171-aac4-52bda5d74cab','clogin','cpassword','ADMIN');