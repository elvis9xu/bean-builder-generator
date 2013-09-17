package com.xjd.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *	自定义ClassLoader
 * </pre>
 * @author elvis.xu
 */
public class MyClassLoader extends ClassLoader {

	public MyClassLoader() {
		super();
	}

	public MyClassLoader(ClassLoader parent) {
		super(parent);
	}

	protected List<String> classPaths = new ArrayList<String>(3);

	public void addClassPath(String... classPath) {
		classPaths.addAll(Arrays.asList(classPath));
	}

	public void removeClassPath(String... classPath) {
		classPaths.removeAll(Arrays.asList(classPath));
	}

	public void clearClassPath() {
		classPaths.clear();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] bytes;
		try {
			bytes = loadClassData(name);
		} catch (IOException e) {
			throw new ClassNotFoundException("加载类出错: " + name, e);
		}
		try {
			return defineClass(name, bytes, 0, bytes.length, null);
		} catch (ClassFormatError e) {
			throw new ClassNotFoundException("", e);
		}
	}

	protected byte[] loadClassData(String name) throws IOException {
		File file = getFile(name);
		if (file == null) {
			throw new FileNotFoundException("在给定的Class Paths下无法找到找定的类文件!");
		}
		InputStream in = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new IOException("类文件太大, 无法加载!");
		}
		int remain = (int) length;
		byte[] bytes = new byte[remain];
		int index = 0, count = 0;
		while (remain > 0) {
			count = in.read(bytes, index, remain);
			if (count > 0) {
				index += count;
				remain -= count;
			} else if (count == -1) {
				throw new IOException("类文件读取时长度异常: expected: " + length + ", actual: " + (length - remain));
			}
		}
		return bytes;
	}

	protected File getFile(String name) {
		String namePath = name.replaceAll("\\.", "/") + ".class";

		File file = null;
		for (String classPath : classPaths) {
			File tmpFile = new File(classPath, namePath);
			if (tmpFile.isFile()) {
				file = tmpFile;
				break;
			}
		}

		return file;
	}

}
