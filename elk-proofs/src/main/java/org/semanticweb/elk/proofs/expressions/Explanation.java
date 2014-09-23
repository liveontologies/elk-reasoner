/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * A collection of axioms which entailes a particular conclusion which may not
 * be representable as an axiom.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class Explanation {

	private final Iterable<ElkAxiom> axioms_;

	public Explanation(Iterable<ElkAxiom> axioms) {
		axioms_ = axioms;
	}
	
	public Explanation(ElkAxiom axiom) {
		axioms_ = Collections.singletonList(axiom);
	}

	public Iterable<ElkAxiom> getAxioms() {
		return axioms_;
	}
}
