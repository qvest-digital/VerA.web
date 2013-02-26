	function activateTab(tab, link) {
		if ( tab )
		{
			if ( tab.className == "tabdisabled" )
			{
				tab.style.borderBottom = 'none';
//				tab.style.backgroundColor = '#b3b8bb';
				tab.style.backgroundColor = '#f3f8ff';
				if (link) {
					link.style.color = '#000000';
		/*			link.style.cursor = 'default'; TODO */
				}
			}
			else
			{
				tab.className = "tabactiv";
/*				tab.style.borderTop = '1px solid #C0C7F0'; */
/*				tab.style.borderLeft = '1px solid #C0C7F0'; */
				tab.style.borderBottom = 'none';
				tab.style.backgroundColor = '#f3f8ff';
				if (link) {
					link.style.color = '#336699';
		/*			link.style.cursor = 'default'; TODO */
				}
			}
		}
	}

	function deactivateTab(tab, link) {
		if ( tab )
		{
			if ( tab.className == "tabdisabled" )
			{
				disableTab( tab, link );
			}
			else
			{
				tab.className = "tabinactiv";
				tab.style.borderBottom = '1px solid #80A7E0';
				tab.style.backgroundColor = '#d3d8df';
				if (link) {
	/*				link.style.cursor = 'pointer'; TODO */
				}
			}
		}
	}

	function disableTab(tab, link) {
		if (tab) {
			tab.style.borderBottom = '1px solid #80A7E0';
			tab.style.backgroundColor = '#93989f';
		}
		if (link) {
			link.style.color = '#000000';
/*			link.style.cursor = 'default'; TODO */
		}
	}

	function changeTab(activate, deactivate, disable) {
		if (disable)
			for (var i = 0; i < disable.length; i++)
				disableTab(document.getElementById(disable[i] + '-tab'), document.getElementById(disable[i] + '-link'));
		if (deactivate)
			for (var i = 0; i < deactivate.length; i++)
				deactivateTab(document.getElementById(deactivate[i] + '-tab'), document.getElementById(deactivate[i] + '-link'));
		if (activate)
			activateTab(document.getElementById(activate + '-tab'), document.getElementById(activate + '-link'));
	}

	function showBlock(block) {
		if (block)
			block.style.display = '';
	}

	function hideBlock(block) {
		if (block)
			block.style.display = 'none';
	}

	function changeBlock(activate, deactivate) {
		if (deactivate)
			for (var i = 0; i < deactivate.length; i++)
				hideBlock(document.getElementById(deactivate[i] + '-block'));
		if (activate)
			showBlock(document.getElementById(activate + '-block'));
	}
	
	function getParameter(name) {
		var url = window.location.href;
		var i = url.indexOf(name + '=');
		if (i >= 0) {
			i += name.length + 1;
			url = url.substring(i);
			i = url.indexOf('&');
			if (i >= 0) {
				url = url.substring(0, i);
			}
			i = url.indexOf('?');
			if (i >= 0) {
				url = url.substring(0, i);
			}
			return url;
		} else {
			return null;
		}
	}

	function getTabParameter(standard) {
		var value = getParameter('tab');
		if (value == null) {
			return standard;
		} else {
			return value;
		}
	}
