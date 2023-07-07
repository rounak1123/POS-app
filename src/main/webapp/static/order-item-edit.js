var orderId;
function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}

//BUTTON ACTIONS
function addOrderItem(event){
	//Set the values to update
	var $form = $("#order-item-form");
	var json = toJson($form);
	var url = getOrderItemUrl();
    var barcode = $("#inputBarcode").val();
    var quantity = $("#inputQuantity").val();
    var sellingPrice = $("#inputSellingPrice").val();
    var obj = {
    barcode,
    quantity,
    sellingPrice,
    orderId
    }
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: JSON.stringify(obj),
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getOrderItemList();
	   		$("#order-item-form")[0].reset();
	   },
	   error: handleAjaxError
	});

	return false;
}

function updateOrderItem(event){
	$('#order-item-edit-modal').modal('toggle');
	//Get the ID
	var id = $("#order-item-edit-form input[name=id]").val();
	var url = getOrderItemUrl() + "/" + id;
	//Set the values to update
	var $form = $("#order-item-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getOrderItemList();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getOrderItemList(){
	var url = getOrderItemUrl();
	const currentUrl = window.location.href;

    // Create a URLSearchParams object with the query parameters
    const urlParams = new URLSearchParams(window.location.search);

    // Get a specific query parameter by name
     orderId = urlParams.get('orderId');

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
		var buttonHtml = ' <button onclick="displayEditOrderItem(' + e.id + ')" class="btn btn-primary"><i class="fas fa-edit fa-sm"></i></button>'
            buttonHtml += ' <button onclick="deleteOrderItem(' + e.id + ')" class="btn btn-danger ml-2"><i class="fas fa-trash fa-sm"></i></button>'
          table.row.add([
            e.barcode,
            e.quantity,
            e.sellingPrice,
            buttonHtml
          ]).draw();
	}

}

function deleteOrderItem(id){

    var url = getOrderItemUrl();
    url+= '/' + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getOrderItemList();
	   },
	   error: handleAjaxError
	});

}

function displayEditOrderItem(id){
	var url = getOrderItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderItem(data);
	   },
	   error: handleAjaxError
	});
}


function displayOrderItem(data){
	$("#order-item-edit-form input[name=barcode]").val(data.barcode);
	$("#order-item-edit-form input[name=quantity]").val(data.quantity);
	$("#order-item-edit-form input[name=sellingPrice]").val(data.sellingPrice);
	$("#order-item-edit-form input[name=id]").val(data.id);
	$('#order-item-edit-modal').modal('toggle');
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
	$('#add-order-item').click(addOrderItem);
	$('#update-order-item').click(updateOrderItem);
	$('#refresh-data').click(getOrderItemList);
}

$(document).ready(init);
$(document).ready(getOrderItemList);

