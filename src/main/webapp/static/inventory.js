var table;
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
		$('#add-inventory-modal').modal('toggle');
	var $form = $("#inventory-add-form");
	var json = toJson($form);
	var url = getInventoryUrl();
     url+='/add';
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;
	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function addToInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;
	    url += "/add";
	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});

	return false;
}

function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('inventory list', data);
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS

function processData(){
   var url = getInventoryUrl()+'/upload';
    var fileUpload = document.getElementById("inventoryFile");

    if (fileUpload .value != null) {
        var files = $("#inventoryFile").get(0).files;
        // Add the uploaded file content to the form data collection
        console.log(files);
        if (files.length > 0) {
    var formTag = $("#import-inventory-form")[0];
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
                   getInventoryList();

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

function displayInventoryList(data){
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-primary mr-2" onclick="displayEditInventory(' + e.id + ')" ><i class="fa fa-edit fa-sm text-white" ></i></button>' ;

          table.row.add([
            e.id,
            e.barcode,
            e.quantity,
            buttonHtml
          ]).draw();
	}
}

function displayEditInventory(id){
  	var url = getInventoryUrl() + "/" + id;
  	$.ajax({
  	   url: url,
  	   type: 'GET',
  	   success: function(data) {
  	   		displayInventoryEditModal(data);
  	   },
  	   error: handleAjaxError
  	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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
	var $file = $('#inventoryFile');
var fileName = $file.val().split('\\').pop();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#inventory-add-form input[name=id]").val(data.id);
	$("#inventory-add-form input[name=barcode]").val(data.barcode);
	$("#inventory-add-form input[name=quantity]").val(0);
	$('#add-inventory-modal').modal('toggle');
}

function displayInventoryEditModal(data){
	$("#inventory-edit-form input[name=id]").val(data.id);
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}


//INITIALIZATION CODE
 function initDatatable() {
    table = $('#inventory-table').DataTable(
                  {dom: 'lrtip',
                   paging: false,
                   scrollY: '450px',
                   scrollColapse: 'true',
                   });

   if(user_role == 'standard'){
     table.column(3).visible(false);
    }
} ;


function init(){
    initDatatable();
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

