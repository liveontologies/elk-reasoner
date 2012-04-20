package org.semanticweb.elk.owl.printers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

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

	//@Ignore
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
		
		/*Writer writer = new FileWriter(new File("test.owl"));
		writer.write(builder.toString());
		writer.flush();
		writer.close();*/
		
		Set<? extends ElkObject> loadedElkObjects = loadPrintedElkObjects(builder.toString());
		//TODO A diff here?
		
		assertEquals(elkObjects.size(), loadedElkObjects.size());
	}

	protected abstract Set<? extends ElkObject> getOriginalElkObjects();
	protected abstract Set<? extends ElkObject> loadPrintedElkObjects(String serialized);

}
