package com.devcru.arb.controllers;

import javax.sql.DataSource;


//import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devcru.arb.dao.Dao;
import com.devcru.arb.objects.Answer;
import com.devcru.arb.objects.AnswerWrapper;
import com.devcru.arb.objects.JsonResponse;
import com.devcru.arb.objects.Question;
import com.devcru.arb.objects.QuestionRequest;
import com.devcru.arb.objects.QuestionWrapper;

// XXX THEORY XXX:
// We could separate "questions" from "answers" to each their own controllers...
// but for now, this doesn't appear necessary until we need three tiers of descriptions

// TODO: Implement log4j instance and mess with it

@Controller
//@JsonIgnoreProperties(ignoreUnknown = true) // Doesn't seem to be necessary, but leaving in for now
@RequestMapping(value = "/*")
public class MainController {
	
	Dao dao;
	@Autowired
	public void setUserDao(Dao dao) { this.dao = dao; }

	protected JdbcTemplate template;
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource ds) { this.template = new JdbcTemplate(ds); }
	
	// QUESTIONS
	// XXX: Can create an endpoint that retrieves ALL questions via "/questions"
	
	@RequestMapping(value="/question", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getQuestion() {
		String event = "OK";
		Object data = "question GET success";
		
		Question question = new Question(0); // sample id
		QuestionWrapper questionWrapper = new QuestionWrapper();
		questionWrapper.setQuestion(question);
		
		data = questionWrapper;
		
		return new JsonResponse(event, data);
	}
	
	@RequestMapping(value="/question", method=RequestMethod.POST)
	// FIXME: headers="content-type=application/json" or produces="application/json"
	public @ResponseBody
	JsonResponse postQuestion(@RequestBody QuestionRequest questionRequest) {
		
		System.out.println("POST /question reached");
		
		String event = "OK";
		Object data = "You have posted: ";
		
		data = questionRequest;
		
		return new JsonResponse(event, "POST Request Body: " + data);
	}
	
	// ANSWERS
	// XXX: Can create an endpoint that retrieves ALL questions via "/answers"
	
	@RequestMapping(value="/answer", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getAnswer() {
		String event = "OK";
		String data = "answer GET success";
		
		return new JsonResponse(event, data);
	}
	
	@RequestMapping(value="/answer", method=RequestMethod.POST)
	public @ResponseBody
	JsonResponse getAnswer(@RequestBody AnswerWrapper answerWrapper) {
		String event = "OK";
		String data = "answer POST success";
		
		Answer answer = answerWrapper.getAnswer();
		
		return new JsonResponse(event, data);
	}
	
}
