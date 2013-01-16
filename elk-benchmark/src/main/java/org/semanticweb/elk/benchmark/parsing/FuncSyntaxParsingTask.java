/*
 * #%L
 * ELK Bencharking Package
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
package org.semanticweb.elk.benchmark.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;

/**
 * A simple utility to eval loading performance
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class FuncSyntaxParsingTask implements Task {

	private final File file_;
	
	public FuncSyntaxParsingTask(String[] args) throws TaskException {
		file_ = new File(args[0]);
	}

	private static Owl2Parser createParser(InputStream stream) {
		return new Owl2FunctionalStyleParserFactory().getParser(stream);
	}

	@Override
	public Result run() throws TaskException {
		InputStream stream = null;

		try {
			stream = new FileInputStream(file_);
			Owl2Parser parser = createParser(stream);

			parser.accept(new Owl2ParserAxiomProcessor() {
				@Override
				public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				}

				@Override
				public void visit(ElkPrefix elkPrefix)
						throws Owl2ParseException {
				}
			});
		} catch (Throwable e) {
			throw new TaskException(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		return null;
	}

	@Override
	public String getName() {
		return "FuncSyntaxParsing";
	}

	@Override
	public void prepare() throws TaskException {		
	}
	
	@Override
	public void dispose() {
	}
	
}
