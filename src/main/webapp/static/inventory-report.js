var brandData = [];
var categoryData = [];
var inventoryReportData = [];

var table;
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}
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
function downloadReport(){
console.log(inventoryReportData);
if(inventoryReportData.length == 0){
$.notify("No Inventory report data, cannot download report");
return;};


var reportArrayData = [];

for(i in inventoryReportData){
var rowData = {};
rowData.brand = inventoryReportData[i].brand;
rowData.category = inventoryReportData[i].category;
rowData.quantity = inventoryReportData[i].quantity;
reportArrayData.push(rowData);
}

writeFileData(reportArrayData);
}
//BUTTON ACTIONS
function getInventoryList(){
	var $form = $("#inventory-report-filter-form");
	var json = toJson($form);
	var url = getInventoryUrl();
	url+='/report';
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
	        console.log('inventory list', data);
            inventoryReportData = data;
	        updateBrandCategoryList(data);
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function searchBrandCategoryDropdown() {
 getInventoryList();
$.notify("Filter Applied","success");
}

//UI DISPLAY METHODS

function displayInventoryList(data){

	table.clear().draw();

	for(var i in data){
		var e = data[i];
          table.row.add([
            e.brand,
            e.category,
            e.quantity
          ]).draw();
	}
}

//INITIALIZATION CODE
function initDataTable(){

  table = $('#inventory-table').DataTable(
		               {dom: 'lrtip',
                        paging: false,
                        scrollY: '450px',
                        scrollColapse: 'true',
                        });

}
function init(){
     initDataTable();
    $('#filter-brand-category').click(searchBrandCategoryDropdown);
	$('#download-report').click(downloadReport);


}

$(document).ready(init);
$(document).ready(getInventoryList);

