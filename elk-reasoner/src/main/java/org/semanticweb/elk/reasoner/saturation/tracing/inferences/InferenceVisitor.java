/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

/**
 * Visitor inference for the {@link Inference} hierarchy.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface InferenceVisitor<R> {

	public R visit(ClassInitializationInference inference);
	
	public R visit(SubClassOfInference inference);
	
	public R visit(ConjunctionCompositionInference inference);
	
	public R visit(ConjunctionDecompositionInference inference);
	
	public R visit(PropertyChainInference inference);
	
	public R visit(ReflexiveInference inference);
	
	public R visit(ExistentialInference inference);
	
	public R visit(BridgeInference inference);
}
