package org.semanticweb.elk.explanations.list;
/*
 * #%L
 * Explanation Workbench
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.border.Border;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListDeleteButton;
import org.protege.editor.core.ui.list.MListEditButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.framelist.ExplainButton;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListInferredSectionRowBorder;
import org.semanticweb.elk.explanations.AxiomSelectionModel;
import org.semanticweb.elk.explanations.WorkbenchManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlockingExpression;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
@SuppressWarnings("serial")
public class ProofFrameList extends OWLFrameList<CycleBlockingExpression> {
	
	private static final Border INFERRED_BORDER = new OWLFrameListInferredSectionRowBorder();
	
	private static final Set<AxiomType<?>> EDITABLE_AXIOM_TYPES = new HashSet<AxiomType<?>>(Arrays.<AxiomType<?>>asList(AxiomType.SUBCLASS_OF, AxiomType.OBJECT_PROPERTY_DOMAIN, AxiomType.OBJECT_PROPERTY_RANGE, AxiomType.EQUIVALENT_CLASSES));

    public ProofFrameList(OWLEditorKit editorKit, AxiomSelectionModel axiomSelectionModel, WorkbenchManager workbenchManager, ProofFrame proofFrame) {
        super(editorKit, proofFrame);
        
        setCellRenderer(new ProofFrameListRenderer(editorKit));
    }
    
    @Override
	public ProofFrame getFrame() {
		return (ProofFrame) super.getFrame();
	}

	@Override
    protected List<MListButton> getButtons(final Object value) {
        if (value instanceof ProofFrameSectionRow) {
        	ProofFrameSectionRow row = (ProofFrameSectionRow) value;
        	
			if (row.isInferred()) {
				if (!row.isExpanded()) {
					// explain button for inferred expressions
					return Arrays.<MListButton> asList(new ExplainButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										explainRow((ProofFrameSectionRow) value);
									}
								}));
				}
				else {
					return Arrays.<MListButton> asList(new CollapseButton(
							new AbstractAction() {

								public void actionPerformed(ActionEvent e) {
									collapseRow((ProofFrameSectionRow) value);
								}
							}));
				}
			}
			else if (row.getAxiom().isOfType(EDITABLE_AXIOM_TYPES)) {
				// asserted axiom, can edit or delete it
				return Arrays.<MListButton> asList(
						new MListEditButton(new AbstractAction() {

							public void actionPerformed(ActionEvent e) {
								editRow((ProofFrameSectionRow) value);
							}
						}),
						new MListDeleteButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										deleteRow((ProofFrameSectionRow) value);
									}
								})
						);
			}
        }

        return Collections.emptyList();
    }
    
	protected void editRow(ProofFrameSectionRow expressionRow) {
    	//TODO show editor
		//blockInferencesForPremise(expressionRow.getRootObject());
    }
    
    protected void deleteRow(ProofFrameSectionRow expressionRow) {
    	//TODO
    	blockInferencesForPremise(expressionRow.getRoot());
    }
    
    private void blockInferencesForPremise(OWLExpression premise) {
    	getFrame().blockInferencesForPremise(premise);
    	refreshComponent();
    }
    
    protected void collapseRow(ProofFrameSectionRow expressionRow) {
    	expressionRow.setExpanded(false);
    	refreshComponent();
    }

	protected void explainRow(ProofFrameSectionRow expressionRow) {
		expressionRow.setExpanded(true);
		
		if (!expressionRow.isFilled()) {
			expressionRow.refillInferenceSections();
		}
		
		refreshComponent();
	}

	@Override
	protected Border createPaddingBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE);
    }
	
	@Override
    protected Border createListItemBorder(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// creating indents to reflect the proof's structure
		if (value instanceof ProofFrameSectionRow) {
			ProofFrameSectionRow row = (ProofFrameSectionRow) value;
			
			Border internalPadding = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220));
			Border externalBorder = BorderFactory.createMatteBorder(0, 20 * row.getDepth(), 0, 0, list.getBackground());
			Border border = BorderFactory.createCompoundBorder(externalBorder, BorderFactory.createCompoundBorder(row.isInferred() ? INFERRED_BORDER : line, internalPadding));
			
			return border;
		}
		
		return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }

	
	@Override
	public ListModel<?> getModel() {
		return new FlattenedListModel((ProofFrameSection) this.getFrame().getFrameSections().get(0));
	}

	@Override
	protected Color getItemBackgroundColor(MListItem item) {
        if (item instanceof ProofFrameSectionRow) {
            if (((ProofFrameSectionRow) item).isInferred()) {
                return INFERRED_BG_COLOR;
            }
        }
        
        return super.getItemBackgroundColor(item);
    }
	
	// TODO cache the list of items for better performance, e.g., constant time lookup by index
	private static class FlattenedListModel extends AbstractListModel<Object> {

		private final ProofFrameSection header_;
		
		FlattenedListModel(ProofFrameSection header) {
			header_ = header;
		}
		
		@Override
		public Object getElementAt(int arg0) {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				Object item = iter.next();
				
				if (i == arg0) {
					return item;
				}
				
				i++;
			}
			
			return null;
		}

		@Override
		public int getSize() {
			int i = 0;
			Iterator<?> iter = iterate();
			
			while (iter.hasNext()) {
				iter.next();
				i++;
			}
			
			return i;
		}
		
		private Iterator<?> iterate() {
			final LinkedList<Object> todo = new LinkedList<Object>();
			
			todo.add(header_);
			
			return new Iterator<Object>() {
				
				private Object next_ = null;

				@Override
				public boolean hasNext() {
					if (next_ != null) {
						return true;
					}

					next_ = todo.poll();
					
					if (next_ == null) {
						return false;
					}
					
					if (next_ instanceof ProofFrameSection) {
						ProofFrameSection section = (ProofFrameSection) next_;
						int i = 0;
						
						for (Object row : section.getRows()) {
							todo.add(i++, row);
						}
					}
					else if (next_ instanceof ProofFrameSectionRow) {
						ProofFrameSectionRow row = (ProofFrameSectionRow) next_;
						int i = 0;
						
						if (row.isExpanded()) {
							for (Object section : row.getInferenceSections()) {
								todo.add(i++, section);
							}
						}
					}
					
					return true;
				}

				@Override
				public Object next() {
					Object tmp = next_;
					
					next_ = null;
					
					return tmp;
				}

				@Override
				public void remove() {
					// no-op
				}
				
			};
		}
		
	}

}
