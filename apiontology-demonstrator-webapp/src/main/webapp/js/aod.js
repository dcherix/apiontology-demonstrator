var aodApp = angular.module('aodApp', []);

aodApp.controller('MainController', [ '$scope', '$element', '$http', '$sce', function(s, $element, $http, $sce) {
    s.init = function() {
        $http.get('configurations.json').success(function(configurations) {
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

        $http.post('configurations.json', configurations).success(function(configurations) {
            s.configurations = configurations;
        });
    };

    s.datamodel = function() {
        return $sce.trustAsHtml(s.configurations && s.configurations.datamodel ? s.configurations.datamodel : '');
    };
} ]);