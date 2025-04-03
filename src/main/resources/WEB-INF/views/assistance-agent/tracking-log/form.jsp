<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-moment code="assistance-agent.tracking-log.form.label.updateMoment" path="updateMoment" readonly="true"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.step" path="step"/>
	<acme:input-double code="assistance-agent.tracking-log.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-select code="assistance-agent.tracking-log.form.label.indicator" path="indicator" choices="${indicators}"/>
	<acme:input-textarea code="assistance-agent.tracking-log.form.label.resolutionReason" path="resolutionReason"/>

	<jstl:choose>		
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">	
			<acme:submit code="assistance-agent.tracking-log.form.button.update" action="/assistance-agent/tracking-log/update"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
		</jstl:when>
		
		<jstl:when test="${_command == 'create'}">
				<acme:submit code="assistance-agent.tracking-log.form.button.create" action="/assistance-agent/tracking-log/create"/>
		</jstl:when>	
	</jstl:choose>

</acme:form>