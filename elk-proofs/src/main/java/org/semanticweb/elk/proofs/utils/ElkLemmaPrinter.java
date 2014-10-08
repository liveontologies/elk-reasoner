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
