package org.easy4.tools.generator;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.easy4.tools.generator.entities.Field;
import org.easy4.tools.generator.entities.Table;

public class DataBase {

	private String driverClass;
	
	private String username;
	
	private String password;
	
	private String url;
	
	private String library;
	
	private Connection connection;
	
	public DataBase(){};
	
	public DataBase(String driverClass, String username, String password, String url, String library){
		this.driverClass = driverClass;
		this.username = username;
		this.password = password;
		this.url = url;
		this.library = library;
	}
	
	protected void initConnection() throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		URL libURL = new URL("jar:file:/" + library + "!/");
		URLClassLoader classLoader = new URLClassLoader(new URL[] { libURL });
		Driver driver = (Driver)Class.forName(driverClass, true, classLoader).newInstance();
		DriverManager.registerDriver(new DriverImpl(driver));
		connection = DriverManager.getConnection(url, username, password);
	}
	
	public List getTables() throws SQLException{
		if (connection == null || connection.isClosed()){
			try{
				initConnection();
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet tables = meta.getTables(null, null, null, new String[]{"TABLE"});
		List tableList = new ArrayList();
		while(tables.next()){
			Table table = new Table();
			table.setName(tables.getString("TABLE_NAME"));
			table.setDescription(tables.getString("REMARKS"));
			HashSet pkSet = new HashSet();
			// Get PrimaryKey from metadata
			ResultSet primaryKeys = meta.getPrimaryKeys(null, null, table.getName());
			while(primaryKeys.next()){
				pkSet.add(primaryKeys.getString("COLUMN_NAME"));
				
			}
			primaryKeys.close();
			ArrayList fieldList = new ArrayList();
			// Get fields from metadata
			ResultSet fields = meta.getColumns(null, null, table.getName(), null);
			fieldList.clear();
			while(fields.next()){
				Field field = new Field();
				field.setName(fields.getString("COLUMN_NAME"));
				field.setDescription(fields.getString("REMARKS"));
				field.setType(getJavaType(fields.getInt("DATA_TYPE")));
				if(DatabaseMetaData.columnNoNulls == fields.getShort("NULLABLE")){
					field.setNullable(true);
				} else {
					field.setNullable(false);
				}
				field.setPrimaryKey(pkSet.contains(field.getName()));
				fieldList.add(field);
			}
			fields.close();
			table.setFields((Field[]) fieldList.toArray(new Field[0]));
			tableList.add(table);
		}
		tables.close();
		if (!connection.isClosed()){
			connection.close();
		}
		connection = null;
		return tableList;
	}
	
	public String getJavaType(int sqlType){
		switch(sqlType) {
			case Types.VARCHAR :
				return Field.STRING;
			case Types.INTEGER :
				return Field.INT;
			case Types.CHAR :
				return Field.STRING;
			case Types.DATE :
				return Field.DATE;
			case Types.DECIMAL :
				return Field.FLOAT;
			case Types.NUMERIC :
				return Field.LONG;
			case Types.TIMESTAMP :
				return Field.DATE;
			default :
				return Field.OBJECT;
		}
	}

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the library
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * @param library the library to set
	 */
	public void setLibrary(String library) {
		this.library = library;
	}
}
