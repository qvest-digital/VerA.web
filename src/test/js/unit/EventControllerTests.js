describe('Online Registration App', function() {

	describe('Eventontroller Test', function(){
        var eventList =[{"pk":"1","shortname":"Tag der deutschen Einheit","datebegin":"2014-11-03 00:00:00+02"},
            {"pk":"2","shortname":"Woche der Brüderlichkeit","datebegin":"2014-10-13 00:00:00+02"},
            {"pk":"3","shortname":"Kamingespräch","datebegin":"2014-10-28 18:00:00+02","location":{"pk":1,"locationname":"Kamin"}}
        ];

        var scope, ctrl;

        beforeEach(module('onlineRegApp'));

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {

            _$httpBackend_.whenGET('api/event/list').respond(eventList);

            scope = $rootScope.$new();

            //create new EventController
            ctrl = $controller('EventController', {$scope: scope});

            _$httpBackend_.verifyNoOutstandingExpectation();
            _$httpBackend_.flush();
        }));

        it('should have loaded the events on startup', function() {
        	expect(scope.events).toEqual(eventList);
        });

	});
});
