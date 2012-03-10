/*
 * Copyright 2012 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.reasoner.datatypes;

import java.util.Map.Entry;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.intervals.Interval;
import org.semanticweb.elk.reasoner.datatypes.intervals.IntervalNode;
import org.semanticweb.elk.reasoner.datatypes.intervals.IntervalTree;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * Utility class for resolving datatype restriction implications
 *
 * @author Pospishnyi Olexandr
 */
public class DatatypeToolkit {

	public enum Relation {

		LESS("<"), MORE(">"), EQUAL("="), LESS_OR_EQUAL("<="), MORE_OR_EQUAL(">=");
		private String label;

		Relation(String printLabel) {
			this.label = printLabel;
		}

		@Override
		public String toString() {
			return label;
		}
	};

	public enum Domain {

		N, Z, R, TEXT, DATE, TIME, DATETIME, OTHER
	};

	public static Relation clarifyRelation(String relation) {
		if (relation == null || relation.length() == 0) {
			return null;
		}
		if ("minInclusive".equals(relation)) {
			return Relation.MORE_OR_EQUAL;
		} else if ("minExclusive".equals(relation)) {
			return Relation.MORE;
		} else if ("maxExclusive".equals(relation)) {
			return Relation.LESS;
		} else if ("maxInclusive".equals(relation)) {
			return Relation.LESS_OR_EQUAL;
		} else {
			return null;
		}
	}

	public static Domain clarifyDomain(ElkDatatype datatype) {
		if (datatype == null) {
			return null;
		}
		String dt = datatype.getDatatypeShortname();
		if ("string".equals(dt) || "PlainLiteral".equals(dt)) {
			return Domain.TEXT;
		} else if ("integer".equals(dt) || "nonPositiveInteger".equals(dt)
				|| "negativeInteger".equals(dt) || "long".equals(dt)
				|| "int".equals(dt) || "short".equals(dt) || "byte".equals(dt)) {
			return Domain.Z;
		} else if ("nonNegativeInteger".equals(dt) || "positiveInteger".equals(dt)
				|| "unsignedLong".equals(dt) || "unsignedInt".equals(dt)
				|| "unsignedShort".equals(dt) || "unsignedByte".equals(dt)) {
			return Domain.N;
		} else if ("double".equals(dt) || "float".equals(dt) || "decimal".equals(dt)) {
			return Domain.R;
		} else if ("date".equals(dt)) {
			return Domain.DATE;
		} else if ("time".equals(dt)) {
			return Domain.TIME;
		} else if ("datetime".equals(dt)) {
			return Domain.DATETIME;
		} else {
			return Domain.OTHER;
		}
	}

	private static Number getNextValueUp(Number n, Domain domain) {
		switch (domain) {
			case N:
			case Z:
				return n.longValue() + 1;
			case R:
				return Math.nextAfter(n.doubleValue(), Double.POSITIVE_INFINITY);
			default:
				return n;
		}
	}

	private static Number getNextValueDown(Number n, Domain domain) {
		switch (domain) {
			case N:
				if (n.longValue() == 0) {
					return 0;
				}
			case Z:
				return n.longValue() - 1;
			case R:
				return Math.nextAfter(n.doubleValue(), Double.NEGATIVE_INFINITY);
			default:
				return n;
		}
	}

	public static List<Interval> convertRestrictionToIntervals(List<DatatypeRestriction> restrictions) {
		List<Interval> result = new ArrayList<Interval>(2);

		Number leftEnd = Double.NEGATIVE_INFINITY;
		Number rightEnd = Double.POSITIVE_INFINITY;

		boolean isN = false;

		for (DatatypeRestriction r : restrictions) {
			if (r.value != null) {
				Number value = r.getValueAsNumber();
				switch (r.relation) {
					case LESS:
						rightEnd = getNextValueDown(value, r.domain);
						if (r.domain == Domain.N) {
							leftEnd = 0;
							isN = true;
						}
						break;
					case LESS_OR_EQUAL:
						rightEnd = value;
						if (r.domain == Domain.N) {
							leftEnd = 0;
							isN = true;
						}
						break;
					case EQUAL:
						rightEnd = value;
						leftEnd = value;
						break;
					case MORE_OR_EQUAL:
						leftEnd = value;
						break;
					case MORE:
						leftEnd = getNextValueUp(value, r.domain);
						break;
				}
			}
		}

		if (leftEnd.doubleValue() <= rightEnd.doubleValue()) {
			result.add(new Interval(leftEnd, rightEnd));
		} else {
			if (isN) {
				result.add(new Interval(0, rightEnd));
			} else {
				result.add(new Interval(Double.NEGATIVE_INFINITY, rightEnd));
			}
			result.add(new Interval(leftEnd, Double.POSITIVE_INFINITY));
		}
		return result;
	}

	public static IntervalTree makeNewIntervalTree() {
		return new IntervalTree(new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				double d1 = ((Number) o1).doubleValue();
				double d2 = ((Number) o2).doubleValue();
				return Double.compare(d1, d2);
			}
		});
	}

	public static Set<IndexedDataSomeValuesFrom> findSatisfyingExpressions(IndexedDatatypeExpression ide, IntervalTree tree) {
		List<Interval> intervals = convertRestrictionToIntervals(ide.getRestrictions());
		Set<IndexedDataSomeValuesFrom> retSet = new HashSet<IndexedDataSomeValuesFrom>(5);
		boolean first = true;
		for (Interval interval : intervals) {
			List<IntervalNode> nodes = tree.findAllNodesContaining(interval);
			Set<IndexedDataSomeValuesFrom> dtExps = new HashSet<IndexedDataSomeValuesFrom>(nodes.size());
			for (IntervalNode intervalNode : nodes) {
				dtExps.add((IndexedDataSomeValuesFrom) intervalNode.getData());
			}
			if (first) {
				retSet.addAll(dtExps);
				first = false;
			} else {
				retSet.retainAll(dtExps);
			}
		}
		return retSet;
	}

	public static Set<IndexedDataSomeValuesFrom> findSatisfyingExpressions(IndexedDatatypeExpression ide, Map<String, IndexedDataSomeValuesFrom> stringCache) {
		Set<IndexedDataSomeValuesFrom> retSet = new HashSet<IndexedDataSomeValuesFrom>(5);
		for (Entry<String, IndexedDataSomeValuesFrom> entry : stringCache.entrySet()) {
			for (DatatypeRestriction dr : ide.getRestrictions()) {
				if (computeCorollary(dr.getValueAsString(), entry.getKey())) {
					retSet.add(entry.getValue());
				}
			}
		}
		return retSet;
	}

	/**
	 * Check if one expression implies another
	 *
	 * @param expA
	 * @param expB
	 * @return returns true if expB is equal to expA or matches it as a regexp
	 */
	private static boolean computeCorollary(String expA, String expB) {
		if (expA.equals(expB)) {
			return true;
		}
		Pattern p = Pattern.compile(expB);
		Matcher m = p.matcher(expA);
		return m.matches();
	}
}
