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

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class IncrementalClassTaxonomyComputationStage extends
		ClassTaxonomyComputationStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalClassTaxonomyComputationStage.class);

	public IncrementalClassTaxonomyComputationStage(
			AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CONSTRUCTION.toString();
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays
				.asList((ReasonerStage) new IncrementalTaxonomyCleaningStage(
						reasoner));
	}
	
	

	@Override
	public void execute() throws ElkInterruptedException {
		super.execute();
		reasoner.incrementalState.diffIndex.clearSignatureChanges();
	}

	@Override
	void initComputation() {
		super.initComputation();

		final Collection<IndexedClass> indexedClasses = reasoner.ontologyIndex
				.getIndexedClasses();

		if (!reasoner.useIncrementalTaxonomy()) {
			reasoner.classTaxonomyState.taxonomy = null;
		}

		if (reasoner.classTaxonomyState.taxonomy == null) {

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Using non-incremental taxonomy");

			computation_ = new ClassTaxonomyComputation(Operations.split(
					indexedClasses, 128), reasoner.getProcessExecutor(),
					workerNo, progressMonitor, reasoner.saturationState);
		} else {

			/*Collection<IndexedClass> modified = new AbstractSet<IndexedClass>() {

				@Override
				public Iterator<IndexedClass> iterator() {
					return Operations.filter(
							reasoner.ontologyIndex.getIndexedClasses(),
							new Condition<IndexedClass>() {

								@Override
								public boolean holds(IndexedClass clazz) {
									UpdateableNode<ElkClass> node = reasoner.classTaxonomyState.taxonomy
											.getUpdateableNode(clazz
													.getElkClass());

									return node == null || node.isModified();
								}
							}).iterator();
				}

				@Override
				public int size() {
					// TODO: this is only an upper bound; calculate exactly
					return indexedClasses.size();
				}

			};*/
			//classes which correspond to changed nodes in the taxonomy
			//*plus* new classes
			final Iterable<ElkClass> modifiedClassesIter = Operations.concat(
					reasoner.classTaxonomyState.classesForModifiedNodes,
					reasoner.incrementalState.diffIndex.getAddedClasses());
			final IndexObjectConverter converter = reasoner.objectCache_
					.getIndexObjectConverter();

			//let's convert to indexed objects and filter out removed classes
			Collection<IndexedClass> modified = new AbstractSet<IndexedClass>() {

				@Override
				public Iterator<IndexedClass> iterator() {
					return new Iterator<IndexedClass>() {

						final Iterator<ElkClass> iter_ = modifiedClassesIter.iterator();
						IndexedClass curr_ = null;
						
						@Override
						public boolean hasNext() {
							if (curr_ != null) {
								return true;
							} else {
								while (curr_ == null && iter_.hasNext()) {
									ElkClass elkClass = iter_.next();
									IndexedClass indexedClass = (IndexedClass) elkClass.accept(converter);
									
									if (indexedClass.occurs()) {
										curr_ = indexedClass;
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
					// TODO: this is only an upper bound; calculate exactly
					return reasoner.classTaxonomyState.classesForModifiedNodes
							.size()
							+ reasoner.incrementalState.diffIndex
									.getAddedClasses().size();
				}

			};
			
			//System.out.println(reasoner.incrementalState.diffIndex.getAddedClasses());
			//System.out.println(reasoner.classTaxonomyState.classesForModifiedNodes);
			//System.out.println(modified);

			computation_ = new ClassTaxonomyComputation(Operations.split(
					modified, 64), reasoner.getProcessExecutor(), workerNo,
					progressMonitor, reasoner.saturationState, reasoner.classTaxonomyState.taxonomy);
		}

	}

}
