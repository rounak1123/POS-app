const toggleForm = () => {
  const container = document.querySelector('.container');
  container.classList.toggle('active');
};

function getNewUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/site/user/new";
}

function handleSignUp(){
   var url = getNewUserUrl();
   console.log(url);
   var email = $("#signup-form input[name=email]").val();
   var password = $("#signup-form input[name=password]").val();
   var confirmPassword = $("#signup-form input[name=confirm-password]").val();

   if(email == '' || password == '' || confirmPassword == ''){
   $.notify("Email or password not entered.");
   return;
   }
console.log(url, email, password);
   if(password != confirmPassword){
   $.notify("Password doesn't match. Try Again.");
   $("#signup-form")[0].reset();
   return;
   }
   var obj = {email, password};

   	$.ajax({
   	   url: url,
   	   type: 'POST',
   	   data: JSON.stringify(obj),
   	   headers: {
          	'Content-Type': 'application/json'
          },
   	   success: function(response) {
    $.notify("Succesfully Signed Up. Login to continue", "success");
        toggleForm();
   	   },
   	   error: handleAjaxError
   	});


}

function init(){
	$('#sign-up').click(handleSignUp);
}

$(document).ready(init);