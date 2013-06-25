/*
 * This JavaScript will be append to the output HTML-File.
 * It will be enable the be sorely missed! 
 * 
 * @author Sven Schumann <s.schumann@tarent.de>
 */

$(document).ready(function(){
	
	scenarios = {};
	
	scenarios.total = $("section.scenario").length;
	scenarios.failed = $("section.scenario.failed").length;
	scenarios.failedPercent = (scenarios.failed * 100 / scenarios.total).toFixed(2);
	scenarios.passed = $("section.scenario:not(.failed)").length;
	scenarios.passedPercent = (scenarios.passed * 100 / scenarios.total).toFixed(2);
	
	steps = {};
	
	steps.total = $("li.step").length;
	steps.failed = $("li.step.failed").length;
	steps.failedPercent = (steps.failed * 100 / steps.total).toFixed(2);
	steps.skipped = $("li.step.skipped").length;
	steps.skippedPercent = (steps.skipped * 100 / steps.total).toFixed(2);
	steps.passed = $("li.step.passed").length;
	steps.passedPercent = (steps.passed * 100 / steps.total).toFixed(2);
	
	cucumberHeaderTemplate = ' <div id="cucumber-header" style="background: none repeat scroll 0% 0% rgb(196, 13, 13); color: rgb(255, 255, 255);">\
		<div id="label">\
			<h1>Cucumber Features</h1>\
		</div>\
		<div id="summary">\
			<p id="totals"></p>\
			<div id="expand-collapse">\
				<p id="expanderFail" style="cursor: pointer;">Expand failed</p>\
				<p id="collapserFail" style="cursor: pointer;">Collapse failed</p>\
				<p id="expanderAll" style="cursor: pointer;">Expand All</p>\
				<p id="collapserAll" style="cursor: pointer;">Collapse All</p>\
			</div>\
		</div>\
	</div>';
	
	$("body").prepend(cucumberHeaderTemplate);
	
	/*
	 * Enable collapse/expand each scenario 
	 */
	$("section.scenario > details > summary").each(function(index, curSummary) {
	    curSummary.style.cursor = 'pointer';
	    curSummary.style.display = '';
	    
	    curSummary.onclick = function(){
	        if($(this).hasClass('hidden')){
	            $(this).parent().find("ol").show("slow");
	            $(this).removeClass('hidden');
	        }else{
	            $(this).parent().find("ol").hide("slow");
	            $(this).addClass('hidden');
	        }
	    }
	});
	
	/*
	 * Enable collapse/expand each failed step (included picture)
	 */
	$("li.step.failed").each(function(index, curStep){
	    curStep.style.cursor = 'pointer';

	    $(this).parent().find("div > img").hide();
        $(this).addClass('hidden');
	    
	    curStep.onclick = function(){
	        if($(this).hasClass('hidden')){
	            $(this).parent().find("div > img").show("slow");
	            $(this).removeClass('hidden');
	        }else{
	            $(this).parent().find("div > img").hide("slow");
	            $(this).addClass('hidden');
	        }
	    };
	});
	
	document.getElementById("collapserFail").onclick = function(){
		$("section.scenario.failed > details > summary").each(function(index, curSummary) {
			$(curSummary).addClass('hidden');
		});
	    $("section.scenario.failed").find("ol").hide();
	};
	
	document.getElementById("expanderFail").onclick = function(){
		$("section.scenario.failed > details > summary").each(function(index, curSummary) {
			$(curSummary).removeClass('hidden');
		});
	    $("section.scenario.failed").find("ol").show();
	};
	
	document.getElementById("collapserAll").onclick = function(){
		$("section.scenario > details > summary").each(function(index, curSummary) {
			$(curSummary).addClass('hidden');
		});
	    $("section.scenario").find("ol").hide();
	};
	
	document.getElementById("expanderAll").onclick = function(){
		$("section.scenario > details > summary").each(function(index, curSummary) {
			$(curSummary).removeClass('hidden');
		});
	    $("section.scenario").find("ol").show();
	};
	
	cucumberHeaderTotals = document.getElementById("totals");
	
	/* set scenario count */
	cucumberHeaderTotals.innerHTML = scenarios.total + ' scenarios (' + scenarios.failed + ' / ' + scenarios.failedPercent + '% failed, ' + scenarios.passed + ' / ' + scenarios.passedPercent + '% passed)';
	
	cucumberHeaderTotals.innerHTML += '<br />';
	
	/* set step count */
	cucumberHeaderTotals.innerHTML += steps.total + ' steps (' + steps.failed + ' / ' + steps.failedPercent + '% failed, ' + steps.skipped + ' / ' + steps.skippedPercent + '% skipped, ' + steps.passed + ' / ' + steps.passedPercent + '% passed)';

});