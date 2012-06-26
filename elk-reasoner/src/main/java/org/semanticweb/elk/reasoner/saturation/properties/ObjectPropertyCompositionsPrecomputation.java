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
package org.semanticweb.elk.reasoner.saturation.properties;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.classes.RuleRoleComposition;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * Sets up multimaps for fast look-up of object property compositions to be used
 * in {@link RuleRoleComposition}.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ObjectPropertyCompositionsPrecomputation
		extends
		ReasonerComputation<IndexedPropertyChain, ObjectPropertyCompositionsPrecomputationFactory.Engine, ObjectPropertyCompositionsPrecomputationFactory> {

	protected ObjectPropertyCompositionsPrecomputation(
			ObjectPropertyCompositionsPrecomputationFactory inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		super(ontologyIndex.getIndexedPropertyChains(), ontologyIndex
				.getIndexedPropertyChainCount(), inputProcessorFactory,
				executor, maxWorkers, progressMonitor);
	}

	public ObjectPropertyCompositionsPrecomputation(
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		this(new ObjectPropertyCompositionsPrecomputationFactory(), executor,
				maxWorkers, progressMonitor, ontologyIndex);
	}

}