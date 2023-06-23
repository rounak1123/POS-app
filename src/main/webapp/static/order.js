
function getOrderTableItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}

function getOrderUrl(){
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/order";
}

function getOrderTableItemUrl() {
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/order-table-item";
}

//BUTTON ACTIONS
function addOrderItem(event){
	//Set the values to update
	var $form = $("#item-form");
	$.("#item-form").notify("hello");
	var json = toJson($form);
	var url = getOrderTableItemUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
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

function updateOrderItemDetails(event){
	$('#edit-item-modal').modal('toggle');
	//Get the ID
	var id = $("#item-edit-form input[name=id]").val();
	var url = getOrderTableItemUrl() + "/" + id;

	//Set the values to update
	var $form = $("#item-edit-form");
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
	var url = getOrderTableItemUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('Order Item list', data);
	   		displayOrderItemList(data);
	   },
	   error: handleAjaxError
	});
}

//@TODO createOrder
function placeOrder(){
 var orderUrl = getOrderUrl();

 // c

}

//UI DISPLAY METHODS

function displayOrderItemList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditItem(' + e.id + ')">edit</button>'
		var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>'  + e.sellingPrice + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditItem(id){
	var url = getOrderTableItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayItem(data);
	   },
	   error: handleAjaxError
	});
}

function displayItem(data){
	$("#item-edit-form input[name=quantity]").val(data.quantity);
	$("#item-edit-form input[name=mrp]").val(data.mrp);
	$("#item-edit-form input[name=barcode]").val(data.barcode);
	$("#item-edit-form input[name=id]").val(data.id);
	$('#edit-item-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#add-item').click(addOrderItem);
	$('#update-item').click(updateOrderItemDetails);
	$('#refresh-data').click(getOrderItemList);
	$('#place-order').click(placeOrder);
}

$(document).ready(init);
$(document).ready(getOrderItemList);

