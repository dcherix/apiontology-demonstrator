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
</head>

<body data-ng-app>
<pre><code class="owlms">${ontology}</code></pre>
</body>
<script type="text/javascript">
$(document).ready(function() {
	  $('pre code').each(function(i, block) {
	    hljs.highlightBlock(block);
	  });
	});
</script>
</html>