var brandCategoryList=[];
var brandList=[];
var table ;
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

function getProductAdminUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/product";
}

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

function addDropdownForEditProduct(data){

console.log("in add product header click");
var brandDropdown = $('#inputBrandEdit');
brandDropdown.select2({data: brandList});

 brandDropdown.val(data.brand).trigger('change');

    var selectedValue = data.brand;
    var categoryDropdown = $('#inputCategoryEdit');

      // Enable the second dropdown and make an AJAX call to fetch data
      categoryDropdown.prop('disabled', false);
      var categoryList = brandCategoryList.filter(function(data){
      return data.brand == selectedValue;
      });

            var categoryArray =[];

            	for(var i in categoryList){
            		var e = categoryList[i];
            		categoryArray.push(e.category);
            	}
                  console.log(categoryList);
            //      categoryDropdown.empty();
                  categoryDropdown.select2({
                  data: categoryArray})

     categoryDropdown.val(data.category).trigger('change');

}

function addProductHeaderClick(){

var brandDropdown = $('#inputBrand');
brandDropdown.select2({data: brandList});
$("#inputCategory").select2({data:[]});
}


$(document).ready(function() {
  // When the first dropdown changes
  $('#inputBrand').change(function() {
    var selectedValue = $(this).val();
    var categoryDropdown = $('#inputCategory');


    if (selectedValue != '' && selectedValue != null) {
      // Enable the second dropdown and make an AJAX call to fetch data
      categoryDropdown.prop('disabled', false);
      var categoryList = brandCategoryList.filter(function(data){
      return data.brand == selectedValue;
      });
      var categoryArray =[];

	for(var i in categoryList){
		var e = categoryList[i];
		categoryArray.push(e.category);
	}
      categoryDropdown.empty();
      categoryDropdown.select2({
      placeholder:'Select an Option',
      data: categoryArray});
      categoryDropdown.val(null).trigger('change');


    } else {
      categoryDropdown.val('').trigger('change');
      categoryDropdown.prop('disabled', true);
    }
  });
});






$(document).ready(function() {
  // When the first dropdown changes
  $('#inputBrandEdit').change(function() {
    var selectedValue = $(this).val();
    var categoryDropdown = $('#inputCategoryEdit');

    if (selectedValue != '' && selectedValue != null) {
      // Enable the second dropdown and make an AJAX call to fetch data
      categoryDropdown.prop('disabled', false);
      var categoryList = brandCategoryList.filter(function(data){
      return data.brand == selectedValue;
      });

      var categoryArray =[];

      	for(var i in categoryList){
      		var e = categoryList[i];
      		categoryArray.push(e.category);
      	}
      categoryDropdown.empty();

      categoryDropdown.select2({
      placeholder:'Select an Option',
      data: categoryArray});

      categoryDropdown.val('').trigger('change');

    } else {
            categoryDropdown.val('').trigger('change');
      categoryDropdown.prop('disabled', true);
    }
  });
});


//BUTTON ACTIONS
function addProduct(event){
	//Set the values to update
	    var isValid = $("#product-form")[0].checkValidity();
        if(!isValid){
        $("#product-form")[0].reportValidity();
        return;
        }
	var $form = $("#product-form");
	var json = toJson($form);
	var url = getProductAdminUrl();
	callAjaxApi(url, 'POST', json, "Product added", addProductSuccess);

	return false;
}
function addProductSuccess(data){
	   		getProductList();

	   		$("#product-form")[0].reset();
	        $('#addProduct').modal('toggle');
}

function updateProductDetails(event){

	    var isValid = $("#product-edit-form")[0].checkValidity();
        if(!isValid){
        $("#product-edit-form")[0].reportValidity();
        return;
        }

	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductAdminUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);
console.log(json);
   callAjaxApi(url, 'PUT', json, 'Product details updated', updateProductDetailsSuccess);
}

function updateProductDetailsSuccess(data){
	   		getProductList();
	$('#edit-product-modal').modal('toggle');
}


function getProductList(){
	var url = getProductUrl();
	callAjaxApi(url, 'GET', null, null, getProductListSuccess );
}

function getProductListSuccess(data){
    updateSearchList(data);
    displayProductList(data);
    }


// Add to brandList array and brandCategoryList Arrray
function setBrandCategory(data) {
    brandList = [];
    brandCategoryList = [];

	for(var i in data){
		var e = data[i];
		brandList.push(e.brand);
		brandCategoryList.push({
		brand: e.brand,
		category: e.category});
	}
	brandList = [...new Set(brandList)];

}

function getBrandList(){
	var url = getBrandUrl();
	callAjaxApi(url, 'GET', null, null, setBrandCategory);
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
	data: brandData,
	width: '160px',
	})
    $("#inputCategorySearch").select2({
    data: categoryData,
    width: '160px',})

    $("#inputProductNameSearch").select2({
    data: productNameData,
    width: '160px',})
    $("#inputBarcodeSearch").select2({
    data: barcodeData,
    width: '160px',})
}

function resetFilters(){
$("#inputBarcodeSearch").val('').trigger('change');
$("#inputProductNameSearch").val('').trigger('change');
$("#inputBrandSearch").val('').trigger('change');
$("#inputCategorySearch").val('').trigger('change');

}

function searchProduct() {
  console.log($("#inputBrandSearch").val(), $("#inputCategorySearch").val());
    var brand = $("#inputBrandSearch").val();
    var category = $("#inputCategorySearch").val();
    var name = $("#inputProductNameSearch").val();
    var barcode = $("#inputBarcodeSearch").val();

    var obj = {brand, category, name, barcode};

    var url = getProductUrl();
    url+='/search';
    callAjaxApi(url, 'POST', JSON.stringify(obj), "Filtered Data", displayProductList);
}
// FILE UPLOAD METHOD

function processData(){
   var url = getProductAdminUrl()+'/upload';
    var fileUpload = document.getElementById("productFile");

    if (fileUpload .value != null) {
        var files = $("#productFile").get(0).files;
        // Add the uploaded file content to the form data collection
        console.log(files);
        if (files.length > 0) {
    var formTag = $("#import-product-form")[0];
    var formData = new FormData(formTag);
    formData.append("file", files[0]);
            $.ajax({
                url: url,
                data:formData,
                type:"POST",

                // Tell jQuery not to process data or not to worry about content-type
                // You *must* include these options in order to send MultipartFile objects
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',

                success:function(data){
                    console.log(data);
                    $.notify("Uploaded the file successfully","success");
                    getProductList();
    	          $('#upload-product-modal').modal('toggle');

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

function displayProductList(data){
	table.clear().draw();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button class="btn btn-warning mr-2" onclick="displayEditProduct(' + e.id + ')" ><span class="material-symbols-outlined">border_color</span></button>' ;
          table.row.add([
            e.barcode,
            e.brand,
            e.category,
            e.name,
            e.mrp,
            buttonHtml
          ]);
	}
	table.draw();

}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	callAjaxApi(url, 'GET', null, null, displayProduct);
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
var fileName = $file.val().split('\\').pop();

	$('#productFileName').html(fileName);
    $('#download-errors').prop("disabled", true);

}

function displayUploadData(){
 	resetUploadDialog();
 	 	callAjaxApi("/error/exists/product-upload-error.tsv", "GET", null, null, function(data){
     	errorOnUpload();
     	})
	$('#upload-product-modal').modal('toggle');
}


function displayProduct(data){
    addDropdownForEditProduct(data);
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form select[name=brand]").val(data.brand);
	$("#product-edit-form select[name=category]").val(data.category);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}


//INITIALIZATION CODE

 function initDatatable() {
    table = $('#product-table').DataTable(
                    {dom: 'lrtip',
                    paging: false,
                    "info": false,
                    scrollY: '400px',
                    scrollColapse: 'true',
                    }
    );
    console.log(user_role);
    if(user_role == 'standard'){
           console.log("making the first colmn hidden");
            table.column(5).visible(false);
     }
} ;


function init(){
    initDatatable();
    $('#add-product-header').click(addProductHeaderClick);
	$('#add-product').click(addProduct);
	$('#search-product').click(searchProduct);
	$('#update-product').click(updateProductDetails);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
//	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#reset-filters').click(resetFilters);

}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(getBrandList);

