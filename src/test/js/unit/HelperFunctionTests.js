describe('Helper Functions', function() {

	beforeEach(function() {
	});

	describe('deleteFromCollection', function(){

        it('should delete simple elements correctly', function() {
        	var list = ['a', 'b', 'c'];
        	deleteFromCollection(list, 'b');
        	expect('a').toEqual( list[0] );
        	expect('c').toEqual( list[1] );
        });
        
        it('should delete objects correctly, based on ==', function() {
        	var object = {'b':'2', 'y':'2'};
        	var list = [ {'a':'1', 'x':'2'},
        	             object,
        	             {'c':'3', 'z':'2'},
        	             ];
        	deleteFromCollection(list, object );
        	expect('1').toEqual( list[0]['a'] );
        	expect('3').toEqual( list[1]['c'] );
        });

        it('should not throw an error for unknown elements', function() {
        	var list = ['a', 'b', 'c'];
        	deleteFromCollection(list, 'x');
        	expect('a').toEqual( list[0] );
        	expect('b').toEqual( list[1] );
        	expect('c').toEqual( list[2] );
        });

	});
	
	describe('fuzzyMatch', function(){

        it('should match strings which differ in non characters', function() {
        	expect(true).toEqual(fuzzyMatch("Hallo", "Hallo"));
        	expect(false).toEqual(fuzzyMatch("Ballo", "Hallo"));
        	expect(true).toEqual(fuzzyMatch("Hello World!", "hello   world"));
        	expect(false).toEqual(fuzzyMatch("EJB3", "EJB4"));
        });        
        
    });
	
	describe('revoveHTMLAndStyleButNotBr', function(){

        it('should return clean text', function() {
        	var html = '<style type="text/css">P { margin-bottom: 0.21cm; }P.western { font-family: "Museo Sans",sans-serif; font-size: 11pt; }P.cjk { font-size: 10pt; }A:link {  }</style>\
        		<p style="margin-bottom: 0cm; background: #b3b3b3; border-top: 1px solid #ffffff; border-bottom: 1px solid #ffffff; border-left: 1px solid #ffffff; border-right: none; padding-top: 0.05cm; padding-bottom: 0.05cm; padding-left: 0.05cm; padding-right: 0cm; font-variant: normal; font-weight: demi-bold">\
        		<font color="#000000"><font face="Museo Sans, sans-serif"><span style="font-variant: normal"><font color="#000000"><font style="font-size: 11pt" size="2"><b>tarent\
        		Gm</b></font></font></span><b>bH</b></font>\
        		</font></br>Bonn</p>';

        	expect('tarent GmbH </br>Bonn').toEqual(revoveHTMLAndStyleButNotBr(html));
        });        
    });
});
