var aodApp = angular.module('aodApp', []);

aodApp.controller('MainController', [ '$scope', '$element', '$http', function(s, $element, $http) {
    s.init = function() {
        // TODO remove mock
        s.configurations = {
            constraints : [ {
                webService : 'web-service-1',
                parameters : [ 'parameter-1', 'parameter-2', 'parameter-3' ]
            }, {
                webService : 'web-service-2',
                parameters : [ 'parameter-1', 'parameter-2', 'parameter-3' ]
            }, {
                webService : 'web-service-3',
                parameters : [ 'parameter-1', 'parameter-2', 'parameter-3' ]
            } ],
            equivalences : [ {
                parameter : 'parameter-1',
                eqParameters : [ 'eq-parameter-1', 'eq-parameter-2', 'eq-parameter-3' ]
            }, {
                parameter : 'parameter-2',
                eqParameters : [ 'eq-parameter-1', 'eq-parameter-2', 'eq-parameter-3' ]
            }, {
                parameter : 'parameter-3',
                eqParameters : [ 'eq-parameter-1', 'eq-parameter-2', 'eq-parameter-3' ]
            } ],
            datamodel : 'datamodel'
        };

        $http.get('/configurations.json').success(function(configurations) {
            s.configurations = configurations;
        });
    };

    var selectedConstraints = function() {
        var result = [];
        s.configurations.constraints.forEach(function(constraint) {
            var selected = [];
            for (var index = 0; index < constraint.parameters.length; index++) {
                if (constraint.hasOwnProperty('selected') && constraint.selected[index]) {
                    selected.push(constraint.parameters[index]);
                }
            }
            if (selected.length > 0) {
                result.push({
                    webService : constraint.webService,
                    parameters : selected
                });
            }
        });
        return result;
    }

    var selectedEquivalences = function() {
        var result = [];
        s.configurations.equivalences.forEach(function(equivalence) {
            var selected = [];
            for (var index = 0; index < equivalence.eqParameters.length; index++) {
                if (equivalence.hasOwnProperty('selected') && equivalence.selected[index]) {
                    selected.push(equivalence.eqParameters[index]);
                }
            }
            if (selected.length > 0) {
                result.push({
                    parameter : equivalence.parameter,
                    eqParameters : selected
                });
            }
        });
        return result;
    }

    s.submit = function() {
        var configurations = {
            constraints : selectedConstraints(),
            equivalences : selectedEquivalences(),
            datamodel : null
        };

        console.log(configurations)

        $http.post('/configurations.json', configurations).success(function(configurations) {
            s.configurations = configurations;
        });
    };
} ]);