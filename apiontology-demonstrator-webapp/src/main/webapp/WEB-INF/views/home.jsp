<!DOCTYPE html><%@ page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apiontology Demonstrator</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/brown_paper.css" />
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="js/angular-1.4.1.min.js"></script>
<script type="text/javascript" src="js/highlight.pack.js"></script>
<script type="text/javascript" src="js/aod.js"></script>
</head>

<body
	data-ng-app="aodApp"
	data-ng-controller="MainController"
	data-ng-init="init()">
	<div class="left content">
		<div>
			<div class="label">Ontology URI</div>
			<table class="box uri">
				<tr>
					<td class="input">
						<input
							type="text"
							data-ng-model="uri"/>
					</td>
					<td>
						<button
							data-ng-click="addUri()">Add</button>
					</td>
				</tr>
			</table>
		</div>

		<div data-ng-show="hasConstraintsAndEquivalences()">
			<div>
				<div class="label">Constraints</div>
				<table class="box constraints">
					<tr class="constraint"
						data-ng-repeat="constraint in configurations.constraints">
						<td>{{ constraint.webService }}</td>
						<td class="parameters">
							<div class="option parameter"
								data-ng-repeat="parameter in constraint.parameters"
								data-ng-class="{ 'selected' : constraint.selected[$index] }"
								data-ng-click="constraint.selected[$index] = !constraint.selected[$index]">{{ parameter }}</div>
						</td>
					</tr>
				</table>
			</div>

			<div>
				<div class="label">Parameter Equivalences</div>
				<table class="box equivalences">
					<tr class="equivalence"
						data-ng-repeat="equivalence in configurations.equivalences">
						<td class="parameter">{{ equivalence.parameter }}</td>
						<td>&equiv;</td>
						<td class="eq-parameters">
							<div class="option eq-parameter"
								data-ng-repeat="eqParameter in equivalence.eqParameters"
								data-ng-class="{ 'selected' : equivalence.selected[$index] }"
								data-ng-click="equivalence.selected[$index] = !equivalence.selected[$index]">{{ eqParameter }}</div>
						</td>
					</tr>
				</table>
			</div>

			<div class="button-container">
				<button
					data-ng-click="submitConfig()">Submit parameter equivalence configuration</button>
			</div>
		</div>

		<div
			data-ng-show="hasExperimentInput()">
			<div>
				<div class="label">Experiment Input</div>
				<table class="box">
					<tr class="experiment-input"
						data-ng-repeat="(parameter, value) in experimentInput">
						<td class="experiment-input-parameter">{{ parameter }}</td>
						<td class="experiment-input-value">
							<input
								type="text"
								data-ng-model="experimentInput[parameter]"/>
						</td>
					</tr>
				</table>
			</div>

			<div class="button-container">
				<button
					data-ng-click="submitInput()">Submit experiment input</button>
			</div>
		</div>
	</div>

	<div class="right content">
		<div
			data-ng-show="hasDatamodel()">
			<div class="label">Ontology</div>
			<table class="box datamodel">
				<tr>
					<td
						data-ng-bind-html="datamodel()"></td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>