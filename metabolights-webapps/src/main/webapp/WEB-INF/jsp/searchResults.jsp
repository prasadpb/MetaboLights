<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<script type="text/javascript" src="javascript/jquery-1.6.1.min.js"></script>
<script type="text/javascript" src="javascript/jquery-imtechPager.js"></script>
<script type="text/javascript" src="javascript/jquery-highlight.js"></script>

<div id="text_header">
	<spring:message code="msg.searchResults" />
	<c:if test="${!empty userQuery}">"${userQuery}"</c:if>
</div>

<br />

<c:if test="${!empty searchResults}">
	<div id="pagingControlsTop"></div>
	<br>
	<div id="highlight-plugin">
		<div id="content">
			<c:forEach var="searchResult" items="${searchResults}">
				<div class="z">
					<div style='width: 800px; border: 1px solid #D5AE60; margin-bottom: 10px;'>
						
						<!-- div style='width: 950px;' class='iscell'><b><a href="http://wwwdev.ebi.ac.uk/mtbl/entry=${searchResult.accStudy}">${searchResult.title}</a></b></div-->
						<div style='width: 750px;' class='iscell'>
							<b><a href="entry=${searchResult.accStudy}">${searchResult.title}</a></b>
						</div>
						
						<div style='clear: both;'></div>
						<!-- new row -->

						<div style='width: 550px;' class='iscell'>
							<spring:message code="label.expFact" />
							<ul id="resultList">
								<c:forEach var="factor" items="${searchResult.factors}">
									<li><b>${factor.key}:</b>${factor.value}</li>
								</c:forEach>
							</ul>
						</div>

						<div style='width: 200px;' class='iscell'>
							<spring:message code="label.expId" />
							: ${searchResult.accStudy}<br>
							<spring:message code="label.subm" />
							${searchResult.userName}
						</div>
						
						
						<div style='clear: both;'></div>
						<!-- new row -->

						<div style='width: 350px;' class='iscell'>
							<spring:message code="label.assays" />
							<ul id="resultList">
								<c:forEach var="assay" items="${searchResult.assays}">
									<li>${assay.technology} (${assay.count})</li>
								</c:forEach>
							</ul>
						</div>
						<div style='width: 400px;' class='iscell'>
							<spring:message code="label.pubBy" /> TODO ${searchResult.pubAuthors},<spring:message code="label.pubIn" />
								<a href="http://www.ebi.ac.uk/citexplore/citationDetails.do?externalId=${searchResult.pubId}&dataSource=MED">Citexplore</a>
						</div>

						<div style='clear: both;'></div>
						<!-- new row -->

						<div style='width: 800px;' class='iscell'>
							<spring:message code="label.properties" />
							<ul id="resultList">
								<c:forEach var="property" items="${searchResult.properties}">
									<li>${property.key}: ${property.value}</li>
								</c:forEach>
							</ul>
						</div>

						<div style='clear: both;'></div>
						<!-- new row -->
						
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
	<br>
	<br>
	<div id="pagingControlsBot"></div>
	<br>
	<br>
	<a href="javascript:void($('#highlight-plugin').removeHighlight().highlight('${userQueryClean}'));">Highlight Search Term</a>

</c:if>

<c:if test="${empty searchResults}">
	<br />
	<br />
	<div class="messageBox">
		<br />
		<h3>
			<spring:message code="msg.nothingFound" />
		</h3>
		<br />
	</div>

</c:if>


<script type="text/javascript">
var pager = new Imtech.Pager();
$(document).ready(function() {
    pager.paragraphsPerPage = 10; // set amount elements per page
    pager.pagingContainer = $('#content'); // set of main container
    pager.paragraphs = $('div.z', pager.pagingContainer); // set of required containers
    pager.showPage(1);
});
</script>
