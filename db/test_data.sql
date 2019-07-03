# select * from answer where user_id in (select followed_id from follow where follow_id = 'id')

insert into user(id, name, password) VALUES ('bb105db1addc44ad83a60de84b0863cd', 'tobias', 'qwe45623');
select * from follow where follow_id = 'bb105db1addc44ad83a60de84b0863cd' and followed_id = 'f2d478e152b74b3ca9cc76093b3058b4';

insert into question(id, question, content, user_id) values ('bb105db1adc344ad84ds0de84b0863t4', '测试问题', '测试问题的内容', 'bb105db1addc44ad83a60de84b0863cd');

insert into answer(id, question_id, user_id, content) values ('e3d478e152b74b3ca9cc76093b3058b5', 'bb105db1adc344ad84ds0de84b0863t4', 'f2d478e152b74b3ca9cc76093b3058b4', '测试回答')

select id, user_id, question, content, time from question where id = 'bb105db1adc344ad84ds0de84b0863t4';

select * from question where id = 'bb105db1adc344ad84ds0de84b0863t4';