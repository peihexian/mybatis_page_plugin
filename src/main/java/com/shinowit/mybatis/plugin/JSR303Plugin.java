package com.shinowit.mybatis.plugin;

import java.sql.Types;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class JSR303Plugin extends PluginAdapter {
	
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelFieldGenerated(Field field,
			TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		
		if (false==introspectedColumn.isNullable()){
			if (false==introspectedColumn.isIdentity()){
				topLevelClass.addImportedType("javax.validation.constraints.NotNull");
				field.addAnnotation("@NotNull");
			}
		}
		
		if (true==introspectedColumn.isStringColumn()){
			topLevelClass.addImportedType("javax.validation.constraints.Size");
			field.addAnnotation("@Size(min = 0, max = "+introspectedColumn.getLength()+" , message = \"长度必须在{min}和{max}之间\")");
		}
		
		if ((introspectedColumn.getJdbcType()==Types.INTEGER) && (introspectedColumn.isIdentity()==false)){
			topLevelClass.addImportedType("javax.validation.constraints.Max");
			field.addAnnotation("@Max(value=2147483647,message=\"最大值不能高于{value}\")");
			topLevelClass.addImportedType("javax.validation.constraints.Min");
			field.addAnnotation("@Min(value=-2147483648,message=\"最小值不能低于{value}\")");
		}

		if (introspectedColumn.getJdbcType()==Types.DATE){
			topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
			field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd\")");
		}
		
		if (introspectedColumn.getJdbcType()==Types.TIMESTAMP){
			topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
			field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
		}
		
		return true;
	}
	
	

}
