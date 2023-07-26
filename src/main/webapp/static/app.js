
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}

function handleAjaxError(response){
    console.log('response data', response);
	var response = JSON.parse(response.responseText);
	$.notify(response.message);
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr,nameFile){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
    console.log(arr);
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;
    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, nameFile);
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', nameFile);
    tempLink.click(); 
}

            const menuHamburger = document.querySelector(".burger")
            const navLinks = document.querySelector(".navbar")

            menuHamburger.addEventListener('click', () => {
                navLinks.classList.toggle('mobile-menu')
            })

function init(){
}

$(document).ready(init);

