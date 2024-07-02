<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="client.progress-log.form.label.recordId"
		path="recordId" placeholder="client.progress-log.form.recordId.placeholder"/>
	<acme:input-select
		code="client.progress-log.form.label.contract"
		path="contract" choices="${contracts}" />
	<acme:input-double
		code="client.progress-log.form.label.completenessPercentage"
		path="completenessPercentage" />
	<acme:input-textarea
		code="client.progress-log.form.label.progressComment"
		path="progressComment" />
	<acme:input-moment code="client.progress-log.form.label.registrationMoment"
		path="registrationMoment" />
	<acme:input-textbox
		code="client.progress-log.form.label.responsiblePerson"
		path="responsiblePerson"/>


	<acme:hidden-data path="id" />
	<acme:hidden-data path="draftMode"/>

	<jstl:if test="${_command != 'create'}">
		<acme:input-checkbox code="client.progress-log.label.draftMode"
			path="draftMode" readonly="true" />

	</jstl:if>
	<jstl:if test="${_command == 'create'}">
		<acme:hidden-data path="draftMode" />

		<acme:submit code="client.progress-log.button.create"
			action="/client/progress-log/create" />
	</jstl:if>


	<jstl:if test="${_command != 'create' && draftMode == true }">
		<acme:submit code="client.progress-log.button.update"
			action="/client/progress-log/update" />
		<acme:submit code="client.progress-log.button.delete"
			action="/client/progress-log/delete" />
		<acme:submit code="client.progress-log.button.publish"
			action="/client/progress-log/publish" />
	</jstl:if>
</acme:form>