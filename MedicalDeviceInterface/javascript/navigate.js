var timeOffset;
var myTime = 0;
var calcTime = 0;
var drugName = "";
var numValuesEntered = "";
var dataEntryStart;
var dataEntryEnd;

window.onload = function(event) {
	timeOffset = event.timeStamp;
}
//define states of behaviour, used in navigation
var states = [{
	order : 0,
	divId : "#drug",
	dataFieldName : '#drugList',
	confirmButton : "#drugConfirm"
}, {
	order : 1,
	divId : "#numVals",
	dataFieldName : '#numberOfValues',
	confirmButton : "#numValuesConfirm"
}, {
	order : 2,
	divId : "#vtbi",
	dataFieldName : '#theVTBINumber',
	confirmButton : "#vtbiConfirm"
}, {
	order : 3,
	divId : "#rate",
	dataFieldName : '#theRateNumber',
	confirmButton : "#rateConfirm"
}, {
	order : 4,
	divId : "#time",
	dataFieldName : '#theTimeNumber',
	confirmButton : "#timeConfirm"
}, {
	order : 5,
	divId : "#finalizeRx",
	dataFieldName : '000',
	confirmButton : "#confirmRx"
}];

var currentState = 0;
//initialize currentState to 0

var drugs = [/*{
	name : 'AABA',
	risk : 'Low'
}, */{
	name : 'ABBA',
	risk : 'Low'
}, {
	name : 'ACBA',
	risk : 'Low'
} , /*{
	name : 'ADBA',
	risk : 'Low'
}, {
	name : 'AEBA',
	risk : 'Low'
}, {
	name : 'AFBA',
	risk : 'Low'
}, {
	name : 'AGBA',
	risk : 'Low'
}, {
	name : 'AHBA',
	risk : 'Low'
}, {
	name : 'AIBA',
	risk : 'Low'
}, {
	name : 'AJBA',
	risk : 'Low'
}, {
	name : 'AKBA',
	risk : 'Low'
}, {
	name : 'ALBA',
	risk : 'Low'
}, {
	name : 'AMBA',
	risk : 'Low'
}, {
	name : 'ANBA',
	risk : 'Low'
}, {
	name : 'AOBA',
	risk : 'Low'
}, */
{
	name : 'APBA',
	risk : 'Low'
},/* {
	name : 'AQBA',
	risk : 'Low'
}, {
	name : 'ARBA',
	risk : 'Low'
}, {
	name : 'ASBA',
	risk : 'Low'
}, {
	name : 'ATBA',
	risk : 'Low'
}, {
	name : 'AUBA',
	risk : 'Low'
}, {
	name : 'AVBA',
	risk : 'Low'
}, {
	name : 'BABA',
	risk : 'High'
}, {
	name : 'BBBA',
	risk : 'High'
}, {
	name : 'BCBA',
	risk : 'High'
}, {
	name : 'BDBA',
	risk : 'High'
}, {
	name : 'BEBA',
	risk : 'High'
}, {
	name : 'BFBA',
	risk : 'High'
}, {
	name : 'BGBA',
	risk : 'High'
}, {
	name : 'BHBA',
	risk : 'High'
}, {
	name : 'BIBA',
	risk : 'High'
}, {
	name : 'BJBA',
	risk : 'High'
}, {
	name : 'BKBA',
	risk : 'High'
}, {
	name : 'BLBA',
	risk : 'High'
}, {
	name : 'BMBA',
	risk : 'High'
}, */{
	name : 'BNBA',
	risk : 'High'
}, /*{
	name : 'BOBA',
	risk : 'High'
}, {
	name : 'BPBA',
	risk : 'High'
}, {
	name : 'BQBA',
	risk : 'High'
}, {
	name : 'BRBA',
	risk : 'High'
}, {
	name : 'BSBA',
	risk : 'High'
},*/ {
	name : 'BTBA',
	risk : 'High'
}, /*{
	name : 'BUBA',
	risk : 'High'
}, */{
	name : 'BVBA',
	risk : 'High'
}];

var currentTimeVal = "";
var currentRateVal = "";

//This array stores the numbers you will want the participant to enter.
// read this from file.

var vtbi = [100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 500, 500, 500, 500, 500, 1000, 1000, 1000, 1000, 1000, 50, 250, 20, 30, 10, 80, 95, 89, 88, 60, 40, 5, 1, 200, 15, 550, 90, 975, 110, 33, 11, 55, 70, 39, 45, 22, 990, 888, 150, 180, 56, 75];

var rate = [200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 125, 125, 125, 125, 125, 999, 999, 999, 999, 999, 500, 250, 100, 167, 900, 333, 50, 83, 6, 56, 184, 980, 89, 41, 10, 42, 177, 888, 150, 83, 88, 30, 99, 165, 25, 160, 170, 95, 28, 138, 120, 173, 212, 12, 60, 143, 62, 55];

var time = [200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 125, 125, 125, 125, 125, 999, 999, 999, 999, 999, 500, 250, 100, 167, 900, 333, 50, 83, 6, 56, 184, 980, 89, 41, 10, 42, 177, 888, 150, 83, 88, 30, 99, 165, 25, 160, 170, 95, 28, 138, 120, 173, 212, 12, 60, 143, 62, 55];

var drugEntered = false;
var vtbiEntered = false;
var rateEntered = false;
var timeEntered = false;
var stopped = false;
var firstSeen = 0;

var type = "drug";

var enterHit = true;
var enterDose = 0;
var decimalEntered = false;
var amount = 0;

var key = "";

var targetR = 0;
var targetV = 0;
var stages = [0, 1, 2, 3, 4, 5];

function shuffle(array) {
	var tmp, current, top = array.length;

	if (top)
		while (--top) {
			current = Math.floor(Math.random() * (top + 1));
			tmp = array[current];
			array[current] = array[top];
			array[top] = tmp;
		}
}

/**
 * Author: Dan Davis
 *  **/
function download(strData, strFileName, strMimeType) {
	var D = document, A = arguments, a = D.createElement("a"), d = A[0], n = A[1], t = A[2] || "text/plain";

	//build download link:
	a.href = "data:" + strMimeType + "," + strData;

	if (window.MSBlobBuilder) {// IE10
		var bb = new MSBlobBuilder();
		bb.append(strData);
		return navigator.msSaveBlob(bb, strFileName);
	}/* end if(window.MSBlobBuilder) */

	if ('download' in a) {//FF20, CH19
		a.setAttribute("download", n);
		a.innerHTML = "downloading...";
		D.body.appendChild(a);
		setTimeout(function() {
			var e = D.createEvent("MouseEvents");
			e.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
			a.dispatchEvent(e);
			D.body.removeChild(a);
		}, 66);
		return true;
	};/* end if('download' in a) */

	//do iframe dataURL download: (older W3)
	var f = D.createElement("iframe");
	D.body.appendChild(f);
	f.src = "data:" + (A[2] ? A[2] : "application/octet-stream") + (window.btoa ? ";base64" : "") + "," + (window.btoa ? window.btoa : escape)(strData);
	setTimeout(function() {
		D.body.removeChild(f);
	}, 333);
	return true;
}/* end download() */

function shuffleAll() {
	shuffle(rate);
	shuffle(vtbi);
}

function restartNumberEntry() {
	currentState = 2;
	vtbiEntered = false;
	rateEntered = false;
	timeEntered = false;
	selectField();
	enableVTBI();
	disableRate();
	disableTime();
}

function initializeDrugList() {
	var sel = $("#drugList");
	sel.append($("<option>").attr('value', '').text('-- Select Drug --'));
	$(drugs).each(function() {
		sel.append($("<option>").attr('value', this.risk).text(this.name));
	});
	sel.focus();
	currentState = 0;
}

function selectField() {//used by arrow up/down
	//use currentState to move up and down
	var stageIndex = stages[currentState];
	var state = states[stageIndex];
	var divId = state.divId;

	if (currentState >= 2 && currentState <= 4) {
		var field = $(state.dataFieldName);
		field.focus();
	}
	//highlight current step in blue rectangle
	$(".item").css({
		border : 'solid 1px #CCC'
	});
	if (stageIndex > 1 && stageIndex < 5) {
		$(divId).css({
			border : 'solid 1px #009'
		});
	}
}

function confirmVTBI() {

	if (confirm("Confirm VTBI value?")) {

		vtbiEntered = true;
		$("#vtbiConfirm").html('Confirmed');
		$("#vtbiConfirm").prop("disabled", true);
		dose = "";

		currentState++;
		selectField();
	}
}

function disableNumVals() {
	$("#numValuesConfirm").prop('disabled', true);
	$("#numberOfValues").prop('disabled', true);
}

function enableNumVals() {
	$("#numValuesConfirm").prop('disabled', false);
	$("#numberOfValues").prop('disabled', false);
}

function disableVTBI() {
	$("#theVTBINumber").prop('contentEditable', false);
	$("#vtbiConfirm").prop('disabled', true);
	//$("#vtbiConfirm").html('Confirmed');
}

function enableVTBI() {
	$("#theVTBINumber").prop('contentEditable', true);
	$("#vtbiConfirm").prop('disabled', false);
	$("#vtbiConfirm").html('Confirm');
}

function disableRate() {
	$("#theRateNumber").prop('contentEditable', false);
	$("#rateConfirm").prop('disabled', true);
	//$("#rateConfirm").html('Confirmed');
}

function enableRate() {
	$("#theRateNumber").prop('contentEditable', true);
	$("#rateConfirm").prop('disabled', false);
	$("#rateConfirm").html('Confirm');
}

function disableTime() {
	$("#theTimeNumber").prop('contentEditable', false);
	$("#timeConfirm").prop('disabled', true);
	//$("#rateConfirm").html('Confirmed');
}

function enableTime() {
	$("#theTimeNumber").prop('contentEditable', true);
	$("#timeConfirm").prop('disabled', false);
	$("#timeConfirm").html('Confirm');
}

function disableFinalize() {
	$("#confirmRx").prop('disabled', true);
}

function enableFinalize() {
	$("#confirmRx").prop('disabled', false);
}

//list of steps string values:

//This variable stores everything you want to be in the csv file. This first bit lists the headings that will be at the top.
var csv = "Target VTBI, Target Rate, Target Type, Key Press, Current Number, Time, First Seen Number<br/>";
var csv2 = "Drug Name, Num Values Entered, Target Type, Key Press, Current Number, Time, First Seen Number, Dose Entered%0A";

$("document").ready(function() {

	if (!drugEntered) {
		initializeDrugList();
		disableNumVals();
		disableVTBI();
		disableRate();
		disableTime();
		disableFinalize();
	}

	$('#drugConfirm').click(function(e) {

		var riskFrame = $("#riskFrame");
		var value = $("#drugList").val();

		drugName = $("#drugList option:selected").text();
		if (drugName.length > 4) {
			alert("Please select drug type");
			return;
		}

		if (confirm("Confirm drug type?")) {
			enableNumVals();

			$("#drugList").prop("hidden", true);
			$("#drugConfirm").prop("hidden", true);
			$("#drugName").html(drugName);

			$("#instruction").html('Please enter VTBI, Rate, and Time (optional).');

			riskFrame.html(value);
			drugEntered = true;
			if (value == 'High') {
				riskFrame.css({
					color : "#F00"
				});
			} else {
				riskFrame.css({
					color : "#00F"
				});
			}
			$("#numberOfValues").focus();
			currentState++;
			selectField();
		} else {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	});

	$('#numValuesConfirm').click(function(e) {

		numValuesEntered = $("#numberOfValues").val();

		if (confirm("Confirm number of values?")) {

			enableVTBI();

			$("#numVals").prop("hidden", true);
			$("#vtbi").prop("hidden", false);
			$("#rate").prop("hidden", false);

			if (numValuesEntered == "2") {//if 2 values, enter VTBI and RATE
				
				stages = [0, 1, 2, 3, 5];
				$("#instruction").html('Please enter VTBI and Rate');
			} else {
				//if 3 values, enter VTBI, RATE, TIME
				stages = [0, 1, 2, 3, 4, 5];
				$("#time").prop("hidden", false);
				$("#instruction").html('Please enter VTBI, Rate, and Time.');
			}

			$("#finalizeRx").prop("hidden", false);

			currentState++;
			selectField();

			//record time start of data entry
			dataEntryStart = e.timeStamp;

		} else {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	});

	$('#vtbiConfirm').click(function(e) {
		if (confirm("Confirm VTBI value?")) {
			currentVTBIVal = dose;
			enableRate();
			disableVTBI();

			vtbiEntered = true;
			$("#vtbiConfirm").html('Confirmed');
			$("#vtbiConfirm").prop("disabled", true);
			dose = "";

			currentState++;
			selectField();

		} else {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	});

	$('#rateConfirm').click(function(e) {
		if (confirm("Confirm Rate value?")) {
			currentRateVal = dose;

			if (stages[4] == 5) {
				enableFinalize();
			} else
				enableTime();

			disableRate();

			rateEntered = true;
			$("#rateConfirm").html('Confirmed');
			$("#rateConfirm").prop("disabled", true);
			dose = "";

			currentState++;
			selectField();

		} else {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	});

	$('#timeConfirm').click(function(e) {

		var currentVTBIVal = $('#theVTBINumber').html();
		var currentRateVal = $('#theRateNumber').html();
		var currentTimeVal = $('#theTimeNumber').html();

		var correctDose = parseFloat(currentVTBIVal) / parseFloat(currentRateVal);

		if (parseFloat(currentTimeVal) != correctDose) {
			alert("Please re-enter correct VTBI, Rate, and/or Time values.");
			dose = "";
			//restart number entry
			restartNumberEntry();
		} else {

			if (confirm("Confirm Time value?")) {
				disableTime();
				enableFinalize();

				timeEntered = true;
				$("#timeConfirm").html('Confirmed');
				$("#timeConfirm").prop("disabled", true);
				dose = "";

				currentState++;
				selectField();

			} else {
				e.stopImmediatePropagation();
				e.preventDefault();
			}
		}
	});

	$('#confirmRx').click(function(e) {
		if (confirm("You are about to start the infusion. Press Cancel to re-check your entries.")) {

			dose = "";
			currentVTBIVal = "";
			currentRateVal = "";

			drugEntered = false;

			trial = trial + 1;
			$('#theTrial').html(trial);
			$('#theTarget1').html(vtbi[trial]);
			$('#theTarget2').html(rate[trial]);

			$('#theRateNumber').html('0.0');
			$('#theVTBINumber').html('0.0');
			$('#theTimeNumber').html('0');

			$("#drugList option:first").attr('selected', 'selected');
			$("#riskFrame").html('');
			$("#drugName").html('');
			$("#drugList").prop("hidden", false);
			$("#drugConfirm").prop("hidden", false);
			$("#numVals").prop("hidden", false);
			$("#numVals option:first").attr('selected', 'selected');

			$("#instruction").html('Please choose prescription drug.<br /><br/>Press <b>Up/Down</b> arrow keys to select and <b>Enter</b> to confirm drug type.');

			$("#vtbi").prop("hidden", true);
			$("#rate").prop("hidden", true);
			$("#time").prop("hidden", true);

			$("#finalizeRx").prop("hidden", true);

			$("#rateConfirm").html('Confirm');
			$("#vtbiConfirm").html('Confirm');
			$("#timeConfirm").html('Confirm');
			disableVTBI();
			disableRate();
			disableTime();

			firstSeen = (e.timeStamp - timeOffset) / 1000;

			currentState = 0;
			$("#drugList").focus();
			selectField();

		} else {
			restartNumberEntry();
		}

	});

	$("#theTimeNumber").keypress(function(e) {
		e.preventDefault();
	});

	$("#theVTBINumber").keypress(function(e) {
		e.preventDefault();
	});

	$("#theRateNumber").keypress(function(e) {
		e.preventDefault();
	});

	$(document).keydown(function(e) {

		if (!stopped) {

			enterHit = false;

			if (!drugEntered) {
				initializeDrugList();
			}
			//This adds to the csv. The first thing is the target number, then the button pressed (this looks odd for the arrow
			//keys but don't worry, it will be fine for others). Then the current dose, then the time that key was pressed.
			var num="";
			switch (e.keyCode) {

				case 38:
					//up arrow

					break;
				case 40:
					//down arrow

					break;
				//0
				case 48:
					
					num = "0";

					break;
				//1
				case 49:
					num = "1";

					break;
				//2
				case 50:
					num = "2";

					break;
				//3
				case 51:
					num = "3";

					break;
				//4
				case 52:
					num = "4";

					break;

				//5
				case 53:
					num = "5";

					break;
				//6
				case 54:
					num = "6";

					break;
				//7
				case 55:
					num = "7";

					break;
				//8
				case 56:
					num = "8";

					break;
				//9

				case 57:
					num = "9";

					break;

				//.  (this is #58 on prototype
				case 190:
					//if dose
					if (dose.indexOf(".") > -1 && currentState != 4 && dose.length<7) {
						dose = dose + ".";
					}
					decimalEntered = true;

					break;

				//Clear
				case 8:
					key = "Clear";
					dose = "";
					
					break;
				//Escape
				case 27:
					key = "Escape";
					dose = "";
					
					break;
				//The enter key is pressed. This needs to do a number of things. Firstly update the trial number. Then reset the dose to
				//0 as a new number needs to be input. It also updates the target number by getting a new number from the targets array.
				case 13:
					key = "Enter";
					enterHit = true;
					enterDose = dose;

					//confirm entries
					if (currentState < 6 && !stopped) {
						var confirmButton = $(states[stages[currentState]].confirmButton);

						if (!confirmButton.is(':disabled')) {
							confirmButton.click();
						}
					}

					break;

			}

			if (currentState == 0) {
				dose = "";
			}

			if((currentState < 5 && currentState > 1) && e.keyCode == 8){
				
				if (currentState != 4)
					dataField.html('0.0');
				else
					dataField.html('0');
						
			} else if ((currentState < 5 && currentState > 1) 
				&& (e.keyCode <= 57 && e.keyCode >= 48)) {
				
				var dataField = $(states[currentState].dataFieldName);
				
				if(dose.length<7){
					dose = dose + num;
					if (currentState != 4)
						dataField.html(dose + '.0');
					else
						dataField.html(dose);
				}
			}

			if (enterHit && currentState > 1 && currentState < 5) {
				amount = enterDose;
			} else {
				if (currentState >= 2 && currentState <= 4)
					amount = dose;
			};

			if (key == "") {
				key = String.fromCharCode(e.keyCode);
			}

			if (key == "Enter" & !(vtbiEntered)) {

				targetV = vtbi[trial - 1];
				targetR = rate[trial - 1];
				targetT = time[trial - 1];

			} else {
				targetV = vtbi[trial];
				targetR = rate[trial];
				targetT = time[trial];
			}
			var currDiv = "Confirm Rx";
			if (currentState < 6) {
				currDiv = states[currentState].divId;
			}
			
			csv = csv + targetV + ',' + targetR + ',' + currDiv + ',' + key + ',' + amount + ',' + (e.timeStamp - timeOffset) / 1000 + ',' + firstSeen + '<br/>';
			csv2 = csv2 + drugName + ',' + numValuesEntered + ',' + currDiv + ',' + key + ',' + amount + ',' + (e.timeStamp - timeOffset) / 1000 + ',' + firstSeen + ',' + dose + "%0A";

			key = "";

			// stop trial after 2min
			myTime = e.timeStamp;
			calcTime = myTime - timeOffset;
			if (calcTime > 180000) {
				window.onload = document.getElementById('gc-overlay').style.display = 'block';
				//window.location.href = "application/csv;charset=UTF-8,"+csv;
				download(csv2, "results.csv", "application/vnd.ms-excel");

				stopped = true;
				//end session

				alert("END OF TRIAL. Thanks for your participation!");
				disableVTBI();
				disableRate();
				disableTime();
				disableFinalize();
				return;

			}
		}
	});

});

