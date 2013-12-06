/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleContextTracer implements ContextTracer {
	
	private final Multimap<IndexedClassExpression, Inference> subsumerInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, Inference>> backwardLinkInferenceMap_;
	
	private final ConclusionVisitor<Iterable<Inference>, Void> inferenceReader_ = new BaseConclusionVisitor<Iterable<Inference>, Void>() {

		@Override
		public Iterable<Inference> visit(NegativeSubsumer negSCE, Void v) {
			return getSubsumerInferences(negSCE.getExpression());
		}

		@Override
		public Iterable<Inference> visit(PositiveSubsumer posSCE, Void v) {
			return getSubsumerInferences(posSCE.getExpression());
		}

		@Override
		public Iterable<Inference> visit(BackwardLink link, Void v) {
			return getBackwardLinkInferences(link.getRelation(), link.getSource());
		}
		
	}; 
	
	private final ConclusionVisitor<Boolean, Inference> inferenceWriter_ = new BaseConclusionVisitor<Boolean, Inference>() {

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Inference inf) {
			return addSubsumerInference(negSCE.getExpression(), inf);
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Inference inf) {
			return addSubsumerInference(posSCE.getExpression(), inf);
		}

		@Override
		public Boolean visit(BackwardLink link, Inference inf) {
			return addBackwardLinkInference(link.getRelation(), link.getSource(), inf);
		}
		
	};
	
	/**
	 * 
	 */
	public SimpleContextTracer() {
		subsumerInferenceMap_ = new HashSetMultimap<IndexedClassExpression, Inference>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
	}
	

	protected Boolean addBackwardLinkInference(IndexedPropertyChain relation,
			Context source, Inference inf) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(relation);
		
		if (infMap == null) {
			infMap = new HashSetMultimap<Context, Inference>();
			
			infMap.add(source, inf);
			backwardLinkInferenceMap_.put(relation, infMap);
			
			return true;
		}
		
		return infMap.add(source, inf);
	}


	protected Boolean addSubsumerInference(IndexedClassExpression ice, Inference inf) {
		return subsumerInferenceMap_.add(ice, inf);
	}


	@Override
	public Iterable<Inference> getInference(Conclusion conclusion) {
		return conclusion.accept(inferenceReader_, null);
	}

	@Override
	public Iterable<Inference> getSubsumerInferences(IndexedClassExpression conclusion) {
		return subsumerInferenceMap_.get(conclusion);
	}

	@Override
	public Iterable<Inference> getBackwardLinkInferences(
			IndexedPropertyChain linkRelation, Context linkSource) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(linkRelation);
		
		return infMap == null ? null : infMap.get(linkSource);
	}

	@Override
	public boolean addInference(Conclusion conclusion, Inference inference) {
		return conclusion.accept(inferenceWriter_, inference);
	}

}
