var brandData = []; // Programatically-generated options array with > 5 options
var categoryData = [];

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

//BUTTON ACTIONS

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


//UI DISPLAY METHODS

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
//	 var table = $('#brand-table').DataTable({searching: false});
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
          table.row.add([
            serialNo,
            e.brand,
            e.category
          ]).draw();
	}

}

function displayBrand(data){
	$("#brand-edit-form input[name=id]").val(data.id);
	$("#brand-edit-form input[name=brand]").val(data.brand);
	$("#brand-edit-form input[name=category]").val(data.category);
	$('#edit-brand-modal').modal('toggle');
}

// DROPDOWN

function updateBrandCategoryList(data) {

    var brandData = [];
    var categoryData = [];

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


//INITIALIZATION CODE
var table;

function initDatatable(){
            table = $('#brand-table').DataTable(
              {dom: 'lrtip'}
            );

}

function init(){
    initDatatable();
    $('#search-brand-category').click(searchBrandCategoryDropdown);
}

$(document).ready(init);
$(document).ready(getBrandList);

