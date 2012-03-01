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

package org.semanticweb.elk.reasoner.rules;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for resolving datatype restriction implications
 *
 * @author Pospishnyi Olexandr
 */
public class DatatypeResolutionEngine {
	
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
	
	public static boolean computeCorollary(DatatypeRestriction r1, DatatypeRestriction r2) {
		if (r1.equals(r2)) {
			return true;
		}
		if (r1.domain != r2.domain) {
			return false;
		}
		if (r2.relation == null && r2.value == null) {
			return true;
		}
		if (r1.relation == null && r1.value == null) {
			return false;
		}
		switch (r1.domain) {
			case N:
			case Z:
			case R:
				return computeCorollary(r1.relation, r1.getValueAsNumber(), r2.relation, r2.getValueAsNumber(), r1.domain);
			case TEXT:
				return computeCorollary(r1.getValueAsString(), r2.getValueAsString());
			case DATE:
			case TIME:
			case DATETIME:
				return computeCorollary(r1.relation, r1.getValueAsDate(), r2.relation, r2.getValueAsDate());
		}
		return false;
	}
	
	/**
	 * Compute a corollary between r+ and r- w.r.t. numerical domain
	 *
	 * @param r_n r+ relation
	 * @param n   r+ value
	 * @param r_m r- relation
	 * @param m   r- value
	 * @param domain numeric domain
	 *
	 * Rules:
	 * (<,n)  -> (<,m) : n <= m
	 * (<,n)  -> (<=,m) : n <= m+1, D = N, Z, or n <= m, D = R
	 * (<,n)  -> (=,m) : n = 0, D = N or n = 1, m = 0, D = N
	 * (<,n)  -> (>=,m) : n = 0, D = N
	 * (<,n)  -> (>,m) : n = 0, D = N
	 * (<=,n) -> (<,m) : n < m
	 * (<=,n) -> (<=,m) : n <= m
	 * (<=,n) -> (=,m) : n = m = 0, D = N
	 * (<=,n) -> (>=,m) : never
	 * (<=,n) -> (>,m) : never
	 * (=,n)  -> (<,m) : n < m
	 * (=,n)  -> (<=,m) : n <= m
	 * (=,n)  -> (=,m) : n = m
	 * (=,n)  -> (>=,m) : n >= m
	 * (=,n)  -> (>,m) : n > m
	 * (>=,n) -> (<,m) : never
	 * (>=,n) -> (<=,m) : never
	 * (>=,n) -> (=,m) : never
	 * (>=,n) -> (>=,m) : n >= m
	 * (>=,n) -> (>,m) : n > m
	 * (>,n)  -> (<,m) : never
	 * (>,n)  -> (<=,m) : never
	 * (>,n)  -> (=,m) : never
	 * (>,n)  -> (>=,m) : n >= m−1, D = N, Z, or n >= m, D = R
	 * (>,n)  -> (>,m) : n >= m
	 *
	 * @return is r+ -> r-
	 */
	private static boolean computeCorollary(Relation r_n, Number n, Relation r_m, Number m, Domain domain) {
		switch (domain) {
			case N: return computeCorollaryForN(r_n, n.longValue(), r_m, m.longValue());
			case Z: return computeCorollaryForZ(r_n, n.longValue(), r_m, m.longValue());
			case R: return computeCorollaryForR(r_n, n.doubleValue(), r_m, m.doubleValue());
		}
		return false;
	}
	
	/**
	 * Compute unsatisfiability of r+ w.r.t. numerical domain
	 *
	 * @param r_n r+ relation
	 * @param n   r+ value
	 * @param domain numeric domain
	 *
	 * Rules:
	 * (<,n)  -> ⊥ : n = 0, D = N
	 * (<=,n) -> ⊥ : never
	 * (=,n)  -> ⊥ : never
	 * (>=,n) -> ⊥ : never
	 * (>,n)  -> ⊥ : never
	 *
	 * @return is r+ -> ⊥
	 */
	public static boolean computeBottomConcept(Relation r_n, Number n, Domain domain) {
		if (domain == Domain.N && r_n == Relation.LESS && n.longValue() == 0) {
			return true;
		} else return false;
	}
	
	private static boolean computeCorollaryForN(Relation r_n, long n, Relation r_m, long m) {
		
		switch (r_n) {
			case LESS:
				switch (r_m) {
					case LESS: return n <= m;
					case LESS_OR_EQUAL: return n <= m + 1;
					case EQUAL: return n == 0 || (n == 1 && m == 0);
					case MORE_OR_EQUAL: return n == 0;
					case MORE: return n == 0;
				}
			case LESS_OR_EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return n == 0 && m == 0;
					case MORE_OR_EQUAL: return false;
					case MORE: return false;
				}
			case EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return n == m;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE_OR_EQUAL:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m - 1;
					case MORE: return n >= m;
				}
		}
		return false;
	}
	
	private static boolean computeCorollaryForZ(Relation r_n, long n, Relation r_m, long m) {
		
		switch (r_n) {
			case LESS:
				switch (r_m) {
					case LESS: return n <= m;
					case LESS_OR_EQUAL: return n <= m + 1;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return false;
					case MORE: return false;
				}
			case LESS_OR_EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return false;
					case MORE: return false;
				}
			case EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return n == m;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE_OR_EQUAL:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m - 1;
					case MORE: return n >= m;
				}
		}
		return false;
	}
	
	private static boolean computeCorollaryForR(Relation r_n, double n, Relation r_m, double m) {
		
		switch (r_n) {
			case LESS:
				switch (r_m) {
					case LESS: return n <= m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return false;
					case MORE: return false;
				}
			case LESS_OR_EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return false;
					case MORE: return false;
				}
			case EQUAL:
				switch (r_m) {
					case LESS: return n < m;
					case LESS_OR_EQUAL: return n <= m;
					case EQUAL: return n == m;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE_OR_EQUAL:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n > m;
				}
			case MORE:
				switch (r_m) {
					case LESS: return false;
					case LESS_OR_EQUAL: return false;
					case EQUAL: return false;
					case MORE_OR_EQUAL: return n >= m;
					case MORE: return n >= m;
				}
		}
		return false;
	}
	
	/**
	 * Check if one expression implies another
	 *
	 * @param expA
	 * @param expB
	 * @return returns true if expB is equal to expA or matches it as a regexp
	 */
	private static boolean computeCorollary(String expA, String expB) {
		if (expA.equals(expB)) return true;
		Pattern p = Pattern.compile(expB);
		Matcher m = p.matcher(expA);
		return m.matches();
	}
	
	private static boolean computeCorollary(Relation r_n, Date n, Relation r_m, Date m) {
		return computeCorollaryForN(r_n, n.getTime(), r_m, m.getTime());
	}

}
