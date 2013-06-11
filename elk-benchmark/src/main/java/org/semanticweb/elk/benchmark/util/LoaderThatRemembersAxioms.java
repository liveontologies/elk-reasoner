/**
 * 
 */
package org.semanticweb.elk.benchmark.util;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LoaderThatRemembersAxioms extends Owl2StreamLoader implements ElkAxiomProcessor {

	private final int limit_;
	private final List<ElkAxiom> loaded_;
	
	private ElkAxiomProcessor processor_;
	
	public LoaderThatRemembersAxioms(Owl2ParserFactory parserFactory, File file, int maxAxiomsToRemember)
			throws FileNotFoundException {
		super(parserFactory, file);
		limit_ = maxAxiomsToRemember;
		loaded_ = new ArrayList<ElkAxiom>(maxAxiomsToRemember);
	}
	
	@Override
	public Loader getLoader(ElkAxiomProcessor axiomLoader) {
		processor_ = axiomLoader;
		
		return super.getLoader(this);
	}


	@Override
	public void visit(ElkAxiom elkAxiom) {
		processor_.visit(elkAxiom);
		
		if (loaded_.size() <= limit_) {
			loaded_.add(elkAxiom);
		}
	}


	public List<ElkAxiom> getLoadedAxioms() {
		return loaded_;
	}
}