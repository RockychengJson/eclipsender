package org.easy4.tools.generator;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC driver agent class
 * 
 * @author Simon
 *
 */
public class DriverImpl implements Driver {
	
	private Driver driver;
	
	public DriverImpl(Driver driver){
		this.driver = driver;
	}

	public boolean acceptsURL(String url) throws SQLException {
		return driver.acceptsURL(url);
	}

	public Connection connect(String url, Properties info) throws SQLException {
		return driver.connect(url, info);
	}

	public int getMajorVersion() {
		return driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return driver.getMinorVersion();
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return driver.getPropertyInfo(url, info);
	}

	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}

}
