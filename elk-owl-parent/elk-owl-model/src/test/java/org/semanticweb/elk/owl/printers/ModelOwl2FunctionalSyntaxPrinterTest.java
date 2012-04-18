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
package org.semanticweb.elk.owl.printers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * The abstract test for ELK functional syntax printer
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public abstract class ModelOwl2FunctionalSyntaxPrinterTest {

	@Ignore
	/*
	ignored because the printer can't print constructs which are not instantiated during parsing
	*/ 
	@Test
	public void testRoundtrip() throws IOException {
		Set<? extends ElkObject> elkObjects = getOriginalElkObjects();
		//serialize into a string
		StringBuilder builder = new StringBuilder();
		
		for (ElkObject elkObject : elkObjects) {
			OwlFunctionalStylePrinter.append(builder, elkObject);
			builder.append(System.getProperty("line.separator"));
		}
		
		Writer writer = new FileWriter(new File("test.owl"));
		writer.write(builder.toString());
		writer.flush();
		writer.close();
		
		Set<? extends ElkObject> loadedElkObjects = loadPrintedElkObjects(builder.toString());
		//TODO A diff here?
		assertEquals(elkObjects, loadedElkObjects);
	}

	protected abstract Set<? extends ElkObject> getOriginalElkObjects();
	protected abstract Set<? extends ElkObject> loadPrintedElkObjects(String serialized);

}
