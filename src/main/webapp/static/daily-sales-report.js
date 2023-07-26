var daySalesData;
var daySalesReportData;
var table;
function getDailySalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/reports/day-sales";
}

//BUTTON ACTIONS

function getDaySalesReport(){
	var url = getDailySalesUrl();
	callAjaxApi(url, 'GET', null, null, getDaySalesReportSuccess);
}

function getDaySalesReportSuccess(data){
	        daySalesData = data;
	   		displayDaySalesReport(data);
	   		daySalesReportData = data;
}
//UI DISPLAY METHODS

function displayDaySalesReport(data){
	var $tbody = $('#daily-sales-report-table').find('tbody');

	table.clear().draw();
   console.log(table);
	for(var i in data){
		var e = data[i];
          table.row.add([
            e.date,
            e.invoicedOrdersCount,
            e.invoicedItemsCount,
            e.totalRevenue.toFixed(2)
          ]);
	}
	table.draw();

}

function filterSalesReport() {
 var startDateInput = $("#inputStartDate").val();
 var endDateInput= $("#inputEndDate").val();


 if(startDateInput =='' && endDateInput == ''){
 displayDaySalesReport(daySalesData);

 return;
 }
 var startDate = new Date(startDateInput);
 var endDate = endDateInput == '' ? Date.now() : new Date(endDateInput);

   // Filter the array based on the start and end dates
   var filteredData = daySalesData.filter(item => {
     var date = new Date(item.date);
     return date >= startDate && date <= endDate;
   });
   displayDaySalesReport(filteredData);
   $.notify("Filtered Data", "success");

}

function resetFilters(){
$("#inputStartDate").val('');
$("#inputEndDate").val('');
$("#inputEndDate").prop("disabled", true);
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
 }

}

function downloadReport(){
if(daySalesReportData.length == 0){
$.notify("No sales report data, cannot download report");
return;};


var reportArrayData = [];

for(i in daySalesReportData){
var rowData = {};
rowData.date = daySalesReportData[i].date;
rowData["total orders"] = daySalesReportData[i].invoicedOrdersCount;
rowData["total items count"] = daySalesReportData[i].invoicedItemsCount,

reportArrayData.push(rowData);
}

writeFileData(reportArrayData, 'daily-sales-report.tsv');
}
function init(){
     table = $('#daily-sales-report-table').DataTable(
    	              {dom: 'lrtip',
                       paging: false,
                       "info": false,
                       scrollY: '450px',
                       scrollColapse: 'true',
                       order: [[0,'desc']]
                       });
    $("#filter-day-sales").click(filterSalesReport);
    $("#inputStartDate").on('change', onInputStartDateChange);
	$('#download-report').click(downloadReport);
    $('#reset-filters').click(resetFilters);

}
$(document).ready(init);
$(document).ready(getDaySalesReport);

