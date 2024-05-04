package org.semanticweb.elk.owlapi.wrapper;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * An implementation of the visitor pattern for OWL individuals to convert OWL
 * individuals to ELK individuals.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlIndividualConverterVisitor
		implements OWLIndividualVisitorEx<ElkIndividual> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlIndividualConverterVisitor INSTANCE_ = new OwlIndividualConverterVisitor();

	public static OwlIndividualConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlIndividualConverterVisitor() {
	}

	@Override
	public ElkAnonymousIndividual visit(
			OWLAnonymousIndividual owlAnonymousIndividual) {
		return CONVERTER.convert(owlAnonymousIndividual);
	}

	@Override
	public ElkNamedIndividual visit(OWLNamedIndividual owlNamedIndividual) {
		return CONVERTER.convert(owlNamedIndividual);
	}

}
