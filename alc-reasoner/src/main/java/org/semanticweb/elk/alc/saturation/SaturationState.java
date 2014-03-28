package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ExternalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.LocalPossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleConclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationState implements ExternalConclusionProducer {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationState.class);

	/**
	 * if {@code true}, some statistics will be printed
	 */
	private static final boolean PRINT_STATS_ = false;

	/**
	 * A map for root lookup
	 */
	private final Map<Root, Root> existingRoots_;

	/**
	 * Contains all {@link Context}s that are not saturated, i.e., for which
	 * {@link Context#isSaturated()} returns {@code false}
	 */
	private final Queue<Context> nonSaturatedContexts_;

	// statistics counters
	private int contextCount = 0;

	private int rootSizesSum = 0;

	private int maxRootSize = 0;

	/**
	 * {@link Context}s that have unprocessed {@link Conclusion}s, i.e., for
	 * which {@link Context#takeToDo()} returns not {@code null}
	 */
	private final Queue<Context> activeContexts_;

	/**
	 * {@link Context}s that have unprocessed {@link PossibleConclusion}s
	 */
	private final Queue<Context> possibleContexts_;

	private final ExternalDeterministicConclusion init_ = new ContextInitializationImpl();

	public SaturationState() {
		this.existingRoots_ = new HashMap<Root, Root>(1024);
		this.activeContexts_ = new ArrayDeque<Context>(1024);
		this.possibleContexts_ = new ArrayDeque<Context>(1024);
		this.nonSaturatedContexts_ = new ArrayDeque<Context>(1024);
	}

	@Override
	public void produce(Root root, ExternalDeterministicConclusion conclusion) {
		if (isBlocked(root, conclusion))
			return;
		produce(getCreateContext(root), conclusion);
	}

	@Override
	public void produce(Root root, ExternalPossibleConclusion conclusion) {
		if (isBlocked(root, conclusion))
			return;
		produce(getCreateContext(root), conclusion);
	}

	public void produce(Root root, LocalPossibleConclusion conclusion) {
		if (isBlocked(root, conclusion))
			return;
		produce(getCreateContext(root), conclusion);
	}

	private void produce(Context context,
			ExternalDeterministicConclusion conclusion) {
		LOGGER_.trace("{}: produced {}", context, conclusion);
		if (context.addToDo(conclusion)) {
			LOGGER_.trace("{}: activated deterministic", context);
			activeContexts_.add(context);
		}
	}

	void produce(Context context, PossibleConclusion conclusion) {
		LOGGER_.trace("{}: produced {}", context, conclusion);
		if (context.addToGuess(conclusion)) {
			LOGGER_.trace("{}: activated possible", context);
			possibleContexts_.add(context);
		}
	}

	/**
	 * @param root
	 * @param conclusion
	 * @return {@code true} if the given {@link Conclusion} should not be
	 *         produced for the given {@link Root}, i.e., when the
	 *         {@link Context} for the {@link Root} exists and is already
	 *         saturated.
	 */
	private boolean isBlocked(Root root, Conclusion conclusion) {
		/*Root sourceRoot = conclusion.getSourceRoot(root);
		Context context = getContext(sourceRoot);
		if (context != null && context.isSaturated()) {
			LOGGER_.trace("{}: not produced {}: context {} is saturated", root,
					conclusion, context);
			return true;
		}*/
		// else
		return false;
	}

	public Context pollActiveContext() {
		return activeContexts_.poll();
	}

	public Context pollPossibleContext() {
		return possibleContexts_.poll();
	}

	public Context getContext(IndexedClassExpression positiveMember,
			IndexedClassExpression... negativeMembers) {
		return getContext(new Root(positiveMember, negativeMembers));
	}

	/**
	 * Sets the given {@link Context} as non-saturated. After calling this
	 * method, {@link Context#isSaturated()} should return {@code false};
	 * 
	 * @param context
	 * @return {@code true} if the given {@link Context} was saturated before.
	 */
	public boolean setNotSaturated(Context context) {
		if (!context.isSaturated())
			return false;
		// else
		context.setNotSaturated();
		nonSaturatedContexts_.add(context);
		return true;
	}

	/**
	 * @return some {@link Context} that was non saturated and which becomes
	 *         saturated after calling of this method, or {@code null} if all
	 *         {@link Contexts} were marked as saturated.
	 */
	public Context takeAndSetSaturated() {
		Context context = nonSaturatedContexts_.poll();
		if (context == null)
			return null;
		context.setSaturated();
		return context;
	}

	Context getContext(Root root) {
		Context result = root.getContext();
		if (result != null)
			return result;
		// else, try to find equal root
		Root equalRoot = existingRoots_.get(root);
		if (equalRoot == null)
			return null;
		// else
		return equalRoot.getContext();
	}

	Context getCreateContext(Root root) {
		Context context = getContext(root);
		if (context != null)
			return context;
		// else create new
		context = new Context(root);
		root.setContext(context);
		existingRoots_.put(root, root);
		nonSaturatedContexts_.add(context);
		produce(root, init_);
		if (PRINT_STATS_) {
			contextCount++;
			int rootSize = root.size();
			if (rootSize > maxRootSize)
				maxRootSize = rootSize;
			rootSizesSum += rootSize;
			if ((contextCount / 1000) * 1000 == contextCount)
				LOGGER_.info(
						"{} contexts created (evarage root size: {}, max root size: {})",
						contextCount, rootSizesSum / contextCount, maxRootSize);
		}
		return context;
	}

	void checkSaturation() {
		for (Root root : existingRoots_.values()) {
			Context context = root.getContext();
			if (context.isInconsistent()) {
				for (IndexedObjectProperty backwardRelation : context
						.getBackwardLinks().keySet()) {
					for (Root backwardRoot : context.getBackwardLinks().get(
							backwardRelation)) {
						Context backwardContext = backwardRoot.getContext();
						if (backwardContext.isInconsistent())
							continue;
						// else
						LOGGER_.error(
								"{}({}): inconsistent, but its parent {}({}) is not (inconsistent successors: {})",
								root, root.hashCode(), backwardRoot,
								backwardRoot.hashCode(),
								backwardContext.getInconsistentSuccessors());
					}
				}

			}
		}
	}

}
