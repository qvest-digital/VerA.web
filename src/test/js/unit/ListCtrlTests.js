describe('List controller', function() {

	beforeEach(function() {
	});

	var allSkillList = [
	 {
	    "name" : "Wireshark",
	    "category" : "tooling",
	    "id" : "Wireshark",
	 },
	 {
	    "name" : "WS/SOAP",
	    "category" : "paradigm",
	    "id" : "WSSOAP",
	 },
	 {
	    "name" : "XSLT",
	    "category" : "prog",
	    "id" : "XSLT",
	 },
	 {
	    "name" : "YourKit Profiler",
	    "category" : "tooling",
	    "id" : "YourKitProfiler",
	 },
	 {		
	    "name" : "YouTrack",
	    "description" : "http://www.jetbrains.com/youtrack/\n\nJetBrains Bugtracker mit Agile Project Management Support, etc...",
	    "id" : "YouTrack",
	 }
	];
	
	describe('ListCtrl searching', function(){

        var scope, ctrl, skills, $httpBackend;
 
        beforeEach(module('cvio'));

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        	$httpBackend = _$httpBackend_;
        	$httpBackend.whenGET('/api/skills').respond(allSkillList);
        	$httpBackend.whenGET('/api/cv/cvs?fields=familyName&fields=givenName&fields=skills').respond([{'givenName' : 'Bob'}]);
        	
            scope = $rootScope.$new();
            scope.cv = {};
            ctrl = $controller('ListCtrl', {$scope: scope});
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.flush();
        }));

        it('should load the cvs at startup', function() {
        	expect('Bob').toEqual(scope.cvs[0]['givenName']);        	
        });

        it('calculateSearchScorePercent works correct', function() {
        	var cv = {
        			skills: { "Wireshark": 3,
        					   "WSSOAP" : 1 } 
        	};
        	
        	scope.searchSkillItems =  [ allSkillList[0] ];

        	
       	 	expect(100).toEqual( scope.calculateSearchScorePercent(cv) );

        	scope.searchSkillItems =  [ allSkillList[0], allSkillList[1] ];
        	expect(67).toEqual( scope.calculateSearchScorePercent(cv) );

        	scope.searchSkillItems =  [ allSkillList[0], allSkillList[1], allSkillList[2]  ];
        	expect(44).toEqual( scope.calculateSearchScorePercent(cv) );

        	scope.searchSkillItems =  [ allSkillList[2]  ];
        	expect(0).toEqual( scope.calculateSearchScorePercent(cv) );

       	 	// if no search criteria is selected, this is a full match
        	scope.searchSkillItems =  [ ];
        	expect(100).toEqual( scope.calculateSearchScorePercent(cv) );
        });
        
        it('bySearchCriteria filters correct', function() {

        	var cvMatch = { skills: { "Wireshark": 3, "WSSOAP" : 1 }};
        	var cvNonMatch = { skills: { "foo": 3 }};        	
        	scope.searchSkillItems =  [{ "id" : "Wireshark" }];
        	                        	         	
       	 	expect(true).toEqual( scope.bySearchCriteria()(cvMatch) );
       	 	expect(false).toEqual( scope.bySearchCriteria()(cvNonMatch) );
       	 	
       	 	// if no search criteria is selected, every items matches
        	scope.searchSkillItems =  [];
       	 	expect(true).toEqual( scope.bySearchCriteria()(cvNonMatch) );
        });

        it('is possible to add and remove search terms', function() {
        	scope.skillItems = allSkillList;

       	 	expect(0).toEqual( scope.searchSkillItems.length );
       	 	
       	 	// direct match
       	 	scope.addSearchTerm('Wireshark');  
       	 	scope.addSearchTerm('WS/SOAP');  
       	 	expect(2).toEqual( scope.searchSkillItems.length );
       	 	
       	 	// no match -> nothing happens
       	 	scope.addSearchTerm('lkmlmlkml kmlkml kmlk');  
       	 	expect(2).toEqual( scope.searchSkillItems.length );
       	 	
       	 	// adding twice -> nothing happens
       	 	scope.addSearchTerm('Wireshark');  
       	 	expect(2).toEqual( scope.searchSkillItems.length );
       	 	
       	 	// remove one item -> removed
       	 	var leavingItem = scope.searchSkillItems[1];
       	 	scope.removeSearchSkill(scope.searchSkillItems[0]);  
       	 	expect(1).toEqual( scope.searchSkillItems.length );
       	 	expect(leavingItem).toEqual( scope.searchSkillItems[0]);
        });
	});
});
