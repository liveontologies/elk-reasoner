/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A wrapper around the underlying {@link Conclusion} which implements equals
 * and hashCode. Useful for storing and looking up conclusions in collections.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionEntry implements Conclusion {

	private static final ConclusionEqualityChecker equalityChecker = new ConclusionEqualityChecker();
	private static final ConclusionHashGenerator hashGen = new ConclusionHashGenerator();
	
	private final Conclusion conclusion_;
	
	ConclusionEntry(Conclusion c) {
		conclusion_ = c;
	}
	
	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C parameter) {
		return conclusion_.accept(visitor, parameter);
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return conclusion_.getSourceContext(contextWhereStored);
	}

	@Override
	public int hashCode() {
		return conclusion_.accept(hashGen, null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conclusion) {
			return conclusion_.accept(equalityChecker, (Conclusion) obj);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return conclusion_.toString();
	}

	
}
