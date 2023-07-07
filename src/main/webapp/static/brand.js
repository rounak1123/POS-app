var brandData = [];
var categoryData = [];

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
	   		updateBrandCategoryList(data);
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
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
		var buttonHtml = '<button class="btn btn-primary mr-2 " onclick="displayEditBrand(' + e.id + ')" ><i class="fa fa-edit fa-sm text-white" ></i></button>' ;


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
var fileName = $file.val().split('\\').pop();
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

	  var brandDropdown = $('#inputBrandSearch');
      brandDropdown.empty();
      brandDropdown.append($('<option></option>').val('').html('Select an option'));
      $.each(brandData, function (i, brand){
          brandDropdown.append($('<option></option>').val(brand).html(brand));
      })
        var categoryDropdown = $('#inputCategorySearch');
        categoryDropdown.empty();
        categoryDropdown.append($('<option></option>').val('').html('Select an option'));
        $.each(categoryData, function (i, brand){
            categoryDropdown.append($('<option></option>').val(brand).html(brand));
        })
}
function openFilterModal() {
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('brand list', data);
	   		updateBrandCategoryList(data);
	   },
	   error: handleAjaxError
	});

}


//INITIALIZATION CODE
var table;

function searchBrandCategory() {
//var brandSearch = $('#inputBrandSearch');
//var categorySearch = $('#inputCategorySearch')
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

  	$.ajax({
  	   url: url,
  	   type: 'POST',
  	   data: JSON.stringify(obj),
  	   headers: {
         	'Content-Type': 'application/json'
         },
  	   success: function(response) {
  	   		displayBrandList(response);
  	   		$("#brand-search-form")[0].reset();
  	   },
  	   error: handleAjaxError
  	});
}

function initDatatable(){
            table = $('#brand-table').DataTable(
              {dom: 'lrtip',
               paging: false,
               scrollY: '450px',
               scrollColapse: 'true',
               }
            );
        if(user_role == 'standard'){
        table.column(3).visible(false);
        }

        // Custom range filtering function
        $.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
            var brand = $('#inputBrandSearch').val().toLowerCase().trim();
            var category= $('#inputCategorySearch').val().toLowerCase().trim();

            var brandCheck = data[1]; // use data for the age column
            var categoryCheck = data[2];
            console.log('in searching');
            if (
                (brand=="" && category=="") ||
                (brandCheck.includes(brand) && category=="") ||
                (categoryCheck.includes(category) && brand=="") ||
                (brandCheck.includes(brand) && categoryCheck.includes(category))

            ) {
                return true;
            }

            return false;
        });
//$('table'). dataTable({dom: 'lrtip'});

}

//$(document).ready(function() {
//  // When the first dropdown changes
//  var brandDropdown = $('#inputBrandSearch');
//  brandDropdown.empty();
//  brandDropdown.append($('<option></option>').val('').html('Select an option'));
//  $.each(brandData, function (i, brand){
//      brandDropdown.append($('<option></option>').val(brand).html(brand));
//  })
//
//    var categoryDropdown = $('#inputCategorySearch');
//    categoryDropdown.empty();
//    categoryDropdown.append($('<option></option>').val('').html('Select an option'));
//    $.each(categoryData, function (i, brand){
//        brandDropdown.append($('<option></option>').val(brand).html(brand));
//    })
//


//  $('#inputBrandSearch').change(function() {
//    var selectedValue = $(this).val();
//    var categoryDropdown = $('#inputCategory');
//
//    if (selectedValue != '' && selectedValue != null) {
//      // Enable the second dropdown and make an AJAX call to fetch data
//      categoryDropdown.prop('disabled', false);
//      var categoryList = brandCategoryList.filter(function(data){
//      return data.brand == selectedValue;
//      });
//      console.log(categoryList);
//      categoryDropdown.empty();
//      categoryDropdown.append($('<option></option>').val('').html('Select an option'));
//
//      $.each(categoryList, function(key,value){
//      categoryDropdown.append($('<option></option>').val(value.category).html(value.category));
//      })
//
//    } else {
//      categoryDropdown.prop('disabled', true);
//    }
//  });
//});


function init(){
    initDatatable();
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
    $('#search-brand-category').click(searchBrandCategoryDropdown);
    $('#filter-data').click(openFilterModal);
}

$(document).ready(init);
$(document).ready(getBrandList);

