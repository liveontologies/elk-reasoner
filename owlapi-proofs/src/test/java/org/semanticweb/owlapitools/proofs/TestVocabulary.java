/**
 * 
 */
package org.semanticweb.owlapitools.proofs;
/*
 * #%L
 * OWL API Proofs Model
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TestVocabulary {

	public static final OWLDataFactory FACTORY = OWLManager.getOWLDataFactory();
	public static final String PREFIX = "http://example.com/";
	public static final OWLClass A = FACTORY.getOWLClass(IRI.create(PREFIX + "A"));
	public static final OWLClass B = FACTORY.getOWLClass(IRI.create(PREFIX + "B"));
	public static final OWLClass C = FACTORY.getOWLClass(IRI.create(PREFIX + "C"));
	public static final OWLClass D = FACTORY.getOWLClass(IRI.create(PREFIX + "D"));
	public static final OWLClass E = FACTORY.getOWLClass(IRI.create(PREFIX + "E"));
	public static final OWLClass F = FACTORY.getOWLClass(IRI.create(PREFIX + "F"));
	public static final OWLClass G = FACTORY.getOWLClass(IRI.create(PREFIX + "G"));
	public static final OWLObjectProperty R = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R"));
	public static final OWLObjectProperty S = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "S"));
	public static final OWLObjectProperty H = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "H"));
	public static final OWLObjectProperty T = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "T"));
	public static final OWLObjectProperty R1 = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R1"));
	public static final OWLObjectProperty R2 = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R2"));
	public static final OWLObjectProperty R3 = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R3"));
	public static final OWLObjectProperty R4 = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R4"));
	public static final OWLObjectProperty R5 = FACTORY.getOWLObjectProperty(IRI.create(PREFIX + "R5"));
}
