/*
 * #%L
 * ELK Reasoner
 * *
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
package org.semanticweb.elk.reasoner.datatypes.handlers;

import java.util.Comparator;

import org.semanticweb.elk.owl.implementation.literals.ElkIntLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkRealLiteralImpl;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkDecimalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkIntegerLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLongLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRationalLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRealLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.parsing.NumberUtils;
import org.semanticweb.elk.owl.parsing.NumberUtils.Infinity;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.ArbitraryIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.IntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RealInterval;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;

/**
 * owl:real, owl:rational, xsd:decimal, xsd:integer and xsd:nonNegativeInteger
 * datatype handler
 * <p>
 * Datatype expressions are converted to interval presentation with lower and
 * upper bounds or a single numeric value.
 * <p>
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public class NumericDatatypeHandler extends AbstractDatatypeHandler {

	private final static Comparator<Number> comparator_ = NumberUtils.COMPARATOR;
	
	private final ElkLiteralVisitor<PointValue<?, Number>> literalConverter_ = new BaseLiteralConverter<Number>() {

		@Override
		public PointValue<?, Number> visit(ElkRationalLiteral elkLiteral) {
			return new RationalValue(elkLiteral.getNumber());
		}

		@Override
		public PointValue<?, Number> visit(ElkDecimalLiteral elkLiteral) {
			return new DecimalValue(elkLiteral.getNumber());
		}

		@Override
		public PointValue<?, Number> visit(ElkIntegerLiteral elkLiteral) {
			if (comparator_.compare(elkLiteral.getNumber(), 0) < 0) {
				return new IntegerValue(elkLiteral.getNumber());
			}
			else {
				return new NonNegativeIntegerValue(elkLiteral.getNumber());
			}
		}

		@Override
		public PointValue<?, Number> visit(ElkIntLiteral elkLiteral) {
			if (comparator_.compare(elkLiteral.getNumber(), 0) < 0) {
				return new IntegerValue(elkLiteral.getNumber());
			}
			else {
				return new NonNegativeIntegerValue(elkLiteral.getNumber());
			}
		}

		@Override
		public PointValue<?, Number> visit(ElkLongLiteral elkLiteral) {
			if (comparator_.compare(elkLiteral.getNumber(), 0) < 0) {
				return new IntegerValue(elkLiteral.getNumber());
			}
			else {
				return new NonNegativeIntegerValue(elkLiteral.getNumber());
			}
		}

	};
	
	/**
	 * a stateless visitor for converting data ranges into numerical value spaces.
	 */
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseDataRangeConverter() {
		/**
		 * a stateless visitor for creating entire value spaces for numerical datatypes.
		 */
		private final ElkDatatypeVisitor<EntireValueSpace<?>> entireValueSpaceCreator_ = new BaseDatatypeVisitor<EntireValueSpace<?>>() {

			@Override
			public EntireValueSpace<?> visit(
					RealDatatype datatype) {
				return EntireValueSpace.OWL_REAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					RationalDatatype datatype) {
				return EntireValueSpace.OWL_RATIONAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					DecimalDatatype datatype) {
				return EntireValueSpace.XSD_DECIMAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					IntegerDatatype datatype) {
				return EntireValueSpace.XSD_INTEGER;
			}
			
			@Override
			public EntireValueSpace<?> visit(
					NonNegativeIntegerDatatype datatype) {
				return EntireValueSpace.XSD_NON_NEGATIVE_INTEGER;
			}
			
		};
		
		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return elkDatatype.accept(entireValueSpaceCreator_);
		}

		@Override
		public ValueSpace<?> visit(
				ElkDatatypeRestriction elkDatatypeRestriction) {
			ElkRealLiteral lowerBound = null, upperBound = null;
			boolean lowerInclusive = false, upperInclusive = false;
			RealDatatype datatype = (RealDatatype) elkDatatypeRestriction.getDatatype();

			// process all facet restrictions
			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
					.getFacetRestrictions()) {
				ElkRealLiteral restrictionValue = asNumericLiteral(facetRestriction.getRestrictionValue());

				switch (PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet())) {
					case XSD_MIN_INCLUSIVE: // >=
						if (lowerBound == null || comparator_.compare(restrictionValue.getNumber(), lowerBound.getNumber()) >= 0) {
							lowerBound = restrictionValue;
							lowerInclusive = true;
						}
						break;
					case XSD_MIN_EXCLUSIVE: // >
						if (lowerBound == null ||comparator_.compare(restrictionValue.getNumber(), lowerBound.getNumber()) >= 0) {
							lowerBound = restrictionValue;
							lowerInclusive = false;
						}
						break;
					case XSD_MAX_INCLUSIVE: // <=
						if (upperBound == null ||comparator_.compare(restrictionValue.getNumber(), upperBound.getNumber()) <= 0) {
							upperBound = restrictionValue;
							upperInclusive = true;
						}
						break;
					case XSD_MAX_EXCLUSIVE: // <
						if (upperBound == null ||comparator_.compare(restrictionValue.getNumber(), upperBound.getNumber()) <= 0) {
							upperBound = restrictionValue;
							upperInclusive = false;
						}
						break;
				default:
					throw new ElkIndexingException("Unsupported constraining facet: " + facetRestriction.getConstrainingFacet());
				}
			}
			// set default bounds, if needed
			if (lowerBound == null) {
				lowerBound = datatype == ElkDatatypeMap.XSD_NON_NEGATIVE_INTEGER ? new ElkIntLiteralImpl("0", 0) : negativeInfinity(datatype);
				lowerInclusive = true;
			}
			
			upperBound = upperBound == null ? positiveInfinity(datatype) : upperBound;
			
			final ElkRealLiteral lowerPoint = lowerBound;
			// validate bound datatypes
			validateFacetValue(elkDatatypeRestriction, lowerPoint.getDatatype(), lowerPoint.toString());
			
			// build representing interval				
			if (isUnitInterval(lowerBound.getNumber(), lowerInclusive, upperBound.getNumber(), upperInclusive)) {
				return createValueSpace(lowerBound);
			} else {			
				final ElkRealLiteral upperPoint = upperBound;
				final boolean li = lowerInclusive, ui = upperInclusive;
				// make sure that restriction values belong to the value space of the data range
				validateFacetValue(elkDatatypeRestriction, upperPoint.getDatatype(), upperPoint.toString());
				
				return datatype.accept(new BaseDatatypeVisitor<ValueSpace<?>>() {

					@Override
					public ValueSpace<?> visit(RealDatatype datatype) {
						return new RealInterval(lowerPoint.getNumber(), li, upperPoint.getNumber(), ui);
					}
					
					@Override
					public ValueSpace<?> visit(RationalDatatype datatype) {
						return new RationalInterval(lowerPoint.getNumber(), li, upperPoint.getNumber(), ui);
					}

					@Override
					public ValueSpace<?> visit(DecimalDatatype datatype) {
						return new DecimalInterval(lowerPoint.getNumber(), li, upperPoint.getNumber(), ui);
					}

					@Override
					public ValueSpace<?> visit(IntegerDatatype datatype) {
						if (comparator_.compare(lowerPoint.getNumber(), 0) >= 0) {
							return new NonNegativeIntegerInterval(lowerPoint.getNumber(), li, upperPoint.getNumber(), ui);
						}
						else {
							return new ArbitraryIntegerInterval(lowerPoint.getNumber(), li, upperPoint.getNumber(), ui);
						} 
					}

					@Override
					public ValueSpace<?> visit(
							NonNegativeIntegerDatatype datatype) {
						
						boolean lessThanZero = comparator_.compare(lowerPoint.getNumber(), 0) < 0;
						
						return new NonNegativeIntegerInterval(lessThanZero ? 0 : lowerPoint.getNumber(), li || lessThanZero, upperPoint.getNumber(), ui);
					}

					
				});
			}
		}

		private boolean isUnitInterval(Number lowerBound,
				boolean lowerInclusive, Number upperBound,
				boolean upperInclusive) {
			return lowerInclusive && upperInclusive
					&& comparator_.compare(lowerBound, upperBound) == 0;
		}
		
	};
	
	@Override
	public PointValue<?, Number> createValueSpace(ElkLiteral literal) {
		return literal.accept(literalConverter_);
	}

	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return dataRangeConverter_;
	}
	
	private ElkRealLiteral positiveInfinity(RealDatatype datatype) {
		return new ElkInfinityLiteral(NumberUtils.POSITIVE_INFINITY, datatype);
	}
	
	private ElkRealLiteral negativeInfinity(RealDatatype datatype) {
		return new ElkInfinityLiteral(NumberUtils.NEGATIVE_INFINITY, datatype);
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class ElkInfinityLiteral extends ElkRealLiteralImpl {

		private final RealDatatype datatype_;
		
		ElkInfinityLiteral(Infinity inf, RealDatatype datatype) {
			super(inf.toString(), inf);
			datatype_ = datatype;
		}
		
		@Override
		public RealDatatype getDatatype() {
			return datatype_;
		}

		@Override
		public String toString() {
			return getNumber().toString();
		}		
		
	}
}
