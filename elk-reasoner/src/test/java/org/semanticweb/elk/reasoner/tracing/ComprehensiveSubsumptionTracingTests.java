/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.tracing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject.Factory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.completeness.TestIncompleteness;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * Checks equivalence of classes in each taxonomy node and subsumptions between
 * direct sub-nodes using the provided {@link TracingTestVisitor}. Used for
 * tracing tests and benchmarking.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class ComprehensiveSubsumptionTracingTests implements TracingTests {

	private Reasoner reasoner_;

	public ComprehensiveSubsumptionTracingTests(Reasoner reasoner)
			throws ElkException {
		this.reasoner_ = reasoner;
	}

	@Override
	public void accept(TracingTestVisitor visitor) throws Exception {
		if (TestIncompleteness.getValue(reasoner_.isInconsistent())) {
			Factory elkFactory = reasoner_.getElkFactory();
			visitor.testSubsumption(elkFactory.getOwlThing(),
					elkFactory.getOwlNothing());
			return;
		}
		// else

		Taxonomy<ElkClass> classTaxonomy = TestIncompleteness
				.getValue(reasoner_.getTaxonomy());
		/*
		 * Store a copy of the taxonomy, because it may change during the
		 * reasoning.
		 */
		final Collection<List<ElkClass>> equivalences = new ArrayList<List<ElkClass>>();
		final List<ElkClass> subClasses = new ArrayList<ElkClass>();
		final List<ElkClass> superClasses = new ArrayList<ElkClass>();
		Set<TaxonomyNode<ElkClass>> visited = new HashSet<TaxonomyNode<ElkClass>>();
		Queue<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();

		toDo.add(classTaxonomy.getTopNode());

		for (;;) {
			TaxonomyNode<ElkClass> next = toDo.poll();

			if (next == null) {
				break;
			}

			int nextSize = next.size();
			if (nextSize > 1) {
				List<ElkClass> equivalent = new ArrayList<ElkClass>(nextSize);
				for (ElkClass member : next) {
					equivalent.add(member);
				}
				equivalences.add(equivalent);
			}

			for (TaxonomyNode<ElkClass> subNode : next.getDirectSubNodes()) {
				subClasses.add(subNode.getCanonicalMember());
				superClasses.add(next.getCanonicalMember());
				if (visited.add(subNode)) {
					toDo.add(subNode);
				}
			}
		}

		for (final List<ElkClass> equivalence : equivalences) {
			visitor.testEquivalence(equivalence);
		}

		for (int index = 0; index < subClasses.size(); index++) {
			visitor.testSubsumption(subClasses.get(index),
					superClasses.get(index));
		}

	}

}
