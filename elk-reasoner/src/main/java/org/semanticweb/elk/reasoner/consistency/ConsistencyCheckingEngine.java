/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationEngine;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for checking consistency of an ontology by checking satisfiability
 * of all submitted class expressions. The jobs are submitted using the method
 * {@link #submit(IndexedClassExpressions)}.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 */
public class ConsistencyCheckingEngine implements
		InputProcessor<IndexedClassExpression> {

	protected final ConsistencyCheckingShared shared;

	protected final ClassExpressionSaturationEngine<SaturationJob<IndexedClassExpression>> saturationEngine;

	public ConsistencyCheckingEngine(ConsistencyCheckingShared shared) {
		this.shared = shared;
		this.saturationEngine = new ClassExpressionSaturationEngine<SaturationJob<IndexedClassExpression>>(
				shared.saturationShared);
	}

	@Override
	public final void submit(IndexedClassExpression job) {
		if (shared.isConsistent)
			saturationEngine.submit(new SaturationJob<IndexedClassExpression>(
					job));
	}

	@Override
	public final void process() throws InterruptedException {
		saturationEngine.process();
	}

	@Override
	public boolean canProcess() {
		return saturationEngine.canProcess();
	}

}
