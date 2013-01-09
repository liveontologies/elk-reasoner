/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
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
public class RandomWalkIncrementalClassificationRunner {

	private static final Logger LOGGER_ = Logger
			.getLogger(RandomWalkIncrementalClassificationRunner.class);

	private final int maxRounds_;

	private final int iterations_;

	public RandomWalkIncrementalClassificationRunner(int rounds, int iter) {
		maxRounds_ = rounds;
		iterations_ = iter;
	}
	
	public void run(final Reasoner reasoner,
			final OnOffVector<ElkAxiom> changingAxioms,
			final List<ElkAxiom> staticAxioms,
			final long seed
			) throws ElkException,
			InterruptedException, IOException {
		run(reasoner, changingAxioms, staticAxioms, seed, null);
	}

	public void run(final Reasoner reasoner,
			final OnOffVector<ElkAxiom> changingAxioms,
			final List<ElkAxiom> staticAxioms,
			final long seed,
			final RandomWalkTestHook hook) throws ElkException,
			InterruptedException, IOException {

		// for storing change history
		Deque<IncrementalChange> changesHistory = new LinkedList<IncrementalChange>();
		// for storing taxonomy hash history
		Deque<String> taxonomyHashHistory = new LinkedList<String>();

		reasoner.setIncrementalMode(true);
		TrackingChangesLoader.setSeed(seed);

		reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
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

		for (int j = 0; j < rounds; j++) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Generating " + iterations_ + " changes of size: "
						+ changeSize);
			changingAxioms.setAllOn();

			taxonomyHashHistory.add(originalTaxonomyHash);
			for (int i = 0; i < iterations_; i++) {
				//all random changes happen inside the tracking change loader
				reasoner.registerOntologyChangesLoader(new TrackingChangesLoader(
						changingAxioms, changesHistory, changeSize));
				String taxonomyHash = TaxonomyPrinter
						.getHashString(reasoner.getTaxonomyQuietly());
				taxonomyHashHistory.add(taxonomyHash);
				if (LOGGER_.isDebugEnabled())
					LOGGER_.debug("Taxonomy hash code for round " + (j + 1)
							+ " iteration " + (i + 1) + ": " + taxonomyHash);

				printCurrentAxioms(changingAxioms, staticAxioms);

				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace("======= Current Taxonomy =======");
					printTaxonomy(reasoner.getTaxonomyQuietly());
				}
				
				if (hook != null) {
					hook.apply(reasoner, changingAxioms, staticAxioms);
				}
			}

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Checking the final taxonomy");

			String finalTaxonomyHash = taxonomyHashHistory.pollLast();

			Reasoner standardReasoner = TestReasonerUtils.createTestReasoner(
					new SimpleStageExecutor(), 1);
			standardReasoner.setIncrementalMode(false);
			standardReasoner.registerOntologyLoader(new TestAxiomLoader(
					Operations.concat(changingAxioms.getOnElements(),
							staticAxioms)));
			standardReasoner
					.registerOntologyChangesLoader(new EmptyChangesLoader());
			standardReasoner.setIncrementalMode(false);
			String expectedTaxonomyHash = TaxonomyPrinter
					.getHashString(standardReasoner.getTaxonomyQuietly());

			try {
				assertEquals("Seed " + seed, expectedTaxonomyHash,
						finalTaxonomyHash);
			} catch (AssertionError e) {
				LOGGER_.info("======= Current Taxonomy =======");
				printTaxonomy(reasoner.getTaxonomyQuietly());
				LOGGER_.info("====== Expected Taxonomy =======");
				printTaxonomy(standardReasoner.getTaxonomyQuietly());
				standardReasoner.shutdown();
				throw e;
			}
			standardReasoner.shutdown();

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Reverting the changes");
			for (;;) {
				IncrementalChange change = changesHistory.pollLast();
				String expectedHash = taxonomyHashHistory.pollLast();
				if (change == null)
					break;
				reasoner.registerOntologyChangesLoader(new ReversesChangeLoader(
						change));
				String taxonomyHash = TaxonomyPrinter
						.getHashString(reasoner.getTaxonomyQuietly());

				assertEquals("Seed " + seed, expectedHash, taxonomyHash);
			}
			// doubling the change size every round
			changeSize *= 2;
		}
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

	private void printCurrentAxioms(OnOffVector<ElkAxiom> changingAxioms,
			Iterable<ElkAxiom> staticAxioms) {
		if (LOGGER_.isTraceEnabled()) {
			for (ElkAxiom axiom : changingAxioms.getOnElements())
				LOGGER_.trace("Current axiom: "
						+ OwlFunctionalStylePrinter.toString(axiom));
			for (ElkAxiom axiom : staticAxioms)
				LOGGER_.trace("Current axiom: "
						+ OwlFunctionalStylePrinter.toString(axiom));
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
