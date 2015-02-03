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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkSubPropertyChainOfLemmaImpl implements
		ElkSubPropertyChainOfLemma {

	private final ElkSubObjectPropertyExpression sub_; 
	
	private final ElkObjectPropertyChain sup_;
	
	public ElkSubPropertyChainOfLemmaImpl(ElkSubObjectPropertyExpression sub, ElkObjectPropertyChain sup) {
		sub_ = sub;
		sup_ = sup;
	}
	
	@Override
	public ElkSubObjectPropertyExpression getSubPropertyChain() {
		return sub_;
	}

	@Override
	public ElkObjectPropertyChain getSuperPropertyChain() {
		return sup_;
	}
	
	@Override
	public <I, O> O accept(ElkLemmaVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
