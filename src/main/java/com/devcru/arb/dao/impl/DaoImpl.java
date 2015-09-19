package com.devcru.arb.dao.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.devcru.arb.dao.Dao;

public class DaoImpl implements Dao {
	
	protected JdbcTemplate template;
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource ds) { this.template = new JdbcTemplate(ds); }
	
	@Override
	public boolean getQuestion() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean insertQuestion() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean getAnswer() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean insertAnswer() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
