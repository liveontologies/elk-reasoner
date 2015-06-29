package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

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

/**
 * A general type of conclusions, produced by inference rules and used as
 * premises of inference rules. The rules can be applied to {@link Conclusion}s
 * together with other {@link Conclusion}s stored in {@link ContextPremises}.
 * 
 * @see Rule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {

	/**
	 * @return The root of the {@link Context} in which this conclusion should
	 *         participate in inferences; it cannot be {@code null}.
	 */
	public IndexedContextRoot getConclusionRoot();

	/**
	 * 
	 * @return The {@link IndexedContextRoot} of the {@link Context} from which
	 *         this {@link Conclusion} originate, that is, if to start deriving
	 *         conclusions with this root, it will be derived; this value cannot
	 *         be {@code null}. This value be different from
	 *         {@link #getConclusionRoot()}
	 */
	public IndexedContextRoot getOriginRoot();

	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input);

}
