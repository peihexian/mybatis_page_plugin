package com.shinowit.mybatis.plugin;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;


public class SelectByPage4MySqlPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	private void addField(String fieldName, FullyQualifiedJavaType fieldType,
			TopLevelClass topLevelClass) {
		Field tmpField = new Field(fieldName, fieldType);
		tmpField.setVisibility(JavaVisibility.PRIVATE);
		topLevelClass.addField(tmpField);

		Method setMethod = new Method();
		setMethod.setName("set" + fieldName.toUpperCase().substring(0, 1)
				+ fieldName.substring(1));
		Parameter param = new Parameter(fieldType, fieldName);
		setMethod.addParameter(param);
		setMethod.setVisibility(JavaVisibility.PUBLIC);
		setMethod.addBodyLine("this." + fieldName + "=" + fieldName + ";");

		topLevelClass.addMethod(setMethod);

		Method getMethod = new Method();
		getMethod.setName("get" + fieldName.toUpperCase().substring(0, 1)
				+ fieldName.substring(1));

		getMethod.setReturnType(fieldType);
		getMethod.setVisibility(JavaVisibility.PUBLIC);
		getMethod.addBodyLine("return this." + fieldName + ";");

		topLevelClass.addMethod(getMethod);

	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		FullyQualifiedJavaType intType = FullyQualifiedJavaType
				.getIntInstance();
		addField("pageIndex", intType, topLevelClass);
		addField("pageSize", intType, topLevelClass);
		addField("customCriteria", FullyQualifiedJavaType.getStringInstance(), topLevelClass);

		Method getSkipCountMethod = new Method();
		getSkipCountMethod.setName("getSkipRecordCount");

		getSkipCountMethod.setReturnType(intType);
		getSkipCountMethod.setVisibility(JavaVisibility.PUBLIC);
		getSkipCountMethod.addBodyLine("int count=(this.pageIndex-1)*this.pageSize;");
		getSkipCountMethod.addBodyLine("if (count<0){");
		getSkipCountMethod.addBodyLine(" count=0;");
		getSkipCountMethod.addBodyLine("}");
		getSkipCountMethod.addBodyLine("return count;");

		topLevelClass.addMethod(getSkipCountMethod);
		
		Method getEndRecordIndexMethod = new Method();
		getEndRecordIndexMethod.setName("getEndRecordCount");

		getEndRecordIndexMethod.setReturnType(intType);
		getEndRecordIndexMethod.setVisibility(JavaVisibility.PUBLIC);
		getEndRecordIndexMethod
				.addBodyLine("return this.pageIndex*this.pageSize;");

		topLevelClass.addMethod(getEndRecordIndexMethod);
		

		Method newConstructorMethod = new Method();
		newConstructorMethod.setConstructor(true);
		newConstructorMethod.addParameter(new Parameter(intType, "pageSize"));
		newConstructorMethod.addParameter(new Parameter(intType, "pageIndex"));
		newConstructorMethod.addBodyLine("this();");
		newConstructorMethod.addBodyLine("this.pageSize=pageSize;");
		newConstructorMethod.addBodyLine("this.pageIndex=pageIndex;");
		newConstructorMethod.setVisibility(JavaVisibility.PUBLIC);
		newConstructorMethod.setName(topLevelClass.getType().getShortName());

		topLevelClass.addMethod(newConstructorMethod);
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze,
			TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
				introspectedTable.getExampleType());
		importedTypes.add(type);
		importedTypes.add(FullyQualifiedJavaType.getNewListInstance());

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);

		FullyQualifiedJavaType returnType = FullyQualifiedJavaType
				.getNewListInstance();
		FullyQualifiedJavaType listType;
		if (introspectedTable.getRules().generateBaseRecordClass()) {
			listType = new FullyQualifiedJavaType(introspectedTable
					.getBaseRecordType());
		} else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
			listType = new FullyQualifiedJavaType(introspectedTable
					.getPrimaryKeyType());
		} else {
			throw new RuntimeException(getString("RuntimeError.12")); //$NON-NLS-1$
		}

		importedTypes.add(listType);
		returnType.addTypeArgument(listType);
		method.setReturnType(returnType);

		method.setName("selectPage");
		method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$

		interfaze.addImportedTypes(importedTypes);
		interfaze.addMethod(method);
		

		List<IntrospectedColumn> pks=introspectedTable.getPrimaryKeyColumns();
		if (pks.size()==1){
			Method select_max_primary_key_value_method = new Method();
			select_max_primary_key_value_method.setVisibility(JavaVisibility.PUBLIC);

			FullyQualifiedJavaType select_max_primary_key_value_method_returnType = pks.get(0).getFullyQualifiedJavaType();

			importedTypes.add(select_max_primary_key_value_method_returnType);
			
			select_max_primary_key_value_method.setReturnType(select_max_primary_key_value_method_returnType);

			select_max_primary_key_value_method.setName("selectMaxPrimaryKeyValue");

			interfaze.addImportedTypes(importedTypes);
			interfaze.addMethod(select_max_primary_key_value_method);
			
		}

		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document,
			IntrospectedTable introspectedTable) {
		XmlElement parentElement = document.getRootElement();

		XmlElement newResultMapElement = new XmlElement("resultMap");
		newResultMapElement
				.addAttribute(new Attribute("id", "selectPageResult"));
		newResultMapElement.addAttribute(new Attribute("extends",
				"BaseResultMap"));
		
	    String returnType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            returnType = introspectedTable.getBaseRecordType();
        } else {
            returnType = introspectedTable.getPrimaryKeyType();
        }
		
		newResultMapElement.addAttribute(new Attribute("type",returnType));

		newResultMapElement
				.addElement(new TextElement(
						"<!--\r\n<association property=\"\" column=\"\" javaType=\"\">\r\n"
								+ "   <id column=\"\" property=\"\" jdbcType=\"\" />\r\n"
								+ "   <result column=\"\" property=\"\" jdbcType=\"\" />\r\n"
								+ " </association>\r\n-->\r\n"));

		parentElement.addElement(newResultMapElement);
		
		
		//以下代码用于生成支持分页的sql片段
		/**
		 *注意千万不要随意删除代码中的空格 ！
		 *do not remove spaces 
		 */

		StringBuffer sb=new StringBuffer();
		
		String fqjt = introspectedTable.getExampleType();

		
		//以下生成自定义查询条件sql
		XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", //$NON-NLS-1$
				"selectPage"));
		answer.addAttribute(new Attribute(
				"resultMap","selectPageResult")); //$NON-NLS-1$
		answer.addAttribute(new Attribute("parameterType", fqjt)); //$NON-NLS-1$

		
		sb.setLength(0);
		sb.append("select a.* from ");
		sb.append(introspectedTable
				.getAliasedFullyQualifiedTableNameAtRuntime());

		sb.append(" a  ");
		sb.append(" <if test=\"_parameter != null\" >    <include refid=\"Example_Where_Clause\" />   </if> ");
		sb.append(" <if test=\"orderByClause != null\" >      order by ${orderByClause}    </if>");
		sb.append(" limit ${skipRecordCount}, ${pageSize} ");

		answer.addElement(new TextElement(sb.toString()));
		parentElement.addElement(answer);


		List<IntrospectedColumn> pks=introspectedTable.getPrimaryKeyColumns();
		
		//以下为取最大主键字段值sql
		if (pks.size()==1){
			XmlElement selectMaxPrimaryKeyElement = new XmlElement("select"); //$NON-NLS-1$
			selectMaxPrimaryKeyElement.addAttribute(new Attribute("id", //$NON-NLS-1$
				"selectMaxPrimaryKeyValue"));
		
			selectMaxPrimaryKeyElement.addAttribute(new Attribute("resultType",pks.get(0).getFullyQualifiedJavaType().getFullyQualifiedName())); //$NON-NLS-1$
			selectMaxPrimaryKeyElement.addElement(new TextElement("select max("+MyBatis3FormattingUtilities.getAliasedEscapedColumnName(pks.get(0))+") from "+introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
			parentElement.addElement(selectMaxPrimaryKeyElement);
		}

		return true;
	}

}
