/**
 * 
 */
package org.semanticweb.elk.benchmark;

import java.lang.reflect.Constructor;

/**
 * A very simple factory for creating tasks via reflection
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaskFactory {

	public static Task create(String className, String[] params) {
		try {
			Class<?> clazz = Class.forName(className);
			Constructor<?> constructor = clazz.getConstructor(new Class<?>[] {String[].class});

			return (Task) constructor.newInstance(new Object[] {params});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
