/*
 * #%L
 * ELK OWL Model Implementation
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
/**
 * 
 */
package org.semanticweb.elk.owl.parsing;

import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * The base interface for OWL 2 parsers
 * 
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Owl2Parser {

	/**
	 * Registers an additional prefix declaration for this parser, which can be
	 * used to resolve IRIs. Normally, prefix declarations should be parsed from
	 * OWL 2 files, but some prefix declarations, e.g., the OWL 2 predefined
	 * prefixes can be supplied separately.
	 * 
	 * @param elkPrefix
	 */
	public void declarePrefix(ElkPrefix elkPrefix);

	public void accept(ElkAxiomProcessor axiomProcessor)
			throws Owl2ParseException;

}
