/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState.Writer;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Used for decomposing class expressions when rules are applied backwards
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BackwardDecompositionRuleApplicationVisitor extends
		BasicDecompositionRuleApplicationVisitor {
	
	private final SaturationState.Writer writer_;

	public BackwardDecompositionRuleApplicationVisitor(SaturationState.Writer writer) {
		writer_ = writer;
	}
	
	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
		if (ice.getFiller().getContext() != null) {
			writer_.produce(ice.getFiller().getContext(),
					new BackwardLink(context, ice.getRelation()));	
		}
	}

	@Override
	protected Writer getSaturationStateWriter() {
		return writer_;
	}

}
