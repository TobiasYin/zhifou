drop  table if exists tips;
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
    id          varchar(32) primary key,
    topic       tinytext not null,
    description text     default null,
    head        tinytext default null
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

# create fulltext index ft_idx on answer (content) with parser ngram;
# alter table answer
#     add fulltext content (content);

create table tips
(
    id               int auto_increment primary key,
    user_id          varchar(32) not null,
    other_user_id    varchar(32) not null,
    action_name      tinytext    not null,
    type             int         not null,
    question_id      varchar(32),
    answer_id        varchar(32),
    comment_id       varchar(32),
    other_comment_id varchar(32),
    is_read          boolean     not null default false,
    foreign key (user_id) references user (id),
    foreign key (other_user_id) references user (id),
    foreign key (question_id) references question (id),
    foreign key (answer_id) references answer (id),
    foreign key (comment_id) references comment (id),
    foreign key (other_comment_id) references comment (id)
);
# 类型: 1: 回答问题, 2:点赞或反对, 3:评论回答, 4:回复评论

delimiter //

drop trigger if exists comment_add_tip;
create trigger comment_add_tip
    after insert
    on comment
    for each row
begin
    declare uid varchar(32);
    select answer.user_id into uid from answer where answer.id = NEW.answer_id;
    insert into tips(user_id, other_user_id, action_name, type, answer_id, comment_id)
    values (uid, new.user_id, '评论了您的回答', 3, new.answer_id, new.id);
    if new.refer is not null then
        insert into tips(user_id, other_user_id, action_name, type, answer_id, comment_id)
        values (uid, new.user_id, '回复了您的评论', 4, new.answer_id, new.id);
    end if;
end;
//

drop trigger if exists answer_add_tip;
create trigger answer_add_tip
    after insert
    on answer
    for each row
begin
    declare uid varchar(32);
    select question.user_id into uid from question where id = NEW.question_id;
    insert into tips(user_id, other_user_id, action_name, type, answer_id, question_id)
    values (uid, new.user_id, '回答了您的问题', 1, new.id, new.question_id);
end;
//

drop trigger if exists agree_tip;
create trigger agree_tip
    after insert
    on agree
    for each row
begin
    declare uid varchar(32);
    select user_id into uid from answer where id = NEW.answer_id;
    if new.agree = 1 then
        insert into tips(user_id, other_user_id, action_name, type, answer_id)
        values (uid, new.user_id, '赞同了您的回答', 2, new.answer_id);
    else
        insert into tips(user_id, other_user_id, action_name, type, answer_id)
        values (uid, new.user_id, '反对了您的回答', 2, new.answer_id);
    end if;
end;
//

drop trigger if exists delete_answer;
create trigger delete_answer before delete on answer
    for each row
    begin
        delete from agree where answer_id = old.id;
        delete from tips where answer_id = OLD.id;
    end;
//
delimiter ;

