

insert into user(id, name, password) VALUES ('bb105db1addc44ad83a60de84b0863cd', 'tobias', 'qwe45623');
insert into user(id, name, password) VALUES ('f2d478e152b74b3ca9cc76093b3058b4', 'Test_user1', 'qwe45623');
select * from follow where follow_id = 'bb105db1addc44ad83a60de84b0863cd' and followed_id = 'f2d478e152b74b3ca9cc76093b3058b4';

insert into question(id, question, content, user_id) values ('bb105db1adc344ad84ds0de84b0863t4', '测试问题', '测试问题的内容', 'bb105db1addc44ad83a60de84b0863cd');

insert into answer(id, question_id, user_id, content) values ('e3d478e152b74b3ca9cc76093b3058b5', 'bb105db1adc344ad84ds0de84b0863t4', 'f2d478e152b74b3ca9cc76093b3058b4', '测试回答');

insert into answer (id, question_id, user_id, content)
values ('rgdhg8e152b74b3ca9cc76093b3058b4', 'bb105db1adc344ad84ds0de84b0863t4', 'bb105db1addc44ad83a60de84b0863cd', '测试回答2');

select * from answer;

select id, question_id, user_id, content, time from answer where content like '%17eg%';


select * from answer;

show indexes from answer;

select id, question_id, user_id, content, time, agrees from (select id, question_id, user_id, content, time from answer where user_id in (select followed_id from follow where follow_id = ?)) as A natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f order by agrees - (now() - time)  / 1800 desc limit ?;

select * from user;

desc comment;

desc topic;

alter table topic modify  topic tinytext not null ;
alter table topic modify description text default null;