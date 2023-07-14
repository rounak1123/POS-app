var table;
var salesReportData = [];
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/sales";
}

function getSalesReport(form){

	var $form = $("#sales-filter-form");
	var json = toJson($form);
	var url = getSalesReportUrl();

    console.log(json);

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   console.log(response);
	     salesReportData=response;
          displaySalesReports(response);
	   },
	   error: handleAjaxError
	});
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
          ]).draw();
	}
}

function filterSales(){
getSalesReport();
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

writeFileData(reportArrayData);
}

function init(){
    getSalesReport();
     table = $('#sales-table').DataTable(
                   {dom: 'lrtip',
                    paging: false,
                    scrollY: '360px',
                    scrollColapse: 'true',
                    });
	$('#filter-sales').click(filterSales);
	$('#download-report').click(downloadReport);
}

$(document).ready(init);
//$(document).ready(getSalesReport);

