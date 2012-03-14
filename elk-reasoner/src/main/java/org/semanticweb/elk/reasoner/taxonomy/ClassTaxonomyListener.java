/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorListenerNotifyCanProcess;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorListenerNotifyFinishedJob;

/**
 * A listener to be used with {@link ClassTaxonomyEngine}. The listener defines
 * functions that are triggered during the construction of the taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ClassTaxonomyListener<P extends ClassTaxonomyEngine> extends
		InputProcessorListenerNotifyCanProcess<P>,
		InputProcessorListenerNotifyFinishedJob<IndexedClass, P> {
}