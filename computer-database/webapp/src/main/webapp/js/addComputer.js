$(function() {
	let computerName = $("#computerName");
	let companyId = $("#companyId");
	let introduced = $("#introduced");
	let discontinued = $("#discontinued");
	
	computerName.change(function(){
		let errMsg = $("#cnErr");
		if($(this).val() == "")
		{
			this.style.borderColor = "red";
			errMsg.text(strings['js.computer.name.empty']);
			errMsg.slideDown();
		}
		else
		{
			errMsg.slideUp();
			this.style.borderColor = "green";
		}
	});
	
	companyId.change(function(){
		if($(this).val() > 0)
		{
			this.style.borderColor = "green";
		}
		else
		{
			this.style.borderColor = "";
		}
	});

	discontinued.change(checkDates);
	introduced.change(checkDates);
});

function isIntroBeforeDiscont(intro, discont) {
	let introDate = new Date(intro);
	let discontDate = new Date(discont);
	return introDate <= discontDate;	
}

let checkDates = function() {
	let jqIntroduced = $("#introduced");
	let jqDiscontinued = $("#discontinued");
	let introduced = jqIntroduced[0];
	let discontinued = jqDiscontinued[0];
	let errText = $(this).next();
	let introErr = jqIntroduced.next();
	let discontErr = jqDiscontinued.next();
	
	if(jqIntroduced.val() == "" && jqDiscontinued.val() != "")
	{
		introduced.style.borderColor = "red";
		discontinued.style.borderColor = "red";
		introErr.text(strings['js.date.discont.without.intro']);
		discontErr.text(strings['js.date.discont.without.intro']);
		errText.slideDown();
		console.log("Hey");
	}
	else if(jqIntroduced.val() != "" && jqDiscontinued.val() != "" && !isIntroBeforeDiscont(jqIntroduced.val(), jqDiscontinued.val()))
	{
		introduced.style.borderColor = "red";
		discontinued.style.borderColor = "red";
		introErr.text(strings['js.date.discont.before.intro']);
		discontErr.text(strings['js.date.discont.before.intro']);
		errText.slideDown();
	}
	else
	{
		jqIntroduced.next().slideUp();
		jqDiscontinued.next().slideUp();
		introduced.style.borderColor = "green";
		discontinued.style.borderColor = "green";
	}	
}

function checkInputs(formName) {
	let computerName = $("#computerName");
	let companyId = $("#companyId");
	let introduced = $("#introduced");
	let discontinued = $("#discontinued");
	
	let ok = computerName.val() != "";
	ok = ok && !(introduced.val() == "" && discontinued.val() != "");
	ok = ok && !(introduced.val() != "" && discontinued.val() != "" && !isIntroBeforeDiscont(introduced.val(), discontinued.val()));
	
	if(!ok)
	{
		alert("Some fields are invalid. Make sure fields are set correctly.");
	}
	else
	{
		$("#" + formName).submit();
	}
}
