/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form: A => R_1 some B, B => R_2 some C, and
 * the role index entails R_1 o R_2 => S and (S some C) occurs in the ontology,
 * thus A => S some C.
 * 
 * Here the context is where the composition actually happened, i.e. B. Both the
 * forward link and the backward link are stored in that context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PropertyChainInference extends AbstractForeignContextInference {

	private final IndexedPropertyChain backwardLinkRelation_;
	
	private final Context backwardLinkSource_;

	private final IndexedPropertyChain forwardLinkRelation_;
	
	private final Context forwardLinkTarget_;

	PropertyChainInference(Context context,
			IndexedPropertyChain bwLinkRelation,
			Context bwLinkSource,
			IndexedPropertyChain fwLinkRelation,
			Context fwLinkTarget) {
		super(context);
		backwardLinkRelation_ = bwLinkRelation;
		backwardLinkSource_ = bwLinkSource;
		forwardLinkRelation_ = fwLinkRelation;
		forwardLinkTarget_ = fwLinkTarget;
	}

	public IndexedPropertyChain getBackwardLinkRelation() {
		return backwardLinkRelation_;
	}

	public Context getBackwardLinkSource() {
		return backwardLinkSource_;
	}

	public IndexedPropertyChain getForwardLinkRelation() {
		return forwardLinkRelation_;
	}

	public Context getForwardLinkTarget() {
		return forwardLinkTarget_;
	}

	@Override
	public String toString() {
		return "Property chain inference: " + backwardLinkSource_ + " => " + backwardLinkRelation_ + " o " + forwardLinkRelation_ + " some " + context.getRoot();
	}

}
