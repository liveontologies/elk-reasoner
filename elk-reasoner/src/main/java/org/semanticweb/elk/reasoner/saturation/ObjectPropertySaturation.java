/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.util.AbstractConcurrentComputation;
import org.semanticweb.elk.util.ArrayHashSet;

/**
 * @author Frantisek Simancik
 *
 */
public class ObjectPropertySaturation 
		extends AbstractConcurrentComputation<IndexedObjectProperty> {

	protected OntologyIndex ontologyIndex;
	
	public ObjectPropertySaturation(ExecutorService executor, int maxWorkers, OntologyIndex ontologyIndex) {
		super(executor, maxWorkers, 0, 128);
		this.ontologyIndex = ontologyIndex;
		for (IndexedObjectProperty iop : ontologyIndex.getIndexedObjectProperties())
			iop.resetSaturated();
	}

	@Override
	protected void process(IndexedObjectProperty iop) {
		SaturatedObjectProperty sop = new SaturatedObjectProperty(iop);
		iop.setSaturated(sop);

		//compute all subproperties
		ArrayDeque<IndexedObjectProperty> queue = new ArrayDeque<IndexedObjectProperty>();
		sop.derivedSubObjectProperties.add(iop);
		queue.addLast(iop);
		while (!queue.isEmpty()) {
			IndexedObjectProperty r = queue.removeLast();
			if (r.getToldSubObjectProperties() != null)
				for (IndexedObjectProperty s : r.getToldSubObjectProperties())
					if (sop.derivedSubObjectProperties.add(s))
						queue.addLast(s);
		}

		//find transitive subproperties
		for (IndexedObjectProperty r : sop.derivedSubObjectProperties)
			if (r.isTransitive()) {
				if (sop.transitiveSubObjectProperties == null)
					sop.transitiveSubObjectProperties = new ArrayHashSet<IndexedObjectProperty> ();
				sop.transitiveSubObjectProperties.add(r);
			}

		//compute all superproperties
		queue.clear();
		sop.derivedSuperObjectProperties.add(iop);
		queue.addLast(iop);
		while (!queue.isEmpty()) {
			IndexedObjectProperty r = queue.removeLast();
			if (r.getToldSuperObjectProperties() != null)
				for (IndexedObjectProperty s : r.getToldSuperObjectProperties())
					if (sop.derivedSuperObjectProperties.add(s))
						queue.addLast(s);
		}

		//find transitive superproperties
		for (IndexedObjectProperty r : sop.derivedSuperObjectProperties)
			if (r.isTransitive()) {
				if (sop.transitiveSuperObjectProperties == null)
					sop.transitiveSuperObjectProperties = new ArrayHashSet<IndexedObjectProperty> ();
				sop.transitiveSuperObjectProperties.add(r);
			}
	}

}
