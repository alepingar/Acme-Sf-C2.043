

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="client.contract.form.label.code"
		path="code" placeholder="client.contract.form.code.placeholder"/>
	<acme:input-select
		code="client.contract.form.label.project"
		path="project" choices="${projects}" />
	<acme:input-moment code="client.contract.form.label.instantiationMoment"
		path="instantiationMoment" />
	<acme:input-textbox
		code="client.contract.form.label.providerName"
		path="providerName" />
	<acme:input-textbox
		code="client.contract.form.label.customerName"
		path="customerName" />
	<acme:input-textarea
		code="client.contract.form.label.goals"
		path="goals" />
	<acme:input-money
		code="client.contract.form.label.budget" path="budget" />


	<acme:hidden-data path="id" />
	<acme:hidden-data path="draftMode"/>

	<jstl:if test="${_command != 'create'}">
		<acme:input-checkbox code="client.contract.label.draftMode"
			path="draftMode" readonly="true" />

	</jstl:if>
	<jstl:if test="${_command == 'create'}">
		<acme:hidden-data path="draftMode" />

		<acme:submit code="client.contract.button.create"
			action="/client/contract/create" />
	</jstl:if>


	<jstl:if test="${_command != 'create' && draftMode == true }">
		<acme:submit code="client.contract.button.update"
			action="/client/contract/update" />
		<acme:submit code="client.contract.button.delete"
			action="/client/contract/delete" />
		<acme:submit code="client.contract.button.publish"
			action="/client/contract/publish" />
	</jstl:if>
</acme:form>