package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.slf4j.Logger;

/**
 * A {@link IncompletenessMonitor} that delegates all methods to the provided
 * {@link IncompletenessMonitor}.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class DelegatingIncompletenessMonitor implements IncompletenessMonitor {

	private final IncompletenessMonitor delegate_;

	public DelegatingIncompletenessMonitor(IncompletenessMonitor delegate) {
		this.delegate_ = delegate;
	}

	public IncompletenessMonitor getDelegate() {
		return this.delegate_;
	}

	@Override
	public boolean isIncompletenessDetected() {
		return delegate_.isIncompletenessDetected();
	}

	@Override
	public void logStatus(Logger logger) {
		delegate_.logStatus(logger);
	}

	@Override
	public boolean isStatusChanged() {
		return delegate_.isStatusChanged();
	}

}
