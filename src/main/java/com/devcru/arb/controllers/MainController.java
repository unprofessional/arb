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
import com.devcru.arb.geostorage.QuestionStorage;
import com.devcru.arb.objects.AskRequest;
import com.devcru.arb.objects.AskResponse;
import com.devcru.arb.objects.AnswerRequest;
import com.devcru.arb.objects.AnswerResponse;
import com.devcru.arb.objects.JSONTTempQWrapper;
import com.devcru.arb.objects.JsonResponse;
import com.devcru.arb.objects.Question;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	
	double searchRadius = 10000.0;
	
	// QUESTIONS
	// XXX: Can create an endpoint that retrieves ALL questions via "/questions"
	
//	@RequestMapping(value="/question", method=RequestMethod.GET)
//	public @ResponseBody
//	JsonResponse getQuestion() {
//		
//		logger.info("GET /question reached");
//		
//		String status = "success";
//		Object data = "";
//		
//		AskResponse askResponse = new AskResponse();
//		
//		data = askResponse;
//		
//		return new JsonResponse(status, data);
//	}
	
	@RequestMapping(value="/question/{latitude}/{longitude}", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getQuestionByCoords(
			@PathVariable("latitude") double latitude,
			@PathVariable("longitude") double longitude) {
		
		logger.info("GET /question (by coordinates) reached");
		
		QuestionStorage qs = QuestionStorage.getInstance();
		QuestionStorage.Question q = (QuestionStorage.Question) qs.findRandom(
			latitude,
			longitude,
			searchRadius);
		
		if (q == null) {
			String status = "not_found";
			
			Object jsonQ = new Object() {
				@JsonProperty("message")
				String message = "No questions found nearby.";
			};
			
			return new JsonResponse(status, jsonQ);
		}
		else {
			String status = "success";
			
			//JSONTTempQWrapper jsonQ = new JSONTTempQWrapper();
			Question qWrapper = new Question(q.getKey());
			qWrapper.setLatitude(q.getLatitude());
			qWrapper.setLongitude(q.getLongitude());
			qWrapper.setText(q.getText());
			//jsonQ.setQuestion(q);
			
			return new JsonResponse(status, qWrapper);
		}
	}
	
	@RequestMapping(value="/question", method=RequestMethod.POST)
	// FIXME: headers="content-type=application/json" or produces="application/json"
	public @ResponseBody
	JsonResponse postQuestion(@RequestBody AskRequest askRequest) {
		
		logger.info("POST /question reached");
		
		String status = "success";
		Object data = "";
		
		String text = askRequest.getText();
		double latitude = askRequest.getLatitude();
		double longitude = askRequest.getLongitude();

		QuestionStorage qs = QuestionStorage.getInstance();
		int key = qs.putNext(qs.new Question(text, latitude, longitude));

		Question question = new Question(key); // This is where the id is assigned
		question.setText(text);
		question.setLatitude(latitude);
		question.setLongitude(longitude);
		
		AskResponse askResponse = new AskResponse();
		askResponse.setId(question.getId());
		
		data = askResponse;
		
		return new JsonResponse(status, data);
	}
	
	// ANSWERS
	// XXX: Can create an endpoint that retrieves ALL questions via "/answers"
	
	@RequestMapping(value="/answer/{qid}", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse getAnswer(@PathVariable("qid") long qid) {
		
		logger.info("GET /answer (by qid) reached");

		String status;
		Object data;
		
		QuestionStorage qs = QuestionStorage.getInstance();
		QuestionStorage.Question q = qs.get((int)qid);
		
		if (q != null) {
			String answer = q.getAnswer();
			if (answer != null) {
				status = "success";
				data = new AnswerResponse(answer);
			}
			else {
				status = "not_answered";
				data = new Object() {
					@JsonProperty("message")
					String message = "This question has not been answered.";
				};
			}
		}
		else {
			status = "not_found";
			data = new Object() {
				@JsonProperty("message")
				String message = "Question not found.";
			};
		}
		
		return new JsonResponse(status, data);
	}
	
	@RequestMapping(value="/answer", method=RequestMethod.POST)
	public @ResponseBody
	JsonResponse postAnswer(@RequestBody AnswerRequest answerRequest) {
		
		logger.info("POST /answer reached");
		
		String status;
		Object data;
		
		QuestionStorage qs = QuestionStorage.getInstance();
		QuestionStorage.Question q = qs.get((int)answerRequest.getQuestionId());
		
		if (q != null) {
			if (q.getAnswer() == null) {
				q.setAnswer(answerRequest.getText());
				
				status = "success";
				data = new Object() {
					@JsonProperty("message")
					String message = "Question answered successfully.";
				};
			}
			else {
				status = "already_answered";
				data = new Object() {
					@JsonProperty("message")
					String message = "This question has already been answered.";
				};
			}
		}
		else {
			status = "not_found";
			data = new Object() {
				@JsonProperty("message")
				String message = "Question not found.";
			};
		}
		
		return new JsonResponse(status, data);
	}
	
}
