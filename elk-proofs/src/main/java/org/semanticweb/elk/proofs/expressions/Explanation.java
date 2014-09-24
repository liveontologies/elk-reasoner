/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;
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

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;

/**
 * A collection of axioms which entailes a particular conclusion which may not
 * be representable as an axiom.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class Explanation {

	private final Iterable<ElkAxiom> axioms_;

	public Explanation(Iterable<ElkAxiom> axioms) {
		axioms_ = axioms;
	}
	
	public Explanation(ElkAxiom axiom) {
		axioms_ = Collections.singletonList(axiom);
	}

	public Iterable<ElkAxiom> getAxioms() {
		return axioms_;
	}

	@Override
	public String toString() {
		return "{" + Operations.toString(Operations.map(axioms_, new Transformation<ElkAxiom, String>() {

			@Override
			public String transform(ElkAxiom element) {
				return OwlFunctionalStylePrinter.toString(element);
			}
			
		})) + "}";
	}
	
}
