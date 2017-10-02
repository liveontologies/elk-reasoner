/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.completeness;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

class IncompletenessDueToInStatedAxiomsMessageProvider
		implements IncompletenessMessageProvider {

	public static final int AT_MOST_N_OCCURRENCES_IN_MESSAGE = 3;

	public static final IncompletenessDueToInStatedAxiomsMessageProvider INSTANCE = new IncompletenessDueToInStatedAxiomsMessageProvider();

	private IncompletenessDueToInStatedAxiomsMessageProvider() {
		// Forbid instantiation.
	}

	@Override
	public StringBuilder printOccurrences(
			final Collection<? extends ElkObject> occursIn,
			final StringBuilder message) {
		message.append("occurrences:");
		int i = 0;
		for (final ElkObject elkObject : occursIn) {
			if (i >= AT_MOST_N_OCCURRENCES_IN_MESSAGE) {
				message.append("\n\t...");
				break;
			}
			message.append("\n\t")
					.append(OwlFunctionalStylePrinter.toString(elkObject));
			i++;
		}
		return message;
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToNegativeOccurrenceOfObjectComplementOfMonitor monitor) {
		return "ELK does not support negative occurrences of ObjectComplementOf.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToNegativeOccurrenceOfTopObjectPropertyMonitor monitor) {
		return "ELK does not support negative occurrences of TopObjectProperty.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToOccurrenceOfDataHasValueMonitor monitor) {
		return "ELK supports DataHasValue only partially.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToOccurrenceOfDisjointUnionMonitor monitor) {
		return "ELK supports DisjointUnion only partially.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToOccurrenceOfNominalMonitor monitor) {
		return "ELK supports ObjectOneOf only partially.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToOccurrenceOfUnsupportedExpressionMonitor monitor) {
		return "Axioms ignored.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToPositiveOccurrenceOfBottomObjectPropertyMonitor monitor) {
		return "ELK does not support positive occurrences of BottomObjectProperty.";
	}

	@Override
	public String visit(
			final LoggingIncompletenessDueToPositiveOccurrenceOfObjectUnionOfMonitor monitor) {
		return "ELK does not support positive occurrences of ObjectUnionOf or ObjectOneOf.";
	}

}
