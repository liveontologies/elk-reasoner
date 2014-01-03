package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
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
public class DummyConclusionVisitor implements ConclusionVisitor<Void> {

	private final static DummyConclusionVisitor INSTANCE_ = new DummyConclusionVisitor();

	private DummyConclusionVisitor() {

	}

	public static ConclusionVisitor<Void> getInstance() {
		return INSTANCE_;
	}

	@Override
	public Void visit(ComposedSubsumer negSCE, Context context) {
		return null;
	}

	@Override
	public Void visit(DecomposedSubsumer posSCE, Context context) {
		return null;
	}

	@Override
	public Void visit(BackwardLink link, Context context) {
		return null;
	}

	@Override
	public Void visit(ForwardLink link, Context context) {
		return null;
	}

	@Override
	public Void visit(Contradiction bot, Context context) {
		return null;
	}

	@Override
	public Void visit(Propagation propagation, Context context) {
		return null;
	}

	@Override
	public Void visit(DisjointSubsumer disjointnessAxiom, Context context) {
		return null;
	}

}
