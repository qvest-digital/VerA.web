// This file is part of VerA.web and published under the same licence.

(function(){
	$(document).ready(function() {
	    var backtotop = ['<div id="gotop"></a>'].join("");
        	$("body").append(backtotop);
            $('#gotop').gotop({
             // Background color
              background : '#8B0F0F',

              // Icon color
              color: '#fff',

              // Rounded button?
              rounded: true,

              // width/height
              width: '35px',
              height: '35px',

              // bottom position
              bottom : '25px',

              // right position
              right : '25px',

              // Window height after which show the button
              windowShow: 200,

              // animation speed
              speed: 1000,

              // custom html for the back to top button
              customHtml: '',

              // Show button only on mobile device
              mobileOnly: false
            });
        });
})();
