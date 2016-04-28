
package com.tingken.infoshower.outside;

import java.io.File;
import java.util.Date;

public interface ShowService {
	public static final String SERVER_ADDRESS = "";
	//public static final String DEFAULT_SERVER_ADDRESS = "http://10.0.205.12:10001/";// http://192.168.2.3:8080/showService-war-1.0/showService/
	//public static final String DEFAULT_SERVER_ADDRESS = "http://10.0.205.13:211/Video/";// http://192.168.2.3:8080/showService-war-1.0/showService/
	//内江
	public static final String DEFAULT_SERVER_ADDRESS = "http://118.112.186.84:8002/";// http://192.168.2.3:8080/showService-war-1.0/showService/
	//内江2
	//public static final String DEFAULT_SERVER_ADDRESS = "http://10.0.73.8:8002/";
	//内江3
	//public static final String DEFAULT_SERVER_ADDRESS = "http://10.0.35.240:8002/";
	//内江局域网
	//public static final String DEFAULT_SERVER_ADDRESS = "http://192.168.200.250:8002/";

	void init(String basicUrl);

	AuthResult authenticate(String authCode, String deviceId, String dimension) throws Exception;

	ServerCommand heartBeat(String loginId);

	boolean uploadScreen(String loginId, Date captureTime, File capture);

	VersionInfo getLatestVersion(String loginId);

}
