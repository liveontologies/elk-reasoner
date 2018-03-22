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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionProcessor;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Skocovsky
 */
public class ClassQueryIndexingProcessor
		implements ElkClassExpressionProcessor {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassQueryIndexingProcessor.class);

	private final ElkPolarityExpressionConverter indexer_;

	private final int increment_; // deletion < 0, addition > 0

	private final OccurrenceListener occurrenceTracker_;

	public ClassQueryIndexingProcessor(
			final ElkPolarityExpressionConverter indexer, final int increment,
			final OccurrenceListener indexingListener) {
		this.indexer_ = indexer;
		this.increment_ = increment;
		this.occurrenceTracker_ = indexingListener;
	}

	@Override
	public void visit(ElkClassExpression elkClassExpression) {
		try {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("$$ indexing {} for {}",
						OwlFunctionalStylePrinter.toString(elkClassExpression),
						(increment_ > 0 ? "addition" : "deletion"));
			}
			elkClassExpression.accept(indexer_);
		} catch (final ElkIndexingUnsupportedException e) {
			occurrenceTracker_.occurrenceChanged(
					Feature.OCCURRENCE_OF_UNSUPPORTED_EXPRESSION,
					increment_);
		}
	}

}
