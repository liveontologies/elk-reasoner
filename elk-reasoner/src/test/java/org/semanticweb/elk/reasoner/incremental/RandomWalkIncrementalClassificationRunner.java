/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.Operations;

/**
 * A reusable runner which can be used for both unit tests and benchmarking
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class RandomWalkIncrementalClassificationRunner<T> {

	private static final Logger LOGGER_ = Logger
			.getLogger(RandomWalkIncrementalClassificationRunner.class);

	private final int maxRounds_;

	private final int iterations_;

	public RandomWalkIncrementalClassificationRunner(int rounds, int iter) {
		maxRounds_ = rounds;
		iterations_ = iter;
	}
	
	public void run(final Reasoner reasoner,
			final OnOffVector<T> changingAxioms,
			final List<T> staticAxioms,
			final long seed
			) throws ElkException,
			InterruptedException, IOException {
		run(reasoner, changingAxioms, staticAxioms, seed, null);
	}

	public void run(final Reasoner reasoner,
			final OnOffVector<T> changingAxioms,
			final List<T> staticAxioms,
			final long seed,
			final RandomWalkTestHook hook) throws ElkException,
			InterruptedException, IOException {

		// for storing taxonomy hash history
		Deque<String> taxonomyHashHistory = new LinkedList<String>();

		reasoner.setIncrementalMode(true);
		
		final String originalTaxonomyHash = TaxonomyPrinter
				.getHashString(reasoner.getTaxonomyQuietly());

		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("Original taxonomy hash code: "
					+ originalTaxonomyHash);

		int changingAxiomsCount = changingAxioms.size();
		int rounds = getNumberOfRounds(changingAxiomsCount);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Running " + rounds + " rounds with " + iterations_
					+ " random changes");

		int changeSize = getInitialChangeSize(changingAxiomsCount);
		//this tracker is responsible for generating random changes
		IncrementalChangeTracker<T> tracker = new IncrementalChangeTracker<T>(changingAxioms, changeSize);

		for (int j = 0; j < rounds; j++) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Generating " + iterations_ + " changes of size: "
						+ changeSize);
			changingAxioms.setAllOn();

			taxonomyHashHistory.add(originalTaxonomyHash);
			for (int i = 0; i < iterations_; i++) {
				loadChanges(reasoner, tracker.generateNextChange());
				
				String taxonomyHash = TaxonomyPrinter
						.getHashString(reasoner.getTaxonomyQuietly());
				taxonomyHashHistory.add(taxonomyHash);
				
				if (LOGGER_.isDebugEnabled()) {
					LOGGER_.debug("Taxonomy hash code for round " + (j + 1)
							+ " iteration " + (i + 1) + ": " + taxonomyHash);
				}

				printCurrentAxioms(Operations.concat(changingAxioms.getOnElements(), staticAxioms));

				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("======= Current Taxonomy =======");
					printTaxonomy(reasoner.getTaxonomyQuietly());
				}
				
				/*if (hook != null) {
					hook.apply(reasoner, changingAxioms, staticAxioms);
				}*/
			}

			if (LOGGER_.isDebugEnabled()) {
				LOGGER_.debug("Checking the final taxonomy");
			}

			String finalTaxonomyHash = taxonomyHashHistory.pollLast();
			Reasoner standardReasoner = createReasoner(
					Operations.concat(changingAxioms.getOnElements(),
							staticAxioms));

			/*Reasoner standardReasoner = TestReasonerUtils.createTestReasoner(
					new SimpleStageExecutor(), 1);
			standardReasoner.setIncrementalMode(false);
			standardReasoner.registerOntologyLoader(new TestAxiomLoader(
					Operations.concat(changingAxioms.getOnElements(),
							staticAxioms)));
			standardReasoner
					.registerOntologyChangesLoader(new EmptyChangesLoader());
			standardReasoner.setIncrementalMode(false);*/
			String expectedTaxonomyHash = TaxonomyPrinter
					.getHashString(standardReasoner.getTaxonomyQuietly());

			try {
				assertEquals("Seed " + seed, expectedTaxonomyHash,
						finalTaxonomyHash);
			} catch (AssertionError e) {
				LOGGER_.debug("======= Current Taxonomy =======");
				printTaxonomy(reasoner.getTaxonomyQuietly());
				LOGGER_.debug("====== Expected Taxonomy =======");
				printTaxonomy(standardReasoner.getTaxonomyQuietly());
				standardReasoner.shutdown();
				throw e;
			}
			standardReasoner.shutdown();

			if (LOGGER_.isDebugEnabled()) {
				LOGGER_.debug("Reverting the changes");
			}
			
			for (;;) {
				IncrementalChange<T> change = tracker.getChangeHistory().pollLast();
				String expectedHash = taxonomyHashHistory.pollLast();
				
				if (change == null) {
					break;
				}
				
				revertChanges(reasoner, change);
				/*reasoner.registerOntologyChangesLoader(new ReversesChangeLoader(
						change));*/
				String taxonomyHash = TaxonomyPrinter
						.getHashString(reasoner.getTaxonomyQuietly());

				assertEquals("Seed " + seed, expectedHash, taxonomyHash);
			}
			
			// doubling the change size every round
			changeSize *= 2;
		}
	}

	protected abstract void revertChanges(Reasoner reasoner, IncrementalChange<T> change);

	protected abstract Reasoner createReasoner(Iterable<T> axioms);

	protected abstract void loadChanges(Reasoner reasoner, IncrementalChange<T> change);
	
	protected abstract void printAxiom(T axiom);

	private int getInitialChangeSize(int changingAxiomsCount) {
		// the changes size will double with every iteration;
		// we find a good starting size
		int result = changingAxiomsCount >> maxRounds_;
		if (result == 0)
			result = 1;
		return result;
	}

	private int getNumberOfRounds(int changingAxiomsCount) {
		// we perform a logarithmic number of rounds in the number
		// of changed axioms, unless it is larger than MAX_ROUND
		return Math.min(maxRounds_,
				2 * (31 - Integer.numberOfLeadingZeros(changingAxiomsCount)));
	}

	private void printCurrentAxioms(Iterable<T> axioms) {
		if (LOGGER_.isTraceEnabled()) {
			for (T axiom : axioms) {
				printAxiom(axiom);
			}
/*				*/
		}
	}

	private void printTaxonomy(Taxonomy<ElkClass> taxonomy) {
		try {
			Writer writer = new OutputStreamWriter(System.out);
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			writer.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
