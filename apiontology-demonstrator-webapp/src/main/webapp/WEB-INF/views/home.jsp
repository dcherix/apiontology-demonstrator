<!DOCTYPE html><%@ page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apiontology Demonstrator</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="js/angular-1.4.1.min.js"></script>
<script type="text/javascript" src="js/aod.js"></script>
</head>

<body data-ng-app="aodApp" data-ng-controller="MainController"
	data-ng-init="init()">
	<table>
		<tr>
			<td>
				<div>
					<div class="label">WSDL url</div>
					<div>Enter here a WSDL definition url</div>
					<table class="box uri">
						<tr>
							<th colspan="2">Web Service URI</th>
						</tr>
						<tr>
							<td class="input"><input type="text"
								data-ng-enter="addUri()" data-ng-model="uri" /></td>
							<td>
								<button data-ng-click="addUri()">Add</button>
							</td>
						</tr>
					</table>
				</div>

				<div data-ng-show="hasConstraintsAndEquivalences()">
					<div>
						<div class="label">Constraints</div>
						<div>Select here for each web service at least one mandatory parameter</div>
						<table class="box constraints">
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

					<div>
						<div class="label">Parameter Equivalences</div>
						<div>Select in the right list the to the left equivalent parameter(s)</div>
						<table class="box equivalences">
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

					<div class="button-container">
						<button data-ng-click="submitConfig()">Submit parameter
							equivalence configuration</button>
					</div>
				</div>

				<div data-ng-show="hasExperimentInput()">
					<div>
						<div class="label">Experiment Input</div>
						<div>Enter here the value(s) for some parameter and start the experiment</div>
						<table class="box">
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
						<button data-ng-click="submitInput()">Submit experiment
							input</button>
					</div>

					<div>Execution Runs: {{ experimentInput.executionRuns }}</div>
				</div>
			</td>

			<td class="right">
				<div data-ng-show="hasDatamodel()">
					<div class="label">Dynamic OWL Definitions</div>
					<div>The following OWL abstract in Manchester Syntax describes the configured endpoints and show the computed values in red</div>
					<table class="box datamodel">
						<tr>
							<td data-ng-bind-html="format('datamodel')"></td>
						</tr>
					</table>
				</div>
				<div>
					<div class="label">Standard OWL Definitions</div>
					<div>This OWL part defines the resources used in the above ontology.</div>
					<table class="box datamodel">
						<tr>
							<td data-ng-bind-html="format('standardDatamodel')"></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>