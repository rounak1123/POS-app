
function getDailySalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/day-sales";
}

//BUTTON ACTIONS

function getDaySalesReport(){
	var url = getDailySalesUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('day sales report', data);
	   		displayDaySalesReport(data);
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS

function displayDaySalesReport(data){
	var $tbody = $('#daily-sales-report-table').find('tbody');
	var table = $('#daily-sales-report-table').DataTable(
	              {dom: 'lrtip',
                   paging: false,
                   scrollY: '450px',
                   scrollColapse: 'true',
                   });
	table.clear().draw();
   console.log(table);
	for(var i in data){
		var e = data[i];
          table.row.add([
            e.date,
            e.invoicedOrdersCount,
            e.invoicedItemsCount,
            e.totalRevenue
          ]).draw();
          console.log(e.date,e.invoiced_items_count, e.invoiced_orders_count, e.total_revenue)
	}
}

$(document).ready(getDaySalesReport);

