package com.di.jdbc.mapper.core.impl;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.di.jdbc.mapper.JdbcConfig;

public class DefaultDataSource implements DataSource, Runnable {
	private String driver;
	private String url;
	private String username;
	private String password;
	private AtomicInteger poolSize;
	private int maxSize;
	private LinkedList<Connection> pool = new LinkedList<>();

	private DefaultDataSource() {
		super();
	}

	public DefaultDataSource build(String fileName) {
		return build(JdbcConfig.createDefault(fileName));
	}

	public DefaultDataSource build(JdbcConfig config) {
		return build(config.getDriverClassName(), config.getUrl(), config.getUsername(), config.getPassword(), config.getInitSize(), config.getMaxSize());
	}

	public DefaultDataSource build(Map<String, String> prop) {
		return build(prop.get("driver"), prop.get("url"), prop.get("username"), prop.get("password"), prop.get("poolSize") == null ? 1 : Integer.valueOf(prop.get("poolSize")),
				prop.get("poolSize") == null ? 10 : Integer.valueOf(prop.get("maxSize")));
	}

	public static DefaultDataSource build(String url, String name, String pwd) {
		return build(DbTypeUtil.get(url), url, name, pwd, 1, 10);
	}

	public static DefaultDataSource build(String driver, String url, String name, String pwd) {
		return build(driver, url, name, pwd, 1, 10);
	}

	public static DefaultDataSource build(String driver, String url, String name, String pwd, int poolSize, int maxSize) {
		DefaultDataSource d = new DefaultDataSource();
		d.driver = driver;
		d.username = name;
		d.password = pwd;
		d.poolSize = new AtomicInteger(poolSize);
		d.maxSize = maxSize;
		try {
			Class.forName(driver);
			if (poolSize <= 0) {
				throw new RuntimeException("不支持的池大小" + poolSize);
			}
			for (int i = 0; i < poolSize; i++) {
				Connection con = DriverManager.getConnection(url, name, pwd);
				con = ConnectionProxy.getProxyedConnection(con, d.pool);
				d.pool.add(con);
			}
			Thread t = new Thread(d);
			t.setDaemon(true);
			t.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return d;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getPoolSize() {
		return poolSize.get();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new RuntimeException("Unsupport Operation.");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new RuntimeException("Unsupport Operation.");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new RuntimeException("Unsupport Operation.");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T) this;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection con = getOneConn();
		if (!valid(con)) {
			removeConnection(con);
			con = getOneConn();
		}
		return con;
	}

	private Connection getOneConn() {
		int poolSize = getPoolSize();
		synchronized (pool) {
			if (pool.size() == 0 && poolSize >= maxSize) {
				try {
					pool.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
				return getOneConn();
			} else if (pool.size() == 0 && poolSize < maxSize) {
				return newConnection();
			} else {
				Connection con = pool.removeFirst();
				// if (!valid(con)) {
				// removeConnection(con);
				// con = newConnection();
				// }
				return con;
			}
		}
	}

	private Connection newConnection() {
		try {
			Connection con = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
			con = ConnectionProxy.getProxyedConnection(con, this.pool);
			this.poolSize.incrementAndGet();
			return con;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeConnection(Connection con) {
		this.poolSize.decrementAndGet();
		con = null;
	}

	private boolean valid(Connection con) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery("SELECT 1");
			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new RuntimeException("不支持接收用户名和密码的操作");
	}

	static class ConnectionProxy implements InvocationHandler {
		private Object o;
		private LinkedList<Connection> pool;

		private ConnectionProxy(Object o, LinkedList<Connection> pool) {
			this.o = o;
			this.pool = pool;
		}

		public static Connection getProxyedConnection(Object o, LinkedList<Connection> pool) {
			Object proxed = Proxy.newProxyInstance(o.getClass().getClassLoader(), new Class[] { Connection.class }, new ConnectionProxy(o, pool));
			return (Connection) proxed;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("close")) {
				synchronized (pool) {
					pool.add((Connection) proxy);
					pool.notify();
				}
				return null;
			} else {
				return method.invoke(o, args);
			}
		}

	}

	@Override
	public void run() {
		System.out.println("start to valid connection.");
		while (true) {
			synchronized (pool) {
				for (Connection c : pool) {
					if (!valid(c)) {
						pool.remove(c);
						pool.add(newConnection());
					}
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
