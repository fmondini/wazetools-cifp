<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.cifp.*"
%>
<%
	Database DB = null;

	try {

		DB = new Database();
		Bookmark UBK = new Bookmark(DB.getConnection());

		UBK.Delete(EnvTool.getInt(request, "id", 0));

	} catch (Exception e) {
		System.err.println("_bookmarks_delete.jsp: " + e.toString());
	}

	if (DB != null)
		DB.destroy();
%>
