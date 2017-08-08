# jdbc-mapper
基于jdbc的封装，自带连接池等。支持多数据源，配置简单，支持注解sql查询，自带分页查询。

#快速开始
引入依赖
```
<dependency>
<groupId>com.github.dingey</groupId>
<artifactId>jdbc-mapper</artifactId>
<version>1.1</version>
</dependency>
```
JdbcMapper m=new JdbcMapper();<br>
默认加载classpath路径下的jdbc.properties文件。<br>
properties文件中必须配置driverClassName,url,username,password,initPoolSize（可选）,maxPoolSize（可选）。<br>
```
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/test
username=root
password=root
initPoolSize=2
maxPoolSize=10
```
配置为spring bean:<br>
```
<bean id="jdbcMapper" class="com.di.jdbc.mapper.core.JdbcMapper"></bean>

<bean id="jdbcMapper" class="com.di.jdbc.mapper.core.JdbcMapper">
	<property name="fileName" value="jdbc.properties" />
</bean>
```
多数据源可以通过不同的构造函数实现。<br>
JdbcMapper m=new JdbcMapper(String fileName);<br>
传人properties文件名，即可。<br>
配置为spring bean:<br>
```
<bean id="oracle" class="com.di.jdbc.mapper.JdbcMapper">
<constructor-arg>
<value>oracle.properties</value>
</constructor-arg>
</bean>
```
#方法介绍
```
boolean execute(String sql)
```
执行sql语句，返回成功或失败;
```
boolean prepareExecute(String preSql, Object[] args)
```
执行预编译的sql语句，args参数，返回成功或失败;
```
List<T> queryForList(String sql, Class<T> resultClass)
```
执行sql语句，返回多条记录为指定类型的pojo;
```
T queryForObject(String sql, Class<T> resultClass)
```
执行sql语句，返回一条记录映射为指定类型的pojo;
```
List<HashMap<String, Object>> queryForMap(String sql)
```
执行sql语句，结果映射为Map;
```
T queryForSingleValue(String sql, Class<T> resultClass)
```
执行sql语句，返回一行一列的值，通常可用来查询总数;
```
List<T> prepareQueryForList(String preSql, Object[] args, Class<T> resultClass)
```
执行prepareStatement语句，返回多条记录为指定类型的pojo;
省略其余prepare方法……
```
void insert(T o)
```
插入一条记录。
```
void insertReturnKey(T o)
```
插入一条记录，如果数据库有返回自动生成的主键，则主键值会赋值给o。
```
void update(T o)
```
更新一条记录；
```
void insertMillionObjects(List<T> os, int sqlSize, int batchSize)
```
用于大批量插入数据，可插入百万条数据到数据库，sqlSize通常应小于10000，如果实体类字段较多数据较大则相应减小sqlSize值防止插入失败，batchSize随意。
#分页查询
执行分页查询,只支持mysql,oracle。
```
Pager<T> queryPager(String sql, int pageNum, int pageSize, Class<T> resultClass)
```
执行sql分页查询
```
Pager<T> prepareQueryPager(String preSql, Object[] args, int pageNum, int pageSize, Class<T> resultClass)
```
执行prepare sql分页查询
```
Pager<T> prepareNamedQueryPager(String namedQueryName, Object[] args, Class<T> resultClass)
```
#命名查询
执行本地命名查询
```
@NamedNativeQueries(value = { @NamedNativeQuery(name = "selectAll", query = "select * from person"),
		@NamedNativeQuery(name = "selectOne", query = "select * from person where id<?") })
public class Person {}
```
可以将sql语句写在类上，方便统一管理。

#分页查询
```
Pager<T> selectPagerByExample(Object e, int pageNum, int pageSize, Class<T> resultClass)
```
执行example分页查询<br/>
分页
```
Pager｛
	List<T> list;
	int pageNum;
	int pageSize;
	long total;
｝
```
#example查询
与mybatis通用的example;
```
List<T> selectByExample(Object e, Class<T> resultClass)
```
根据example查询;
```
long countByExample(Object e, Class<T> resultClass)
```
根据example查询汇总;
```
void deleteByExample(Object e, Class<T> resultClass)
```
根据example删除;

#分表查询
```
@NamedNativeQueries(value = { @NamedNativeQuery(name = "selectAll", query = "select * from person where id>?"),
		@NamedNativeQuery(name = "selectOne", query = "select * from person where id<?") })
public class Person {
	@Id
	private Integer id;
	private String name;
	private Integer age;
	private Integer sex;
	@TableField
	private String tableName;
}
```
支持分表查询，将代表表名的成员变量，添加@TableField注解。程序会自动将加了注解的变量值替换为表名。
#分库查询
分库查询默认以多数据源的形式实现。

#事务
```
JdbcMapper mapper=new JdbcMapper();
try {
	Man m=new Man();
	m.setId(1);
	m.setName("b");
	mapper.beginTransaction();
	mapper.insertWithTransaction(m);
	m.setName("a");
	mapper.updateWithTransaction(m);
	mapper.deleteWithTransaction(m);
	mapper.commit();
} catch (SQLException e) {
	e.printStackTrace();
	mapper.rollback();
}
```
