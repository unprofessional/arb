package com.devcru.arb.objects;

import com.devcru.arb.geostorage.QuestionStorage;

public class JSONTTempQWrapper {
	
	QuestionStorage.Question question = null;

	public QuestionStorage.Question getQuestion() {
		return question;
	}

	public void setQuestion(QuestionStorage.Question question) {
		this.question = question;
	}

}
