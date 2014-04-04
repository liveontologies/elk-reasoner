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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
import org.semanticweb.elk.alc.saturation.PropertyHierarchyComputation;
import org.semanticweb.elk.alc.saturation.SaturatedContext;
import org.semanticweb.elk.alc.saturation.Saturation;
import org.semanticweb.elk.alc.saturation.SaturationState;
import org.semanticweb.elk.alc.saturation.SaturationStatistics;
import org.semanticweb.elk.alc.saturation.reduction.SubsumptionReduct;
import org.semanticweb.elk.alc.saturation.reduction.SubsumptionTransitiveReduction;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.logging.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reasoner {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Reasoner.class);

	/**
	 * if {@code true}, some statistics will be printed
	 */
	private static final boolean PRINT_STATS_ = true;

	/**
	 * the index for loading of axioms and changes
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
	
	private final SaturationStatistics statistics_ = new SaturationStatistics();

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

		Saturation saturation = new Saturation(saturationState_, ontologyIndex_.getIndexedOwlNothing());
		//TODO this is temporary
		if (second instanceof IndexedClass) {
			Collection<? extends IndexedClassExpression> atomicSubsumers = saturation.getAtomicSubsumers(first);
			
			return atomicSubsumers.contains(second);
		}
		else {
			return (saturation.checkSubsumer(context, second));	
		}
	}
	
	private void computePropertyHierarchy() {
		new PropertyHierarchyComputation().compute(ontologyIndex_);
	}

	public void checkSatisfiability() throws ElkLoadingException {
		if (satisfiabilityCheckingFinished_)
			return;
		
		statistics_.reset();
		forceLoading();
		computePropertyHierarchy();
		saturationState_ = new SaturationState();
		Saturation saturation = new Saturation(saturationState_, ontologyIndex_.getIndexedOwlNothing());
		Statistics.logOperationStart("concept satisfiability testing", LOGGER_);
		try {
			int count = 0;
			for (IndexedClass initialClass : ontologyIndex_.getIndexedClasses()) {
				saturation.submit(initialClass);
				saturation.process();
				if (PRINT_STATS_) {
					count++;
					if ((count / 1000) * 1000 == count)
						LOGGER_.debug("{} concepts processed", count);
				}
			}
		} finally {
			SaturationStatistics saturationStats = saturation.getStatistics();
			
			statistics_.add(saturation.getStatistics());
			
			Statistics.logOperationFinish("concept satisfiability testing",
					LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
			LOGGER_.debug("Conclusions added: {}, removed: {}",
					saturationStats.addedConclusions,
					saturationStats.removedConclusions);
		}
		satisfiabilityCheckingFinished_ = true;
	}

	public void classify() throws ElkLoadingException {
		if (classificationFinished_)
			return; 
		
		statistics_.reset();
		checkSatisfiability();
		Saturation saturation = new Saturation(saturationState_, ontologyIndex_.getIndexedOwlNothing());
		Statistics.logOperationStart("classification", LOGGER_);
		int countClasses = 0;
		int countSubsumers = 0;
		int countSubsumerTests = 0;
		int countNegativeSubsumerTests = 0;
		try {

			for (IndexedClass initialClass : ontologyIndex_.getIndexedClasses()) {
				countClasses++;
				Context context = saturationState_.getContext(initialClass);
				Set<IndexedClassExpression> allSubsumers = context
						.getSubsumers();
				Set<IndexedClass> atomicSubsumers = new ArrayHashSet<IndexedClass>(allSubsumers.size());
				
				for (IndexedClassExpression subsumer : allSubsumers) {
					if (subsumer instanceof IndexedClass) {
						//countSubsumers++;
						atomicSubsumers.add((IndexedClass) subsumer);
					}
				}
				// contains possible subsumers remained to be tested
				Queue<IndexedClass> subsumersToTest = null;
				
				for (;;) {
					Set<IndexedClassExpression> possibleSubsumers = context
							.getComposedSubsumers();
					if (subsumersToTest == null) {
						// initializing subsumers by possible subsumers
						subsumersToTest = new LinkedList<IndexedClass>();
						for (IndexedClassExpression possibleSubsumer : possibleSubsumers) {
							if (possibleSubsumer instanceof IndexedClass) {
								subsumersToTest
										.add((IndexedClass) possibleSubsumer);
							}
						}
					} else {
						// filtering out subsumers that are no longer derived
						// non-deterministically
						/*allSubsumers = context.getSubsumers();
						Iterator<IndexedClass> subsumersToTestIterator = subsumersToTest
								.iterator();
						while (subsumersToTestIterator.hasNext()) {
							IndexedClass next = subsumersToTestIterator.next();
							if (possibleSubsumers.contains(next))
								continue;
							// else not derived non-deterministically
							subsumersToTestIterator.remove();
							if (!allSubsumers.contains(next)) {
								// not derived deterministically either
								//countSubsumers--;
								atomicSubsumers.remove(next);
							}
						}*/
					}
					IndexedClass possibleSubsumer = subsumersToTest.poll();
					if (possibleSubsumer == null)
						break;
					if (!saturation.checkSubsumer(context, possibleSubsumer)) {
						//countSubsumers--;
						atomicSubsumers.remove(possibleSubsumer);
						countNegativeSubsumerTests++;
					}
					countSubsumerTests++;
				}
				
				countSubsumers += atomicSubsumers.size();
				context.setSaturatedContext(new SaturatedContext(atomicSubsumers));

				if (PRINT_STATS_) {
					if ((countClasses / 1000) * 1000 == countClasses)
						LOGGER_.info(
								"{} concepts processed (average: {} subsumers, {} subsumer tests, {} positive)",
								countClasses,
								atomicSubsumers.size() / countClasses,
								countSubsumerTests / countClasses,
								(countSubsumerTests - countNegativeSubsumerTests)
										/ countClasses);
				}
			}
		} finally {
			SaturationStatistics saturationStats = saturation.getStatistics();
			
			statistics_.add(saturation.getStatistics());
			
			LOGGER_.info(
					"Total classes: {}, subsumers: {}, subsumer tests: {}, positive: {}",
					countClasses, countSubsumers, countSubsumerTests,
					countSubsumerTests - countNegativeSubsumerTests);
			LOGGER_.debug("Conclusions added: {}, removed: {}",
					saturationStats.addedConclusions,
					saturationStats.removedConclusions);
			Statistics.logOperationFinish("classification", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}
		
		classificationFinished_ = true;
	}
	
	public void classifyOptimized() throws ElkLoadingException {
		if (classificationFinished_)
			return;
		
		statistics_.reset();
		checkSatisfiability();
		Saturation saturation = new Saturation(saturationState_, ontologyIndex_.getIndexedOwlNothing());
		Statistics.logOperationStart("classification", LOGGER_);
		int countClasses = 0;
		int countSubsumers = 0;
		int countSubsumerTests = 0;
		int countNegativeSubsumerTests = 0;
		
		try {
			
			for (IndexedClass initialClass : ontologyIndex_.getIndexedClasses()) {
				countClasses++;
				
				Collection<IndexedClass> atomicSubsumers = saturation.getAtomicSubsumers(initialClass);
				
				if (atomicSubsumers == null) {
					//currently this means the class is unsatisfiable 
					LOGGER_.debug("{}: is unsatisfiable", initialClass);
					countSubsumers += 1;//owl:Nothing is the only subsumer
				}
				else {
					countSubsumers += atomicSubsumers.size();
				}
				
				if (PRINT_STATS_) {
					if ((countClasses / 1000) * 1000 == countClasses) {
						LOGGER_.info(
								"{} concepts processed (average: {} subsumers, {} subsumer tests, {} positive)",
								countClasses,
								countSubsumers / countClasses,
								countSubsumerTests / countClasses,
								(countSubsumerTests - countNegativeSubsumerTests)
										/ countClasses);
					}
				}
			}
		} finally {
			SaturationStatistics saturationStats = saturation.getStatistics();
			
			statistics_.add(saturation.getStatistics());
			
			LOGGER_.debug(
					"Total classes: {}, subsumers: {}, subsumer tests: {}, positive: {}",
					countClasses, countSubsumers, countSubsumerTests,
					countSubsumerTests - countNegativeSubsumerTests);
			LOGGER_.debug("Conclusions added: {}, removed: {}",
					saturationStats.addedConclusions,
					saturationStats.removedConclusions);
			Statistics.logOperationFinish("classification", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}
		
		classificationFinished_ = true;
	}
	
	// TODO hide this method
	public Map<IndexedClass, SubsumptionReduct> reduce() {
		return new SubsumptionTransitiveReduction().compute(ontologyIndex_.getIndexedClasses(), saturationState_);
	}
	
	public SaturationStatistics getStatistics() {
		return statistics_;
	}
}
