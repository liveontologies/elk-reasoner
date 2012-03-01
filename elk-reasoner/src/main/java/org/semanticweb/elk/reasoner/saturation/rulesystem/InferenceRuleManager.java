/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.saturation.rulesystem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.elkrulesystem.NegativeSuperClassExpression;

/**
 * Class for managing the application of inference rules and related methods
 * that are specific to a particular inference system. Java Reflection is used
 * to find and manage methods that are provided by input objects.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class InferenceRuleManager {

	protected final static Logger LOGGER_ = Logger
			.getLogger(InferenceRuleManager.class);

	static AtomicInteger debugProcessedQueueables = new AtomicInteger(0);
	static AtomicInteger debugRuleApplications = new AtomicInteger(0);

	protected RuleApplicationEngine engine;

	protected class LinkedListOfMethods {
		// UnaryRule<? extends Queueable> first;
		Method firstMethod;
		InferenceRule<?> firstInferenceRule;
		LinkedListOfMethods rest;

		public LinkedListOfMethods(InferenceRule<?> firstInferenceRule,
				Method firstMethod, LinkedListOfMethods rest) {
			this.firstInferenceRule = firstInferenceRule;
			this.firstMethod = firstMethod;
			this.rest = rest;
		}

		public void applyToQueueable(Queueable<?> argument, Context context)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			// debugRuleApplications.incrementAndGet();
			firstMethod.invoke(firstInferenceRule, argument, context, engine);
			if (rest != null) {
				rest.applyToQueueable(argument, context);
			}
		}

	}

	HashMap<Class<?>, LinkedListOfMethods> ruleMethods = new HashMap<Class<?>, LinkedListOfMethods>();
	HashMap<Class<?>, Method> storeMethods = new HashMap<Class<?>, Method>();

	public InferenceRuleManager(RuleApplicationEngine engine) {
		this.engine = engine;
	}

	public void addInferenceSystem(InferenceSystem<?> inferenceSystem)
			throws IllegalRuleMethodException {
		for (InferenceRule<?> inferenceRule : inferenceSystem
				.getInferenceRules()) {
			addInferenceRule(inferenceRule);
		}
		initializeRuleMethodsForClass(NegativeSuperClassExpression.class);
	}

	public void addInferenceRule(InferenceRule<?> inferenceRule)
			throws IllegalRuleMethodException {
		Class<?> ruleClass = inferenceRule.getClass();

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering inference rule: " + ruleClass.toString());
		}

		Method[] methods = ruleClass.getMethods();
		for (Method method : methods) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (method.getName().equals("apply")) {
				registerRuleMethod(inferenceRule, method, paramTypes[0]);
			}
		}
	}

	/**
	 * Register a rule method. Finds out which subclass of Queueable the rule
	 * applies to and registers it under this key. It is checked whether the
	 * method is of correct format and modifiers.
	 * 
	 * @param ruleMethod
	 * @throws IllegalRuleMethodException
	 */
	public void registerRuleMethod(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> queueableClass)
			throws IllegalRuleMethodException {
		Class<?>[] paramTypes = ruleMethod.getParameterTypes();
		if (!ruleMethod.getName().equals("apply")) {
			throw new IllegalRuleMethodException(ruleMethod,
					"Rule methods must be called 'apply'.");
		}
		// if (Modifier.isStatic(ruleMethod.getModifiers())) {
		// throw new IllegalRuleMethodException(ruleMethod,
		// "Rule methods must not be static.");
		// }
		if (paramTypes.length != 3
				|| !Context.class.isAssignableFrom(paramTypes[1])
				|| !RuleApplicationEngine.class.isAssignableFrom(paramTypes[2])) {
			throw new IllegalRuleMethodException(
					ruleMethod,
					"Rule methods must accept three parameters: a queueable, a Context, and a RuleApplicationEngine.");
		}

		if (!ruleMethods.containsKey(queueableClass)) {
			initializeRuleMethodsForClass(queueableClass);
		}
		registerRuleMethodForClass(inferenceRule, ruleMethod, queueableClass);
	}

	/**
	 * Initialize the rule methods for a new class key (usually a subclass of
	 * Queueable). A previously registered rule method is copied to the new
	 * class key if it was registered for a superclass of this class.
	 * 
	 * @param clazz
	 */
	protected void initializeRuleMethodsForClass(Class<?> clazz) {
		if (!ruleMethods.containsKey(clazz)) {
			ruleMethods.put(clazz, null); // add entry to avoid structural
											// modification during iteration
		}
		Class<?>[] parameterTypes = new Class<?>[1];
		parameterTypes[0] = Context.class;
		try {
			storeMethods.put(clazz,
					clazz.getMethod("storeInContext", parameterTypes));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Class<?> keyclass : ruleMethods.keySet()) {
			if (!keyclass.equals(clazz) && keyclass.isAssignableFrom(clazz)) {
				LinkedListOfMethods keyrules = ruleMethods.get(keyclass);
				while (keyrules != null) {
					addRuleMethodForClass(keyrules.firstInferenceRule,
							keyrules.firstMethod, clazz);
					keyrules = keyrules.rest;
				}
			}
		}
	}

	/**
	 * Register an apply() method for the given class key (usually a subclass of
	 * Queueable). The new rule will be registered under all keys that are a
	 * subclass of the given class. The method is expected to have the correct
	 * format; no further checks are performed at this stage.
	 * 
	 * @param ruleMethod
	 * @param clazz
	 */
	protected void registerRuleMethodForClass(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> clazz) {
		for (Class<?> keyclass : ruleMethods.keySet()) {
			if (clazz.isAssignableFrom(keyclass) && !clazz.equals(keyclass)) {
				addRuleMethodForClass(inferenceRule, ruleMethod, keyclass);
			}
		}
		addRuleMethodForClass(inferenceRule, ruleMethod, clazz);
	}

	/**
	 * Add a single apply() method for the given class. This method manages the
	 * linked list structure of registered rules but does not check if the given
	 * method would also be relevant for other classes or if it even has the
	 * right format.
	 * 
	 * @param ruleMethod
	 * @param clazz
	 */
	protected void addRuleMethodForClass(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> clazz) {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering rule method for "
					+ clazz.getSimpleName() + ": " + ruleMethod.toString());
		}
		LinkedListOfMethods listedRule = new LinkedListOfMethods(inferenceRule,
				ruleMethod, ruleMethods.get(clazz));
		ruleMethods.put(clazz, listedRule);
	}

	void processItemInContext(Queueable<?> queueable, Context context) {
		Class<?> clazz = queueable.getClass();
		Method storeMethod = storeMethods.get(clazz);
		if (storeMethod != null) {
			try {
				if (Boolean.TRUE.equals(storeMethod.invoke(queueable, context))) {
					LinkedListOfMethods ruleMethodList = ruleMethods.get(clazz);
					if (ruleMethodList == null) {
						// synchronized (unaryRules) {
						// initializeUnaryRulesForClass(clazz);
						// }
						// ruleList = unaryRules.get(clazz);
						// if (ruleList == null) // give up
						return;
						// System.out.println("Initialized for " +
						// clazz.toString());
					}
					ruleMethodList.applyToQueueable(queueable, context);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// return false;
	}

	boolean storeItemInContext(Queueable<?> queueable, Context context) {
		Class<?> clazz = queueable.getClass();
		Method storeMethod = storeMethods.get(clazz);
		if (storeMethod != null) {
			try {
				return storeMethod.invoke(queueable, context).equals(
						Boolean.TRUE);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	void applyRuleToItemInContext(Queueable<?> queueable, Context context) {
		// debugProcessedQueueables.incrementAndGet();
		Class<?> clazz = queueable.getClass();
		LinkedListOfMethods ruleMethodList = ruleMethods.get(clazz);
		if (ruleMethodList == null) {
			// synchronized (unaryRules) {
			// initializeUnaryRulesForClass(clazz);
			// }
			// ruleList = unaryRules.get(clazz);
			// if (ruleList == null) // give up
			return;
			// System.out.println("Initialized for " + clazz.toString());
		}
		try {
			ruleMethodList.applyToQueueable(queueable, context);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
