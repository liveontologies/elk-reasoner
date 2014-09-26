/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ExpressionEntry implements Expression {

	private static final ExpressionHasher HASHER_ = null;
			
	private static final ExpressionEqualityChecker EQUALITY_CHECKER = null;
	
	private final Expression expr_;
	
	public ExpressionEntry(Expression expr) {
		expr_ = expr;
	}
	
	@Override
	public <I, O> O accept(ExpressionVisitor<I, O> visitor, I input) {
		return expr_.accept(visitor, input);
	}

	@Override
	public int hashCode() {
		return HASHER_.hashCode(expr_);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Expression)) {
			return false;
		}
		
		return EQUALITY_CHECKER.equal(expr_, (Expression) obj);
	}

	
}
