/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.properties.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;

/**
 * A {@link SaturationInference} that produce {@link ObjectPropertyConclusion}s
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public interface ObjectPropertyInference extends SaturationInference {

	public <O> O accept(Visitor<O> visitor);

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O>
			extends
				PropertyRangeInference.Visitor<O>,
				SubPropertyChainInference.Visitor<O> {

		// combined interface

	}

}
