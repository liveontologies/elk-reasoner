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
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.model.IndexingListener;
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
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

	public static final String ADDITION = "addition", REMOVAL = "removal";

	private final ElkPolarityExpressionConverter indexer_;

	private final String type_;

	private final IndexingListener indexingListener_;

	public ClassQueryIndexingProcessor(
			final ElkPolarityExpressionConverter indexer, final String type,
			final IndexingListener indexingListener) {
		this.indexer_ = indexer;
		this.type_ = type;
		this.indexingListener_ = indexingListener;
	}

	@Override
	public void visit(ElkClassExpression elkClassExpression) {
		try {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("$$ indexing {} for {}",
						OwlFunctionalStylePrinter.toString(elkClassExpression),
						type_);
			}
			elkClassExpression.accept(indexer_);
		} catch (final ElkIndexingUnsupportedException e) {
			indexingListener_.onIndexing(
					Occurrence.OCCURRENCE_OF_UNSUPPORTED_EXPRESSION);
		}
	}

}
