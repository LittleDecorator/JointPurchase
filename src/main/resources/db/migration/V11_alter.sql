-- "Самовывоз"
update delivery set hint='Самовывоз осуществляется около станции м. Строгино по предварительной договоренности. Если Вы планируете забрать товар самостоятельно, то мы с Вами свяжемся по указанному Вами телефону и договоримся о встрече.' where id='5e24b8ea-8a81-4984-ae92-fed630d5b859';
-- "Доставка по России"
update delivery set hint='Доставка по России осуществляется почтой России или компанией СДЭК. Стоимость рассчитывается индивидуально в зависимости от веса и объемов полученного заказа. Мы с Вами свяжемся для уточнения способа отправки. Оплата осуществляется предварительно по направленным нами реквизитам.' where id='72fab541-593e-48ef-882e-e226775f0b6f';
-- "Курьерская доставка"
update delivery set hint='Стоимость доставки по Москве 350 руб (в пределах МКАД), за пределы МКАД и по Московской области от 400 руб. Доставка осуществляется в течение 1-2 дней после оформления заказа. В комментариях к заказу просьба указать дни и интервалы времени, наиболее удобные для принятия заказа, а аткже иную важную для курьера информацию. В день доставки заказа курьер предварительно свяжется с Вами. Оплата производится наличными курьеру при получении заказа.' where id='b172e14f-33fd-4819-9831-09c54541feb4';

/* будем отмечать ошибочные посты */
alter table instagram_posts add column wrong_post boolean default false;
/* показываем ли данный пост на главной */
alter table instagram_posts add column show_on_main boolean default false;
/* добавим признак лидера продаж */
alter table item add column bestseller boolean default false;

update item set bestseller = true where id = 'ff8081815be41260015be437ae8f0087';
update item set bestseller = true where id = 'ff8081815be83fa0015be96306200230';
update item set bestseller = true where id = 'ff8081815bc4b077015bc4de7f60002c';
update item set bestseller = true where id = '4730453a-6b63-4761-9e01-3fbc269f614a';
update item set bestseller = true where id = '8feb329a-509a-4e7e-8d24-eac5e2bd0e8a';
update item set bestseller = true where id = 'ff8081815bbeb4a3015bbfe498ae0094';
update item set bestseller = true where id = '2da3bd33-98d5-47ee-9fed-1a4371bb6be6';
update item set bestseller = true where id = '5e24b8ea-8a81-4984-ae92-fed630d5b859';
update item set bestseller = true where id = 'ff8081815be83fa0015be89874e20143';
update item set bestseller = true where id = 'ff8081815b5b3476015b5b592e9f0027';
update item set bestseller = true where id = 'ff8081815be83fa0015be86620da0035';
update item set bestseller = true where id = 'ff8081815be83fa0015be85f431a0000';
update item set bestseller = true where id = 'ff8081815be41260015be44accc000cf';
update item set bestseller = true where id = 'ff8081815be83fa0015be8a61a84016a';
update item set bestseller = true where id = '8d27a24e-d18a-4503-a4db-a4a1ac6cc02a';
update item set bestseller = true where id = 'ff8081815be83fa0015be8c36b6e01c2';
update item set bestseller = true where id = 'fe639000-57cd-4cc3-8368-94d47b8369c6';
update item set bestseller = true where id = 'e4dcca8d-5925-4e12-9b78-baf79b06bccc';