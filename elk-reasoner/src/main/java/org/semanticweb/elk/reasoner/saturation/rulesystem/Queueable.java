/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rulesystem;

/**
 * Common interface for derivations that can be queued when computing a
 * saturation. Objects of this class contain partial information about a
 * derivation, so they should be lightweight data containers. Queueables are
 * affiliated with a {@link Context} during processing, and together with the
 * information in the context the derivation is uniquely determined. For
 * example, if it is derived that class <tt>A</tt> is a subclass of class
 * <tt>B</tt>, then a Queueable that is related to the context of <tt>A</tt>
 * only needs to store <tt>B</tt>. When a queued item is processed, it is stored
 * in its context. Therefore, it may require certain context implementations to
 * be used.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * 
 * @param <C>
 *            the type of contexts that can be used with this queueable;
 *            different queueables may require different features of contexts
 */
public interface Queueable<C extends Context> {

	/**
	 * Ensure that the object is stored in the given context. Storing will be
	 * performed by the supplied rule application engine.
	 * 
	 * @param context
	 * @return <tt>true</tt> if context has been modified by this action
	 */
	public boolean storeInContext(C context,
			RuleApplicationFactory.Engine engine);

}
