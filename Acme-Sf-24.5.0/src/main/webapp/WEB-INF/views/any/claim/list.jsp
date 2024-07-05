<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

		
<acme:list>
	<acme:list-column code="any.claim.label.code" path="code" width="5%"/>
	<acme:list-column code="any.claim.label.instantiation" path="instantiation" width="5%"/>
	<acme:list-column code="any.claim.label.heading" path="heading" width="5%"/>
</acme:list>

<acme:button code="any.claim.button.publish" action="/any/claim/create"/>	