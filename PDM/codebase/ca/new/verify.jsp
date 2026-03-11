<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="comjni.NativeComJNI" %>
<%@page import="cn.hutool.core.util.StrUtil" %>
<%@ page import="com.glaway.mpm.util.MPMBase64" %>
<%
    NativeComJNI comjni = new NativeComJNI();
    String ocspIp = "";
    String cert = request.getParameter("cert");
    String signedData = request.getParameter("signedData");
    String random = request.getParameter("random");
    String errorMsg = "";
    //获取用户名
    byte[] userName = new byte[1024];
    int ret = comjni.VerifyCert(2, cert, userName);
    String username_str = null;
    username_str = new String(userName);
    if (username_str.startsWith("#")) {
        errorMsg = username_str;
    } else{
		if(username_str != null && username_str.contains("@")){
			username_str = username_str.split("@")[0];
		}
	}

    //验证证书有效性
    if (StrUtil.isEmpty(errorMsg)) {
        byte[] cert_state = new byte[1024];
        ret = comjni.CertValid(2, cert, ocspIp, cert_state);
        String cert_state_str = null;
        cert_state_str = new String(cert_state);
        if (cert_state_str.startsWith("#")) {
            errorMsg = cert_state_str;
        }
    }


    //验签
    if (StrUtil.isEmpty(errorMsg)) {
        byte[] sign_state = new byte[1024];
        int verify_flag = 1;//验证证书有效性标识，1验证，0不验证
        comjni.VERIFYSIG(signedData, random, ocspIp, verify_flag, sign_state);
		String sign_state_str = null;
		try {
            int length = 0;
            for (int i = 0; i < sign_state.length; i++) {
                if (sign_state[i] == 0) {
                    length = i;
                    break;
                }
            }
            sign_state_str =  new String(sign_state, 0, length, "UTF-8");
        } catch (Exception e) {
            sign_state_str = "";
        }
        if (sign_state_str.startsWith("#")) {
            errorMsg = sign_state_str;
        }
    }


    //验证证书链
    if (StrUtil.isEmpty(errorMsg)) {
        byte[] certChainState = new byte[1024];
        ret = comjni.CertChainValid(2, cert, ocspIp, certChainState);
        String cert_chain_state_str = new String(certChainState);
        if (cert_chain_state_str.startsWith("#")) {
            errorMsg = cert_chain_state_str;
        }
    }

    if (StrUtil.isNotEmpty(errorMsg)) {
        response.getWriter().write(errorMsg);
    } else {
		username_str = MPMBase64.encode(username_str);
        response.getWriter().write(username_str);
    }
%>