/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.proofs.expressions.entries.ExpressionEntryFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionFactoryWithCaching implements ExpressionFactory {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(ExpressionFactoryWithCaching.class);

	private final KeyEntryHashSet<AxiomExpression> axiomLookup_;
	
	private final KeyEntryHashSet<LemmaExpression> lemmaLookup_;

	public ExpressionFactoryWithCaching() {
		axiomLookup_ = new KeyEntryHashSet<AxiomExpression>(new ExpressionEntryFactory<AxiomExpression>(), 128);
		lemmaLookup_ = new KeyEntryHashSet<LemmaExpression>(new ExpressionEntryFactory<LemmaExpression>(), 32);
	}
	
	@Override
	public AxiomExpression create(ElkAxiom axiom) {
		AxiomExpression newExpr = new AxiomExpression(axiom);
		
		return axiomLookup_.merge(newExpr);
	}

	@Override
	public LemmaExpression create(ElkLemma lemma) {
		LemmaExpression newExpr = new LemmaExpression(lemma);
		
		return lemmaLookup_.merge(newExpr);
	}

}
