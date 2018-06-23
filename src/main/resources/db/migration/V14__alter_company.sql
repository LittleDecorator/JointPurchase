alter table company add column short_description varchar(255);
alter table company alter column date_add set default current_timestamp;
update "company" set date_add =current_timestamp where date_add is null;