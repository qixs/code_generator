package com.qxs.base.util.code.compiler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In-memory compile Java source code as String.
 * 
 * @author michael
 */
public class JavaStringCompiler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaStringCompiler.class);

	private static final List<String> OPTIONS = new ArrayList<>();

	private JavaCompiler compiler;
	private StandardJavaFileManager stdManager;

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler == null) {
			LOGGER.error("未找到tools.jar文件，可能是因为当前运行时环境是JRE，请先复制tools.jar文件到当前运行时环境下，tools.jar文件地址：JAVA_HOME/lib/tools.jar，复制的地址：JRE_HOME/lib/tools.jar");
			System.exit(0);
		}
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "Test.java"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> compile(String fileName, String source) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			StringWriter stringWriter = new StringWriter();

			CompilationTask task = compiler.getTask(stringWriter, manager, null, OPTIONS, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException(stringWriter.toString());
			}
			return manager.getClassBytes();
		}
	}

	/**
	 * Load class from compiled classes.
	 * 
	 * @param name
	 *            Full class name.
	 * @param classBytes
	 *            Compiled results as a Map.
	 * @return The Class instance.
	 * @throws ClassNotFoundException
	 *             If class not found.
	 * @throws IOException
	 *             If load error.
	 */
	@SuppressWarnings("resource")
	public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
		MemoryClassLoader classLoader = new MemoryClassLoader(classBytes);
		Set<Map.Entry<String, byte[]>> entries = classBytes.entrySet();
		for(Map.Entry<String, byte[]> entry : entries) {
			try {
				Class<?> clazz = classLoader.loadClass(entry.getKey());
				if(name.equals(entry.getKey())) {
					return clazz;
				}
			}catch(ClassFormatError e) {
				throw new RuntimeException("加载插件“" + name + "”失败，可能是插件class文件被修改");
			}
			
		}
		return null;
	}

	public synchronized static final void addOptions(String optionName, String optionValue){
		OPTIONS.add(optionName);
		OPTIONS.add(optionValue);
	}
}
