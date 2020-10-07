package com.va.removeconsult.clouddisk.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.pojo.LoginInfoPojo;
import com.va.removeconsult.clouddisk.pojo.PublicKeyInfo;
import com.va.removeconsult.clouddisk.service.AccountService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.DecryptionUtil;
import com.va.removeconsult.clouddisk.util.KeyUtil;
import com.va.removeconsult.clouddisk.util.VerificationCode;
import com.va.removeconsult.clouddisk.util.VerificationCodeFactory;

@Service
public class AccountServiceImpl implements AccountService {
	@Resource
	private KeyUtil ku;

	
	private static final long TIME_OUT = 30000L;

	@Resource
	private Gson gson;

	
	private VerificationCodeFactory vcf = new VerificationCodeFactory(45, 6, 2, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z');

	
	private static final Set<String> focusAccount = new HashSet<>();

	public String checkLoginRequest(final HttpServletRequest request, final HttpSession session) {
		final String encrypted = request.getParameter("encrypted");
		final String loginInfoStr = DecryptionUtil.dncryption(encrypted, ku.getPrivateKey());
		try {
			final LoginInfoPojo info = gson.fromJson(loginInfoStr, LoginInfoPojo.class);
			if (System.currentTimeMillis() - Long.parseLong(info.getTime()) > TIME_OUT) {
				return "error";
			}
			final ConfigureReader cr = ConfigureReader.instance();
			final String accountId = info.getAccountId();
			if (!cr.foundAccount(accountId)) {
				return "accountnotfound";
			}
			
			synchronized (focusAccount) {
				if (focusAccount.contains(accountId)) {
					String reqVerCode = request.getParameter("vercode");
					String trueVerCode = (String) session.getAttribute("VERCODE");
					session.removeAttribute("VERCODE");
					if (reqVerCode == null || trueVerCode == null || !trueVerCode.equals(reqVerCode.toLowerCase())) {
						return "needsubmitvercode";
					}
				}
			}
			if (cr.checkAccountPwd(accountId, info.getAccountPwd())) {
				session.setAttribute("ACCOUNT", (Object) accountId);
				
				synchronized (focusAccount) {
					focusAccount.remove(accountId);
				}
				return "permitlogin";
			}
			
			synchronized (focusAccount) {
				focusAccount.add(accountId);
			}
			return "accountpwderror";
		} catch (Exception e) {
			return "error";
		}
	}

	public void logout(final HttpSession session) {
		session.invalidate();
	}

	public String getPublicKey() {
		PublicKeyInfo pki = new PublicKeyInfo();
		pki.setPublicKey(ku.getPublicKey());
		pki.setTime(System.currentTimeMillis());
		return gson.toJson(pki);
	}

	@Override
	public void getNewLoginVerCode(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		VerificationCode vc = vcf.next(4);
		session.setAttribute("VERCODE", vc.getCode());
		try {
			response.setContentType("image/png");
			OutputStream out = response.getOutputStream();
			vc.saveTo(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			try {
				response.sendError(404);
			} catch (IOException e1) {

			}
		}

	}
}
