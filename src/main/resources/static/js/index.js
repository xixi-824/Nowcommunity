$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	// 1、获取标题及正文文本框的内容
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中显示返回的消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后自动消除
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 帖子更新成功后才刷新整个页面
				// 刷新页面是再次向服务端发起同步请求，对整个页面的数据进行更新

				if(data.code == 200){
					window.location.reload();
				}
			}, 2000);
		}
	)
}