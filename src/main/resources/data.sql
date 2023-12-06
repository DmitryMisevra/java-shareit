delete from users;
delete from items;

alter table users alter column user_id restart with 1;
alter table items alter column item_id restart with 1;