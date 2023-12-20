delete
from requests;
delete
from comments;
delete
from bookings;
delete
from users;
delete
from items;

alter table comments
    alter column comment_id restart with 1;
alter table requests
    alter column request_id restart with 1;
alter table bookings
    alter column booking_id restart with 1;
alter table users
    alter column user_id restart with 1;
alter table items
    alter column item_id restart with 1;