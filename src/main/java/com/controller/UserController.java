package com.controller;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bean.AccountBean;
import com.bean.AccountType;
import com.bean.CategoryBean;
import com.bean.Expense;
import com.bean.LabelBean;
import com.bean.LoginBean;
import com.bean.PayeeBean;
import com.bean.RoleBean;
import com.bean.SubcategoryBean;
import com.bean.UserBean;
import com.dao.AccountDao;
import com.dao.CategoryDao;
import com.dao.ExpenseDao;
import com.dao.RoleDao;
import com.dao.UserDao;
import com.dao.labelDao;
import com.dao.payeeDao;
import com.ser.MailSend;

@Controller
class UserController {

	@Autowired
	UserDao dao;

	@Autowired
	AccountDao acc;

	@Autowired
	RoleDao roleDao;
	
	@Autowired
	CategoryDao cDao;
	
	@Autowired
	labelDao lDao;
	
	@Autowired
	payeeDao pDao;
	
	@Autowired
	ExpenseDao eDao;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcomePage(Model model) {
		model.addAttribute("validate", new LoginBean());
//		List<RoleBean> listRole = roleDao.getRoles();
//		System.out.println(listRole.get(1).getRoleName());
//		model.addAttribute("roles", listRole);
		return "login";
	}

	@RequestMapping(value = "/addRole", method = RequestMethod.GET)
	public String roleAddition(Model model) {
		model.addAttribute("validate1", new RoleBean());
		return "createRole";
	}

	@PostMapping("/insertRole")
	public String InsertRole(@Valid @ModelAttribute("validate1") RoleBean role, BindingResult result, Model model,
			HttpServletRequest request) {

		if (result.hasErrors()) {
			model.addAttribute("role", role);
			return "createRole";
		} else {
			roleDao.insertRole(role);
			System.out.println("Role has added...");
			return "viewRole";
		}
	}

//	@PostMapping("/entry")
//	public String entryUser(RoleBean role,UserBean user, Model model) {
//		System.out.println(user.getrId());
//		System.out.println(role.getRoleName());
//		if (role.getRoleName().equalsIgnoreCase("Admin")) {
//			return "login";
//		}else {
//			boolean flag = dao.checkUser(user);
//			if (flag) {
//				model.addAttribute("roleBean", role);
//				return "login";
//			}else {
//				model.addAttribute("roleBean", role);
//				return "UserSignup";
//			}
//		}
//	}

	@GetMapping("/roles")
	public String listAllUsers(Model model) {
		model.addAttribute("roles", roleDao.getRoles());
		System.out.println("All users are viewed...");
		return "viewRole";
	}

	@GetMapping("/edit/{roleId}")
	public String editById(@PathVariable("roleId") int roleId, Model model) {
		RoleBean role = roleDao.getRoleById(roleId);
		return "updateRole";
	}

	@GetMapping("/delete/{roleId}")
	public String deleteById(@PathVariable("roleId") int roleId) {
		Boolean bool = roleDao.deleteRoles(roleId);
		System.out.println("Role has deleted...");
		return "redirect:/roles";
	}

	@PostMapping("/updateRole")
	public String updateRole(RoleBean role, Model model) {

		model.addAttribute("role", role);
		boolean flag = roleDao.updateRoles(role);
		if (flag) {
			System.out.println("role has updated...");
			System.out.println(role.getRoleName());
			return "redirect:/roles";
		} else {
			System.out.println("Something wrong!");
			System.out.println(role.getRoleName());
			return "updateRole";
		}

	}

	@PostMapping("/verifyUser")
	public String login(@Valid @ModelAttribute("validate") LoginBean loginuser, BindingResult result,
			@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session,
			HttpServletRequest request, Model model, @RequestParam String message) {

		if (result.hasErrors()) {
			model.addAttribute("user", loginuser);
			return "login";
		} else {
			loginuser = dao.login(email, password);
			if (loginuser != null) {
				int i = loginuser.getUserId();
				System.out.println(i);
				if (i == 1) {
					message = "You are successfully logged in...";
					session = request.getSession();
					session.setAttribute("admin", loginuser);
					session.setAttribute("message", message);
					return "AdminHome";
				} else {
					message = "You are successfully logged in...";
					session = request.getSession();
					int userId = loginuser.getUserId();
					System.out.println("Session Variable of userID after logged in =>" + userId);
					session.setAttribute("user", loginuser);
					session.setAttribute("message", message);
					model.addAttribute("validate5", new AccountBean());
					model.addAttribute("validate6", new CategoryBean());
					model.addAttribute("validate9", new LabelBean());
					model.addAttribute("validate10", new PayeeBean());
					List<AccountType> list = acc.getAccTypes();
					model.addAttribute("getAccTypes", list);
					return "UserHome";
				}
			} else {
				message = "This user is not valid!";
				model.addAttribute("message", message);
				return "login";
			}
		}

	}

	@GetMapping("/adminLogout")
	public String adminSignout(HttpSession session, @Valid @ModelAttribute("validate") UserBean user,
			BindingResult result) {
		session.invalidate();
		System.out.println("Session Destroyed!");
		System.out.println("Your are logged out from our site!");
		return "login";
	}
	@GetMapping("/loginPage")
	public String moveToLoginPage(Model model, HttpSession session) {
		model.addAttribute("validate", new LoginBean());
		session.invalidate();
		return "login";
	}

	@GetMapping("/userLogout")
	public String userSignout(HttpSession session, @Valid @ModelAttribute("validate") UserBean user,
			BindingResult result) {
		session.invalidate();
		System.out.println("Session Destroyed!");
		System.out.println("Your are logged out from our site!");
		return "login";
	}

	@RequestMapping(value = "viewData")
	public String getUserData(Model model) {
		model.addAttribute("showdata", dao.showData());
		return "userData";

	}

	@GetMapping("/moveToRegister")
	public String signUp(Model model) {
		model.addAttribute("validate3", new UserBean());
		return "UserSignup";
	}

	@PostMapping("/saveUser")
	public String signup(@Valid @ModelAttribute("validate3") UserBean user, BindingResult result, RoleBean role, Model model, String message) {
		System.out.println("Binding Result=> " + result);
		if (result.hasErrors()) {
			model.addAttribute("user", user);
			return "UserSignup";
		} else {
			// String filename= service.fileUpload(file);
			// user.setProfilePic(filename);
			role.setRoleId(2);
			user.setRole(role);
			int rId = user.getRole().getRoleId();
			System.out.println("Role ID:" + rId);
			List<UserBean> list = dao.checkExistingUserData(user);
			System.out.println(list.size());
			if(list.size()==1) {
				model.addAttribute("user", user);
				model.addAttribute("validate", user);
				message = "Invalid Action!";
				model.addAttribute("message", message);
				return "UserSignup";
			}else {
				model.addAttribute("user", user);
				model.addAttribute("validate", user);
				dao.insert(user);
				return "login";
			}
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteUser(@RequestParam("id") int id, Model model) {
		int i = dao.delete(id);
		if (i != 0) {
			model.addAttribute("showdata", dao.showData());
			return "userData";
		}
		return "userData";

	}

	@GetMapping(value = "/update/{id}")
	public String updateUser(@PathVariable("id") int id, Model model) {

		UserBean bean = dao.getDataById(id);
		model.addAttribute("user", bean);
		return "updateUser";
	}

	@PostMapping("/editUser")
	public String EditUser(UserBean user, Model model) {
		System.out.println(user.getEmail());
		int i = dao.updateUser(user);
		System.out.println(i);
		return "redirect:/viewData";
	}

	@GetMapping("/forgotpassword")
	public String forgot() {
		return "Email";
	}

	@GetMapping("/createExpenses")
	public String checkAccountStatus(Model model) {
		return "";
	}

	@GetMapping("/moveToInsertAcc")
	public String moveToInsertAcc(Model model) {

		model.addAttribute("validate4", new AccountType());
		return "AddAccountType";
	}

	@PostMapping("/insertAccType")
	public String insertAccType(@Valid @ModelAttribute("validate4") AccountType accType, BindingResult result,
			Model model, String message, HttpSession session) {
		if (result.hasErrors()) {
			model.addAttribute("accType", accType);
			return "AddAccountType";
		} else {
			model.addAttribute("accType", accType);
			acc.addAccType(accType);
			message = "Account type has inserted...";
			model.addAttribute("msg1", message);
			System.out.println("Account type has inserted...");
			return "AddAccountType";
		}
	}

	@GetMapping("/adminHome")
	public String AdminHome(Model model) {
		return "AdminHome";
	}

	@GetMapping("/userHome")
	public String UserHome(Model model, LoginBean user) {
		
		List<CategoryBean> list2 = cDao.getAllCategories(user);
		model.addAttribute("getAllCategories", list2);
		
		List<AccountType> list = acc.getAccTypes();
		model.addAttribute("getAccTypes", list);
		
		List<LabelBean> list4 = lDao.getAllLabels(user);
		model.addAttribute("getAllLabels", list4);
		
		List<PayeeBean> list3 = pDao.getAllPayees(user);
		model.addAttribute("getAllPayees", list3);
		
		List<AccountBean> list5 = acc.getAccounts(user);
		model.addAttribute("getAccounts", list5);
		
		model.addAttribute("validate5", new AccountBean());
		model.addAttribute("validate6", new CategoryBean());
		model.addAttribute("validate9", new LabelBean());
		model.addAttribute("validate10", new PayeeBean());
		return "UserHome";
	}
	@PostMapping("/ExpenceTracking")
	public String ExpenseTracking(@Valid @ModelAttribute("validate8") Expense ex, BindingResult result, Model model, CategoryBean category,LabelBean label,PayeeBean payee,AccountBean account, HttpSession session,String message) {
		if(result.hasErrors()) {
			LoginBean user = (LoginBean) session.getAttribute("user");
			model.addAttribute("expense", ex);
			
			List<AccountType> list = acc.getAccTypes();
			model.addAttribute("getAccTypes", list);
			
			List<LabelBean> list4 = lDao.getAllLabels(user);
			model.addAttribute("getAllLabels", list4);
			
			List<PayeeBean> list3 = pDao.getAllPayees(user);
			model.addAttribute("getAllPayees", list3);
			
			List<AccountBean> list5 = acc.getAccounts(user);
			model.addAttribute("getAccounts", list5);
			
			List<CategoryBean> list2 = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list2);
			
			return "ExpenseCreation";
		}else {
			LoginBean user = (LoginBean) session.getAttribute("user");
			model.addAttribute("expense", ex);
			
			List<AccountType> list = acc.getAccTypes();
			model.addAttribute("getAccTypes", list);
			
			List<LabelBean> list4 = lDao.getAllLabels(user);
			model.addAttribute("getAllLabels", list4);
			
			List<PayeeBean> list3 = pDao.getAllPayees(user);
			model.addAttribute("getAllPayees", list3);
			
			List<AccountBean> list5 = acc.getAccounts(user);
			model.addAttribute("getAccounts", list5);
			
			List<CategoryBean> list2 = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list2);
			
			boolean flag = false;
			try {
				flag = eDao.createExpense(ex,category,payee,label,user,account);
			}catch(Exception e) {
				e.printStackTrace();
			}
			if(flag) {
				message = "Your expense has added successfully...";
				model.addAttribute("EMessage", message);
			}else {
				message = "Sorry try again later!";
				model.addAttribute("EMessage", message);
			}
			return "ExpenseCreation";
		}
	}

	@PostMapping("/createAcc")
	public String CreateAcc(@Valid @ModelAttribute("validate5") AccountBean account, BindingResult result,
			@RequestParam String accTypeName, Model model, String message, HttpSession session, HttpServletRequest request) {
		
		boolean bool = false;
		System.out.println("Create Account Method Called...");
		System.out.println(account);
		System.out.println(result.hasErrors());
		if (result.hasErrors()) {
			model.addAttribute("account", account);
			List<AccountType> list = acc.getAccTypes();
			model.addAttribute("getAccTypes", list);
			return "AccountCreation";
		}else {
			System.out.println("Validation successfull...");
			List<AccountType> list = acc.getAccTypes();
			System.out.println("Account Type list=> "+list);
			model.addAttribute("getAccTypes", list);
			model.addAttribute("account", account);
			LoginBean user = (LoginBean) session.getAttribute("user");
			System.out.println("User Session Object => " + user);
			int userId = user.getUserId();
			System.out.println("User ID from account..."+userId);
			bool = acc.createAccount(account, userId, accTypeName);
			System.out.println("bool value =>"+bool);
			if(bool == true) {
				System.out.println("Account has created successfully");
				message = "Your account has created successfully...";
				model.addAttribute("msg2", message);
				return "AccountCreation";
			}else {
					System.out.println("Something Wrong!");
					message = "Something Wrong!";
					model.addAttribute("msg2", message);
					return "AccountCreation";
				}
			}
}

	@PostMapping("/categoryInsertion")
	public String CategoryInsertion(@Valid @ModelAttribute("validate6") CategoryBean category,BindingResult result, SubcategoryBean subCategory,@RequestParam("subCatName")String subCatName,HttpSession session,String message,Model model) {
		if(result.hasErrors()) {
			model.addAttribute("category", category);
			
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			
			message = "Invalid input!";
			model.addAttribute("msg4", message);
			return "UserHome";
		}else {
			model.addAttribute("category", category);
			
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			
			LoginBean user = (LoginBean) session.getAttribute("user");
			System.out.println("Session user id:"+user.getUserId());
			System.out.println("Sub-category Name => "+subCatName);
			try {
				category = cDao.createCategory(category,user);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			int catId = category.getCategoryId();
			System.out.println("Category Id from controller => "+catId);
			boolean flag2 = cDao.createSubCategory(subCategory,catId,subCatName);
			
			if(category != null) {
				if(flag2) {
					System.out.println("Category and Sub-Category Insertion Successful...");
					message = "Category And Sub-Category Insertion Successful...";
					model.addAttribute("msg5", message);
					return "UserHome";
				}else {
					System.out.println("Sub-Category Insertion Unsuccessful...");
					message = "Sub-Category Insertion Unsuccessful...";
					model.addAttribute("msg4", message);
					return "UserHome";
				}
			}else {
				System.out.println("Null Category Object Passed...");
				message = "Null Category Object Passed...";
				model.addAttribute("msg7", message);
				return "UserHome";
			}
		}
	}
	
	@GetMapping("/submit")
	public String demoSubmit(@RequestParam String username) {
		System.out.println("User Name:" + username);
		return "redirect:/adminHome";
	}

	@GetMapping("/checkAccStatus")
	public String CheckAccStatus(AccountBean account, String message, Model model, HttpSession session,
			HttpServletRequest request) throws Exception {
		
		LoginBean user = (LoginBean) session.getAttribute("user");
		System.out.println("Method called..."+user.getUserId());
		if (user != null) {
			List<AccountBean> list1 = acc.checkAccCreation(user);
			System.out.println(user.getUserId());
			if (list1.size()>=1) {
				message = "You already have an account!";
				System.out.println(message);
				model.addAttribute("validate8", new Expense());
				model.addAttribute("validate6", new CategoryBean());
				
				List<AccountType> list2 = acc.getAccTypes();
				model.addAttribute("getAccTypes", list2);
				
				List<CategoryBean> list3 = cDao.getAllCategories(user);
				model.addAttribute("getAllCategories", list3);
				
				List<AccountBean> list5 = acc.getAccounts(user);
				model.addAttribute("getAccounts", list5);
				
				List<LabelBean> list4 = lDao.getAllLabels(user);
				model.addAttribute("getAllLabels", list4);
				System.out.println("Label Records: "+list4);
				
				List<PayeeBean> list6 = pDao.getAllPayees(user);
				model.addAttribute("getAllPayees", list6);
				
				return "ExpenseCreation";
			} else {
				message = "You don't have any account, please firstly create one!";
				System.out.println(message);
				model.addAttribute("validate5", new AccountBean());
				List<AccountType> list = acc.getAccTypes();
				model.addAttribute("getAccTypes", list);
				return "AccountCreation";
			}
		} else {
			return "UserHome";
		}
	}

	@GetMapping("/email")
	public String email(@RequestParam("email") String email, Model model, HttpSession session) {
		if (email != "") {
			int int_otp = generateOTP();
			MailSend.mail(email, int_otp);
			session.setAttribute("email", email);
			return "otp";

		} else {
			return "Email";
		}
	}

	public static int generateOTP() {
		int lengthOtp = 6;
		char OTPValue[] = new char[lengthOtp];
		String allDigits = "1234567890";
		Random randomObj = new Random();
		for (int i = 0; i < lengthOtp; i++) {
			int randomNo = randomObj.nextInt(allDigits.length());
			OTPValue[i] = allDigits.charAt(randomNo);
		}
		String otp = new String(OTPValue);
		int int_otp = Integer.parseInt(otp);
		return int_otp;
	}

	@GetMapping("/newPassword")
	public String updatePass(@RequestParam("otp") String otp, Model model, HttpSession session) {
		String eotp = (String) session.getAttribute("eotp");
		if (eotp.equals(otp)) {
			return "forgot";
		}
		return "otp";
	}
	@PostMapping("/labelInsertion")
	public String LabelInsertion(@Valid @ModelAttribute("validate9") LabelBean label,BindingResult result,String message,CategoryBean cbean, Model model, HttpSession session) {
		if(result.hasErrors()) {
			model.addAttribute("label", label);
			message = "Invalid Input!";
			model.addAttribute("msg8", message);
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			return "UserHome";
		}else {
			message = "One label has inserted successfully...";
			model.addAttribute("msg8", message);
			model.addAttribute("label", label);
			LoginBean user = (LoginBean) session.getAttribute("user");
			System.out.println("Session User ID From controller when insert the label => "+user.getUserId());
			try {
				label = lDao.createLabels(label,user);
				System.out.println("LabelBean object from controller => "+label);
				System.out.println("Label Name From controller => "+label.getLabelName());
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			
			return "UserHome";
		}
	}
	@PostMapping("/payeeInsertion")
	public String PayeeInsertion(@Valid @ModelAttribute("validate10") PayeeBean payee,BindingResult result,String message,Model model, HttpSession session) {
		if(result.hasErrors()) {
			message = "Invalid Input!";
			model.addAttribute("msg9", message);
			model.addAttribute("payee", payee);
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			return "UserHome";
		}else {
			message = "One payee has inserted successfully...";
			model.addAttribute("msg9", message);
			model.addAttribute("payee", payee);
			LoginBean user = (LoginBean) session.getAttribute("user");
			System.out.println("Session User ID From controller when insert the payee => "+user.getUserId());
			try {
				payee = pDao.createPayees(payee,user);
				System.out.println("PayeeBean object from controller => "+payee);
				System.out.println("Payee Name from controller => "+payee.getPayeeName());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			return "UserHome";
		}
	}
	@GetMapping ("/manageCategories")
	public String ManageCategories (Model model, HttpSession session) {
		LoginBean user = (LoginBean) session.getAttribute("user");
		List<CategoryBean> list = cDao.getAllCategories(user);
		model.addAttribute("getAllCategories", list);
		return "ManageCategories";
	}
	@RequestMapping("/editCategory/{categoryId}")
	public String EditCategory(@PathVariable("categoryId") int categoryId,Model model, HttpSession session) {
		System.out.println("Category Id when edit button has clicked => "+categoryId);
		CategoryBean category = cDao.getAllCategoriesById(categoryId);
		System.out.println("Category id, name after getAllCategoriesById() called => "+category.getCategoryId()+" - "+category.getCategoryName());
		model.addAttribute("Category", category);
		return "UpdateCategory";
	}
	@GetMapping("/modifyCategory")
	public String ModifyCategory(CategoryBean category, Model model,String message,HttpSession session) {
		System.out.println("Category id and name from modifyCategory mapping =>"+category.getCategoryId()+" - "+category.getCategoryName());
		LoginBean user = (LoginBean) session.getAttribute("user");
		boolean flag= cDao.updateCategories(category);
		System.out.println("Result after updateCategory() function called => "+flag);
		if(flag) {
			message = "Category Updation Successfull...";
			System.out.println("flag true");
			model.addAttribute("msg10", message);
			System.out.println(message);
			List<CategoryBean> list = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list);
			return "ManageCategories";
		}else {
			System.out.println("flag false");
			message = "Category Updation Unsuccessfull...";
			System.out.println(message);
			model.addAttribute("msg10", message);
			List<CategoryBean> list = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list);
			return "ManageCategories";
		}
	}
	@GetMapping("/deleteCategory/{categoryId}")
	public String DeleteCategory(@PathVariable("categoryId") int categoryId,SubcategoryBean subCategory,Model model,String message,HttpSession session) {
		LoginBean user = (LoginBean) session.getAttribute("user");
		boolean flag = cDao.deleteCategoryById(categoryId,user);
		if(flag) {
			message = "Category Deletion Successfull...";
			model.addAttribute("msg11", message);
			List<CategoryBean> list = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list);
			return "ManageCategories";
		}else {
			message = "Category Deletion Unsuccessfull...";
			model.addAttribute("msg11", message);
			List<CategoryBean> list = cDao.getAllCategories(user);
			model.addAttribute("getAllCategories", list);
			return "ManageCategories";
		}
	}
	@PostMapping("insertAccounts")
	public String InsertAccounts(@Valid @ModelAttribute("validate5") AccountBean account, BindingResult result, @RequestParam("accTypeName") String accTypeName, @RequestParam("accountName") String param_accountName, Model model, HttpSession session, String message) {
		boolean flag = false;
		if(result.hasErrors()) {
			List<AccountType> list = acc.getAccTypes();
			model.addAttribute("getAccTypes", list);
			model.addAttribute("account", account);
			model.addAttribute("validate5", new AccountBean());
			model.addAttribute("validate6", new CategoryBean());
			model.addAttribute("validate9", new LabelBean());
			model.addAttribute("validate10", new PayeeBean());
			return "UserHome";
		}else {
			List<AccountType> list = acc.getAccTypes();
			model.addAttribute("getAccTypes", list);
			model.addAttribute("account", account);
			LoginBean user = (LoginBean) session.getAttribute("user");
			System.out.println("User Session Object => " + user);
			System.out.println("User ID from account..."+user.getUserId());
			flag = acc.createAccount1(account, user.getUserId(), accTypeName,param_accountName);
			System.out.println("Account Insertion Result in boolean =>"+flag);
				if(flag == true) {
					System.out.println("Account has created successfully");
					message = "Your account has created successfully...";
					model.addAttribute("msg2", message);
					model.addAttribute("validate5", new AccountBean());
					model.addAttribute("validate6", new CategoryBean());
					model.addAttribute("validate9", new LabelBean());
					model.addAttribute("validate10", new PayeeBean());
					return "UserHome";
				}else {
					System.out.println("Invalid Action!");
					message = "Invalid Action!";
					model.addAttribute("msg2", message);
					model.addAttribute("validate5", new AccountBean());
					model.addAttribute("validate6", new CategoryBean());
					model.addAttribute("validate9", new LabelBean());
					model.addAttribute("validate10", new PayeeBean());
					return "UserHome";
				}
		}
	}
	
}