var orderId;

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-item";
}
function getOrderUrl(){
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/order";
}

function getProductUrl() {
   	var baseUrl = $("meta[name=baseUrl]").attr("content")
   	return baseUrl + "/api/product";
}

//BUTTON ACTIONS
function deleteOrder(){
   var orderUrl = getOrderUrl()+"/"+orderId;
    callAjaxApi(orderUrl, 'DELETE', null, null, function(data){
      window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
    });
}

function deleteOrderItems(){

    var deleteOrderItemsUrl = getOrderItemUrl() + "/order/" + orderId;
    callAjaxApi(deleteOrderItemsUrl, 'DELETE', null, null, function(data){
    deleteOrder();
    });
}

function addOrderItem(event){
	//Set the values to update
	    var isValid = $("#order-item-form")[0].checkValidity();
        if(!isValid){
        $("#order-item-form")[0].reportValidity();
        return;
        }
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
    callAjaxApi(url, 'POST', JSON.stringify(obj), "Added the item", addOrderItemSuccess);
}

function addOrderItemSuccess(){
	   		getOrderItemList();
	   		 $("#inputBarcode").val('').trigger('change');
             $("input[name='quantity']").val('');
             $("input[name='sellingPrice']").val('');
}

function updateOrderItem(event){
	//Get the ID
	    var isValid = $("#order-item-edit-form")[0].checkValidity();
                if(!isValid){
                  $("#order-item-edit-form")[0].reportValidity();
                     return;
                }
	var id = $("#order-item-edit-form input[name=id]").val();
	var url = getOrderItemUrl() + "/" + id;
	    var barcode = $("#order-item-edit-form input[name=barcode]").val();
        var quantity = $("#order-item-edit-form input[name=quantity]").val();
        var sellingPrice = $("#order-item-edit-form input[name=sellingPrice]").val();
        var obj = {barcode, quantity, sellingPrice, orderId};
    callAjaxApi(url, 'PUT', JSON.stringify(obj), "Updated the item",function(data){
    	$('#order-item-edit-modal').modal('toggle');
    	   		getOrderItemList();
    } )
}



function getOrderItemList(){
	var url = getOrderItemUrl();
    // Use the retrieved query parameters

	var url = getOrderItemUrl() + "/order/" + orderId;
	callAjaxApi(url, 'GET', null, null, function(data){
	   		displayOrderItemList(data);
	})

}

function setStatusInvoiced(id){
var url = getOrderUrl() + '/place/'+id;
  callAjaxApi(url, 'PUT', null, "Order Placed", function(data){
      window.location.href= $("meta[name=baseUrl]").attr("content")+'/ui/order/view';
  })
}

function placeOrder(){
if(table.data().count() <= 0){
$.notify("No items in the order , cannot place order");
return;
};

setStatusInvoiced(orderId);

}

//UI DISPLAY METHODS

function displayOrderItemList(data){
	table.clear().draw();
    var totalAmount = 0.0;

	for(var i in data){
		var e = data[i];
		var serialNo = parseInt(i)+1;
		var buttonHtml = ' <button onclick="displayEditOrderItem(' + e.id + ')" class="btn btn-warning"><span class="material-symbols-outlined">border_color</span></button>'
            buttonHtml += ' <button onclick="deleteOrderItem(' + e.id + ')" class="btn btn-danger ml-2"><i class="fas fa-trash fa-sm"></i></button>';
        var amount = e.quantity * e.sellingPrice;

            totalAmount += amount;
          table.row.add([
            e.barcode,
            e.name,
            e.quantity,
            e.sellingPrice,
            amount.toFixed(2),
            buttonHtml
          ]);
	}
	table.draw();

		$("#orderId").text("#"+orderId);
    	$("#totalAmount").text(" Rs."+totalAmount.toFixed(2));

}

function deleteOrderItem(id){

    var url = getOrderItemUrl();
    url+= '/' + id;
    callAjaxApi(url, 'DELETE', null, 'Order item deleted', function(data){
    getOrderItemList();
    })
}

function displayEditOrderItem(id){
	var url = getOrderItemUrl() + "/" + id;
	callAjaxApi(url, 'GET', null, null, function(data){
	displayOrderItem(data);
	})
}


function displayOrderItem(data){
	$("#order-item-edit-form input[name=barcode]").val(data.barcode);
	$("#order-item-edit-form input[name=quantity]").val(data.quantity);
	$("#order-item-edit-form input[name=sellingPrice]").val(data.sellingPrice);
	$("#order-item-edit-form input[name=id]").val(data.id);
	$('#order-item-edit-modal').modal('toggle');
}

function setBarcodeDropdown(){
   var url = getProductUrl();
   var barcodeDropdown = $('#inputBarcode');
   callAjaxApi(url, 'GET', null, null, function(data){
      var array = [];
      for(var i in data){
        array.push(data[i].barcode);
      }
      barcodeDropdown.select2({
      data: array,
      width: "160px",
      })
   })
}
//INITIALIZATION CODE
var table;

function initDatatable(){
            table = $('#order-item-table').DataTable(
                    {dom: 'lrtip',
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
    var urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId');
//    const cleanUrl = '/ui/orders'; // Replace with your clean URL
//    history.pushState({}, document.title, cleanUrl);
	$('#add-order-item').click(addOrderItem);
	$('#update-order-item').click(updateOrderItem);
	$('#place-order').click(placeOrder);
	$('#delete-button-modal').click(deleteOrderItems);
	$('#refresh-data').click(getOrderItemList);

}

$(document).ready(init);
$(document).ready(getOrderItemList);

