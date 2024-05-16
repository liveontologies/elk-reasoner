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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * Factory for creating
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Object_Properties">built-in
 * object properties</a> in the OWL 2 specification, such as
 * {@code owl:topObjectProperty} and {@code owl:bottomObjectProperty} .
 * 
 * @author "Yevgeny Kazakov"
 */
public interface PredefinedElkObjectPropertyFactory {

	/**
	 * @return the {@link ElkObjectProperty} corresponding to
	 *         {@code owl:topObjectProperty}
	 */
	ElkObjectProperty getOwlTopObjectProperty();

	/**
	 * @return the {@link ElkObjectProperty} corresponding to
	 *         {@code owl:bottomObjectProperty}
	 */
	ElkObjectProperty getOwlBottomObjectProperty();

}
