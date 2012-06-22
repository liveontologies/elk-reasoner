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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Abstract base class for inference systems. An inference system defines the
 * rules and data structures that form the basis of saturation-based reasoning.
 * It is parameterized by a generic Context type that its rules are based on. It
 * maintains a list of inference rules that implement a saturation calculus. The
 * generic context parameter is used to check the compatibility of the set of
 * rules with the context at compile time.
 * 
 * @author Markus Kroetzsch
 * 
 * @param <C>
 */
public abstract class InferenceSystem<C extends Context> {

	protected List<InferenceRule<C>> inferenceRules = new ArrayList<InferenceRule<C>>();

	/**
	 * Add an inference rule to this inference system. This is typically done in
	 * the constructor of concrete inference systems.
	 * 
	 * @param inferenceRule
	 */
	public void add(InferenceRule<C> inferenceRule) {
		inferenceRules.add(inferenceRule);
	}

	/**
	 * Get the list of inference rules of this inference system.
	 * 
	 * @return
	 */
	public List<InferenceRule<C>> getInferenceRules() {
		return inferenceRules;
	}

	/**
	 * Create a new context for the given root expression.
	 * 
	 * @param root
	 * @return
	 */
	public abstract C createContext(IndexedClassExpression root);

	/**
	 * Temporary method to cast contexts that have been stored without
	 * specifying C back to C. This is mainly needed for indexed objects, since
	 * they do not depend on C but currently store contexts; this will be fixed
	 * by storing contexts elsewhere.
	 * 
	 * @note This method will vanish soon. Do not use it unless you really,
	 *       really must.
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public C castContext(Context context) {
		return (C) context;
	}
}
