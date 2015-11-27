
<!DOCTYPE html><%@ page language="java"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Apiontology Demonstrator</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha256-MfvZlkHCEqatNoGiOXveE8FIwMzZg4W85qfrfIFBfYc= sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ=="
	crossorigin="anonymous">
<link rel="stylesheet" href="css/github.css">
<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"
	integrity="sha256-Sk3nkD6mLTMOF0EOpNtsIry+s1CsaqQC1rVLTAy+0yc= sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ=="
	crossorigin="anonymous"></script>
<script type="text/javascript" src="js/angular-1.4.1.min.js"></script>
<script type="text/javascript" src="js/ui-bootstrap-0.14.0.min.js"></script>
<script type="text/javascript" src="js/ui-bootstrap-tpls-0.14.1.min.js"></script>
<script type="text/javascript" src="js/aod.js"></script>
</head>

<body data-ng-app="aodApp" data-ng-controller="MainController"
	data-ng-init="init()">
	<div class="col-lg-4">
		<div class="panel panel-primary">
			<div class="panel-heading">WSDL url</div>
			<div class="panel-body">
				Enter here a WSDL definition url
				<div class="input-group">
					<input type="text" list="urls" class="form-control"
						data-ng-enter="addUri()" data-ng-model="uri" /> <span
						class="input-group-btn">
						<button type="button" class="btn btn-primary"
							data-ng-click="addUri()">Add</button>
					</span>
					<datalist id="urls">
						<option>http://localhost:8181</option>
						<option>http://localhost:8182</option>
						<option>http://localhost:8183</option>
						<option>http://localhost:8184</option>
						<option>http://localhost:8185</option>
					</datalist>
				</div>
			</div>
		</div>
		<div data-ng-show="hasConstraintsAndEquivalences()">
			<div class="panel panel-primary">
				<div class="panel-heading">Constraints</div>
				<div class="panel-body">Select here for each web service at
					least one mandatory parameter.</div>
				<table class="table">
					<tr>
						<th>Web Service</th>
						<th>Constraints</th>
					</tr>
					<tr data-ng-repeat="constraint in configurations.constraints">
						<td>{{ constraint.webService }}</td>
						<td>
							<div class="dropdown">
								<a class="dropdown-toggle" id="dropdownMenu1"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="true"> Parameters<span class="caret"></span>
								</a>
								<ul class="dropdown-menu">
									<li><a href="#"
										data-ng-repeat="parameter in constraint.parameters"
										data-ng-class="{ 'selected' : constraint.selected[$index] }"
										data-ng-click="constraint.selected[$index] = !constraint.selected[$index]">{{
											parameter }}</a></li>
								</ul>
							</div>
						</td>
					</tr>
				</table>
			</div>

			<div class="panel panel-primary">
				<div class="panel-heading">Parameter Equivalences</div>
				<div class="panel-body">Select in the right list the to the
					left equivalent parameter(s)</div>
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
							<div class="dropdown">
								<a class="dropdown-toggle" id="dropdownMenu1"
									data-toggle="dropdown" aria-haspopup="true"
									aria-expanded="true"> Parameters<span class="caret"></span>
								</a>
								<ul class="dropdown-menu">
									<li><a href="#" class="option eq-parameter"
										data-ng-repeat="eqParameter in equivalence.eqParameters"
										data-ng-class="{ 'selected' : equivalence.selected[$index] }"
										data-ng-click="equivalence.selected[$index] = !equivalence.selected[$index]">{{
											eqParameter }}</a></li>
								</ul>
							</div>
						</td>

					</tr>
				</table>
			</div>

			<div class="button-container">
				<button class="btn btn-success" data-ng-click="submitConfig()">Submit
					parameter equivalence configuration</button>
			</div>
		</div>
		<div data-ng-show="hasExperimentInput()">
			<div class="panel panel-primary">
				<div class="panel-heading">Experiment Input</div>
				<div class="panel-body">Enter here the value(s) for some
					parameter and start the experiment</div>
				<table class="table">
					<tr>
						<th>Parameter</th>
						<th>Value</th>
					</tr>
					<tr data-ng-repeat="(parameter, value) in experimentInput.values">
						<td
							data-ng-class="{ 'alert alert-success' : experimentInput.isNew[parameter] }">{{
							parameter }}</td>
						<td><input type="text" class="form-control"
							data-ng-model="experimentInput.values[parameter]" /></td>
					</tr>
				</table>
			</div>

			<div class="button-container">
				<button class="btn btn-primary" data-ng-click="submitInput()">Submit
					experiment input</button>
			</div>

			<div><h3><span  class="label label-default">Execution Runs: {{ experimentInput.executionRuns }}</span></h3></div>
		</div>
	</div>

	<div class="col-lg-8">
		<ul class="nav nav-tabs nav-justified">
			<li class="active" data-ng-show="hasDatamodel()"><a showtab=""
				href="#dynamic">Dynamic OWL Definitions</a></li>
			<li><a showtab="" href="#standard">Standard OWL Definitions</a></li>
		</ul>
		<div class="tab-content">
			<div id="dynamic" class="tab-pane fade in active" data-ng-show="hasDatamodel()">
				<div class="panel panel-primary">
					<div class="panel-heading">The following OWL abstract in
						Manchester Syntax describes the configured endpoints and show the
						computed values in green.
					</div>
					<div>
						<pre data-ng-bind-html="format('datamodel')"></pre>
					</div>
				</div>
			</div>

			<div id="standard" class="tab-pane fade">
			<div class="panel panel-primary">
				<div class="panel-heading">This OWL part defines the resources
					used in the above ontology.
				</div>
				<div>
					<pre data-ng-bind-html="format('standardDatamodel')"></pre>
				</div>
			</div>
			</div>
		</div>
	</div>
</body>
</html>