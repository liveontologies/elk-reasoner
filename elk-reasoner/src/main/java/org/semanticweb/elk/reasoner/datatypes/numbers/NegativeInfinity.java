/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.datatypes.numbers;

/**
 * Custom numeric type that represent negative infinity
 *
 * @author Pospishnyi Olexandr
 */
public final class NegativeInfinity extends Number {

	public static final NegativeInfinity INSTANCE = new NegativeInfinity();

	private NegativeInfinity() {
	}

	public double doubleValue() {
		throw new UnsupportedOperationException();
	}

	public float floatValue() {
		throw new UnsupportedOperationException();
	}

	public int intValue() {
		throw new UnsupportedOperationException();
	}

	public long longValue() {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return "-INF";
	}
}
