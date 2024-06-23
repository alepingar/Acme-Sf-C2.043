<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="client.contract.label.code" path="code" width="15%"/>
	<acme:list-column code="client.contract.label.providerName" path="providerName" width="25%"/>
	<acme:list-column code="client.contract.label.customerName" path="customerName" width="10%"/>
	<acme:list-column code="client.contract.label.draftMode" path="draftMode" width="10%"/>
	
</acme:list>

<acme:button code="client.contract.button.create" action="/client/contract/create"/>