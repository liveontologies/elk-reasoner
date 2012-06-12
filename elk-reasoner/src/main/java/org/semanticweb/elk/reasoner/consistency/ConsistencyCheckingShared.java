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
package org.semanticweb.elk.reasoner.consistency;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationShared;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;

public class ConsistencyCheckingShared {

	/**
	 * The saturation engine used for saturating submitted indexed class
	 * expressions
	 */
	protected final ClassExpressionSaturationShared<SaturationJob<IndexedClassExpression>> saturationShared;
	/**
	 * The result of the computation. True iff all submitted class expressions
	 * are satisfiable.
	 */
	protected boolean isConsistent = true;

	/**
	 * Creates a new class taxonomy engine for the input ontology index and a
	 * listener for executing callback functions.
	 * 
	 * @param ontologyIndex
	 *            the ontology index for which the engine is created
	 */
	public ConsistencyCheckingShared(OntologyIndex ontologyIndex) {
		this.saturationShared = new ClassExpressionSaturationShared<SaturationJob<IndexedClassExpression>>(
				ontologyIndex, new ThisClassExpressionSaturationListener());
	}

	/**
	 * Print statistics about class taxonomy construction
	 */
	public void printStatistics() {
		saturationShared.printStatistics();
	}

	/**
	 * Returns whether all submitted class expressions are satisfiable
	 */
	public boolean isConsistent() {
		return isConsistent;
	}

	/**
	 * The listener class used for the class expression saturation engine, which
	 * is used within this consistency engine
	 * 
	 */
	class ThisClassExpressionSaturationListener
			implements
			ClassExpressionSaturationListener<SaturationJob<IndexedClassExpression>, ClassExpressionSaturationEngine<SaturationJob<IndexedClassExpression>>> {

		@Override
		public void notifyCanProcess() {
		}

		@Override
		public void notifyFinished(SaturationJob<IndexedClassExpression> job)
				throws InterruptedException {
			if (!((ContextClassSaturation) job.getOutput()).isSatisfiable())
				isConsistent = false;

		}

	}

}
