package org.semanticweb.elk.owl.inferences;

import java.util.Collection;

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

public interface ElkInferenceSet {

	/**
	 * @param conclusion
	 * @return all inferences producing the given conclusion or {@code null} if
	 *         those have not been computed yet
	 */
	Collection<? extends ElkInference> get(ElkAxiom conclusion);

	public void add(ChangeListener listener);

	public void remove(ChangeListener listener);

	/**
	 * A listener to monitor if inferences for axioms have changed
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface ChangeListener {

		/**
		 * called whenever the inferences already returned for some conclusions
		 * by {@link ElkInferenceSet#get(ElkAxiom)} may have changed, i.e.,
		 * calling this method again with the same input may produce a different
		 * result
		 */
		void inferencesChanged();

	}

}
