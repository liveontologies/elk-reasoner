/**
 * 
 */
package org.semanticweb.elk.proofs.utils;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkLemmaPrinter implements ElkLemmaVisitor<Void, String> {

	private static ElkLemmaPrinter PRINTER_ = new ElkLemmaPrinter();
	
	public static String print(ElkLemma lemma) {
		return lemma.accept(PRINTER_, null);
	}
	
	private static String print(ElkObject obj) {
		return OwlFunctionalStylePrinter.toString(obj);
	}
	
	public static String print(ElkComplexClassExpression obj) {
		return ElkComplexClassExpressionPrinter.print(obj);
	}
	
	@Override
	public String visit(ElkReflexivePropertyChainLemma lemma, Void input) {
		return String.format("ReflexivePropertyChain( %s )", print(lemma.getPropertyChain()));
	}

	@Override
	public String visit(ElkSubClassOfLemma lemma, Void input) {
		return String.format("SubClassOf( %s %s )", print(lemma.getSubClass()), print(lemma.getSuperClass()));
	}

	@Override
	public String visit(ElkSubPropertyChainOfLemma lemma, Void input) {
		return String.format("SubPropertyChainOf( %s %s )", print(lemma.getSubPropertyChain()), print(lemma.getSuperPropertyChain()));
	}

}
