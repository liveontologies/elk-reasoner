package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
public class DummyConclusionVisitor extends AbstractConclusionVisitor<Void> {

	private final static DummyConclusionVisitor INSTANCE_ = new DummyConclusionVisitor();

	private DummyConclusionVisitor() {

	}

	public static ConclusionVisitor<Void> getInstance() {
		return INSTANCE_;
	}

	@Override
	Void defaultVisit(Conclusion conclusion, Context context) {
		return null;
	}

}
