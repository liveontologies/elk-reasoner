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

import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ProofUtils {

	public static ElkObjectProperty asObjectProperty(ElkObjectPropertyExpression expr) {
		return expr.accept(new ElkObjectPropertyExpressionVisitor<ElkObjectProperty>() {

			@Override
			public ElkObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
				throw new IllegalArgumentException("Inverses aren't in EL");
			}

			@Override
			public ElkObjectProperty visit(ElkObjectProperty elkObjectProperty) {
				return elkObjectProperty;
			}
			
		});
	}
	
	public static boolean isAsserted(DerivedExpression expr) {
		return expr.accept(new ExpressionVisitor<Void, Boolean>() {

			@Override
			public Boolean visit(DerivedAxiomExpression<?> expr, Void input) {
				return expr.isAsserted();
			}

			@Override
			public Boolean visit(LemmaExpression expr, Void input) {
				// lemmas can not be asserted
				return false;
			}
			
		}, null);
	}

}
