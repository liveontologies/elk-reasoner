/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form A => R some B, B => C, (R some C) occurs
 * negatively in the ontology, thus A => R some C.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExistentialInference extends AbstractForeignContextInference {

	private final IndexedClassExpression subsumer_;
	
	private final IndexedPropertyChain linkRelation_;
	
	private final Context linkSource_;
	
	ExistentialInference(Context cxt, IndexedClassExpression subsumer, IndexedPropertyChain linkRelation, Context linkSource) {
		super(cxt);
		subsumer_ = subsumer;
		linkRelation_ = linkRelation;
		linkSource_ = linkSource;
	}

	public IndexedClassExpression getSubsumer() {
		return subsumer_;
	}

	public IndexedPropertyChain getLinkRelation() {
		return linkRelation_;
	}

	public Context getLinkSource() {
		return linkSource_;
	}
	
	@Override
	public String toString() {
		return "Existential inference from " + context + " => " + subsumer_
				+ " and " + linkSource_.getRoot() + " => " + linkRelation_
				+ " some " + context.getRoot();
	}
}
