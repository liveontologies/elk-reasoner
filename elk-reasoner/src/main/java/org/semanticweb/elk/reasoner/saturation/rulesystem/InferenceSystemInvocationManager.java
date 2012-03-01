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
import java.util.Arrays;
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
public class InferenceSystemInvocationManager {

	protected final static Logger LOGGER_ = Logger
			.getLogger(InferenceSystemInvocationManager.class);

	/**
	 * Name of the method for applying an inference rule to an incoming
	 * queueable.
	 */
	protected final static String nameRuleMethod = "apply";
	protected final static Class<?>[] parameterTypesRuleMethod = {
			Queueable.class, Context.class, RuleApplicationEngine.class };
	protected final static String nameInitMethod = "init";
	protected final static Class<?>[] parameterTypesInitMethod = {
			Context.class, RuleApplicationEngine.class };
	protected final static String nameStoreMethod = "storeInContext";
	protected final static Class<?>[] parameterTypesStoreMethod = { Context.class };

	static AtomicInteger debugProcessedQueueables = new AtomicInteger(0);
	static AtomicInteger debugRuleApplications = new AtomicInteger(0);

	protected final RuleApplicationEngine engine;

	/**
	 * Simple linked list implementation for rule methods.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	protected class RuleMethodList {
		Method firstMethod;
		InferenceRule<?> firstInferenceRule;
		RuleMethodList rest;

		public RuleMethodList(InferenceRule<?> firstInferenceRule,
				Method firstMethod, RuleMethodList rest) {
			this.firstInferenceRule = firstInferenceRule;
			this.firstMethod = firstMethod;
			this.rest = rest;
		}

		public void invoke(Queueable<?> argument, Context context)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			// debugRuleApplications.incrementAndGet();
			firstMethod.invoke(firstInferenceRule, argument, context, engine);
			if (rest != null) {
				rest.invoke(argument, context);
			}
		}
	}

	/**
	 * Simple linked list implementation for init methods.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	protected class InitMethodList {
		Method firstMethod;
		InferenceRule<?> firstInferenceRule;
		InitMethodList rest;

		public InitMethodList(InferenceRule<?> firstInferenceRule,
				Method firstMethod, InitMethodList rest) {
			this.firstInferenceRule = firstInferenceRule;
			this.firstMethod = firstMethod;
			this.rest = rest;
		}

		public void invoke(Context context) throws IllegalArgumentException,
				IllegalAccessException, InvocationTargetException {
			firstMethod.invoke(firstInferenceRule, context, engine);
			if (rest != null) {
				rest.invoke(context);
			}
		}
	}

	/**
	 * Container for the information that is stored about each type queueable.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	protected class InferenceMethods {
		Method storeMethod;
		RuleMethodList ruleMethods;
	}

	/**
	 * Map from types of queueable objects to containers with all relevant
	 * inference methods.
	 */
	HashMap<Class<?>, InferenceMethods> methodsForQueueable = new HashMap<Class<?>, InferenceMethods>();

	/**
	 * List of methods that should be called to initialize new contexts.
	 */
	InitMethodList initMethods;

	/**
	 * Constructor. The object keeps a RuleApplicationEngine since this is
	 * needed during inferencing for enqueueing derived results and for storing
	 * processed queueables.
	 * 
	 * @param engine
	 */
	public InferenceSystemInvocationManager(RuleApplicationEngine engine) {
		this.engine = engine;
	}

	/**
	 * Register a new inference system. Each inference rule of the system is
	 * processed to extract methods relevant for reasoning.
	 * 
	 * An IllegalRuleMethodException is thrown if a method that is relevant for
	 * applying rules (based on the name of the method) does not have the
	 * correct declaration to be used as intended.
	 * 
	 * @param inferenceSystem
	 * @throws IllegalInferenceMethodException
	 * @throws NoSuchMethodException
	 */
	public void addInferenceSystem(InferenceSystem<?> inferenceSystem)
			throws IllegalInferenceMethodException, NoSuchMethodException {
		for (InferenceRule<?> inferenceRule : inferenceSystem
				.getInferenceRules()) {
			addInferenceRule(inferenceRule);
		}
		// TODO a better solution is needed for doing the following:
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering additional rules "
					+ NegativeSuperClassExpression.class.toString());
		}
		initializeMethodsForClass(NegativeSuperClassExpression.class);
	}

	/**
	 * Register a single inference rule. The rule object is scanned for methods
	 * that are relevant in reasoning (which are found based on their names),
	 * and pointers to these methods are stored in internal data structures
	 * according to their signature (so that they can be called without further
	 * type checks to process inputs).
	 * 
	 * An IllegalRuleMethodException is thrown if a method that is relevant for
	 * applying rules (based on the name of the method) does not have the
	 * correct declaration to be used as intended.
	 * 
	 * @param inferenceRule
	 * @throws IllegalInferenceMethodException
	 * @throws NoSuchMethodException
	 */
	public void addInferenceRule(InferenceRule<?> inferenceRule)
			throws IllegalInferenceMethodException, NoSuchMethodException {
		Class<?> ruleClass = inferenceRule.getClass();

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering inference rule: " + ruleClass.toString());
		}

		Method[] methods = ruleClass.getMethods();
		for (Method method : methods) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if (nameRuleMethod.equals(method.getName())) {
				registerRuleMethod(inferenceRule, method, paramTypes[0]);
			}
			if (nameInitMethod.equals(method.getName())) {
				registerInitMethod(inferenceRule, method);
			}
		}
	}

	/**
	 * Register a rule method. Finds out which subclass of Queueable the rule
	 * applies to and registers it under this key. It is checked whether the
	 * method is of correct format and modifiers, and an exception is thrown if
	 * this check fails.
	 * 
	 * @param ruleMethod
	 * @throws IllegalInferenceMethodException
	 * @throws NoSuchMethodException
	 */
	public void registerRuleMethod(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> queueableClass)
			throws IllegalInferenceMethodException, NoSuchMethodException {

		checkMethodSignature(ruleMethod, nameRuleMethod,
				parameterTypesRuleMethod);

		if (!methodsForQueueable.containsKey(queueableClass)) {
			initializeMethodsForClass(queueableClass);
		}
		registerRuleMethodForClass(inferenceRule, ruleMethod, queueableClass);
	}

	public void registerInitMethod(InferenceRule<?> inferenceRule,
			Method initMethod) throws IllegalInferenceMethodException {
		checkMethodSignature(initMethod, nameInitMethod,
				parameterTypesInitMethod);
		addInitMethod(inferenceRule, initMethod);
	}

	/**
	 * Check if the method has the required signature and throw an exception if
	 * otherwise.
	 * 
	 * @param method
	 * @param methodName
	 * @param parameterTypes
	 * @throws IllegalInferenceMethodException
	 */
	protected void checkMethodSignature(Method method, String methodName,
			Class<?>[] parameterTypes) throws IllegalInferenceMethodException {
		if (!methodName.equals(method.getName())) {
			throw new IllegalInferenceMethodException(method,
					"Method must be called '" + methodName + "'.");
		}
		Class<?>[] methodParamTypes = method.getParameterTypes();
		if (methodParamTypes.length != parameterTypes.length) {
			throw new IllegalInferenceMethodException(method,
					"Method must accept the following parameters: "
							+ Arrays.deepToString(parameterTypes));
		}
		for (int i = 0; i < parameterTypes.length; ++i) {
			if (!parameterTypes[i].isAssignableFrom(methodParamTypes[i]))
				throw new IllegalInferenceMethodException(method,
						"Method must accept the following parameters: "
								+ Arrays.deepToString(parameterTypes));

		}
	}

	/**
	 * Initialize the rule methods for a new class key. The class key
	 * corresponds to the first argument that the rule methods applies to (the
	 * type of item that it processes) and should be a subclass of Queueable.
	 * The initialization finds and registers the method for storing this
	 * queueable item in a context. Moreover, the queueable class is compared to
	 * all previously registered queueables: if any superclass of the queueable
	 * is known, then all rule methods for this superclass also apply to the
	 * given class; the rule methods are registered accordingly.
	 * 
	 * @param clazz
	 * @throws NoSuchMethodException
	 * @throws IllegalInferenceMethodException
	 */
	protected void initializeMethodsForClass(Class<?> clazz)
			throws NoSuchMethodException, IllegalInferenceMethodException {
		if (!methodsForQueueable.containsKey(clazz)) {
			methodsForQueueable.put(clazz, new InferenceMethods());
		}
		InferenceMethods classMethods = methodsForQueueable.get(clazz);

		try {
			classMethods.storeMethod = clazz.getMethod(nameStoreMethod,
					parameterTypesStoreMethod);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodException("The queueable item "
					+ clazz.getName() + " does not declare a method '"
					+ nameStoreMethod + "' with parameters "
					+ Arrays.deepToString(parameterTypesStoreMethod)
					+ " as needed for storing it in a context.");
		}

		for (Class<?> keyclass : methodsForQueueable.keySet()) {
			if (!keyclass.equals(clazz) && keyclass.isAssignableFrom(clazz)) {
				InferenceMethods keymethods = methodsForQueueable.get(keyclass);
				RuleMethodList keyrules = keymethods.ruleMethods;
				while (keyrules != null) {
					classMethods.ruleMethods = addRuleMethod(
							keyrules.firstInferenceRule, keyrules.firstMethod,
							classMethods.ruleMethods);
					keyrules = keyrules.rest;
				}
			}
		}
	}

	/**
	 * Register a rule method for the given class key (usually a subclass of
	 * Queueable). It is necessary that the methods for this class have been
	 * initialized first using initializeMethodsForClass(); otherwise a
	 * NullPointerException will occur.
	 * 
	 * The registered method will be registered under all keys that are a
	 * subclass of the given class. The method is expected to have the correct
	 * format; no further checks are performed at this stage.
	 * 
	 * @param ruleMethod
	 * @param clazz
	 */
	protected void registerRuleMethodForClass(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> clazz) {
		InferenceMethods inferenceMethods = methodsForQueueable.get(clazz);
		assert (inferenceMethods != null);
		for (Class<?> keyclass : methodsForQueueable.keySet()) {
			if (clazz.isAssignableFrom(keyclass) && !clazz.equals(keyclass)) {
				inferenceMethods.ruleMethods = addRuleMethod(inferenceRule,
						ruleMethod, inferenceMethods.ruleMethods);
			}
		}
		inferenceMethods.ruleMethods = addRuleMethod(inferenceRule,
				ruleMethod, inferenceMethods.ruleMethods);
	}

	/**
	 * Add a new method at the beginning of the list and return the resulting
	 * linked list. The input list can be null (if empty).
	 * 
	 * @param inferenceRule
	 * @param ruleMethod
	 * @return
	 */
	protected RuleMethodList addRuleMethod(InferenceRule<?> inferenceRule,
			Method ruleMethod, RuleMethodList ruleMethodList) {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering rule method " + ruleMethod.toString());
		}
		return new RuleMethodList(inferenceRule, ruleMethod, ruleMethodList);
	}

	/**
	 * Add a new method at the beginning of the list and return the resulting
	 * linked list. The input list can be null (if empty).
	 * 
	 * @param inferenceRule
	 * @param initMethod
	 * @return
	 */
	protected void addInitMethod(InferenceRule<?> inferenceRule,
			Method initMethod) {
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Registering init method " + initMethod.toString());
		}
		initMethods = new InitMethodList(inferenceRule, initMethod, initMethods);
	}

	public void initContext(Context context) throws IllegalArgumentException {
		if (initMethods != null) {
			try {
				initMethods.invoke(context);
			} catch (IllegalAccessException e) {
				// Happens if VM security configuration prevents method call.
				// Wrap and throw as unchecked exception:
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// This happens if one of the inference methods called through
				// reflection has thrown an exception. Throw it but don't
				// require
				// others to check it (wrap into RuntimeException).
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				} else {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Store an item in a context and apply inference rules to this new
	 * information if necessary. The method first tries to store the given
	 * queueable item. Only if this was necessary (i.e., if it was not known
	 * before), then all inference rule methods registered for this queueable
	 * are called.
	 * 
	 * @param queueable
	 * @param context
	 * 
	 * @throws IllegalArgumentException
	 *             Can potentially happen since the use of reflection implicitly
	 *             casts the arguments to the required input types; this could
	 *             fail. This would happen the context is not of the type that
	 *             is required by some inference rule. Since both context type
	 *             and inference rule type is controlled by the same generic in
	 *             InferenceSystem, this is extremely unlikely to ever happen.
	 *             Nevertheless, we declare it here for clarity.
	 */
	public void processItemInContext(Queueable<?> queueable, Context context)
			throws IllegalArgumentException {
		Class<?> clazz = queueable.getClass();
		InferenceMethods inferenceMethods = methodsForQueueable.get(clazz);
		if (inferenceMethods != null) {
			try {
				if (Boolean.TRUE.equals(inferenceMethods.storeMethod.invoke(
						queueable, context))) {
					if (inferenceMethods.ruleMethods == null) {
						// synchronized (unaryRules) {
						// initializeUnaryRulesForClass(clazz);
						// }
						// ruleList = unaryRules.get(clazz);
						// if (ruleList == null) // give up
						return;
						// System.out.println("Initialized for " +
						// clazz.toString());
					}
					inferenceMethods.ruleMethods.invoke(queueable, context);
				}
			} catch (IllegalAccessException e) {
				// Happens if VM security configuration prevents method call.
				// Wrap and throw as unchecked exception:
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// This happens if one of the inference methods called through
				// reflection has thrown an exception. Throw it but don't
				// require
				// others to check it (wrap into RuntimeException).
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				} else {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

}
