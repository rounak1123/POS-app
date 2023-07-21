var table;

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}

function getOrderUrl(){
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/order";
}

function getOrderTempTableItemUrl() {
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/order-table-item";
}

function getProductUrl() {
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/product";
}

//BUTTON ACTIONS
function addOrderTempTableItem(event){

    var isValid = $("#item-form")[0].checkValidity();

    if(!isValid){
    $("#item-form")[0].reportValidity();
    return;
    }
  var barcode = $("select[name='barcode']").val();
  var quantity = $("input[name='quantity']").val();
  var sellingPrice = $("input[name='sellingPrice']").val();
  var url = getOrderTempTableItemUrl();
  var obj = {barcode, quantity, sellingPrice, userId: user_id};

  	$.ajax({
  	   url: url,
  	   type: 'POST',
  	   data: JSON.stringify(obj),
  	   headers: {
         	'Content-Type': 'application/json'
         },
  	   success: function(response) {
  	   		getOrderItemList();
            $.notify("Added Order Item","success");

            console.log(response);
            $("select[name='barcode']").val('');
            $("input[name='quantity']").val('');
            $("input[name='sellingPrice']").val('');
  	   },
  	   error: handleAjaxError
  	});

	return false;
}

function updateOrderItemDetails(event){

	//Get the ID

	    var isValid = $("#item-edit-form")[0].checkValidity();

        if(!isValid){
        $("#item-edit-form")[0].reportValidity();
        return;
        }

	var id = $("#item-edit-form input[name=id]").val();
	var url = getOrderTempTableItemUrl() + "/" + id;

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
            $.notify("Updated Order Item","success");
        	$('#edit-item-modal').modal('toggle');
	   		getOrderItemList();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getOrderItemList(){
	var url = getOrderTempTableItemUrl();
	url+='/all/'+user_id;

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

function setBarcodeDropdown(){
   var url = getProductUrl();
   var barcodeDropdown = $('#inputBarcode');
   barcodeDropdown.empty();
 $.ajax({
   url: url,
   type: 'GET',
   success: function(data){
   barcodeDropdown.append($('<option></option>').val('').html('Select an option'));
   $.each(data, function (i, product){
       console.log(product);
       barcodeDropdown.append($('<option></option>').val(product.barcode).html(product.barcode));
   })

   },
   error: handleAjaxError
 })
}

function addOrderItems(orderId){
 var data = [];
$('#order-table tbody tr').each(function() {
    var row = {};

    $(this).find('td').each(function(index, cell) {
        var columnName = $('#order-table thead th').eq(index).attr('name');
        console.log($('#order-table thead th').eq(index), columnName);
        row[columnName] = $(cell).text();
    });
    row['orderId'] = orderId;
    data.push(row);
});
console.log(data);
clearOrder();
$.ajax({
    url: '/api/order-item/all',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(data),
    success: function(response) {
    $.notify("Order Items added successfully to the database", "success");
    console.log("order added to database");
    },
    error: handleAjaxError
});

}

function saveOrder(){
if(table.data().count() <= 0){
$.notify("No items in the order , cannot save order");
return;
};
 var orderUrl = getOrderUrl();
 $.ajax({
     url: orderUrl,
     type: 'POST',
     contentType: 'application/json',
     success: function(response) {
     $.notify("Order Saved","success");
     console.log(response);
     addOrderItems(response.id);
    window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';

     },
     error: handleAjaxError
 });



}

function setStatusInvoiced(id){
var url = getOrderUrl() + '/place/'+id;
 $.ajax({
     url: url,
     type: 'PUT',
     success: function(response) {
      window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
     },
     error: handleAjaxError
 });

}

function placeOrder(){
if(table.data().count() <= 0){
$.notify("No items in the order , cannot place order");
return;
};
 var orderUrl = getOrderUrl();
 $.ajax({
     url: orderUrl,
     type: 'POST',
     contentType: 'application/json',
     success: function(response) {
     console.log("Order Placed");
     addOrderItems(response.id);
     setStatusInvoiced(response.id);
    window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
     },
     error: handleAjaxError
 });

}

function clearOrder(){

	var url = getOrderTempTableItemUrl();
	url+='/all/'+user_id;
    console.log('in clear order');
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	        $.notify("Order cleared","success");
	        	            $("select[name='barcode']").val('');
                            $("input[name='quantity']").val('');
                            $("input[name='sellingPrice']").val('');
	   },
	   error: handleAjaxError
	});
}



//UI DISPLAY METHODS

function displayOrderItemList(data){

    var totalAmount = 0.0;
    table.clear().draw();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditItem(' + e.id + ')" class="btn btn-primary"><span class="material-symbols-outlined">border_color</span></button>';
		 buttonHtml += ' <button onclick="deleteTempTableItem(' + e.id + ')" class="btn btn-danger ml-2"><i class="fas fa-trash fa-sm"></i></button>'
          var subTotal = e.sellingPrice * e.quantity;
          totalAmount += subTotal;

          table.row.add([
            e.barcode,
            e.quantity,
            e.sellingPrice,
            subTotal.toFixed(2),
            buttonHtml
          ]).draw();

	}

	$("#totalAmount").text(" Rs."+totalAmount.toFixed(2));

}

function displayEditItem(id){
	var url = getOrderTempTableItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayItem(data);
	   },
	   error: handleAjaxError
	});
}

function deleteTempTableItem(id){
	var url = getOrderTempTableItemUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   $.notify("Item deleted","success");
	   getOrderItemList();
	   },
	   error: handleAjaxError
	});
}

function displayItem(data){
	$("#item-edit-form input[name=quantity]").val(data.quantity);
	$("#item-edit-form input[name=sellingPrice]").val(data.sellingPrice);
	$("#item-edit-form input[name=barcode]").val(data.barcode);
	$("#item-edit-form input[name=id]").val(data.id);
	$('#edit-item-modal').modal('toggle');
}


//INITIALIZATION CODE

function initDatatable(){
            table = $('#order-table').DataTable(
              {
               dom: 'lrtip',
               paging: false,
               "info": false,
               scrollY: '450px',
               scrollColapse: 'true',
               }
            );
}

function init(){
    initDatatable();
    setBarcodeDropdown();
//	$('#add-item').click(addOrderItem);
	$('#add-item').click(addOrderTempTableItem);
	$('#update-item').click(updateOrderItemDetails);
	$('#refresh-data').click(getOrderItemList);
	$('#save-order').click(saveOrder);
	$('#place-order').click(placeOrder);
	$('#clear-order').click(clearOrder);

}

$(document).ready(init);
$(document).ready(getOrderItemList);

