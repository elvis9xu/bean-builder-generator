package com.xjd.maven.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
	Class clazz;
	String pkgName;
	String className;
	List<Property> properties;

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public static class Property {
		String name;
		Class type;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Class getType() {
			return type;
		}

		public void setType(Class type) {
			this.type = type;
		}

	}

	public static ClassInfo inspect(Class clazz) {
		ClassInfo clazzInfo = new ClassInfo();
		clazzInfo.setClazz(clazz);
		clazzInfo.setClassName(clazz.getSimpleName());
		String fullName = clazz.getName();
		int index = fullName.lastIndexOf('.');
		if (index != -1) {
			clazzInfo.setPkgName(fullName.substring(0, index));
		}

		List<Property> props = new ArrayList<Property>();
		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterTypes().length == 1
					&& void.class.equals(method.getReturnType())) {
				Property prop = new Property();
				prop.setName(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
				prop.setType(method.getParameterTypes()[0]);
				props.add(prop);
			}
		}

		clazzInfo.setProperties(props);
		return clazzInfo;
	}
}
