var table;
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getAdminInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/inventory";
}
//BUTTON ACTIONS
function updateInventory(event){
    console.log('in update')
    var isValid = $("#inventory-edit-form")[0].checkValidity();
            if(!isValid){
              $("#inventory-edit-form")[0].reportValidity();
                 return;
            }

	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getAdminInventoryUrl() + "/" + id;
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
	   		$.notify("Inventory updated", "success");
	$('#edit-inventory-modal').modal('toggle');

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
	   		updateSearchList(data);
	   },
	   error: handleAjaxError
	});
}

function updateSearchList(data){
    brandData = [];
    categoryData = [];
    productNameData = [];
    barcodeData = [];

	for(var i in data){
		var e = data[i];
		brandData.push(e.brand);
		categoryData.push(e.category);
		productNameData.push(e.name);
		barcodeData.push(e.barcode);
	}
    console.log(productNameData);

	brandData = [...new Set(brandData)];
	categoryData = [...new Set(categoryData)];
	productNameData = [...new Set(productNameData)]

		$("#inputBrandSearch").select2({
    	data: brandData})
        $("#inputCategorySearch").select2({
        data: categoryData})

        $("#inputProductNameSearch").select2({
        data: productNameData})
        $("#inputBarcodeSearch").select2({
        data: barcodeData})

//	  var brandDropdown = $('#inputBrandSearch');
//      brandDropdown.empty();
//      brandDropdown.append($('<option></option>').val('').html('Select an option'));
//      $.each(brandData, function (i, brand){
//          brandDropdown.append($('<option></option>').val(brand).html(brand));
//      })
//        var categoryDropdown = $('#inputCategorySearch');
//        categoryDropdown.empty();
//        categoryDropdown.append($('<option></option>').val('').html('Select an option'));
//        $.each(categoryData, function (i, category){
//            categoryDropdown.append($('<option></option>').val(category).html(category));
//        })
//
//        var productDropdown = $('#inputProductNameSearch');
//              productDropdown.empty();
//              productDropdown.append($('<option></option>').val('').html('Select an option'));
//              $.each(productNameData, function (i, product){
//                  productDropdown.append($('<option></option>').val(product).html(product));
//              })
//        var barcodeDropdown = $('#inputBarcodeSearch');
//                barcodeDropdown.empty();
//                barcodeDropdown.append($('<option></option>').val('').html('Select an option'));
//                $.each(barcodeData, function (i, barcode){
//                    barcodeDropdown.append($('<option></option>').val(barcode).html(barcode));
//                })
}


function searchInventory() {
  console.log($("#inputBrandSearch").val(), $("#inputCategorySearch").val());
    var brand = $("#inputBrandSearch").val();
    var category = $("#inputCategorySearch").val();
    var name = $("#inputProductNameSearch").val();
    var barcode = $("#inputBarcodeSearch").val();

    var obj = {brand, category, name, barcode};

    var url = getInventoryUrl();
    url+='/search';

  	$.ajax({
  	   url: url,
  	   type: 'POST',
  	   data: JSON.stringify(obj),
  	   headers: {
         	'Content-Type': 'application/json'
         },
  	   success: function(response) {
                    $.notify("Filtered Inventory data","success");

  	   		displayInventoryList(response);
  	   },
  	   error: handleAjaxError
  	});
}

// FILE UPLOAD METHODS

function processData(){
   var url = getAdminInventoryUrl()+'/upload';
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
                    $.notify("Uploaded the file successfully","success");
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
		var buttonHtml = '<button class="btn btn-primary mr-2" onclick="displayEditInventory(' + e.id + ')" ><span class="material-symbols-outlined">border_color</span></button>' ;

          table.row.add([
            e.barcode,
            e.brand,
            e.category,
            e.name,
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
    $('#download-errors').prop("disabled", true);
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
                   "info": false,
                   scrollY: '400px',
                   scrollColapse: 'true',
                   });

   if(user_role == 'standard'){
     table.column(3).visible(false);
    }
} ;


function init(){
    initDatatable();
	$('#update-inventory').click(updateInventory);
    $('#search-inventory').click(searchInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
//	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

