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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

//TODO: Document this class
/**
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class RedundantCompositionsElimination
		extends
		ReasonerComputation<Map.Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>, RedundantCompositionsEliminationEngine> {

	public RedundantCompositionsElimination(
			Collection<? extends Entry<Pair<IndexedPropertyChain, IndexedPropertyChain>, Vector<IndexedPropertyChain>>> inputs,
			Interrupter interrupter, ProgressMonitor progressMonitor) {
		super(inputs, new RedundantCompositionsEliminationEngine(),
				interrupter, 1, progressMonitor);
	}

}
