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
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.context.ContextRootCollection;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyCleaning;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to clean both class and instance taxonomies (removed nodes which
 * super-nodes or types need to be recomputed)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalTaxonomyCleaningStage extends AbstractReasonerStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IncrementalTaxonomyCleaningStage.class);

	private TaxonomyCleaning cleaning_ = null;

	public IncrementalTaxonomyCleaningStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CLEANING.toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		final Collection<IndexedClassEntity> modifiedEntities = new IndexedClassEntityCollection(
				new ContextRootCollection(
						reasoner.saturationState.getNotSaturatedContexts()));
		final Collection<IndexedClassEntity> removedClasses = new IndexedClassEntityCollection(
				reasoner.classTaxonomyState.getRemovedClasses());
		final Collection<IndexedClassEntity> removedIndividuals = new IndexedClassEntityCollection(
				reasoner.instanceTaxonomyState.getRemovedIndividuals());
		@SuppressWarnings("unchecked")
		Collection<IndexedClassEntity> inputs = Operations.getCollection(
				Operations.concat(modifiedEntities,
						Operations.concat(removedClasses, removedIndividuals)),
				modifiedEntities.size() + removedClasses.size()
						+ removedIndividuals.size());

		LOGGER_.trace("{}: entities with modified contexts", modifiedEntities);
		LOGGER_.trace("{}: removed classes", removedClasses);
		LOGGER_.trace("{}: removed individuals", removedIndividuals);

		cleaning_ = new TaxonomyCleaning(inputs, reasoner.classTaxonomyState,
				reasoner.instanceTaxonomyState, reasoner.getProcessExecutor(),
				workerNo, progressMonitor);

		return true;
	}

	@Override
	public void executeStage() throws ElkException {

		if (reasoner.classTaxonomyState.getTaxonomy() == null) {
			// nothing to do
			return;
		}
		cleaning_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		// at this point we're done with unsaturated contexts
		markAllContextsAsSaturated();
		reasoner.classTaxonomyState.getWriter().clearRemovedClasses();
		reasoner.instanceTaxonomyState.getWriter().clearRemovedIndividuals();
		this.cleaning_ = null;

		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}

	@Override
	public void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(cleaning_, flag);
	}

	/*
	 * Used to pass a collection of context's roots without extra copying
	 */
	private static class IndexedClassEntityCollection extends
			AbstractCollection<IndexedClassEntity> {

		private final Collection<? extends IndexedContextRoot> roots_;

		IndexedClassEntityCollection(
				Collection<? extends IndexedContextRoot> roots) {
			roots_ = roots;
		}

		@Override
		public Iterator<IndexedClassEntity> iterator() {
			return new Iterator<IndexedClassEntity>() {

				private IndexedClassEntity curr_ = null;
				private final Iterator<? extends IndexedContextRoot> iter_ = roots_
						.iterator();

				@Override
				public boolean hasNext() {
					if (curr_ != null) {
						return true;
					}
					while (curr_ == null && iter_.hasNext()) {
						IndexedContextRoot expr = iter_.next();

						if (expr instanceof IndexedClassEntity) {
							curr_ = (IndexedClassEntity) expr;
						}
					}

					return curr_ != null;
				}

				@Override
				public IndexedClassEntity next() {
					IndexedClassEntity tmp = curr_;

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
			return roots_.size();
		}
	}
}
