d3.selection.enter.prototype=d3.selection.prototype.addNodeForm=function(c,a,f,d,e,b){this.append("rect").attr("class",function(g){return g.getBiologicalType()}).attr("id",function(g){return g.getId()}).attr("identifier",function(g){return g.getId()}).attr("width",c).attr("height",a).attr("rx",f).attr("ry",d).attr("transform","translate(-"+c/2+",-"+a/2+")").style("stroke",e).style("stroke-width",b);this.append("rect").attr("class","fontSelected").attr("width",c).attr("height",a).attr("rx",f).attr("ry",d).attr("transform","translate(-"+c/2+",-"+a/2+")").style("fill-opacity","0").style("fill","#000")};d3.selection.prototype.setNodeForm=function(c,a,f,d,e,b){this.select("rect").attr("width",c).attr("height",a).attr("rx",f).attr("ry",d).attr("transform","translate(-"+c/2+",-"+a/2+")").style("stroke",e).style("stroke-width",b);this.select(".fontSelected").attr("width",c).attr("height",a).attr("rx",f).attr("ry",d).attr("transform","translate(-"+c/2+",-"+a/2+")").style("stroke-width",b)};