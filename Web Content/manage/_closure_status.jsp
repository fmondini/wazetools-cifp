<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	final String GidUUID = EnvTool.getStr(request, "gid", "");

	Database DB = null;

	try {

		DB = new Database();

		Group GID = new Group(DB.getConnection());
		Group.Data gidData = GID.Read(GidUUID);

		String backColor = "DS-back-" + (gidData.isUnedited()
			? "Gold"
			: (gidData.isActive()
				? "DarkGreen"
				: "FireBrick"
			)
		);

		String foreColor = "DS-text-" + (gidData.isUnedited()
			? "maroon"
			: (gidData.isActive()
				? "lightgreen"
				: "lightred"
			)
		);

		String statusText = (gidData.isUnedited()
			? "ORPHAN"
			: (gidData.isActive()
				? "ACTIVE"
				: "PAUSED"
			)
		);
%>
		<div class="DS-padding-24px <%= foreColor %> <%= backColor %> DS-border-full" align="center">
			<div class="DS-padding-8px">
				<div class="DS-text-italic">Closure Status</div>
			</div>
			<div class="DS-padding-8px">
				<div class="DS-text-huge DS-text-bold"><%= statusText %></div>
			</div>
		</div>
<%
	} catch (Exception e) {
		out.println(e.getMessage());
	}

	if (DB != null)
		DB.destroy();
%>
