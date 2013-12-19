/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;



/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TracedConclusionVisitor<R, C> {

	public R visit(InitializationSubsumer conclusion, C parameter);
	
	public R visit(SubClassOfSubsumer conclusion, C parameter);
	
	public R visit(ComposedConjunction conclusion, C parameter);
	
	public R visit(DecomposedConjunction conclusion, C parameter);
	
	public R visit(PropagatedSubsumer conclusion, C parameter);
	
	public R visit(ReflexiveSubsumer conclusion, C parameter);
	
	public R visit(ComposedBackwardLink conclusion, C parameter);
	
	public R visit(ReversedBackwardLink conclusion, C parameter);
	
	public R visit(DecomposedExistential conclusion, C parameter);
	
	public R visit(TracedPropagation conclusion, C parameter);
	
}
