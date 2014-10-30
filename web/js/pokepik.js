/**
 * 
 */

function logout() {
	var jsonLogout = { "sessionID" : localStorage.getItem("pokepikSessionId") };
	var httpHeader = new XMLHttpRequest;
	httpHeader.onreadystatechange=function()
	  {
	  if (httpHeader.readyState==4 && httpHeader.status==200) {
		 	  localStorage.clear();
			  document.getElementById("Content").innerHTML = httpHeader.responseText;
	    }
	  }
	httpHeader.open("POST","/logout",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonLogout));
}

function checkSession() {
	var sessId = localStorage.getItem("pokepikSessionId");
	if (! sessId) {
		sessId="givemeoneplease";
	}
		var jsonSess = { "sessionID" : sessId };
		var httpHeader = new XMLHttpRequest;
		httpHeader.onreadystatechange=function()
		  {
		  if (httpHeader.readyState==4 && httpHeader.status==200)
		    {
		    		document.getElementById("Content").innerHTML = httpHeader.responseText;
		    }
		  }
		httpHeader.open("POST","/sess",true);
		httpHeader.setRequestHeader("Content-type","application/json");
		httpHeader.send(JSON.stringify(jsonSess));
	
}

function auth() {	
	var jsonAuth = { "username" : document.getElementById("username").value, "password" : md5(document.getElementById("password").value) };
	var httpHeader = new XMLHttpRequest;
	httpHeader.onreadystatechange=function()
	  {
	  if (httpHeader.readyState==4 && httpHeader.status==200)
	    {	
	    	var jsonResponse = JSON.parse(httpHeader.responseText);
	    	    	
	    	if ("ok" == jsonResponse.status) {
	    		localStorage.setItem("pokepikSessionId", jsonResponse.sessionID);
	    		checkSession();
	    	} else {
	    		location.reload();
	    	}
	    }
	  }
	httpHeader.open("POST","/auth",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonAuth));
}

function newUser() {	
	var jsonNU = { "newUserRequestType" : "init" };
	var httpHeader = new XMLHttpRequest;
	httpHeader.onreadystatechange=function()
	  {
	  if (httpHeader.readyState==4 && httpHeader.status==200)
	    {	
		  console.log("responseText : " + httpHeader.responseText);
		  document.getElementById("Content").innerHTML = httpHeader.responseText;
	    }
	  }
	httpHeader.open("POST","/newUser",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonNU));
}

function subNewUser() {
	var jsonNUSub = {
			"newUserRequestType" : "submit",
			"login" : document.getElementById("username").value,
			"pwd1" : md5(document.getElementById("password1").value)
		};
	var httpHeader = new XMLHttpRequest;
	httpHeader.onreadystatechange=function()
	  {
	  if (httpHeader.readyState==4 && httpHeader.status==200)
	    {	
		  console.log("responseText : " + httpHeader.responseText);
		  document.getElementById("Content").innerHTML = httpHeader.responseText;
		  document.getElementById("username").value = httpHeader.getResponseHeader("username");
		  document.getElementById("password").focus();
	    }
	  }
	httpHeader.open("POST","/newUser",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonNUSub));
	
}