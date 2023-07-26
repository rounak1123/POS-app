


function callAjaxApi( url, method, requestBody, successMessage, successCallback){
  if(method != 'GET' && method != 'DELETE'){
	$.ajax({
	   url: url,
	   type: method,
	   data: requestBody,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        if(successMessage){
	   		$.notify(successMessage, "success");
	   		}
              if(successCallback){
              successCallback(response);
              }
	   },
	   error: handleAjaxError
	});
	}else {
		$.ajax({
    	   url: url,
    	   type: method,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	       if(successMessage){
    	       $.notify(successMessage, "success");
    	       }
              if(successCallback){
              successCallback(response);
              }
    	   },
    	   error: handleAjaxError
    	});
	}
}