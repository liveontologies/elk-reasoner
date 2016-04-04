package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
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

import org.semanticweb.elk.owl.interfaces.ElkClass;

/**
 * Factory for creating
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Classes">built-in classes<a> in
 * the OWL 2 specification, such as {@code owl:Thing} and {@code owl:Nothing}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface PredefinedElkClassFactory {

	/**
	 * @return the {@link ElkClass} corresponding to {@code owl:Thing}
	 */
	ElkClass getOwlThing();

	/**
	 * @return the {@link ElkClass} corresponding to {@code owl:Nothing}
	 */
	ElkClass getOwlNothing();

}
