<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:hidden-data path="id"/>

	<acme:input-moment code="administrator.banner.label.instationUpdateMoment" path="instationUpdateMoment"/>
	<acme:input-moment code="administrator.banner.label.startTime" path="startTime"/>
	<acme:input-moment code="administrator.banner.label.finishTime" path="finishTime"/>	
	<acme:input-url code="administrator.banner.label.linkPicture" path="linkPicture"/>	
	<acme:input-textbox code="administrator.banner.label.slogan" path="slogan"/>	
	<acme:input-url code="administrator.banner.label.linkDocument" path="linkDocument"/>

	<jstl:if test="${_command == 'create'}">
		<acme:submit test="${_command == 'create'}" code="administrator.banner.button.create" action="/administrator/banner/create"/>	
	</jstl:if>
	
		<jstl:if test="${_command != 'create'}">
		<acme:submit code="administrator.banner.button.update" action="/administrator/banner/update" />
		<acme:submit code="administrator.banner.button.delete" action="/administrator/banner/delete" />
		</jstl:if>
</acme:form>