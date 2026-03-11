
function showdiv(){
	var option = document.getElementById("changeOption").value;
	var time = document.getElementById("timeOption");
	var stop = document.getElementById("stopOption");
	var responsor = document.getElementById("responsorOption");
	
	if(option == 'time'){
		time.style.display = "block";
		stop.style.display = "none";
		responsor.style.display = "none";
	}
	
	if(option == 'stop'){
		time.style.display = "none";
		stop.style.display = "block";
		responsor.style.display = "none";
	}
	if(option == 'responsor'){
		time.style.display = "none";
		stop.style.display = "none";
		responsor.style.display = "block";
	}
}