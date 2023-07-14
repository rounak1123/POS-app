var brandCategoryList=[];
var brandList=[];
var table ;
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

function addDropdownForEditProduct(data){

console.log("in add product header click");
var brandDropdown = $('#inputBrandEdit');
brandDropdown.empty();
brandDropdown.append($('<option></option>').val('').html('Select an option'));
$.each(brandList, function (i, brand){
    brandDropdown.append($('<option></option>').val(brand).html(brand));
})

 brandDropdown.val(data.brand);

    var selectedValue = data.brand;
    var categoryDropdown = $('#inputCategoryEdit');

      // Enable the second dropdown and make an AJAX call to fetch data
      categoryDropdown.prop('disabled', false);
      var categoryList = brandCategoryList.filter(function(data){
      return data.brand == selectedValue;
      });
      console.log(categoryList);
      categoryDropdown.empty();
      categoryDropdown.append($('<option></option>').val('').html('Select an option'));

      $.each(categoryList, function(key,value){
      categoryDropdown.append($('<option></option>').val(value.category).html(value.category));
      })
     categoryDropdown.val(data.category);

}

function addProductHeaderClick(){
console.log("in add product header click");
var brandDropdown = $('#inputBrand');
brandDropdown.empty();
brandDropdown.append($('<option></option>').val('').html('Select an option'));
$.each(brandList, function (i, brand){
    brandDropdown.append($('<option></option>').val(brand).html(brand));
})
console.log(brandList,brandDropdown);
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
      console.log(categoryList);
      categoryDropdown.empty();
      categoryDropdown.append($('<option></option>').val('').html('Select an option'));

      $.each(categoryList, function(key,value){
      categoryDropdown.append($('<option></option>').val(value.category).html(value.category));
      })

    } else {
      categoryDropdown.val('');
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
      console.log(categoryList);
      categoryDropdown.empty();
      categoryDropdown.append($('<option></option>').val('').html('Select an option'));

      $.each(categoryList, function(key,value){
      categoryDropdown.append($('<option></option>').val(value.category).html(value.category));
      })

    } else {
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
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();

	   		$("#product-form")[0].reset();
	        $('#addProduct').modal('toggle');

	   		$.notify("Product added", "success");
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateProductDetails(event){

	    var isValid = $("#product-edit-form")[0].checkValidity();
        if(!isValid){
        $("#product-edit-form")[0].reportValidity();
        return;
        }

	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);
console.log(json);
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   $.notify("Product details updated","success");
	   		getProductList();
	$('#edit-product-modal').modal('toggle');

	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('product list', data);
	        updateSearchList(data);
	   		displayProductList(data);
	   },
	   error: handleAjaxError
	});
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
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	     setBrandCategory(data);
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

	  var brandDropdown = $('#inputBrandSearch');
      brandDropdown.empty();
      brandDropdown.append($('<option></option>').val('').html('Select an option'));
      $.each(brandData, function (i, brand){
          brandDropdown.append($('<option></option>').val(brand).html(brand));
      })
        var categoryDropdown = $('#inputCategorySearch');
        categoryDropdown.empty();
        categoryDropdown.append($('<option></option>').val('').html('Select an option'));
        $.each(categoryData, function (i, category){
            categoryDropdown.append($('<option></option>').val(category).html(category));
        })

        var productDropdown = $('#inputProductNameSearch');
              productDropdown.empty();
              productDropdown.append($('<option></option>').val('').html('Select an option'));
              $.each(productNameData, function (i, product){
                  productDropdown.append($('<option></option>').val(product).html(product));
              })
        var barcodeDropdown = $('#inputBarcodeSearch');
                barcodeDropdown.empty();
                barcodeDropdown.append($('<option></option>').val('').html('Select an option'));
                $.each(barcodeData, function (i, barcode){
                    barcodeDropdown.append($('<option></option>').val(barcode).html(barcode));
                })
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

  	$.ajax({
  	   url: url,
  	   type: 'POST',
  	   data: JSON.stringify(obj),
  	   headers: {
         	'Content-Type': 'application/json'
         },
  	   success: function(response) {
  	        $.notify("Filtered Data","success");
  	   		displayProductList(response);
  	   		$("#product-search-form")[0].reset();
  	   },
  	   error: handleAjaxError
  	});
}
// FILE UPLOAD METHOD

function processData(){
   var url = getProductUrl()+'/upload';
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
                    getProductList();

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
		var buttonHtml = '<button class="btn btn-primary mr-2" onclick="displayEditProduct(' + e.id + ')" ><span class="material-symbols-outlined">border_color</span></button>' ;
          table.row.add([
            e.barcode,
            e.brand,
            e.category,
            e.name,
            e.mrp.toFixed(2),
            buttonHtml
          ]).draw();
	}
}

function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);
	   },
	   error: handleAjaxError
	});	
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
                    scrollY: '400px',
                    scrollColapse: 'true',
                    }
    );
    console.log(user_role);
    if(user_role == 'standard'){
           console.log("making the first colmn hidden");
            table.column(6).visible(false);
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
    $('#productFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(getBrandList);

