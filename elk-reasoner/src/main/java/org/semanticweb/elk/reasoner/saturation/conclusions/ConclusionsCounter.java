/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;

/**
 * The counters for the numbers of {@link Conclusion}s derived and stored within
 * {@link Context}s. This class is designed to work for concurrent computations.
 * To avoid race condition and data corruption when several thread
 * simultaneously try update the values of the same counter, one should use one
 * instance of this class for each thread performing the computation. After the
 * computation is over, the values of one instance can be aggregated with the
 * corresponding values of another instance using the method
 * {@link #merge(ConclusionsCounter)}.
 * 
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionsCounter {

	int backLinkInfNo;
	int backLinkNo;
	long backLinkTime;
	int forwLinkInfNo;
	int forwLinkNo;
	long forwLinkTime;
	int posSubsumerInfNo;
	int negSubsumerInfNo;
	int subsumersNo;
	long subsumersTime;

	/**
	 * @return the number of times a {@link BackwardLink} has been produced
	 */
	public int getBackLinkInfNo() {
		return backLinkInfNo;
	}

	/**
	 * @return the number of different {@link BackwardLink}s produced and stored
	 */
	public int getBackLinkNo() {
		return backLinkNo;
	}

	/**
	 * @return the time spent within
	 *         {@link BackwardLink#apply(RuleEngine, Context)}
	 */
	public long getBackLinkTime() {
		return backLinkTime;
	}

	/**
	 * @return the number of times a {@link BackwardLink} has been produced
	 */
	public int getForwLinkInfNo() {
		return forwLinkInfNo;
	}

	/**
	 * @return the number of different {@link ForwardLink}s produced and stored
	 */
	public int getForwLinkNo() {
		return forwLinkNo;
	}

	/**
	 * @return the time spent within
	 *         {@link ForwardLink#apply(RuleEngine, Context)}
	 */
	public long getForwLinkTime() {
		return forwLinkTime;
	}

	/**
	 * @return the number of times a {@link PositiveSubsumer} has
	 *         been produced
	 */
	public int getPositiveSuperClassExpressionInfNo() {
		return posSubsumerInfNo;
	}

	/**
	 * @return the number of times a {@link NegativeSubsumer} has
	 *         been produced
	 */
	public int getNegativeSuperClassExpressionInfNo() {
		return negSubsumerInfNo;
	}

	/**
	 * @return the number of different {@link Subsumer}s produced and stored
	 */
	public int getSubsumerNo() {
		return subsumersNo;
	}

	/**
	 * @return the time spent within {@link Subsumer#apply(RuleEngine, Context)}
	 */
	public long getSubsumerTime() {
		return subsumersTime;
	}

	/**
	 * Reset all statistics counters to zero.
	 */
	public void reset() {
		backLinkInfNo = 0;
		backLinkNo = 0;
		backLinkTime = 0;
		forwLinkInfNo = 0;
		forwLinkNo = 0;
		forwLinkTime = 0;
		posSubsumerInfNo = 0;
		negSubsumerInfNo = 0;
		subsumersNo = 0;
		subsumersTime = 0;
	}

	/**
	 * Adds all counters of the argument to the corresponding counters of this
	 * object. The counters should not be directly modified (other than using
	 * this method) during this operation. The counter in the argument will be
	 * reseted after this operation.
	 * 
	 * @param statistics
	 *            the object which counters should be added
	 */
	public synchronized void merge(ConclusionsCounter statistics) {
		this.backLinkInfNo += statistics.backLinkInfNo;
		this.backLinkNo += statistics.backLinkNo;
		this.backLinkTime += statistics.backLinkTime;
		this.forwLinkInfNo += statistics.forwLinkInfNo;
		this.forwLinkNo += statistics.forwLinkNo;
		this.forwLinkTime += statistics.forwLinkTime;
		this.posSubsumerInfNo += statistics.posSubsumerInfNo;
		this.negSubsumerInfNo += statistics.negSubsumerInfNo;
		this.subsumersNo += statistics.subsumersNo;
		this.subsumersTime += statistics.subsumersTime;
		statistics.reset();
	}

}
