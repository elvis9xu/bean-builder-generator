package com.xjd.maven.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import com.xjd.maven.plugin.ClassInfo.Property;

public class BuilderGenerator {

	public static void generate(ClassInfo clazzInfo, Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(writer);
		//package
		if (clazzInfo.getPkgName() != null) {
			bw.write("package ");
			bw.write(clazzInfo.getPkgName());
			bw.write(";");
			bw.newLine();
		}
		
		bw.newLine();
		
		//imports
		for (Property property : clazzInfo.getProperties()) {
			String propTypeName = property.getType().getName();
			if (!propTypeName.startsWith("java.lang") && (clazzInfo.getPkgName() != null ? !propTypeName.startsWith(clazzInfo.getPkgName()) : true)) {
				bw.write("import ");
				bw.write(propTypeName);
				bw.write(";");
				bw.newLine();
			}
		}
		
		bw.newLine();
		
		String builderName = clazzInfo.getClassName() + "Builder";
		//class begin
		bw.write("public class ");
		bw.write(builderName);
		bw.write(" {");
		bw.newLine();
		
		//properties
		for (Property property : clazzInfo.getProperties()) {
			bw.write("\tprotected " + property.getType().getSimpleName() + " " + property.getName() + ";");
			bw.newLine();
		}
		
		bw.newLine();
		
		//methods
		for (Property property : clazzInfo.getProperties()) {
			bw.write("\t");
			bw.write("public ");
			bw.write(builderName);
			bw.write(" ");
			bw.write(property.getName());
			bw.write("(");
			bw.write(property.getType().getSimpleName());
			bw.write(" ");
			bw.write(property.getName());
			bw.write(") {");
			bw.newLine();
			bw.write("\t\tthis.");
			bw.write(property.getName());
			bw.write(" = ");
			bw.write(property.getName());
			bw.write(";");
			bw.newLine();
			bw.write("\t\treturn this;");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
		}
		
		//build method
		bw.write("\tpublic " + clazzInfo.getClassName() + " build() {");
		bw.newLine();
		bw.write("\t\t" + clazzInfo.getClassName() + " bean = new " + clazzInfo.getClassName() + "();");
		bw.newLine();
		for (Property property : clazzInfo.getProperties()) {
			bw.write("\t\tbean.set" + capitalize(property.getName()) + "(" + property.getName() + ");");
			bw.newLine();
		}
		bw.write("\t\treturn bean;");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		//class end
		bw.write("}");
		bw.flush();
		bw.close();
	}
	
	protected static String capitalize(String s) {
		if (s == null || "".equals(s)) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
