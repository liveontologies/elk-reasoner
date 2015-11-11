/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

/**
 * A generic interface for objects which recursively unwind previously stored
 * inferences and let the calling code visit inferences.
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface TraceUnwinder<O> {

	/*
	 * class trace unwinding involves both class and property inferences.
	 */
	public void accept(ClassConclusion conclusion,
			ClassInferenceVisitor<?, O> inferenceVisitor,
			ObjectPropertyInferenceVisitor<?, O> propertyInferenceVisitor);

	/*
	 * unwinds only object property inferences.
	 */
	public void accept(ObjectPropertyConclusion conclusion,
			ObjectPropertyInferenceVisitor<?, O> inferenceVisitor);
}
