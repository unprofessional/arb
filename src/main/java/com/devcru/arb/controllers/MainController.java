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
import com.devcru.arb.geostorage.GeoStorage;
import com.devcru.arb.geostorage.QuestionStorage;
import com.devcru.arb.objects.AskRequest;
import com.devcru.arb.objects.AskResponse;
import com.devcru.arb.objects.AnswerRequest;
import com.devcru.arb.objects.AnswerResponse;
import com.devcru.arb.objects.JsonResponse;
import com.devcru.arb.objects.QuestionResponse;
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
	
	double searchRadius = 20.0;
	
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
			
			final double dist = GeoStorage.getGeoDistance(
				q.getLatitude(), q.getLongitude(),
				latitude,		 longitude);
			QuestionResponse qWrapper = new QuestionResponse(q.getKey(), q.getText(), dist);
			
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
		QuestionStorage.Question q = qs.putNext(qs.new Question(text, latitude, longitude));
		
		AskResponse askResponse = new AskResponse();
		askResponse.setId(q.getKey());
		
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
		QuestionStorage.Question q = qs.get(qid);
		
		logger.info("URL qid: " + qid);
		
		if (q != null) {
			QuestionStorage.Answer answer = q.getAnswer();
			if (answer != null) {
				status = "success";

				final double dist = GeoStorage.getGeoDistance(
						 q.getLatitude(),	   q.getLongitude(),
					answer.getLatitude(), answer.getLongitude());
				data = new AnswerResponse(answer.getText(), dist);
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
		QuestionStorage.Question q = qs.get(answerRequest.getQuestionId());
		
		if (q != null) {
			if (q.getAnswer() == null) {
				q.setAnswer(qs.new Answer(
					answerRequest.getText(),
					answerRequest.getLatitude(),
					answerRequest.getLongitude()
				));
				
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

	@RequestMapping(value="/qs/{operation}", method=RequestMethod.GET)
	public @ResponseBody
	JsonResponse qsControl(@PathVariable String operation) {
		
		logger.info("GET /qs reached");
		
		QuestionStorage qs = QuestionStorage.getInstance();
		
		if ("size".equals(operation)) {
			return new JsonResponse("success", "[QS]"
				+"\n Data Size: "+qs.getData().size()
				+"\n Quadtree Root Size: "+qs.getQuadtree().getRoot().getNumDataPoints()
				+"\n Quadtree Root Points: "+qs.getQuadtree().getRoot().getDataPoints().size()
			);
		}
		else if ("clear".equals(operation)) {
			qs.clear();
			return new JsonResponse("success", "[QS] Cleared.");
		}
		else if ("write".equals(operation)) {
			qs.writeToFile();
			return new JsonResponse("success", "[QS] Written to file. Size: "+qs.getData().size());
		}
		else if ("read".equals(operation)) {
			qs.readFromFile();
			return new JsonResponse("success", "[QS] Read from file. Size: "+qs.getData().size());
		}
		else {
			return new JsonResponse("error", "Must follow format: /qs/{operation = read|write|clear|...}");
		}
	}
}
