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

/**
 * A collection of helper methods to manage incomplete reasoning results
 * 
 * @author Yevgeny Kazakov
 */
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
	 *            the {@link IncompletenessMonitor} to be combined
	 * @return an {@link IncompletenessMonitor} that monitors several other
	 *         {@link IncompletenessMonitor}s and reports incompleteness if some
	 *         of these monitors report incompleteness. The status messages
	 *         include information about a bounded number of these monitors.
	 */
	public static IncompletenessMonitor someOf(
			IncompletenessMonitor... monitors) {
		return new SomeOfIncompletenessMonitor(monitors);
	}

	/**
	 * @param monitors
	 *            the {@link IncompletenessMonitor} to be combined
	 * @return an {@link IncompletenessMonitor} that monitors several other
	 *         {@link IncompletenessMonitor}s. and reports incompleteness if
	 *         some of these monitors report incompleteness. The status messages
	 *         include information about a bounded number of these monitors.
	 * @see #someOf(IncompletenessMonitor...)
	 */
	public static IncompletenessMonitor someOf(
			Collection<IncompletenessMonitor> monitors) {
		return new SomeOfIncompletenessMonitor(monitors);
	}

	/**
	 * @param monitors
	 *            the {@link IncompletenessMonitor} to be combined
	 * @return an {@link IncompletenessMonitor} that monitors several other
	 *         {@link IncompletenessMonitor}s and reports incompleteness if some
	 *         of these monitors report incompleteness. The status messages
	 *         include information of monitors until the first monitor that
	 *         reports incompleteness.
	 */
	public static IncompletenessMonitor firstOf(
			IncompletenessMonitor... monitors) {
		return new FirstOfIncompletenessMonitor(monitors);
	}

	/**
	 * @param monitors
	 *            the {@link IncompletenessMonitor} to be combined
	 * @return an {@link IncompletenessMonitor} that monitors several other
	 *         {@link IncompletenessMonitor}s and reports incompleteness if some
	 *         of these monitors report incompleteness. The status messages
	 *         include information of monitors until the first monitor that
	 *         reports incompleteness.
	 * @see #firstOf(IncompletenessMonitor...)
	 */
	public static IncompletenessMonitor firstOf(
			Collection<IncompletenessMonitor> monitors) {
		return new FirstOfIncompletenessMonitor(monitors);
	}

	/**
	 * Compose the given {@link IncompleteResult}s using a function and
	 * combining their {@link IncompletenessMonitor}s in the order in which they
	 * are listed
	 * 
	 * @param <RA>
	 *            the type of the first input result
	 * @param <RB>
	 *            the type of the second input result
	 * @param <O>
	 *            the type of the output result
	 * @param <E>
	 *            the type of the exception that can occur during composition
	 * @param first
	 *            the first {@link IncompleteResult}
	 * @param second
	 *            the second {@link IncompleteResult}
	 * @param fn
	 *            the function used for composing the inputs of the
	 *            {@link IncompleteResult}s
	 * @return the {@link IncompleteResult} whose value is obtained by applying
	 *         the function to the values of the given
	 *         {@link IncompleteResult}s, and whose
	 *         {@link IncompletenessMonitor} is obtained by composing the
	 *         {@link IncompletenessMonitor}s of the input
	 * @throws E
	 *             if the operation was not successful
	 */
	public static <RA, RB, O, E extends Throwable> IncompleteResult<O> compose(
			IncompleteResult<? extends RA> first,
			IncompleteResult<? extends RB> second,
			CheckedBiFunction<RA, RB, O, E> fn) throws E {
		return new IncompleteResult<O>(
				fn.apply(first.getValue(), second.getValue()),
				firstOf(first.getIncompletenessMonitor(),
						second.getIncompletenessMonitor()));
	}

	/**
	 * Compose the given {@link IncompleteResult}s using a function and
	 * combining their {@link IncompletenessMonitor}s in the order in which they
	 * are listed
	 * 
	 * @param <RA>
	 *            the type of the first input result
	 * @param <RB>
	 *            the type of the second input result
	 * @param <RC>
	 *            the type of the third input result
	 * @param <O>
	 *            the type of the output result
	 * @param <E>
	 *            the type of the exception that can occur during composition
	 * @param first
	 *            the first {@link IncompleteResult}
	 * @param second
	 *            the second {@link IncompleteResult}
	 * @param third
	 *            the third {@link IncompleteResult}
	 * @param fn
	 *            the function used for composing the inputs of the
	 *            {@link IncompleteResult}s
	 * @return the {@link IncompleteResult} whose value is obtained by applying
	 *         the function to the values of the given
	 *         {@link IncompleteResult}s, and whose
	 *         {@link IncompletenessMonitor} is obtained by composing the
	 *         {@link IncompletenessMonitor}s of the input
	 * @throws E
	 *             if the operation was not successful
	 */
	public static <RA, RB, RC, O, E extends Throwable> IncompleteResult<O> compose(
			IncompleteResult<? extends RA> first,
			IncompleteResult<? extends RB> second,
			IncompleteResult<? extends RC> third,
			CheckedTriFunction<RA, RB, RC, O, E> fn) throws E {
		return new IncompleteResult<O>(
				fn.apply(first.getValue(), second.getValue(), third.getValue()),
				firstOf(first.getIncompletenessMonitor(),
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
		result.getIncompletenessMonitor().logStatus(LOGGER_);
		return result.getValue();
	}

	/**
	 * Returns the entailment value of the given {@link QueryResult} while
	 * producing the necessary log messages about its incompleteness.
	 * 
	 * @param result
	 *            the {@link QueryResult} whose entailment to be returned
	 * @return {@code true} if the entailment was proved and {@code false}
	 *         otherwise
	 * @throws ElkQueryException
	 *             if this {@link QueryResult} has not been computed yet
	 */
	static public boolean getValue(QueryResult result)
			throws ElkQueryException {
		result.getIncompletenessMonitor().logStatus(LOGGER_);
		return result.entailmentProved();
	}

}
