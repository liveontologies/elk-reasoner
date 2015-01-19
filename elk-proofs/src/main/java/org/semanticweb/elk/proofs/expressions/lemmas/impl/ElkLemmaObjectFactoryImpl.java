/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas.impl;
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkLemmaObjectFactoryImpl implements ElkLemmaObjectFactory {

	@Override
	public ElkReflexivePropertyChainLemma getReflexivePropertyChainLemma(
			ElkSubObjectPropertyExpression chain) {
		return new ElkReflexivePropertyChainLemmaImpl(chain);
	}

	@Override
	public ElkSubPropertyChainOfLemma getSubPropertyChainOfLemma(
			ElkSubObjectPropertyExpression subchain,
			ElkSubObjectPropertyExpression superchain) {
		return new ElkSubPropertyChainOfLemmaImpl(subchain, superchain);
	}

	@Override
	public ElkSubClassOfLemma getSubClassOfLemma(
			ElkClassExpression subclass,
			ElkComplexClassExpression superclass) {
		return new ElkSubClassOfLemmaImpl(subclass, superclass);
	}

	@Override
	public ElkComplexObjectSomeValuesFrom getComplexObjectSomeValuesFrom(
			ElkObjectPropertyChain chain, ElkClassExpression filler) {
		return new ElkComplexObjectSomeValuesFromImpl(chain, filler);
	}

}
