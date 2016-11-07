# jdbc-mapper
基于jdbc的封装，自带连接池等。支持多数据源，配置简单，支持注解sql查询，自带分页查询。

#快速开始
JdbcMapper m=new JdbcMapper();
默认加载classpath路径下的jdbc.properties文件。
properties文件中必须配置driverClassName,url,username,password,initPoolSize（可选）,maxPoolSize（可选）。
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/test
username=root
password=root
initPoolSize=2
maxPoolSize=10
g配置为spring bean:
<bean id="jdbcMapper" class="com.di.jdbc.template.JdbcMapper"></bean>
多数据源可以通过不同的构造函数实现。
JdbcMapper m=new JdbcMapper(String fileName);
传人properties文件名，即可。
g配置为spring bean:
<bean id="oracle" class="com.di.jdbc.template.JdbcMapper">
<constructor-arg>
<value>oracle.properties</value>
</constructor-arg>
</bean>

#方法介绍
boolean execute(String sql)；
执行sql语句，返回成功或失败;
boolean prepareExecute(String preSql, Object[] args)；
执行预编译的sql语句，args参数，返回成功或失败;

List<T> queryForList(String sql, Class<T> resultClass);
执行sql语句，返回多条记录为指定类型的pojo;

T queryForObject(String sql, Class<T> resultClass);
执行sql语句，返回一条记录映射为指定类型的pojo;

List<HashMap<String, Object>> queryForMap(String sql);
执行sql语句，结果映射为Map;

T queryForSingleValue(String sql, Class<T> resultClass);
执行sql语句，返回一行一列的值，通常可用来查询总数;

List<T> prepareQueryForList(String preSql, Object[] args, Class<T> resultClass)；
执行prepareStatement语句，返回多条记录为指定类型的pojo;
省略其余prepare方法……

void insert(T o);
插入一条记录。

void insertReturnKey(T o)；
插入一条记录，如果数据库有返回自动生成的主键，则主键值会赋值给o。

void update(T o);
更新一条记录；

void insertMillionObjects(List<T> os, int sqlSize, int batchSize)；
用于大批量插入数据，可插入百万条数据到数据库，sqlSize通常应小于10000，如果实体类字段较多数据较大则相应减小sqlSize值防止插入失败，batchSize随意。

Pager<T> queryPager(String sql, int pageNum, int pageSize, Class<T> t);
Pager<T> prepareQueryPager(String preSql, Object[] args, int pageNum, int pageSize, Class<T> t);
Pager<T> selectPagerByExample(Object e, int pageNum, int pageSize, Class<T> t)；
执行分页查询
Pager｛
	List<T> list;
	int pageNum;
	int pageSize;
	long total;
｝
List<T> selectByExample(Object e, Class<T> t);
根据example查询，与mybatis通用的example;
long countByExample(Object e, Class<T> t);
void deleteByExample(Object e, Class<T> t);