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
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleContextTraceStore implements ContextTracer {
	
	private final Multimap<IndexedClassExpression, TracedConclusion> subsumerInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, TracedConclusion>> backwardLinkInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, TracedConclusion>> forwardLinkInferenceMap_;
	
	private final ConclusionVisitor<Void, TracedConclusionVisitor<?,?>> inferenceReader_ = new BaseConclusionVisitor<Void, TracedConclusionVisitor<?,?>>() {

		private void visitAll(Iterable<TracedConclusion> conclusions, TracedConclusionVisitor<?,?> visitor) {
			if (conclusions != null) {
				for (TracedConclusion inf : conclusions) {
					inf.acceptTraced(visitor, null);
				}
			}
		}
		
		@Override
		public Void visit(NegativeSubsumer negSCE, TracedConclusionVisitor<?,?> visitor) {
			visitAll(getSubsumerInferences(negSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(PositiveSubsumer posSCE, TracedConclusionVisitor<?,?> visitor) {
			visitAll(getSubsumerInferences(posSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(BackwardLink link, TracedConclusionVisitor<?,?> visitor) {
			visitAll(getLinkInferences(backwardLinkInferenceMap_, link.getRelation(), link.getSource()), visitor);
			
			return null;
		}
		
		@Override
		public Void visit(ForwardLink link, TracedConclusionVisitor<?,?> visitor) {
			visitAll(getLinkInferences(forwardLinkInferenceMap_, link.getRelation(), link.getTarget()), visitor);
			
			return null;
		}
		
	}; 
	
	private final TracedConclusionVisitor<Boolean, ?> inferenceWriter_ = new BaseTracedConclusionVisitor<Boolean, Void>() {

		@Override
		public Boolean visit(InitializationSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(SubClassOfSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ComposedConjunction conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedConjunction conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(PropagatedSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ReflexiveSubsumer conclusion, Void param) {
			return addSubsumerInference(conclusion.getExpression(), conclusion);
		}

		@Override
		public Boolean visit(ComposedBackwardLink conclusion, Void param) {
			return addLinkInference(backwardLinkInferenceMap_, conclusion.getRelation(), conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(ReversedBackwardLink conclusion, Void param) {
			return addLinkInference(forwardLinkInferenceMap_, conclusion.getRelation(), conclusion.getTarget(), conclusion);
		}

		@Override
		public Boolean visit(DecomposedExistential conclusion, Void param) {
			return addLinkInference(backwardLinkInferenceMap_, conclusion.getRelation(), conclusion.getSource(), conclusion);
		}

		@Override
		public Boolean visit(TracedPropagation conclusion, Void param) {
			return addSubsumerInference(conclusion.getCarry(), conclusion);
		}
		
	};
	
	/**
	 * 
	 */
	public SimpleContextTraceStore() {
		subsumerInferenceMap_ = new HashSetMultimap<IndexedClassExpression, TracedConclusion>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,TracedConclusion>>();
		forwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,TracedConclusion>>();
	}
	

	protected Boolean addLinkInference(Map<IndexedPropertyChain, Multimap<Context, TracedConclusion>> linkMap, IndexedPropertyChain relation,
			Context source, TracedConclusion inf) {
		Multimap<Context, TracedConclusion> infMap = linkMap.get(relation);
		
		if (infMap == null) {
			infMap = new HashSetMultimap<Context, TracedConclusion>();
			
			infMap.add(source, inf);
			linkMap.put(relation, infMap);
			
			return true;
		}
		
		return infMap.add(source, inf);
	}


	protected Boolean addSubsumerInference(IndexedClassExpression ice, TracedConclusion inf) {
		return subsumerInferenceMap_.add(ice, inf);
	}

	@Override
	public void accept(Conclusion conclusion, TracedConclusionVisitor<?,?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}

	public Iterable<TracedConclusion> getSubsumerInferences(IndexedClassExpression conclusion) {
		return subsumerInferenceMap_.get(conclusion);
	}

	public Iterable<TracedConclusion> getLinkInferences(Map<IndexedPropertyChain, Multimap<Context, TracedConclusion>> linkMap, 
			IndexedPropertyChain linkRelation, Context linkSource) {
		Multimap<Context, TracedConclusion> infMap = linkMap.get(linkRelation);
		
		return infMap == null ? null : infMap.get(linkSource);
	}

	@Override
	public boolean addInference(TracedConclusion conclusion) {
		return conclusion.acceptTraced(inferenceWriter_, null);
	}

}
