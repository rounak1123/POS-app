const toggleForm = () => {
  const container = document.querySelector('.container');
  container.classList.toggle('active');
};

function getNewUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/user";
}

function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

function isValidPassword(password) {
  const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
  return passwordRegex.test(password);
}


function handleSignUp(){

    var isValid = $("#signup-form")[0].checkValidity();
            if(!isValid){
              $("#signup-form")[0].reportValidity();
                 return;
            }

   var url = getNewUserUrl();
   var email = $("#signup-form input[name=email]").val();
   var password = $("#signup-form input[name=password]").val();
   var confirmPassword = $("#signup-form input[name=confirm-password]").val();

   if(email == '' || password == '' || confirmPassword == ''){
//   $.notify("Email or password not entered.");
    $.notify("Email or password not entered.");
   return;
   }
console.log(url, email, password);

   if(!isValidEmail(email)){
   $.notify("Invalid Email format");
   return;
   }
   if(password != confirmPassword){
   $.notify("Password doesn't match. Try Again.");
   $("#signup-form")[0].reset();
   return;
   }

   if(!isValidPassword(password)){
   $.notify("Password must be between 8 to 15 characters and should contain at least one lowercase letter, one uppercase letter, one numeric digit");
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
    console.log(message);
    if(message != "No message"){
    $.notify(message);
    }
	$('#sign-up').click(handleSignUp);
}

$(document).ready(init);