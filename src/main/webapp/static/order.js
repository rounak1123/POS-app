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
    callAjaxApi(url, 'POST', JSON.stringify(obj), "Added Order Item", addOrderTempTableItemSuccess);
}

function addOrderTempTableItemSuccess(data){
  	   		getOrderItemList();
            $("select[name='barcode']").val('').trigger('change');
            $("input[name='quantity']").val('');
            $("input[name='sellingPrice']").val('');
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

  var barcode = $("#item-edit-form input[name=barcode]").val();
  var quantity = $("#item-edit-form input[name=quantity]").val();
  var sellingPrice = $("#item-edit-form input[name=sellingPrice]").val();
  console.log(barcode);
  var obj = {barcode,quantity,sellingPrice, userId: user_id}

	//Set the values to update
    callAjaxApi(url, 'PUT', JSON.stringify(obj), "Updated Order Item", updateOrderItemDetailsSuccess);
}

function updateOrderItemDetailsSuccess(data){
        	$('#edit-item-modal').modal('toggle');
	   		getOrderItemList();
}


function getOrderItemList(){
	var url = getOrderTempTableItemUrl();
	url+='/all/'+user_id;
    callAjaxApi(url, 'GET', null, null, displayOrderItemList);
}

function setBarcodeDropdown(){
   var url = getProductUrl();

   callAjaxApi(url, 'GET', null, null, setBarcodeDropdownSuccess);
}

function setBarcodeDropdownSuccess(data){
   var barcodeDropdown = $('#inputBarcode');
   var array = [];
   for(var i in data){
     array.push(data[i].barcode);
   }
   barcodeDropdown.select2({
   data: array,
   width: "160px",
   })
}


function getOrderTableItemList(){
 var data = [];
$('#order-table tbody tr').each(function() {
    var row = {};

    $(this).find('td').each(function(index, cell) {
        var columnName = $('#order-table thead th').eq(index).attr('name');
        console.log($('#order-table thead th').eq(index), columnName);
        if(columnName && columnName != "name")
        row[columnName] = $(cell).text();
    });
    row["userId"] = user_id;
    data.push(row);
});
console.log(data);
return data;

}


function saveOrder(){
if(table.data().count() <= 0){
$.notify("No items in the order , cannot save order");
return;
};
var data = getOrderTableItemList();

 var orderUrl = getOrderUrl();
 callAjaxApi(orderUrl, 'POST', JSON.stringify(data),"Order Saved", saveOrderSuccess);

}

function saveOrderSuccess(){
 clearOrder();
    window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
}

function setStatusInvoiced(id){
var url = getOrderUrl() + '/place/'+id;
callAjaxApi(url, 'PUT', null, null, function(data){
      window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
});
}

function placeOrder(){
if(table.data().count() <= 0){
$.notify("No items in the order , cannot place order");
return;
};
 var orderUrl = getOrderUrl();
 var data = getOrderTableItemList();
 callAjaxApi(orderUrl, 'POST', JSON.stringify(data), null, placeOrderSuccess);

}

function placeOrderSuccess(data){
setStatusInvoiced(data.id);
    window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
}

function clearOrder(){

	var url = getOrderTempTableItemUrl();
	url+='/all/'+user_id;
    console.log('in clear order');
    callAjaxApi(url, 'DELETE', null, "Ordered cleared", clearOrderSuccess);

}

function clearOrderSuccess(data){
	      $("select[name='barcode']").val('');
          $("input[name='quantity']").val('');
          $("input[name='sellingPrice']").val('');
}

//UI DISPLAY METHODS

function displayOrderItemList(data){

    var totalAmount = 0.0;
    table.clear().draw();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditItem(' + e.id + ')" class="btn btn-warning"><span class="material-symbols-outlined">border_color</span></button>';
		 buttonHtml += ' <button onclick="deleteTempTableItem(' + e.id + ')" class="btn btn-danger ml-2"><i class="fas fa-trash fa-sm"></i></button>'
          var amount = e.sellingPrice * e.quantity;
          totalAmount += amount;

          table.row.add([
            e.name,
            e.barcode,
            e.quantity,
            e.sellingPrice,
            amount.toFixed(2),
            buttonHtml
          ]);

	}
	table.draw();

	$("#totalAmount").text(" Rs."+totalAmount.toFixed(2));

}

function displayEditItem(id){
	var url = getOrderTempTableItemUrl() + "/" + id;
	callAjaxApi(url, 'GET', null, null, displayItem);
}

function deleteTempTableItem(id){
	var url = getOrderTempTableItemUrl() + "/" + id;
	callAjaxApi(url, 'DELETE', null, "Item deleted", function(data){
	getOrderItemList();
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

