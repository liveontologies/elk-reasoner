package org.semanticweb.elk.reasoner;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

/**
 * A {@link SatisfiabilityCheckingOutcome} for the test that has returned a
 * result
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SatisfiabilityCheckingResult extends SatisfiabilityCheckingOutcome {

	/**
	 * the result return by the test, {@code true} for satisfiable,
	 * {@code false} for not satisfiable
	 */
	private final boolean result_;

	/**
	 * Creates the outcome for the test result
	 * 
	 * @param result
	 *            the result of the test, {@code true} for satisfiable,
	 *            {@code false} for not satisfiable
	 */
	public SatisfiabilityCheckingResult(boolean result) {
		this.result_ = result;
	}

	@Override
	public boolean get() {
		return result_;
	}

}
