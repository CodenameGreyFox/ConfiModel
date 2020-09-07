package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * Class that compiles code at run-time
 * 
 * @author Tomé Neves
 * and https://gist.github.com/chrisvest/9873843
 *
 */

public class CodeCompiler {
	public CodeCompiler()  {


	}

	/**
	 * Compiles a method to evaluate birth rate and mortality rate based on the code provided in a String
	 * 
	 * @param code String The code that when executed gives the rates
	 * @param brmr String "br" if calculating birth rate, "mr" if mortality
	 */

	public Method compile(String code, String brmr) throws Exception {

		//So the user doesn't have to input ";"
		code = code.replace("}",";}")+";";		

		String program = "" +
				"public class Equation {\n" +
				"  public static double solve(Double[] s, Double[] v, double popSize) {\n" +		
				" double " + brmr + ";\n" +
				"try{" +
				code + "\n" +
				"} catch (ArrayIndexOutOfBoundsException e) { return -123456789; }\n" +
				" return " + brmr + ";" +
				"  }\n" +
				"}\n";

		//Work-a-round so that the user doesn't have to have a JDK installed in his system
		@SuppressWarnings("deprecation")
		JavaCompiler compiler = new com.sun.tools.javac.api.JavacTool();
		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		JavaFileObject compilationUnit =  new StringJavaFileObject("Equation", program);

		SimpleJavaFileManager fileManager =  new SimpleJavaFileManager(compiler.getStandardFileManager(null, null, null));

		JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, null, null, null, Arrays.asList(compilationUnit));

		compilationTask.call();

		CompiledClassLoader classLoader =  new CompiledClassLoader(fileManager.getGeneratedOutputFiles());

		Class<?> equation = classLoader.loadClass("Equation");

		Method main = equation.getMethod("solve", Double[].class, Double[].class, double.class);

		return main;

		//	main.invoke(null, new Object[]{new String[]{"isto"}});
	}

	private static class StringJavaFileObject extends SimpleJavaFileObject {
		private final String code;

		public StringJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
					Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return code;
		}
	}

	private static class ClassJavaFileObject extends SimpleJavaFileObject {
		private final ByteArrayOutputStream outputStream;
		private final String className;

		protected ClassJavaFileObject(String className, Kind kind) {
			super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
			this.className = className;
			outputStream = new ByteArrayOutputStream();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return outputStream;
		}

		public byte[] getBytes() {
			return outputStream.toByteArray();
		}

		public String getClassName() {
			return className;
		}
	}

	@SuppressWarnings("rawtypes")
	private static class SimpleJavaFileManager extends ForwardingJavaFileManager {
		private final List<ClassJavaFileObject> outputFiles;

		@SuppressWarnings("unchecked")
		protected SimpleJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
			outputFiles = new ArrayList<ClassJavaFileObject>();
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
			outputFiles.add(file);
			return file;
		}

		public List<ClassJavaFileObject> getGeneratedOutputFiles() {
			return outputFiles;
		}
	}

	private static class CompiledClassLoader extends ClassLoader {
		private final List<ClassJavaFileObject> files;

		private CompiledClassLoader(List<ClassJavaFileObject> files) {
			this.files = files;
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Iterator<ClassJavaFileObject> itr = files.iterator();
			while (itr.hasNext()) {
				ClassJavaFileObject file = itr.next();
				if (file.getClassName().equals(name)) {
					itr.remove();
					byte[] bytes = file.getBytes();
					return super.defineClass(name, bytes, 0, bytes.length);
				}
			}
			return super.findClass(name);
		}
	}
}
