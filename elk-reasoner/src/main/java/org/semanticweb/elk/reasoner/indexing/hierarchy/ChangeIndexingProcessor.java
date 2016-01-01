/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.logging.ElkMessage;

/**
 * Basically an adapter from {@link ElkAxiomIndexingVisitor} to
 * {@link ElkAxiomProcessor} specifically for classes which index axioms.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ChangeIndexingProcessor implements ElkAxiomProcessor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ChangeIndexingProcessor.class);

	private final ElkAxiomIndexingVisitor indexer_;

	public ChangeIndexingProcessor(ElkAxiomIndexingVisitor indexer) {
		indexer_ = indexer;
	}

	@Override
	public void visit(ElkAxiom elkAxiom) {
		try {
			elkAxiom.accept(indexer_);
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("indexing "
						+ OwlFunctionalStylePrinter.toString(elkAxiom)
						+ " for "
						+ (indexer_.getMultiplicity() == 1 ? "addition"
								: "deletion"));
		} catch (ElkIndexingUnsupportedException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn(
						ElkMessage.serialize("reasoner.indexing.axiomIgnored",
								e.getMessage() + " Axiom ignored:\n"
										+ OwlFunctionalStylePrinter
												.toString(elkAxiom)));
		}
	}
}
