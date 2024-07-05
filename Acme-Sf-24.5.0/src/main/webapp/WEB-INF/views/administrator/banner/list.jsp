
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

		
<acme:list>
	<acme:list-column code="administrator.banner.label.slogan" path="slogan" width="5%"/>
	<acme:list-column code="administrator.banner.label.startTime" path="startTime" width="5%"/>
	<acme:list-column code="administrator.banner.label.finishTime" path="finishTime" width="5%"/>
	
</acme:list>

<acme:button code="administrator.banner.button.create" action="/administrator/banner/create"/>
	
