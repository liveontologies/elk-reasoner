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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

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
	 * Name of the method(s) that InferenceRule objects use for applying an
	 * inference rule to an incoming queueable.
	 */
	protected final static String nameRuleMethod = "apply";
	/**
	 * Required parameter types of the rule method in InferenceRule objects.
	 * Note that generic type information is irrelevant here (it will not be
	 * checked, but the use of compatible generic types is ensured by the
	 * InferenceSystem that stores the rules).
	 */
	protected final static Class<?>[] parameterTypesRuleMethod = {
			Queueable.class, Context.class, RuleApplicationFactory.Engine.class };
	/**
	 * Name of the method that InferenceRule objects use for initializing a
	 * context. These methods implement inference rules without premises.
	 */
	protected final static String nameInitMethod = "init";
	/**
	 * Required parameter types of the init method in InferenceRule objects.
	 * Note that generic type information is irrelevant here (it will not be
	 * checked, but the use of compatible generic types is ensured by the
	 * InferenceSystem that stores the rules).
	 */
	protected final static Class<?>[] parameterTypesInitMethod = {
			Context.class, RuleApplicationFactory.Engine.class };
	/**
	 * Name of the method that Queueable objects use for storing their date in a
	 * given context. This is also part of the required interface of Queueable,
	 * since storing is not optional (as opposed to applying rules of any kind).
	 */
	protected final static String nameStoreMethod = "storeInContext";
	/**
	 * Required parameter types of the store method in Queueable objects. A
	 * generic type used for Context can be more specific than this.
	 */
	protected final static Class<?>[] parameterTypesStoreMethod = {
			Context.class, RuleApplicationFactory.Engine.class };

	/**
	 * RuleApplicationEngine that owns this object. Rule applications need to
	 * know this for enqueueing new derivations.
	 */
	// protected final RuleApplicationEngine engine;

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

		public void invoke(Queueable<?> argument, Context context,
				RuleApplicationFactory.Engine engine)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			firstMethod.invoke(firstInferenceRule, argument, context, engine);
			if (rest != null) {
				rest.invoke(argument, context, engine);
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

		public void invoke(Context context, RuleApplicationFactory.Engine engine)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			firstMethod.invoke(firstInferenceRule, context, engine);
			if (rest != null) {
				rest.invoke(context, engine);
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
	protected ConcurrentHashMap<Class<?>, InferenceMethods> methodsForQueueable = new ConcurrentHashMap<Class<?>, InferenceMethods>(
			10, (float) 0.50, 4); // parameters are rough guesses, no major
									// impact on performance observed so far

	/**
	 * List of methods that should be called to initialize new contexts.
	 */
	protected InitMethodList initMethods;

	/**
	 * Constructor.
	 */
	public InferenceSystemInvocationManager() {
	}

	/**
	 * Register a new inference system. Each inference rule of the system is
	 * processed to extract methods relevant for reasoning.</p>
	 * 
	 * <p>
	 * An IllegalRuleMethodException is thrown if a method that is relevant for
	 * applying rules (based on the name of the method) does not have the
	 * correct declaration to be used as intended.
	 * 
	 * There is only one public method for registering rules to ensure that all
	 * rules use a compatible generic type of context, without requiring this
	 * class to check it. Note that method invocation via Java Reflexion does
	 * not need to know the type of context either.
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
	}

	/**
	 * Register a single inference rule. The rule object is scanned for methods
	 * that are relevant in reasoning (which are found based on their names),
	 * and pointers to these methods are stored in internal data structures
	 * according to their signature (so that they can be called without further
	 * type checks to process inputs).</p>
	 * 
	 * <p>
	 * An IllegalRuleMethodException is thrown if a method that is relevant for
	 * applying rules (based on the name of the method) does not have the
	 * correct declaration to be used as intended.
	 * 
	 * @param inferenceRule
	 * @throws IllegalInferenceMethodException
	 * @throws NoSuchMethodException
	 */
	protected void addInferenceRule(InferenceRule<?> inferenceRule)
			throws IllegalInferenceMethodException, NoSuchMethodException {
		Class<?> ruleClass = inferenceRule.getClass();

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Registering inference rule: " + ruleClass.toString());
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
	protected void registerRuleMethod(InferenceRule<?> inferenceRule,
			Method ruleMethod, Class<?> queueableClass)
			throws IllegalInferenceMethodException, NoSuchMethodException {

		checkMethodSignature(ruleMethod, nameRuleMethod,
				parameterTypesRuleMethod);

		if (!methodsForQueueable.containsKey(queueableClass)) {
			initializeMethodsForClass(queueableClass);
		}
		registerRuleMethodForClass(inferenceRule, ruleMethod, queueableClass);
	}

	/**
	 * Register an init method. It is first checked whether the method is of
	 * correct format and modifiers, and an exception is thrown if this check
	 * fails.
	 * 
	 * @param inferenceRule
	 * @param initMethod
	 * @throws IllegalInferenceMethodException
	 */
	protected void registerInitMethod(InferenceRule<?> inferenceRule,
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
	protected static void checkMethodSignature(Method method,
			String methodName, Class<?>[] parameterTypes)
			throws IllegalInferenceMethodException {
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
		methodsForQueueable.put(clazz, getInitialMethodsForClass(clazz));
	}

	protected InferenceMethods getInitialMethodsForClass(Class<?> clazz)
			throws NoSuchMethodException, IllegalInferenceMethodException {
		InferenceMethods result;
		if (methodsForQueueable.containsKey(clazz)) {
			result = methodsForQueueable.get(clazz);
		} else {
			result = new InferenceMethods();
		}

		try {
			result.storeMethod = clazz.getMethod(nameStoreMethod,
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
					result.ruleMethods = addRuleMethod(
							keyrules.firstInferenceRule, keyrules.firstMethod,
							result.ruleMethods);
					keyrules = keyrules.rest;
				}
			}
		}

		return result;
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

		for (Class<?> keyclass : methodsForQueueable.keySet()) {
			if (clazz.isAssignableFrom(keyclass)) {
				InferenceMethods inferenceMethods = methodsForQueueable
						.get(keyclass);
				inferenceMethods.ruleMethods = addRuleMethod(inferenceRule,
						ruleMethod, inferenceMethods.ruleMethods);
			}
		}
	}

	/**
	 * Add a new method at the beginning of the list and return the resulting
	 * linked list. The input list can be null (if empty).
	 * 
	 * @param inferenceRule
	 * @param ruleMethod
	 * @return the resulting linked list
	 */
	protected RuleMethodList addRuleMethod(InferenceRule<?> inferenceRule,
			Method ruleMethod, RuleMethodList ruleMethodList) {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Registering rule method " + ruleMethod.toString());
		}
		return new RuleMethodList(inferenceRule, ruleMethod, ruleMethodList);
	}

	/**
	 * Add a new method at the beginning of the list and return the resulting
	 * linked list. The input list can be {@code null} (if empty).
	 * 
	 * @param inferenceRule
	 * @param initMethod
	 */
	protected void addInitMethod(InferenceRule<?> inferenceRule,
			Method initMethod) {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Registering init method " + initMethod.toString());
		}
		initMethods = new InitMethodList(inferenceRule, initMethod, initMethods);
	}

	/**
	 * Complete information for a class of queueables that was only encoutered
	 * during reasoning. This needs to be synchronized since the data that is
	 * added in principle also depends on the current registration information.
	 * So not only the actual update (write) but also the computation of the
	 * value that is written must be sychronized.
	 * 
	 * @param clazz
	 * @throws RuntimeException
	 *             whenever the initialization failed
	 */
	synchronized protected void lateInitializeMethodsForClass(Class<?> clazz) {
		if (methodsForQueueable.containsKey(clazz))
			return; // someone else did it while we waited for the
					// synchronization
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Late initialization of methods for class: "
					+ clazz.toString());
		}
		try {
			methodsForQueueable.put(clazz, getInitialMethodsForClass(clazz));
		} catch (NoSuchMethodException e) {
			// Re-throw as runtime exception
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalInferenceMethodException e) {
			// Re-throw as runtime exception
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Execute all known initialization rules for the given context.
	 * Initialization rules are rules that do not require premises that are
	 * derived first. There should always be initialization rules (otherwise
	 * nothing would ever be derived): a {@link RuntimeException} will be thrown
	 * if no rules are given to alert the user of this problem.
	 * 
	 * @param context
	 * @param engine
	 * @throws IllegalArgumentException
	 */
	public void initContext(Context context,
			RuleApplicationFactory.Engine engine)
			throws IllegalArgumentException {
		if (initMethods == null) {
			throw new RuntimeException(
					"Incomplete inference system: no initialization rules have been provided; nothing will ever be derived.");
		}
		try {
			initMethods.invoke(context, engine);
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

	/**
	 * Store an item in a context and apply inference rules to this new
	 * information if necessary. The method first tries to store the given
	 * queueable item. Only if this was necessary (i.e., if it was not known
	 * before), then all inference rule methods registered for this queueable
	 * are called.</p>
	 * 
	 * <p>
	 * If the class of queueable has not been encountered yet, a late
	 * registration is performed. This can cause runtime exceptions if the
	 * queueable does not provide a suitable method for storing it.
	 * 
	 * @param queueable
	 * @param context
	 * @param engine
	 * 
	 * @throws IllegalArgumentException
	 *             Can potentially happen since the use of reflection implicitly
	 *             casts the arguments to the required input types; this could
	 *             fail. This would happen if the context is not of the type
	 *             that is required by some inference rule. Since both context
	 *             type and inference rule type is controlled by the same
	 *             generic in InferenceSystem, this is extremely unlikely to
	 *             ever happen. Nevertheless, we declare it here for clarity.
	 */
	public void processItemInContext(Queueable<?> queueable, Context context,
			RuleApplicationFactory.Engine engine)
			throws IllegalArgumentException {
		Class<?> clazz = queueable.getClass();
		InferenceMethods inferenceMethods = methodsForQueueable.get(clazz);
		if (inferenceMethods == null) {
			lateInitializeMethodsForClass(clazz);
			inferenceMethods = methodsForQueueable.get(clazz);
			assert (inferenceMethods != null); // exception thrown otherwise
		}

		try {
			if (Boolean.TRUE.equals(inferenceMethods.storeMethod.invoke(
					queueable, context, engine))) {
				if (inferenceMethods.ruleMethods != null) {
					inferenceMethods.ruleMethods.invoke(queueable, context,
							engine);
				}

				applyAdditionalMethodsToItem(queueable, context, engine);
			}
		} catch (IllegalAccessException e) {
			// Happens if VM security configuration prevents method call.
			// Wrap and throw as unchecked exception:
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			// This happens if one of the inference methods called through
			// reflection has thrown an exception. Throw it but don't
			// require others to check it (wrap into RuntimeException).
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * handle for applying other methods to the item in subclasses of this
	 * invocation manager without using reflection
	 */
	protected void applyAdditionalMethodsToItem(Queueable<?> queueable,
			Context context, RuleApplicationFactory.Engine engine) {
	}

}
