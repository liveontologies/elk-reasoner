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

import java.util.Collection;

import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Incompleteness {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Incompleteness.class);

	/**
	 * @return an {@link IncompletenessMonitor} that never reports
	 *         incompleteness. I.e., it assumes that the result is complete.
	 */
	public static IncompletenessMonitor getNoIncompletenessMonitor() {
		return NoIncompletenessMonitor.INSTNANCE;
	}

	/**
	 * @param monitors
	 * @return an {@link IncompletenessMonitor} consisting of a combination of
	 *         several other {@link IncompletenessMonitor}s. That is, this
	 *         monitor detects incompleteness if and only if at least one of the
	 *         monitors in the combination detects incompleteness.
	 */
	public static IncompletenessMonitor combine(
			IncompletenessMonitor... monitors) {
		return new CombinedIncompletenessMonitor(monitors);
	}

	/**
	 * @param monitors
	 * @return an {@link IncompletenessMonitor} consisting of a combination of
	 *         several other {@link IncompletenessMonitor}s. That is, this
	 *         monitor detects incompleteness if and only if at least one of the
	 *         monitors in the combination detects incompleteness.
	 * @see #combine(IncompletenessMonitor...)
	 */
	public static IncompletenessMonitor combine(
			Collection<IncompletenessMonitor> monitors) {
		return new CombinedIncompletenessMonitor(monitors);
	}

	/**
	 * Compose the given {@link IncompleteResult}s using a function and
	 * combining their {@link IncompletenessMonitor}s in the order in which they
	 * are listed
	 * 
	 * @param <RA>
	 * @param <RB>
	 * @param <O>
	 * @param <E>
	 * @param first
	 * @param second
	 * @param fn
	 * @return the {@link IncompleteResult} whose value is obtained by applying
	 *         the function to the values of the given
	 *         {@link IncompleteResult}s, and whose
	 *         {@link IncompletenessMonitor} is obtained by composing the
	 *         {@link IncompletenessMonitor}s of the input
	 * @throws E
	 */
	public static <RA, RB, O, E extends Throwable> IncompleteResult<O> compose(
			IncompleteResult<? extends RA> first,
			IncompleteResult<? extends RB> second,
			CheckedBiFunction<RA, RB, O, E> fn) throws E {
		return new IncompleteResult<O>(
				fn.apply(first.getValue(), second.getValue()),
				combine(first.getIncompletenessMonitor(),
						second.getIncompletenessMonitor()));
	}

	/**
	 * Compose the given {@link IncompleteResult}s using a function and
	 * combining their {@link IncompletenessMonitor}s in the order in which they
	 * are listed
	 * 
	 * @param <RA>
	 * @param <RB>
	 * @param <O>
	 * @param <E>
	 * @param first
	 * @param second
	 * @param third
	 * @param fn
	 * @return the {@link IncompleteResult} whose value is obtained by applying
	 *         the function to the values of the given
	 *         {@link IncompleteResult}s, and whose
	 *         {@link IncompletenessMonitor} is obtained by composing the
	 *         {@link IncompletenessMonitor}s of the input
	 * @throws E
	 */
	public static <RA, RB, RC, O, E extends Throwable> IncompleteResult<O> compose(
			IncompleteResult<? extends RA> first,
			IncompleteResult<? extends RB> second,
			IncompleteResult<? extends RC> third,
			CheckedTriFunction<RA, RB, RC, O, E> fn) throws E {
		return new IncompleteResult<O>(
				fn.apply(first.getValue(), second.getValue(), third.getValue()),
				combine(first.getIncompletenessMonitor(),
						second.getIncompletenessMonitor(),
						third.getIncompletenessMonitor()));
	}

	@FunctionalInterface
	public interface CheckedBiFunction<I, J, O, E extends Throwable> {
		O apply(I first, J second) throws E;
	}

	@FunctionalInterface
	public interface CheckedTriFunction<I, J, K, O, E extends Throwable> {
		O apply(I first, J second, K third) throws E;
	}

	/**
	 * Returns the value of an {@link IncompleteResult} while producing the
	 * necessary log messages about incompleteness
	 * 
	 * @param <R>
	 *            the type of the values of the result
	 * @param result
	 *            the {@link IncompleteResult} whose value should be returned
	 * @return the value of the given {@link IncompleteResult}
	 */
	static public <R> R getValue(IncompleteResult<? extends R> result) {
		R value = result.getValue();
		IncompletenessMonitor monitor = result.getIncompletenessMonitor();
		if (monitor.hasNewExplanation()) {
			monitor.explainIncompleteness(LOGGER_);
		}
		return value;
	}

	static public boolean getValue(QueryResult result)
			throws ElkQueryException {
		if (result.entailmentProved()) {
			return true;
		}
		// else check for incompleteness
		IncompletenessMonitor queryMonitor = result.getIncompletenessMonitor();
		if (queryMonitor.hasNewExplanation()) {
			LOGGER_.warn(
					"Cannot determine entailment of the query {}! See INFO for more details.",
					result.getQuery());
			queryMonitor.explainIncompleteness(LOGGER_);
		}
		return false;
	}

}
