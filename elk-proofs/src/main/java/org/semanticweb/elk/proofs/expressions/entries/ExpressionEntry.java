/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.entries;

import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.StructuralEquivalenceHasher;
import org.semanticweb.elk.util.collections.entryset.StrongKeyEntry;


/**
 * A wrapper around an {@link Expression} object which is convenient to store
 * and find in collections.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionEntry<K extends Expression> extends StrongKeyEntry<K, K> {

	public ExpressionEntry(K key) {
		super(key);
	}
	
	@Override
	public int computeHashCode() {
		return new StructuralEquivalenceHasher().hashCode(key);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (object == null || !(object instanceof ExpressionEntry<?>)) {
			return false;
		}
		
		return new StructuralEquivalenceChecker().equal(key, ((ExpressionEntry<?>) object).key);
	}
	
}
