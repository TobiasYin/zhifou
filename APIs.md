# Java Web 大作业

# Fake Zhihu APIs



本次开发采用前后端分离的方式开发.

所有数据采用json格式沟通.

本文档将描述每个接口的url, url全部以域名的绝对路径表示, /表示网站根目录.

method描述接口接收的类型, 主要为post和get方法.

send下有json的属性以及类型(类型为json格式所允许的类型, 包括int, float, bool, str, list, dict, 其中dict类型应使用相同的方式描述其field), 作用的描述.

return表示返回的json的内容, 格式和send属性类似.

以及关于接口的简介, 保存在desc下.

如果接口需要登录才能获取, 讲有login: true的字样. 此属性的接口, 前端后端都应作出相应的检查, 检查发现调用这写接口但是没有登陆的, 需重定向到登陆界面.

所有返回数据的接口, 都必须在success状态等于true时, 才会有返回数据的属性.

示例:

```
url: /api/login
method: post
desc: 用于登陆的接口
send:
	username: str
	password: str
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
```



## 申请验证码

```
url: /api/get_captcha
desc: 注册, 修改密码等页面需申请验证码, 直接返回图片格式. 可直接把api放入img的scr属性中.
method: get
send:
	time: str #使用js生产当前时间的时间戳, 防止缓存验证码.
return_type: image/jpeg
```

## 注册

```
url: /api/register
method: post
desc: 注册接口, 需要前端验证两次输入的密码是否相等, 需申请验证码
send:
	username: str
	password: str
	captcha: str
return:
	success: bool #如果失败需重新申请验证码
	error: str #仅当success为false是, 有此属性, 值为错误原因
```

## 登陆

```
url: /api/login
method: post
desc: 用于登陆的接口
send:
	username: str
	password: str
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
```

## 修改密码


```
url: /api/change_password
method: post
desc: 修改密码
login: true #登录才能修改密码
send:
	username: str
	old_password: str
	new_password: str
	captcha: str  #修改密码前需获取验证码
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
```


## 判断是否登陆 (通过cookies)

```
url: /api/islogin
method: get
desc: 是否的登陆
return:
	islogin: bool
```



## 获取自己的信息

```
url: /api/self_info
desc: 获取自己的信息, 主要用于header的展示.
method: get
login: true #需要登陆才能获取.
return:
	success: bool #表示获取是否成功, 一般登录超时会失败
	error: str #仅当success为false是, 有此属性, 值为错误原因
	username: str #返回用户名, 此属性包括以下属性都是success为true时才有
	userid: str #用户id
	head_sculpture: str #头像的url, 可直接放入img的src中
	tip: int #提醒的数量
	message: int #私信的数量, 可能不会使用
```



## 获取主页问答

![1561873671665](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561873671665.png)

知乎的首页分为三栏, 推荐算法太麻烦了, 我们就做最新和热门两栏吧.

上面的上栏也改成首页, 话题和关注吧. 这样看起来多一点.

![1561891703715](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561891703715.png)

效果图大概如下.

### 最新

应为默认进入首屏

![1561874887158](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561874887158.png)

每一篇文章显示成这样的卡片, 建议抽离成一个vue组件. 分享功能直接复制链接实现. 取消感谢功能. 取消右边三个点. 如果前端判断发现author_id和当前登录的用户id相等, 需增加删除按钮, 删除的api见后文.

```
url: /api/newest
method: get
desc: 获取最新回答
send:
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```

### 最热

最热和最新具有基本相同的返回结果与请求方式, 不同的是, 最热返回的数据是赞同数和时间通过一定比例计算排序后的文章列表.
```
url: /api/hottest
method: get
desc: 获取最热回答
send:
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```



## 删除回答

```
url: /api/delete_answer
method: post
desc: 删除回答
login: true #除了必须登陆外, 还必须待删除的回答的作者与登录账号的作者相同.
send:
	answer_id: str #回答的id
return:
	success: bool #返回是否删除成果, 如果删除成功, 应把它从当前页面的渲染列表里删除
	error: str #仅当success为false是, 有此属性, 值为错误原因
```



## 获取头像

虽很多接口都返回用户头像url, 但为方便起见, 直接提供一个获取头像的api.

```
url: /api/get_head_sculpture
method: get
desc: 获取用户头像
send:
	id: str #需要获取头像的用户的id
return-type: image/jpeg
#直接返回图片, 故可使用/api/get_head_sculpture&id=1这样的形式直接放在img标签的scr内.
```

## 获取评论

在回答中,  按评论数量的按钮, 将会展示评论. 因为篇幅原因, 暂时不给评论提供点赞的功能.

```
url: /api/get_comment
method: post
desc: 获取指定回答的评论
send:
	answer_id: str #待获取评论的文章链接
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
	data: list #评论列表, 每一个元素为dict, 属性为:
		comment_id: str #评论的id
		comment_author_id: str #评论者id
		comment_author_name: str #评论者昵称
		comment_author_head_sculpture: str #评论者头像url
		content: str #评论的内容
		refer: str #如果是回复的其他评论, 则为被回复的评论的id, 否则, 为空值
		time: int #回答发送时的timeslot, 需要显示在评论中
```

## 发表评论
```
url: /api/send_comment
method: post
desc: 发表评论
login: true
send:
	answer_id: str #待获取评论的文章链接
	refer: str #如果回复其他评论, 则为被回复的评论的id, 否则, 为空值
	content: str #评论的内容
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
	info: dict #返回成功上传的评论的信息, 应把信息插入评论渲染列表中
		comment_id: str #评论的id
		comment_author_id: str #评论者id
		comment_author_name: str #评论者昵称
		comment_author_head_sculpture: str #评论者头像url
		comment: str #评论的内容
		refer: str #如果是回复的其他评论, 则为被回复的评论的id, 否则, 为空值
		time: int #回答发送时的timeslot, 需要显示在评论中
```




## 获取话题列表

主要用于话题页面请求话题, 返回值排序方法按话题下回答数计算. 请求的数量依然按照最新最热相同规则获取.

```
url: /api/get_topics
method: get
desc: 返回所有话题的列表
send:
	start: int
	end: int
return:
	success: bool #返回是否成功获取
	error: str #仅当success为false是, 有此属性, 值为错误原因
	hasMore: bool #是否还有更多
	data: list #data为dict组成的list, 每一个dict描述一个话题的信息, 具体属性如下:
		topic_name: str #话题的名字
		topic_id: str #话题的id
		description: str #话题的简介
		topic_pic: str #话题的头像url
```

![1561893957792](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561893957792.png)

大致效果图如上.

## 话题详情页

点进话题后, 进入话题详情页, 将显示类似主页的页面, 但问题都是有此话题标签的问题. 效果如下, 其中问题和回答依然可以复用主页的组件. 返回值排序方式和最热类似.

![1561894485502](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561894485502.png)



```
url: /api/topic
method: get
desc: 获取话题下的回答
send:
	topic_id: str #话题的id
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	topic_id: str #话题的id
	topic_name: str #话题的名字
	description: str #话题的简介
	topic_pic: str #话题的头像url
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```

## 关键词获取话题

主要用于搜索话题. 可以在发布问题添加话题时使用

```
url: /api/get_topic_by_name
method: get
desc: 返回含指定关键字的话题的列表
send:
	name: str #用户输入的话题关键字
return: #返回格式类似上一个api
	success: bool #返回是否成功获取
	error: str #仅当success为false是, 有此属性, 值为错误原因
	data: list #data为dict组成的list, 每一个dict描述一个话题的信息, 具体属性如下:
		topic_name: str #话题的名字
		topic_id: str #话题的id
		description: str #话题的简介
```

## 添加话题

此api为上传

```
url: /api/add_topic
method: post
ectype: multipart/form-data #使用此格式使上传话题能一并上传话题简介图
islogin: true
desc: 添加话题
send: 
	topic_name: str #话题名
	topic_desc: str #话题简介
	topic_pic: FormData() #此为特殊格式, 将用户头像作为form-data一起上传
return:
	topic_name: str #话题的名字
	topic_desc: str #话题简介
	topic_id: str #话题的id
	description: str #话题的简介
```





## 发布问题

发布问题使用的api, 在问题发布时, 可以添加话题, 采用关键字搜索(上一个api)并选择的方式添加, 最多添加三个话题.

```
url: /api/add_question
method: post
desc: 发布问题
login: true
send:
	question: str #提出的问题的标题.
	question_desc: str #问题的详细描述.
	topics: list #话题的列表, 每一个元素是话题的id,0<= length <= 3
return:
	success: bool #返回是否成功获取
	error: str #仅当success为false是, 有此属性, 值为错误原因
	question_id: str #返回的问题的id, 应该在发布成功后跳转到问题详情页
```



## 获取问题详情页信息

![1561893025593](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561893025593.png)

问题详情页每一个回答的组件可以复用主页的组件. 但应注意, 这里不会显示每一个回答的问题, 因为都是一样的.

```
url: /api/question
method: get
desc: 获取最热回答
send:
	question_id: str #问题id
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	question_id: str #问题id
	question_title: str #问题本体
	question_desc: str #问题详情
	topics: list #本问题的话题, 每一个元素为一个dict
		topic: str #话题的名字
		topic_id: str #话题的id
	time: int #问题创建的时间
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```



## 发表回答

```
url: /api/send_answer
method: post
desc: 发表回答
send:
	question_id: str #待获取评论的文章链接
	content: str #回答的内容
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
	info: dict #返回成功上传的评论的信息, 应把信息插入评论渲染列表中
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```



## 用户详情页

在任意地方点击用户头像或名字可进入用户详情页,  暂时不添加私信功能, 看时间是否充足在决定

![1561895343621](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561895343621.png)

知乎的用户详情页长这样, 我们缩减一下吧, 只保留提问和回答两栏, 其中回答一栏的接口和主页的类似. 

### 用户元数据

```
url: /api/user
method: get
desc: 获取用户的数据
send:
	user_id: str #用户id
return:
	success: bool
	error: str #仅当success为false是, 有此属性, 值为错误原因
	username: str #返回用户名, 此属性包括以下属性都是success为true时才有
	userid: str #用户id
	head_sculpture: str #头像的url, 可直接放入img的src中
	intro: str #用户的简介
	isFollow: bool #查看是否关注
```



### 用户回答

```
url: /api/user_answer
method: get
desc: 获取用户回答, 按时间排序
send:
	user_id: str #用户的id
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```

### 用户提问

![1561895860999](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561895860999.png)

此栏较为简单. 有几个关注取消.

```
url: /api/user_ques
method: get
desc: 获取用户提问, 按时间排序
send:
	user_id: str #用户的id
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_count: int #问题的回答数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```



## 关注

```
url: /api/follow
method: post
desc: 关注用户
send:
	user_id: str #用户的id
return:
	success: bool #如果成功注意更改页面显示状态
	error: str #exist when success = false
```



## 取消关注

```
url: /api/unfollow
method: post
desc: 取消关注用户
send:
	user_id: str #用户的id
return:
	success: bool #如果成功注意更改页面显示状态
	error: str #exist when success = false
```



## 主页关注页面

本页面的基本格式和最新最热类似, 区别是显示的回答都来自关注的人, 排序方式按最新发帖排序.

```
url: /api/follows
method: get
desc: 获取关注的人的回答
send:
	start: int
	end: int #获取最新最热的条目, 区间从0开始, 前闭后开,[start,end) 建议初始获取20条, 之后每次加载更多获取5条. 即初始start=0&end=20, 第二次start=20&end=25....
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```



## 关注者列表

类似下图的显示.

![1561896427315](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561896427315.png)

```
url: /api/get_followees
method: get
desc: 获取关注的人
send:
	user_id: str #用户id
return:
	success: bool
	error: str #exist when success = false
	data: list #list的每一个成员为一个用户的简介, 为一个dict:
		username: str #返回用户名, 此属性包括以下属性都是success为true时才有
        userid: str #用户id
        head_sculpture: str #头像的url, 可直接放入img的src中
        intro: str #用户的简介
        answer_count: int #回答数
        followers_count: int #粉丝数
        isFollow: bool #查看是否关注
```



## 粉丝列表

格式和关注者列表类似, api也类似

```
url: /api/get_followers
method: get
desc: 获取粉丝
send:
	user_id: str #用户id
return:
	success: bool
	error: str #exist when success = false
	data: list #list的每一个成员为一个用户的简介, 为一个dict:
		username: str #返回用户名, 此属性包括以下属性都是success为true时才有
        userid: str #用户id
        head_sculpture: str #头像的url, 可直接放入img的src中
        intro: str #用户的简介
        answer_count: int #回答数
        followers_count: int #粉丝数
        isFollow: bool #查看是否关注
```



## 设置个人简介

```
url: /api/set_self_intro
method: post
desc: 获取关注的人
login: true
send:
	intro: str #简介
return:
	success: bool
	error: str #exist when success = false
```

## 设置个人头像

```
url: /api/set_head
method: post
ectype: multipart/form-data #使用此格式使上传能上传头像
desc: 上传头像
login: true
send:
	data: FormData() #图片
return:
	success: bool
	error: str #exist when success = false
```

## 赞同或反对

```
url: /api/agree
method: post
desc: 赞同, 反对
login: true
send:
	answer_id: str #赞同或反对的文章id
	agree: int #赞同或反对的状态, 1代表赞同, 0代表取消, -1代表反对, 后端检查, 不允许其他取值.
return:
	success: bool
	error: str #exist when success = false
```



## 获取提醒列表

提醒主要是自己的问题被回答了, 以及自己的回答被评论了.

```
url: /api/get_tips
method: get
desc: 获取tips
login: true
return: 
	success: bool
	error: str #exist when success = false
	tips: list #每一个元素为一个tip
		other_user_id: str #发出评论, 赞同或者回答的人的id
		other_user_name: str #发出评论, 赞同或者回答的人的名字
		action: str #比如"赞同了您的回答", "回答了您的问题"等
		question_id: str #被操作的问题的id
		question_title: str #被操作的问题名
		is_read: bool #本tip是否点开过, 获取了这个之后, 下一次获取状态将变为false.
```



## 搜索

用于header上的搜索. 

![1561897732846](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561897732846.png)

保留综合和用户, 综合可搜索到回答和问题中的内容, 用户可以搜索用户名.

### 综合

返回类型类似主页渲染, 可复印组件, 关键词使用id="keywords"的span标签嵌套, 可增加其属性.

```
url: /api/search
method: get
desc: 搜索问题和答案
send: 
	text: str #搜索的句子
	start: int
	end: int
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool #返回是否还有更多, 如果有更多, 可显示加载更多的按钮
	data: list #list的每一个成员为一个回答的信息, 为一个dict, 每一个成员的信息描述如下:
		question_id: str #问题编号
		question_title: str #问题标题
		answer_id: str #回答编号
		answer_author: str #回答作者名
		answer_author_id: str #回答作者的id
		answer_content: str #回答的内容
		answer_author_head_sculpture: str #答主的头像的url, 展开阅读全文时需要显示
		is_agree: int #取值0代表没有选择, 1代表赞同, -1代表反对.
		agree_count: int #回答的赞同数
		comment_count: int #回答的评论数
		time: int #回答发送时的timeslot, 需要显示在阅读全文中显示
```

### 用户

![1561898138745](C:\Users\yrt19\AppData\Roaming\Typora\typora-user-images\1561898138745.png)

返回类似的页面,  课复用关注, 粉丝列表的组件

```
url: /api/search_user
method: get
desc: 搜索用户
send: 
	text: str #搜索的句子
	start: int
	end: int
return:
	success: bool
	error: str #exist when success = false
	hasMore: bool
	data: list #list的每一个成员为一个用户的简介, 为一个dict:
		username: str #返回用户名, 此属性包括以下属性都是success为true时才有
        userid: str #用户id
        head_sculpture: str #头像的url, 可直接放入img的src中
        intro: str #用户的简介
        answer_count: int #回答数
        followers_count: int #粉丝数
        isFollow: bool #查看是否关注
```



---

更新N处Bug, 修复了一些不完善的地方.

By Tobias. yrt1999@163.com

Update 2019.7.6