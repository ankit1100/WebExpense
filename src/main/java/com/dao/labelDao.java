package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.CategoryBean;
import com.bean.LabelBean;
import com.bean.LoginBean;
import com.bean.PayeeBean;

@Repository
public class labelDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
		
	public LabelBean createLabels(LabelBean label, LoginBean user) {
		int uId = user.getUserId();
		System.out.println("USerId from lables table => "+uId);
		jdbcTemplate.update("insert into labels(labelName, userId) values (?, ?)",label.getLabelName(),uId);
		label = jdbcTemplate.queryForObject("select labelId from labels where labelName=? and userId=?", new BeanPropertyRowMapper<LabelBean>(LabelBean.class),label.getLabelName(),uId);
		return label;
	}
	public List<LabelBean> getAllLabels(LoginBean loginuser){
		List<LabelBean> list = jdbcTemplate.query("select * from labels where userId = ?", new BeanPropertyRowMapper<LabelBean>(LabelBean.class),loginuser.getUserId());
		return list;
	}

}
