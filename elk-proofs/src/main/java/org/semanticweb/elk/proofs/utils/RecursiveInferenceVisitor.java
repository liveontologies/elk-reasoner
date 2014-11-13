/**
 * 
 */
package org.semanticweb.elk.proofs.utils;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * Recursively visits all inferences using {@link ProofReader}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RecursiveInferenceVisitor {

	public static void visitInferences(Reasoner reasoner, ElkClassExpression subsumee, ElkClassExpression subsumer, InferenceVisitor<?, ?> visitor) throws ElkException {
		DerivedExpression next = ProofReader.start(reasoner, subsumee, subsumer);
		// start recursive unwinding
		Queue<DerivedExpression> toDo = new LinkedList<DerivedExpression>();
		Set<DerivedExpression> done = new HashSet<DerivedExpression>();
		
		toDo.add(next);
		done.add(next);
		
		for (;;) {
			next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (Inference inf : next.getInferences()) {
				// pass to the client
				inf.accept(visitor, null);
				// recursively unwind premise inferences
				for (DerivedExpression premise : inf.getPremises()) {
					// proof reader guarantees pointer equality for structurally equivalent expressions so we avoid infinite loops here
					if (done.add(premise)) {
						toDo.add(premise);
					}
				}
			}
		}
	}
}
