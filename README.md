# jdbc-mapper
基于jdbc的封装，支持数据源，配置简单，支持注解sql查询，自带分页查询。

# 快速开始

引入依赖
```
<dependency>
<groupId>com.github.dingey</groupId>
<artifactId>jdbc-mapper</artifactId>
<version>3.0</version>
</dependency>
```

配置bean
```
DataSource dataSource=DefaultDataSource.build("jdbc:mysql://localhost:3306/sys", "root", "root");
JdbcFactory factory = JdbcFactory.build(dataSource);
JdbcMapper mapper = factory.getMapper();
```
或者
```
DataSource dataSource=DefaultDataSource.build("jdbc:mysql://localhost:3306/sys", "root", "root");
JdbcMapper mapper = JdbcMapper.build(dataSource);
```

# 方法介绍

执行sql语句，返回成功或失败;
```
boolean execute(String sql)
```

执行预编译sql语句，返回成功或失败;
```
boolean execute(String preSql, Object...args)
```

执行sql语句，返回一条或一列，具体取决于resultClass类型;
```
T get(String sql, Class<T> resultClass)
```

执行预编译sql语句，返回一条或一列，具体取决于resultClass类型;
```
T get(String sql, Class<T> resultClass)
```

执行sql语句，结果映射为Map,第一列为key,第二列value;
```
Map<Object,Object> listToMap(String sql);
```

执行预编译的sql语句，结果映射为Map,第一列为key,第二列value;
```
Map<Object, Object> listToMap(String sql, Object... args);
```

执行sql语句，返回list对象;
```
List<T> list(String sql, Class<T> resultClass)
```

执行预编译的sql语句，返回list对象，
```
List<T> list(String preSql,Class<T> resultClass, Object...args)
```


插入一条记录,如果需要返回自增主键在主键属性上加@GeneratedValue注解。
```
void insert(T o)
```

可选择插入一条记录，忽略null的属性。
```
void insertSelective(T o)
```

更新一条记录；
```
int update(T t)
```

可选择更新一条记录，忽略null的属性。
```
int updateSelective(T t)
```

# 分页查询
执行分页查询,只支持mysql,oracle。

分页对象
```
Pager｛
	List<T> list;
	int pageNum;
	int pageSize;
	long total;
｝
```
执行sql分页查询
```
Pager<T> page(String sql, int pageNum, int pageSize, Class<T> resultClass)
```

执行预编译sql分页查询
```
Pager<T> page(String preSql, int pageNum, int pageSize, Class<T> resultClass， Object...args)
```

执行NamedQuery分页查询
```
Pager<T> pageByNamedQuery(String namedQueryName,Class<T> resultClass,Object... args)
```

# 命名查询
执行本地命名查询
```
@NamedNativeQueries(value = { @NamedNativeQuery(name = "selectAll", query = "select * from person"),
		@NamedNativeQuery(name = "selectOne", query = "select * from person where id<?") })
public class Person {}
```
可以将sql语句写在类上，方便统一管理。

执行命名语句
```
boolean executeByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args);
```

执行命名查询语句
```
T getByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args);
```

执行命名语句查询，返回list对象
```
List<T> listByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args);	
```


# 事务
```
DataSource dataSource=DefaultDataSource.build("jdbc:mysql://localhost:3306/sys", "root", "root");
JdbcFactory factory = JdbcFactory.build(dataSource);
JdbcMapper mapper = factory.getMapper();
try {
	Man m=new Man();
	m.setId(1);
	m.setName("b");
	mapper.begin();
	mapper.insert(m);
	m.setName("a");
	mapper.update(m);
	mapper.commit();
} catch (Exception e) {
	e.printStackTrace();
	mapper.rollback();
}
```
