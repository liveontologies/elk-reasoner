/*-
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * Processes axioms with the provided processor and visits them before and after
 * the processing by the provided visitors.
 * 
 * @author Peter Skocovsky
 */
public class DecoratingElkAxiomProcessor implements ElkAxiomProcessor {

	private final ElkAxiomVisitor<?> pre_;
	private final ElkAxiomProcessor processor_;
	private final ElkAxiomVisitor<?> post_;

	public DecoratingElkAxiomProcessor(final ElkAxiomVisitor<?> pre,
			final ElkAxiomProcessor processor, final ElkAxiomVisitor<?> post) {
		this.pre_ = pre;
		this.processor_ = processor;
		this.post_ = post;
	}

	@Override
	public void visit(final ElkAxiom elkAxiom) {
		elkAxiom.accept(pre_);
		processor_.visit(elkAxiom);
		elkAxiom.accept(post_);
	}

}
