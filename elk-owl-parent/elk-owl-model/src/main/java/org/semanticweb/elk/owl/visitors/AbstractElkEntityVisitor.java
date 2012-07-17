/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * The default visitor, does nothing
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the output type of the visitor
 */
public abstract class AbstractElkEntityVisitor<O> implements
		ElkEntityVisitor<O> {

	@Override
	public O visit(ElkAnnotationProperty elkAnnotationProperty) {
		return null;
	}

	@Override
	public O visit(ElkClass elkClass) {
		return null;
	}

	@Override
	public O visit(ElkDataProperty elkDataProperty) {
		return null;
	}

	@Override
	public O visit(ElkDatatype elkDatatype) {
		return null;
	}

	@Override
	public O visit(ElkNamedIndividual elkNamedIndividual) {
		return null;
	}

	@Override
	public O visit(ElkObjectProperty elkObjectProperty) {
		return null;
	}
}