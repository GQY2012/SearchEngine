<%@ page session="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %> 
<%@ page import="demo.*" %>

<!DOCTYPE html>	
<html>
    <head>
        <meta charset="UTF-8" />
		<style>
			#page{width:100%}
			#foot{width:100%;font-size:0.8em;line-height:1em}
			 .fill{margin:0;padding:0}
			#content_body a{font-size:0.8em}
		</style>
    </head>

	

    <body class="fill">
		<div id="page" class="fill" style = "background-color:white">
			<div class = "fill" id="search_head" style ="width:100%;height:100px;background-color:#FFF">
				<div class = "fill" style = "width:100%;height:30px"></div>
				<div class = "fill" style="padding-left:2%;float:left;width:10%;">
					<img style = "width:100%;" src="./s.jpg" alt="图片加载失败">
				</div>
				<% 
					request.setCharacterEncoding("UTF-8");
					 String query_str = request.getParameter("input");
				%>
				<div class = "fill" style="padding-top:5px;float:left;width:85%;height:40px">
					<form style="padding-left:15px;height:100%" method="get" action="http://localhost:8080/SearchEngine/search.jsp">
						 <input style="width:50%; height:90%;font-size:1.3em" type="text" name="input" value = "<%= query_str %>">
					</form>
				</div>
			</div>
			<%
				int n ;
				Search s = new Search();
				Date start_time = new Date();
				n = s.do_search(query_str,10);
				Date end_time = new Date();
				double time = end_time.getTime() - start_time.getTime();
			%>
			<div class = "fill" style = "text-aligin:left;padding-left:13%;width:60%;height:20px">
				<a style = "font-size:0.5em"> 为您找到<%= n %>条结果,用时<%= time/1000 %>s秒:</a>
			</div>
			<div id="content_body" style = "padding-left:13%;width:90%;background-color:white; float:left" >
			
			<jsp:useBean id="replace" scope="page" class="demo.Replace" />
			
			<%
					for(int i = 0;i < n; i++) {
						String content = s.get_content(i,"content");
						if(content.length() > 250){
							content = content.substring(0,250)+"...";
						}
						String url  = s.get_content(i,"url");
						String title = s.get_content(i,"title");
						out.println("<div style = \"width:80%; background-color:#FFF;margin-top:10px\">");
						out.println("<a style = \"font-size:1.2em; width:100%\" href = \""+url
								+ "\">"+replace.replace(title,query_str,"<font color='#FF0000'><strong>" + query_str + "</strong></font>")+"</a><br>");
				//		out.println("<a style = \"font-size:0.8em;\"> "+content+"</a>");
						out.println(replace.replace(content,query_str,"<font color='#FF0000'><strong>" + query_str + "</strong></font>"));
						out.println("</div>");

						 
					}
			%>
			
			
			
			</div>
			<HR class="fill" style="width:100%">
			<div class = "fill" style="width:100%;height:15px;background-color:white" >
			</div>
			<HR class="fill" style="width:100%">
			
			
			
		</div>
		
    </body>
</html>
