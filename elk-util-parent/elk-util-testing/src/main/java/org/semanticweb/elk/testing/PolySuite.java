/*
 * #%L
 * ELK Utilities for Testing
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * A generic implementation of a suite for creating custom collections
 * of test, assigning each test a meaningful name (not just 0,1,... as with 
 * Parameterized), and passing arbitrary parameters into each test.
 * 
 * The most common use case for ELK is to execute a collection of tests, e.g.,
 * hierarchy tests, based on a fixed set of test data files in a specific location.
 * Each test can be named according to the name of its data file.  
 * 
 * Based on the solution suggested in http://stackoverflow.com/questions/650894/change-test-name-of-parameterized-tests/3404257#3404257 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class PolySuite extends Suite {

	
  // //////////////////////////////
  // Public helper interfaces

  /**
   * Annotation for a method which returns a {@link Configuration}
   * to be injected into the test class constructor
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public static @interface Config {
  }

	public static interface Configuration {

		String getName();

		Collection<? extends TestManifest<?>> getManifests();

		Collection<? extends Configuration> getChildren();

		boolean isEmpty();

	}

	private final String name_;

  // //////////////////////////////
  // Constructor

  /**
   * Only called reflectively. Do not use programmatically.
   * @param c the test class
   * @throws Throwable if something bad happens
   */
  public PolySuite(Class<?> c) throws Throwable {
    super(c, Collections.<Runner>singletonList(new PolySuite(c, getConfiguration(c))));
    this.name_ = c.getName();
  }

	public PolySuite(final Class<?> klass, final Configuration configuration)
			throws Throwable {
		super(klass, config2Runners(klass, configuration));
		this.name_ = configuration.getName();
	}

	private static List<Runner> config2Runners(final Class<?> klass,
			final Configuration configuration) throws Throwable {
		final List<Runner> runners = new ArrayList<Runner>();
		for (final Configuration child : configuration.getChildren()) {
			runners.add(new PolySuite(klass, child));
		}
		int index = 0;
		for (final TestManifest<?> manifest : configuration.getManifests()) {
			runners.add(new SingleRunner(index++, klass, manifest, manifest.getName()));
		}
		return runners;
	}

	@Override
	protected String getName() {
		return name_;
	}

  // //////////////////////////////
  // Private

	private static Configuration getConfiguration(final Class<?> klass)
			throws Throwable {
		return getConfiguration(new TestClass(klass));
	}

  private static Configuration getConfiguration(TestClass testClass) throws Throwable {
    return (Configuration) getConfigMethod(testClass).invokeExplosively(null);
  }

  private static FrameworkMethod getConfigMethod(TestClass testClass) {
    List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Config.class);
    if (methods.isEmpty()) {
      throw new IllegalStateException("@" + Config.class.getSimpleName() + " method not found");
    }
    if (methods.size() > 1) {
      throw new IllegalStateException("Too many @" + Config.class.getSimpleName() + " methods");
    }
    FrameworkMethod method = methods.get(0);
    int modifiers = method.getMethod().getModifiers();
    if (!(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))) {
      throw new IllegalStateException("@" + Config.class.getSimpleName() + " method \"" + method.getName() + "\" must be public static");
    }
    return method;
  }
  
    	
  // //////////////////////////////
  // Helper classes

  private static class SingleRunner extends BlockJUnit4ClassRunner {

	static final private int NAME_LENGTH_LIMIT = 100;  
	private final int index;  
    private final Object testVal;
    private final String testName;

    SingleRunner(int index, Class<?> testClass, Object testVal, String testName) throws InitializationError {
      super(testClass);
      this.index = index;
      this.testVal = testVal;
      this.testName = testName;
    }

    @Override
    protected Object createTest() throws Exception {
      return getTestClass().getOnlyConstructor().newInstance(testVal);
    }

    @Override
	protected String getName() {
		/*
		 * Replacing the parentheses is a workaround, so that Eclipse displays
		 * the whole test name. Otherwise Eclipse cuts everything before the
		 * last '(' and after the last ')'.
		 */
		String name = testName.replace('(', '{').replace(')', '}');		
		return truncate(name);
	}

    @Override
    protected String testName(FrameworkMethod method) {
      return truncate(method.getName() + ": " + testName);
    }

	/** truncate long names, preserving inequality if possible */
	private String truncate(String name) {
		String suffix = " ..." + index;
		if (name.length() + suffix.length() > NAME_LENGTH_LIMIT) {			
			name = name.substring(0, NAME_LENGTH_LIMIT - suffix.length());
		}
		return name + suffix;
	}
    
    @Override
    protected void validateConstructor(List<Throwable> errors) {
      validateOnlyOneConstructor(errors);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
      return childrenInvoker(notifier);
    }
  }
}
