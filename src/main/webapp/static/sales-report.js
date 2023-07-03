var table;
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/sales";
}

function getSalesReport(form){

	var $form = $("#sales-filter-form");
	var json = toJson($form);
	var url = e=getSalesReportUrl();

    console.log(json);

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
//	   		filterSales();
       console.log(response);
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
            e.revenue
          ]).draw();
	}
}

function filterSales(){
getSalesReport();
}

function init(){
    getSalesReport();
   var $tbody = $('#sales-table').find('tbody');
     table = $('#sales-table').DataTable({dom : 'lrtip'});
	$('#filter-sales').click(filterSales);
}

$(document).ready(init);
//$(document).ready(getSalesReport);

