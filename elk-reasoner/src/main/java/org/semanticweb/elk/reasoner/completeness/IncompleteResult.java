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

/**
 * Represents the possibly incomplete result of a reasoning task, such as
 * computing a class taxonomy. The reasons for incompleteness (if any) can be
 * explained using the {@link IncompletenessMonitor} that can be returned using
 * {@link #getIncompletenessMonitor()}. The return value of the
 * {@link IncompleteResult} cannot be obtained directly to prevent ignoring the
 * incompleteness. Use {@link Incompleteness#getValue(IncompleteResult)}. to
 * obtain this value.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <R>
 *            The type of the reasoning result for which the incompleteness
 *            information is provided
 */
public class IncompleteResult<R> {

	private final R value_;

	private final IncompletenessMonitor monitor_;

	public IncompleteResult(R result, IncompletenessMonitor monitor) {
		this.value_ = result;
		this.monitor_ = monitor;
	}

	R getValue() {
		return value_;
	}

	public IncompletenessMonitor getIncompletenessMonitor() {
		return monitor_;
	}

	public <O, E extends Throwable> IncompleteResult<O> map(
			CheckedFunction<R, O, E> fn) throws E {
		return new IncompleteResult<O>(fn.apply(value_), monitor_);
	}

	@FunctionalInterface
	public interface CheckedFunction<I, O, E extends Throwable> {
		O apply(I input) throws E;
	}

}
