var brandData = [];
var categoryData = [];

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
function getAdminBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/brand";
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
    var isValid = $("#brand-form")[0].checkValidity();
    if(!isValid){
      $("#brand-form")[0].reportValidity();
         return;
    }
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getAdminBrandUrl();
    callAjaxApi(url,'POST', json, "Added Brand Category combination", addBrandSuccess);
	return false;
}

function addBrandSuccess(data){
	   		getBrandList();
	      $('#add-brand-modal').modal('toggle');

	   		$("#brand-form")[0].reset();
}
function updateBrand(event){
	//Get the ID

	    var isValid = $("#brand-edit-form")[0].checkValidity();
        if(!isValid){
          $("#brand-edit-form")[0].reportValidity();
             return;
        }

	var id = $("#brand-edit-form input[name=id]").val();
	var url = getAdminBrandUrl() + "/" + id;
	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);
    callAjaxApi(url, 'PUT', json, "Updated brand category", function(data){
    getBrandList();
    	$('#edit-brand-modal').modal('toggle');
});

}

function getBrandList(){
	var url = getBrandUrl();
	callAjaxApi(url, 'GET', null, null,function(data){
		   		displayBrandList(data);
    	   		updateBrandCategoryList(data);
	});
}


// FILE UPLOAD METHODS

function processData(){
var url = getAdminBrandUrl()+'/upload';


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
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST',

                success:function(data){
                    console.log(data);
                    $.notify("Uploaded the file successfully","success");
                   getBrandList();
	               $('#upload-brand-modal').modal('toggle');
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
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
		var buttonHtml = '<button class="btn btn-warning mr-2 " onclick="displayEditBrand(' + e.id + ')" ><span class="material-symbols-outlined">border_color</span></button>' ;


          table.row.add([
            serialNo,
            e.brand,
            e.category,
            buttonHtml
          ]);
	}
	table.draw();

}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	callAjaxApi(url, 'GET', null, null, displayBrand);
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
var fileName = $file.val().split('\\').pop();
	$('#brandFileName').html(fileName);
    $('#download-errors').prop("disabled", true);
}

function displayUploadData(){
 	resetUploadDialog();
 	callAjaxApi("/error/exists/brand-upload-error.tsv", "GET", null, null, function(data){
 	errorOnUpload();
 	})
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=id]").val(data.id);
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$('#edit-brand-modal').modal('toggle');
}

function updateBrandCategoryList(data) {

    brandData = [];
    categoryData = [];

	for(var i in data){
		var e = data[i];
		brandData.push(e.brand);
		categoryData.push(e.category);
	}

	brandData = [...new Set(brandData)];
	categoryData = [...new Set(categoryData)];
     $('#inputBrandSearch').select2({
     data: brandData,
     width: '160px',
     })
      $('#inputCategorySearch').select2({
      data: categoryData,
      width: '160px',
      })
}
function openFilterModal() {
	var url = getBrandUrl();
	callAjaxApi(url, 'GET', null, null, updateBrandCategoryList);

}

function resetFilters(){
$("#inputBrandSearch").val('').trigger('change');
$("#inputCategorySearch").val('').trigger('change');

}


//INITIALIZATION CODE
var table;

function searchBrandCategory() {
console.log(table);
table.draw();
        $('#searchBrand').modal('toggle');
}

function searchBrandCategoryDropdown() {
  console.log($("#inputBrandSearch").val(), $("#inputCategorySearch").val());
    var brand = $("#inputBrandSearch").val();
    var category = $("#inputCategorySearch").val();
    var obj = {brand, category};

    var url = getBrandUrl();
    url+='/search';
    callAjaxApi(url, 'POST', JSON.stringify(obj), "Filtered data", displayBrandList);
}

function initDatatable(){
            table = $('#brand-table').DataTable(
              {dom: 'lrtip',
               paging: false,
               "info": false,
               scrollY: '450px',
               scrollColapse: 'true',
               }
            );
        if(user_role == 'standard'){
        table.column(3).visible(false);
        }
}


function init(){
    initDatatable();
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
//	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
    $('#search-brand-category').click(searchBrandCategoryDropdown);
    $('#filter-data').click(openFilterModal);
    $('#reset-filters').click(resetFilters);
}

$(document).ready(init);
$(document).ready(getBrandList);

