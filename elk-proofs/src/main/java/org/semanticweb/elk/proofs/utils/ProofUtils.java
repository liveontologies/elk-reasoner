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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.inferences.Inference;

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
	
	public static boolean isAsserted(Expression expr) {
		return expr.accept(new ExpressionVisitor<Void, Boolean>() {

			@Override
			public Boolean visit(AxiomExpression<?> expr, Void input) {
				return expr.isAsserted();
			}

			@Override
			public Boolean visit(LemmaExpression<?> expr, Void input) {
				// lemmas can not be asserted
				return false;
			}
			
		}, null);
	}

	public static boolean checkAsserted(Expression expr) throws ElkException {
		if (isAsserted(expr)) {
			return true;
		}
		
		// asserted expressions are either marked as asserted or have inferences which derive them from themselves
		for (Inference inf : expr.getInferences()) {
			for (Expression premise : inf.getPremises()) {
				if (premise.equals(expr) && isAsserted(premise)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static Iterable<? extends Inference> getInferences(Expression expr) {
		try {
			return expr.getInferences();
		} catch (ElkException e) {
			throw new RuntimeException(e);
		}
	}
}
