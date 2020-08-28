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
import java.util.List;

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
			instanceTaxonomyMonitor_, queryMonitorForOntologySatisfiability_;

	public IncompletenessManager(OccurrenceManager occurrencesInOntology) {
		this.occurrencesInOntology_ = occurrencesInOntology;

		ontologySatisfiabilityMonitor_ = new IncompletenessStatusMonitor(
				new TopIncompletenessMonitor(occurrencesInOntology),
				"Ontology satisfiability cannot be checked! See log level INFO for details.",
				"Ontology satisfiability can now be checked!");

		List<IncompletenessMonitor> objectPropertyTaxonomyMonitors = new ArrayList<>();
		objectPropertyTaxonomyMonitors.add(new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Object property inclusions may be incomplete because ontology satisfiability cannot be checked!",
				null));
		objectPropertyTaxonomyMonitor_ = new IncompletenessStatusMonitor(
				Incompleteness.combine(ObjectPropertyTaxonomyIncompleteness
						.appendMonitors(objectPropertyTaxonomyMonitors,
								occurrencesInOntology)),
				"Object property inclusions may be incomplete!",
				"Object property inclusions are now complete!");

		classTaxonomyMonitor_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Class inclusions may be incomplete because ontology satisfiability cannot be checked!",
				"Class inclusions are now complete!");

		instanceTaxonomyMonitor_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Instance relations may be incomplete because ontology satisfiability cannot be checked!",
				"Instance relations are now complete!");

		queryMonitorForOntologySatisfiability_ = new IncompletenessStatusMonitor(
				ontologySatisfiabilityMonitor_,
				"Query answers may be incomplete because ontology satisfiability cannot be checked!",
				null);

	}

	// TODO: append warn messages to each incompleteness monitor

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
		List<IncompletenessMonitor> monitors = new ArrayList<>();
		monitors.add(queryMonitorForOntologySatisfiability_);
		monitors.add(new UnsupportedQueryTypeIncompletenessMonitor(
				occurencesInQuery));
		monitors.add(new TopIncompletenessMonitor(new CombinedOccurrenceManager(
				occurrencesInOntology_, occurencesInQuery)));
		return new IncompletenessStatusMonitor(Incompleteness.combine(monitors),
				"Query answers may be incomplete!",
				"Query answers are now complete!");
	}

}
