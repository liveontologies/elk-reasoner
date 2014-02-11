/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

// TODO: this class is no longer necessary: contexts can be creating and initialized
// by SaturationStateWriter itself
/**
 * The extended writer for situations when new contexts may need to be
 * created/initialized or removed. With every
 * {@link ExtendedSaturationStateWriter} one can register a
 * {@link ContextCreationListener} that will be executed every time this
 * {@link ExtendedSaturationStateWriter} creates a new {@code Context}. Although
 * all functions of this {@link ExtendedSaturationStateWriter} are thread safe,
 * the function of the {@link ContextCreationListener} might not be, in which
 * the access of functions of {@link ExtendedSaturationStateWriter} should be
 * synchronized between threads.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationStateWriter extends SaturationStateWriter {

	public Context getCreateContext(IndexedClassExpression root);

	public void initContext(Context context);

}
