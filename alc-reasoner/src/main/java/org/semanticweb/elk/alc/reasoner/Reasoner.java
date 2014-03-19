package org.semanticweb.elk.alc.reasoner;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.alc.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.alc.indexing.hierarchy.ElkAxiomIndexingVisitor;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.MainAxiomIndexerVisitor;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.ComposedAxiomLoader;
import org.semanticweb.elk.alc.loading.ElkLoadingException;
import org.semanticweb.elk.alc.saturation.Context;
import org.semanticweb.elk.alc.saturation.Saturation;
import org.semanticweb.elk.alc.saturation.SaturationState;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.logging.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reasoner {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Reasoner.class);

	/**
	 * the (differential) index for loading of axioms and changes
	 */
	private final OntologyIndex ontologyIndex_;
	/**
	 * the object using which axioms are inserted into the index
	 */
	private final ElkAxiomIndexingVisitor axiomInserterVisitor_;
	/**
	 * the object using which axioms are deleted from the index
	 */
	private final ElkAxiomIndexingVisitor axiomDeleterVisitor_;
	/**
	 * The source where axioms and changes in ontology can be loaded
	 */
	private AxiomLoader axiomLoader_;

	private SaturationState saturationState_ = null;

	private boolean satisfiabilityCheckingFinished_ = false;

	private boolean classificationFinished_ = false;

	public Reasoner() {
		ontologyIndex_ = new OntologyIndex();
		this.axiomInserterVisitor_ = new MainAxiomIndexerVisitor(
				ontologyIndex_, true);
		this.axiomDeleterVisitor_ = new MainAxiomIndexerVisitor(ontologyIndex_,
				false);
		// adding declarations for predefined classes
		axiomInserterVisitor_
				.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);
		axiomInserterVisitor_
				.indexClassDeclaration(PredefinedElkClass.OWL_THING);

	}

	public Reasoner(AxiomLoader axiomLoader) {
		this();
		registerAxiomLoader(axiomLoader);
	}

	public synchronized void registerAxiomLoader(AxiomLoader newAxiomLoader) {
		LOGGER_.trace("Registering new axiom loader");

		if (axiomLoader_ == null || axiomLoader_.isLoadingFinished())
			axiomLoader_ = newAxiomLoader;
		else
			axiomLoader_ = new ComposedAxiomLoader(axiomLoader_, newAxiomLoader);
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex_;
	}

	/**
	 * Forces loading of all axioms from the registered {@link AxiomLoader}s.
	 * Typically, loading lazily when reasoning tasks are requested.
	 * 
	 * @throws ElkLoadingException
	 *             if axioms cannot be loaded
	 */
	public void forceLoading() throws ElkLoadingException {
		if (axiomLoader_ == null || axiomLoader_.isLoadingFinished()) {
			return;
		}

		ElkAxiomProcessor axiomInserter = new ChangeIndexingProcessor(
				axiomInserterVisitor_);
		ElkAxiomProcessor axiomDeleter = new ChangeIndexingProcessor(
				axiomDeleterVisitor_);

		Statistics.logOperationStart("loading", LOGGER_);
		try {
			axiomLoader_.load(axiomInserter, axiomDeleter);
		} finally {
			axiomLoader_.dispose();
			// clear interrupt status
			Thread.interrupted();
			Statistics.logOperationFinish("loading", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}

	}

	public boolean subsumes(IndexedClassExpression first,
			IndexedClassExpression second) throws ElkLoadingException {
		checkSatisfiability();
		Context context = saturationState_.getContext(first);
		if (context == null) {
			// does not occur
			return first == second;
		}
		if (context.isInconsistent()) {
			return true;
		}
		Saturation saturation = new Saturation(saturationState_);
		return (saturation.checkSubsumer(context, second));
	}

	public void checkSatisfiability() throws ElkLoadingException {
		if (satisfiabilityCheckingFinished_)
			return;
		forceLoading();
		saturationState_ = new SaturationState();
		Saturation saturation = new Saturation(saturationState_);
		Statistics.logOperationStart("concept satisfiability testing", LOGGER_);
		try {
			// int count = 0;
			for (IndexedClass initialClass : ontologyIndex_.getIndexedClasses()) {
				saturation.submit(initialClass);
				saturation.process();
				// count++;
				// if ((count / 1000) * 1000 == count)
				// LOGGER_.info("{} concepts processed", count);
			}
		} finally {
			Statistics.logOperationFinish("concept satisfiability testing",
					LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}
		satisfiabilityCheckingFinished_ = true;
	}

	public void classify() throws ElkLoadingException {
		if (classificationFinished_)
			return;
		checkSatisfiability();
		Saturation saturation = new Saturation(saturationState_);
		Statistics.logOperationStart("classification", LOGGER_);
		// int count = 0;
		int countSubsumers = 0;
		int countSubsumerTests = 0;
		int countNegativeSubsumerTests = 0;
		try {
			for (IndexedClass initialClass : ontologyIndex_.getIndexedClasses()) {
				Context context = saturationState_.getContext(initialClass);
				for (IndexedClassExpression subsumer : context.getSubsumers()) {
					if (subsumer instanceof IndexedClass)
						countSubsumers++;
				}
				for (IndexedClassExpression possibleSubsumer : context
						.getPossibleSubsumers()) {
					if (possibleSubsumer instanceof IndexedClass) {
						if (!saturation
								.checkSubsumer(context, possibleSubsumer)) {
							countSubsumers--;
							countNegativeSubsumerTests++;
						}
						countSubsumerTests++;
						// if (saturation.checkSubsumer(context,
						// possibleSubsumer) != saturation
						// .checkSubsumer(context, possibleSubsumer)) {
						// LOGGER_.error("{}: testst do not agree on {}",
						// context, possibleSubsumer);
						// }
					}
				}
				// count++;
				// if ((count / 1000) * 1000 == count)
				// LOGGER_.info(
				// "{} concepts processed (evarage: {} subsumers, {} subsumer tests, {} positive)",
				// count, countSubsumers / count, countSubsumerTests
				// / count,
				// (countSubsumerTests - countNegativeSubsumerTests)
				// / count);
			}
		} finally {
			LOGGER_.debug(
					"Total subsumers: {}, subsumer tests: {}, positive: {}",
					countSubsumers, countSubsumerTests, countSubsumerTests
							- countNegativeSubsumerTests);
			Statistics.logOperationFinish("classification", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}
		classificationFinished_ = true;
	}
}
