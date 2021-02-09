/*
    Copyright (C) 2005 Dejvuth Suwimonteerabuth

    This file is part of jMoped.

    jMoped is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    jMoped is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jMoped; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package jmoped;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class PDSDefault {
	
	static Logger logger = Logger.getLogger(PDSDefault.class);
	
	public static final String LABEL_ASSERT_ERROR = "AssertError";
	public static final String LABEL_HEAP_OVERFLOW = "HeapOverflow";
	public static final String LABEL_NULL_POINTER_EXCEPTION = "NPE";
	public static final String LABEL_GENERAL_EXCEPTION = "Exception";
	public static final String LABEL_INDEX_OUT_OF_BOUNDS = "IndexOutOfBounds";
	public static final String LABEL_STRINGBUILDER_OVERFLOW = "StringBuilderOverflow";
	
	public static final String CONFIG_NAME;

	public static final String HEAP_NAME = "heap";
	public static final String HEAP_PTR_NAME = "heap_ptr";
	public static final int HEAP_SIZE = 7;
	public static final int BITS = 3;
	public static final String JMOPED_INIT = "jinit";
	public static final String LOCAL_VAR_NAME = "v";
	public static final String MOPED_PATH = "moped";
	public static final String STACK_NAME = "s";
	public static final int STRING_BUILDER_SIZE = 4;
	public static boolean CHECK_FOR_HEAP_OVERFLOW = true;
	
	public static boolean CHECK_FOR_NULL_POINTER_EXCEPTIONS = false;
	
	public static final boolean CHECK_FOR_INDEX_OUT_OF_BOUNDS = false;
	
	public static final boolean CHECK_FOR_STRINGBUILDER_OVERFLOW = true;
	
	public static final Map<String, List<String>> IGNORED_TABLE;
	
	static {
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> emptyList = Collections.emptyList();
		
		map.put("java/io/Serializable", emptyList);
		map.put("java/applet/Applet", emptyList);
		map.put("java/awt/Graphics", emptyList);
		map.put("java/awt/Canvas", emptyList);
		map.put("java/lang/AssertionError", emptyList);
		map.put("java/lang/Boolean", emptyList);
		map.put("java/lang/Class", emptyList);

		map.put("java/lang/Math", emptyList);
		map.put("java/lang/Throwable", emptyList);
		
		/*
		List objList = Arrays.asList("registerNatives()V", "clone()Ljava/lang/Object;",
				"finalize()V", "getClass()Ljava/lang/Class;", "hashCode()I", "notify()V",
				"notifyAll()V",  "toString()Ljava/lang/String;", "wait()V", "wait(J)V", 
				"wait(JI)V");
		*/
		
		map.put("java/lang/Object", emptyList);
		
		map.put("java/lang/String", emptyList);
		map.put("java/lang/StringBuffer", emptyList);
		map.put("java/lang/StringBuilder", emptyList);
		map.put("java/lang/System", emptyList);
		map.put("java/io/PrintStream", emptyList);
		map.put("com/vladium/emma/rt/RT", emptyList);
                /*added by suncong:*/
                map.put("java.util.Vector", emptyList);
                map.put("java.util.Hashtable", emptyList);
                map.put("java.util.Enumeration", emptyList);
                /*****************/
		// TODO I removed a few classes
		map.put("java/lang/Integer",  Arrays.asList("parseInt" + "(Ljava/lang/String;)I"));
		IGNORED_TABLE = Collections.unmodifiableMap(map);
	}
	
	public static final String[] IGNORED_FIELD = {
		"$assertionsDisabled",
		"class$"
	};
	
	public static final String[] IGNORED_FORMATTED_FIELD = {
		"java_lang_System_out",
		"java_lang_System_err"
	};
	
	public static final String JAVA_HOME;
	public static final String JMOPED_HOME;

	static {
		
		// Sets JAVA_HOME variable to where Java is.
		String javaHome = System.getenv("JAVA_HOME");
		if (javaHome != null) {
			JAVA_HOME = javaHome;
		} else {
			// System.getProperty("java.home") outputs e.g. /opt/jdk1.5.0_04/jre
			javaHome = System.getProperty("java.home");
			// But we want only /opt/jdk1.5.0_04
			JAVA_HOME = javaHome.substring(0, javaHome.length() - 4);
		}
		
		// Sets JMOPED_HOME variable to where jMoped is.
		String jMopedHome = System.getenv("JMOPED_HOME");
		if (jMopedHome != null) {
			JMOPED_HOME = jMopedHome;
		} else {
			logger.warn("JMOPED_HOME variable is not set."
					+ "The current directory is assumed.");
			logger.warn("Normally, this should be fine, "
					+ "but if something went wrong, "
					+ "please try setting JMOPED_HOME to where jMoped is.");
			String workingDir=System.getProperty("user.dir");
			File f=new File(workingDir+separator+"bin");
			if(f.exists() && f.isDirectory())
				JMOPED_HOME= workingDir +separator+"bin";
			else
				JMOPED_HOME = workingDir;
		}
		// jmoped.conf is assumed to be at JMOPED_HOME
		CONFIG_NAME = JMOPED_HOME + separator + "jmoped.conf";
                //modified by suncong on 2014,6,10, for eclipse project, jmoped.conf at bin's parent directory
                //CONFIG_NAME = JMOPED_HOME + separator + ".." + separator + "jmoped.conf";
	}
	
	private static final Class[] parameters = new Class[]{URL.class};
	 
	/**
	 * Adds the String s to classpath.
	 * 
	 * @param s a string.
	 * @throws IOException
	 */
	public static void addClasspath(String s) throws IOException {
		
		File f = new File(s);
		addClasspath(f);
	}
	
	/**
	 * Adds the File f to classpath.
	 * 
	 * @param f a File.
	 * @throws IOException
	 */
	public static void addClasspath(File f) throws IOException {
		
		addClasspath(f.toURL());
	}
	 
	/**
	 * Adds the URL u to classpath.
	 * 
	 * @param u a URL.
	 * @throws IOException
	 */
	public static void addClasspath(URL u) throws IOException {
	 
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
	 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	 
	}
	
	/**
	 * Returns the mininmum number of bits required to correctly index entries
	 * in the heap array.
	 * @param heapSize
	 * @return
	 */
	public static final int getMinimumBitsForHeapSize(int heapSize)
	{
		return (int) Math.ceil(Math.log(heapSize + 1)/Math.log(2.0));
	}
	
	/**
	 * Returns the maximum size of the heap that can still be indexed by the
	 * given number of bits.
	 */
	public static final int getMaximumHeapSizeForBits(int bits)
	{
		//System.out.println("AAAAAAA:"+bits);
		return (int)Math.pow(2, bits) - 1;
	}
}
