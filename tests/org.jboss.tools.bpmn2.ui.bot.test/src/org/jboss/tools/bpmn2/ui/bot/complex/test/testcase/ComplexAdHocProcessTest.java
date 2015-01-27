package org.jboss.tools.bpmn2.ui.bot.complex.test.testcase;

import java.util.Arrays;

import org.jboss.tools.bpmn2.reddeer.editor.ElementType;
import org.jboss.tools.bpmn2.reddeer.editor.Position;
import org.jboss.tools.bpmn2.reddeer.editor.jbpm.Process;
import org.jboss.tools.bpmn2.reddeer.editor.jbpm.activities.ScriptTask;
import org.jboss.tools.bpmn2.ui.bot.complex.test.JBPM6ComplexTest;
import org.jboss.tools.bpmn2.ui.bot.complex.test.TestPhase;
import org.jboss.tools.bpmn2.ui.bot.complex.test.JBPM6ComplexTestDefinitionRequirement.JBPM6ComplexTestDefinition;
import org.jboss.tools.bpmn2.ui.bot.complex.test.TestPhase.Phase;
import org.jboss.tools.bpmn2.ui.bot.test.jbpm.JbpmAssertions;
import org.jboss.tools.bpmn2.ui.bot.test.jbpm.PersistenceWorkItemHandler;
import org.jboss.tools.bpmn2.ui.bot.test.jbpm.TriggeredNodesListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

@JBPM6ComplexTestDefinition(projectName="JBPM6ComplexTest",
							importFolder="resources/bpmn2/model/base",
							openFile="BaseBPMN2-AdHocProcess.bpmn2",
							saveAs="BPMN2-AdHocProcess.bpmn2")
public class ComplexAdHocProcessTest extends JBPM6ComplexTest {

	@TestPhase(phase=Phase.MODEL)
	public void model() {
		Process process = new Process("BPMN2-AdHocProcess");
		process.setAddHoc(true);
		
		// Finish parallel activities
		process.add("Task 2", ElementType.SCRIPT_TASK);
		
		ScriptTask task2 = new ScriptTask("Task 2");
		task2.setScript("", "System.out.println(\"Task2\");");
		
		process.add("Task 1", ElementType.SCRIPT_TASK, task2, Position.NORTH);
		
		ScriptTask task1 = new ScriptTask("Task 1");
		task1.setScript("", "System.out.println(\"Task1\");");
		task1.select();
	}
	
	@TestPhase(phase=Phase.RUN)
	public void run(KieSession kSession) {
		PersistenceWorkItemHandler handler = new PersistenceWorkItemHandler();
		kSession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		
		TriggeredNodesListener triggered = new TriggeredNodesListener(
				Arrays.asList("StartProcess", "Task 1", "Task 2", "Task 3", "User", "Gateway", "End"), 
				Arrays.asList("ScriptEnd", "Task 4"));
		kSession.addEventListener(triggered);
		
		org.jbpm.bpmn2.objects.Person person = new org.jbpm.bpmn2.objects.Person();
		person.setName("john");
		kSession.insert(person);
		
		ProcessInstance processInstance = kSession.startProcess("BPMN2AdHocProcess");
		
		kSession.signalEvent("Task 1", null, processInstance.getId());
		kSession.signalEvent("Task 2", null, processInstance.getId());
		
		WorkItem item = handler.getWorkItem("User");
		handler.completeWorkItem(item, kSession.getWorkItemManager());
		
		JbpmAssertions.assertProcessInstanceCompleted(processInstance, kSession);
	}
}
