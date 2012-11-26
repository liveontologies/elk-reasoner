package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.apache.log4j.Logger;

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

public class ConclusionStatistics {
	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(ConclusionStatistics.class);

	private final ConclusionCounter processedConclusionCounts_;
	private final ConclusionCounter usedConclusionCounts_;
	private final ConclusionTimer conclusionProcessingTimer_;
	private int addCounter = 0;

	public ConclusionStatistics(ConclusionCounter processedConclusionCounts,
			ConclusionCounter usedConclusionCounts,
			ConclusionTimer conclusionTimers) {
		this.processedConclusionCounts_ = processedConclusionCounts;
		this.usedConclusionCounts_ = usedConclusionCounts;
		this.conclusionProcessingTimer_ = conclusionTimers;
	}

	public ConclusionStatistics() {
		this(new ConclusionCounter(), new ConclusionCounter(),
				new ConclusionTimer());
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
	public void reset() {
		processedConclusionCounts_.reset();
		usedConclusionCounts_.reset();
		conclusionProcessingTimer_.reset();
	}

	public synchronized void add(ConclusionStatistics stats) {
		this.processedConclusionCounts_.add(stats.processedConclusionCounts_);
		this.usedConclusionCounts_.add(stats.usedConclusionCounts_);
		this.conclusionProcessingTimer_.add(stats.conclusionProcessingTimer_);
		addCounter++;
	}

	private static String ERR_MSG_MORE_USED = ": more used that processed!";

	public void check() {
		if (usedConclusionCounts_.countPositiveSubsumers > processedConclusionCounts_.countPositiveSubsumers)
			LOGGER_.error("Positive Subsumers" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countNegativeSubsumers > processedConclusionCounts_.countNegativeSubsumers)
			LOGGER_.error("Negative Subsumers" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countBackwardLinks > processedConclusionCounts_.countBackwardLinks)
			LOGGER_.error("Backward Links" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countForwardLinks > processedConclusionCounts_.countForwardLinks)
			LOGGER_.error("Forward Links" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countPropagations > processedConclusionCounts_.countPropagations)
			LOGGER_.error("Propagations" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countContradictions > processedConclusionCounts_.countContradictions)
			LOGGER_.error("Contradictions" + ERR_MSG_MORE_USED);
		if (usedConclusionCounts_.countDisjointnessAxioms > processedConclusionCounts_.countDisjointnessAxioms)
			LOGGER_.error("Disjointness Axioms" + ERR_MSG_MORE_USED);
	}

	public void print() {
		if (!LOGGER_.isDebugEnabled())
			return;
		if (addCounter == 0)
			return;
		if (processedConclusionCounts_.countPositiveSubsumers > 0
				|| conclusionProcessingTimer_.timePositiveSubsumers > 0)
			LOGGER_.debug("Positive Subsumers produced/used: "
					+ processedConclusionCounts_.countPositiveSubsumers + "/"
					+ usedConclusionCounts_.countPositiveSubsumers + " ("
					+ conclusionProcessingTimer_.timePositiveSubsumers
					/ addCounter + " ms)");
		if (processedConclusionCounts_.countNegativeSubsumers > 0
				|| conclusionProcessingTimer_.timeNegativeSubsumers > 0)
			LOGGER_.debug("Negative Subsumers produced/used: "
					+ processedConclusionCounts_.countNegativeSubsumers + "/"
					+ usedConclusionCounts_.countNegativeSubsumers + " ("
					+ conclusionProcessingTimer_.timeNegativeSubsumers
					/ addCounter + " ms)");
		if (processedConclusionCounts_.countBackwardLinks > 0
				|| conclusionProcessingTimer_.timeBackwardLinks > 0)
			LOGGER_.debug("Backward Links produced/used: "
					+ processedConclusionCounts_.countBackwardLinks + "/"
					+ usedConclusionCounts_.countBackwardLinks + " ("
					+ conclusionProcessingTimer_.timeBackwardLinks / addCounter
					+ " ms)");
		if (processedConclusionCounts_.countForwardLinks > 0
				|| conclusionProcessingTimer_.timeForwardLinks > 0)
			LOGGER_.debug("Forward Links produced/used: "
					+ processedConclusionCounts_.countForwardLinks + "/"
					+ usedConclusionCounts_.countForwardLinks + " ("
					+ conclusionProcessingTimer_.timeForwardLinks / addCounter
					+ " ms)");
		if (processedConclusionCounts_.countPropagations > 0
				|| conclusionProcessingTimer_.timePropagations > 0)
			LOGGER_.debug("Propagations produced/used: "
					+ processedConclusionCounts_.countPropagations + "/"
					+ usedConclusionCounts_.countPropagations + " ("
					+ conclusionProcessingTimer_.timePropagations / addCounter
					+ " ms)");
		if (processedConclusionCounts_.countContradictions > 0
				|| conclusionProcessingTimer_.timeContradictions > 0)
			LOGGER_.debug("Contradictions produced/used: "
					+ processedConclusionCounts_.countContradictions + "/"
					+ usedConclusionCounts_.countContradictions + " ("
					+ conclusionProcessingTimer_.timeContradictions
					/ addCounter + " ms)");
		if (processedConclusionCounts_.countDisjointnessAxioms > 0
				|| conclusionProcessingTimer_.timeDisjointnessAxioms > 0)
			LOGGER_.debug("Disjointness Axioms produced/used: "
					+ processedConclusionCounts_.countDisjointnessAxioms + "/"
					+ usedConclusionCounts_.countDisjointnessAxioms + " ("
					+ conclusionProcessingTimer_.timeDisjointnessAxioms
					/ addCounter + " ms)");
		long totalTime = conclusionProcessingTimer_.getTotalTime();
		if (totalTime > 0)
			LOGGER_.debug("Total conclusion processing time: " + totalTime
					/ addCounter + " ms");
	}
}
