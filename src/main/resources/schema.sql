create table if not exists users
(
    user_id    integer generated by default as identity
        constraint users_pk
            primary key,
    user_name  varchar(255) not null,
    user_email varchar(255) not null
        constraint users_pk2
            unique
);

create table if not exists requests
(
    request_id          integer generated by default as identity
        constraint requests_pk
            primary key,
    request_description varchar(255) not null,
    requestor_id        integer      not null
        constraint requests_users_user_id_fk
            references users,
    created             timestamp    not null
);

create table if not exists items
(
    item_id          integer generated always as identity
        constraint items_pk
            primary key,
    item_name        varchar(255) not null,
    item_description varchar(200) not null,
    item_ownerid     integer      not null,
    item_available   boolean      not null,
    request_id       integer
        constraint items_requests_request_id_fk
            references requests
);

create table if not exists bookings
(
    booking_id     integer generated by default as identity,
    start_date     timestamp   not null,
    end_date       timestamp   not null,
    item_id        integer     not null
        constraint bookings_items_item_id_fk
            references items,
    booker_id      integer     not null
        constraint bookings_users_user_id_fk
            references users,
    booking_status varchar(50) not null
);

create table if not exists comments
(
    comment_id   integer generated by default as identity
        constraint comments_pk
            primary key,
    comment_text varchar(255) not null,
    item_id      integer      not null
        constraint comments_items_item_id_fk
            references items,
    author_id    integer      not null
        constraint comments_users_user_id_fk
            references users,
    created      timestamp    not null
);




