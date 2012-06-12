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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionEngine;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/*
 * TODO: current implementation does not support equivalent individuals,
 * i.e. assumes that all individual nodes are singletons
 */

/**
 * The engine for constructing of the {@link Taxonomy}. The jobs are submitted
 * using the method {@link #submit(IndexedClass)}, which require the computation
 * of the {@link Node} for the input {@link IndexedClass}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class TaxonomyComputationEngine implements
		InputProcessor<IndexedClassEntity> {

	/**
	 * The transitive reduction engine used in the taxonomy construction
	 */
	protected final TransitiveReductionEngine<IndexedClassEntity, TransitiveReductionJob<IndexedClassEntity>> transitiveReductionEngine;

	public TaxonomyComputationEngine(TaxonomyComputationShared shared) {
		this.transitiveReductionEngine = new TransitiveReductionEngine<IndexedClassEntity, TransitiveReductionJob<IndexedClassEntity>>(
				shared.transitiveReductionShared);
	}

	@Override
	public final void submit(IndexedClassEntity job) {
		transitiveReductionEngine
				.submit(new TransitiveReductionJob<IndexedClassEntity>(job));
	}

	@Override
	public final void process() throws InterruptedException {
		transitiveReductionEngine.process();
	}

	@Override
	public boolean canProcess() {
		return transitiveReductionEngine.canProcess();
	}	

}
