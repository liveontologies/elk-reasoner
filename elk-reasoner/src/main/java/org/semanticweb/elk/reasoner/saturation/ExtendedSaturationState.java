/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationState extends SaturationState {

	public Collection<IndexedClassExpression> getNotSaturatedContexts();

	/**
	 * Creates a new {@link ExtendedSaturationStateWriter} for modifying this
	 * {@link SaturationState} associated with the given
	 * {@link ContextCreationListener}. If {@link ContextCreationListener} is
	 * not thread safe, the calls of the methods should be synchronized
	 * 
	 * The passed rule application visitor is used to apply initialization rules
	 * to the newly created contexts
	 * 
	 */
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated);
	
	/**
	 * Just as the previous method but allows for using a specific
	 * {@link ConclusionFactory}.
	 * 
	 * @param contextCreationListener
	 * @param contextModificationListener
	 * @param ruleAppVisitor
	 * @param conclusionVisitor
	 * @param conclusionFactory
	 * @param trackNewContextsAsUnsaturated
	 * @return
	 */
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			ConclusionFactory conclusionFactory,
			boolean trackNewContextsAsUnsaturated);
	/**
	 * TODO
	 * @param contextModificationListener
	 * @param conclusionVisitor
	 * @return
	 */
	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?, Context> conclusionVisitor);
}
