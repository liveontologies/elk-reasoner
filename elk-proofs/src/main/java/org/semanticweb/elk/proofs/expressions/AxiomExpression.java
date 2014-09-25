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

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AxiomExpression implements Expression {

	private final Explanation singleton_;
	
	public AxiomExpression(ElkAxiom ax) {
		singleton_ = new Explanation(Collections.singletonList(ax));
	}
	
	public ElkAxiom getAxiom() {
		return singleton_.getAxioms().iterator().next();
	}
	
	@Override
	public String toString() {
		return OwlFunctionalStylePrinter.toString(singleton_.getAxioms().iterator().next());
	}
	
}
