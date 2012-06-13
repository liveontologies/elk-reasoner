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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.saturation.classes.RuleStatistics;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

public class TaxonomyComputationFactory implements
		InputProcessorFactory<IndexedClassEntity, TaxonomyComputationEngine> {

	private final TaxonomyComputationShared shared;

	public TaxonomyComputationFactory(TaxonomyComputationShared shared) {
		this.shared = shared;
	}

	@Override
	public TaxonomyComputationEngine createProcessor() {
		return new TaxonomyComputationEngine(shared, new RuleStatistics());
	}

	TaxonomyComputationShared getShared() {
		return this.shared;
	}

}
