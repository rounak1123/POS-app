var table;
var counter=0;

function getOrderItemUrl(){
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
//function addOrderItem(event){
//  var barcode = $('#inputBarcode');
//  var quantity = $('#inputQuantity');
//  var sellingPrice = $('#inputSellingPrice');
//
//  var buttonHtml = ' <button onclick="displayEditBrand(' + e.id + ')"><i class="fas fa-edit fa-sm"></i></button>';
//          table.row.add([
//            e.brand,
//            e.quantity,
//            e.sellingPrice,
//            buttonHtml
//          ]).draw();
//
//	//Set the values to update
//
//	return false;*/
//}

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
$.ajax({
    url: '/api/order-item/all',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(data),
    success: function(response) {
    console.log("Order Items added successfully to the database");
    },
    error: handleAjaxError
});

}

function saveOrder(){
var rowCount = $('#order-table tbody tr').length;
console.log(rowCount);
if(rowCount <= 0){
$.notify("No items in the order , cannot save order");
return;
};
 var orderUrl = getOrderUrl();
 $.ajax({
     url: orderUrl,
     type: 'POST',
     contentType: 'application/json',
     success: function(response) {
     console.log("Order Saved Successfully");
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
     console.log("Order Status Set to Invoice Successfully");
      window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order-item/view?orderId='+id;
     },
     error: handleAjaxError
 });

}

function placeOrder(){
var rowCount = $('#order-table tbody tr').length;
if(rowCount <= 0){
$.notify("No items in the order, cannot place order.");
return;
};
 var orderUrl = getOrderUrl();
console.log('inPlace order')
 $.ajax({
     url: orderUrl,
     type: 'POST',
     contentType: 'application/json',
     success: function(response) {
     console.log("Order Created Successfully");
     addOrderItems(response.id);
     setStatusInvoiced(response.id);
    window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';



     },
     error: handleAjaxError
 });

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


//function initDatatable(){
//            table = $('#order-table').DataTable(
//              {dom: 'lrtip'}
//            );
//
//}

// Create Order Dynamic Table

function addOrderItemToTable(){
        var barcode = $("input[name='barcode']").val();
        var quantity = $("input[name='quantity']").val();
        var sellingPrice = $("input[name='sellingPrice']").val();
       console.log(barcode,quantity,sellingPrice);
        $("#order-table tbody").append("<tr data-barcode='"+barcode+"' data-quantity='"+quantity+"' data-sellingPrice='"+sellingPrice+"'><td>"+barcode+"</td><td>"+quantity+"</td><td>"+sellingPrice+"</td><td><button class='btn btn-info btn-xs mr-2 btn-edit'>Edit</button><button class='btn btn-danger btn-xs mr-2 btn-delete'>Delete</button></td></tr>");
       console.log($('#order-table'));
        $("input[name='barcode']").val('');
        $("input[name='quantity']").val('');
        $("input[name='sellingPrice']").val('');
}
function addOrderItem() {

    var $form = $("#item-form");
	var json = toJson($form);
	var url = getOrderItemUrl();
     url+='/validate';
console.log('in add order item');
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		addOrderItemToTable();
	   },
	   error: handleAjaxError
	});
}

   $("body").on("click", ".btn-delete", function(){
        $(this).parents("tr").remove();
    });

    $("body").on("click", ".btn-edit", function(){
        var barcode = $(this).parents("tr").attr('data-barcode');
        var quantity = $(this).parents("tr").attr('data-quantity');
        var sellingPrice = $(this).parents("tr").attr('data-sellingPrice');


        $(this).parents("tr").find("td:eq(0)").html('<input name="edit_barcode" value="'+barcode+'">');
        $(this).parents("tr").find("td:eq(1)").html('<input name="edit_quantity" value="'+quantity+'">');
        $(this).parents("tr").find("td:eq(2)").html('<input name="edit_selling_price" value="'+sellingPrice+'">');

        $(this).parents("tr").find("td:eq(3)").prepend("<button class='btn btn-info btn-xs btn-update mr-2 '>Update</button><button class='btn btn-warning btn-xs btn-cancel mr-2'>Cancel</button>")
        $(this).hide();
    });

    $("body").on("click", ".btn-cancel", function(){
        var barcode = $(this).parents("tr").attr('data-barcode');
        var quantity = $(this).parents("tr").attr('data-quantity');
        var sellingPrice = $(this).parents("tr").attr('data-sellingPrice');


        $(this).parents("tr").find("td:eq(0)").text(barcode);
        $(this).parents("tr").find("td:eq(1)").text(quantity);
        $(this).parents("tr").find("td:eq(2)").text(sellingPrice);

        $(this).parents("tr").find(".btn-edit").show();
        $(this).parents("tr").find(".btn-update").remove();
        $(this).parents("tr").find(".btn-cancel").remove();
    });

    $("body").on("click", ".btn-update", function(){
        var barcode = $(this).parents("tr").find("input[name='edit_barcode']").val();
        var quantity = $(this).parents("tr").find("input[name='edit_quantity']").val();
        var sellingPrice = $(this).parents("tr").find("input[name='edit_selling_price']").val();
        var obj = {
        sellingPrice,
        barcode,
        quantity
        };

	        var url = getOrderItemUrl();

             url+='/validate';

        	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: JSON.stringify(obj),
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	           $(this).parents("tr").find("td:eq(0)").text(barcode);
                       $(this).parents("tr").find("td:eq(1)").text(quantity);
                       $(this).parents("tr").find("td:eq(2)").text(sellingPrice);

                       $(this).parents("tr").attr('data-barcode', barcode);
                       $(this).parents("tr").attr('data-quantity', quantity);
                       $(this).parents("tr").attr('data-sellingPrice', sellingPrice);
        	   },
        	   error: handleAjaxError
        	});


        $(this).parents("tr").find(".btn-edit").show();
        $(this).parents("tr").find(".btn-cancel").remove();
        $(this).parents("tr").find(".btn-update").remove();
    });


function clearOrder(){
$("#order-table").find("tr:gt(0)").remove();
}

//INITIALIZATION CODE
function init(){
//    initDatatable();
	$('#add-item').click(addOrderItem);
	$('#update-item').click(updateOrderItemDetails);
	$('#refresh-data').click(getOrderItemList);
	$('#save-order').click(saveOrder);
	$('#place-order').click(placeOrder);
	$('#clear-order').click(clearOrder);

}

$(document).ready(init);
$(document).ready(getOrderItemList);

