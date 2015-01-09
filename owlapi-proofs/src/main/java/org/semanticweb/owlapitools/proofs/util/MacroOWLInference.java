/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;

import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class MacroOWLInference implements OWLInference {

	private final String name_;
	
	private final OWLExpression conclusion_;
	
	private final List<? extends OWLExpression> premises_;
	
	public MacroOWLInference(String name, OWLExpression conclusion, List<? extends OWLExpression> premises) {
		name_ = name;
		conclusion_ = conclusion;
		premises_ = premises;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return conclusion_;
	}

	@Override
	public Collection<? extends OWLExpression> getPremises() {
		return premises_;
	}

	@Override
	public String getName() {
		return name_;
	}

	@Override
	public String toString() {
		return name_ + premises_ + " |- " + conclusion_;
	}
}
