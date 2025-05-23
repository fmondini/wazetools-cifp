<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="net.danisoft.dslib.*"
	import="net.danisoft.wazetools.*"
%>
<%!
	private static final String PAGE_Title = AppCfg.getAppName() + " Bookmark List";
	private static final String PAGE_Keywords = "Waze, Tools, CIFP, Closure, Incident, Feed";
	private static final String PAGE_Description = AppCfg.getAppAbstract();
%>
	<!DOCTYPE html>
	<html>
	<head>

		<jsp:include page="../_common/head.jsp">
			<jsp:param name="PAGE_Title" value="<%= PAGE_Title %>"/>
			<jsp:param name="PAGE_Keywords" value="<%= PAGE_Keywords %>"/>
			<jsp:param name="PAGE_Description" value="<%= PAGE_Description %>"/>
		</jsp:include>

		<script>

		/**
		 * Read Bookmarks
		 */
		function ReadBookmarks() {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_bookmarks_view.jsp',

				beforeSend: function(XMLHttpRequest) {
					$('#divBookmarks').html('<center><img border="0" src="../images/ajax-loader.gif"></center>');
				},

				success: function(data) {
					$('#divBookmarks').html(data);
				},

				error: function (jqXHR, textStatus, errorThrown) {
					$('#divBookmarks').html('Error <b>' + jqXHR.status + '</b> getting bookmarks data - ' + errorThrown);
				}
			});
		}

		/**
		 * Delete bookmark
		 */
		function DeleteBookmark(ubkId) {

			$.ajax({

				cache: false,
				type: 'GET',
				url: './_bookmarks_delete.jsp',
				data: { id: ubkId },

				beforeSend: function(XMLHttpRequest) {
					$('#divBookmarks').html('<center><img border="0" src="../images/ajax-loader.gif"></center>');
				},

				success: function(data) {
					ReadBookmarks();
				},

				error: function (jqXHR, textStatus, errorThrown) {
					ShowDialog_Error('Error <b>' + jqXHR.status + '</b> deleting bookmark');
					ReadBookmarks();
				}
			});
		}

		/**
		 * On Page Loaded
		 */
		$(document).ready(function() {
			ReadBookmarks();
		});

		</script>

	</head>

	<body>

		<jsp:include page="../_common/header.jsp" />

		<div class="mdc-layout-grid DS-layout-body">
			<div class="mdc-layout-grid__inner">
				<div class="<%= MdcTool.Layout.Cell(12, 8, 4) %>">
					<div class="DS-padding-updn-8px">
						<div class="DS-text-title-shadow"><%= PAGE_Title %> for <%= SysTool.getCurrentUser(request) %></div>
					</div>
					<div class="DS-padding-top-8px DS-padding-bottom-16px">
						<div id="divBookmarks">...</div>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="../_common/footer.jsp">
			<jsp:param name="RedirectTo" value=""/>
		</jsp:include>

	</body>
	</html>
