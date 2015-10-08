/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ConclusionEquality;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ConclusionHash;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ConclusionPrinter;

/**
 * A skeleton for implementation of {@link Conclusion}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractConclusion implements Conclusion {

	private final IndexedContextRoot root_;

	protected AbstractConclusion(IndexedContextRoot root) {
		this.root_ = root;
	}

	@Override
	public IndexedContextRoot getConclusionRoot() {
		return this.root_;
	}

	@Override
	public IndexedContextRoot getOriginRoot() {
		return this.root_;
	}

	@Override
	public boolean equals(Object o) {
		return ConclusionEquality.equals(this, o);
	}

	@Override
	public int hashCode() {
		return ConclusionHash.hashCode(this);
	}

	@Override
	public String toString() {
		return ConclusionPrinter.toString(this);
	}

}
