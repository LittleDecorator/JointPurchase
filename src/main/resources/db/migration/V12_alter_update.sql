-- меняем обязательное поле
ALTER TABLE subscribers ALTER COLUMN subject_id DROP NOT NULL
ALTER TABLE subscribers ALTER COLUMN email set not null;

-- добавляем пару полей
alter table instagram_posts add column wrong_post boolean;
alter table instagram_posts add column show_on_main boolean DEFAULT false;

-- убираем совпадающие имена
update item set name='Петух кукарекующий' where id='2041c54d-70e1-4752-8d03-eb2778d73a14'
update item set name='Белый медвежонок (носик кверху)' where id='84b53e52-5abf-426a-9803-3ba327de28e3'
update item set name='Пирамида "Аленький цветочек"' where id='ff8081815be1d32f015be278a829013a'

-- добавляем уникальное ограничение на имя
ALTER TABLE item ADD CONSTRAINT un_name UNIQUE (translite_name);

-- сократим название
update item set name='Карандаши Lyra Groove Slim 24 цв.' where id='e4dcca8d-5925-4e12-9b78-baf79b06bccc'
update item set name='Переносной домик, розовый' where id='ff8081815be41260015be44accc000cf'
update item set name='Мелки-камушки 8 шт' where id='fe639000-57cd-4cc3-8368-94d47b8369c6'

--https://serverfault.com/questions/496530/sudo-etc-init-d-postgresql-restart-how-to-chose-which-installation