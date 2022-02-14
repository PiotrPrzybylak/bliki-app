create table blikis
(
    id          serial
        primary key,
    name        text,
    description text
);

create table categories
(
    id          serial
        primary key,
    name        text,
    description text,
    bliki_id    integer
        constraint categories_bliki_fk
            references blikis
);


create index fki_categories_bliki_fk
    on categories (bliki_id);

create table users
(
    id       serial
        constraint users_pk
            primary key,
    username text not null,
    email    text,
    password text,
    admin    boolean,
    name     text,
    phone    text
);



create table links
(
    id          serial
        constraint links_pk
            primary key,
    href        text,
    text        text,
    rating      integer,
    description text,
    category_id integer
        constraint links_fk
            references categories
            on update restrict on delete restrict,
    bliki_id    integer
        constraint links_blik_fk
            references blikis
            on update restrict on delete restrict,
    user_id     integer
        constraint links_users_id_fk
            references users
            on update restrict on delete restrict
);


create unique index users_username_uindex
    on users (username);

