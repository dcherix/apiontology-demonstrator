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

<body class="table"
	data-ng-app="aodApp"
	data-ng-controller="MainController"
	data-ng-init="init()">
	<div class="left content cell">
		<div>
			<div class="label">Constraints</div>
			<div class="table box constraints">
				<div class="row constraint"
					data-ng-repeat="constraint in configurations.constraints">
					<div class="cell">{{ constraint.webService }}</div>
					<div class="cell parameters">
						<div class="option parameter"
							data-ng-repeat="parameter in constraint.parameters"
							data-ng-class="{ 'selected' : constraint.selected[$index] }"
							data-ng-click="constraint.selected[$index] = !constraint.selected[$index]">{{ parameter }}</div>
					</div>
				</div>
			</div>
		</div>

		<div>
			<div class="label">Parameter Equivalences</div>
			<div class="table box equivalences">
				<div class="equivalence row"
					data-ng-repeat="equivalence in configurations.equivalences">
					<div class="cell parameter">{{ equivalence.parameter }}</div>
					<div class="cell">&equiv;</div>
					<div class="cell eq-parameters">
						<div class="option eq-parameter"
							data-ng-repeat="eqParameter in equivalence.eqParameters"
							data-ng-class="{ 'selected' : equivalence.selected[$index] }"
							data-ng-click="equivalence.selected[$index] = !equivalence.selected[$index]">{{ eqParameter }}</div>
					</div>
				</div>
			</div>
		</div>

		<button
			data-ng-click="submit()">Submit</button>
	</div>

	<div class="right content cell">
		<div class="label">Ontotlogy</div>
		<div class="table box datamodel">
			<div class="row">
				<div class="cell"
					data-ng-bind-html="datamodel()"></div>
			</div>
		</div>
	</div>
</body>
</html>