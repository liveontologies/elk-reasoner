/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;

import org.semanticweb.elk.proofs.inferences.ClassInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ClassInitialization;
import org.semanticweb.elk.proofs.inferences.classes.ClassSubsumption;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionDecomposition;
import org.semanticweb.elk.proofs.inferences.classes.DisjointnessContradiction;
import org.semanticweb.elk.proofs.inferences.classes.DisjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialCompositionViaChain;
import org.semanticweb.elk.proofs.inferences.classes.InconsistentDisjointness;
import org.semanticweb.elk.proofs.inferences.classes.NegationContradiction;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ThingInitialization;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;

/**
 * The visitor which maps the low level {@link ClassInference}s onto the higher
 * level {@link Inference}s reported to the user.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceMapper implements
		ClassInferenceVisitor<IndexedClassExpression, Void> {

	private final ClassInferenceVisitor<?, ?> userVisitor;

	public InferenceMapper(ClassInferenceVisitor<?, ?> visitor) {
		userVisitor = visitor;
	}

	@Override
	public Void visit(ClassInitialization inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ThingInitialization inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ClassSubsumption inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ConjunctionComposition inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ConjunctionDecomposition inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(DisjointnessContradiction inf,
			IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(DisjunctionComposition inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ExistentialComposition inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ExistentialCompositionViaChain inf,
			IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(InconsistentDisjointness inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(NegationContradiction inf, IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(ReflexiveExistentialComposition inf,
			IndexedClassExpression input) {
		// TODO Auto-generated method stub
		return null;
	}

}
