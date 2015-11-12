
<!DOCTYPE html><%@ page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apiontology Demonstrator</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="css/prettify.css" />
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="js/angular-1.4.1.min.js"></script>
<script type="text/javascript" src="js/aod.js"></script>
<script type="text/javascript" src=js/ui-bootstrap-tpls-0.14.1.min.js"></script>
<script type="text/javascript" src=js/prettify.js"></script>
</head>

<body data-ng-app="aodApp" data-ng-controller="MainController"
	data-ng-init="init()">
	<div class="container">
		<div class="row">
			<div class=col-lg-6>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">WSDL url</h3>
						Enter here a WSDL definition url
					</div>
					<div>
						<div class="input-group">
							<input type="text" class="form-control" data-ng-enter="addUri()"
								data-ng-model="uri" /> <span class="input-group-btn">
								<button class="btn btn-default" data-ng-click="addUri()"
									type="button">Add</button>
							</span>
						</div>
					</div>
				</div>

				<div data-ng-show="hasConstraintsAndEquivalences()">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Constraints</h3>
							Select here for each web service at least one mandatory parameter
						</div>
						<div class="panel-body">
							<table class="table">
								<tr>
									<th>Web Service</th>
									<th>Constraints</th>
								</tr>
								<tr class="constraint"
									data-ng-repeat="constraint in configurations.constraints">
									<td>{{ constraint.webService }}</td>
									<td class="parameters">
										<div class="option parameter"
											data-ng-repeat="parameter in constraint.parameters"
											data-ng-class="{ 'selected' : constraint.selected[$index] }"
											data-ng-click="constraint.selected[$index] = !constraint.selected[$index]">{{
											parameter }}</div>
									</td>
								</tr>
							</table>
						</div>
					</div>

					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Parameter Equivalences</h3>
							Select in the right list the to the left equivalent parameter(s)
						</div>
						<div class="panel-body">
							<table class="table">
								<tr>
									<th colspan="2">Parameter</th>
									<th>Equivalent Parameters</th>
								</tr>
								<tr class="equivalence"
									data-ng-repeat="equivalence in configurations.equivalences">
									<td class="parameter">{{ equivalence.parameter }}</td>
									<td>&equiv;</td>
									<td class="eq-parameters">
										<div class="option eq-parameter"
											data-ng-repeat="eqParameter in equivalence.eqParameters"
											data-ng-class="{ 'selected' : equivalence.selected[$index] }"
											data-ng-click="equivalence.selected[$index] = !equivalence.selected[$index]">{{
											eqParameter }}</div>
									</td>
								</tr>
							</table>
						</div>
					</div>

					<div class="button-container">
						<button class="btn btn-primary" data-ng-click="submitConfig()">Submit
							parameter equivalence configuration</button>
					</div>
				</div>

				<div data-ng-show="hasExperimentInput()">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Experiment Input</h3>
							Enter here the value(s) for some parameter and start the
							experiment
						</div>
						<table class="table">
							<tr>
								<th>Parameter</th>
								<th>Value</th>
							</tr>
							<tr class="experiment-input"
								data-ng-repeat="(parameter, value) in experimentInput.values">
								<td class="experiment-input-parameter"
									data-ng-class="{ 'is-new' : experimentInput.isNew[parameter] }">{{
									parameter }}</td>
								<td class="experiment-input-value"><input type="text"
									data-ng-model="experimentInput.values[parameter]" /></td>
							</tr>
						</table>
					</div>

					<div class="button-container">
						<button class="btn btn-primary" data-ng-click="submitInput()">Submit
							experiment input</button>
					</div>

					<div>Execution Runs: {{ experimentInput.executionRuns }}</div>
				</div>
			</div>

			<div class=col-lg-6>
				<div data-ng-show="hasDatamodel()" class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Dynamic OWL Definitions</h3>
						The following OWL abstract in Manchester Syntax describes the
						configured endpoints and show the computed values in red
					</div>
					<div class="panel-body">
						<pre class="pre-xy-scrollable prettyprint"
							data-ng-bind-html="format('datamodel')"></pre>
					</div>
				</div>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Standard OWL Definitions</h3>
						This OWL part defines the resources used in the above ontology.
					</div>
					<pre class="pre-xy-scrollable prettyprint"
						data-ng-bind-html="format('standardDatamodel')"></pre>
				</div>
			</div>
		</div>
	</div>
</body>
<footer>
	<script type="text/javascript">
		!function($) {
			$(function() {
				window.prettyPrint && prettyPrint()
			})
		}(window.jQuery)
	</script>
</footer>
</html>