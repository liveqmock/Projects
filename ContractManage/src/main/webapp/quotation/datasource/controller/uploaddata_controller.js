//根据ID删除数据源
function deleteDataSource(id){
	$.messager.confirm('提示','确定要删除选中的数据吗？',function(r){  
	if (r){  
		//设置为同步
		$.ajaxSettings.async = false;
		$.post(
			ctx+'/dataSource/dataSource/dataSource!deleteDataSource.action',
			{id:id},
			function(data){
				var data = eval('(' + data + ')');
				if(null!=data && ""!=data){
					 if(data.msg=="ok"){
					    	$.messager.alert("提示","删除成功！");
					    	$("#tt").datagrid("reload");
					    	reloadDataGrid();
					 }else{
					    	$.messager.alert("提示","删除失败！");
					 }
				}
			});
		}
	});
}
			
//批量删除
function deleteSomeData() {
//获取表格选择行
	var rows = $('#tt').datagrid('getSelections');
	//判断是否选择行
	if (!rows || rows.length == 0) { 
		$.messager.alert('提示', '请选择要删除的数据!', 'info'); 
		return; 
	} 
	var parm = ""; 
	// 循环给提交删除参数赋值(音乐风格编码)
	$.each(rows, function (i, n) {
		parm +=  n.id+","; 
	}); 
	parm = parm.substring(0,parm.length-1);
	deleteDataSource(parm);
}			 
//弹出层***************************************
//添加存储操信息操作
function showWindow() {
  $('#addVmDiv').window('open');   
}

//弹出数据明细添加窗口
function showDataInfoWindow() {
	$('#addDataInfoDiv').window('open');   
}

//清空上传、数据详情表单
function clearVMFormFun(params) {
	if(1 == params) {		//清空数据源上传层
		$("#modelsName").val("");
		$("#modelsmiaoshu").val("");
		$("#modelshangchuan").val("");
	} else {
		$("#dinghuohao").val("");
		$("#chanpinshu").val("");
		$("#danjia").val("");
		$("#zhekoulv").val("");
		$("#chanpinbei").val("");
		$("#chanpinmiaoshu").val("");
		$("#dierbao").val("");
		$("#diyibao").val("");
		$("#disanbao").val("");
		$("#qitafei").val("");
		$("#anzhuangfei").val("");
	}
}

function clearFormFun()
{
	$("#modelsName").val("");
	$("#modelsmiaoshu").val("");
	$("#modelshangchuan").val("");
}
//隐藏存储操信息操作
function hideDiv() {   
	clearFormFun();
	$('#addVmDiv').window('close');
} 

//隐藏数据详情窗口操作
function hideDataInfoDiv() {
	 clearVMFormFun(2);
	 $('#addDataInfoDiv').window('close');
}

//重新加载列表
function reloadDataGrid() {
	$('#tt').datagrid('reload');
}

//上传数据的方法
function uploadFileFun() {
	$('#addVmform').form('submit', {
		url:ctx+"/dataSource/dataSource/dataSource!.action",
		onSubmit: function(){
			// 做某些检查
			// 返回 false 来阻止提交
			return $(this).form('validate'); 
		},
		success:function(data){
			if(data!=null && data!=""){
				if("error" == data.msg){
					$.messager.alert("提示","操作失败，请从新尝试!");
				} else {
					reloadDataGrid();
					$.messager.alert("提示","操作成功!");
					hideDiv();
				}
			} else {
				$.messager.alert("提示","系统异常，请稍后操作!");
			}
		}
	});
}

//文件上传
function upload(){
	var fileName = $('input[name="dataSource"]').val();
	if(fileName=="" || null==fileName){
		$.messager.alert('提示', "请选择需要上传的文件。", 'info');
		return;
	}
	var index = fileName.lastIndexOf(".");
	fileName = fileName.substring(index+1,fileName.length);
	if('xlsx'!=fileName){
		$.messager.alert('提示', "您选择的文件类型不对，只能上传xlsx文件。", 'info');
		return;
	}
	// 调用 form 插件的 'submit' 方法来提交 form
	$('#addVmform').form('submit', {
		url:ctx+'/dataSource/dataSource/dataSource!uploadDataSource.action',
		onSubmit: function(){
			// 做某些检查
			// 返回 false 来阻止提交
			return $(this).form('validate'); 
		},
		success:function(data){
			var data = eval('(' + data + ')');
			if(null!=data && ""!=data){
				if(data.msg=="ok"){
					$.messager.alert("提示","添加成功!");
					$('#addVmform').form("clear");
					hideDiv();
//					$('#addVmDiv').css('display','none');
					$("#tt").datagrid("reload");
				}else {
					$.messager.alert("提示","添加失败!");
					$('#addVmform').form("clear");
				}
			}
		}
	}); 
}

//保存详细数据信息的方法
function saveNewDataInfos() {
	$('#addDataInfoform').form('submit', {
		url:ctx+'/dataSource/dataSource/dataRecord!save.action',
		onSubmit: function(){
			// 做某些检查
			// 返回 false 来阻止提交
			return $(this).form('validate'); 
		},
		success:function(data){
			var data = eval('(' + data + ')');
			if(null!=data && ""!=data){
				if(data.msg=="ok"){
					$.messager.alert("提示","添加成功!");
					hideDataInfoDiv();
				}else {
					$.messager.alert("提示","添加失败!");
				}
			}
		}
	}); 
}

//设置标准配置
function updateModelsInfos(paramsid,paramsName) {
	window.location = "standardConfiguration.jsp?dataSourceIds="
		+ paramsid + "&dataSourceNames="
		+ paramsName;
}