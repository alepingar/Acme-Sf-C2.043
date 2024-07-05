<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:hidden-data path="id"/>

	<acme:input-textbox code="any.claim.label.code" path="code" placeholder="C-XXXX"/>
	<acme:input-moment code="any.claim.label.instantiation" path="instantiation"/>
	<acme:input-textbox code="any.claim.label.heading" path="heading"/>	
	<acme:input-textbox code="any.claim.label.description" path="description"/>	
	<acme:input-textbox code="any.claim.label.departament" path="departament"/>	
	<acme:input-email code="any.claim.label.email" path="email"/>
	<acme:input-url code="any.claim.label.link" path="link"/>
	<jstl:if test="${_command == 'create'}">
		<acme:input-checkbox code="any.note.label.confirm" path="confirm"/>
		<acme:submit test="${_command == 'create'}" code="any.claim.button.publish" action="/any/claim/create"/>	
	</jstl:if>

</acme:form>