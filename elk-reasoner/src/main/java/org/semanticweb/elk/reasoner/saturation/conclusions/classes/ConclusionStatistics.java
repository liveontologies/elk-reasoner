package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.util.logging.statistics.AbstractStatistics;
import org.semanticweb.elk.util.logging.statistics.StatisticsPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

public class ConclusionStatistics extends AbstractStatistics {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConclusionStatistics.class);

	/**
	 * Number of conclusions put to the todo queue
	 */
	private final ClassConclusionCounter producedConclusionCounts_;
	/**
	 * Number of conclusions taken from the todo queue and processed
	 */
	private final ClassConclusionCounter processedConclusionCounts_;
	/**
	 * Number of unique conclusions saved to contexts
	 */
	private final ClassConclusionCounter usedConclusionCounts_;

	private final ClassConclusionTimer conclusionProcessingTimer_;

	public ConclusionStatistics(ClassConclusionCounter producedConclusionCounter,
			ClassConclusionCounter processedConclusionCounts,
			ClassConclusionCounter usedConclusionCounts,
			ClassConclusionTimer conclusionTimers) {
		this.producedConclusionCounts_ = producedConclusionCounter;
		this.processedConclusionCounts_ = processedConclusionCounts;
		this.usedConclusionCounts_ = usedConclusionCounts;
		this.conclusionProcessingTimer_ = conclusionTimers;
	}

	public ConclusionStatistics() {
		this(new ClassConclusionCounter(), new ClassConclusionCounter(),
				new ClassConclusionCounter(), new ClassConclusionTimer());
	}

	public ClassConclusionCounter getProducedConclusionCounts() {
		return producedConclusionCounts_;
	}

	public ClassConclusionCounter getProcessedConclusionCounts() {
		return processedConclusionCounts_;
	}

	public ClassConclusionCounter getUsedConclusionCounts() {
		return usedConclusionCounts_;
	}

	public ClassConclusionTimer getConclusionTimers() {
		return conclusionProcessingTimer_;
	}

	/**
	 * Reset all timers to zero.
	 */
	@Override
	public void reset() {
		super.reset();
		producedConclusionCounts_.reset();
		processedConclusionCounts_.reset();
		usedConclusionCounts_.reset();
		conclusionProcessingTimer_.reset();
	}

	public synchronized void add(ConclusionStatistics stats) {
		super.add(stats);
		this.producedConclusionCounts_.add(stats.producedConclusionCounts_);
		this.processedConclusionCounts_.add(stats.processedConclusionCounts_);
		this.usedConclusionCounts_.add(stats.usedConclusionCounts_);
		this.conclusionProcessingTimer_.add(stats.conclusionProcessingTimer_);
	}

	public void check(Logger logger) {
		// TODO
	}

	void print(StatisticsPrinter printer, String name, long processedCount,
			long usedCount, long producedCount, long time) {
		if (processedCount == 0)
			return;

		if (usedCount > processedCount)
			LOGGER_.error("{}: conclusions used: {} more than processed: {}!",
					name, usedCount, processedCount);

		printer.print(name, processedCount, usedCount, producedCount, time
				/ getNumberOfMeasurements());

	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled()) {
			return;
		}

		if (!measurementsTaken()) {
			return;
		}

		if (processedConclusionCounts_.getTotalCount() == 0) {
			return;
		}

		StatisticsPrinter printer = new StatisticsPrinter(
				logger,
				"%{CONCLUSIONS:}s %,{processed}d | %,{used}d | %,{produced}d [%,{time}d ms]",
				"TOTAL CONCLUSIONS",
				processedConclusionCounts_.getTotalCount(),
				usedConclusionCounts_.getTotalCount(),
				producedConclusionCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printHeader();

		print(printer, BackwardLink.NAME,
				processedConclusionCounts_.countBackwardLinks,
				usedConclusionCounts_.countBackwardLinks,
				producedConclusionCounts_.countBackwardLinks,
				conclusionProcessingTimer_.timeBackwardLinks);

		print(printer, ContextInitialization.NAME,
				processedConclusionCounts_.countContextInitializations,
				usedConclusionCounts_.countContextInitializations,
				producedConclusionCounts_.countContextInitializations,
				conclusionProcessingTimer_.timeContextInitializations);

		print(printer, Contradiction.NAME,
				processedConclusionCounts_.countContradictions,
				usedConclusionCounts_.countContradictions,
				producedConclusionCounts_.countContradictions,
				conclusionProcessingTimer_.timeContradictions);

		print(printer, DisjointSubsumer.NAME,
				processedConclusionCounts_.countDisjointSubsumers,
				usedConclusionCounts_.countDisjointSubsumers,
				producedConclusionCounts_.countDisjointSubsumers,
				conclusionProcessingTimer_.timeDisjointSubsumers);

		print(printer, ForwardLink.NAME,
				processedConclusionCounts_.countForwardLinks,
				usedConclusionCounts_.countForwardLinks,
				producedConclusionCounts_.countForwardLinks,
				conclusionProcessingTimer_.timeForwardLinks);

		print(printer, DecomposedSubsumer.NAME,
				processedConclusionCounts_.countDecomposedSubsumers,
				usedConclusionCounts_.countDecomposedSubsumers,
				producedConclusionCounts_.countDecomposedSubsumers,
				conclusionProcessingTimer_.timeDecomposedSubsumers);

		print(printer, ComposedSubsumer.NAME,
				processedConclusionCounts_.countComposedSubsumers,
				usedConclusionCounts_.countComposedSubsumers,
				producedConclusionCounts_.countComposedSubsumers,
				conclusionProcessingTimer_.timeComposedSubsumers);

		print(printer, Propagation.NAME,
				processedConclusionCounts_.countPropagations,
				usedConclusionCounts_.countPropagations,
				producedConclusionCounts_.countPropagations,
				conclusionProcessingTimer_.timePropagations);

		print(printer, SubContextInitialization.NAME,
				processedConclusionCounts_.countSubContextInitializations,
				usedConclusionCounts_.countSubContextInitializations,
				producedConclusionCounts_.countSubContextInitializations,
				conclusionProcessingTimer_.timeSubContextInitializations);

		printer.printSeparator();

		print(printer, "TOTAL CONCLUSIONS:",
				processedConclusionCounts_.getTotalCount(),
				usedConclusionCounts_.getTotalCount(),
				producedConclusionCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printSeparator();
	}

}
