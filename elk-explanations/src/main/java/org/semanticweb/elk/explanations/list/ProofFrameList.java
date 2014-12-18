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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.framelist.ExplainButton;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListInferredSectionRowBorder;
import org.semanticweb.elk.explanations.AxiomSelectionModel;
import org.semanticweb.elk.explanations.WorkbenchManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
@SuppressWarnings("serial")
public class ProofFrameList extends OWLFrameList<OWLExpression> {
	
	private static final Logger LOGGER_ = Logger.getLogger(ProofFrameList.class);

	private static final Border INFERRED_BORDER = new OWLFrameListInferredSectionRowBorder();
	
    public static final Color SINGLE_POPULARITY_COLOR = new Color(170, 70, 15);

    public static final Color MULTI_POPULARITY_COLOR = new Color(10, 75, 175);

    public static final Color ALL_POPULARITY_COLOR = new Color(6, 133, 19);

    private OWLEditorKit editorKit;

    private int buttonRunWidth = 0;

    private AxiomSelectionModel axiomSelectionModel;
    
    private WorkbenchManager workbenchManager;

    public ProofFrameList(OWLEditorKit editorKit, AxiomSelectionModel axiomSelectionModel, WorkbenchManager workbenchManager, ProofFrame proofFrame) {
        super(editorKit, proofFrame);
        
        this.workbenchManager = workbenchManager;
        this.axiomSelectionModel = axiomSelectionModel;
        this.editorKit = editorKit;
        
        setCellRenderer(new ProofFrameListRenderer(editorKit));
        
        /*Action moveUpAction = new AbstractAction("Move up") {
            public void actionPerformed(ActionEvent e) {
                handleMoveUp();
            }
        };
        getActionMap().put(moveUpAction.getValue(Action.NAME), moveUpAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK), moveUpAction.getValue(Action.NAME));


        Action moveDownAction = new AbstractAction("Move down") {
            public void actionPerformed(ActionEvent e) {
                handleMoveDown();
            }
        };
        getActionMap().put(moveDownAction.getValue(Action.NAME), moveDownAction);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK), moveDownAction.getValue(Action.NAME));


        Action increaseIndentation = new AbstractAction("Increase indentation") {
            public void actionPerformed(ActionEvent e) {
                handleIncreaseIndentation();
            }
        };
        getActionMap().put(increaseIndentation.getValue(Action.NAME), increaseIndentation);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK), increaseIndentation.getValue(Action.NAME));


        Action decreaseIndentation = new AbstractAction("decrease indentation") {
            public void actionPerformed(ActionEvent e) {
                handleDecreaseIndentation();
            }
        };
        getActionMap().put(decreaseIndentation.getValue(Action.NAME), decreaseIndentation);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK), decreaseIndentation.getValue(Action.NAME));*/



    }
    
    
/*    private void handleMoveUp() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        int newIndex = formattingManager.moveAxiomUp(getRootObject(), selectedAxiom);
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(newIndex + 1);
    }
    
    

    private void handleMoveDown() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        int newIndex = formattingManager.moveAxiomDown(getRootObject(), selectedAxiom);
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(newIndex + 1);
    }
    
    
    private void handleIncreaseIndentation() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        formattingManager.increaseIndentation(getRootObject(), selectedAxiom);
        int selIndex = getSelectedIndex();
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(selIndex);
    }

    private void handleDecreaseIndentation() {
        OWLAxiom selectedAxiom = getSelectedAxiom();
        if(selectedAxiom == null) {
            return;
        }
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        formattingManager.decreaseIndentation(getRootObject(), selectedAxiom);
        int selIndex = getSelectedIndex();
        getFrame().setRootObject(getRootObject());
        setSelectedIndex(selIndex);
    }




    private OWLAxiom getSelectedAxiom() {
        int selectedIndex = getSelectedIndex();
        if(selectedIndex == -1) {
            return null;
        }
        Object element = getModel().getElementAt(selectedIndex);
        if(!(element instanceof JustificationFrameSectionRow)) {
            return null;
        }
        return ((JustificationFrameSectionRow) element).getAxiom();
    } */

    @Override
    protected List<MListButton> getButtons(final Object value) {
        if (value instanceof ProofFrameSectionRow) {
        	ProofFrameSectionRow row = (ProofFrameSectionRow) value;
        	
			if (row.isInferred()) {
				// explain button for inferred expressions
				List<MListButton> buttons = Arrays.<MListButton> asList(new ExplainButton(
								new AbstractAction() {

									public void actionPerformed(ActionEvent e) {
										invokeExplanationHandler((ProofFrameSectionRow) value);
									}
								}));
				buttonRunWidth = buttons.size() * (getButtonDimension() + 2) + 20;
				
				return buttons;
			}
			else {
				// TODO edit and delete buttons here
			}
        }

        return Collections.emptyList();
    }

	protected void invokeExplanationHandler(ProofFrameSectionRow expressionRow) {
		OWLExpression conclusion = expressionRow.getRootObject();
		ProofFrame frame = (ProofFrame) getFrame();
		ProofFrameSection expressionSection = (ProofFrameSection) expressionRow.getFrameSection();
		int rowDepth = expressionRow.getDepth() + 1;
		int sectionIndex = frame.indexOf(expressionSection);
		// removing the current section since it's going to be split on two
		frame.removeSection(sectionIndex);
		
		List<OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>> rows = expressionSection.getRows();
		// first part consists of all rows before (and including) the explained expression
		List<OWLExpression> firstExpressions = new ArrayList<OWLExpression>();
		int i = 0;
		
		for (; i < rows.size();) {
			OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom> row = rows.get(i++);
			
			firstExpressions.add(row.getRoot());
			
			if (row == expressionRow) {
				break;
			}
		}
		
		// second part consists of all rows after the explained expression
		List<OWLExpression> secondExpressions = new ArrayList<OWLExpression>();

		for (; i < rows.size(); i++) {
			secondExpressions.add(rows.get(i).getRoot());
		}
		
		ProofFrameSection firstSection = new ProofFrameSection(editorKit, frame, firstExpressions, expressionSection.getLabel(), expressionRow.getDepth());
		
		firstSection.refill(null);
		frame.addSection(firstSection, sectionIndex);
		// add sections corresponding to inferences
		int offset = 1;
		
		try {
			for (OWLInference inference : conclusion.getInferences()) {
				ProofFrameSection inferenceSection = new ProofFrameSection(editorKit, frame, inference.getPremises(), inference.getName(), rowDepth);
				
				inferenceSection.refill(null);
				frame.addSection(inferenceSection, sectionIndex + (offset++));
			}
		} catch (ProofGenerationException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
		
		if (!secondExpressions.isEmpty()) {
			ProofFrameSection secondSection = new ProofFrameSection(editorKit, frame, secondExpressions, "", expressionRow.getDepth());
			
			secondSection.refill(null);
			frame.addSection(secondSection, sectionIndex + offset);
		}
		
		refreshComponent();
	}

	@Override
	protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE);
    }
	
	@Override
    protected Border createListItemBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// creating indents to reflect the proof's structure
		if (value instanceof ProofFrameSectionRow) {
			ProofFrameSectionRow row = (ProofFrameSectionRow) value;
			
			Border internalPadding = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220));
			Border externalBorder = BorderFactory.createMatteBorder(0, 20 * row.getDepth(), 0, 0, list.getBackground());
			Border border = BorderFactory.createCompoundBorder(externalBorder, BorderFactory.createCompoundBorder(row.isInferred() ? INFERRED_BORDER : line, internalPadding));
			
/*			if (row.isInferred()) {
                border = BorderFactory.createCompoundBorder(border, INFERRED_BORDER);
            }*/
			return border;
		}
		
		return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }

	
	@Override
	public ListModel<?> getModel() {
		final ListModel<?> model = super.getModel();
		// filtering out sections with empty labels
		return new ListModel<Object>() {
			
			@Override
			public void addListDataListener(ListDataListener l) {
				model.addListDataListener(l);
			}
			
			private boolean isFiltered(Object element) {
				if (element instanceof ProofFrameSection) {
					ProofFrameSection section = (ProofFrameSection) element;
					
					return section.getLabel() == null || section.getLabel().length() == 0;
				}
				
				return false;
			}

			@Override
			public Object getElementAt(int index) {
				for (int i = index; i < model.getSize(); i++) {
					Object element = model.getElementAt(i);
					
					if (!isFiltered(element)) {
						return element;
					}
				}
				
				return null;
			}

			@Override
			public int getSize() {
				int size = 0;
				
				for (int i = 0; i < model.getSize(); i++) {
					Object element = model.getElementAt(i);
					
					if (!isFiltered(element)) {
						size++;
					}
				}
				
				return size;
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				model.removeListDataListener(l);
			}
			
		};
	}

	
	
    /*
    @Override
    public void addToPopupMenu(OWLFrameListPopupMenuAction<Explanation<OWLAxiom>> explanationOWLFrameListPopupMenuAction) {
        // NO MENU FOR US
    }

    @Override
    protected List<MListButton> getListItemButtons(MListItem item) {
        return Collections.emptyList();
    }

    @Override
    protected Border createListItemBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.createListItemBorder(list, value, index, isSelected, cellHasFocus);
    }


    @Override
    protected Border createPaddingBorder(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.createPaddingBorder(list, value, index, isSelected, cellHasFocus);
    }

*/

}
