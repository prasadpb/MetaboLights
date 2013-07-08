
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
$(document).ready(function() {
	$("#formulae").formularize();
});
</script>

<script>
$(function() {
	$( "#tabs" ).tabs({
		cache:true,
        activate: function( event, ui ) {


            // If the new tab is NMR...
            if ($(ui.newTab.children('a')[0]).attr('href')=="#tabs-5"){

                $("#spectrumbrowser").find("select").change(function(e){
                    e.preventDefault();

                    /* Display the image */
                    var spectrumId = $(this).val();

                    /* Show info in the info div*/
                    var nmrInfoDiv = $('#nmrInfo');
                    /* Get the selected option */
                    var option = $(this).find(":selected");


                    /* Get the pathway object (json element)*/
                    var spectra = nmrInfo[$(this)[0].selectedIndex];

                    var html = spectra.name + "<br/>a " + spectra.type + " spectrum." ;

                    $.each(spectra.properties, function() {

                        html = html + "<br/>" + this.name + ":"

                        if (this.value.indexOf("http:")==0){
                            html = html + "<a href=\"" + this.value + "\">" + this.value + "</a>"
                        } else {
                            html = html + this.value;
                        }

                    });
                    nmrInfoDiv.html(html);


                });

                // And now fire change event when the DOM is ready
                $("#spectrumbrowser").find("select").trigger('change');

            }

        }

    });
});
</script>

<script type="text/javascript" src="<spring:url value="specbrowser/SpectrumBrowser/SpectrumBrowser.nocache.js"/>"></script>
<script type="text/javascript" src="<spring:url value="javascript/jquery.imageLens.js"/>"></script>
<%--<script type="text/javascript" src="http://wwwdev.ebi.ac.uk/metabolights/specbrowser/SpectrumBrowser/SpectrumBrowser.nocache.js"/>--%>
<%--<script type="text/javascript" src="proxy?url=http://wwwdev.ebi.ac.uk/metabolights/specbrowser/SpectrumBrowser/SpectrumBrowser.nocache.js"/>--%>
<script type="text/javascript">

	$(document).ajaxStart(function(){showWait();}).ajaxStop(function(){
        hideWait();
    });

    $(document).ready(function() {

        $("#hourglass").dialog({
            create: function(){
                $('.ui-dialog-titlebar-close').removeClass('ui-dialog-titlebar-close');
            },
            width: 200,
            height: 60,
            modal: true,
            autoOpen: false
        });

        $("#pathwayList").change(function(e){
            e.preventDefault();

            /* Display the image */
            var pathwayId = $(this).val();
            var container = $('#pathwayContainer');
            container.html('');
            container.append("<img id='pathway' src='pathway/" + pathwayId + "/png'/>");

            $('[id=pathway]').imageLens({ lensSize: 350 , borderColor: "#666666", borderSize: 2});

            /* Show info in the info div*/
            var pathwayInfoDiv = $('#pathwayInfo');
            /* Get the selected option */
            var option = $(this).find(":selected");


            /* Get the pathway object (json element)*/
            var pathway = pathwaysInfo[$(this)[0].selectedIndex];

            var html = "generated from " +  pathway.source + "<br/>for  " + pathway.species;

            $.each(pathway.properties, function() {

                html = html + "<br/>" + this.name + ":"

                if (this.value.indexOf("http:")==0){
                    html = html + "<a href=\"" + this.value + "\">" + this.value + "</a>"
                } else {
                    html = html + this.value;
                }

            });
            pathwayInfoDiv.html(html);


        });


        // And now fire change event when the DOM is ready
        $('#pathwayList').trigger('change');

    });

    function showWait() {
        document.body.style.cursor = "wait";
        $('.ui-dialog-titlebar').hide();
        $( "#hourglass" ).dialog( "open" );
    }

    function hideWait(){
        document.body.style.cursor = "default";
        $( "#hourglass" ).dialog("close");
    }
	
</script>

<script>
function color_for_atom(formulaChar)
{
// based on jmol colors: http://jmol.sourceforge.net/jscolors/
  if (formulaChar == 'C') color = "909090";
  else if (formulaChar == 'N') color = "3050F8";
  else if (formulaChar == 'O') color = "FF0D0D";
  else color = "000000";
 
  return color;
}

(function( $ ) {
	 
	  $.fn.formularize = function() {
	    return this.each(function() {
	      var formulaetext = '';
	     
	      // get the current text inside element
	      var text = $(this).text();
	 
	      // iterate the whole 360 degrees
	      for (var i = 0; i < text.length; i++)
	      {
	    	formulaetext = formulaetext + '<span style="color:#' + color_for_atom(text.charAt(i)) + '">' + text.charAt(i) + '</span>';
	        
	      }
	 
	      $(this).html(formulaetext);
	    });
	  };
	})( jQuery );
</script>

	<div id="hourglass">
   		<img src="img/wait.gif" alt="Please wait"/>&nbsp;<b><spring:message code="msg.fetchingData"/></b>
   	</div>

<%--  Place holder for the compound --%>
<div id="content" class="grid_24">
	<div class="grid_24">
		<h3>${compound.mc.accession} - ${compound.mc.name}</h3>
	</div>
	<div class="grid_6 alpha">
		<div class="grid_16 prefix_4">
			<%--<h5>Structure</h5><br>--%>
			<img src="http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&imageIndex=0&chebiId=${compound.mc.chebiId}"/>
		</div>
		<div class="grid_24">
			<p><a href="http://www.ebi.ac.uk/chebi/searchId.do?chebiId=${compound.mc.chebiId}">${compound.chebiEntity.chebiAsciiName} - (${compound.mc.chebiId})</a></p>
		</div>
	
	</div>

	<div class="grid_18 omega">
		<div id="tabs">
			<ul>
				<li>
					<a class="noLine" href="#tabs-1"><spring:message code="ref.compound.tab.chemistry"/></a>
				</li>
				<c:if test="${compound.mc.hasSpecies}">
                    <li>
                        <a class="noLine" href="#tabs-2"><spring:message code="ref.compound.tab.biology"/></a>
                    </li>
				</c:if>
				<c:if test="${compound.mc.hasPathways}">
                    <li>
                        <a class="noLine" href="#tabs-3"><spring:message code="ref.compound.tab.pathways"/></a>
                    </li>
				</c:if>
				<c:if test="${compound.mc.hasReactions}">
                    <li>
                        <a class="noLine" href="reactions?chebiId=${compound.mc.chebiId}"><spring:message code="ref.compound.tab.reactions"/></a>
                    </li>
				</c:if>
                
                <c:if test="${compound.mc.hasNMR}">
                    <li>
                        <a class="noLine" href="#tabs-5"><spring:message code="ref.compound.tab.nmrspectra"/></a>
                    </li>
                </c:if>
				<c:if test="${compound.mc.hasMS}">
                    <li>
                        <a class="noLine" href="#tabs-6"><spring:message code="ref.compound.tab.msspectra"/></a>
                    </li>
				</c:if>
				<c:if test="${compound.mc.hasLiterature}">
                    <li>
                        <a class="noLine" href="citations?mtblc=${compound.mc.accession}"><spring:message code="ref.compound.tab.literature"/></a>
                    </li>
				</c:if>
			</ul>
		
			<div id="tabs-1" class="tab">
				<c:if test="${not empty compound.chebiEntity.definition}">
				<h6><spring:message code="ref.compound.tab.characteristics.definition"/></h6>
				${compound.chebiEntity.definition}
				</c:if>
                <c:if test="${not empty compound.chebiEntity.iupacNames}">
                    <h6>IUPAC Name</h6>
                    <c:forEach var="iupacName" items="${compound.chebiEntity.iupacNames}">
                        ${iupacName.data}
                    </c:forEach>
                </c:if>
				<h6><spring:message code="ref.compound.tab.characteristics.chemicalproperties"/></h6>
				<c:forEach var="formula" items="${compound.chebiEntity.formulae}">
				<spring:message code="ref.compound.tab.characteristics.formula"/> - <span id="formulae">${formulae.data}</span><br/>
				</c:forEach>
				Average mass - ${compound.chebiEntity.mass}
				<br/>
				<h6><spring:message code="ref.compound.synonyms"/>:</h6>
				<c:forEach var="synonym" items="${compound.chebiEntity.synonyms}">
					<span class="tag">${synonym.data}</span>
				</c:forEach>
				<br/><br/>				
				${compound.chebiEntity.inchi}<br/>
			</div>

            <c:if test="${compound.mc.hasSpecies}">
                <!-- Found in -->
                <div id="tabs-2" class="tab">
                    <%--<c:forEach var="metSpecie" items="${compound.mc.metSpecies}">--%>
                        <%--${metSpecie.species.species} - ${metSpecie.crossReference.accession}<br/>--%>
                    <%--</c:forEach>--%>
                    <c:forEach var="item" items="${compound.species}">
                        <br/>${item.key.species} :
                        <c:forEach var="xref" items="${item.value}">
                            <c:choose>
                                <c:when test="${xref.crossReference.db.id eq 2}">
                                    <a class="tag" href='${xref.crossReference.accession}'>${xref.crossReference.accession}</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="tag" href='http://www.ebi.ac.uk/chebi/searchId.do?chebiId=${xref.crossReference.accession}'>${xref.crossReference.accession}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${compound.mc.hasPathways}">
                <!-- Pathways -->
                <div id="tabs-3" class="tab">
                    <select id="pathwayList">
                        <c:forEach var="pathway" items="${compound.mc.metPathways}">
                            <option value="${pathway.id}" source="${pathway.database.name}" species="${pathway.speciesAssociated.species}">${pathway.name}</option>
                        </c:forEach>
                    </select>
                    <br/><br/><br/>
                    <div id="pathwayContainer" style="position: relative"></div>
                    <script>
                        var pathwaysInfo = [
                                <c:forEach var="pathway" items="${compound.mc.metPathways}" varStatus="pathwayLoopStatus">
                                    <c:if test="${pathwayLoopStatus.index gt 0}">,</c:if>
                                    {"id":${pathway.id}
                                    , "source":"${pathway.database.name}"
                                    , "species":"${pathway.speciesAssociated.species}"
                                    , "properties": [
                                        <c:forEach var="attribute" items="${pathway.attributes}" varStatus="attributeLoopStatus">
                                        <c:if test="${attributeLoopStatus.index gt 0}">,</c:if>
                                        {"name": "${attribute.attributeDefinition.name}", "value":"${attribute.value}"}
                                        </c:forEach>
                                    ]
                                    }
                                </c:forEach>
                            ];
                    </script>
                    <div id="pathwayInfo" class="specs"></div>
                </div>
            </c:if>
			<%-- Reactions
			<div id="tabs-4" class="tab">
				<h4>To be implemented...</h4>
				<img src="https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTLBHrDpm9cZkyAdfU7KdQnVLVZ9MG6SByle5QQM0IpSf2hBezB"/>
			</div>--%>


            <c:if test="${compound.mc.hasNMR}">
                <!-- NMR Spectra -->
                <div id="tabs-5" class="tab">
                    <div id="spectrumbrowser">
                        {"list":
                        [<c:forEach var="spectra" items="${compound.mc.metSpectras}" varStatus="spectraloopStatus">
                            <c:if test="${spectraloopStatus.index gt 0}">,</c:if>{"name":"${spectra.name}", "id":${spectra.id}, "url":"spectra/${spectra.id}/json"}
                        </c:forEach>
                        ]
                        }
                    </div>
                    <script>
                        var nmrInfo = [
                            <c:forEach var="spectra" items="${compound.mc.metSpectras}" varStatus="spectraLoopStatus">
                            <c:if test="${spectraLoopStatus.index gt 0}">,</c:if>
                            {"id":${spectra.id}
                                , "name":"${spectra.name}"
                                , "url":"spectra/${spectra.id}/json"
                                , "type":"${spectra.spectraType}"
                                , "properties": [
                                <c:forEach var="attribute" items="${spectra.attributes}" varStatus="attributeLoopStatus">
                                <c:if test="${attributeLoopStatus.index gt 0}">,</c:if>
                                {"name": "${attribute.attributeDefinition.name}", "value":"${attribute.value}"}
                                </c:forEach>
                            ]
                            }
                            </c:forEach>
                        ];
                    </script>
                    <div id="nmrInfo" class="specs"/>
                </div>
            </c:if>

            <c:if test="${compound.mc.hasMS}">
                <!-- MS Spectra -->
                <div id="tabs-6" class="tab">
                    <div id="spectrumbrowserms">
                        {"list":
                        [<c:forEach var="spectra" items="${compound.mc.metSpectras}" varStatus="spectraloopStatus">
                        <c:if test="${spectraloopStatus.index gt 0}">,</c:if>{"name":"${spectra.name}<c:forEach var="attribute" items="${spectra.attributes}" varStatus="attributeloopStatus"><c:if test="${attributeloopStatus.index eq 0}"> (</c:if><c:if test="${attributeloopStatus.index gt 0}">, </c:if>${attribute.attributeDefinition.name}:${attribute.value}<c:if test="${attributeloopStatus.index eq (fn:length(spectra.attributes)-1)}">)</c:if></c:forEach>", "id":${spectra.id}, "url":"spectra/${spectra.id}/json"}
                    </c:forEach>
                        ]
                        }
                    </div>
                </div>
            </c:if>

			<%-- Literature
			<div id="tabs-7" class="tab">
				<!--<h4>To be implemented...</h4>
				<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIS7_rCf2a4_96A56MVeTRTsvyLmohyfOwBLDrUwds5HAat6RZ"/>
				<br/>
				<c:forEach var="citation" items="${compound.chebiEntity.citations}">
					<span class="tag">${citation.data}</span><br/>
				</c:forEach>
			</div>
			--%>
		</div>
	</div>
</div>
<br/>