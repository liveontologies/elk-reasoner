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
 * Used for decomposing class expressions when rules are applied forward
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ForwardDecompositionRuleApplicationVisitor extends
		BasicDecompositionRuleApplicationVisitor {
	
	private final SaturationState.ExtendedWriter writer_;

	public ForwardDecompositionRuleApplicationVisitor(SaturationState.ExtendedWriter writer) {
		writer_ = writer;
	}
	
	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
		writer_.produce(writer_.getCreateContext(ice.getFiller()),
				new BackwardLink(context, ice.getRelation()));
	}

	@Override
	protected Writer getSaturationStateWriter() {
		return writer_;
	}

}
