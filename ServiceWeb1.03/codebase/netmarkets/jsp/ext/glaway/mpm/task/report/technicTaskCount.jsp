<%@ page language="java" pageEncoding="UTF-8" %>
<style>
	div{
		font-size:12px;
		color:red;
		background-color:#EAEAE8;
		border:1 solid #1892B6;
		padding:1;
	}
</style>

<script>
	function expand(){
		var main = document.getElementById("child1").style.display;
		if(main == 'none'){
			document.getElementById("child1").style.display = '';
		}else{
			document.getElementById("child1").style.display = 'none';
		}
	}
</script>

<div id="main1" style="color:blue" onclick="expand()">
	<a href="#">+ 主目录1</a><br/>
</div>
<div id="child1" style="display:none">
	<a href="#">- 子目录1</a><br/>
	<a href="#">- 子目录2</a><br/>
	<a href="#">- 子目录3</a><br/>
</div>