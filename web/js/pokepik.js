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
			  document.getElementById("Header").innerHTML = httpHeader.responseText;
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
//			  console.log("responseText : " + httpHeader.responseText);
		    		document.getElementById("Content").innerHTML = httpHeader.responseText;
//		    		document.getElementById("lblUsername").innerHTML = httpHeader.getResponseHeader("username");
		    }
		  }
		httpHeader.open("POST","/sess",true);
		httpHeader.setRequestHeader("Content-type","application/json");
		httpHeader.send(JSON.stringify(jsonSess));
	
}

function auth() {	
	var jsonAuth = { "username" : document.getElementById("username").value, "password" : document.getElementById("password").value };
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