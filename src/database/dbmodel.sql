drop table if exists comment;
drop table if exists follow;
drop table if exists ques_topic;
drop table if exists answer;
drop table if exists topic;
drop table if exists agree;
drop table if exists question;
drop table if exists user;
create table user
(
    id        varchar(32) primary key,
    name      varchar(32) not null unique,
    password  varchar(32) not null,
    introduce tinytext default null,
    head      tinytext default null
);

create table follow
(
    follow_id   varchar(32) not null,
    followed_id varchar(32) not null,
    primary key (follow_id, followed_id),
    foreign key (follow_id) references user (id),
    foreign key (followed_id) references user (id)
);

create table question
(
    id       varchar(32) primary key,
    question tinytext    not null,
    content  text        not null,
    user_id  varchar(32) not null,
    time     timestamp default now(),
    foreign key (user_id) references user (id)
);

create table answer
(
    id          varchar(32) primary key,
    question_id varchar(32) not null,
    user_id     varchar(32) not null,
    content     text        not null,
    time        timestamp default now(),
    foreign key (question_id) references question (id),
    foreign key (user_id) references user (id)
);


create table agree
(
    user_id   varchar(32) not null,
    answer_id varchar(32) not null,
    agree     bool        not null,
    primary key (user_id, answer_id),
    foreign key (user_id) references user (id),
    foreign key (answer_id) references answer (id)
);

create table topic
(
    id    varchar(32) primary key,
    topic varchar(32) not null,
    head  tinytext default null
);

create table ques_topic
(
    question_id varchar(32) not null,
    topic_id    varchar(32) not null,
    primary key (question_id, topic_id),
    foreign key (question_id) references question (id),
    foreign key (topic_id) references topic (id)
);

create table comment
(
    id        varchar(32) primary key,
    user_id   varchar(32) not null,
    answer_id varchar(32) not null,
    content   text        not null,
    refer     varchar(32) references comment (id),
    time      timestamp default now(),
    foreign key (user_id) references user (id),
    foreign key (answer_id) references answer (id)
);

# select * from answer where user_id in (select followed_id from follow where follow_id = 'id')

insert into user(id, name, password) VALUES ('bb105db1addc44ad83a60de84b0863cd', 'tobias', 'qwe45623')