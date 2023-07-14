var orderItemTable;
var table;

function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
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
// window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order-item/view?orderId='+id;
	$('#order-item-view-modal').modal('toggle');
	getOrderItemList(id);


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
        $.notify("Invoice downloaded.","success");

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
       $.notify("Order placed and invoice generated.","success");

	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS
function displayOrderList(data){

	var $tbody = $('#order-view-table').find('tbody');
	table.clear().draw();

	for(var i in data){
		var e = data[i];


		var buttonHtml ;
		if(e.status == 'Active'|| e.status == 'active' || e.status == null){
		buttonHtml = '<button class="btn btn-primary mr-2" onclick="viewOrder(' + e.id + ')"><i class="fa fa-eye fa-sm" ></i></button><button class="btn btn-primary mr-2" onclick="editOrder(' + e.id + ')" ><span class="material-symbols-outlined">border_color</span></button>' ;
		buttonHtml+= '<button class="btn btn-success" onclick="generateInvoice(' + e.id + ')">Generate <i class="fa fa-folder-plus"></i></button>';
		}
		else{
		buttonHtml = '<button class="btn btn-primary mr-2" onclick="viewOrder(' + e.id + ')"><i class="fa fa-eye fa-sm" ></i></button><button class="btn btn-primary mr-2" onclick="editOrder(' + e.id + ')" disabled><span class="material-symbols-outlined">border_color</span></button>';
		 buttonHtml += '<button class="btn btn-success" onclick="downloadGeneratedInvoice(' + e.id + ')">Download <i class="fa fa-download fa-sm"></i></button>';
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

function getOrderItemList(orderId){
	var url = getOrderItemUrl();

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


function displayOrderItemList(data){
	orderItemTable.clear().draw();

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
          orderItemTable.row.add([
            e.barcode,
            e.name,
            e.quantity,
            e.sellingPrice.toFixed(2)
          ]).draw();
	}

}


//INITIALIZATION CODE



function init(){
    table = $('#order-view-table').DataTable(
              {dom: 'lrtip',
               paging: false,
               scrollY: '450px',
               scrollColapse: 'true',
               order: [[1,'desc']],
               }
            );

        orderItemTable = $('#order-item-view-table').DataTable(
                  {dom: 'lrtip',
                   paging: false,
                   scrollY: '300px',
                   scrollColapse: 'true',
                   }
                );
    getOrderList();
}

$(document).ready(init);
//$(document).ready(getOrderList);

