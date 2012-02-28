package org.semanticweb.elk.reasoner.saturation.rules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.saturation.expressions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.Queueable;

public class InferenceRuleManager {
	
	static AtomicInteger debugProcessedQueueables = new AtomicInteger(0);
	static AtomicInteger debugRuleApplications = new AtomicInteger(0);
	
	protected RuleApplicationEngine engine;

	protected class LinkedListOfRules {
		UnaryRule<? extends Queueable> first;
		LinkedListOfRules rest;

		public LinkedListOfRules(UnaryRule<? extends Queueable> first,
				LinkedListOfRules rest) {
			this.first = first;
			this.rest = rest;
		}

		public void applyToQueueable(Queueable argument, Context context) {
			//debugRuleApplications.incrementAndGet();
			first.applyToQueuable(argument, context);
			if (rest != null) {
				rest.applyToQueueable(argument, context);
			}
		}
		
	}

	HashMap<Class<?>, LinkedListOfRules> unaryRules = new HashMap<Class<?>, LinkedListOfRules>();
	
	public InferenceRuleManager(RuleApplicationEngine engine) {
		this.engine = engine;
	}
	
	public void addInferenceSystem(InferenceSystem inferenceSystem) {
		for (InferenceRule inferenceRule : inferenceSystem.getInferenceRules()) {
			addInferenceRule(inferenceRule);
		}
		initializeUnaryRulesForClass(NegativeSuperClassExpression.class);
	}

	public void addInferenceRule(InferenceRule inferenceRule) {
		Class<? extends InferenceRule> ruleClazz = inferenceRule.getClass();
		Class<?> unaryRuleClass = UnaryRule.class;
		
		System.out.println(" Inference rule " + ruleClazz.toString());
		
		Class<?>[] declaredClasses = ruleClazz.getDeclaredClasses();
		for ( Class<?> declaredClass : declaredClasses ) {
			//System.out.println("  Checking rule " + declaredClass.toString() + " against " + unaryRuleClass.toString() );
			if (unaryRuleClass.isAssignableFrom(declaredClass)) {
				try {
					Class<?> partypes[] = new Class<?>[1];
			        partypes[0] = RuleApplicationEngine.class;
					Constructor<?> ruleConstructor = declaredClass.getConstructor(partypes);
			        Object arglist[] = new Object[1];
		            arglist[0] = engine;
					Object unaryRule = ruleConstructor.newInstance(arglist);
					if ( unaryRule instanceof UnaryRule<?>) {
						registerUnaryRule((UnaryRule<?>) unaryRule);
					}
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Register a unary rule. Finds out which subclass of Queueable the rule
	 * applies to and registers it under this key.
	 * 
	 * @param unaryRule
	 */
	void registerUnaryRule(UnaryRule<? extends Queueable> unaryRule) {
		Class<?> cls = unaryRule.getClass();
		System.out.println("  Registering rule " + cls.toString());
		Class<?> qaClass = null;
		for (Method method : cls.getMethods()) {
			if (method.getName().equals("apply")) {
				Class<?>[] parameters = method.getParameterTypes();
				if (parameters.length > 0) {
					qaClass = parameters[0];
					break;
				}
			}
		}
		if (qaClass != null) {
			if (!unaryRules.containsKey(qaClass)) {
				initializeUnaryRulesForClass(qaClass);
			}
			registerUnaryRuleForClass(unaryRule, qaClass);
		} else {
			System.out.println(" *** ERROR *** (no qaclass found)");
		}
	}

	/**
	 * Initialize the rules for a new class key (usually a subclass of
	 * Queueable). A registered unary rule is copied to the new class key if it
	 * was registered for a superclass of this class.
	 * 
	 * @param clazz
	 */
	protected void initializeUnaryRulesForClass(Class<?> clazz) {
		if (!unaryRules.containsKey(clazz)) {
			unaryRules.put(clazz, null); // add entry to avoid structural
											// modification during iteration
		}
		for (Class<?> keyclass : unaryRules.keySet()) {
			if (!keyclass.equals(clazz) && keyclass.isAssignableFrom(clazz)) {
				LinkedListOfRules keyrules = unaryRules.get(keyclass);
				while (keyrules != null) {
					addUnaryRuleForClass(keyrules.first, clazz);
					keyrules = keyrules.rest;
				}
			}
		}
	}

	/**
	 * Register a unary rule for the given class key (usually a subclass of
	 * Queueable). The new rule will be registered under all keys that are a
	 * subclass of the given class.
	 * 
	 * @param unaryRule
	 * @param clazz
	 */
	protected void registerUnaryRuleForClass(
			UnaryRule<? extends Queueable> unaryRule, Class<?> clazz) {
		for (Class<?> keyclass : unaryRules.keySet()) {
			if (clazz.isAssignableFrom(keyclass)) {
				addUnaryRuleForClass(unaryRule, keyclass);
			}
		}
		addUnaryRuleForClass(unaryRule, clazz);
	}

	/**
	 * Add a single unary rule for the given class. This method manages the
	 * linked list structure of registered rules but does not check if the given
	 * rule would also be relevant for other classes.
	 * 
	 * @param unaryRule
	 * @param clazz
	 */
	protected void addUnaryRuleForClass(
			UnaryRule<? extends Queueable> unaryRule, Class<?> clazz) {
		// System.out.println( "Adding rule " + unaryRule.getClass().getName() +
		// " under " + clazz.getSimpleName());
		LinkedListOfRules listedRule = new LinkedListOfRules(unaryRule,
				unaryRules.get(clazz));
		unaryRules.put(clazz, listedRule);
	}

	void applyRuleInContext(Queueable queueable, Context context) {
		//debugProcessedQueueables.incrementAndGet();
		Class<? extends Queueable> clazz = queueable.getClass();
		LinkedListOfRules ruleList = unaryRules.get(clazz);
//		if (ruleList == null) {
//			synchronized (unaryRules) {
//				initializeUnaryRulesForClass(clazz);
//			}
//			ruleList = unaryRules.get(clazz);
//			if (ruleList == null) // give up 
//				return;
//			System.out.println("Initialized for " + clazz.toString());
//		}
		ruleList.applyToQueueable(queueable, context);
	}
	
}
