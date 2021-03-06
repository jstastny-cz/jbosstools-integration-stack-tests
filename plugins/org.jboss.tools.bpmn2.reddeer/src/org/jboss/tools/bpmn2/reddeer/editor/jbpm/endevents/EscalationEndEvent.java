package org.jboss.tools.bpmn2.reddeer.editor.jbpm.endevents;

import org.jboss.tools.bpmn2.reddeer.editor.ElementType;
import org.jboss.tools.bpmn2.reddeer.editor.jbpm.Escalation;
import org.jboss.tools.bpmn2.reddeer.editor.jbpm.eventdefinitions.EscalationEventDefinition;
import org.jboss.tools.bpmn2.reddeer.properties.setup.EventDefinitionSetUp;

/**
 * 
 */
public class EscalationEndEvent extends EndEvent {
	
	/**
	 * 
	 * @param name
	 */
	public EscalationEndEvent(String name) {
		super(name, ElementType.ESCALATION_END_EVENT);
	}

	/**
	 * 
	 * @param escalation
	 */
	public void setEscalation(Escalation escalation, String variableForMapping) {
		propertiesHandler.setUp(new EventDefinitionSetUp(new EscalationEventDefinition(escalation, variableForMapping, "Source")));
		refresh();
	}
	
}
