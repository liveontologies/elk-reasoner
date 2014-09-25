/**
 * 
 */
package org.semanticweb.elk.proofs.utils;

import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkComplexClassExpressionPrinter implements
		ElkComplexClassExpressionVisitor<Void, String> {
	
	// singleton printer
	private static ElkComplexClassExpressionPrinter PRINTER_ = new ElkComplexClassExpressionPrinter();
	
	public static String print(ElkComplexClassExpression ce) {
		return ce.accept(PRINTER_, null);
	}
	
	@Override
	public String visit(ElkComplexObjectSomeValuesFrom ce, Void input) {
		return String.format("ObjectSomeValuesFrom( %s %s )", OwlFunctionalStylePrinter.toString(ce.getPropertyChain()), OwlFunctionalStylePrinter.toString(ce.getFiller()));
	}

	@Override
	public String visit(ElkClassExpressionWrap ce, Void input) {
		return OwlFunctionalStylePrinter.toString(ce.getClassExpression());
	}

}
