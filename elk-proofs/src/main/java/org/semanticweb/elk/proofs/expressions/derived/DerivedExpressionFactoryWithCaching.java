/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.derived.entries.ExpressionEntryFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.inferences.InferenceReader;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DerivedExpressionFactoryWithCaching implements DerivedExpressionFactory {

	private final KeyEntryHashSet<DerivedAxiomExpression> axiomLookup_;
	
	private final KeyEntryHashSet<DerivedLemmaExpression> lemmaLookup_;
	
	private final InferenceReader reader_;

	public DerivedExpressionFactoryWithCaching(InferenceReader reader) {
		axiomLookup_ = new KeyEntryHashSet<DerivedAxiomExpression>(new ExpressionEntryFactory<DerivedAxiomExpression>(), 128);
		lemmaLookup_ = new KeyEntryHashSet<DerivedLemmaExpression>(new ExpressionEntryFactory<DerivedLemmaExpression>(), 32);
		reader_ = reader;
	}
	
	@Override
	public DerivedAxiomExpression create(ElkAxiom axiom) {
		DerivedAxiomExpression newExpr = new DerivedAxiomExpression(axiom, reader_);
		
		return axiomLookup_.merge(newExpr);
	}

	@Override
	public DerivedLemmaExpression create(ElkLemma lemma) {
		DerivedLemmaExpression newExpr = new DerivedLemmaExpression(lemma, reader_);
		
		return lemmaLookup_.merge(newExpr);
	}

}
