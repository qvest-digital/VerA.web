describe('Registration Test Suits', function() {

	describe('RegistrationController Test', function(){
	var acceptanceOptions =[{"id": 0, "label": "Offen"},
        {"id": 1, "label": "Zusage"},
        {"id": 2, "label": "Absage"}
	];

	var eventList =[{"pk":"1","shortname":"Tag der deutschen Einheit","datebegin":"2014-11-03 00:00:00+02"},
            {"pk":"2","shortname":"Woche der Brüderlichkeit","datebegin":"2014-10-13 00:00:00+02"},
            {"pk":"3","shortname":"Kamingespräch","datebegin":"2014-10-28 18:00:00+02","location":{"pk":1,"locationname":"Kamin"}}
        ];

	var scope, ctrl, routeParams;

	beforeEach(module('onlineRegApp'));

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {

            scope = $rootScope.$new();
	    routeParams = {};
	    routeParams.eventId = 1;
	    routeParams.acceptanceId = 0;
	    routeParams.noteToHost="wir sind dabei";
	    //routeParams.

            //create new RegisterController
            ctrl = $controller('RegisterController', {$scope: scope, $routeParams: routeParams});

	    //for(var i=1;i<4;i++){
		    _$httpBackend_.whenGET('api/event/'+routeParams.eventId).respond(eventList[routeParams.eventId-1]);
	    //}

		_$httpBackend_.whenGET('api/event/'+routeParams.eventId+'/register/2').respond(acceptanceOptions[routeParams.acceptanceId]);

		//_$httpBackend_.whenGET('api/event/'+routeParams.eventId+'/register/2').respond(routeParams.noteToHost);

            _$httpBackend_.verifyNoOutstandingExpectation();
            _$httpBackend_.flush();
            }));

        it('should have loaded the selected event', function() {
        	expect(scope.event).toEqual(eventList[0]);
		expect(scope.acceptance).toEqual(acceptanceOptions[routeParams.acceptanceId]);
		//expect(scope.noteToHost).toEqual(routeParams.noteToHost);
		//_$httpBackend_.expectPOST('api/event/' + routeParams.eventId + '/register/2').respond();

        });

	});
});
