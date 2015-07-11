package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.SubsumerInferenceVisitor;

public interface SubsumerInference<S extends IndexedClassExpression> extends
		Subsumer<S>, ClassInference {

	public <I, O> O accept(SubsumerInferenceVisitor<I, O> visitor, I input);

}
