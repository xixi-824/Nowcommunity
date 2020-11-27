$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	let toName = $("#recipient-name").val();
	let content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			if(data.code == 2){
				// 当前用户未登录
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					window.location.replace(CONTEXT_PATH + "/login");
				}, 2000);
			}


			if(data.code == 0){
				// 私信发送成功
				// 提示2秒后重新加载私信界面
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					location.reload();
				}, 2000);
			}else{
				// 私信发送失败
				// 提示2秒信息后，无需重新加载整个界面
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
				}, 2000);
			}



		}
	)
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}