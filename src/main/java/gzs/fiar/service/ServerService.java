package gzs.fiar.service;

import gzs.fiar.dto.ServerStatusDto;

public interface ServerService {
    ServerStatusDto getStatus();

    void logClick(String link, String userAgent, String language, String remoteAddr, String screenWidth, String screenHeight);

    String getLog();
}
