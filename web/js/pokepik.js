/**
 * 
 */

function loadHeader() {
	var httpHeader = new XMLHttpRequest;
	httpHeader.onreadystatechange=function()
	  {
	  if (httpHeader.readyState==4 && httpHeader.status==200)
	    {
	    	document.getElementById("Header").innerHTML=httpHeader.responseText;
	    }
	  }
	var jsonHeader = { "IDSESSION" : "f4be24d7-312f-489c-883a-78f5ca58b3f6" };
	httpHeader.open("POST","/header",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonHeader));
	console.log(JSON.stringify(jsonHeader));
}

function loadContent() {
	var httpContent = new XMLHttpRequest;
	httpContent.onreadystatechange=function()
	  {
	  if (httpContent.readyState==4 && httpContent.status==200)
	    {
	    	document.getElementById("Content").innerHTML=httpContent.responseText;
	    }
	  }
	var jsonHeader = { 'IDSESSION' : 'toto' };
	httpContent.open("GET","/content",true);
	httpContent.setRequestHeader("Content-type","application/json");
	httpContent.send(JSON.stringify(jsonHeader));
}

function loadBody() {
	loadHeader();
	loadContent();
}