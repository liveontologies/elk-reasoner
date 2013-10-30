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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A reusable runner which can be used for both unit tests and benchmarking
 * 
 * @param T Type of axioms (using the ELK API, the OWL API, or whatever) 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalClassificationRunner<T> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(RandomWalkIncrementalClassificationRunner.class);

	private final int maxRounds_;

	private final int iterations_;

	private final RandomWalkRunnerIO<T> io_;

	public RandomWalkIncrementalClassificationRunner(int rounds, int iter,
			RandomWalkRunnerIO<T> io) {
		maxRounds_ = rounds;
		iterations_ = iter;
		io_ = io;
	}

	public void run(final Reasoner reasoner,
			final OnOffVector<T> changingAxioms, final List<T> staticAxioms,
			final long seed) throws ElkException, InterruptedException,
			IOException {
		run(reasoner, changingAxioms, staticAxioms, seed, null);
	}

	public void run(final Reasoner reasoner,
			final OnOffVector<T> changingAxioms, final List<T> staticAxioms,
			final long seed, final RandomWalkTestHook hook)
			throws ElkException, InterruptedException, IOException {

		// for storing taxonomy hash history
		Deque<String> resultHashHistory = new LinkedList<String>();

		reasoner.setAllowIncrementalMode(true);

		final String originalTaxonomyHash = getResultHash(reasoner);
		int changingAxiomsCount = changingAxioms.size();
		int rounds = getNumberOfRounds(changingAxiomsCount);

		LOGGER_.info("Running {} rounds with {} random changes", iterations_, rounds);

		int changeSize = getInitialChangeSize(changingAxiomsCount);
		// this tracker is responsible for generating random changes
		IncrementalChangeTracker<T> tracker = new IncrementalChangeTracker<T>(
				changingAxioms, changeSize);

		for (int j = 0; j < rounds; j++) {
			LOGGER_.info("Generating {} changes of size: {}", iterations_, changeSize);

			changingAxioms.setAllOn();

			resultHashHistory.add(originalTaxonomyHash);

			for (int i = 0; i < iterations_; i++) {
				IncrementalChange<T> change = tracker.generateNextChange();

				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("Change for round " + (j + 1) + " iteration " + (i + 1));

					LOGGER_.trace("Deleted axioms");
					printCurrentAxioms(change.getDeletions(), LogLevel.TRACE);
					LOGGER_.trace("Added axioms");
					printCurrentAxioms(change.getAdditions(), LogLevel.TRACE);
				}
				
				io_.loadChanges(reasoner, change);

				final String resultHash = getResultHash(reasoner);

				resultHashHistory.add(resultHash);

				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("Taxonomy hash code for round " + (j + 1)
							+ " iteration " + (i + 1) + ": " + resultHash);
					LOGGER_.trace("Current axioms");
					printCurrentAxioms(Operations.concat(
							changingAxioms.getOnElements(), staticAxioms),
							LogLevel.DEBUG);
					
					printResult(reasoner, LOGGER_, LogLevel.TRACE);
				}
			}

			LOGGER_.trace("Checking the final result");

			String finalResultHash = resultHashHistory.pollLast();
			Reasoner standardReasoner = io_.createReasoner(Operations.concat(
					changingAxioms.getOnElements(), staticAxioms));

			final String expectedResultHash = getResultHash(standardReasoner);
			
			if (!expectedResultHash.equals(finalResultHash)){
				StringWriter writer = new StringWriter();
				
				try {
					writer.write("EXPECTED TAXONOMY:\n");
					printResult(standardReasoner, writer);
					writer.write("\nINCREMENTAL TAXONOMY:\n");
					printResult(reasoner, writer);
					writer.flush();
				} catch (IOException ioe) {	}
				
				fail("Seed: " + seed + "\n" + writer.getBuffer().toString());
			}

			standardReasoner.shutdown();

			LOGGER_.trace("Reverting the changes");

			for (;;) {
				IncrementalChange<T> change = tracker.getChangeHistory()
						.pollLast();
				String expectedHash = resultHashHistory.pollLast();

				if (change == null) {
					break;
				}

				io_.revertChanges(reasoner, change);
				
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("Reverting the next change");
					LOGGER_.trace("Adding back:");
					printCurrentAxioms(change.getDeletions(), LogLevel.TRACE);
					LOGGER_.trace("Deleting:");
					printCurrentAxioms(change.getAdditions(), LogLevel.TRACE);
				}				

				String taxonomyHash = getResultHash(reasoner);

				try {
					assertEquals("Seed " + seed, expectedHash, taxonomyHash);
				} catch (AssertionError e) {
					// TODO print the taxonomies here?
					printResult(reasoner, LOGGER_, LogLevel.ERROR);

					throw e;
				}
			}

			// doubling the change size every round
			changeSize *= 2;
		}
	}

	/*
	 * The next methods should be overridden in subclasses which perform other
	 * reasoning tasks
	 */
	protected void printResult(Reasoner reasoner, Logger logger, LogLevel level)
			throws IOException, ElkException {
		StringWriter writer = new StringWriter();
		
		printResult(reasoner, writer);
		LoggerWrap.log(logger, level, "CLASS TAXONOMY");
		LoggerWrap.log(logger, level, writer.getBuffer().toString());

		writer.close();
	}
	
	protected void printResult(Reasoner reasoner, Writer writer)
			throws IOException, ElkException {
		Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();

		TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
		writer.flush();
	}

	protected String getResultHash(Reasoner reasoner) throws ElkException {
		return TaxonomyPrinter.getHashString(reasoner.getTaxonomyQuietly());
	}

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

	private void printCurrentAxioms(Iterable<T> axioms, LogLevel level) {
		if (LoggerWrap.isEnabledFor(LOGGER_, level)) {
			for (T axiom : axioms) {
				io_.printAxiom(axiom, LOGGER_, level);
			}
		}
	}

}
