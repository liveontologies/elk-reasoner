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

import org.semanticweb.elk.owl.exceptions.ElkException;

/**
 * A {@link SatisfiabilityCheckingOutcome} for the test that has returned an
 * exception
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SatisfiabilityCheckingException extends
		SatisfiabilityCheckingOutcome {

	/**
	 * the exception returned by the test
	 */
	private final ElkException exception_;

	/**
	 * Creates the outcome for an exception
	 * 
	 * @param exception
	 *            the {@link ElkException} happened during the test
	 */
	public SatisfiabilityCheckingException(ElkException exception) {
		this.exception_ = exception;
	}

	@Override
	public boolean get() throws ElkException {
		throw exception_;
	}

}
