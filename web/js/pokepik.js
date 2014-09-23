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
	var jsonHeader = { 'IDSESSION' : 'toto' };
	httpHeader.open("GET","/header",true);
	httpHeader.setRequestHeader("Content-type","application/json");
	httpHeader.send(JSON.stringify(jsonHeader));
}

function loadContext() {
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
	loadContext();
}