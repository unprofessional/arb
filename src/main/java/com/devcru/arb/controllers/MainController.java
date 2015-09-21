package com.devcru.arb.controllers;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devcru.arb.dao.Dao;
import com.devcru.arb.objects.AskRequest;
import com.devcru.arb.objects.AskResponse;
import com.devcru.arb.objects.AnswerRequest;
import com.devcru.arb.objects.AnswerResponse;
import com.devcru.arb.objects.JsonResponse;
import com.devcru.arb.objects.Question;

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
	
	final static Logger logger = Logger.getLogger(MainController.class);
	
	// QUESTIONS
	// XXX: Can create an endpoint that retrieves ALL questions via "/questions"
	
//	@RequestMapping(value="/question", method=RequestMethod.GET)
//	public @ResponseBody
//	JsonResponse getQuestion() {
//		
//		logger.info("GET /question reached");
//		
//		String event = "success";
//		Object data = "";
//		
//		AskResponse askResponse = new AskResponse();
//		
//		data = askResponse;
//		
//		return new JsonResponse(event, data);
//	}
	
	@RequestMapping(value="/question/{latitude}/{longitude}", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getQuestionByCoords(@PathVariable("latitude") double latitude,
			@PathVariable("longitude") double longitude) {
		
		logger.info("GET /question (by coordinates) reached");
		
		String event = "success";
		Object data = "";
		
		AskResponse askResponse = new AskResponse();
		
		data = askResponse;
		
		return new JsonResponse(event, data);
	}
	
	@RequestMapping(value="/question", method=RequestMethod.POST)
	// FIXME: headers="content-type=application/json" or produces="application/json"
	public @ResponseBody
	JsonResponse postQuestion(@RequestBody AskRequest askRequest) {
		
		logger.info("POST /question reached");
		
		String event = "success";
		Object data = "";
		
		Question question = new Question(0); // This is where the id is assigned
		question.setText(askRequest.getText());
		question.setLatitude(askRequest.getLatitude());
		question.setLongitude(askRequest.getLongitude());
		
		// TODO: Store question
		
		AskResponse askResponse = new AskResponse();
		askResponse.setId(question.getId());
		
		data = askResponse;
		
		return new JsonResponse(event, data);
	}
	
	// ANSWERS
	// XXX: Can create an endpoint that retrieves ALL questions via "/answers"
	
	@RequestMapping(value="/answer/{qid}", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getAnswer(@PathVariable("qid") double qid) {
		
		logger.info("GET /answer (by qid) reached");
		
		String event = "success";
		Object data = "answer GET ";
		
		AnswerResponse answerResponse = new AnswerResponse();
		
		data = answerResponse;
		
		return new JsonResponse(event, data);
	}
	
	@RequestMapping(value="/answer", method=RequestMethod.POST)
	public @ResponseBody
	JsonResponse getAnswer(@RequestBody AnswerRequest answerRequest) {
		
		logger.info("POST /answer reached");
		
		String event = "success";
		Object data = "answer POST ";
		
		AnswerResponse answerResponse = new AnswerResponse();
		//answerResponse.doSomething();
		
		data = answerResponse;
		
		return new JsonResponse(event, data);
	}
	
}
