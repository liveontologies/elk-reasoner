/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceWrap implements OWLInference {

	private final Inference inference_;
	
	InferenceWrap(Inference inf) {
		inference_ = inf;
	}
	
	@Override
	public OWLExpression getConclusion() {
		return ElkToOwlProofConverter.convert(inference_.getConclusion());
	}

	@Override
	public Collection<OWLExpression> getPremises() {
		final Collection<? extends DerivedExpression> premises = inference_.getPremises();
		
		return new AbstractCollection<OWLExpression>() {

			@Override
			public Iterator<OWLExpression> iterator() {
				return new Iterator<OWLExpression>() {

					private final Iterator<? extends DerivedExpression> iter_ = premises.iterator();
					
					@Override
					public boolean hasNext() {
						return iter_.hasNext();
					}

					@Override
					public OWLExpression next() {
						return ElkToOwlProofConverter.convert(iter_.next());
					}

					@Override
					public void remove() {
						iter_.remove();
					}
					
				};
			}

			@Override
			public int size() {
				return premises.size();
			}
			
		};
	}

}
