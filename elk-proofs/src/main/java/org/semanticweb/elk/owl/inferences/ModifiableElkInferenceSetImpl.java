package org.semanticweb.elk.owl.inferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifiableElkInferenceSetImpl
		implements ModifiableElkInferenceSet {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ModifiableElkInferenceSetImpl.class);

	private final Multimap<ElkAxiom, ElkInference> inferenceMap_ = new HashSetMultimap<ElkAxiom, ElkInference>();

	/**
	 * axioms for which {@link #get(ElkAxiom)} was called after the last
	 * inference for this axiom was added
	 */
	private final Set<ElkAxiom> queried_ = new HashSet<ElkAxiom>();

	private final List<ChangeListener> listeners_ = new ArrayList<ChangeListener>();

	private final ElkObject.Factory elkFactory_;

	public ModifiableElkInferenceSetImpl(ElkObject.Factory elkFactory) {
		this.elkFactory_ = elkFactory;
	}

	@Override
	public void produce(ElkInference inference) {
		LOGGER_.trace("{}: inference produced", inference);
		ElkAxiom conclusion = inference.getConclusion(elkFactory_);
		inferenceMap_.add(conclusion, inference);
		if (queried_.remove(conclusion)) {
			fireChanged();
		}
	}

	@Override
	public void clear() {
		inferenceMap_.clear();
		queried_.clear();
		fireChanged();
	}

	Collection<? extends ElkInference> getInferences(ElkAxiom conclusion) {
		return inferenceMap_.get(conclusion);
	}

	@Override
	public Collection<? extends ElkInference> get(ElkAxiom conclusion) {
		queried_.add(conclusion);
		return getInferences(conclusion);
	}

	@Override
	public void add(ChangeListener listener) {
		listeners_.add(listener);
	}

	@Override
	public void remove(ChangeListener listener) {
		listeners_.remove(listener);
	}

	void fireChanged() {
		for (ChangeListener listener : listeners_) {
			listener.inferencesChanged();
		}
	}

	boolean isQuieried(ElkAxiom conclusion) {
		return queried_.contains(conclusion);
	}

}
