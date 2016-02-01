package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
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

public class ClassConclusionStatistics extends AbstractStatistics {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionStatistics.class);

	/**
	 * Number of different inferences by type of the conclusion
	 */
	private final ClassConclusionCounter inferenceCounts_;
	/**
	 * Number of different conclusions
	 */
	private final ClassConclusionCounter conclusionCounts_;

	/**
	 * Time for processing of conclusions of inferences
	 */
	private final ClassConclusionTimer conclusionProcessingTimer_;

	public ClassConclusionStatistics(ClassConclusionCounter inferenceCounts,
			ClassConclusionCounter conclusionCounts,
			ClassConclusionTimer conclusionTimers) {
		this.inferenceCounts_ = inferenceCounts;
		this.conclusionCounts_ = conclusionCounts;
		this.conclusionProcessingTimer_ = conclusionTimers;
	}

	public ClassConclusionStatistics() {
		this(new ClassConclusionCounter(), new ClassConclusionCounter(),
				new ClassConclusionTimer());
	}

	public ClassConclusionCounter getInferenceCounts() {
		return inferenceCounts_;
	}

	public ClassConclusionCounter getConclusionCounts() {
		return conclusionCounts_;
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
		inferenceCounts_.reset();
		conclusionCounts_.reset();
		conclusionProcessingTimer_.reset();
	}

	public synchronized void add(ClassConclusionStatistics stats) {
		super.add(stats);
		this.inferenceCounts_.add(stats.inferenceCounts_);
		this.conclusionCounts_.add(stats.conclusionCounts_);
		this.conclusionProcessingTimer_.add(stats.conclusionProcessingTimer_);
	}

	public void check(Logger logger) {
		// TODO
	}

	void print(StatisticsPrinter printer, String name, long inferenceCount,
			long conclusionCount, long time) {
		if (inferenceCount == 0)
			return;

		if (conclusionCount > inferenceCount)
			LOGGER_.error("{}: conclusions: {} more than inferences: {}!", name,
					conclusionCount, inferenceCount);

		printer.print(name, inferenceCount, conclusionCount,
				time / getNumberOfMeasurements());

	}

	public void print(Logger logger) {
		if (!logger.isDebugEnabled()) {
			return;
		}

		if (!measurementsTaken()) {
			return;
		}

		if (conclusionCounts_.getTotalCount() == 0) {
			return;
		}

		StatisticsPrinter printer = new StatisticsPrinter(logger,
				"%{CONCLUSIONS:}s %,{processed}d | %,{unique}d [%,{time}d ms]",
				"TOTAL CONCLUSIONS", conclusionCounts_.getTotalCount(),
				inferenceCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printHeader();

		print(printer, BackwardLink.NAME, inferenceCounts_.countBackwardLink,
				conclusionCounts_.countBackwardLink,
				conclusionProcessingTimer_.timeBackwardLinks);

		print(printer, ContextInitialization.NAME,
				inferenceCounts_.countContextInitialization,
				conclusionCounts_.countContextInitialization,
				conclusionProcessingTimer_.timeContextInitializations);

		print(printer, Contradiction.NAME, inferenceCounts_.countContradiction,
				conclusionCounts_.countContradiction,
				conclusionProcessingTimer_.timeContradictions);

		print(printer, DisjointSubsumer.NAME,
				inferenceCounts_.countDisjointSubsumer,
				conclusionCounts_.countDisjointSubsumer,
				conclusionProcessingTimer_.timeDisjointSubsumers);

		print(printer, ForwardLink.NAME, inferenceCounts_.countForwardLink,
				conclusionCounts_.countForwardLink,
				conclusionProcessingTimer_.timeForwardLinks);

		print(printer, SubClassInclusionDecomposed.NAME,
				inferenceCounts_.countSubClassInclusionDecomposed,
				conclusionCounts_.countSubClassInclusionDecomposed,
				conclusionProcessingTimer_.timeDecomposedSubsumers);

		print(printer, SubClassInclusionComposed.NAME,
				inferenceCounts_.countSubClassInclusionComposed,
				conclusionCounts_.countSubClassInclusionComposed,
				conclusionProcessingTimer_.timeComposedSubsumers);

		print(printer, Propagation.NAME, inferenceCounts_.countPropagation,
				conclusionCounts_.countPropagation,
				conclusionProcessingTimer_.timePropagations);

		print(printer, SubContextInitialization.NAME,
				inferenceCounts_.countSubContextInitialization,
				conclusionCounts_.countSubContextInitialization,
				conclusionProcessingTimer_.timeSubContextInitializations);

		printer.printSeparator();

		print(printer, "TOTAL CONCLUSIONS:", inferenceCounts_.getTotalCount(),
				conclusionCounts_.getTotalCount(),
				conclusionProcessingTimer_.getTotalTime());

		printer.printSeparator();
	}

}
