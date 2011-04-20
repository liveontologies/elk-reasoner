/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/** OWL 2 grammar ANTLR v3 lexer
 * 
 * Follows the offical OWL 2 specifications: 
 * http://www.w3.org/TR/owl2-syntax/#Appendix:_Complete_Grammar_.28Normative.29
 * Some lexing definitions use terminals from SPQRQL specification:  
 * http://www.w3.org/TR/2008/REC-rdf-sparql-query-20080115/#sparqlGrammar
 * 
 * @author Yevgeny Kazakov, Apr 19, 2011
 */
 
lexer grammar Owl2FunctionalStyleLexer;

options {
  language = Java;  
}
tokens {
/* Reserved Keywords */
  OWL_BACKWARD_COMPATIBLE_WITH; 
  OWL_BOTTOM_DATA_PROPERTY;
  OWL_BOTTOM_OBJECT_PROPERTY;  
  OWL_DEPRECATED;
  OWL_INCOMPATIBLE_WITH;
  OWL_NOTHING;
  OWL_PRIOR_VERSION;
  OWL_RATIONAL; 
  OWL_REAL;
  OWL_VERSION_INFO;  
  OWL_THING;
  OWL_TOP_DATA_PROPERTY;
  OWL_TOP_OBJECT_PROPERTY;
  RDF_LANG_RANGE;
  RDF_PLAIN_LITERAL;
  RDF_XML_LITERAL;
  RDFS_COMMENT;
  RDFS_IS_DEFINED_BY;
  RDFS_LABEL;
  RDFS_LITERAL;
  RDFS_SEE_ALSO;
  XSD_ANY_URI;
  XSD_BASE_64_BINARY;
  XSD_BOOLEAN;
  XSD_BYTE;
  XSD_DATE_TIME;
  XSD_DATE_TIMEStamp;
  XSD_DECIMAL;
  XSD_DOUBLE;  
  XSD_FLOAT;
  XSD_HEX_BINARY;  
  XSD_INT;
  XSD_INTeger;
  XSD_LANGUAGE;
  XSD_LENGTH;
  XSD_LONG;
  XSD_MAX_EXCLUSIVE;
  XSD_MAX_INCLUSIVE;
  XSD_MAX_LENGTH;
  XSD_MIN_EXCLUSIVE;
  XSD_MIN_INCLUSIVE;
  XSD_MIN_LENGTH;
  XSD_NAME;
  XSD_NC_NAME;
  XSD_NEGATIVE_INTEGER;
  Xsd_NMTOKEN;
  XSD_NON_NEGATIVE_INTEGER;
  XSD_NON_POSITIVE_INTEGER;
  XSD_NORMALIZED_STRING;
  XSD_PATTERN;
  XSD_POSITIVE_INTEGER;
  XSD_SHORT;
  XSD_STRING;
  XSD_TOKEN;
  XSD_UNSIGNED_BYTE;
  XSD_UNSIGNED_INT;
  XSD_UNSIGNED_LONG;
  XSD_UNSIGNED_SHORT;

/* Ontology Keywords */
  PREFIX;
  ONTOLOGY;
  IMPORT;
  
/* Entities and Literals */
  CLASS;
  DATATYPE;
  OBJECT_PROPERTY;
  DATA_PROPERTY;
  ANNOTATION_PROPERTY;
  NAMED_INDIVIDUAL;
  
/* Declaration */
  DECLARATION;
  
/* Property Expressions */
  OBJECT_INVERSE_OF;
  OBJECT_PROPERTY_CHAIN;
    
/* Data Ranges */
  COMPLEMENT_OF;
  ONE_OF;
  DATATYPE_RESTRICTION;
    
/* Class Expressions */  
  OBJECT_INTERSECTION_OF;  
  OBJECT_UNION_OF;
  OBJECT_COMPLEMENT_OF;
  OBJECT_ONE_OF;
  OBJECT_SOME_VALUES_FROM;
  OBJECT_ALL_VALUES_FROM;
  OBJECT_HAS_VALUE;
  OBJECT_HAS_SELF;
  OBJECT_MIN_CARDINALITY; 
  OBJECT_MAX_CARDINALITY;
  OBJECT_EXACT_CARDINALITY;
  DATA_INTERSECTION_OF;    
  DATA_UNION_OF;         
  DATA_COMPLEMENT_OF;    
  DATA_ONE_OF;
  DATA_SOME_VALUES_FROM;  
  DATA_ALL_VALUES_FROM;   
  DATA_HAS_VALUE;                 
  DATA_MIN_CARDINALITY;  
  DATA_MAX_CARDINALITY;  
  DATA_EXACT_CARDINALITY;
   
/* Class Expressions Axioms */
  SUB_CLASS_OF;
  EQUIVALENT_CLASSES;
  DISJOINT_CLASSES;    
  DISJOINT_UNION;
    
/* Object Property Axioms */ 
  SUB_OBJECT_PROPERTY_OF;
  EQUIVALENT_OBJECT_PROPERTIES;
  DISJOINT_OBJECT_PROPERTIES;
  OBJECT_PROPERTY_DOMAIN;
  OBJECT_PROPERTY_RANGE;
  INVERSE_OBJECT_PROPERTIES; 
  FUNCTIONAL_OBJECT_PROPERTY;
  INVERSE_FUNCTIONAL_OBJECT_PROPERTY;
  REFLEXIVE_OBJECT_PROPERTY;
  IRREFLEXIVE_OBJECT_PROPERTY;
  SYMMETRIC_OBJECT_PROPERTY;
  ASYMMETRIC_OBJECT_PROPERTY;
  TRANSITIVE_OBJECT_PROPERTY;
   
/* Data Property Axioms */          
  SUB_DATA_PROPERTY_OF;          
  EQUIVALENT_DATA_PROPERTIES;   
  DISJOINT_DATA_PROPERTIES;     
  DATA_PROPERTY_DOMAIN;         
  DATA_PROPERTY_RANGE;      
  FUNCTIONAL_DATA_PROPERTY;     
  DATATYPE_DEFINITION;
  
/* Keys */        
  HAS_KEY;
    
/* Assertions */
  SAME_INDIVIDUAL;
  DIFFERENT_INDIVIDUALS;
  CLASS_ASSERTION;
  OBJECT_PROPERTY_ASSERTION;
  NEGATIVE_OBJECT_PROPERTY_ASSERTION;
  DATA_PROPERTY_ASSERTION;
  NEGATIVE_DATA_PROPERTY_ASSERTION;
   
/* Annotations */
  ANNOTATION;
  ANNOTATION_ASSERTION;
  SUB_ANNOTATION_PROPERTY_OF;
  ANNOTATION_PROPERTY_DOMAIN;         
  ANNOTATION_PROPERTY_RANGE;
     
}


@header {
  package org.semanticweb.elk.parser;
  
  import java.util.Hashtable;
}

@members {

  @SuppressWarnings("serial")
  private final Hashtable<String,Integer> reservedIriTable = 
      new Hashtable<String, Integer>() {{
  
  /* Reserved Keywords */
  put("owl:backwardCompatibleWith",       OWL_BACKWARD_COMPATIBLE_WITH); 
  put("owl:bottomDataProperty",           OWL_BOTTOM_DATA_PROPERTY);
  put("owl:bottomObjectProperty",         OWL_BOTTOM_OBJECT_PROPERTY);  
  put("owl:deprecated",                   OWL_DEPRECATED);
  put("owl:incompatibleWith",             OWL_INCOMPATIBLE_WITH);
  put("owl:Nothing",                      OWL_NOTHING);
  put("owl:priorVersion",                 OWL_PRIOR_VERSION);
  put("owl:rational",                     OWL_RATIONAL); 
  put("owl:real",                         OWL_REAL);
  put("owl:versionInfo",                  OWL_VERSION_INFO);  
  put("owl:Thing",                        OWL_THING);
  put("owl:topDataProperty",              OWL_TOP_DATA_PROPERTY);
  put("owl:topObjectProperty",            OWL_TOP_OBJECT_PROPERTY);
  put("rdf:langRange",                    RDF_LANG_RANGE);
  put("rdf:PlainLiteral",                 RDF_PLAIN_LITERAL);
  put("rdf:XMLLiteral",                   RDF_XML_LITERAL);
  put("rdfs:comment",                     RDFS_COMMENT);
  put("rdfs:isDefinedBy",                 RDFS_IS_DEFINED_BY);
  put("rdfs:label",                       RDFS_LABEL);
  put("rdfs:Literal",                     RDFS_LITERAL);
  put("rdfs:seeAlso",                     RDFS_SEE_ALSO);
  put("xsd:anyURI",                       XSD_ANY_URI);
  put("xsd:base64Binary",                 XSD_BASE_64_BINARY);
  put("xsd:boolean",                      XSD_BOOLEAN);
  put("xsd:byte",                         XSD_BYTE);
  put("xsd:dateTime",                     XSD_DATE_TIME);
  put("xsd:dateTimeStamp",                XSD_DATE_TIMEStamp);
  put("xsd:decimal",                      XSD_DECIMAL);
  put("xsd:double",                       XSD_DOUBLE);  
  put("xsd:float",                        XSD_FLOAT);
  put("xsd:hexBinary",                    XSD_HEX_BINARY);  
  put("xsd:int",                          XSD_INT);
  put("xsd:integer",                      XSD_INTeger);
  put("xsd:language",                     XSD_LANGUAGE);
  put("xsd:length",                       XSD_LENGTH);
  put("xsd:long",                         XSD_LONG);
  put("xsd:maxExclusive",                 XSD_MAX_EXCLUSIVE);
  put("xsd:maxInclusive",                 XSD_MAX_INCLUSIVE);
  put("xsd:maxLength",                    XSD_MAX_LENGTH);
  put("xsd:minExclusive",                 XSD_MIN_EXCLUSIVE);
  put("xsd:minInclusive",                 XSD_MIN_INCLUSIVE);
  put("xsd:minLength",                    XSD_MIN_LENGTH);
  put("xsd:Name",                         XSD_NAME);
  put("xsd:NCName",                       XSD_NC_NAME);
  put("xsd:negativeInteger",              XSD_NEGATIVE_INTEGER);
  put("xsd:NMTOKEN",                      Xsd_NMTOKEN);
  put("xsd:nonNegativeInteger",           XSD_NON_NEGATIVE_INTEGER);
  put("xsd:nonPositiveInteger",           XSD_NON_POSITIVE_INTEGER);
  put("xsd:normalizedString",             XSD_NORMALIZED_STRING);
  put("xsd:pattern",                      XSD_PATTERN);
  put("xsd:positiveInteger",              XSD_POSITIVE_INTEGER);
  put("xsd:short",                        XSD_SHORT);
  put("xsd:string",                       XSD_STRING);
  put("xsd:token",                        XSD_TOKEN);
  put("xsd:unsignedByte",                 XSD_UNSIGNED_BYTE);
  put("xsd:unsignedInt",                  XSD_UNSIGNED_INT);
  put("xsd:unsignedLong",                 XSD_UNSIGNED_LONG);
  put("xsd:unsignedShort",                XSD_UNSIGNED_SHORT);
  }};
  

  @SuppressWarnings("serial")
  private final Hashtable<String,Integer> reservedKeywordTable = 
      new Hashtable<String, Integer>() {{    
  
/* Ontology Keywords */
  put("Prefix",                          PREFIX);
  put("Ontology",                        ONTOLOGY);
  put("Import",                          IMPORT);
  
/* Entities and Literals */
  put("Class",                           CLASS);
  put("Datatype",                        DATATYPE);
  put("ObjectProperty",                  OBJECT_PROPERTY);
  put("DataProperty",                    DATA_PROPERTY);
  put("AnnotationProperty",              ANNOTATION_PROPERTY);
  put("NamedIndividual",                 NAMED_INDIVIDUAL);
  
/* Declaration */
  put("Declaration",                     DECLARATION);
  
/* Property Expressions */
  put("ObjectInverseOf",                 OBJECT_INVERSE_OF);
  put("ObjectPropertyChain",             OBJECT_PROPERTY_CHAIN);
    
/* Data Ranges */
  put("ComplementOf",                    COMPLEMENT_OF);
  put("OneOf",                           ONE_OF);
  put("DatatypeRestriction",             DATATYPE_RESTRICTION);
    
/* Class Expressions */  
  put("ObjectIntersectionOf",            OBJECT_INTERSECTION_OF);  
  put("ObjectUnionOf",                   OBJECT_UNION_OF);
  put("ObjectComplementOf",              OBJECT_COMPLEMENT_OF);
  put("ObjectOneOf",                     OBJECT_ONE_OF);
  put("ObjectSomeValuesFrom",            OBJECT_SOME_VALUES_FROM);
  put("ObjectAllValuesFrom",             OBJECT_ALL_VALUES_FROM);
  put("ObjectHasValue",                  OBJECT_HAS_VALUE);
  put("ObjectHasSelf",                   OBJECT_HAS_SELF);
  put("ObjectMinCardinality",            OBJECT_MIN_CARDINALITY); 
  put("ObjectMaxCardinality",            OBJECT_MAX_CARDINALITY);
  put("ObjectExactCardinality",          OBJECT_EXACT_CARDINALITY);
  put("DataIntersectionOf",              DATA_INTERSECTION_OF);    
  put("DataUnionOf",                     DATA_UNION_OF);         
  put("DataComplementOf",                DATA_COMPLEMENT_OF);    
  put("DataOneOf",                       DATA_ONE_OF);
  put("DataSomeValuesFrom",              DATA_SOME_VALUES_FROM);  
  put("DataAllValuesFrom",               DATA_ALL_VALUES_FROM);   
  put("DataHasValue",                    DATA_HAS_VALUE);        
  put("DataMinCardinality",              DATA_MIN_CARDINALITY);  
  put("DataMaxCardinality",              DATA_MAX_CARDINALITY);  
  put("DataExactCardinality",            DATA_EXACT_CARDINALITY);
   
/* Class Expressions Axioms */
  put("SubClassOf",                      SUB_CLASS_OF);
  put("EquivalentClasses",               EQUIVALENT_CLASSES);
  put("DisjointClasses",                 DISJOINT_CLASSES);    
  put("DisjointUnion",                   DISJOINT_UNION);
    
/* Object Property Axioms */ 
  put("SubObjectPropertyOf",             SUB_OBJECT_PROPERTY_OF);
  put("EquivalentObjectProperties",      EQUIVALENT_OBJECT_PROPERTIES);
  put("DisjointObjectProperties",        DISJOINT_OBJECT_PROPERTIES);
  put("ObjectPropertyDomain",            OBJECT_PROPERTY_DOMAIN);
  put("ObjectPropertyRange",             OBJECT_PROPERTY_RANGE);
  put("InverseObjectProperties",         INVERSE_OBJECT_PROPERTIES); 
  put("FunctionalObjectProperty",        FUNCTIONAL_OBJECT_PROPERTY);
  put("InverseFunctionalObjectProperty", INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
  put("ReflexiveObjectProperty",         REFLEXIVE_OBJECT_PROPERTY);
  put("IrreflexiveObjectProperty",       IRREFLEXIVE_OBJECT_PROPERTY);
  put("SymmetricObjectProperty",         SYMMETRIC_OBJECT_PROPERTY);
  put("AsymmetricObjectProperty",        ASYMMETRIC_OBJECT_PROPERTY);
  put("TransitiveObjectProperty",        TRANSITIVE_OBJECT_PROPERTY);
   
/* Data Property Axioms */          
  put("SubDataPropertyOf",               SUB_DATA_PROPERTY_OF);          
  put("EquivalentDataProperties",        EQUIVALENT_DATA_PROPERTIES);   
  put("DisjointDataProperties",          DISJOINT_DATA_PROPERTIES);     
  put("DataPropertyDomain",              DATA_PROPERTY_DOMAIN);         
  put("DataPropertyRange",               DATA_PROPERTY_RANGE);                    
  put("FunctionalDataProperty",          FUNCTIONAL_DATA_PROPERTY);     
  put("DatatypeDefinition",              DATATYPE_DEFINITION);   
  
/* Keys */        
  put("HasKey",                          HAS_KEY);
    
/* Assertions */
  put("SameIndividual",                  SAME_INDIVIDUAL);
  put("DifferentIndividuals",            DIFFERENT_INDIVIDUALS);
  put("ClassAssertion",                  CLASS_ASSERTION);
  put("ObjectPropertyAssertion",         OBJECT_PROPERTY_ASSERTION);
  put("NegativeObjectPropertyAssertion", NEGATIVE_OBJECT_PROPERTY_ASSERTION);
  put("DataPropertyAssertion",           DATA_PROPERTY_ASSERTION);
  put("NegativeDataPropertyAssertion",   NEGATIVE_DATA_PROPERTY_ASSERTION);
   
/* Annotations */
  put("Annotation",                      ANNOTATION);
  put("AnnotationAssertion",             ANNOTATION_ASSERTION);
  put("SubAnnotationPropertyOf",         SUB_ANNOTATION_PROPERTY_OF);
  put("AnnotationPropertyDomain",        ANNOTATION_PROPERTY_DOMAIN);         
  put("AnnotationPropertyRange",         ANNOTATION_PROPERTY_RANGE);   
  
  }};  

 private int findReservedIri(String text) {    
    Integer type = reservedIriTable.get(text);
    if (type != null)
       return type;
    else
      return PNAME_LN;
  }

 private int findReservedKeyword(String text) {    
    Integer type = reservedKeywordTable.get(text);
    if (type != null)
       return type;
    else
      return KEYWORD;
  }
}


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WHITESPACE 
    : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+   { skip(); } 
    ;

KEYWORD
    : ('a'..'z'|'A'..'Z')+   { $type = findReservedKeyword($text); };

NON_NEGATIVE_INTEGER 
    : ('1'..'9') DIGIT*
    ;

fragment
DIGIT 
    : '0'..'9'
    ;    

REFERENCE
    : '^^'
    ;
    
OPEN_BRACE
    : '('
    ;

CLOSE_BRACE
    : ')'
    ;    
 
EQUALS
    : '='
    ; 

/* a finite sequence of characters in which " (U+22) and \ (U+5C) 
*  occur only in pairs of the form \" (U+5C, U+22) and \\ (U+5C, U+5C), 
*  enclosed in a pair of " (U+22) characters
*/
QUOTED_STRING 
    :  '\"' ( ~('\\'|'\"') 
            |  ('\\' ('\"'|'\''|'\\')) 
            )* 
       '\"'
    ;

IRI_REF 
    : '<' ( 
           ~( '<' 
            | '>' 
            | '"' 
            | '{' 
            | '}' 
            | '|' 
            | '^' 
            | '`' 
            | '\\'
            | '\u0000'..'\u0020'
            ) 
          )* '>'
     ;  /* see IRI_REF of [SPARQL] */

PNAME_NS 
    : PN_PREFIX? ':'
    ;


PNAME_LN 
    : PNAME_NS PN_LOCAL  { $type = findReservedIri($text); }      
    ;

BLANK_NODE_LABEL
    : '_:' PN_LOCAL
    ;

LANGTAG
    : '@' ('a'..'z'|'A'..'Z')+ ('-' ('a'..'z'|'A'..'Z'|DIGIT)+)*    
    ;

           
fragment    
PN_CHARS_U 
    : PN_CHARS_BASE | '_'
    ;

fragment    
PN_CHARS 
    : PN_CHARS_U 
    | '-' 
    | DIGIT 
    | '\u00B7' 
    | '\u0300'..'\u036F' 
    | '\u203F'..'\u2040'
    ;    
    
fragment
PN_PREFIX 
    : PN_CHARS_BASE ( ( PN_CHARS | '.' )* PN_CHARS )?
    ;
    
fragment
PN_LOCAL 
    : ( PN_CHARS_U | DIGIT ) ( ( PN_CHARS | '.' )* PN_CHARS)?
    ;    

fragment
PN_CHARS_BASE
    : 'a'..'z' 
    | 'A'..'Z' 
    | '\u00C0'..'\u00D6' 
    | '\u00D8'..'\u00F6' 
    | '\u00F8'..'\u02FF' 
    | '\u0370'..'\u037D' 
    | '\u037F'..'\u1FFF' 
    | '\u200C'..'\u200D' 
    | '\u2070'..'\u218F' 
    | '\u2C00'..'\u2FEF' 
    | '\u3001'..'\uD7FF' 
    | '\uF900'..'\uFDCF' 
    | '\uFDF0'..'\uFFFD' 
//    | '\u10000'..'\uEFFFF'   // not supported by java
    ;    
