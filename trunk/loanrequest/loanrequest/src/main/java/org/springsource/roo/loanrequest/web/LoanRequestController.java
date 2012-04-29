package org.springsource.roo.loanrequest.web;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import org.springsource.roo.loanrequest.server.domain.LoanRequest;

@RequestMapping("/loanrequests")
@Controller
@RooWebScaffold(path = "loanrequests", formBackingObject = LoanRequest.class)
public class LoanRequestController {

	private TaskService taskService;

	private static Log logger = LogFactory.getLog(LoanRequestController.class);

	private static final String submitterRole = "accountancy";
	private static final String approverRole = "management";
	private static final String submitTaskName = "Submit loan request";
	private static final String approveTaskName = "Verify loan request";

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid LoanRequest loanRequest, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, loanRequest);
			return "loanrequests/create";
		}
		uiModel.asMap().clear();
		loanRequest.setProcessId(startProcess(loanRequest, httpServletRequest));
		loanRequest.persist();

		return "redirect:/loanrequests/" + encodeUrlPathSegment(loanRequest.getId().toString(), httpServletRequest);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(Model uiModel) {
		populateEditForm(uiModel, new LoanRequest());
		return "loanrequests/create";
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("loanrequest", LoanRequest.findLoanRequest(id));
		uiModel.addAttribute("itemId", id);
		return "loanrequests/show";
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("loanrequests", LoanRequest.findLoanRequestEntries(firstResult, sizeNo));
			float nrOfPages = (float) LoanRequest.countLoanRequests() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("loanrequests", LoanRequest.findAllLoanRequests());
		}
		return "loanrequests/list";
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@Valid LoanRequest loanRequest, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, loanRequest);
			return "loanrequests/update";
		}
		uiModel.asMap().clear();
		loanRequest.merge();

		approveProcess(loanRequest, httpServletRequest);
		return "redirect:/loanrequests/" + encodeUrlPathSegment(loanRequest.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		populateEditForm(uiModel, LoanRequest.findLoanRequest(id));
		return "loanrequests/update";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		LoanRequest loanRequest = LoanRequest.findLoanRequest(id);
		loanRequest.remove();
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
		return "redirect:/loanrequests";
	}

	void populateEditForm(Model uiModel, LoanRequest loanRequest) {
		uiModel.addAttribute("loanRequest", loanRequest);
	}

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
		String enc = httpServletRequest.getCharacterEncoding();
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		try {
			pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
		} catch (UnsupportedEncodingException uee) {
		}
		return pathSegment;
	}

	private String startProcess(LoanRequest loanRequest, HttpServletRequest request) {
		if (request.isUserInRole(submitterRole)) {
			logger.debug("in the startProcess ");
			// Get Activiti services
			// Create Activiti process engine
			ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration().buildProcessEngine();

			RuntimeService runtimeService = processEngine.getRuntimeService();

			taskService = processEngine.getTaskService();

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("loanProcess");
			logger.debug("startProcess processInstance Id=" + processInstance.getId());

			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(user.getUsername()).list();
			logger.debug("startProcess task size=" + tasks.size());
			for (Task task : tasks) {
				logger.debug("Task Name = " + task.getName());
				if (task.getName().equals(submitTaskName)) {
					logger.debug("startProcess before claim = " + task.getName());
					taskService.claim(task.getId(), user.getUsername());
					List<Task> tasks1 = taskService.createTaskQuery().taskAssignee(user.getUsername()).list();

					for (Task task1 : tasks1) {
						if (task1.getName().equals(approveTaskName)) {
							logger.debug("startProcess before complete = " + task1.getName());
							taskService.complete(task1.getId());
							return processInstance.getId();
						}
					}
				}
			}
		}
		return "";
	}

	private void approveProcess(LoanRequest loanRequest, HttpServletRequest request) {
		// TODO Auto-generated method stub

		logger.debug("in the approveProcess ");
		if (!loanRequest.getProcessId().isEmpty() && request.isUserInRole(approverRole)) {
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(approverRole).list();
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for (Task task : tasks) {
				logger.debug("approveProcess Task Name = " + task.getName());
				if (task.getName().equals(approveTaskName)) {

					logger.debug("approveProcess before claim = " + task.getName());
					taskService.claim(task.getId(), user.getUsername());
					List<Task> tasks1 = taskService.createTaskQuery().taskAssignee(user.getUsername()).list();

					for (Task task1 : tasks1) {
						if (task1.getName().equals(approveTaskName)) {
							logger.debug("approveProcess before complete = " + task1.getName());
							taskService.complete(task1.getId());
						}
					}
				}
			}
		}
	}
}
