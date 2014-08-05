describe('Skill controller', function() {

	beforeEach(function() {
		this.addMatchers({
			toEqualData : function(expected) {
				return angular.equals(this.actual, expected);
			}
		});
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
	
	describe('ListCtrl', function(){

        var scope, ctrl, skills, $httpBackend;
 
        beforeEach(module('cvio'));

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        	$httpBackend = _$httpBackend_;
        	$httpBackend.expectGET('/api/skills').respond(allSkillList);
        	
            scope = $rootScope.$new();
            scope.cv = {};
            ctrl = $controller('SkillCtrl', {$scope: scope});
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.flush();
        }));

        it('should have loaded some items', function() {        
       	 expect(scope.skillItems).toEqualData(allSkillList);
        });

        it('the category filters for the main box should work', function() {
        	var categoryFilter = scope.byCategory('tooling');

        	scope.cv.skills = {};
       	 	expect(categoryFilter(allSkillList[0])).toBeTruthy();
       	 	expect(categoryFilter(allSkillList[1])).toBeFalsy();

        	scope.cv.skills = {"Wireshark": 1};
       	 	expect(categoryFilter(allSkillList[0])).toBeFalsy();
        	scope.cv.skills = {"Wireshark": 1};
        });

	
        it('the box filter for skill selection should work', function() {
        	var bySkillSelection = scope.bySkillSelection('beginner');

        	scope.cv.skills = {};
       	 	expect(bySkillSelection(allSkillList[0])).toBeFalsy();

        	scope.cv.skills = {"Wireshark": 1};
       	 	expect(bySkillSelection(allSkillList[0])).toBeTruthy();

       	 	scope.cv.skills = {"Wireshark": 2};
       	 	expect(bySkillSelection(allSkillList[0])).toBeFalsy();
       	 	
        	bySkillSelection = scope.bySkillSelection('advanced');
       	 	scope.cvSkills = {"Wireshark": 2};
       	 	expect(bySkillSelection(allSkillList[0])).toBeTruthy();

        	bySkillSelection = scope.bySkillSelection('expert');
       	 	scope.cv.skills = {"Wireshark": 3};
       	 	expect(bySkillSelection(allSkillList[0])).toBeTruthy();
        });
	});
});
