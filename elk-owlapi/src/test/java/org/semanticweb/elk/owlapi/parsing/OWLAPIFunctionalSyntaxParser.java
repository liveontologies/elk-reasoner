/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.parsing;

import java.io.InputStream;
import java.io.Reader;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarations;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * A simple implementation to enable testing coverage of our OWL API bindings
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class OWLAPIFunctionalSyntaxParser implements Owl2Parser {

	private final OWLOntologyDocumentSource mOntoSource;

	OWLAPIFunctionalSyntaxParser(InputStream source) {
		mOntoSource = new StreamDocumentSource(source);
	}
	
	OWLAPIFunctionalSyntaxParser(Reader source) {
		mOntoSource = new ReaderDocumentSource(source);
	}

	@Override
	public void setPrefixDeclarations(ElkPrefixDeclarations prefDecls) {
	}

	@Override
	public void parseOntology(ElkAxiomProcessor processor)
			throws Owl2ParseException {
		// First, parse the ontology
		OWLOntology ontology = loadViaOWLAPI();
		// Second, convert it 
		OwlConverter converter = OwlConverter.getInstance();
		
		for (OWLAxiom axiom : ontology.getAxioms()) {
			processor.process(converter.convert(axiom));
		}
	}

	private OWLOntology loadViaOWLAPI() throws Owl2ParseException {
		try {
			return OWLManager.createOWLOntologyManager()
					.loadOntologyFromOntologyDocument(mOntoSource);
			
		} catch (OWLOntologyCreationException e) {
			throw new Owl2ParseException(e);
		} catch (OWLRuntimeException re) {
			throw new Owl2ParseException(re);
		}
	}
}