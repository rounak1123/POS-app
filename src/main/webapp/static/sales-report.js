var table;
var salesReportData = [];
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/reports/sales";
}


function updateSearchList(data){
    brandData = [];
    categoryData = [];

	for(var i in data){
		var e = data[i];
		brandData.push(e.brand);
		categoryData.push(e.category);
	}
	brandData = [...new Set(brandData)];
	categoryData = [...new Set(categoryData)];

	$("#inputBrand").select2({
	data: brandData})
    $("#inputCategory").select2({
    data: categoryData})
}

function onInputStartDateChange() {
 var startDateInput = $("#inputStartDate").val();
 var endDateInput= $("#inputEndDate").val();
  var startDate = new Date(startDateInput);
  var endDate = endDateInput == '' ? Date.now() : new Date(endDateInput);

 if(startDateInput != ''){
 $('#inputEndDate').prop("disabled", false);

 if(endDate < startDate)
 $('#inputEndDate').val('');

 $('#inputEndDate').prop('min', startDateInput);

 }else {
 $("#inputEndDate").val('');
 $('#inputEndDate').prop("disabled", true);

 }

}

function getSalesReport(form){

	var $form = $("#sales-filter-form");
	var json = toJson($form);
	var url = getSalesReportUrl();

    console.log(json);
    callAjaxApi(url, 'POST', json, null, getSalesReportSuccess);
}

function getSalesReportSuccess(data){
	     salesReportData=data;
	        updateSearchList(data);
          displaySalesReports(data);
}

//Display Report

function displaySalesReports(data){

	table.clear().draw();
	for(var i in data){
		var e = data[i];
          table.row.add([
            e.brand,
            e.category,
            e.quantity,
            e.revenue.toFixed(2)
          ]);
	}
	table.draw();

}

function filterSales(){
getSalesReport();
}

function resetFilters(){
$("#inputStartDate").val('');
$("#inputEndDate").val('');
$("#inputBrand").val('').trigger('change');
$("#inputCategory").val('').trigger('change');

}

function downloadReport(){
console.log(salesReportData);
if(salesReportData.length == 0){
$.notify("No sales report data, cannot download report");
return;};


var reportArrayData = [];

for(i in salesReportData){
var rowData = {};
rowData.brand = salesReportData[i].brand;
rowData.category = salesReportData[i].category;
rowData.quantity = salesReportData[i].quantity;
rowData.revenue = salesReportData[i].revenue;
reportArrayData.push(rowData);
}

writeFileData(reportArrayData, 'sales-report.tsv');
}

function init(){
    getSalesReport();
     table = $('#sales-table').DataTable(
                   {dom: 'lrtip',
                    paging: false,
                    "info": false,
                    scrollY: '360px',
                    scrollColapse: 'true',
                    });
	$('#filter-sales').click(filterSales);
    $("#inputStartDate").on('change', onInputStartDateChange);
	$('#download-report').click(downloadReport);
    $('#reset-filters').click(resetFilters);

}

$(document).ready(init);
//$(document).ready(getSalesReport);

