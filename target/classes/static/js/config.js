// var url = 'http://' + location.host + '/st/';
var url = 'http://127.0.0.1/';
var sortable;
function saveSortData(id, url) {
	var el = document.getElementById(id);
	sortable = Sortable.create(el, { 
		onEnd: function(evt) {
			//evt.oldIndex; // element's old index within old parent
			//evt.newIndex; // element's new index within new parent
			var new_order = [];
			evt.to.childNodes.forEach(function(tr) {
				if (tr.id)
					new_order.push(tr.id)
			});
			axios.post(url, {
					newOrder: new_order.join(',')
				})
				.catch(function(error) {
					alert("系统异常");
				});
		}
	});
}
function destroySort() {
	sortable.destroy();
}

function moveTop(trId, tbId, url) {
	var tr = document.getElementById(trId);
	var tb = document.getElementById(tbId);
	tb.deleteRow(tr.rowIndex - 1);
	tb.insertBefore(tr, tb.childNodes.item(1));
	var new_order = [];
	tb.childNodes.forEach(function(e) {
		if (e.id)
			new_order.push(e.id)
	});
	axios.post(url, {
			newOrder: new_order.join(',')
		})
		.catch(function(error) {
			alert("系统异常");
		});
}
