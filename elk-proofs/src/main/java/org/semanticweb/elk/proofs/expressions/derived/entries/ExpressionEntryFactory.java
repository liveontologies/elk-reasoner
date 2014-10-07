/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;

import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.util.collections.entryset.KeyEntryFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ExpressionEntryFactory<K extends Expression> implements KeyEntryFactory<K> {

	@Override
	public ExpressionEntry<K> createEntry(K key) {
		return new ExpressionEntry<K>(key);
	}

}
