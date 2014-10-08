/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * Recursively visits all inferences using {@link ProofReader}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RecursiveInferenceVisitor {

	public static void visitInferences(Reasoner reasoner, ElkClass subsumee, ElkClass subsumer, InferenceVisitor<?, ?> visitor) throws ElkException {
		DerivedExpression next = ProofReader.start(reasoner, subsumee, subsumer);
		// start recursive unwinding
		Queue<DerivedExpression> toDo = new LinkedList<DerivedExpression>();
		Set<DerivedExpression> done = new HashSet<DerivedExpression>();
		
		toDo.add(next);
		
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
