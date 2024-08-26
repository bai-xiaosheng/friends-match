# 项目介绍

**光点匹配**是一个旨在为用户提供高效、智能的伙伴匹配服务的创新平台。无论你是寻找兴趣相投的朋友，还是在寻找共同合作的团队伙伴，光点匹配都能帮助你找到最佳的匹配对象。通过标签搜索和账号搜索，用户可以轻松找到与自己志趣相投的人，并通过内置的聊天功能展开互动。

此外，光点匹配还提供了创建和加入队伍的功能，让用户能够组建和参与不同的团队，实现更加深入的合作与交流。为了提升用户体验，平台引入了先进的大模型技术，实现了智能聊天，让用户在与系统交互时，能够感受到更为自然流畅的对话体验。

**在线地址**: [https://www.xiaobaiai.com.cn](https://www.xiaobaiai.com.cn)

### 项目资料

- **[光点匹配项目笔记](https://www.yuque.com/g/baidongsheng-6pvqz/urmgza/obnx2ky4221g6lku/collaborator/join?token=SxHCOC8dpuWowmhg&source=doc_collaborator#)**  
  详细记录项目开发过程中的关键点与技术细节。

- **[项目部署上线笔记](https://www.yuque.com/g/baidongsheng-6pvqz/urmgza/ih74be991oa61uwy/collaborator/join?token=SodcBCXNkmFtYXXz&source=doc_collaborator#)**  
  步骤清晰地指导项目的部署与上线操作。

## 项目亮点

1. **支持标签匹配**：根据标签为用户推荐最适合的伙伴。
2. **引入大模型技术**：用户可以直接在本网页与我们的光点智能机器人进行智能对话，获取建议、信息查询。这是光点匹配区别于其他社交平台的重要特征。
3. **团队的创建和管理**：允许用户自由创建或加入不同的队伍。
4. **多样化社交功能**：平台支持用户私人聊天、队伍内部聊天、大厅聊天和智能聊天，提高用户体验。

## 项目难点

1. 匹配算法的设计与优化。
2. 智能聊天的实现与优化。
3. 高并发处理与系统扩展性的保障。

## 技术选型
### 前端：

- Vue 3
- Vant UI 组件库
- Vite 脚手架
- Axios 请求库
- Nginx单机部署
### 后端：

- Spring Boot 框架
- SpringMVC
- MySQL 数据库
- Mybatis +MyBatis-Plus
- MyBatis X 自动生成代码
- Redis 缓存
- Redis 分布式登录
- Redission 分布式锁
- WebSocket、 消息队列
- Spring Scheduler 定时任务
- Swagger + Knife4j 接口文档
- slf4j + logback +lombok 日志管理
- Gson：JSON 序列化库
- 相似度匹配算法

## 项目开发流程
需求分析=>设计（概要设计/详细设计）=>技术选型=>初始化/引入必要的技术=>写demo=>写代码（业务逻辑）=>测试（单元测试、整体测试）=>代码评审=>部署=>上线

## 需求：
### 用户功能

- 用户注册
- 用户登录
- 用户推荐
- 添加标签
- 标签搜索
- 账号搜索
- 用户信息修改
### 队伍功能

- 创建队伍
- 加入队伍
- 修改队伍信息
- 转让队伍
- 解散队伍
### 伙伴功能

- 添加好友
- 同意好友申请
- 好友申请通知
### 聊天功能

- 用户私聊
- 队伍聊天
- 大厅聊天
### 智能聊天

- 与大模型聊天
- 获取历史信息

# 用户功能
## 需求分析

1. 登录和注册功能
   1. 账号密码登录
   2. 手机号登录
   3. 微信登录
   4. github 登录
   5. qq 登录
2. 推荐
   1. 相似度计算算法+本地实时计算
3. 用户添加标签，
4. 搜索功能：
   1. 用户可以根据标签进行搜索
   2. 用户可以查询自己的标签
   3. 用户还可以用用户账号查询
5. 组队
   1. 用户可以自己创建一个队伍
      1. vip 可以创建多个队伍，普通用户只能创建 k 个
   1. 加入队伍
   2. 根据标签查询队伍
   3. 邀请其他人
1. 允许用户修改标签（包括删除）
2. 推荐
   1. 相似度计算算法+本地实时计算
3. 聊天功能
   1. 队伍聊天
   2. 私聊
   3. 大厅聊天

## 数据库设计
### 用户表
```sql
create table user
(
  id           bigint auto_increment comment '主键'
  primary key,
  userAccount  varchar(1024)                          null comment '账户',
  userName     varchar(1024)                          null comment '账户名称',
  userUrl      varchar(512)                           null comment '头像',
  gender       tinyint                                null comment '性别 0-女 1-男',
  userPassword varchar(512)                           null comment '密码',
  userStatus   tinyint      default 0                 null comment '状态 0-正常',
  createTime   timestamp    default CURRENT_TIMESTAMP null,
  updateTime   timestamp    default CURRENT_TIMESTAMP null,
  isDelete     int          default 0                 null comment '是否删除（逻辑删除） 0-正常 1-删除',
  userRole     int          default 0                 not null comment '用户权限 0-普通用户 1-管理员',
  vipState     varchar(512) default '0'               null comment '会员 0-普通用户 1-vip 2-svip',
  tags         varchar(1024)                          null comment '标签 json 列表',
  profile      varchar(512)                           null comment '个人简介',
  phone        varchar(128)                           null comment '电话',
  email        varchar(512)                           null comment '邮箱',
  lastTime     timestamp    default CURRENT_TIMESTAMP null comment '用户最后登录时间',
  friendsIds   varchar(512)                           null comment '添加的好友'
)
    comment '用户';
```
### 标签表（分类表）
标签分类
方向：后端、前端、算法、大数据
正在学：SpringBoot、mySQL、mybatis、redis 缓存、多线程、jvm
目标：考研、春招、秋招
段位：初级、中级、高级、升天
身份：大一、大二、大三、大四、研一、研二、研三、待业、以就业
状态：非常好、很好、还会更好
还支持用户自定义标签
### 标签表设计：
| id | bigint | 主键 |
| --- | --- | --- |
| 标签名 | varchar(1024) | **唯一索引**，提高查询效率 |
| 上传用户 id  userId | int0 | 普通索引 |
| 父 id parentId | bigint | 分类 |
| 是否为父 id isParentId | tinyint | 0 - 不是 1 - 父 id |
| 创建时间 createTime | datatime | 创建时间 |
| 修改时间 updateTime | datatime | 修改时间 |
| 逻辑删除 isDelete | tingint | 0 - 正常 1 - 删除 |

### 验证设计
主要是**思考该设计能否满足业务操作诉求：**

1. 怎么查询所有标签，并把标签分组
- 分组可以用父标签进行分组
2. 如何根据父标签查询子标签
- 根据 id 查询
### 用户表补充标签
问题：怎么给用户表补充标签？
**一切都根据实际情况来**
常见方案有两种：

1. 直接在用户表中添加标签字段，用一个 json 字符串，比如:['java','男']

优点：查询速度块，不用新建关联表，标签是用户的固有属性（其它系统也可能用到），节省开发成本
缺点：根据标签查询用户时只能用模糊查询，或者遍历用户列表，性能不高。如果数据小于 20w，还是推荐用这个。

2. 新建一个关联表，关联用户 id 和标签的关系

优点：查询灵活、可以正查反查
缺点：需要多建一个表，多维护一个表，在大型项目开发中，尽量减少关联表，建议不超过三个，否则很影响扩展性和查询性能。

本项目**采用方案 1，使用缓存来提高查询效率**

## 用户注册功能
**设计**：

1. 账号不能重复
2. 账号长度不能小于四位？
3. 账号中不能包含特殊字符
4. 密码长度不能小于 8 位，同时数据库中要加密
5. 注册时两次密码要保持一致
6. 密码加密（**md5 加密**）
## 用户登录功能
设计：

1. 判断输入参数是否为空
2. 利用**布隆过滤器**解决缓存穿透问题
3. 判断用户账号和密码是否正确

## 用户匹配设计
需求：根据登录用户的标签，匹配与其标签最相似 Top N 个用户
方案对比：[Java实现计算两个字符串的相似度：杰卡德、编辑距离、余弦相似度......_simmetrics-CSDN博客](https://blog.csdn.net/qq_58202163/article/details/132109983)

1. 杰卡德：利用字符串的交集和并集计算相似度。优点：简单；缺点：对不同位置的相同字符不敏感
2. 余弦相似度：基于两个字符串的向量空间模型来计算相似度。优点：对不同位置的相同字符敏感，可以实习加权计算相似度；缺点：需要将字符串转换为向量表示，比较复杂
3. 编辑距离：基于两个字符串之间的最少编辑操作次数来计算相似度。优点：对不同位置的相同字符敏感，但计算复杂度较高。 √

业务逻辑：

1. 检查用户是否登录
2. 获取用户标签，使用 gson 将 json 格式数据转换为字符串列表
3. 根据标签查询相似度高的用户
   1. 获取所有用户的信息（id 和标签即可，太多了影响查询效率）
   2. 匹配所有用户的标签与当前用户标签的相似度，这里需要排除与自己的相似度计算
   3. 用一个键值对<User,Interger>保存用户信息与相似度得分，再用一个 list 保存所有的用户，为了方便后续排序等处理
   4. 排序，按照 Interger 进行排序，得到排序后的用户 id
   5. 对最相似的几个用户查询个人信息（之前只查询了 id 和标签），这里**注意查询后返回的结果的按照 id 顺序返回的，没有按照排序后的顺序返回**。这是因为使用queryWrapper.in()方法，所以是在数据库中从第一个数据开始查询
   6. 按照排序后的用户 id 重新排序 e 查询到的个人信息。
   7. **Top N 问题**，这里可以使用**最小堆**实现

问题(todo)：

1. 是否需要计算数据库中所有用户相似度
2. 是否需要实时计算用户的相似度
## 按标签搜索用户功能
### 设计：

1. 允许用户输入多个标签，多个标签都存在时才搜索出来： like "%java%" and "%python%"
2. 允许用户输入多个标签，只要满足其中一个标签就可以被返回： like "%java%" or "%python%"
### 技术选型：

1. sql 语句实现，使用带条件的 sql 查询语句：**实现简单，可以通过拆分查询进一步优化**。标签数少的时候查询可能比内存查询快一点。
2. 内存查询：**灵活性更高**，可以通过**并发**进一步优化。标签数大的时候查询速度可能比 sql 查询快一点，在本项目中，数据量和标签量都不大，所以差不多。
### 方案选择

- 如果参数可以分析，根据用户的参数选择查询方式，比如标签数
- 如果参数不可分析，并且数据库连接足够、内存空间足够，可以并发同时查询，谁先返回用谁
- 还可以 sql 查询和内存计算相结合，比如先用 sql 过滤部分 tags

通过实际测试来分析哪种查询更快，一般在数据量比较大的情况下测试。
### 关键点
#### json 字符串存储：
在 mysql5.7 以前，使用字符串存储 json，现在 mysql 支持 json 格式，本项目采用的是将数据库中 tags 定义为字符串，数据按照 json 数组形式存储：["java","python"]
#### sql 查询：
首先判断输入标签是否为空，之后将查询条件用 QueryWrapper.like().like()形式传递给 sql。之后将用户信息脱敏返回
```java
        if (CollectionUtils.isEmpty(tagsNameList)){
            throw new BusinessException(Code.PARAMS_ERROR);
        }
//        //1.sql查询   like "%java%"
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName:tagsNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        //用户信息脱敏
        return  userList.stream().map(this::getSafeUser).collect(Collectors.toList());
```
#### 内存查询：
首先判断输入标签是否为空，之后将所有的数据读取出来，也就是查询条件为空，之后在内存中对所有数据进行条件查询，脱敏
```java
        //内存查询
        //1.先将所有数据放到内存中
       QueryWrapper<User> queryWrapper = new QueryWrapper<>();
       List<User> userList = userMapper.selectList(queryWrapper);
       Gson gson = new Gson();
        //2.判断
return  userList.stream().filter(user -> {
           String tags = user.getTags();
           if (StringUtils.isBlank(tags)) {
               return false;
           }
           Set<String> tempTagName = gson.fromJson(tags, new TypeToken<Set<String>>() {
           }.getType());
           for (String tagName : tagsNameList) {
               if (!tempTagName.contains(tagName)) {
                   return false;
               }
           }
           return true;
       }).map(this::getSafeUser).collect(Collectors.toList());
```
filter 是一个过滤器，自动删选符合条件的值。
#### json 序列化和反序列化
对于json 字符串
序列化：把 java 对象转换为 json 
```java
implements Serializable
```
反序列化：把 json 转换为 java 对象
java json 序列化库：

1. **gson**（谷歌出品，推荐，从官网查询使用方法）
2. fastjson （阿里出品，快，但是漏洞多）
3. jackson
4. kryo（性能极高的序列化库）

- 导入 gson 包
```java
// 获取用户标签，使用 gson 将 json 格式数据转换为字符串列表
Gson gson = new Gson();

List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
```
#### 测试时注意添加@SpringBootTest
避免自动引入时类型为空
```java
@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;
```
## 
## 用户修改信息
方案设计：

1. 使用 post 请求
2. 首先判断用户是否登录
3. 判断输入参数是否为空，以及 id 是否为空，因为根据 id 进行修改
4. 判断当前登录用户权限，只允许管理员修改全部用户信息，普通用户只能修改个人信息
5. 用 userMapper 修改个人信息

关键点：

1. 在接口层也可以进行用户登良、输入参数和用户权限判别，双重保险。
2. 对于用户登录、权限检查这样通用的方法放在业务层中，方便其它方法调用。

# 队伍功能开发
## 需求分析：

- 用户可以创建队伍、队伍名称、最大人数、描述、持续时间、队伍状态（公开、私有）
- 用户加入队伍，通过搜索队伍名称加入（判断队伍人数是否达到上限）、邀请用户
- 队伍解散
- 用户退出，如果是队长退出，队长位置传递给剩下加入最早的用户
- 转让队伍，需要判断对方是否在队伍中
- 队伍信息修改
- 队伍内聊天
## 数据库表设计：
### team 队伍表：
id	主键
userId	队长（创建人）
teamName	队伍名称
maxNum		最大人数
discription	队伍描述
expireTime	过期时间
status	0-公开 1-私有 2-加密
password		密码
创建时间
更新时间
是否删除
```sql
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除',
    teamUrl     varchar(512)                       null comment '队伍头像'
)
    comment '队伍';
```
### 用户-队伍如何关联：

- 用户加入了哪些队伍
- 队伍中有哪些用户
### 技术选型：

- 建立用户-队伍关系表 userId teamId 关系表（便于修改）   **√** 本项目采用这个** **
- 在用户表和队伍表中添加字段（便于查询，不用写多对多的代码，可以直接根据用户查队伍、根据队伍查用户）
### user_team 用户-队伍关系表
id	主键
userId	用户 id
teamId	队伍 id
joinTime	加入队伍时间
创建时间
更新时间
是否删除
```sql
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';
```
## 创建队伍设计
创建队伍表需要进行校验，以确保创建的队伍是否符合上面要求。用户可以创建队伍、队伍名称、最大人数、描述、持续时间、队伍状态（公开、私有）
校验条件：

   1. 创建队伍表时，需要确保用户已登录
   2. 队伍名称必填，小于 20 个字符串
   3. 最大人数要设置，必须大于 1，小于 20.
   4. 描述不能超过 512 个字符
   5. 持续时间要在当前时间之后
   6. 队伍状态（int）必须大于 0，或者用枚举判断
   7. 如果设置为加密，加密密码不能超过 10 位
   8. 用户最多创建 5 个队伍

其它注意事项：

1. 写入表时，需要同时写入队伍表和用户-队伍表（事物）
```java
    @Transactional(rollbackFor = Exception.class)
```

2. 前端只需要传递上面所需要的信息，不需要整个 team 表中的字段

## 展示队伍设计
需求：能够根据用户输入的 id，队伍名称，最大人数、描述、过期时间、队伍状态找到对应的队伍
逻辑：

1. 用户 id
2. 队伍名称 模糊匹配
3. 最大人数 相等
4. 描述 模糊匹配
5. **关键字 **模糊匹配队伍名称和描述
6. 过期时间 只要没过期都可以访问
7. 队伍状态 管理员和队长可以找到非公开和加密队伍，普通用户只能找到公开队伍

## 队伍信息更新设计
需求：队长和管理员可以修改队伍信息（队伍名称、描述、最大人数、过期时间、状态）
实现：

1. 获取用户登录信息，判断用户是否为队长和管理员
2. 检查修改信息是否为空
3. 依次检查需要修改的信息
4. 如果修改的信息与之前信息相同，不修改。

注意：

1. 如果将状态改为加密，必须要设置密码
2. 信息校验，检验修改信息是否满足设置的条件
3. 判断输入字符串是否为空时，**使用 stringutils 函数，不能使用 string == null** 作为判别语句。

isBlank()判断制表符（\t）和空格时，为true；
isEmpty()判断制表符（\t）和空格时，为false；
两个判断null、""、以及换行符（\n或者\r）时为true

4. 使用 copy 复制时，会将 teamUpdateRequest 里面的字段值赋给newTeam 里面的相同字段
```java
BeanUtils.copyProperties(teamUpdateRequest,newTeam);
```

5. 使用 updateById 方法，会自动更新修改时间，newTeam 里面的 null 不会对原数据修改
```java
teamService.updateById(newTeam)
```

## 用户加入队伍设计
需求：用户可以加入非私有、未过期、人数未满的队伍，但用户能够加入的队伍不能超过 10 个
实现：

1. 判断队伍状态，如果是私有，不能加入，加密房间需要输入密码
2. 判断过期时间
3. 判断队伍中人数是否达到上限
4. 查询用户加入了多少个队伍，并且不能加入重复的队伍
5. 加入队伍，将数据写入 userteam 表

注意：

1. 尽量将数据库查询的语句放在后面，减少数据库查询次数
## 用户退出队伍
需求：如果是普通用户，直接退出即可，如果是队长，需要将队长传给下一个最早加入的用户。
逻辑：

1. 校验请求参数
2. 判断队伍是否存在
3. 判断自己是否加入队伍
4. 判断队伍情况
   1. 如果队伍只剩下一人，解散队伍
   2. 还有其他人
      1. 如果自己是队长，将队长传递给第二早加入队伍的成员
      2. 如果不是队长，直接退出

注意：

1. 删除用户并不是在数据库中真的删除，而是将 **isdelete 字段设置为 1**，需要在定义的字段上添加@TableLogic 注解。当重新添加时，会创建一条新的数据。
2. 查询最早加入的两个成员
```java
userTeamQueryWrapper.last("order by id limit 2");
```
## 队伍解散设计
需求：队长和管理员可以解散队伍
业务流程：

1. 检验参数
2. 判断队伍是否存在
3. 检查是否为队长或者管理员
4. 删除用户队伍中所有成员的信息
5. 删除队伍表中的信息
## 获取当前用户已经加入的队伍
业务逻辑：

1. 用户是否登录
2. 检查加入的队伍是否过期
3. 关联队伍里面的用户信息


## 需求分析：
聊天功能需求：发送文字、语音、图片、消息缓存、消息存储、消息已读、未读、单聊、群聊。
发送消息：

- 给队伍内所有用户群发信息
- 队伍聊天
- 大厅聊天
- 私人聊天
- 保存聊天
- 发送信息失败
- 广播信息
- 单点信息
- 发送所有在线用户信息

获取信息：

- 获取私聊聊天信息
- 获取队伍聊天信息
- 获取大厅聊天信息
## 方案对比

1. 利用 http 接口手动实现消息发送、消息接受、获取历史消息操作
   1. 优点：后端实现简单，可以将数据永久保存到数据库中
   2. 缺点：频繁调用接口，服务器和 api 接口的压力较大，高并发情况下可能会宕机。另一方面，不发送消息时，定时器固定查询，会返回空值。
2. 利用已有的 WebSocket 服务端实现聊天功能
   1. 优点：不用自己实现接口，直接套用 webocket 即可
   2. 缺点：不能持久化，如果服务器宕机，无法读取历史消息。
3. 使用 socket.io，基于 Webocket 协议的一套成熟的解决方案
   1. 优点：性能号，支持多个平台
   2. 缺点：传输的数据并不完全遵循 websocket 协议，所以要求客户端和服务端都必须使用 socket.io 的解决方案
## 设计聊天表
需求：能够保存发送方用户 id，用户姓名，接收方 id， 发送的时间，是否已读，私聊还是群聊，队伍 id
字段设计：

| id  | bigint  | 主键 |
| --- | --- | --- |
| sendUserId | bigint | 发送方用户 id |
| recUserId  | bigint    | 接收方用户 id |
| sendUserName | varchar(512) | 发送方用户名称 |
| content | txt(1024) | 发送内容 |
| isRead | tinyint | 0 - 未读 1 - 已读 |
| chatType | tinyint | 1 - 私聊 2- 群聊 |
| roomId | bigint    | 队伍 id |
| sendTime | Data | 消息创建时间 |
| isDelete | tinyint | 逻辑删除 |


```sql
create table chat
(
  id           bigint auto_increment comment '聊天记录id'
  primary key,
  sendUserId   bigint                                   not null comment '发送消息id',
  recUserId    bigint                                   null comment '接收消息id',
  content      varchar(1024) collate utf8mb4_unicode_ci null comment '聊天内容',
  chatType     tinyint                                  not null comment '聊天类型 1-私聊 2-群聊 3-大厅聊天 4-智能聊天机器人',
  isRead       tinyint  default 0                       null comment '是否已读 1-已读 0-未读',
  sendTime     datetime default CURRENT_TIMESTAMP       null comment '创建时间',
  deleteTime   datetime                                 null comment '删除时间',
  teamId       bigint                                   null comment '要发送信息的队伍id',
  isDelete     tinyint  default 0                       null comment '逻辑删除 0-正常 1-删除',
  sendUserName varchar(1024)                            null
)
    comment '聊天消息表' row_format = COMPACT;
```
## WebSocket 实现
使用 Webocket 实现单聊、群聊聊天功能

1. 导入依赖
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

2. 声明 Socket 配置类
```java
@Configuration

public class WebSocketConfig {


    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

```java
@Component
public class HttpSessionConfig extends ServerEndpointConfig.Configurator implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        //获取HttpSession，将所有request请求都携带上HttpSession
        HttpSession session = ((HttpServletRequest) sre.getServletRequest()).getSession();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取session
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            // session放入serverEndpointConfig
            sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
        super.modifyHandshake(sec, request, response);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent arg0) {
    }
}
```

3. 声明聊天 WebSocket
- 使用 **ConcurrentHasMa**p<String, WebSocket>保存用户连接信息，ConcurrentHasMap 支持**在保证线程安全的情况下，高并发查询**。
**ConcurrentHashMap、hashMap 和 hashTable 三者的关系。**`ConcurrentHashMap`、`HashMap` 和 `Hashtable` 都是 Java 中用于存储键值对的映射集合类，但它们的设计目标和使用场景有所不同。以下是三者之间的关系和主要区别：
### 1. **HashMap**

- **概述**：`HashMap` 是一个非线程安全的实现。它允许 `null` 键和 `null` 值，存储的顺序不保证稳定。
- **线程安全性**：`HashMap` 不是线程安全的，多个线程同时访问时可能会出现数据不一致的情况。
- **性能**：由于没有额外的同步开销，`HashMap` 的性能通常比线程安全的映射类更高，在单线程或无需考虑并发的场景中最为适用。
### 2. **Hashtable**

- **概述**：`Hashtable` 是一种线程安全的哈希表实现，类似于 `HashMap`，但不同之处在于它是同步的。
- **线程安全性**：`Hashtable` 是线程安全的，每个方法都被 `synchronized` 修饰，保证同一时间只有一个线程可以访问它的操作。
- **性能**：由于所有方法都使用了同步锁，所以 `Hashtable` 的性能较 `HashMap` 更低。在高并发环境下，性能尤其会受到影响。
- **其他**：`Hashtable` 不允许 `null` 键和 `null` 值。如果尝试插入 `null`，会抛出 `NullPointerException`。
### 3. **ConcurrentHashMap**

- **概述**：`ConcurrentHashMap` 是为了在高并发环境下提供高效的线程安全性而设计的。
- **线程安全性**：`ConcurrentHashMap` 是线程安全的，但它的线程安全实现方式不同于 `Hashtable`。`ConcurrentHashMap` 通过分段锁（segment lock）或更细粒度的锁（在 Java 8 中引入）来实现部分锁定，从而允许多个线程并发地读写不同的部分。
- **性能**：`ConcurrentHashMap` 相对于 `Hashtable` 提供了更好的并发性能，因为它不会对整个对象加锁，而是使用更细粒度的锁来提高并发访问的效率。
- **其他**：与 `HashMap` 类似，`ConcurrentHashMap` 不允许 `null` 键或 `null` 值。
### **总结与关系**

- `HashMap` 是非线程安全的，一般用于单线程环境。
- `Hashtable` 是线程安全的，但性能较低，通常已被 `ConcurrentHashMap` 替代。
- `ConcurrentHashMap` 是为高并发环境设计的，性能优于 `Hashtable`，是并发编程中的首选。

因此，在多线程环境下，如果需要使用线程安全的映射结构，推荐使用 `ConcurrentHashMap`。在单线程或无需考虑并发的情况下，`HashMap` 则是更高效的选择。

- @OnMessage 接受消息时调用的方法，输入参数只接受 文本、二进制、或 ping 消息
   - 使用心跳包保持长连接不被关闭
   - 将信息保存到数据库中
   - 发送消息分为私聊和群聊，私聊根据对方 id 直接发放，如果不在线，数据库中标识未读
   - 群聊根据队伍 id 查找队伍内的用户 id，然后依次发送消息（排除自身）
- @OnOpen
   - 将连接保存到连接 Map 中，Map<String,Map<String,chatc>>第一个 String 表示队伍/房间/群号，除了用户加入的队伍外，还包含一个 “0” 队伍，用来保存所有的连接。第二个 map 存储队伍的连接，string 表示队伍内的用户 id。
   - 加入的队伍根据用户队伍表查询，依次将当前连接存储到 Map 中
- @onClose：
- 发送消息的方法
4. 声明 MyMessage 实体类
   1. 前面定义的数据库表


# 聊天功能
## 获取聊天信息功能
### 私聊：

- 首先判断请求信息是否为空
- 然后去缓存中查询是否存在对话
- 如果存在，直接返回，并且更新缓存（同时设置随机的过期时间解决缓存雪崩问题）
- 如果不存在，去数据库中查寻，注意查询双方的信息。
```java
// 从数据库中查询，注意分别查询当前用户为发送方，request.getId()为发送方两种情况
QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("sendUserId", loginUser.getId());
queryWrapper.eq("recUserId", chatRequest.getToId());
queryWrapper.or(chatQueryWrapper -> chatQueryWrapper.eq("sendUserId", chatRequest.getToId()).eq("recUserId", loginUser.getId()));
List<Chat> recChats = chatMapper.selectList(queryWrapper);
```

   - 使用 or 保证信息的顺序。或者是增加一个按照时间排序的条件
- 之后将查询到的聊天信息转换为 messageo 格式
- 保存到缓存中，返回结果
- todo：可以设置查询数据库信息的范围，比如首次只返回两天之内的信息，如果有需要在查询历史信息
### 队伍聊天：

   1. 判断用户是否登录
   2. 判断队伍 id 是否存在
   3. 缓存中查询，如果存在，则直接返回
   4. 如果缓存中不存在，去数据库中查询
   5. 将结果保存到缓存中，并返回
### 大厅聊天：

   6. 判断用户是否登录
   7. 判断聊天类型
   8. 缓存中查询，如果存在，则直接返回
   9. 如果缓存中不存在，去数据库中查询，
   10. 将结果保存到缓存中，并返回
## 测试
WebSocket 连接方法：
```powershell
ws://localhost:8080/api/websocket/1/0
# ws://表示未加密   wss://表示加密（HTPPS连接）
# 注意一定要加 api 自己在配置文件设置的地址
# websocket是自己在controller设置的地址
# /userId
# /teamId 队伍id
```
## 遇到的 Bug
###  在测试类启动时报错，（javax.websocket.server.ServerContainer not available）
![image.png](https://cdn.nlark.com/yuque/0/2024/png/12719766/1723015762675-fab8ea20-b341-4220-a0cc-025e7449ddb7.png#averageHue=%2323262c&clientId=udde536a9-eb2c-4&from=paste&height=117&id=zLFAS&originHeight=117&originWidth=1936&originalType=binary&ratio=1&rotation=0&showTitle=false&size=59662&status=done&style=none&taskId=u33606380-a2d4-4750-9f3b-ab51f0ae285&title=&width=1936)
原因：**SpringBootTest在启动的时候不会启动服务器，所以WebSocket自然会报错**
解决办法：添加选项webEnvironment，以便提供一个测试的web环境
```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```
### WebSocket 使用@ServerEndpoint 后在使用@Resource 注解会使得自动注入失效
![image.png](https://cdn.nlark.com/yuque/0/2024/png/12719766/1723017805628-5388b31c-d692-463e-a1a4-04fd0314b898.png#averageHue=%231f2125&clientId=u2a1405d5-e975-4&from=paste&height=218&id=itb7G&originHeight=218&originWidth=910&originalType=binary&ratio=1&rotation=0&showTitle=false&size=32846&status=done&style=none&taskId=uee4e10fc-f9fc-42ea-94c9-9d21f4b4acd&title=&width=910)
原因分析：**sping 容器管理的是单例的**，只会注入一次，而 **WebSockeet 是多对象**，当有新用户连接时，就会创建一个新的 websocket 对象，这就导致用户创建的 WebSocket 对象都不能注入对象了，所以才会出现注入对象为 null 的情况。
解决办法：在 config 中或者在 controller 类导入对象（@Resource）

1. 将@ServerEndpoint 中需要导入的对象转换为静态对象。
```java
@ServerEndpoint(value = "/websocket/{userId}/{teamId}", configurator = HttpSessionConfig.class)
@Component
@Slf4j
public class ChatWebSocketController {

    public static UserTeamService userTeamService;

    public static UserMapper userMapper;
```

2. 然后在 websocket 配置类中注入对象：这里可以使用@Resource 自动注入，然后将值赋给上一步创建的静态对象。
```java
@Resource
public void setUserTeamService(UserTeamService userTeamService){
ChatWebSocketController.userTeamService = userTeamService;
}

@Resource
public void setUserMapper(UserMapper userMapper){
ChatWebSocketController.userMapper = userMapper;
}
```
### 在用户重新上线时，推送历史未读消息报错
报错代码：
The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method
报错原因：消息发送太快，有两个或多个线程同时用 session 发送消息，导致同一时刻发生了线程冲突
解决方法：

1. 使用**锁+同步**线程，异步线程不可以，但如果数据量大的情况下会堵塞进程
```
    public synchronized void sendMessage(String content){
        // this.session.getAsyncRemote() 获取当前异步消息发送的实例
//        this.session.getAsyncRemote().sendText(content);
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(content);
            } catch (IOException e) {
                log.error("sendMessage error",e);
            }
        }
        // this.session.getBasicRemote() 获取当前同步消息发送的实例
    }
```

### 出现说无法写入 JSON 
![image.png](https://cdn.nlark.com/yuque/0/2024/png/12719766/1723446622842-f34ddd92-38d4-408d-985f-272aea04f568.png#averageHue=%231f2125&clientId=ud2b47b35-15bd-4&from=paste&height=339&id=jEqvB&originHeight=339&originWidth=2366&originalType=binary&ratio=1&rotation=0&showTitle=false&size=125846&status=done&style=none&taskId=u4eac3069-93e7-4a81-a593-e3a956dbb45&title=&width=2366)
原因分析：在定义的 WebSocket 类上没有使用 @Data 注解，而且还没有设置 get 和 set 方法，导致在转换 JSON 格式时无法使用 get 方法
解决办法：添加@Data 注解或者是自己设计 get 和 set 方法。
### 数据库成功连接，但在等待数据库服务器响应的时候出现超时或阻塞导致无法接受回复包
![image.png](https://cdn.nlark.com/yuque/0/2024/png/12719766/1723453264875-ed85cec8-26a0-4062-9381-bfa08818bb4e.png#averageHue=%2324282d&clientId=u047e9b8e-cdc6-4&from=paste&height=125&id=rcpXS&originHeight=162&originWidth=1274&originalType=binary&ratio=1.2999999523162842&rotation=0&showTitle=false&size=60662&status=done&style=none&taskId=u1a863ccb-34a4-4c93-808e-c71ce0d3b42&title=&width=980.000035946187)
原因分析：因为之前都可以正常启动，所以数据库连接等都没有问题，后来排查是因为在 service 上打了断点，导致无法接受回复包
## 其他

1. 如何将 String 转换为一个自定义的类。
```
T t = gson.fromJson(string,T.class);
```

2. 如何将一个自定义的类转换为 String
- 在自定义的类中添加 toString 方法
# WebSocket
定义：一种在**单个 TCP 连接上进行全双工通信**的**协议**
特点：

- **服务器可以主动发送消息**
- **长连接**
- **只需要一次握手**

相关技术：

- Ajax：通过固定时间向服务器发送请求来获得信息

注解含义：

- @onOpen 连接成功时调用的方法
-  @onMessage：受到客户端消息时调用的方法
   - 输入参数只接受 文本、二进制、或 pong 消息，所以没办法直接用 json 格式直接传
- @onError：连接出错时调用的方法
- @onClose：连接关闭时调用的方法
- 发送消息，用 websocket 创建的实例 .sent()

# 朋友功能
## 需求分析

- 用户能够向其它用户发送好友申请。
- 对方同意后两者成为好友
- 好友之间可以私聊
## 数据库设计
| id  | bigint  | 主键 |
| --- | --- | --- |
| fromId | bigint | 发送申请的用户 id |
| receiveId  | bigint    | 接收申请的用户 id |
| remark | varchar214) | 好友申请备注信息 |
| isRead | tinyint | 默认 0，0 - 未读 1 - 已读 |
| status | tinyint | 默认 0，0 - 未通过 1 - 已通过 |
| sendTime | Data | 消息创建时间 |
| updateTime | Data | 消息更新时间 |
| isDelete | tinyint | 逻辑删除 |

## 好友申请

1. 首先判断请求参数是否为空
2. 判断对方 id 是否存在
3. 判断备注信息是否超出长度限制
4. 判断要添加的用户是否为自己
5. 不能重复提交申请 
## 其他需求实现
### 查询用户收到的所有好友申请信息
### 用户未读的好友申请个数
### 用户发送的所有好友申请信息
### 同意好友申请

- 在 friends 表中更新状态
- 在 user 表中更改发送方和接受方好友 id
### 撤销好友申请
## 遇到的 bug
### 访问 friend/add 地址后，执行相关方法，但在返回时会报错，显示 post:friend/friends/add 不存在
原因分析：在 FriendsController 文件内使用的@Controller（返回视图），而不是@RestController（返回数据，json 或者 xml 格式）
解决办法：使用@RestController 或者是@Controller+@Restul。
@Controller 和@RestController区别：

1. 返回值不同：@Controller 返回一个视图，直接是一个 html 页面。@RestController 返回的是数据，在通过 javacript 渲染成页面。
### 进入 friends 页面后，疯狂刷新，后端显示 string 错误
原因分析：在 user 表中，friendsId 是 string 类型的 json 数组，中间用了 中文的逗号，导致出现 string 类型错误。
### 同意好友申请时显示服务器异常，经调试，并没有执行对应的方法
原因分析：前端访问的接口地址与后端设置的接口地址有误（单词拼写不一致）
### 添加好友后在好友界面不显示刚添加的好友
原因分析：

1. 排查数据库，数据库中 friends 表和 user 表中的 friendsId 都已经正常更新
2. debug 调用方法，发现 user.getFriendsId 得到的仍然是之前的值，怀疑是数据库哪里使用了缓存，导致并没有真正的查询数据库。
3. 后来发现是因为登陆用户信息是在前端的请求中获取的，由于前端请求信息还没有过期，所以仍然使用的以前的信息。

解决办法：

- 在查询好友时，重新获取一个当前登陆用户信息
### 用户进入聊天界面时 WebSocket 没有连接
现象描述：当用户进入聊天页面时，并没有初始化一个 WebScoket 连接，导致用户无法发送消息。而另一个用户可以正常连接
原因分析：

1. 由于在谷歌浏览器可以正常使用，但在微软浏览器中无法使用，所以后端代码应该没有问题，查找微软浏览器的问题
2. 编写 websocket 测试前端代码，发现 edge 还是无法连接，后来在网上看到插件会影响 websocket，删掉了油猴等插件。发现测试可以通过，但是原来的代码无法初始化。
3. 由于可以正常发送消息，因此应该是在周期内，没有执行初始化方法，因此在生命周期代码内逐行调试。打印正常语句，如果能打印，则说明前面的代码没有问题
4. 后来发现是创建信息框的语句有问题，命名跟我的后端不一致，谷歌可以显示内容，edge 直接跳过了，所以没有执行初始化。

解决办法：

- 将前端页面的变量名与后端保持一致。
### 聊天信息不是按照时间顺序开始的
原因分析：这个应该是后端代码的问题，读取信息的逻辑，以及读取信息后没有按照时间进行排序。
解决办法：

- 在查询时按照时间进行升序排列。
- 查询条件用 or 表示，分别接受当前用户为发送方和接收方消息。
### 无法接受实时消息
原因分析：后端返回的数据是空数据，即服务器发送给接收方的数据为空数据，所以才无法显示实时消息
解决办法：构建完整的数据返回。
### 在线人数持续增加，两个用户显示在线 9 人
原因分析：用户退出时，在线人数没有-1.导致一直增加。
### 大厅聊天时页面疯狂刷新
原因分析：报错信息显示 messagevo 类中没有 id，而原来的缓存中包含 id 属性，所以无法匹配。
解决办法：删除缓存。重新从数据库中加载
### 队伍聊天无法正常显示
原因分析：websocket 队伍聊天中，存放连接信息的 key 写错了

# 智能聊天机器人
## 需求分析：
在主页添加一个智能聊天机器人，利用大模型技术，实时回答用户问题，提高用户体验。
## 设计：
![](https://cdn.nlark.com/yuque/0/2024/jpeg/12719766/1724410804252-59af6d17-bcc6-467f-9d5f-8d7adc8ea74b.jpeg)
## 技术选型
### 连接管理

1. **WebSocket**:
- **实时双向通信**：WebSocket是全双工的通信协议，允许客户端与服务器之间实时地双向发送和接收消息，非常适合需要低延迟的场景。
- **持久化连接**：一旦握手成功，WebSocket连接通常会保持开放，直到客户端或服务器主动断开。对于需要长期维持连接的应用（如实时聊天、实时数据流），WebSocket非常合适。
- **连接管理的复杂性**：由于WebSocket连接是持久的，需要处理心跳、断线重连等问题，以确保连接的稳定性和有效性。
2. **SDK**:
- **封装复杂性**：SDK通常会封装WebSocket或HTTP/2等底层协议，提供更高层次的API，简化了开发者的集成过程。开发者无需关心底层连接管理，只需调用SDK提供的接口即可。
- **连接池管理**：大多数SDK会管理连接池，以提高连接的重用率和效率，减少连接建立和销毁的开销。对于频繁的请求，SDK能更好地管理连接状态和资源。
- **短链接与长链接支持**：SDK可能同时支持短链接（如HTTP请求）和长链接（如WebSocket），可以根据应用需求灵活选择。

考虑到本项目属于聊天项目，实时性要求比较高，所以选用 websocket 实现连接管理。
## 功能实现

1. 获取与大模型的历史聊天信息
2. 获取当前问题的答案

# 后端接口文档
## 什么是接口文档？
写接口信息的文档，每条接口包括：

- 请求参数
- 响应参数
   - 错误码
- 接口地址
- 接口名称
- 请求类型
- 请求格式
- 备注
## who 谁用？
一般是后端或者负责人来提供，后端和前端都要使用
## 为什么需要接口文档？

- 有个书面内容（背书或者归档），便于大家参考和查阅，便于 沉淀和维护 ，拒绝口口相传
- 接口文档便于前端和后端开发对接，前后端联调的 介质 。后端 => 接口文档 <= 前端
- 好的接口文档支持在线调试、在线测试，可以作为工具提高我们的开发测试效率
## 怎么做接口文档？

- 手写（比如腾讯文档、Markdown 笔记）
- 自动化接口文档生成：自动根据项目代码生成完整的文档或在线调试的网页。Swagger，Postman（侧重接口管理）（国外）；apifox、apipost、eolink（国产）
## Knief4j
官网链接：[快速开始 | Knife4j](https://doc.xiaominfo.com/docs/quick-start)

1. 导入依赖：根据 springBoot 选择版本
```java
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

2. 配置 yml 文件
```java
knife4j:
  enable: true
  openapi:
    title: Knife4j官方文档  标题
    description: "`我是测试`,**你知道吗**
    # aaa" 描述
    email: xiaoymin@foxmail.com
    concat: 八一菜刀 作者
    url: https://docs.xiaominfo.com 可以放个github
    version: v4.0
    license: Apache 2.0  下面这些可以不用改，责任声明之类的
    license-url: https://stackoverflow.com/
    terms-of-service-url: https://stackoverflow.com/
    group:
      test1:
        group-name: 分组名称  
        api-rule: package
        api-rule-resources:
          - com.knife4j.demo.new3 这里添加自己的接口地址，注意，只扫描到controller文件夹即可
```
**注意：一定要将扫描改到 controller 文件夹，不是 usercontroller 文件。**

3. 线上部署时，**需要在生产环境上屏蔽资源**：[3.5 访问权限控制 | Knife4j](https://doc.xiaominfo.com/docs/features/accessControl)
```java
knife4j:
  # 开启增强配置 
  enable: true
　# 开启生产环境屏蔽
  production: true
```

4. 访问接口文档：
```java
http://localhost:8080/doc.html
```

- 其中 8080 是自己项目的端口地址，需要根据实际项目端口进行修改

# 分布式 session
## 什么是session
:::info
session在网络应用中称为“会话控制”，是服务器为了保存用户状态而创建的一个特殊的对象。简而言之，session就是一个对象，用于存储信息。 
:::
种 session 的时候注意范围，cookie.domain
比如两个域名：

- aaa.yupi.com
- bbb.yupi.com

如果要共享 cookie，可以种一个更高层的公共域名，比如 yupi.com
## 为什么服务器 A 登录后，请求发到服务器 B，不认识该用户？
用户在 A 登录，所以 session（用户登录信息）存在了 A 上
结果请求 B 时，B 没有用户信息，所以不认识。
![image.png](https://cdn.nlark.com/yuque/0/2022/png/26770555/1669261508769-827a7a9d-0154-4ba8-835e-5532469c96a6.png#averageHue=%23fafafa&clientId=ude8f8b10-a37a-4&from=paste&height=543&id=ipbtX&originHeight=1138&originWidth=1284&originalType=url&ratio=1&rotation=0&showTitle=false&size=179798&status=done&style=none&taskId=u762477b7-220a-4594-8ab3-941e20796a6&title=&width=613)
解决方案：**共享存储 **，而不是把数据放到单台服务器的内存中
![](https://cdn.nlark.com/yuque/0/2022/png/26770555/1669261508796-2a7e4371-9ad7-466a-9e8b-533fb6679221.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_779%2Climit_0#averageHue=%23fafaf9&from=url&height=447&id=qhgdz&originHeight=570&originWidth=779&originalType=binary&ratio=1.0399999618530273&rotation=0&showTitle=false&status=done&style=none&title=&width=611)
## 实现登录的方案

1. 常用是 JWT
2. 将 session 存储到某个公共的位置（mysql，redis）

与后面使用 redis 存储简单对比，jwt 对于内存性能有部分优化，但使用复杂，并且内存优化并不多，所以在此使用 redis
## 如何实现共享存储
核心思想：把数据存储到一个地方去集中处理，这样服务器都能访问到

1. Redis（基于内存的 K/V 可持久化数据库），此处选择 Redis，因为用户信息读取/是否登录的判断极其频繁，Redis 读写性能高，简单的数据单机 qps 5w-10w
2. MySQL
3. 文件服务器 ceph
## Redis 实现存储 Session

1. 引入 Redis，在 maven repository 中搜索 spring boot Redis,选择与自己 SpringBoot 对应的版本
2. 引入 Session 与 redis 的整合，在 maven respository 中搜索导入与 springboot 一致的版本
3. 在 yml 配置文件中，修改 session 配置，注意**是在 spring 下面**
```yaml
spring:
  session:
    timeout: 86400
    store-type: redis
  redis:
    port: 6379
    host: localhost
    database: 0
```
其中：store-type:是指 session 存储的地方

# 批量数据导入
## 技术选型

1. 可以通过 excel 表格导入导出
2. 通过 sql 语句导入导出
3. 通过**代码实现**数据库导入

本项目选择使用代码实现，并利用多线程并发插入。
## 实现

1. 编写一个 test 类，如果对项目打 jar 包时，需要删除该测试类或者跳过测试打包。
2. 在实际批量添加数据库时，首先添加少量个样本进行测试，例如一开始添加几个，然后添加几百个，几千个，几万个。
3. 避免将数据库所有接口占用，以免影响其它用户使用
4. 构建线程池，并发插入数据
### 为什么要用多线程：

1. 建立和释放数据库连接需要时间，属于 IO 密集型任务
2. for 循环是绝对线性的
### 使用并发批量插入
```java
@SpringBootTest
public class InsertUsers {

    @Resource
    private UserService userService;

    //线程设置
    private ExecutorService executorService = new ThreadPoolExecutor(16,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
    @Test
    public void doInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 50000;
        int batchSize = 10000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM / batchSize; i++){
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                // 设置自己的信息，这里跟现在的数据库存在差异，不能再直接使用
                user.setUserAccount("xiaobai");
                user.setUserName("xiaobai");
                user.setUserUrl("https://picx.zhimg.com/80/v2-a67f86b7702594cc75899f23615aef1d_720w.webp?source=1def8aca");
                user.setProfile("熟练掌握 java, Spring, SpringMVC, MyBatis, MyBatisPlus, SpringBoot 等主流框架\n熟练掌握 JUC 并发编程，熟悉 JVM 原理与操作系统\n熟悉 Linux 环境， 熟练使用 Docker 进行 web 项目部署\n" +
                        "熟练掌握关系型数据库如 MySQL 的使用\n熟悉 Redis 的使用与缓存穿透、雪崩、击穿解决方案\n善于总结反思");
                user.setGender(0);
                user.setUserPassword("a1a6c667b32d27a7a8e09f189ba7bba9");
                user.setPhone("969900860@qq.com");
                user.setEmail("969900860@qq.com");

                user.setPlantId("");
                user.setTags("[\"男\",\"java\",\"python\"]");
                userList.add(user);
                if (j % batchSize == 0){
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                System.out.println("ThreadName" + Thread.currentThread().getName());
                userService.saveBatch(userList);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

}

```
在这个代码中使用线程实现并发添加数据。经过测试，5w 条数据需要 28 秒。比其他人的要慢 
## 关键点：

1. 并发要注意执行的先后顺序无所谓，不要用到非并发类的集合
```java
 private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
```
八股考察点：**线程池、多线程、ThreadPoolExecutor 的核心参数**
// CPU 密集型：分配的核心线程数 = CPU - 1
// IO 密集型：分配的核心线程数可以大于 CPU 核数  2 * CPU
# 缓存
## 在项目中遇到了什么问题需要用到缓存：数据查询慢

1. 当数据库数量达到 79w 时，主页面的推荐用户显示超过了 1 秒，对于这种页面显示，我们希望能够将响应小于 1s，所以就考虑用缓存。
2. 登录时需要对比数据库中所有数据，所以很慢

**核心：**提前把数据取出来放在读写更快的介质，如内存，就可以更快的读写
## 缓存的实现
独立于 java：

1. **Redis（分布式）**
2. memcached（分布式）
3. Etcd（云原生架构的一个分布式存储，**存储配置**，扩容能力）

基于 java：

1. ehcache（单机）
2. 本地缓存（java 内存 map）
3. caffeine（java 内存缓存，高性能）
4. Google Guava
# Redis
## 定义：
key-value 存储结构（区别于 MySQL，存储的是键值对），存储在内存中的键值对数据结构
## 数据结构(5 种)
**String 字符串类型：name:"yupi"**

1. List 列表：names:["yupi","dogyupi","dogyupi"] 值可以重复，数组与列表的区别：数组的大小不可变化
2. set 集合：names：["yupi","dogyupi"]  值不能重复
3. Hash 哈希：nameAge：{"yupi":1,"dogyupi":2}
4. Zset 集合：names:{"yupi"-1,"dogyupi":5}
- 包含 key，value，和 score。唯一不重复，按照分数排序
- 添加操作：redisTemplate.opsForzsET().add(key,value,score(Double));
- 计算数量：redisTemplate.opsForzsET().size();
- 删除操作： redisTemplate.opsForzsET().REMOVErANGEbYsCORE()
## java 里的实现

1. **Spring Data Redis**
2. Jedis
3. Redisson
### Spring Data Redis:
spring data 是一个通用的数据访问框架，提供了增删改查的接口，可以使用 mysql，redis 等数据库
### 使用

1. 引入
```java
        <!-- https://mvnrepository.com/artifact/org.springframework.session/spring-session-data-redis -->
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
            <version>2.5.4</version>
        </dependency>
```

2. 配置 Redis 地址
```yaml
spring:  
  redis:
    port: 6379
    host: localhost
    database: 0
```

3. 创建测试类进行测试，由于提供的实现方法使用了 JDK 默认序列化工具，所以存储的 redis 键值对会存在问题，可以自己写一个实现类（网上搜）
```java
package com.example.friendsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

4. 之后在测试类中进行测试，在实际应用中，**要先写测试类（demo）测试功能**，没有问题后在往主程序中编写
### 缓存中的 key 设计：
由于 redis 比较贵，可以几个项目共用一个 redis，所以要设置不同 key，避免撞车
一般可以设计 key 为：systemid:projectid:function:userid。

- Redis 的 key 命名规范：**模块名：业务逻辑：其他：value 类型**。也可以在加一个作者，例如 xiaobai:user:recent.hour:{id.tags.lastTime}:String;
- Redis 中 key 大小建议不超过 1kb，String 类型的 value 大小不超过 512M，list，hash 等单个 value 不超过 512M。

 例如:
```yaml
String redisKey = String.format("xiaobai:user:recommend:%s",loginUser.getId());
```
**注意：**

- **redis 里的缓存一定要设置过期时间，避免内存不够**
- **不同的 key 设置缓存时过期时间应该要具有随机性，避免缓存雪崩问题**
## 缓存预热
使用 redis 缓存后，当数据存放在缓存中时，数据读取较快，但当用户第一次登录时，缓存中没有数据，查询速度较慢，如何解决？  缓存预热
### 缓存预热的优缺点：
优点：能够解决用户第一次登录缓存中没有数据的问题
缺点：

1. 增加开发者的难度
2. 预热的时间如果选择不对，缓存中的数据可能不对或者太老
3. 占用更多的空间，需要根据实际业务场景进行分析，像双十一这种已知的场景，可以使用缓存预热

> 分析一个方法的优缺点，可以从整个项目从 0 到 1 创建的这个链路上（所有参与的人）进行考虑。


### 实现方法：

1. 定时
2. 模拟触发（/手动触发）
### 定时任务实现
实现方法分类：

1. Spring Scheduler (Spring Boot 默认整合的)
2. Quartz（独立于 Spring 存在的定时任务框架）
3. XXL-Job 之类的分布式任务调度平台
#### 使用 Spring Scheduler

1. 创建一个 preRedisJob 文件，用于写缓存预热定时任务
```java
package com.example.friendsbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class preRedisJob {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private List<User> userList = new ArrayList<>(1);

    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser(){
        for (User user: userList){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userMapper.selectPage(new Page<>(1, 20), queryWrapper);
            String redisKey = String.format("xiaobai:user:recommend:%s",user.getId());
            try {
                redisTemplate.opsForValue().set(redisKey,userPage);
            } catch (Exception e) {
                log.error("redis set key error",e);
            }
        }
    }

}
```

2. 在 myApplication 中开启注解
```java
@SpringBootApplication
@MapperScan("com/example/friendsbackend/mapper")
// 开启定时任务 @EnableScheduling
@EnableScheduling
public class FriendsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendsBackendApplication.class, args);
    }

}
```

3. **改进点**：对缓存中的数据进行脱敏，使用流
```java
            List<User> collect = userPage.getRecords().stream().map(
                    user -> userService.getSafeUser(user)).collect(Collectors.toList());
```
#### 关键点：

- cron = "0 0 0 * * *表达式从左到右依次对应 分 时 天 周 月 年
- 添加 compoent 标签
- 由于数据库中数据量大，并且新增少，所以可以只更新重点用户的缓存，并且每天更新一次（**这个就是具体的业务**）
- 后来本项目更改为只缓存最近几个月登录的用户，避免在推荐页出现僵尸号
- scheduled 中的表达式直接在网上查即可


# 锁
锁是指在有限的资源情况下，控制同一时间（段）只有部分线程（客户/服务器）能访问到资源
## 分布式定时任务
如果项目部署在多个服务器中，对于定时任务来说，只需要一台服务器去执行即可，如果多台服务器同时执行会出现什么问题？

1. 多台服务器同时执行定时任务，**浪费资源**
2. 可能会**产生脏数据**，例如：同时插入新数据，这样就会**产生多个重复数据**
### 如何解决？

1. 只在一个服务器上添加定时任务，需要将定时任务从主程序中脱离出来。成本太大
2. 写死配置，在代码中指定固定 ip 的服务器才能执行。成本最低，但服务器的 ip 可能随时变化。
3. 动态配置，将 ip 配置存储在数据库、Redis 或者配置中心（Nacos、Apollo、Spring Cloud Config）,这样无需重启代码
4. 分布式锁：只有抢到锁的服务器才执行
## 技术选型：

- **synchronized 关键字、ReentrantLock 类（两者的关系）**，但只对单个 JVM 有效
- 分布式锁
## 分布式锁
### 为什么需要分布式锁：

1. 可以从分布式定时任务开始介绍，在资源有限的情况下，控制在同一时间内只有部分线程（客户/服务端）能访问到资源。
2. （分布式锁比单个锁的优点）单个锁只对单个 JVM 有效，无法控制多个服务器
### 分布式锁实现的关键
**抢锁机制**
如何保证同一时刻只有一个服务器抢到锁？
核心思想：先来的服务器将数据改为自己的标识，这样当其它服务器访问时，发现已有标识，则抢锁失败。
**如何实现分布式锁：**
MySQL 数据库：设置一个字段，用来存放标识，默认为空。如果同时访问可能会出现访问都为空，同时修改标识，为了避免这样情况发生，使用 select for update 行级锁（最简单），或者用乐观锁
Redis 实现：

- setnx：set if not exists 如果不存在则设置，只有设置成功返回 true，否则返回 false。
   - 需要自己设置过期时间，自行编写续约和释放锁的逻辑 ，自己处理实例间的锁同步和故障恢复等问题  
- Redission：同时引入看门狗机制
   - Redisson 的看门狗机制会自动为锁续约，直到持有锁的线程显式释放锁为止，避免了锁在操作未完成前意外过期的问题。
   -  Redisson 的看门狗机制自动管理编写续约和释放锁的逻辑，简化了代码，减少了出错的可能性。  
   -  Redisson 是基于 Redis 的 Java 客户端，它提供了更加健壮的分布式锁实现，在分布式环境下支持多个实例之间的锁协调  

本项目采用 **Redission 实现分布式锁，同时引入看门狗机制**
### 注意事项

1. 用完锁一定要释放
2. 设置过期时间（避免服务器挂了导致无法释放 ）
3. 到了自动过期的时间，线程还没有执行完
   1. 可能会导致下一个线程进来执行，产生连锁反应，自己线程执行完后把别人的锁释放掉(先判断是不是自己的锁，如果是在释放)
   2. 有多个线程同时执行

解决办法：续期
```java
boolen end = false;
续期
boolen end = true;
```

4. 在释放自己的锁时，可能判断的时候是自己的锁，但在释放锁之前，另一个线程插了进来，加了它的锁，这时候释放，会释放的它的锁
```java
if (get lock == A){
    // set lock = B; 不允许中间插入操作
    del lock;
}
```
用 Redis + lua 脚本实现

## redisson 实现锁
redisson 实现有两种方式：

- 导入 spring boot redisson 的包（不推荐）
- 只导入 redisson 的包
1. 导入 redisson 包
```xml
<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson</artifactId>
  <version>3.30.0</version>
</dependency>
```

2. 创建 config 类
```java
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient(){
        // 1. 创建配置
        Config config = new Config();
//       addNodeAddress("redis://127.0.0.1:7181");
        String redisAddress = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(2);
        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}

```

3. 在测试类中测试能否正常使用
```java
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void doRedissonTest(){
        //列表
        List<String> list = new ArrayList<>();
        list.add("1");
        System.out.println(list.get(0));
//        list.remove(0);

        RList<String> test = redissonClient.getList("test");
        test.add("test");
        System.out.println("test:" + test.get(0));

        //map
        //set
    }
}
```

4. 给缓存预热添加分布式锁机制
```java
@Component
@Slf4j
public class preRedisJob {
    //to do用户信息脱敏
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    private List<Long> userList = Arrays.asList(1L);
    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("xiaobai:preRedis:docache:lock");
        try {
            if (lock.tryLock(0,-1, TimeUnit.MICROSECONDS)){
                System.out.println("get lock"+Thread.currentThread().getId());
                for (Long userid: userList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(new Page<>(1, 20), queryWrapper);
                    String rediasKey = String.format("xiaobai:user:recommend:%s",userid);
                    List<User> collect = userPage.getRecords().stream().map(
                            user -> userService.getSafeUser(user)).collect(Collectors.toList());
                    try {
                        redisTemplate.opsForValue().set(rediasKey,collect);
                    } catch (Exception e) {
                        log.error("redias set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}

```
### 关键点：

1. 导入包和创建 config 按照 github 里面的快速开发配置即可
2. 注意 config 里面的 redis 地址，需要写明**地址和端口号**，可以在 yml 配置文件中导入，变量名和配置文件中变量名完全相同
```java
@ConfigurationProperties(prefix = "spring.redis")
    private String host;

    private String port;

        String redisAddress = String.format("redis://%s:%s",host,port);

```

3. redisson 可以让开发者像操作 list 一样操作 redis
4. 在项目中添加锁机制，使用：
```java
lock.tryLock(0,-1, TimeUnit.MICROSECONDS)
```
当当前线程获取写入锁后返回 true，否则返回 false。0 代表等待时间，只执行一次

5. **一定要主动释放锁，并且是 finally，避免进程因为意外结束，而锁没有释放**。
```java
    finally {
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock" + Thread.currentThread().getId());
                lock.unlock();
            }
```
## 看门狗
看门狗本质是一种定时任务，监听执行锁的线程，如果还没有执行完，自动续期时间（默认续期 30 秒，每 10 秒检查一次）。
实现方式：
将 lock 里面的超时时间设置为 -1.
```java
      // 具有Watch Dog 自动延期机制 默认续30s 每隔30/3=10 秒续到30s
        lock.lock();
        // 尝试拿锁10s后停止重试,返回false 具有Watch Dog 自动延期机制 默认续30s
        boolean res1 = lock.tryLock(10, TimeUnit.SECONDS); 
```
注意事项：

1. 如果 服务器/线程 意外关闭，开门狗也能识别，释放锁
2. debug 模式也会让看门狗认为服务器挂掉了


# 接口分析
需求：判断当前接口反应所需要的时间，并记录
方案：

- 在每一个接口的方法中起始位置添加一个当前时间，在结尾处用当前时间减去起始时间获得接口响应时间。
- 使用 AOP，这样只需要在每一需要记录的接口上添加注解即可。避免重复，另一个好处是可以记录接口的信息，也可以自定义记录其它值

AOP 实现：

- 导入 aop 依赖
- 创建一个 aop 类，创建一个单独的 aop 文件夹，与 controller 同一级。
- @Aspect注解表明这是一个切面类，@Component注解将其作为Spring的一个组件，@Slf4j是Lombok库提供的一个注解，用于自动生成日志对象。
-  @Pointcut 表示对哪些接口进行 aop 增强
```java
@Component
@Aspect
@Slf4j
public class ApiLogAspect {

    @Pointcut("execution(* com.example.friendsbackend.controller.UserController.recommendUser(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
//        Long userId = WebUtils.getId();
//        String userName = WebUtils.getName();
//        String requestUrl = WebUtils.getRequestUrl();
//        requestUrl = new URL(requestUrl).getPath();
        String requestParam = getMethodArgs(point);
        Object result = point.proceed();
        long endTime = System.currentTimeMillis();
        String responseParam = handlerResult(result);
//        log.info("requestUrl:{} userId:{},userName:{} requestParam：{},responseParam:{},rtt:{}ms", requestUrl, userId, userName, requestParam, responseParam, endTime - startTime);
        log.info("userName:{} requestParam：{},responseParam:{},rtt:{}ms", request.getRequestURI(), requestParam, responseParam, endTime - startTime);
        return result;
    }
```

## 实际记录
数据库数据量：50004 条
用户登录：需要 3s
用户 sesson 存储多久：24h   配置文件中设置 timeout:86400 单位秒
首页用户推荐：

- 有缓存 80ms
- 没有缓存：1s

# 优化点
## 根据标签查询用户耗时长
### 问题描述：
用户根据标签查询 30 个用户时，默认 30 个。需要** 3.0s**。需要将其降到 1.0s 以下。本地数据库 **5w** 条数据
### 当前技术：
**sql 查询 + 内存筛选**

1. 通过 sql 查询表中所有用户的 tags（排除标签为空的用户），**返回用户 id 和 tags**。（如果返回所有字段会增加内存和查询时间）
2. 之后用**编辑距离**计算每个用户与输入标签之间的距离。
3. 使用**最小堆**进行排序，最小堆的插入和删除操作时间复杂度是 o（log(n)）
4. 在根据最小堆中的用户 id 查询用户信息
5. 用户信息脱敏后返回给前端
### 解决思路：

- 从 sql 查询出发，建立索引或其他增加查询速度的方法，分表
   - 使用 queryWrapper 查询包含输入标签的用户，然后在计算分数+脱敏，用时** 7.0s**。
   - 改进查询方法，**初始查询只返回用户 id 和 tags**，时间缩短到 **2.0s**.
   - 改进查询条件，按**照最近登陆时间进行降序排列，选择前 100 个用户进行排序**，时间为 **0.95s**。
   - 使用缓存最近登陆用户信息表（1000 个最近用户+会员），然后在进行匹配，时间需要 1.0s。
   - **问题**：第一次查询需要 2.0s， 第二次查询需要 100ms，但并没有使用缓存。
- 减少查询的次数，不需要查全表用户
   - 例如，增加一个最近上线时间字段，首先根据时间筛选出前 10000 个用户，然后在根据这 1000 个用户标签进行匹配。
   - 经过实验证明，响应时间长主要是由于进行编辑距离排序引起，读取数据差异不大，但可以读取一定数量的数据，避免后续处理时间长。
##  第一次查询需要 2.0s， 第二次查询需要 100ms，但并没有使用缓存。
### 分析：

- 之后记录查询数据前的时间戳,查询数据后的时间戳，计算相似度后的时间戳，发现第一次查询使用 1379 毫秒，计算 1000 个用户与标签的匹配度花了 90ms。第二次查询，查询数据花了 63ms，相似度匹配花了 38ms。
- 进一步分析是否 mysql 使用了查询缓存，今查询，**mysql 使用的 8.0.36，不支持查询缓存**
- 之后发现 mybatis 存在缓存机制，一级缓存和二级缓存。而一级缓存作用于同一个会话 sqlsession，默认是开启的，这样在查询相同条件时会从缓存中读取。所以查询速度变快。但如果执行了修改，新增，删除语句，数据库会清空缓存，防止脏读。
- 现在问题是我在数据库中修改了数据，但查询依然很快。应该是因为** innodb 存储引擎使用了缓冲池**的原因。

##  构建最近登陆用户信息缓存表
### 需求分析

- 在 redis 缓存中构建最近登陆的用户信息，保存 1000 个用户的 id 和标签。
- 每次用户登陆或者进行其他操作时，将当前时间修改到 redis 缓存中，之后统一修改到数据库中。
- 使用定时任务，每小时更新一次。
### 方案设计：

1. 按照最后登陆时间进行排序，选取 1000 个用户，使用 String 类型将 id 和标签保存。
   1. String 占用 300M，因为是其他的列也保存到了内存中。不适合
2. 使用 zset，key 为最近登陆，value 存储用户 id，sorce 存储最后一次登陆的时间（戳）
   1. 用户登陆时，将信息缓存到 Redis 中，Zset 存储
   2. 用 Zset 存储会员 id
   3. 需要使用时根据区间读取 id
   4. 设置定时任务，清理一个月未登陆的用户
### 实现

1. java 读取当前时间转换为 TIME 类型，保存到数据库中
```java
Date date = new Date(); 这个 date 可以直接存储到数据库中，并且是当前时间。
user.setLastTime(date);
userMapper.updateById(user);
```

- Data.getTime()获得的是毫秒数，自 1970.1.1 八点到当前时刻的**毫秒数**。
2. 根据缓存 id 进行查询时，首先查询 id 和标签，进行相似度匹配后在读取完整数据
3. 将缓存的 key 和删除时间作为常量放在 constant 中，
4. 设计定时任务，每天更新缓存里面的最近登陆用户，同时删除固定时间以前的登陆用户
```java
 /**
     * 每天凌晨4点更新当前用户缓存
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void doCacheRecentUser(){
        RLock lock = redissonClient.getLock("xiaobai:user:recentUser:lock");
        try {
            if (lock.tryLock(0,-1,TimeUnit.MICROSECONDS)){
                // 查询最近登录的1000个用户，并将id保存到最近登录缓存中
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id","lastTime");
                queryWrapper.orderByDesc("lastTime");
                queryWrapper.last("limit 0,1000");
                List<User> userList = userMapper.selectList(queryWrapper);
                for (User user : userList){
                    redisUtil.zsetSet(RECENTUSER,user.getId(),user.getLastTime().getTime());
                }
                // 查询svip和vip用户，将结果保存到最近登录表中
                queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id","lastTime");
                queryWrapper.eq("vipState",'2');
                queryWrapper.eq("vipState",'1');
                userList = userMapper.selectList(queryWrapper);
                for (User user : userList){
                    redisUtil.zsetSet(RECENTUSER,user.getId(),user.getLastTime().getTime());
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecentUser error" + e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    @Scheduled(cron = "0 3 3 * * *")
    public void deleteOverdueUser(){
        RLock lock = redissonClient.getLock("xiaobai:user:deleteOverdue:lock");
        try {
            if (lock.tryLock(0,-1,TimeUnit.MICROSECONDS)){
                // 删除缓存中一个月以前的用户
                redisUtil.zsetRemove(RECENTUSER,0L,System.currentTimeMillis() - RECENTTIME);
            }
        }catch (InterruptedException e){
            log.error("deleteOverdueUser error" + e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
```

- @Scheduled 参数 cron：秒 分 时 日 月 周 年，其中*表示每一个，？表示当前参数没影响，-表示从几到几。例如 cron= 0 0 * * * *表示每小时执行一次定时任务。**至少要满足六个参数**（忽略年），否则报错。
### 实现效果
用户根据标签查询相似用户所需时间为 1.0s。大幅度降低
## 解决缓存穿透的问题
### 需求分析
当同时大量查询不存在的用户 id 时，缓存和数据库中都不存在该数据，导致数据库压力过大。
### 技术选型：

1. 使用缓存缓存空对象
   1. 查询不存在的 id 时，会在第一次查询后在缓存中对该 id 缓存一个空对象，同时设置一个短的过期时间
   2. 缺点：
      1. 缓存层需要提供更多的缓存空间来缓存空对象，浪费内存空间
      2. 哪怕是一个很短的过期时间，也会导致这一段时间内数据不一致
   3. 优点：实现简单，方便理解
   4. 适用场景：适用于命中不高，但可能频繁更新的数据
2. 使用布隆过滤器
   1. 由一个位数组和 k 个散列函数组成，初始状态，数组所有位置都为 0，之后添加一个元素时，使用 k 个散列函数计算出 k 个值，对应数组中的 k 个点为 1。当用户查询时，根据数组中 k 个值都为 1 判断数据库中是否存在。
   2. 优点
      1. 保密性强，无法根据数组推断出原数据
      2. 时间复杂度低
      3. 存储空间小
   3. 缺点：
      1. 存在误判情况，例如查询时，发现用户对应的 k 个值都为 1，但这些其实是由于其他数据导致的 1.所以存在误判。可以通过设置误判率实现。
      2. 删除复杂度高，无法保证当前 k 仅有当前用户使用
      3. 无法获取元素本身
   4. 适用场景：适用于命中不高，但更新不频繁的情况。
### 实现方案
**参考：**[https://blog.51cto.com/panyujie/6081164](https://blog.51cto.com/panyujie/6081164)。布隆过滤器介绍及原理、集成Redisson布隆过滤器解决Redis缓存穿透问题

1. Google 的 Guava 实现的布隆过滤器
2. Redission 实现布隆过滤器
   1. 倒入 jar 包：redission
   2. 初始化 redisson 配置
   3. 设置一个 BloomFilterUtils，里面包含 create 方法。核心是 RedissonClient.getBloomFilter 和.tryInint(大小，误判率)
   4. 在 userServerce 里面使用@PostContruct 注解，即项目启动时会执行该方法，将数据库中的 Auserccount 导入到布隆过滤器的数组中。
   5. 当查询用户账号时，先检查是否登陆，然后布隆过滤器判断，如果存在，数据库查询
   6. 配合 redission 缓存一起使用，@Cacheable
      1. spingframework.cache.annotation 包
      2. 首先查询缓存，缓存存在，查询缓存，缓存不存在，将结果塞入缓存
      3. 参数：cacheame、key、unless
      4. 缓存中的 key 为 cacheName::key
      5. unless 是为了防止缓存的 key 存活时间太长，后面自己设置 key 的过期时间（使用缓存缓存 null ）
   7. 用户注册时，将用户账户保存到布隆过滤器数组中

# 日志
[SpringBoot的日志信息及Lombok的常用注解_springboot 日志注解-CSDN博客](https://blog.csdn.net/Trong_/article/details/132351238)
本项目采用： slf4j   +    logback    +  lombok
slf4j：是一种日志框架，位于底层日志信息和开发者之间
logback：是一个日志实现方式，实现记录日志。然后交给 sff4j。
lombok：提供一个@slf4j 注解，这样，就不用用户自己创建 log 实例。同时还提供了 set 和 get 方法。
其中slf4j 和logback 是 spring boot 中自带的。
lombok 需要安装插件，并且导入依赖。

## 遇到的 bug
### 在配置文件（yml）中，写了 logging 相关的配置，导致项目无法启动
原因：复制的 logging 配置字段有些已经发生了变化（失效），所以无法启动
解决办法：
```yaml
logging:
  pattern:
#    指定了日志文件的存储路径
    file: "D:\\project\\friends\\friends-backend\\springbootSlf4j\\server.log"
  logback:
#    日志文件滚动策略
    rollingpolicy:
      max-file-size: "10MB"
      file-name-pattern: "D:\\project\\friends\\friends-backend\\springbootSlf4j\\server.%d{yyyy-MM-dd}.%i.log"
  level:
#    将 web 包的日志级别设置为 debug
    web: debug
#    将根日志级别设置为 error
    root: error
```


## 线上 2.0

- [x] 修改队伍无法创建的问题

原因分析：后端接口地址不一致

- [x] 解决用户推荐页用户少的问题

原因分析：定时删除过期用户中，代码存在时间设置问题，导致删除了大部分客户
# todo：

- [x] 设计用户缓存
- [x] 设计布隆过滤器
- [x] 增加一个智能聊天机器人
- [x] 不能成功测试 
- [x] 日志记录
- [x] 前端修改，运行成功
- [x] 完成聊天功能开发（websocket+chatController）
- [ ] 登录后推荐页不显示用户
- [ ] 个人信息修改页的标签无法修改
- [ ] 标签匹配时使用其他的判断方法
- [ ] 消息首先存储在 redis 或者其他消息队列中，然后在存储到数据库，避免数据库的频繁读写

# 
# 
