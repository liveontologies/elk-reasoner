/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyCleaning;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalTaxonomyCleaningStage extends AbstractReasonerStage {

	private ClassTaxonomyCleaning cleaning_ = null;	
	
	public IncrementalTaxonomyCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.TAXONOMY_CLEANING);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalConsistencyCheckingStage(reasoner));
	}

	@Override
	public void execute() throws ElkException {
		if (cleaning_ == null) {
			initComputation();
		}

		progressMonitor.start(getName());

		try {
			for (;;) {
				cleaning_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}

		reasoner.incrementalState.setStageStatus(IncrementalStages.TAXONOMY_CLEANING, true);
		reasoner.incrementalState.diffIndex.clearSignatureChanges();
	}

	@Override
	void initComputation() {
		super.initComputation();
		
		final Collection<ElkClass> removed = reasoner.incrementalState.diffIndex.getRemovedClasses();
		final Collection<ElkClass> modified = new ContextRootCollection(reasoner.saturationState.getNotSaturatedContexts());
		//TODO do a generic lazy collection concatenation?
		Collection<ElkClass> inputs = new AbstractCollection<ElkClass>() {

			private Iterator<ElkClass> remIter_ = removed.iterator();
			private Iterator<ElkClass> modIter_ = modified.iterator();
			
			@Override
			public Iterator<ElkClass> iterator() {
				return new Iterator<ElkClass>() {

					@Override
					public boolean hasNext() {
						return remIter_.hasNext() || modIter_.hasNext();
					}

					@Override
					public ElkClass next() {
						ElkClass next = remIter_.next();
						
						if (next == null) {
							next = modIter_.next();
						}
						
						return next;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			public int size() {
				return removed.size() + modified.size();
			}
		};
		
		cleaning_ = new ClassTaxonomyCleaning(inputs, reasoner.taxonomy, reasoner.getProcessExecutor(), workerNo, progressMonitor);
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Used to pass a collection of context's roots without extra copying
	 */
	private static class ContextRootCollection extends AbstractCollection<ElkClass> {

		private final Collection<IndexedClassExpression> ices_;
		
		ContextRootCollection(Collection<IndexedClassExpression> ices) {
			ices_ = ices;
		}
		
		@Override
		public Iterator<ElkClass> iterator() {
			return new Iterator<ElkClass>(){
				
				private ElkClass curr_ = null;
				private final Iterator<IndexedClassExpression> iter_ = ices_.iterator();

				@Override
				public boolean hasNext() {
					if (curr_ != null) {
						return true;
					}
					else {
						while (curr_ == null && iter_.hasNext()) {
							IndexedClassExpression expr = iter_.next();
							
							if (expr instanceof IndexedClass) {
								curr_ = ((IndexedClass)expr).getElkClass();
							}
						}
					}
					
					return curr_ != null;
				}

				@Override
				public ElkClass next() {
					ElkClass tmp = curr_;
					
					curr_ = null;
					
					return tmp;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}};
		}

		@Override
		public int size() {
			//upper bound
			return ices_.size();
		}		
	}
}
