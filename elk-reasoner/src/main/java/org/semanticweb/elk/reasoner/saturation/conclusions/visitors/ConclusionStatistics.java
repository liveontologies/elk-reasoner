package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubContextInitialization;
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
	private final ConclusionCounter producedConclusionCounts_;
	/**
	 * Number of conclusions taken from the todo queue and processed
	 */
	private final ConclusionCounter processedConclusionCounts_;
	/**
	 * Number of unique conclusions saved to contexts
	 */
	private final ConclusionCounter usedConclusionCounts_;

	private final ConclusionTimer conclusionProcessingTimer_;

	public ConclusionStatistics(ConclusionCounter producedConclusionCounter,
			ConclusionCounter processedConclusionCounts,
			ConclusionCounter usedConclusionCounts,
			ConclusionTimer conclusionTimers) {
		this.producedConclusionCounts_ = producedConclusionCounter;
		this.processedConclusionCounts_ = processedConclusionCounts;
		this.usedConclusionCounts_ = usedConclusionCounts;
		this.conclusionProcessingTimer_ = conclusionTimers;
	}

	public ConclusionStatistics() {
		this(new ConclusionCounter(), new ConclusionCounter(),
				new ConclusionCounter(), new ConclusionTimer());
	}

	public ConclusionCounter getProducedConclusionCounts() {
		return producedConclusionCounts_;
	}

	public ConclusionCounter getProcessedConclusionCounts() {
		return processedConclusionCounts_;
	}

	public ConclusionCounter getUsedConclusionCounts() {
		return usedConclusionCounts_;
	}

	public ConclusionTimer getConclusionTimers() {
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

	void print(StatisticsPrinter printer, String name, int processedCount,
			int usedCount, long time) {
		if (processedCount == 0)
			return;

		if (usedCount > processedCount)
			LOGGER_.error("{}: conclusions used: {} more than processed: {}!",
					name, usedCount, processedCount);

		printer.print(name, processedCount, usedCount, time
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

		StatisticsPrinter printer = new StatisticsPrinter(logger,
				"%{CONCLUSIONS:}s %,{all}d | %,{used}d [%,{time}d ms]",
				"TOTAL CONCLUSIONS",
				processedConclusionCounts_.getTotalCount(),
				usedConclusionCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printHeader();

		print(printer, BackwardLink.NAME,
				processedConclusionCounts_.countBackwardLinks,
				usedConclusionCounts_.countBackwardLinks,
				conclusionProcessingTimer_.timeBackwardLinks);

		print(printer, ContextInitialization.NAME,
				processedConclusionCounts_.countContextInitializations,
				usedConclusionCounts_.countContextInitializations,
				conclusionProcessingTimer_.timeContextInitializations);

		print(printer, Contradiction.NAME,
				processedConclusionCounts_.countContradictions,
				usedConclusionCounts_.countContradictions,
				conclusionProcessingTimer_.timeContradictions);

		print(printer, DisjointSubsumer.NAME,
				processedConclusionCounts_.countDisjointSubsumers,
				usedConclusionCounts_.countDisjointSubsumers,
				conclusionProcessingTimer_.timeDisjointSubsumers);

		print(printer, ForwardLink.NAME,
				processedConclusionCounts_.countForwardLinks,
				usedConclusionCounts_.countForwardLinks,
				conclusionProcessingTimer_.timeForwardLinks);

		print(printer, DecomposedSubsumer.NAME,
				processedConclusionCounts_.countDecomposedSubsumers,
				usedConclusionCounts_.countDecomposedSubsumers,
				conclusionProcessingTimer_.timeDecomposedSubsumers);

		print(printer, ComposedSubsumer.NAME,
				processedConclusionCounts_.countComposedSubsumers,
				usedConclusionCounts_.countComposedSubsumers,
				conclusionProcessingTimer_.timeComposedSubsumers);

		print(printer, Propagation.NAME,
				processedConclusionCounts_.countPropagations,
				usedConclusionCounts_.countPropagations,
				conclusionProcessingTimer_.timePropagations);

		print(printer, SubContextInitialization.NAME,
				processedConclusionCounts_.countSubContextInitializations,
				usedConclusionCounts_.countSubContextInitializations,
				conclusionProcessingTimer_.timeSubContextInitializations);

		printer.printSeparator();

		print(printer, "TOTAL CONCLUSIONS:",
				processedConclusionCounts_.getTotalCount(),
				usedConclusionCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printSeparator();
	}

}
