/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import java.util.Collections;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class EmptyExpression implements Expression {

	@Override
	public Iterable<Explanation> getExplanations() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return "";
	}

}
