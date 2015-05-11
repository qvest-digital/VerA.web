/**
 * Created by mley on 21.07.14.
 */

var onlineRegApp = angular.module('onlineRegApp', [ 'ngRoute', 'ui.bootstrap', 'pascalprecht.translate' ]);

onlineRegApp.run(function ($rootScope) {
	$rootScope.parseDate = function (dt) {
			return moment(dt).toDate();
	};

	$rootScope.isUserLoged = function () {
		return $rootScope.user_logged_in != null;
	}

	$rootScope.cleanMessages = function() {
		$rootScope.previousMessage = null;
		$rootScope.messageContent = null;
	}
});

onlineRegApp.config(function ($routeProvider, $translateProvider) {

    $routeProvider.when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginController'
    }).when('/event', {
        templateUrl: 'partials/event.html',
        controller: 'EventController'
    }).when('/register/:eventId', {
        templateUrl: 'partials/register.html',
        controller: 'RegisterController'
    }).when('/register/', {
        templateUrl: 'partials/register_user.html',
        controller: 'RegisterUserController'
    }).when('/veranstaltungen', {
        templateUrl: 'partials/meine-veranstaltungen.html',
        controller: 'VeranstaltungsController'
    }).when('/kontaktdaten' , {
    	templateUrl: 'partials/kontaktdaten.html',
    	controller: 'KontaktdatenController',
    }).when('/delegation/:uuid', {
      templateUrl: 'partials/delegation.html',
      controller: 'DelegationController'
    }).when('/media/:uuid', {
      templateUrl: 'partials/media.html',
      controller: 'MediaController'
    }).when('/freevisitors/:uuid', {
        template: "",
        controller: 'FreeVisitorController'
    }).when('/update/:eventId', {
		templateUrl: 'partials/update.html',
		controller: 'UpdateController'
    }).when('/reset/password/:uuid', {
		templateUrl: 'partials/reset_password.html',
		controller: 'ResetPasswordController'
    }).otherwise({
      redirectTo: '/event'
    })

	$translateProvider.useStaticFilesLoader({
		prefix: 'languages/lang-',
		suffix: '.json'
	});

	$translateProvider.preferredLanguage('de_DE');
});

onlineRegApp.directive('equals', function() {
	return {
		restrict: 'A',
		require: '?ngModel',
		link: function(scope, elem, attrs, ngModel) {
			if (!ngModel) return;

			scope.$watch(attrs.ngModel, function() {
				validate();
			});

			attrs.$observe('equals', function(val) {
				validate();
			});

			var validate = function() {
				var val1 = ngModel.$viewValue;
				var val2 = attrs.equals;

				ngModel.$setValidity('equals', !val1 || !val2 || val1 === val2);
			}
		}
	}
});

onlineRegApp.controller('LanguageSelectController', function ($scope, $translate) {
  $scope.langKey = 'de_DE';
  $scope.changeLang = function (key) {
	$translate.use(key).then(function (key) {
	  console.log("Sprache zu " + key + " gewechselt.");
	}, function (key) {
	  console.log("Irgendwas lief schief.");
	});
  };
});

onlineRegApp.controller('ResetPasswordController', function($http, $scope, $routeParams, $location, $rootScope, $translate) {
	$scope.resetPassword = function() {
		if ($scope.resetPasswordForm.password.$viewValue != $scope.resetPasswordForm.passwordRepeat.$viewValue) {
			$translate('REGISTER_USER_MESSAGE_PASSWORD_REPEAT_ERROR').then(function (text) {
				$scope.error = text;
			});
		} else {
			$scope.error = null;

			$http({
				method: 'POST',
				url: 'api/reset/password/' + $routeParams.uuid,
				headers: {"Content-Type" : undefined},
				data: $.param({
					password: $scope.resetPasswordForm.password.$viewValue
				})
			}).success(function () {
				$translate('USER_PASSWORD_CHANGE_SUCCESS_MESSAGE').then(function (text) {
					$rootScope.success = text;
				});
				$location.path('/event');
			});
		}
	}
});

onlineRegApp.controller('FreeVisitorController', function($http, $scope, $location, $routeParams, $translate) {
	$http({
		method: 'GET',
		url: 'api/freevisitors/'+ $routeParams.uuid
	}).success(function (result) {
		$location.path('/register/' + result);
	}).error(function (data, status, headers, config) {
		$translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
			$scope.error = text;
		});
	});
});

onlineRegApp.controller('UpdateController', function($scope, $rootScope, $location, $routeParams, $http, $translate) {

	if ($rootScope.user_logged_in == null) {

		$scope.setNextPage('register/' + $routeParams.eventId);
		$location.path('/login');

	} else {

		$scope.acceptanceOptions = [
			{id: 0, name:"USER_EVENTS_STATUS_TYPE_OPEN"},
			{id: 1, name:"USER_EVENTS_STATUS_TYPE_ACCEPTANCE"},
			{id: 2, name:"USER_EVENTS_STATUS_TYPE_REFUSE"}
		];

		$http.get('api/update/' + $routeParams.eventId).success(function (result) {
			$scope.event = result;
			$scope.acceptance = $scope.acceptanceOptions[$scope.event.status];
			$scope.noteToHost = $scope.event.message;
			console.log("Auswahl: " + $scope.event.shortname);
		}).error(function (data, status, headers, config) {
			$translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
				$scope.error = text;
			});
		});

		$scope.update = function () {
			$http({
				method: 'POST',
				url: 'api/update/' + $routeParams.eventId + '/update',
				headers: {"Content-Type" : undefined},
				data: $.param({
						notehost: $scope.noteToHost,
						invitationstatus: $scope.acceptance.id
				})
			}).success(function (result) {
				if (result.status === 'OK') {
					$translate(['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_ONE','USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_TWO']).then(function (translations) {
						$rootScope.previousMessage = translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENTS_STATUS_CHANGED_SUCCESSFULL_MESSAGE_PART_TWO'];
					});

					$scope.setNextPage('veranstaltungen');
					$location.path($scope.nextPage);
				} else if (result.status === 'NOT_REGISTERED') {
					$translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
						$scope.error = text;
					});
				}
			});
		}
	}
});

onlineRegApp.controller('MediaController', function ($scope, $http, $rootScope, $location, $routeParams, $translate) {
	$scope.genderOptions = [
		{id: 0, name:"GENERIC_PLEASE_SELECT"},
		{id: 1, name:"GENERIC_GENDER_MALE"},
		{id: 2, name:"GENERIC_GENDER_FEMALE"}
	];

	$scope.gender = $scope.genderOptions[0];

	$http.get('api/media/' + $routeParams.uuid).then(function(presentPersons) {
		$scope.presentPersons = presentPersons.data;
	});

	$scope.register_pressevertreter = function () {
		if ($scope.gender.id == 0) {
			$translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
				$scope.error = text;
			});
			$scope.success = null;
		} else if ($scope.gender.id == 1 || $scope.gender.id == 2){
			if ($scope.lastname != null && $scope.firstname != null && $scope.email != null && $scope.address != null && $scope.plz != null && $scope.city != null && $scope.country != null) {
				$translate('GENERIC_ERROR').then(function (text) {
					var ERROR_TEXT = text;
				});
				$scope.button = true;
				console.log("registering delegierten in the event.");
				$http({
					method: 'POST',
					url: 'api/media/' + $routeParams.uuid + '/register',
					headers: {"Content-Type" : undefined},
					data: $.param({
							nachname: $scope.lastname,
							vorname: $scope.firstname,
							gender: $scope.gender.label,
							email: $scope.email,
							address: $scope.address,
							plz: $scope.plz,
							city: $scope.city,
							country: $scope.country
					})
				}).success(function (result) {
					$scope.success = null;
					$scope.error = null;

					if (result.status === 'NO_EVENT_DATA') {
						$translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
							$scope.error = text;
						});
						$scope.success = null;
					}  else if (result.status === 'WRONG_EVENT') {
						$translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
							$scope.error = text;
						});
						$scope.success = null;
					} else if (result.status === 'OK') {
						$scope.error= null;
						$translate('MEDIA_REPRESEINTATIVES_REGISTER_SUCCESSFULL_MESSAGE').then(function (text) {
							$scope.success = text;
						});
						$http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
							$scope.presentPersons = presentPersons.data;
						});

						$scope.lastname = null;
						$scope.firstname = null;
						$scope.gender = $scope.genderOptions[0];
						$scope.email = null;
						$scope.address = null;
						$scope.plz = null;
						$scope.city = null;
						$scope.country = null;
					} else {
						$scope.error = ERROR_TEXT;
						$scope.success = null;
					}
					$scope.button = false;
				}).error(function (data, status, headers, config) {
					$scope.error = ERROR_TEXT;
					$scope.button = false;
				});
			} else {
				$translate('GENERIC_MESSAGE_FILL_IN_ALL_FIELDS').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			}
		}
	}
});

onlineRegApp.controller('DelegationController', function ($scope, $http, $rootScope, $location, $routeParams, $translate) {
	$rootScope.cleanMessages();
	if ($rootScope.user_logged_in == null) {
		$scope.setNextPage('delegation/' + $routeParams.uuid);
		$location.path('/login');
	} else {
		$scope.genderOptions = [
			{id: 0, name:"GENERIC_PLEASE_SELECT"},
			{id: 1, name:"GENERIC_GENDER_MALE"},
			{id: 2, name:"GENERIC_GENDER_FEMALE"}
		];

		$scope.categoryNames = [
			{id: 0, name:"Test"},
		]

		$scope.functionSignNames = []

		$http.get('api/delegation/fields/list/category/' + $routeParams.uuid).then(function(categoryNames) {
			//first label of categories
			//{id: 0, name:"DELEGATION_OPTION_CATEGORY"}

			$scope.categoryNames = categoryNames.data;
		});

		$http.get('api/delegation/fields/list/function/' + $routeParams.uuid).then(function(functionNames) {
			$scope.functionSignNames = functionNames.data;
		});

		$scope.gender = $scope.genderOptions[0];
		$scope.success = null;
		$scope.error = null;

		$http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
				$scope.presentPersons = presentPersons.data;
		});

		$scope.register_user = function() {
			if ($scope.gender.id == 0) {
				$translate('GENERIC_MISSING_GENDER_MESSAGE').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			} else if($scope.vorname.length > 35) {
				$translate('DELEGATION_MESSAGE_FIRSTNAME_MAX').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			} else if($scope.nachname.length > 35) {
				$translate('DELEGATION_MESSAGE_LASTNAME_MAX').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			} else if (typeof $scope.vorname == "undefined" && $scope.vorname == null) {
				$translate('DELEGATION_MESSAGE_MISSING_FIRSTNAME').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			} else if (typeof $scope.nachname == "undefined" && $scope.nachname == null) {
				$translate('DELEGATION_MESSAGE_MISSING_LASTNAME').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			} else if ($scope.gender.id == 1 || $scope.gender.id == 2 && typeof $scope.vorname != "undefined" &&
						$scope.vorname != null && typeof $scope.nachname != "undefined" && $scope.nachname != null) {
				$translate('GENERIC_ERROR').then(function (text) {
					var ERROR_TEXT = text;
				});

				$scope.button = true;
				console.log("registering delegation in the event.");

				if (($scope.nachname != null && $scope.nachname != '') && ($scope.vorname != null && $scope.vorname != '')) {
					//Empty the standard label for saving
					//No required fields
					if ($scope.category == null) {
						$scope.category = "";
					}
					if ($scope.functionSign == null) {
						$scope.functionSign = "";
					}

					$http({
						method: 'POST',
						url: 'api/delegation/' + $routeParams.uuid + '/register',
						dataType: 'text',
						headers: {
								"Content-Type": undefined
						},

						data: $.param({
							firstname: $scope.nachname,
							lastname: $scope.vorname,
							gender: $scope.gender.label,
							category: $scope.category,
							fields: $scope.fields,
							functionDescription: $scope.functionDescription
						})
					}).success(function(result) {
						$scope.success = null;
						$scope.error = null;

						if (result.status === 'NO_EVENT_DATA') {
							$translate('GENERIC_MESSAGE_EVENT_DOESNT_EXISTS').then(function (text) {
								$scope.error = text;
							});
							$scope.success = null;
						} else if (result.status === 'WRONG_DELEGATION') {
							$translate('DELEGATION_MESSAGE_DELEGATION_DOESNT_EXISTS').then(function (text) {
								$scope.error = text;
							});
							$scope.success = null;
						} else if (result.status === 'OK') {
							$scope.error = null;
							$translate('DELEGATION_MESSAGE_DELEGATION_DATA_SAVED_SUCCESSFULL').then(function (text) {
								$scope.success = text;
							});
							$scope.gender = $scope.genderOptions[0];
							$scope.category = $scope.categoryNames[0];
							$scope.functionSign = $scope.functionSignNames[0];
							$scope.nachname = null;
							$scope.vorname = null;

							$http.get('api/delegation/' + $routeParams.uuid).then(function(presentPersons) {
								$scope.presentPersons = presentPersons.data;
							});
						} else {
							$scope.error = ERROR_TEXT;
							$scope.success = null;
						}

						$scope.button = false;

					}).error(function(data, status, headers, config) {

					});
				}
			} else {
				$translate('GENERIC_MESSAGE_FILL_IN_ALL_FIELDS').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			}
		}

		$scope.showOptionalFields = function (personId) {

			$scope.targetPersonId=personId;
			$translate('DELEGATION_MESSAGE_NO_EXTRA_FIELDS').then(function (text) {
				var ERROR_TEXT = text;
			});

			$scope.success = null;
			$scope.error = null;
			$scope.labellist = {};

			$http.get('api/delegation/' + $routeParams.uuid + '/data/').then(function(fields) {
				$scope.fields = fields.data;
				console.log("number of fields: "+$scope.fields.length)
				if ($scope.fields.length == 0) {
					$scope.error_dialog = ERROR_TEXT;
					$scope.success = null;
					$scope.hideDialog = true;
				} else {
					$scope.error_dialog = null;
					$scope.hideDialog = false;
					console.log(JSON.stringify($scope.fields));

					for(var prop in $scope.fields) {
						var curField = $scope.fields[prop];
						if(curField.value != null){
							$scope.labellist[curField.pk] = curField.value;
							console.log(curField.value + "|" + $scope.labellist[curField.pk]);
						}
					}
				}
			});
		}

		$scope.saveOptionalFields = function () {
			$scope.success = null;
			$scope.error = null;

			var list = $scope.labellist;
			console.log("labels " + list);

			$http({
				method: 'POST',
				url: 'api/delegation/'+ $routeParams.uuid + '/fields/save',
				headers: {"Content-Type" : undefined},
				data: $.param({
					fields: JSON.stringify($scope.labellist),
					personId: $scope.targetPersonId
				})
			}).success(function (result) {
				$scope.error= null;
				$translate('DELEGATION_MESSAGE_DELEGATION_DATA_SAVED_SUCCESSFULL').then(function (text) {
					$scope.success = text;
				});
			}).error(function (data, status, headers, config) {
				console.log('ERROR! Optional fields not saved!”');
				$translate('GENERIC_ERROR').then(function (text) {
					$scope.error = text;
				});
				$scope.success = null;
			});
		}
	}
});

onlineRegApp.controller('DirectLoginController', function ($scope, $location, $http, $rootScope, $translate) {
	$scope.button = false;
	$rootScope.cleanMessages();
	$rootScope.no_messages = function() {
		$rootScope.status = null;
		$rootScope.messageContent = null;
	}

	$scope.logout = function () {
		$http({
			method: 'POST',
			url: 'api/idm/logout/'
		}).success(function (result) {
			$rootScope.error = null;
			$scope.directusername = null;
			$scope.directpassword = null;

			$translate('GENERIC_LOGOUT_SUCCESSFULL_MESSAGE').then(function (text) {
				$rootScope.messageContent = text;
			});

			$rootScope.status = "success";
			$rootScope.user_logged_in = null;
			$rootScope.userinfo = null;
			$location.path('/');
		}).error(function (data, status, headers, config) {
			console.log('ERROR! Logout failed!”')
		});
	}

	$scope.direct_login = function () {
		$scope.button = true;
		console.log("logging in.");

		$http({
			method: 'POST',
			url: 'api/idm/login/' + encodeURIComponent($scope.directusername),
			headers: {"Content-Type" : undefined},
			data: $.param({
					password: $scope.directpassword
			})
		}).success(function (result) {
			$scope.button = false;
			$rootScope.error = null;
			if (result != "") {
				$rootScope.userinfo = result.status;
				console.log("Login erfolgreich");
				$rootScope.user_logged_in = $scope.directusername;
				$rootScope.status = null;
				$rootScope.messageContent = null;
			} else {
				$rootScope.userinfo = null;
				$translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
					$rootScope.messageContent = text;
				});
				$rootScope.status = "danger";
			}
		}).error(function (data, status, headers, config) {
			$scope.button = false;
			$translate('GENERIC_ERROR').then(function (text) {
				$rootScope.messageContent = text;
			});
			$rootScope.status = "danger";
		});
	}

	$scope.setStatus = function(value) {
			$scope.x = value;
	}

	$scope.setMessageContent = function(value) {
			$scope.x = value;
	}

	$scope.setNextPage = function(value) {
		$scope.nextPage = "/" + value;
	}
});

onlineRegApp.controller('LoginController', function ($scope, $location, $http, $rootScope, $translate) {
	$rootScope.button = false;
	$rootScope.status = null;
	$rootScope.messageContent = null;
	$rootScope.cleanMessages();

	$scope.login = function () {
		$scope.button = true;
		console.log("logging in.");

		$http({
			method: 'POST',
			url: 'api/idm/login/' + encodeURIComponent($scope.username),
			headers: {"Content-Type" : undefined},
			data: $.param({
				password: $scope.password
			})
		}).success(function (result) {
			$scope.button = false;
			if (result != "") {
				$rootScope.user_logged_in = $scope.username;
				$rootScope.userinfo = result.status;
				$rootScope.status = null;
				$rootScope.messageContent = null;
				$location.path($scope.nextPage);
			} else {
				$rootScope.status = "danger";
				$translate('GENERIC_MESSAGE_USER_OR_PASSWORD_WRONG').then(function (text) {
					$rootScope.messageContent = text;
				});
			}
		}).error(function (data, status, headers, config) {
			$scope.button = false;
			$rootScope.status = "danger";
			$translate('GENERIC_ERROR').then(function (text) {
				$rootScope.messageContent = text;
			});
		});
	}
});

onlineRegApp.controller('EventController', function ($scope, $http, $rootScope) {
	$http.get('api/event/list/' + $rootScope.user_logged_in).success(function (result) {
		console.log("loaded data");
		$scope.events = result;
	});
});

onlineRegApp.controller('RegisterController', function ($scope, $rootScope, $location, $routeParams, $http, $translate) {

	if ($rootScope.user_logged_in == null) {
		$scope.setNextPage('register/' + $routeParams.eventId);
		$location.path('/login');
	} else {
	    $http.get('api/event/' + $routeParams.eventId).success(function (result) {
	        $scope.event = result;
	        console.log("Auswahl: " + $scope.event.shortname);
	    });

	    $http.get('api/event/' + $routeParams.eventId + '/register/' + $scope.userId).success(function (result) {
	    	if (!isUserLoged()) {

	    		$location.path('/login');
	    	} else {
		        if (result.invitationstatus) {
		            $scope.acceptance = $scope.acceptanceOptions[result.invitationstatus];
		        }
		        if (result.notehost) {
		            $scope.noteToHost = result.notehost;
		        }
		        console.log("Teilnahme: " + $scope.acceptance.label);
	    	}
	    });

	    $scope.save = function () {
	        $http({
	            method: 'POST',
	            url: 'api/event/' + $routeParams.eventId + '/register',
	            headers: {"Content-Type" : undefined},
	            data: $.param({
	                notehost: $scope.noteToHost
	            })
	        }).success(function (result) {
	        	if (result.status === 'OK') {
	        		$translate(['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE','USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO']).then(function (translations) {
						$rootScope.previousMessage = translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_ONE'] + " \"" + $scope.event.shortname + "\" " + translations['USER_EVENT_REGISTER_MESSAGE_SUCCESSFUL_PART_TWO'];
					});
	        		console.log("Teilnahme gespeichert: " + result);
	        		$scope.setNextPage('veranstaltungen');
	        		$location.path($scope.nextPage);
	        	} else if (result.status === 'REGISTERED') {
	        		$translate('USER_EVENTS_STATUS_CHANGED_ERROR_MESSAGE').then(function (text) {
						$scope.error = text;
					});
	        	}
	        });
	    }
	}
});

onlineRegApp.controller('RegisterUserController',  function($scope, $http) {
	$scope.status = 0;

	$scope.register = function(isValid) {
		if(!isValid) { return; }
		$http({
			method: 'POST',
			url: 'api/user/register/' + encodeURIComponent($scope.osiam.userName) + '',
				headers: {"Content-Type" : undefined},
				data: $.param({
				osiam_firstname: $scope.osiam.firstName,
				osiam_secondname: $scope.osiam.lastName,
				osiam_password1: $scope.osiam.password
			})
		}).success(function (result) {
			switch(result.status) {
			case 'OK':
				$scope.status = 1;
				break;
			case 'USER_EXISTS':
				$scope.status = 'e1';
				break;
			case 'INVALID_USERNAME':
				$scope.status = 'e2';
				break;
			default:
				$scope.status = 'e';
			}

			window.console.log("User registered with response:  '" + result + "'.");
		}).error(function (data, status, headers, config) {
			$scope.status = 'e';
			window.console.log("ERROR while userregistration.");
		});
	};
});

onlineRegApp.controller('VeranstaltungsController', function ($scope, $http, $rootScope, $location) {
	if ($rootScope.user_logged_in == null) {
		$location.path('/login');
		$rootScope.success = null;
	} else {
		console.log("DEBUG: " + $rootScope.user_logged_in);
		var userEventsURL = 'api/event/userevents/' + $rootScope.user_logged_in;
		$http.get(userEventsURL).success(function (result) {
				console.log("Loading user's subscribed events...");
				$scope.events = result;
		});
	}
});

onlineRegApp.controller('KontaktdatenController', function ($scope, $location, $rootScope, $translate, $http) {
	$rootScope.cleanMessages();
	if ($rootScope.user_logged_in == null) {
			$location.path('/login');
	} else {
		$http.get('api/event/list/{userid}/').success(function (result) {
						console.log("loaded data");
						$scope.events = result;
		});
	}
});
