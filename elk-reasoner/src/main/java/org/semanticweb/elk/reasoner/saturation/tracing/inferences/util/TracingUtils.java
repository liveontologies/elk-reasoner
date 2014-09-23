/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingUtils {

	public static Collection<ClassInference> getClassInferences(TraceStore.Reader reader, IndexedClassExpression cxt, Conclusion conclusion) {
		final List<ClassInference> inferences = new LinkedList<ClassInference>();
		
		reader.accept(cxt, conclusion, new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

			@Override
			protected Void defaultTracedVisit(ClassInference inf, IndexedClassExpression root) {
				inferences.add(inf);
				return null;
			}
			
		});
		
		return inferences;
	}
	
	public static Collection<ObjectPropertyInference> getObjectPropertyInferences(TraceStore.Reader reader, ObjectPropertyConclusion conclusion) {
		final List<ObjectPropertyInference> inferences = new LinkedList<ObjectPropertyInference>();
		
		reader.accept(conclusion, new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(ObjectPropertyInference inf, Void _ignored) {
				inferences.add(inf);
				return null;
			}
			
		});
		
		return inferences;
	}	
}
