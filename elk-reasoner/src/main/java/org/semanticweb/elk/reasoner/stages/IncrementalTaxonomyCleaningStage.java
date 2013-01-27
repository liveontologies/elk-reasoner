/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyCleaning;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalTaxonomyCleaningStage extends AbstractReasonerStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalTaxonomyCleaningStage.class);

	private ClassTaxonomyCleaning cleaning_ = null;

	public IncrementalTaxonomyCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState
				.getStageStatus(IncrementalStages.TAXONOMY_CLEANING);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new IncrementalConsistencyCheckingStage(
						reasoner));
	}

	@Override
	public void execute() throws ElkException {

		if (reasoner.classTaxonomyState.taxonomy == null) {
			// perhaps an inconsistency has been detected?
			// exit
			return;
		}

		if (cleaning_ == null) {
			initComputation();
		}

		progressMonitor.start(getName());

		try {
			for (;;) {
				cleaning_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}

		reasoner.incrementalState.setStageStatus(
				IncrementalStages.TAXONOMY_CLEANING, true);
		// at this point we're done with unsaturated contexts
		reasoner.saturationState.getWriter(ConclusionVisitor.DUMMY).clearNotSaturatedContexts();
	}

	@Override
	void initComputation() {
		super.initComputation();

		final Collection<IndexedClass> modified = new ContextRootCollection(
				reasoner.saturationState.getNotSaturatedContexts());
		final Collection<IndexedClass> removed = new ContextRootCollection(
				reasoner.saturationState.getContextsToBeRemoved());
		Collection<IndexedClass> inputs = Operations.getCollection(
				Operations.concat(removed, modified),
				removed.size() + modified.size());
		

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Taxonomy nodes to be cleaned for modified contexts: " + modified);
			LOGGER_.trace("Taxonomy nodes to be cleaned for removed contexts: " + removed);
		}

		cleaning_ = new ClassTaxonomyCleaning(inputs,
				reasoner.classTaxonomyState, reasoner.getProcessExecutor(),
				workerNo, progressMonitor);
	}

	@Override
	public void printInfo() {
	}

	/*
	 * Used to pass a collection of context's roots without extra copying
	 */
	private static class ContextRootCollection extends
			AbstractCollection<IndexedClass> {

		private final Collection<IndexedClassExpression> ices_;

		ContextRootCollection(Collection<IndexedClassExpression> ices) {
			ices_ = ices;
		}

		@Override
		public Iterator<IndexedClass> iterator() {
			return new Iterator<IndexedClass>() {

				private IndexedClass curr_ = null;
				private final Iterator<IndexedClassExpression> iter_ = ices_
						.iterator();

				@Override
				public boolean hasNext() {
					if (curr_ != null) {
						return true;
					} else {
						while (curr_ == null && iter_.hasNext()) {
							IndexedClassExpression expr = iter_.next();

							if (expr instanceof IndexedClass) {
								curr_ = ((IndexedClass) expr);//.getElkClass();
							}
						}
					}

					return curr_ != null;
				}

				@Override
				public IndexedClass next() {
					IndexedClass tmp = curr_;

					curr_ = null;

					return tmp;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			// upper bound, the actual size may be smaller since some contexts'
			// roots could be complex expressions
			return ices_.size();
		}
	}
}
