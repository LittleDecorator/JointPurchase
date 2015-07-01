/*
package purchase.plugins;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

public class ValueOfPlugin extends PluginAdapter implements Interceptor{

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method valueOf = new Method("valueOf");
        valueOf.setStatic(true);
        valueOf.addJavaDocLine("// valueOf instance from json string");
        valueOf.setVisibility(JavaVisibility.PUBLIC);
        valueOf.setReturnType(topLevelClass.getType());
        Parameter parameter = new Parameter(FullyQualifiedJavaType.getStringInstance(), "json");
        valueOf.addParameter(parameter);

        StringBuilder body = new StringBuilder()//
                .append("try {")//
                .append("	return new com.fasterxml.jackson.databind.ObjectMapper().configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, "
                        + topLevelClass.getType().getShortName() + ".class);")//
                .append("} catch (java.io.IOException e) {")//
                .append("	throw new RuntimeException(e);")//
                .append("}");
        valueOf.addBodyLine(body.toString());

        topLevelClass.addMethod(valueOf);
        return super.modelBaseRecordClassGenerated(topLevelClass,
                introspectedTable);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("intercept");
        return null;
    }

    @Override
    public Object plugin(Object target) {
        System.out.println("plugin");
        return null;
    }
}
*/
