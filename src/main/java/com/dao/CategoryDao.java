package com.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.CategoryBean;
import com.bean.LoginBean;
import com.bean.SubcategoryBean;

@Repository
public class CategoryDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public CategoryBean createCategory(CategoryBean category, LoginBean user) throws Exception{
//		KeyHolder keyHolder = new GeneratedKeyHolder();
//		int result = jdbcTemplate.update(new PreparedStatementCreator() {
//			
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement ps = con.prepareStatement("insert into category (categoryName,userId) values (?,?)",new String[]{"categoryId"});
//				ps.setString(1, category.getCategoryName());
//				ps.setInt(2, user.getUserId());
//				return ps;
//			}
//		},keyHolder);
//			int catId = (Integer)keyHolder.getKey();
				System.out.println("USer ID From Category Dao => "+user.getUserId());
				System.out.println("Category Name from dao => "+category.getCategoryName());
				jdbcTemplate.update("insert into category (categoryName, userId) values (?,?)",category.getCategoryName(),user.getUserId());
//			else {
//				category = jdbcTemplate.queryForObject("select userId from category where categoryName = ?", new BeanPropertyRowMapper<CategoryBean>(CategoryBean.class),category.getUser().getUserId());
//				int uId = category.getUser().getUserId();
//				System.out.println(uId);
//				if(uId != user.getUserId()) {
//					jdbcTemplate.update("insert into category (categoryName,userId) values (?,?)",category.getCategoryName(),user.getUserId());
//				}
//			}
		System.out.println("jay -- user id is"+user.getUserId());
		System.out.println("jay -- categoryName is "+category.getCategoryName());
		category = jdbcTemplate.queryForObject("select categoryId from category where categoryName=? and userId=?", new BeanPropertyRowMapper<CategoryBean>(CategoryBean.class),category.getCategoryName(),user.getUserId());
		System.out.println("jay -- categoryId is "+category.getCategoryId());
		return category;
	}

	public boolean createSubCategory(SubcategoryBean subCategory, int catId, String subCatName) {
//		System.out.println("Category Id from Dao => "+catId);
		int result = jdbcTemplate.update("insert into subCategory (subCatName,categoryId) values (?,?)",subCatName,catId);
		if(result == 1) {
			return true;
		}else {
			return false;
		}
	}
	public List<CategoryBean> getAllCategories(LoginBean loginuser){
		List<CategoryBean> list = jdbcTemplate.query("select * from category where userId = ?", new BeanPropertyRowMapper<CategoryBean>(CategoryBean.class),loginuser.getUserId());
		return list;
	}
	public CategoryBean getAllCategoriesById(int categoryId) {
		return  jdbcTemplate.queryForObject("select * from category where categoryId = ?",new Object[]{categoryId},new BeanPropertyRowMapper<CategoryBean>(CategoryBean.class));
	}
	public boolean deleteCategoryById(int catId, LoginBean loginuser) {
		System.out.println("Category Id before delete query => "+catId);
		int row = jdbcTemplate.update("delete from category where categoryId=?",catId);
		System.out.println("Category Id after delete query => "+catId);
		System.out.println("Delete query fired for category =>"+row);
		if(row == 1) {
			return true;
		}else {
			return false;
		}
	}
	public boolean updateCategories(CategoryBean category) {
		int row = jdbcTemplate.update("update category set categoryName = ? where categoryId = ?",category.getCategoryName(),category.getCategoryId());
		System.out.println("Update query fired for category => "+row);
		if(row == 1) {
			return true;
		}else {
			return false;
		}
	}
}
