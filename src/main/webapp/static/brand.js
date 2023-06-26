
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();
	   		$("#brand-form")[0].reset();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateBrand(event){
	$('#edit-brand-modal').modal('toggle');
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;
	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('brand list', data);
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}


// FILE UPLOAD METHODS

function processData(){
var url = getBrandUrl()+'/upload';


    var fileUpload = document.getElementById("brandFile");

    if (fileUpload .value != null) {
        var files = $("#brandFile").get(0).files;
        // Add the uploaded file content to the form data collection
        console.log(files);
        if (files.length > 0) {
    var formTag = $("#import-form")[0];
    var formData = new FormData(formTag);
    formData.append("file", files[0]);
            $.ajax({
                url: url,
                data:formData,
                type:"post",

                // Tell jQuery not to process data or not to worry about content-type
                // You *must* include these options in order to send MultipartFile objects
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST',

                success:function(data){
                    console.log(data);
                    $.notify("Successfully Uploaded the file",{type:"success"});
                   getBrandList();


                },
                error: function(response){
                      errorOnUpload();
                      handleAjaxError(response);
                }
            });
        }
    }
    resetUploadDialog();
}

function errorOnUpload(){
$('#download-errors').prop("disabled", false);
}

function downloadErrors(){
$('#download-errors').prop("disabled", true);

}

//UI DISPLAY METHODS

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	 var table = $('#brand-table').DataTable({searching: false});
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
		var buttonHtml = ' <button onclick="displayEditBrand(' + e.id + ')"><i class="fas fa-edit fa-sm"></i></button>'

          table.row.add([
            serialNo,
            e.brand,
            e.category,
            buttonHtml
          ]).draw();
	}

}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	errorCount = 0;
	errorFlag = false;
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorCount);
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=id]").val(data.id);
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$('#edit-brand-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getBrandList);

