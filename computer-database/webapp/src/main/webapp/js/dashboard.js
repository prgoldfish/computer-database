//On load
$(function() {
    // Default: hide edit mode
    $(".editMode").hide();
    
    // Click on "selectall" box
    $("#selectall").click(function () {
        $('.cb').prop('checked', this.checked);
        if ($(".cb:checked").length != 0) {
            $("#deleteSelected").prop('disabled', false);
        } else {
            $("#deleteSelected").prop('disabled', true);
        }
    });
    
    $("#deleteSelected").prop('disabled', true);

    // Click on a checkbox
    $(".cb").click(function() {
        if ($(".cb").length == $(".cb:checked").length) {
            $("#selectall").prop("checked", true);
        } else {
            $("#selectall").prop("checked", false);
        }
        if ($(".cb:checked").length != 0) {
            $("#deleteSelected").prop('disabled', false);
        } else {
            $("#deleteSelected").prop('disabled', true);
        }
    });
    
    $(".column").click(function(){
    	let orderParameter = "";
    	switch ($(this).attr('id')) {
		case "nameColumn":
			orderParameter = "ComputerName";
			break;
		case "introColumn":
			orderParameter = "IntroducedDate";
			break;
		case "discontColumn":
			orderParameter = "DiscontinuedDate";
			break;
		case "companyColumn":
			orderParameter = "CompanyName";
			break;
		default:
			break;
		}
    	
    	let oldOrder = $("#orderParameter").val();
    	let oldAscendent = $("#ascendentParameter").val();
    	let ascendent = "asc"
    	
    	if(oldOrder == orderParameter && oldAscendent != "desc")
		{
    		ascendent = "desc";
		}
    	$("#orderForm input[name=order]").val(orderParameter);
    	$("#orderForm input[name=ascendent]").val(ascendent);
    	$("#orderForm").submit();
    	
    });

});


// Function setCheckboxValues
(function ( $ ) {

    $.fn.setCheckboxValues = function(formFieldName, checkboxFieldName) {

        var str = $('.' + checkboxFieldName + ':checked').map(function() {
            return this.value;
        }).get().join();
        
        $(this).attr('value',str);
        
        return this;
    };

}( jQuery ));

// Function toggleEditMode
(function ( $ ) {

    $.fn.toggleEditMode = function() {
        if($(".editMode").is(":visible")) {
            $(".editMode").hide();
            $("#editComputer").text(strings['js.computer.edit']);
        }
        else {
            $(".editMode").show();
            $("#editComputer").text(strings['js.computer.view']);
        }
        return this;
    };

}( jQuery ));


//Function delete selected: Asks for confirmation to delete selected computers, then submits it to the deleteForm
(function ( $ ) {
 $.fn.deleteSelected = function() {
     if (confirm(strings['js.computer.delete.confirmation'])) { 
         $('#deleteForm input[name=selection]').setCheckboxValues('selection','cb');
         $('#deleteForm').submit();
     }
 };
}( jQuery ));


(function ( $ ) {
 $.fn.goToPage = function(pageNum) {
	 $('#pageForm input[name=page]').val(pageNum);
	 $('#pageForm').submit();
 };
}( jQuery ));



//Event handling
//Onkeydown
$(document).keydown(function(e) {

    switch (e.keyCode) {
        //DEL key
        case 46:
            if($(".editMode").is(":visible") && $(".cb:checked").length != 0) {
                $.fn.deleteSelected();
            }   
            break;
        //E key (CTRL+E will switch to edit mode)
        case 69:
            if(e.ctrlKey) {
                $.fn.toggleEditMode();
            }
            break;
    }
});

