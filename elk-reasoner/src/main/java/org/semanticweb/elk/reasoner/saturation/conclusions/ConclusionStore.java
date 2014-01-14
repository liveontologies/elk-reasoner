/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A generic storage of conclusions, for example, for caching purposes. A
 * {@link Conclusion} can be added to the store and checked for presense in the
 * store.
 * 
 * It's not threadsafe. 
 * 
 * TODO make it an interface?
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionStore {

	private final Set<IndexedClassExpression> subsumers_;
	
	private final Multimap<IndexedPropertyChain, IndexedClassExpression> backwardLinkMap_;
	
	private final Multimap<IndexedPropertyChain, IndexedClassExpression> forwardLinkMap_;
	
	private final Multimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom> propagationMap_;
	
	private final ConclusionVisitor<Boolean, ?> inserter_ = new ConclusionVisitor<Boolean, Void>() {
		@Override
		public Boolean visit(ComposedSubsumer negSCE, Void context) {
			return subsumers_.add(negSCE.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Void context) {
			return subsumers_.add(posSCE.getExpression());
		}

		@Override
		public Boolean visit(BackwardLink link, Void context) {
			return backwardLinkMap_.add(link.getRelation(), link.getSource().getRoot());
		}

		@Override
		public Boolean visit(ForwardLink link, Void context) {
			return forwardLinkMap_.add(link.getRelation(), link.getTarget().getRoot());
		}

		@Override
		public Boolean visit(Contradiction bot, Void context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Boolean visit(Propagation propagation, Void context) {
			return propagationMap_.add(propagation.getRelation(), propagation.getCarry());
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom, Void context) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	private final ConclusionVisitor<Boolean, ?> checker_ = new ConclusionVisitor<Boolean, Void>() {
		@Override
		public Boolean visit(ComposedSubsumer negSCE, Void context) {
			return subsumers_.contains(negSCE.getExpression());
		}

		@Override
		public Boolean visit(DecomposedSubsumer posSCE, Void context) {
			return subsumers_.contains(posSCE.getExpression());
		}

		@Override
		public Boolean visit(BackwardLink link, Void context) {
			return backwardLinkMap_.contains(link.getRelation(), link.getSource().getRoot());
		}

		@Override
		public Boolean visit(ForwardLink link, Void context) {
			return forwardLinkMap_.contains(link.getRelation(), link.getTarget().getRoot());
		}

		@Override
		public Boolean visit(Contradiction bot, Void context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Boolean visit(Propagation propagation, Void context) {
			return propagationMap_.contains(propagation.getRelation(), propagation.getCarry());
		}

		@Override
		public Boolean visit(DisjointnessAxiom disjointnessAxiom, Void context) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	public ConclusionStore() {
		subsumers_ = new ArrayHashSet<IndexedClassExpression>();
		backwardLinkMap_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>();
		forwardLinkMap_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>();
		propagationMap_ = new HashSetMultimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom>();
	}
	
	public boolean add(Conclusion conclusion) {
		return conclusion.accept(inserter_, null);
	}

	public boolean contains(Conclusion conclusion) {
		return conclusion.accept(checker_, null);
	}
}
