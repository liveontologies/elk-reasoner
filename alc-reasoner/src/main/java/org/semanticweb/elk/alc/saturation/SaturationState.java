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
import java.util.Map;
import java.util.Queue;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationState implements ConclusionProducer {

	private final Map<Root, Context> contextsByRoots_;

	/**
	 * {@link Context}s that have unprocessed {@link Conclusion}s, i.e., for
	 * which {@link Context#takeToDo()} returns not {@code null}
	 */
	private final Queue<Context> activeContexts_;

	public SaturationState() {
		this.contextsByRoots_ = new ArrayHashMap<Root, Context>(1024);
		this.activeContexts_ = new ArrayDeque<Context>();
	}

	@Override
	public void produce(Root root, Conclusion conclusion) {
		Context context = getCreateContext(root);
		if (context.addToDo(conclusion))
			activeContexts_.add(context);
	}

	public Context pollActiveContext() {
		return activeContexts_.poll();
	}

	Context getContext(Root root) {
		return contextsByRoots_.get(root);
	}

	Context getCreateContext(Root root) {
		Context result = getContext(root);
		if (result != null)
			return result;
		// else create new
		result = new Context(root);
		contextsByRoots_.put(root, result);
		return result;
	}

}
