package com.va.removeconsult.clouddisk.service;

import javax.servlet.http.*;

public interface ShowPictureService
{
    String getPreviewPictureJson(final HttpServletRequest request);
    
    
    void getCondensedPicture(final HttpServletRequest request,final HttpServletResponse response);
}
