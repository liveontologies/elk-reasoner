/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.ArrayDeque;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

//TODO: Document this class
//TODO: Add progress monitor
/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
class RoleHierarchyComputationEngine implements
		InputProcessor<IndexedPropertyChain> {

	@Override
	public void submit(IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturated = new SaturatedPropertyChain(ipc);
		ipc.setSaturated(saturated);

		// compute all subproperties
		ArrayDeque<IndexedPropertyChain> queue = new ArrayDeque<IndexedPropertyChain>();
		saturated.derivedSubProperties.add(ipc);
		queue.addLast(ipc);
		while (!queue.isEmpty()) {
			IndexedPropertyChain r = queue.removeLast();
			if (r.getToldSubProperties() != null)
				for (IndexedPropertyChain s : r.getToldSubProperties())
					if (saturated.derivedSubProperties.add(s))
						queue.addLast(s);
		}

		// compute all superproperties
		queue.clear();
		saturated.derivedSuperProperties.add(ipc);
		queue.addLast(ipc);
		while (!queue.isEmpty()) {
			IndexedPropertyChain r = queue.removeLast();
			if (r.getToldSuperProperties() != null)
				for (IndexedPropertyChain s : r.getToldSuperProperties())
					if (saturated.derivedSuperProperties.add(s))
						queue.addLast(s);
		}

	}

	@Override
	public void process() throws InterruptedException {
		// nothing to do here, everything should be processed during the
		// submission
	}

	@Override
	public boolean canProcess() {
		return false;
	}
}
