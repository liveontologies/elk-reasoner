/**
 * 
 */
package org.semanticweb.elk.owl.parsing;
/*
 * #%L
 * ELK OWL Model Implementation
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DateTimeUtils {

	public static final XMLGregorianCalendar START_OF_TIME = LiteralParser.DATATYPE_FACTORY.newXMLGregorianCalendar(
			Integer.MIN_VALUE + 1, 1, 1, 0, 0, 0, 0, 0);
	public static final XMLGregorianCalendar END_OF_TIME = LiteralParser.DATATYPE_FACTORY.newXMLGregorianCalendar(
			Integer.MAX_VALUE, 12, 31, 23, 59, 59, 0, 0);

}
