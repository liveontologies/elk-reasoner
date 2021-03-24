package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;

/**
 * An object that can be used to track of {@link IncompletenessMonitor}s for
 * different ontology reasoning tasks. Since explanations for incompleteness of
 * different reasoning tasks often overlap, this {@link IncompletenessManager}
 * effectively keeps track of which explanations have been already logged.
 * 
 * @author Yevgeny Kazakov
 */
public class IncompletenessManager {

	private final OccurrenceManager occurrencesInOntology_;

	private final IncompletenessMonitor ontologySatisfiabilityMonitor_,
			classTaxonomyMonitor_, objectPropertyTaxonomyMonitor_,
			instanceTaxonomyMonitor_, generalQueryMonitor_;

	public IncompletenessManager(OccurrenceManager occurrencesInOntology) {
		this.occurrencesInOntology_ = occurrencesInOntology;

		ontologySatisfiabilityMonitor_ = new IncompletenessStatusMonitor(
				new TopIncompletenessMonitor(occurrencesInOntology),
				"Ontology satisfiability cannot be checked! Enable INFO for details.");

		objectPropertyTaxonomyMonitor_ = Incompleteness.firstOf(
				new IncompletenessStatusMonitor(ontologySatisfiabilityMonitor_,
						"Object property inclusions may be incomplete because ontology satisfiability cannot be checked!"),
				new IncompletenessStatusMonitor(Incompleteness.firstOf(
						ObjectPropertyTaxonomyIncompleteness.appendMonitorsTo(
								new ArrayList<>(), occurrencesInOntology)),
						"Object property inclusions may be incomplete! Enable INFO for details."));

		classTaxonomyMonitor_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Class inclusions may be incomplete because ontology satisfiability cannot be checked!");

		instanceTaxonomyMonitor_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Instance relations may be incomplete because ontology satisfiability cannot be checked!");

		generalQueryMonitor_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Query answers may be incomplete because ontology satisfiability cannot be checked!");

	}

	public IncompletenessMonitor getOntologySatisfiabilityMonitor() {
		return ontologySatisfiabilityMonitor_;
	}

	public IncompletenessMonitor getClassTaxonomyMonitor() {
		return classTaxonomyMonitor_;
	}

	public IncompletenessMonitor getObjectPropertyTaxonomyMonitor() {
		return objectPropertyTaxonomyMonitor_;
	}

	public IncompletenessMonitor getInstanceTaxonomyMonitor() {
		return instanceTaxonomyMonitor_;
	}

	public IncompletenessMonitor getQueryMonitor(
			OccurrenceManager occurencesInQuery) {
		return Incompleteness.firstOf(generalQueryMonitor_,
				new IncompletenessStatusMonitor(
						Incompleteness.firstOf(
								new UnsupportedQueryTypeIncompletenessMonitor(
										occurencesInQuery),
								new TopIncompletenessMonitor(
										new CombinedOccurrenceManager(
												occurrencesInOntology_,
												occurencesInQuery))),
						"Query answers may be incomplete! Enable INFO for details."));
	}

}