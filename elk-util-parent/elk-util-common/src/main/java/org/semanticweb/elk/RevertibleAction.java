package org.semanticweb.elk;

import java.util.Objects;
import java.util.function.Supplier;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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
 * An action that performs operations that can be reverted
 * 
 * @author Yevgeny Kazakov
 *
 */
public interface RevertibleAction {

	/**
	 * Tries to apply the operations of this action
	 * 
	 * @return {@code true} if all operations have been successfully applied; if
	 *         {@code false} is returned, the action was not applied and this
	 *         call should not have any effect
	 */
	boolean apply();

	/**
	 * Reverts this action. This method should be called only if
	 * {{@link #apply()} has returned {@code true}
	 */
	void revert();

	/**
	 * Creates a new {@link RevertibleAction} with the given actions
	 * 
	 * @param applyAction
	 *            the function executed for the {@link #apply()} action of this
	 *            {@link RevertibleAction}
	 * @param revertAction
	 *            the function executed for the the {@link #revert()} action of
	 *            this {@link RevertibleAction}
	 * @return a new {@link RevertibleAction} with the given functions for
	 *         applying and reverting actions
	 */
	public static RevertibleAction create(Supplier<Boolean> applyAction,
			Runnable revertAction) {
		Objects.requireNonNull(applyAction);
		Objects.requireNonNull(revertAction);
		return new RevertibleAction() {

			@Override
			public boolean apply() {
				return applyAction.get();
			}

			@Override
			public void revert() {
				revertAction.run();
			}

		};

	}

	/**
	 * Creates a new {@link RevertibleAction} in which {@link #apply()} and
	 * {@link #revert()} use a given precondition
	 * 
	 * @param precondition
	 *            a boolean condition that is checked in the beginning of
	 *            {@link #apply()} and {@link #revert()} method; if the
	 *            condition returns {@code false}, the action is assumed to be
	 *            successfully applied (and can be reverted)
	 * @param postApplyAction
	 *            a function that is executed in {@link #apply()} if the
	 *            precondition returns {@code true}
	 * @param postRevertAction
	 *            a function that is executed in {@link #revert()} if the
	 *            precondition returns {@code true}
	 * @return a new {@link RevertibleAction} with the given conditional
	 *         functions for applying and reverting actions
	 */
	public static RevertibleAction create(Supplier<Boolean> precondition,
			Supplier<Boolean> postApplyAction, Runnable postRevertAction) {
		Objects.requireNonNull(precondition);
		Objects.requireNonNull(postApplyAction);
		Objects.requireNonNull(postRevertAction);
		return new RevertibleAction() {

			@Override
			public boolean apply() {
				return !precondition.get() || postApplyAction.get();
			}

			@Override
			public void revert() {
				if (precondition.get()) {
					postRevertAction.run();
				}
			}

		};

	}

	/**
	 * Appends this {@link RevertibleAction} to another
	 * {@link RevertibleAction}. The resulting {@link RevertibleAction} is
	 * successfully applied only if both {@link RevertibleAction} it consists of
	 * are successfully applied.
	 * 
	 * @param other
	 *            a {@link RevertibleAction} that should be applied after a
	 *            successful application of this {@link RevertibleAction}
	 * 
	 * @return a new {@link RevertibleAction} that is obtained by appending
	 *         another {@link RevertibleAction}
	 */
	default RevertibleAction then(RevertibleAction other) {
		Objects.requireNonNull(other);
		return new RevertibleAction() {

			@Override
			public boolean apply() {
				if (!RevertibleAction.this.apply()) {
					return false;
				}
				if (!other.apply()) {
					RevertibleAction.this.revert();
					return false;
				}
				return true;
			}

			@Override
			public void revert() {
				other.revert();
				RevertibleAction.this.revert();
			}

		};
	}

}
