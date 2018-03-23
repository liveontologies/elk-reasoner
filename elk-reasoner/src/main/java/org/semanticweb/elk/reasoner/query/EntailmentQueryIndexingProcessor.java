/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.query;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedFeature;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntailmentQueryIndexingProcessor extends
		DummyElkAxiomVisitor<IndexedEntailmentQuery<? extends Entailment>> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(EntailmentQueryIndexingProcessor.class);

	private final int increment_;

	private final EntailmentQueryConverter converter_;

	private final OccurrenceListener occurrenceTracker_;

	public EntailmentQueryIndexingProcessor(final ElkObject.Factory elkFactory,
			final ModifiableOntologyIndex index, final int increment,
			final OccurrenceListener indexingListener) {
		this.increment_ = increment;
		this.converter_ = new EntailmentQueryConverter(elkFactory, index,
				increment);
		this.occurrenceTracker_ = indexingListener;
	}

	@Override
	protected IndexedEntailmentQuery<? extends Entailment> defaultVisit(
			final ElkAxiom axiom) {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("$$ indexing {} for {}",
					OwlFunctionalStylePrinter.toString(axiom),
					increment_ > 0 ? "addition" : "removal");
		}
		try {
			return axiom.accept(converter_);
		} catch (final ElkIndexingUnsupportedFeature e) {
			occurrenceTracker_.occurrenceChanged(e.getFeature(), increment_);
			return null;
		}
	}

}
