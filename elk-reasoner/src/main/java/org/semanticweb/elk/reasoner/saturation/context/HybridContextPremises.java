package org.semanticweb.elk.reasoner.saturation.context;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;

/**
 * {@link ContextPremises} that are build from two {@link ContextPremises}
 * objects with the same root. Local premises are taken from the first object,
 * whereas non-local premises from the second. A premise is local if its source
 * 
 * 
 * @see ContextPremises#getRoot()
 * @see Conclusion#getSourceRoot(IndexedClassExpression)
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class HybridContextPremises implements ContextPremises {

	private final ContextPremises localPremises_;

	private final ContextPremises nonLocalPremises_;

	public HybridContextPremises(ContextPremises localPremises,
			ContextPremises nonLocalPremises) {
		this.localPremises_ = localPremises;
		this.nonLocalPremises_ = nonLocalPremises;
	}

	@Override
	public IndexedClassExpression getRoot() {
		// should be the same for local and non-local
		return localPremises_.getRoot();
	}

	@Override
	public Set<IndexedClassExpression> getSubsumers() {
		// local
		return localPremises_.getSubsumers();
	}

	@Override
	public Set<IndexedObjectProperty> getLocalReflexiveObjectProperties() {
		// local
		return localPremises_.getLocalReflexiveObjectProperties();
	}

	@Override
	public LinkableBackwardLinkRule getBackwardLinkRuleHead() {
		// local
		return localPremises_.getBackwardLinkRuleHead();
	}

	@Override
	public boolean isInconsistForDisjointnessAxiom(
			IndexedDisjointnessAxiom axiom) {
		// local
		return localPremises_.isInconsistForDisjointnessAxiom(axiom);
	}

	@Override
	public Iterable<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers(
			IndexedPropertyChain subRoot) {
		// local
		return localPremises_.getPropagatedSubsumers(subRoot);
	}

	@Override
	public Map<IndexedObjectProperty, ? extends SubContextPremises> getSubContextPremisesByObjectProperty() {
		// non-local
		return nonLocalPremises_.getSubContextPremisesByObjectProperty();
	}

	@Override
	public String toString() {
		return getRoot() + "[hybrid]";
	}

}
