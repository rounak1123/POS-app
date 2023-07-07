function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}


function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}

function downloadInvoice(){
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');
    console.log(orderId);

    // Get a specific query parameter by name
    var url = getOrderUrl();
    url+= '/download/'+ orderId;
    $.ajax({
        url: url,
        method: "GET",
        xhrFields: {
            responseType: 'blob' // Set the response type to blob
        },
        success: function(data) {
            var downloadLink = document.createElement('a');
            var url = window.URL.createObjectURL(data);
            downloadLink.href = url;
            downloadLink.download = "invoice_"+orderId+".pdf";
            downloadLink.click();
            window.URL.revokeObjectURL(url);
        },
        error: function(xhr, status, error) {
            console.error(error);
        }
    });
}

function getOrderItemList(){
	var url = getOrderItemUrl();
	const currentUrl = window.location.href;

    // Create a URLSearchParams object with the query parameters
    const urlParams = new URLSearchParams(window.location.search);

    // Get a specific query parameter by name
    const orderId = urlParams.get('orderId');

    // Use the retrieved query parameters

	var url = getOrderItemUrl() + "/order/" + orderId;

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('order item list', data);
	   		displayOrderItemList(data);
	   },
	   error: handleAjaxError
	});
}


//UI DISPLAY METHODS

function displayOrderItemList(data){
	var $tbody = $('#order-item-table').find('tbody');
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
          table.row.add([
            e.barcode,
            e.quantity,
            e.sellingPrice
          ]).draw();
	}

}

//INITIALIZATION CODE
var table;

function initDatatable(){
            table = $('#order-item-table').DataTable(
                            {dom: 'lrtip',
                             paging: false,
                             scrollY: '450px',
                             scrollColapse: 'true',
                             }
            );

}

function init(){
    initDatatable();
    $("#download-invoice").click(downloadInvoice);
}

$(document).ready(init);
$(document).ready(getOrderItemList);

