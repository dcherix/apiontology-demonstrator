var aodApp = angular.module('aodApp', []);

aodApp.controller('MainController', [ '$scope', '$element', '$http', '$sce', function(s, $element, $http, $sce) {
    var hasConfigProperty = function(property) {
        return s.configurations && s.configurations[property] && s.configurations[property] instanceof Array && s.configurations[property].length > 0
    };

    var selectedConstraints = function() {
        var result = [];
        if (hasConfigProperty('constraints')) {
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
        }
        return result;
    }

    var selectedEquivalences = function() {
        var result = [];
        if (hasConfigProperty('equivalences')) {
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
        }
        return result;
    };

    var exchange = function(action, request) {
        $http.post('exchange.json', request).success(function(response) {
            s.configurations = respone.configurations;
            s.experimentInput = response.experimentInput;
        });
    };

    s.init = function() {
        exchange('init', {});
    };

    s.addUri = function() {
        exchange('add uri', {
            uri : s.uri
        });
        s.uri = '';
    };

    s.submitConfig = function() {
        exchange('submit config', {
            configurations : {
                constraints : selectedConstraints(),
                equivalences : selectedEquivalences()
            }
        });
    };

    s.submitInput = function() {
        exchange('submit input', {
            experimentInput : s.experimentInput
        });
    };

    s.datamodel = function() {
        return $sce.trustAsHtml(s.configurations && s.configurations.datamodel ? s.configurations.datamodel : '');
    };

    s.hasConstraintsAndEquivalences = function(property) {
        return hasConfigProperty('constraints') && hasConfigProperty('equivalences');
    };

    s.hasExperimentInput = function() {
        return s.experimentInput && Object.keys(s.experimentInput).length > 0;
    };

    s.hasDatamodel = function() {
        return s.configurations && s.configurations.datamodel;
    };
} ]);