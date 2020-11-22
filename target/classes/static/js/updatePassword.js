$(function(){
	// 用户名失去焦点事件
	$("#old-password").blur(check_oldPassword);
	$("#new-password").blur(check_newPassword);
	$("#confirmPassword").blur(check_confirmPassword);
	// 表单提交检查当前所有文本框内容是否合法
	$("#registerForm").submit(check_data);
});

function check_oldPassword() {
	$.post(
		CONTEXT_PATH + "/usernameCheck",
		{"username":$("#username").val()},
		function (data) {
			// 1、json字符串转换为json格式
			data = $.parseJSON(data);
			if(data.code == 200){
				// 用户名合法
				$("#username").removeClass("is-invalid");
				$("#usernameSuccessMsg").text(data.usernameSuccessMsg);
			}else{
				$("#username").addClass("is-invalid");
				$("#usernameMsg").text(data.usernameMsg);
				$("#usernameSuccessMsg").text("");
			}
		}
	)
}

function check_newPassword() {
	$.post(
		CONTEXT_PATH + "/passwordCheck",
		{"password":$("#password").val()},
		function (data) {
			// 1、json字符串转换为json格式
			data = $.parseJSON(data);
			if(data.code == 200){
				// 密码合法
				$("#password").removeClass("is-invalid");
				$("#passwordSuccessMsg").text(data.passwordSuccessMsg);
			}else{
				// 密码不合法
				$("#password").addClass("is-invalid");
				$("#passwordMsg").text(data.passwordMsg);
				$("#passwordSuccessMsg").text("");
			}
		}
	)
}

function check_confirmPassword() {
	$.post(
		CONTEXT_PATH + "/confirmPasswordCheck",
		{"password":$("#password").val(),"confirmPassword":$("#confirmPassword").val()},
		function (data) {
			// 1、json字符串转换为json格式
			data = $.parseJSON(data);
			if(data.code == 200){
				// 核对密码合法
				$("#confirmPassword").removeClass("is-invalid");
				$("#confirmPasswordSuccessMsg").text(data.confirmPasswordSuccessMsg);
			}else{
				// 核对密码不合法
				$("#confirmPassword").addClass("is-invalid");
				$("#confirmPasswordMsg").text(data.confirmPasswordMsg);
				$("#confirmPasswordSuccessMsg").text("");
			}
		}
	)
}

function check_data() {
	// 获取用户文本框的样式
	let oldPasswordClass = $("#old-password").hasClass("is-invalid");
	let newPasswordClass = $("#new-password").hasClass("is-invalid");
	let confirmPasswordClass = $("#confirmPassword").hasClass("is-invalid");
	// 其中四个文本框中存在一个文本框内容非法，无法提交操作
	if(usernameClass || passwordClass || confirmpasswordClass){
		return false;
	}
	return true;
}

