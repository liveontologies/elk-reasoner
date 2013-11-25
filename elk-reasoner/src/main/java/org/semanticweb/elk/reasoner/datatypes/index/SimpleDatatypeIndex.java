/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.datatypes.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.LiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.StringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.UndefinedDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.XmlLiteralDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * Simple (and inefficient) storage and retrieval of datatype expressions. All
 * expressions belonging to the same datatype hierarchy (and thus sharing a
 * value space among themselves) are stored in a single collection.
 *
 * @author Pospishnyi Oleksandr
 */
public class SimpleDatatypeIndex implements DatatypeIndex {

	private final StorageSelector storage_ = new StorageSelector();

	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		Set<IndexedDatatypeExpression> expressionsByDatatype =
			ide.getValueSpace().getDatatype().accept(storage_);
		expressionsByDatatype.add(ide);
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		boolean success = false;
		if (ide != null) {
			Set<IndexedDatatypeExpression> expressionsByDatatype =
				ide.getValueSpace().getDatatype().accept(storage_);
			success = expressionsByDatatype.remove(ide);
		}
		return success;
	}

	@Override
	public Collection<IndexedDatatypeExpression> getSubsumersFor(IndexedDatatypeExpression ide) {
		Set<IndexedDatatypeExpression> expressionsByDatatype =
			ide.getValueSpace().getDatatype().accept(storage_);
		Collection<IndexedDatatypeExpression> resultSet = new ArrayList<IndexedDatatypeExpression>(5);

		if (expressionsByDatatype != null) {
			for (IndexedDatatypeExpression exp : expressionsByDatatype) {
				if (exp.getValueSpace().contains(ide.getValueSpace())) {
					resultSet.add(exp);
				}
			}
		}

		Set<IndexedDatatypeExpression> rdfsLiteralExpressions = ElkDatatypeMap.RDFS_LITERAL
				.accept(storage_);

		if (!rdfsLiteralExpressions.isEmpty()) {
			//using all expressions for rdfs:Literal datatype
			for (IndexedDatatypeExpression exp : rdfsLiteralExpressions) {
				if (exp.getValueSpace().contains(ide.getValueSpace())) {
					resultSet.add(exp);
				}
			}
		}

		return resultSet;
	}

	@Override
	public void appendTo(DatatypeIndex index) {
		for (Set<IndexedDatatypeExpression> pool : storage_.getAllPools()) {
			if (pool != null) {
				for (IndexedDatatypeExpression datatypeExpression : pool) {
					index.addDatatypeExpression(datatypeExpression);
				}
			}
		}
	}

	private class StorageSelector implements ElkDatatypeVisitor<Set<IndexedDatatypeExpression>> {

		private Set<IndexedDatatypeExpression> numericDatatypePool;
		private Set<IndexedDatatypeExpression> dateTimeDatatypePool;
		private Set<IndexedDatatypeExpression> binaryDatatypePool;
		private Set<IndexedDatatypeExpression> stringDatatypePool;
		private Set<IndexedDatatypeExpression> literalDatatypePool;

		@Override
		public Set<IndexedDatatypeExpression> visit(LiteralDatatype datatype) {
			if (literalDatatypePool == null) {
				literalDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return literalDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(DateTimeDatatype datatype) {
			if (dateTimeDatatypePool == null) {
				dateTimeDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return dateTimeDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(DateTimeStampDatatype datatype) {
			if (dateTimeDatatypePool == null) {
				dateTimeDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return dateTimeDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(Base64BinaryDatatype datatype) {
			if (binaryDatatypePool == null) {
				binaryDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return binaryDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(HexBinaryDatatype datatype) {
			if (binaryDatatypePool == null) {
				binaryDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return binaryDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(AnyUriDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(RealDatatype datatype) {
			if (numericDatatypePool == null) {
				numericDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return numericDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(RationalDatatype datatype) {
			if (numericDatatypePool == null) {
				numericDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return numericDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(DecimalDatatype datatype) {
			if (numericDatatypePool == null) {
				numericDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return numericDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(IntegerDatatype datatype) {
			if (numericDatatypePool == null) {
				numericDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return numericDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(NonNegativeIntegerDatatype datatype) {
			if (numericDatatypePool == null) {
				numericDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return numericDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(PlainLiteralDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(StringDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(NormalizedStringDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(TokenDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(NameDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(NcNameDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(NmTokenDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(XmlLiteralDatatype datatype) {
			if (stringDatatypePool == null) {
				stringDatatypePool = new HashSet<IndexedDatatypeExpression>();
			}
			return stringDatatypePool;
		}

		@Override
		public Set<IndexedDatatypeExpression> visit(UndefinedDatatype datatype) {
			return Collections.emptySet();
		}

		@SuppressWarnings("unchecked")
		private Collection<Set<IndexedDatatypeExpression>> getAllPools() {
			return Arrays.asList(numericDatatypePool, dateTimeDatatypePool, 
				binaryDatatypePool, stringDatatypePool, literalDatatypePool);
		}
	}
}
