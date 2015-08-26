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

    var exchange = function(request) {
        $http.post('exchange.json', request).success(function(response) {
            s.configurations = response.configurations;
            s.experimentInput = response.experimentInput;
            if (response.message) {
                setTimeout(function() {
                    alert(response.message);
                }, 100);
            }
        });
    };

    s.init = function() {
        exchange({
            action : 'init'
        });
    };

    s.addUri = function() {
        exchange({
            action : 'add uri',
            uri : s.uri
        });
        s.uri = '';
    };

    s.submitConfig = function() {
        exchange({
            action : 'submit config',
            configurations : {
                constraints : selectedConstraints(),
                equivalences : selectedEquivalences()
            }
        });
    };

    s.submitInput = function() {
        exchange({
            action : 'submit input',
            experimentInput : s.experimentInput
        });
    };

    s.format = function(key) {
        var result = s.configurations && s.configurations[key] ? s.configurations[key] : '';
        var matches = result.match(/^(Individual.*|\s{4}Facts:\$|\s{4}EquivalentTo:\s)$/mg);
        if (matches !== null) {
            matches.forEach(function(match) {
                result = result.replace(match, '<span class="comment">' + match + '</span>');
            });
        }
        return $sce.trustAsHtml(result);
    };

    s.hasConstraintsAndEquivalences = function() {
        return hasConfigProperty('constraints') && hasConfigProperty('equivalences');
    };

    s.hasExperimentInput = function() {
        return s.experimentInput && s.experimentInput.values && Object.keys(s.experimentInput.values).length > 0;
    };

    s.hasDatamodel = function() {
        return s.configurations && s.configurations.datamodel;
    };

} ]);

aodApp.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if (event.which === 13) {
                scope.$apply(function() {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});