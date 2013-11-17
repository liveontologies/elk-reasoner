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
package org.semanticweb.elk.reasoner.datatypes.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.BigRational;

/**
 * Utility class for various operations with {@link Number}
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class NumberUtils {

	public static enum TYPE {

		Int {
			@Override
			public int order() {
				return 0;
			}
		},
		Long {
			@Override
			int order() {
				return 1;
			}
		},
		Integer {
			@Override
			int order() {
				return 2;
			}
		},
		Decimal {
			@Override
			int order() {
				return 3;
			}
		},
		Rational {
			@Override
			int order() {
				return 4;
			}
		};

		abstract int order();

	}
	
	public static final Number NEGATIVE_INFINITY = new Number() {

		private static final long serialVersionUID = 1L;

		@Override
		public int intValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long longValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public float floatValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double doubleValue() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String toString() {
			return "-INF";
		}

	};
	
	public static final Number POSITIVE_INFINITY = new Number() {

		private static final long serialVersionUID = 1L;

		@Override
		public int intValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long longValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public float floatValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double doubleValue() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String toString() {
			return "+INF";
		}
		
	};
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public static TYPE getRuntimeType(Number n) {
		if (n == POSITIVE_INFINITY || n == NEGATIVE_INFINITY) {
			return TYPE.Integer;
		}
		else if (n instanceof Integer) {
			return TYPE.Int;
		} else if (n instanceof Long) {
			return TYPE.Long;
		} else if (n instanceof BigInteger) {
			return TYPE.Integer;
		} else if (n instanceof BigDecimal) {
			return TYPE.Decimal;
		} else if (n instanceof BigRational) {
			return TYPE.Rational;
		} else {
			throw new IllegalArgumentException("Illegal number type: " + n.getClass());
		}
	}
	
	public static TYPE getCommonType(Number num1, Number num2) {
		return getCommonType(getRuntimeType(num1), getRuntimeType(num2));
	}
	
	public static TYPE getCommonType(TYPE type1, TYPE type2) {
		return type1.order() >= type2.order() ? type1 : type2;
	}
	
	/**
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static int compare(Number num1, Number num2) {
		if (num1.equals(num2)) {
			return 0;
		} else if (num1 == NEGATIVE_INFINITY || num2 == POSITIVE_INFINITY) {
			return -1;
		} else if (num1 == POSITIVE_INFINITY || num2 == NEGATIVE_INFINITY) {
			return 1;
		}
		//type-aware comparison
		TYPE num1Type = getRuntimeType(num1);
		TYPE num2Type = getRuntimeType(num2);
		TYPE commonType = getCommonType(num1Type, num2Type);
		
		switch (commonType) {
			case Int: {
				return compareInts(num1.intValue(), num2.intValue());
			}
			case Long: {
				return compareLongs(num1.longValue(), num2.longValue());
			}
			case Integer: {
				return toBigInteger(num1, num1Type).compareTo(toBigInteger(num2, num2Type));
			}
			case Decimal: {
				return toBigDecimal(num1, num1Type).compareTo(toBigDecimal(num2, num2Type));
			}
			case Rational: {
				return toBigRational(num1, num1Type).compareTo(toBigRational(num2, num2Type));
			}
			default:
				throw new IllegalArgumentException("Unexpected number type: " + commonType);
		}
	}

	public static int compareInts(int a, int b) {
	    return (a < b) ? -1 : ((a > b) ? 1 : 0);
	}
	
	public static int compareLongs(long a, long b) {
	    return (a < b) ? -1 : ((a > b) ? 1 : 0);
	}
	
	private static BigInteger toBigInteger(Number n, TYPE nType) {
		switch (nType) {
			case Int:
			case Long:
				return BigInteger.valueOf(n.longValue());
			case Integer:
				return (BigInteger) n;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static BigDecimal toBigDecimal(Number n, TYPE nType) {
		switch (nType) {
			case Int:
			case Long:
				return BigDecimal.valueOf(n.longValue());
			case Integer:
				return new BigDecimal((BigInteger) n);
			case Decimal:
				return (BigDecimal) n;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static BigRational toBigRational(Number n, TYPE nType) {
		switch (nType) {
			case Int:
			case Long:
				return new BigRational(BigInteger.valueOf(n.longValue()), BigInteger.ONE);
			case Integer:
				return new BigRational((BigInteger) n, BigInteger.ONE);
			case Decimal: 
				BigDecimal decimal = (BigDecimal) n;
				return new BigRational(decimal.unscaledValue(), BigInteger.TEN.pow(decimal.scale()));
			case Rational: 
				return (BigRational) n;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public static Number increment(Number num) {
		switch (getRuntimeType(num)) {
		case Int:
			return num.intValue() + 1;
		case Long:
			return num.longValue() + 1;
		case Integer:
			return ((BigInteger) num).add(BigInteger.ONE);
		default:
			throw new IllegalArgumentException("Cannot increment a non-integer number " + num);
		}
	}
	
	public static Number decrement(Number num) {
		switch (getRuntimeType(num)) {
		case Int:
			return num.intValue() -1;
		case Long:
			return num.longValue() - 1;
		case Integer:
			return ((BigInteger) num).subtract(BigInteger.ONE);
		default:
			throw new IllegalArgumentException("Cannot increment a non-integer number" + num);
		}
	}
	
	/**
	 * 
	 */
	public static final Comparator<Number> COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(Number o1, Number o2) {
			return NumberUtils.compare(o1, o2);
		}
		
	};
}
