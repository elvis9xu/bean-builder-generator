package com.xjd.maven.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * <pre>
 * 
 * </pre>
 * @author elvis.xu
 * @goal generate
 */
public class JavabeanBuilderGenerator extends AbstractMojo {
	
	/**
	 * @parameter expression="${classes}"
	 * @required
	 */
	String classes;
	
	/**
	 * @parameter expression="${outputDirectory}" default-value="${project.build.testSourceDirectory}"
	 * @required
	 */
	String outputDirectory;
	
	/**
	 * @parameter expression="${overwrite}" default-value="false"
	 * @required
	 */
	String overwrite;
	
	/**
	 * @parameter expression="${classPaths}" default-value="${project.build.outputDirectory}"
	 * @required
	 */
	String classPaths;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		debug("classPaths=" + classPaths);
		debug("classes=" + classes);
		String[] classArray = splitStrings(classes);
		String[] classPathArray = splitStrings(classPaths);
		
		MyClassLoader myClassLoader = new MyClassLoader(this.getClass().getClassLoader());
		myClassLoader.addClassPath(classPathArray);
		
		Class[] clazzArray = new Class[classArray.length];
		for (int i = 0; i < classArray.length; i++) {
			try {
				clazzArray[i] = myClassLoader.loadClass(classArray[i]);
			} catch (ClassNotFoundException e) {
				throw new MojoExecutionException("类加载失败！", e);
			}
		}
		
		for (int i = 0; i < clazzArray.length; i++) {
			try {
				generateBuilder(clazzArray[i]);
			} catch (IOException e) {
				err("", e);
			}
		}
	}
	
	protected void generateBuilder(Class clazz) throws IOException {
		String clazzName = clazz.getName();
		String builderClazzName = clazzName + "Builder";
		File builderSourceFile = new File(outputDirectory, builderClazzName.replace('.', '/') + ".java");
		
		builderSourceFile.getParentFile().mkdirs();
		boolean noConflict = builderSourceFile.createNewFile();
		
		if (noConflict || (!noConflict && "true".equalsIgnoreCase(overwrite))) {
			FileWriter writer = new FileWriter(builderSourceFile);
			BuilderGenerator.generate(ClassInfo.inspect(clazz), writer);
			writer.close();
		}
	}
	
	protected String[] splitStrings(String strings) {
		String[] stringArray = strings.split("[\\s,]+");
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = stringArray[i].trim();
		}
		return stringArray;
	}

	protected void debug(String msg) {
		getLog().debug(generateLogHead() + msg);
	}
	
	protected void info(String msg) {
		getLog().info(generateLogHead() + msg);
	}
	
	protected void err(String msg) {
		getLog().error(generateLogHead() + msg);
	}
	
	protected void err(String msg, Throwable t) {
		getLog().error(generateLogHead() + msg, t);
	}
	
	protected String generateLogHead() {
		return dformat.format(new Date()) + " bean-builder-generator : ";
	}
	
	SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
