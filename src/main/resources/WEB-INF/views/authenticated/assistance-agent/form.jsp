<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="authenticated.assistance-agent.form.label.employeeCode" path="employeeCode"/>
    <acme:input-textbox code="authenticated.assistance-agent.form.label.spokenLanguages" path="spokenLanguages"/>
    <acme:input-textbox code="authenticated.assistance-agent.form.label.airline" path="airline"/>
    <acme:input-moment code="authenticated.assistance-agent.form.label.employmentStartDate" path="employmentStartDate"/>
    <acme:input-textarea code="authenticated.assistance-agent.form.label.bio" path="bio"/>
    <acme:input-money code="authenticated.assistance-agent.form.label.salary" path="salary"/>
    <acme:input-url code="authenticated.assistance-agent.form.label.photoUrl" path="photoUrl"/>
    
	
	<jstl:if  test="${_command == 'create'}">
		<acme:submit code="authenticated.assistance-agent.form.button.create" action="/authenticated/assistance-agent/create"/>
	</jstl:if>
	<jstl:if test="${_command == 'update'}">
		<acme:submit  code="authenticated.assistance-agent.form.button.update" action="/authenticated/assistance-agent/update"/>
	</jstl:if>
</acme:form>
