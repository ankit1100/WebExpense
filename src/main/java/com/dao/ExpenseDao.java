package com.dao;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.AccountBean;
import com.bean.CategoryBean;
import com.bean.Expense;
import com.bean.LabelBean;
import com.bean.LoginBean;
import com.bean.PayeeBean;

@Repository
public class ExpenseDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean createExpense(Expense ex, CategoryBean category, PayeeBean payee, LabelBean label, LoginBean loginuser, AccountBean account) {
		
		category = jdbcTemplate.queryForObject("select categoryId from category where categoryName=? and userId = ?", new BeanPropertyRowMapper<CategoryBean>(CategoryBean.class),category.getCategoryName(),loginuser.getUserId());
		int cat_Id = category.getCategoryId();
		
		payee = jdbcTemplate.queryForObject("select payeeId from payees where payeeName = ? and userId = ?", new BeanPropertyRowMapper<PayeeBean>(PayeeBean.class), payee.getPayeeName(),loginuser.getUserId());
		int p_Id = payee.getPayeeId();
		
		label = jdbcTemplate.queryForObject("select labelId from labels where labelName = ? and userId = ?", new BeanPropertyRowMapper<LabelBean>(LabelBean.class), label.getLabelName(),loginuser.getUserId());
		int l_Id = label.getLabelId();
		
		account = jdbcTemplate.queryForObject("select accountBalance from account where accountName = ? and userId = ?", new BeanPropertyRowMapper<AccountBean>(AccountBean.class),ex.getAccName(),loginuser.getUserId());
		float acc_balance = account.getAccountBalance();
		
		int u_Id = loginuser.getUserId();
		
		
		int result1 = jdbcTemplate.update("insert into expense (amount, ExpDateTime, status, description, accName, paymentMethod, input_subCategory, catId, pId, lId, userId) values (?,?,?,?,?,?,?,?,?,?,?)",ex.getAmount(),ex.getExpDateTime(),ex.getStatus(),ex.getDescription(),ex.getAccName(),ex.getPaymentMethod(),ex.getInput_subCategory(),cat_Id,p_Id,l_Id,u_Id);
		if(result1 == 1) {
			float e_amount = ex.getAmount();
			System.out.println("Account Balance => "+acc_balance+", Expense Amount => "+e_amount);
			acc_balance = acc_balance - e_amount;
			System.out.println("Account Balance => "+acc_balance+", Expense Amount => "+e_amount);
			int result2 = jdbcTemplate.update("update account set accountBalance = ? where accountName = ? and userId = ?",acc_balance,ex.getAccName(),u_Id);
			if(result2 == 1) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
}
