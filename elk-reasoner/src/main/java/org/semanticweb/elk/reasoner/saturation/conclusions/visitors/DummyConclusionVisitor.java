package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * A {@link ConclusionVisitor} that does nothing
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DummyConclusionVisitor<I> extends
		AbstractConclusionVisitor<I, Void> {

	@Override
	Void defaultVisit(Conclusion conclusion, I input) {
		return null;
	}

}
