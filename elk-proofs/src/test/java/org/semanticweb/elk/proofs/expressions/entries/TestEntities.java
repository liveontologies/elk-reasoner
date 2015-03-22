/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.entries;
/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefix;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TestEntities {

	public static final ElkPrefix prefix_ = new ElkPrefix("", new ElkFullIri("http://test.org/"));
	private static final ElkObjectFactory factory_ = new ElkObjectFactoryImpl();
	
	public static final ElkClass a = factory_.getClass(new ElkAbbreviatedIri(prefix_, "A"));
	public static final ElkClass b = factory_.getClass(new ElkAbbreviatedIri(prefix_, "B"));
	public static final ElkClass c = factory_.getClass(new ElkAbbreviatedIri(prefix_, "C"));
	public static final ElkClass d = factory_.getClass(new ElkAbbreviatedIri(prefix_, "D"));
	public static final ElkClass e = factory_.getClass(new ElkAbbreviatedIri(prefix_, "E"));
	public static final ElkClass f = factory_.getClass(new ElkAbbreviatedIri(prefix_, "F"));
	
	public static final ElkObjectProperty p = factory_.getObjectProperty(new ElkAbbreviatedIri(prefix_, "P"));
	public static final ElkObjectProperty r = factory_.getObjectProperty(new ElkAbbreviatedIri(prefix_, "R"));
	public static final ElkObjectProperty s = factory_.getObjectProperty(new ElkAbbreviatedIri(prefix_, "S"));
	public static final ElkObjectProperty q = factory_.getObjectProperty(new ElkAbbreviatedIri(prefix_, "Q"));
	public static final ElkObjectProperty h = factory_.getObjectProperty(new ElkAbbreviatedIri(prefix_, "H"));
	
}
