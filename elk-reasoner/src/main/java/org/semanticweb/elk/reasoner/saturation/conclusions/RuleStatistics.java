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

public class RuleStatistics {

	int backLinkInfNo;
	int backLinkNo;
	int forwLinkInfNo;
	int forwLinkNo;
	int superClassExpressionInfNo;
	int superClassExpressionNo;

	public int getBackLinkInfNo() {
		return backLinkInfNo;
	}

	public int getBackLinkNo() {
		return backLinkNo;
	}

	public int getForwLinkInfNo() {
		return forwLinkInfNo;
	}

	public int getForwLinkNo() {
		return forwLinkNo;
	}

	public int getSuperClassExpressionInfNo() {
		return superClassExpressionInfNo;
	}

	public int getSuperClassExpressionNo() {
		return superClassExpressionNo;
	}

	/**
	 * Reset all statistics counters (make them zero).
	 */
	public void reset() {
		backLinkInfNo = 0;
		backLinkNo = 0;
		forwLinkInfNo = 0;
		forwLinkNo = 0;
		superClassExpressionInfNo = 0;
		superClassExpressionNo = 0;
	}

	/**
	 * Add all statistic counters of the argument to the corresponding statistic
	 * counter of this object
	 * 
	 * @param statistics
	 *            the object which counters should be added
	 */
	public synchronized void merge(RuleStatistics statistics) {
		this.backLinkInfNo += statistics.backLinkInfNo;
		this.backLinkNo += statistics.backLinkNo;
		this.forwLinkInfNo += statistics.forwLinkInfNo;
		this.forwLinkNo += statistics.forwLinkNo;
		this.superClassExpressionInfNo += statistics.superClassExpressionInfNo;
		this.superClassExpressionNo += statistics.superClassExpressionNo;
	}

}
