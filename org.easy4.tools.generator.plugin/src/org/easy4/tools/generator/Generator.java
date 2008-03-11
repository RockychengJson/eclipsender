package org.easy4.tools.generator;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.easy4.tools.generator.entities.Field;
import org.easy4.tools.generator.entities.Table;
import org.easy4.tools.generator.preferences.Constants;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.preference.IPreferenceStore;

public class Generator {
	
	private final static String EXTENSION = ".java";
	
	private String entityPre;
	
	private String entitySub;
	
	private String daoPre;
	
	private String daoSub;
	
	private String daiPre;
	
	private String daiSub;
	
	private VelocityEngine engine;
	
	public Generator() throws Exception{
		// Initial Velocity Engine
		engine = new VelocityEngine();
		Properties props = new Properties();
	    props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
	    props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
	    engine.init(props);
	    // Initial file prefix and subfix
	    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	    entityPre = store.getString(Constants.P_ENT_PRE);
	    entitySub = store.getString(Constants.P_ENT_SUB);
	    daoPre = store.getString(Constants.P_DAO_PRE);
	    daoSub = store.getString(Constants.P_DAO_SUB);
	    daiPre = store.getString(Constants.P_DAI_PRE);
	    daiSub = store.getString(Constants.P_DAI_SUB);
	}
	
	public void generate(Table table, IPackageFragment pkg) throws ResourceNotFoundException, ParseErrorException, Exception {
		// Make entity bean
		VelocityContext context = new VelocityContext();
    	
    	context.put("package_name", pkg.getElementName());
    	
    	context.put("class_description", table.getDescription());
    	
    	context.put("class_name", table.getNameAsClassName());
    	
    	context.put("table", table);
    	
		Template template = engine.getTemplate("Bean.vm");
		
		StringWriter writer = new StringWriter();
		
		template.merge(context, writer);
		
		String fileName = entityPre + table.getNameAsClassName() + entitySub + EXTENSION;
		
		pkg.createCompilationUnit(fileName, writer.toString(), true, null);
		
		writer.close();
		
		// Make Dao Interface
		context = new VelocityContext();
		
		context.put("package_name", pkg.getElementName());
		
		context.put("class_description", table.getNameAsClassName());
		
		context.put("class_name", daiPre + table.getNameAsClassName() + daiSub);
		
		context.put("module_name", entityPre + table.getNameAsClassName() + entitySub);
		
		context.put("primary_key_parameters", getPrimaryKeysAsParameters(table.getPrimaryKeys()));
		
		context.put("table", table);
		
		template = engine.getTemplate("DaoIF.vm");
		
		writer = new StringWriter();
		
		template.merge(context, writer);
		
		fileName = daiPre + table.getNameAsClassName() + daiSub + EXTENSION;
		
		pkg.createCompilationUnit(fileName, writer.toString(), true, null);
		
		writer.close();
		
		// Make Dao Class
		context = new VelocityContext();
		
		context.put("package_name", pkg.getElementName());
		
		context.put("class_description", table.getNameAsClassName());
		
		context.put("class_name", daoPre + table.getNameAsClassName() + daoSub);
		
		context.put("module_name", entityPre + table.getNameAsClassName() + entitySub);
		
		context.put("primary_key_parameters", getPrimaryKeysAsParameters(table.getPrimaryKeys()));
		
		context.put("where_statement", makeSearchCondition(table.getPrimaryKeys()));
		
		context.put("insert_filed_name", joinFields(table.getFields()));
		
		String[] insertValues = new String[table.getFields().length];
		
		Arrays.fill(insertValues, "?");
		
		context.put("insert_values", joinWith(insertValues, ","));
		
		context.put("update_statement", makeUpdateStatement(table.getFields()));
		
		context.put("interface_name", daiPre + table.getNameAsClassName() + daiSub);
		
		context.put("table", table);
		
		template = engine.getTemplate("Dao.vm");
		
		writer = new StringWriter();
		
		template.merge(context, writer);
		
		fileName = daoPre + table.getNameAsClassName() + daoSub + EXTENSION;
		
		pkg.createCompilationUnit(fileName, writer.toString(), true, null);
		
		writer.close();
		
	}
	
	private String getPrimaryKeysAsParameters(Field[] primaryKeys){
		if (primaryKeys != null){
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<primaryKeys.length; i++) {
				if (i==0) {
					sb.append(primaryKeys[i].getType());
					sb.append(" ");
					sb.append(primaryKeys[i].getNameWithFirstLower());
				} else {
					sb.append(", ");
					sb.append(primaryKeys[i].getType());
					sb.append(" ");
					sb.append(primaryKeys[i].getNameWithFirstLower());
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	private String makeSearchCondition(Field[] fields){
		StringBuffer whereStatement = new StringBuffer();
		if (fields != null){
			for (int i=0; i<fields.length; i++){
				if (i==0) {
					whereStatement.append(" WHERE ");
					whereStatement.append(fields[i].getName()).append(" = ?");
				} else {
					whereStatement.append(" AND ");
					whereStatement.append(fields[i].getName()).append(" = ?");
				}
			}
		}
		return whereStatement.toString();
	}
	
	private String joinFields(Field[] fields) {
		if (fields != null){
			String[] names = new String[fields.length];
			for (int i=0; i<names.length; i++) {
				names[i] = fields[i].getName();
			}
			return joinWith(names, ",");
		}
		return null;
	}
	
	private String makeUpdateStatement(Field[] fields){
		if (fields != null) {
			String[] pairs = new String[fields.length];
			for(int i=0; i<fields.length; i++) {
				pairs[i] = fields[i].getName() + "=?";
			}
			return joinWith(pairs, ",");
		}
		return null;
	}
	
	private String joinWith(String[] values, String spliter){
		if (values != null){
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<values.length; i++){
				if (i==0) {
					sb.append(values[i]);
				} else {
					sb.append(spliter).append(values[i]);
				}
			}
			return sb.toString();
		}
		return null;
	}
}
