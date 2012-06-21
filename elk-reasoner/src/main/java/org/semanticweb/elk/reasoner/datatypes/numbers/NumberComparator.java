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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class NumberComparator implements Comparator<Number> {

	public static final NumberComparator INSTANCE = new NumberComparator();
	
	public int compare(Number num1, Number num2) {
		if (num1.equals(num2)) {
			return 0;
		} else if (num1.equals(NegativeInfinity.INSTANCE) || num2.equals(PositiveInfinity.INSTANCE)) {
			return -1;
		} else if (num1.equals(PositiveInfinity.INSTANCE) || num2.equals(NegativeInfinity.INSTANCE)) {
			return 1;
		}
		int num1Type = getType(num1);
		int num2Type = getType(num2);
		int commonType = (num1Type >= num2Type) ? num1Type : num2Type;
		switch (commonType) {
			case 0: {
				Integer value1 = Integer.valueOf(num1.intValue());
				Integer value2 = Integer.valueOf(num2.intValue());
				return value1.compareTo(value2);
			}
			case 1: {
				Long value1 = Long.valueOf(num1.longValue());
				Long value2 = Long.valueOf(num2.longValue());
				return value1.compareTo(value2);
			}
			case 2: {
				BigInteger value1 = toBigInteger(num1, num1Type);
				BigInteger value2 = toBigInteger(num2, num2Type);
				return value1.compareTo(value2);
			}
			case 3: {
				BigDecimal value1 = toBigDecimal(num1, num1Type);
				BigDecimal value2 = toBigDecimal(num2, num2Type);
				return value1.compareTo(value2);
			}
			case 4: {
				BigRational value1 = toBigRational(num1, num1Type);
				BigRational value2 = toBigRational(num2, num2Type);
				return value1.compareTo(value2);
			}
			default:
				throw new IllegalArgumentException();
		}
	}

	private int getType(Number n) {
		if (n instanceof Integer) {
			return 0;
		} else if (n instanceof Long) {
			return 1;
		} else if (n instanceof BigInteger) {
			return 2;
		} else if (n instanceof BigDecimal) {
			return 3;
		} else if (n instanceof BigRational) {
			return 4;
		} else {
			throw new IllegalArgumentException();
		}
	}

	private BigInteger toBigInteger(Number n, int nType) {
		switch (nType) {
			case 0:
			case 1:
				return BigInteger.valueOf(n.longValue());
			case 2:
				return (BigInteger) n;
			default:
				throw new IllegalArgumentException();
		}
	}

	private BigDecimal toBigDecimal(Number n, int nType) {
		switch (nType) {
			case 0:
			case 1:
				return BigDecimal.valueOf(n.longValue());
			case 2:
				return new BigDecimal((BigInteger) n);
			case 3:
				return (BigDecimal) n;
			default:
				throw new IllegalArgumentException();
		}
	}

	private BigRational toBigRational(Number n, int nType) {
		switch (nType) {
			case 0:
			case 1:
				return new BigRational(BigInteger.valueOf(n.longValue()), BigInteger.ONE);
			case 2:
				return new BigRational((BigInteger) n, BigInteger.ONE);
			case 3: {
				BigDecimal decimal = (BigDecimal) n;
				return new BigRational(decimal.unscaledValue(), BigInteger.TEN.pow(decimal.scale()));
			}
			default:
				throw new IllegalArgumentException();
		}
	}
}
