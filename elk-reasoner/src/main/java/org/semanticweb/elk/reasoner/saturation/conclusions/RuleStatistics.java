package org.semanticweb.elk.reasoner.saturation.conclusions;


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

public class RuleStatistics {

	/**
	 * the number of applications of the backward link rule in
	 * {@link ForwardLink}
	 */
	int countForwardLinkBackwardLinkRule;

	/**
	 * the time spent within the backward link rule of {@link ForwardLink}
	 */
	long timeForwardLinkBackwardLinkRule;

	/**
	 * @return the number of applications of the backward link rule in
	 *         {@link ForwardLink}
	 */
	public int getForwardLinkBackwardLinkRuleCount() {
		return countForwardLinkBackwardLinkRule;
	}

	/**
	 * @return the time spent within the backward link rule of
	 *         {@link ForwardLink}
	 */
	public long getForwardLinkBackwardLinkRuleTime() {
		return timeForwardLinkBackwardLinkRule;
	}

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		countForwardLinkBackwardLinkRule = 0;
		timeForwardLinkBackwardLinkRule = 0;
	}

	public synchronized void merge(RuleStatistics stats) {
		this.countForwardLinkBackwardLinkRule += stats.countForwardLinkBackwardLinkRule;
		this.timeForwardLinkBackwardLinkRule += stats.timeForwardLinkBackwardLinkRule;
	}

}
