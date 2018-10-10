# jdbc-mapper
基于jdbc的封装，自带连接池等。支持多数据源，配置简单，支持注解sql查询，自带分页查询。

#快速开始
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

#实体介绍
```
import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.NamedNativeQueries;
import com.di.jdbc.mapper.annotation.NamedNativeQuery;
import com.di.jdbc.mapper.annotation.Table;
@Table(name = "man")
@NamedNativeQueries(@NamedNativeQuery(name = "getOne", query = "select * from man where id=?"))
public class Man {
	@Id
	private int id;
	@Column(name = "name")
	private String name;
}
```

#方法介绍
```
boolean execute(String sql)
```

执行sql语句，返回成功或失败;
```
boolean execute(String preSql, Object[] args)
```

执行预编译的sql语句，args参数，返回成功或失败;
```
List<T> list(String sql, Class<T> resultClass)
```

执行sql语句，返回多条记录为指定类型的pojo;
```
T get(String sql, Class<T> resultClass)
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

插入一条记录。
```
void insert(T o)
```

可选择插入一条记录。
```
void insertSelective(T o)
```

更新一条记录；
```
int update(T t)
```

可选择更新一条记录；
```
int updateSelective(T t)
```

#分页查询
执行分页查询,只支持mysql,oracle。
```
Pager<T> page(String sql, int pageNum, int pageSize, Class<T> resultClass)
```

执行sql分页查询
```
Pager<T> page(String preSql, Object[] args, int pageNum, int pageSize, Class<T> resultClass)
```

执行NamedQuery分页查询
```
Pager<T> pageByNamedQuery(String namedQueryName, Object[] args, Class<T> resultClass)
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


分页
```
Pager｛
	List<T> list;
	int pageNum;
	int pageSize;
	long total;
｝
```

#事务
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
} catch (SQLException e) {
	e.printStackTrace();
	mapper.rollback();
}
```
