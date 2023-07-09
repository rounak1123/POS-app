
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

//BUTTON ACTIONS
function getInventoryList(){
	var url = getInventoryUrl();
	url+='/report';
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log('inventory list', data);
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
		 var table = $('#inventory-table').DataTable();
	table.clear().draw();

	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button onclick="displayAddInventory(' + e.id + ')">add <i class="fa fa-plus " ></i></button>'+
		                 ' <button onclick="displayEditInventory(' + e.id + ')">edit <span class="material-symbols-outlined">border_color</span></button>'
          table.row.add([
            e.id,
            e.barcode,
            e.brand,
            e.category,
            e.quantity
          ]).draw();
	}
}

//INITIALIZATION CODE
function init(){
}

$(document).ready(init);
$(document).ready(getInventoryList);

