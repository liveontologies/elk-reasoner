/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DummyExpressionfactory implements DerivedExpressionFactory {

	@Override
	public DerivedAxiomExpression create(ElkAxiom axiom) {
		return new DerivedAxiomExpression(axiom, InferenceReader.DUMMY);
	}

	@Override
	public DerivedLemmaExpression create(ElkLemma lemma) {
		return new DerivedLemmaExpression(lemma, InferenceReader.DUMMY);
	}

}
