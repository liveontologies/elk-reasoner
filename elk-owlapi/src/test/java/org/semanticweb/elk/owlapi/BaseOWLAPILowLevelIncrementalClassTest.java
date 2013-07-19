package org.semanticweb.elk.owlapi;

/*
 * #%L
 * ELK OWL API Binding
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

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * Testing incremental reasoning after adding and removing the axiom, taking the
 * buffering mode of the reasoner into account.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class BaseOWLAPILowLevelIncrementalClassTest {

	/**
	 * The reasoner on which to perform the test
	 */
	OWLReasoner reasoner;
	/**
	 * The ontology with which to test
	 */
	OWLOntology ont;

	/**
	 * prepare the test data
	 */
	abstract void prepare();

	/**
	 * Performs addition of some axioms to the ontology
	 */
	abstract void add();

	/**
	 * Performs deletion of axioms to the ontology
	 */
	abstract void remove();

	/**
	 * Perform tests w.r.t. the ontology with the additional axioms
	 */
	abstract void testPresent();

	/**
	 * Perform tests w.r.t. the ontology without the additional axioms
	 */
	abstract void testAbsent();

	public void testReasoner() throws OWLOntologyCreationException {

		boolean bufferingMode = reasoner.getBufferingMode().equals(
				BufferingMode.BUFFERING);

		testPresent();
		remove();
		if (bufferingMode) {
			// the changes are still not yet taken into account
			testPresent();
		} else {
			// the changes are taken into account
			testAbsent();
		}

		// this should take into account the changes for reasoning queries
		reasoner.flush();

		testAbsent();

		add();
		reasoner.flush();
		remove();

		if (bufferingMode) {
			// the reasoner should reflect only changes until the last flush()
			testPresent();
		} else {
			// the reasoner should reflect all changes
			testAbsent();
		}

		reasoner.flush();

		testAbsent();

		// Terminate the reasoner.
		reasoner.dispose();

	}

	@Test
	public void testBufferingReasoner() throws OWLOntologyCreationException {
		prepare();
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		reasoner = reasonerFactory.createReasoner(ont);
		testReasoner();
	}

	@Test
	public void testNonBufferingReasoner() throws OWLOntologyCreationException {
		prepare();
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		reasoner = reasonerFactory.createNonBufferingReasoner(ont);
		testReasoner();
	}
}
