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
