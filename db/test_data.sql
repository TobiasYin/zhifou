insert into user(id, name, password)
VALUES ('bb105db1addc44ad83a60de84b0863cd', 'tobias', 'qwe45623');
insert into user(id, name, password)
VALUES ('f2d478e152b74b3ca9cc76093b3058b4', 'Test_user1', 'qwe45623');
select *
from follow
where follow_id = 'bb105db1addc44ad83a60de84b0863cd'
  and followed_id = 'f2d478e152b74b3ca9cc76093b3058b4';

insert into question(id, question, content, user_id)
values ('bb105db1adc344ad84ds0de84b0863t4', '测试问题', '测试问题的内容', 'bb105db1addc44ad83a60de84b0863cd');

insert into answer(id, question_id, user_id, content)
values ('e3d478e152b74b3ca9cc76093b3058b5', 'bb105db1adc344ad84ds0de84b0863t4', 'f2d478e152b74b3ca9cc76093b3058b4',
        '测试回答');

insert into answer (id, question_id, user_id, content)
values ('rgdhg8e152b74b3ca9cc76093b3058b4', 'bb105db1adc344ad84ds0de84b0863t4', 'bb105db1addc44ad83a60de84b0863cd',
        '测试回答2');

select *
from answer;

select id, question_id, user_id, content, time
from answer
where content like '%17eg%';


select *
from answer;

show indexes from answer;

select id, question_id, user_id, content, time, agrees
from (select id, question_id, user_id, content, time
      from answer
      where user_id in (select followed_id from follow where follow_id = ?)) as A
         natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f
order by agrees - (now() - time) / 1800 desc
limit ?;

select *
from user;

desc comment;

desc topic;

alter table topic
    modify topic tinytext not null;
alter table topic
    modify description text default null;


select id,
       question_id,
       user_id,
       content,
       time,
       qusetion_title,
       question_content,
       q_user_id,
       q_time,
       agrees
from (select answer.id,
             answer.question_id,
             answer.user_id,
             answer.content,
             answer.time,
             q.question as qusetion_title,
             q.content  as question_content,
             q.user_id  as q_user_id,
             q.time     as q_time
      from answer
               join question q on answer.question_id = q.id
      where q.content like ?
         or answer.content like ?) as a
         natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f
order by agrees - (now() - time) / 1800 desc
limit ?;

select id,
       question_id,
       user_id,
       content,
       time,
       qusetion_title,
       question_content,
       q_user_id,
       q_time,
       agrees
from (select answer.id,
             answer.question_id,
             answer.user_id,
             answer.content,
             answer.time,
             q.question as qusetion_title,
             q.content  as question_content,
             q.user_id  as q_user_id,
             q.time     as q_time
      from answer
               join question q on answer.question_id = q.id
      where q.content like ?
         or answer.content like ?) as ans
         natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f
order by agrees - (now() - time) / 1800 desc
limit ?;

select *
from user
where name like '%bi%';

select *
from user;


select count(*)
from answer;

select *
from follow;

select *
from user
where user.name = 'tobias';


select id, question_id, user_id, content, time, agrees
from (select id, question_id, user_id, content, time
      from answer
      where user_id in (select followed_id from follow where follow_id = 'bb105db1addc44ad83a60de84b0863cd')) as A
         natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f
order by agrees - (now() - time) / 1800 desc
limit 20;

select *
from follow
where follow_id = 'bb105db1addc44ad83a60de84b0863cd';

insert into comment (id, user_id, answer_id, content)
values ('gx4a0cf6a16b47e89a2ecf696ccd157d', 'bb105db1addc44ad83a60de84b0863cd', '124a0cf6a16b47e89a21cf696cad157d',
        'This is a comment!');

select *
from answer
where id = '124a0cf6a16b47e89a21cf696cad157d';

desc comment;

select *
from comment
where answer_id = '124a0cf6a16b47e89a21cf696cad157d';
select id, user_id, answer_id, content, refer, time
from comment
where answer_id = '98422ac25d2c45cbb48fcac0228b25c3'
order by time desc;

select id, question_id, user_id, content, time
from answer
where question_id in (select question_id from ques_topic where topic_id = ?);


select *
from ques_topic
where topic_id = '529490f4d07e4526a4e0ae15640c8368';


select *
from user
where name = 'tobias';

select *
from tips
where user_id = 'bb105db1addc44ad83a60de84b0863cd';

select count(*)
from tips
where user_id = 'bb105db1addc44ad83a60de84b0863cd'
  and is_read = false;


select id, question_id, user_id, content, time, qusetion_title, agrees
from (select answer.id, answer.question_id, answer.user_id, answer.content, answer.time, q.question as qusetion_title
      from answer
               join question q on answer.question_id = q.id
      where q.content like '人生'
         or answer.content like '人生') as ans
         natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f
order by agrees - (now() - time) / 1800 desc
limit 10;

desc question;