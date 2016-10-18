/*
 * #%L
 * ELK OWL API Binding
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
/**
 * 
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.reasoner.FailingReasonerInterrupter;
import org.semanticweb.elk.reasoner.stages.ReasonerInterrupter;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * A collection of utility methods to be used in OWL API related tests
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class OWLAPITestUtils {

	public static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode,
			final ReasonerInterrupter interrupter,
			final ReasonerStageExecutor stageExecutor) {
		return new ElkReasoner(ontology, isBufferingMode, interrupter,
				stageExecutor);
	}

	/**
	 * Created a reasoner that fails on interrupt.
	 * 
	 * @param ontology
	 * @param isBufferingMode
	 * @param stageExecutor
	 * @return
	 */
	public static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode,
			final ReasonerStageExecutor stageExecutor) {
		return createReasoner(ontology, isBufferingMode,
				FailingReasonerInterrupter.INSTANCE, stageExecutor);
	}

	/**
	 * Created a reasoner that fails on interrupt.
	 * 
	 * @param ontology
	 * @return
	 */
	public static ElkReasoner createReasoner(final OWLOntology ontology) {
		return createReasoner(ontology, false,
				FailingReasonerInterrupter.INSTANCE, new SimpleStageExecutor());
	}
	
	public static ElkProver createProver(OWLOntology ontology) {
		return new ElkProver(createReasoner(ontology));
	}

}
