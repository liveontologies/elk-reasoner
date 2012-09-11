package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.ChainImpl;

/**
 * A skeleton class for implementing rules that can be applied to
 * {@link BackwardLink}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class BackwardLinkRules extends ChainImpl<BackwardLinkRules>
		implements Rule<BackwardLink>, Chain<BackwardLinkRules> {

	/**
	 * Creates a new chain of {@link BackwardLinkRules} by appending to the
	 * given chain of {@link BackwardLinkRules}.
	 * 
	 * @param tail
	 *            a chain of {@link BackwardLinkRules} to be appended to this
	 *            rule
	 */
	public BackwardLinkRules(BackwardLinkRules tail) {
		super(tail);
	}

}
