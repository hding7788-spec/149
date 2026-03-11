<%
	String reviewUnit = request.getParameter("reviewUnit");
	String userName = request.getParameter("userName");
	 try {
			String usersStr = ext.casc.synch.DataSynchClient.queryOtherServerUsers(reviewUnit,userName);
			usersStr = java.net.URLDecoder.decode(usersStr, "UTF-8");
			out.println(usersStr.trim());
		} catch (java.net.MalformedURLException e) {
			// TODO Auto-generated catch block
			out.println(e.getMessage());
			e.printStackTrace();
		} catch (org.apache.soap.SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println(e.getMessage());
		}



%>