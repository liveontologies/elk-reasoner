/**
 * 
 */
package org.semanticweb.elk.proofs.utils;

import java.util.Arrays;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.MultiAxiomExpression;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;

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
	
	public static Expression<?> merge(Expression<?>... expressions) {
		return new MultiAxiomExpression<ElkAxiom>(Operations.concat(Operations.map(Arrays.asList(expressions), new Transformation<Expression<?>, Iterable<? extends ElkAxiom>>() {

			@Override
			public Iterable<? extends ElkAxiom> transform(Expression<?> expr) {
				return expr.getAxioms();
			}
			
		})));
	}
}
