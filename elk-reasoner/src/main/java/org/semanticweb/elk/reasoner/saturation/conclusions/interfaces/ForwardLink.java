package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * A {@link Conclusion} representing derived existential restriction for the
 * root. Intuitively, if a subclass axiom
 * {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived by inference
 * rules, then a {@link ForwardLink} with the root {@code :A}, relation
 * {@code :r} and the target {@code :B} can is produced.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ForwardLink extends Conclusion {

	public static final String NAME = "Forward Link";

	/**
	 * @return the {@link IndexedPropertyChain} in the existential restriction
	 *         corresponding to this {@link ForwardLink}
	 */
	public IndexedPropertyChain getForwardChain();

	/**
	 * @return the {@link IndexedContextRoot} corresponding to the filler of the
	 *         existential restriction corresponding to this {@link ForwardLink}
	 */
	public IndexedContextRoot getTarget();

}
