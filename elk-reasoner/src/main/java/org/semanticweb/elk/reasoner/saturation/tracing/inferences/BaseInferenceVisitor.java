/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseInferenceVisitor<R> implements InferenceVisitor<R> {

	protected R defaultVisit(Inference inf) {
		return null;
	}
	
	@Override
	public R visit(ClassInitializationInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(SubClassOfInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(ConjunctionCompositionInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(ConjunctionDecompositionInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(PropertyChainInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(ReflexiveInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(ExistentialInference inference) {
		return defaultVisit(inference);
	}

	@Override
	public R visit(BridgeInference inference) {
		return defaultVisit(inference);
	}

}
