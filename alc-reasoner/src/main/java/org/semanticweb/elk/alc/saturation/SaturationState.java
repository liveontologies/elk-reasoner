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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleConclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationState implements ConclusionProducer {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationState.class);

	private final Map<Root, Root> existingRoots_;

	private int contextCount = 0;

	/**
	 * {@link Context}s that have unprocessed {@link Conclusion}s, i.e., for
	 * which {@link Context#takeToDo()} returns not {@code null}
	 */
	private final Queue<Context> activeContexts_;

	/**
	 * {@link Context}s that have unprocessed {@link PossibleConclusion}s
	 */
	private final Queue<Context> possibleContexts_;

	private final Conclusion init_ = new ContextInitializationImpl();

	private Context currentContext_ = null;

	public SaturationState() {
		this.existingRoots_ = new HashMap<Root, Root>(1024);
		this.activeContexts_ = new ArrayDeque<Context>(1024);
		this.possibleContexts_ = new ArrayDeque<Context>(1024);
	}

	@Override
	public void produce(Root root, Conclusion conclusion) {
		produce(getCreateContext(root), conclusion);
	}

	void produce(Context context, Conclusion conclusion) {
		LOGGER_.trace("{}: produced {}", context, conclusion);
		if (conclusion instanceof PossibleConclusion) {
			if (context.addToGuess((PossibleConclusion) conclusion)) {
				LOGGER_.trace("{}: activated possible", context);
				possibleContexts_.add(context);
			}
		} else {
			if (context.addToDo(conclusion)) {
				LOGGER_.trace("{}: activated deterministic", context);
				activeContexts_.add(context);
			}
		}
	}

	public Context pollActiveContext() {
		currentContext_ = activeContexts_.poll();
		return currentContext_;
	}

	public Context pollPossibleContext() {
		currentContext_ = possibleContexts_.poll();
		return currentContext_;
	}

	public Context getContext(IndexedClassExpression positiveMember,
			IndexedClassExpression... negativeMembers) {
		return getContext(new Root(positiveMember, negativeMembers));
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
		Context result = getContext(root);
		if (result != null)
			return result;
		// else create new
		contextCount++;
		if ((contextCount / 1000) * 1000 == contextCount)
			LOGGER_.info("{} contexts created", contextCount);
		result = new Context(root);
		root.setContext(result);
		existingRoots_.put(root, root);
		produce(root, init_);
		return result;
	}

	void discard(Root root) {
		Context context = getContext(root);
		if (context.getBackwardLinks().isEmpty()) {
			existingRoots_.remove(root);
			contextCount--;
		}
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
								backwardRoot.hashCode(), backwardContext
										.getInconsistentSuccessors().keySet());
					}
				}

			}
		}
	}
}
