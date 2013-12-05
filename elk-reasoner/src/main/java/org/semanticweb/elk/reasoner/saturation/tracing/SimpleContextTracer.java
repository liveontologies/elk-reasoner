/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Collection;
import java.util.Collections;
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
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleContextTracer implements ContextTracer {
	
	private final Multimap<IndexedClassExpression, Inference> subsumerInferenceMap_;
	
	private final Map<IndexedPropertyChain, HashSetMultimap<Context, Inference>> backwardLinkInferenceMap_;
	
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
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, HashSetMultimap<Context,Inference>>();
	}
	

	protected Boolean addBackwardLinkInference(IndexedPropertyChain relation,
			Context source, Inference inf) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(relation);
		
		if (infMap == null) {
			infMap = new HashSetMultimap<Context, Inference>();
		}
		
		return infMap.add(source, inf);
	}


	protected Boolean addSubsumerInference(IndexedClassExpression ice, Inference inf) {
		Collection<Inference> inferences = subsumerInferenceMap_.get(ice);
		
		if (inferences == null) {
			inferences = new ArrayHashSet<Inference>();
		}
		
		return inferences.add(inf);
	}


	@Override
	public Iterable<Inference> getInference(Conclusion conclusion) {
		return conclusion.accept(inferenceReader_, null);
	}

	@Override
	public Iterable<Inference> getSubsumerInferences(IndexedClassExpression conclusion) {
		Iterable<Inference> inferences = subsumerInferenceMap_.get(conclusion);
		
		return inferences == null ? Collections.<Inference>emptyList() : inferences;
	}

	@Override
	public Iterable<Inference> getBackwardLinkInferences(
			IndexedPropertyChain linkRelation, Context linkSource) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(linkRelation);
		
		return infMap == null ? Collections.<Inference>emptyList() : infMap.get(linkSource);
	}

	@Override
	public boolean addInference(Conclusion conclusion, Inference inference) {
		return conclusion.accept(inferenceWriter_, inference);
	}

}
