/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SingleAxiomExpression implements Expression {

	private final Explanation singleton_;
	
	public SingleAxiomExpression(ElkAxiom ax) {
		singleton_ = new Explanation(Collections.singletonList(ax));
	}
	
	public ElkAxiom getAxiom() {
		return singleton_.getAxioms().iterator().next();
	}
	
	@Override
	public List<Explanation> getExplanations() {
		return Collections.singletonList(singleton_);
	}
	
}
