function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}

//BUTTON ACTIONS



function getOrderList(){
	var url = getOrderUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('order list', data);
	   		displayOrderList(data);
	   },
	   error: handleAjaxError
	});
}

function editOrder(id){
 window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order-item/edit?orderId='+id;
}

function viewOrder(id){
 window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order-item/view?orderId='+id;
}

function downloadGeneratedInvoice(id){
var url = getOrderUrl();
url+= '/download/'+ id;
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
        downloadLink.download = "invoice_"+id+".pdf";
        downloadLink.click();
        window.URL.revokeObjectURL(url);
    },
    error: function(xhr, status, error) {
        console.error(error);
    }
});
}

function generateInvoice(id){
	var url = getOrderUrl();
	url+='/place/'+id;
	$.ajax({
	   url: url,
	   type: 'PUT',
	   success: function(data) {
	   getOrderList();
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS
var table;
function displayOrderList(data){

	var $tbody = $('#order-view-table').find('tbody');
	table.clear().draw();

	for(var i in data){
		var e = data[i];

		var buttonHtml ;
		if(e.status == 'Active'|| e.status == 'active' || e.status == null){
		buttonHtml = '<button class="btn btn-primary mr-2" ><i class="fa fa-edit fa-sm onclick="editOrder(' + e.id + ')"></i></button>';
		buttonHtml+= '<button class="btn btn-primary" onclick="generateInvoice(' + e.id + ')">Generate <i class="fa fa-folder-plus"></i></button>';
		}
		else{
		buttonHtml = '<button class="btn btn-primary mr-2" ><i class="fa fa-eye fa-sm onclick="viewOrder(' + e.id + ')"></i></button>';
		buttonHtml+= '<button class="btn btn-success" onclick="downloadGeneratedInvoice(' + e.id + ')">Download <i class="fa fa-download fa-sm"></i></button>';
		}
          console.log(e);
          table.row.add([
            e.id,
            e.dateTime,
            e.status,
            buttonHtml
          ]).draw();
	}

}


//INITIALIZATION CODE



function init(){
    table = $('#order-view-table').DataTable(
              {dom: 'lrtip'}
            );
    getOrderList();
}

$(document).ready(init);
//$(document).ready(getOrderList);

