/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.activiti.spring.test.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Assert;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test using spring-orm in spring-bean combined with JPA-variables in activiti.
 * 
 * @author Frederik Heremans
 */
@ContextConfiguration("classpath:org/activiti/spring/test/jpa/JPASpringTest-context.xml")
public class JPASpringTest extends SpringActivitiTestCase {

	@Deployment(resources = { "org/activiti/spring/test/jpa/JPASpringTest.bpmn20.xml" })
	public void testJpaVariableHappyPath() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("customerName", "John Doe");
		variables.put("amount", 15000L);

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("LoanRequestProcess", variables);

		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("manager").list();
		assertEquals(1, tasks.size());

		Task task = tasks.get(0);

		taskService.claim(task.getId(), "gonzo");
		tasks = taskService.createTaskQuery().taskAssignee("gonzo").list();

		assertEquals(1, tasks.size());
		taskService.complete(task.getId());

	    assertProcessEnded(processInstance.getId());
	}
}
