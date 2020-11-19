package com.eseict.zoo.util;

import com.eseict.zoo.proc.node.ServerInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.List;

public class ZookeeperCommUtil {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperCommUtil.class);
	
	public static final String ZONE_ID = "Asia/Seoul";
	public static final String LOCAL_OFFSET_ID = "+09:00";
	public static final String ONEM2M_DATE_FORMAT = "yyyyMMdd'T'HHmmss";
	public static final String ONEM2M_DATE_FORMAT_MILLIS = "yyyyMMdd'T'HHmmssSSS";
	public static final String DATE_FORMAT_STR_17 = "yyyyMMddHHmmssSSS";
	public static final String DATE_FORMAT_STR_14 = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_STR_10 = "yyyyMMddHH";
	public static final String DATE_FORMAT_STR_VIEW = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_YEAR = "yyyy";
	
	public static final String DATE_FORMAT_BATCH_SENSOR_DATA_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT_BATCH_SENSOR_DATA_SECOND = "yyyy-MM-dd HH:mm:ss.";
	
	public static final ZoneId ZONE = ZoneId.of(ZONE_ID);
	public static final ZoneOffset OFFSET = ZoneOffset.of(LOCAL_OFFSET_ID);
	public static final DateTimeFormatter onem2m_format = DateTimeFormatter.ofPattern(ZookeeperCommUtil.ONEM2M_DATE_FORMAT).withZone(ZONE);
	public static final DateTimeFormatter onem2m_format_Millis = DateTimeFormatter.ofPattern(ZookeeperCommUtil.ONEM2M_DATE_FORMAT_MILLIS).withZone(ZONE);
	
	public static final DateTimeFormatter formatter10 = DateTimeFormatter.ofPattern(DATE_FORMAT_STR_10).withZone(ZONE);
	public static final DateTimeFormatter formatter14 = DateTimeFormatter.ofPattern(DATE_FORMAT_STR_14).withZone(ZONE);
//	public static final DateTimeFormatter formatter17 = DateTimeFormatter.ofPattern(DATE_FORMAT_STR_17).withZone(ZONE);
	public static final DateTimeFormatter formatter17 = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_STR_14).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter().withZone(ZONE);
	public static final DateTimeFormatter formatterBatchMillis = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_BATCH_SENSOR_DATA_SECOND).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter().withZone(ZONE);
	public static final DateTimeFormatter formatterBatchSecond = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_BATCH_SENSOR_DATA_SECOND).toFormatter().withZone(ZONE);
	public static final DateTimeFormatter formatterView = DateTimeFormatter.ofPattern(DATE_FORMAT_STR_VIEW).withZone(ZONE);
	
	public static void main(String[] args) {
		
	}
	
	public static String byteArrayToHex(byte[] bytes){ 
		StringBuilder sb = new StringBuilder(); 
		for(byte b : bytes){ 
			sb.append(String.format("%02X", b&0xff)); 
		} 
		return sb.toString(); 
	} 
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String byteArrayToHexAddSpace(byte[] bytes){ 
		StringBuilder sb = new StringBuilder(); 
		if (bytes != null && bytes.length > 0) {
			for(byte b : bytes){ 
				sb.append(String.format("%02X", b&0xff)); 
				sb.append(" ");
			} 
			return sb.substring(0, sb.lastIndexOf(" ")); 
		}
		return "";
	} 
	
	public static byte[] hexStringAddSpaceToByteArray(String s) {
		String trimString = s.replace(" ", "");
		int len = trimString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(trimString.charAt(i), 16) << 4)
					+ Character.digit(trimString.charAt(i+1), 16));
		}
		return data;
	}
	
	
	/**
	 * 
	 * [설명] oneM2M 포맷을 따르는 현재 날짜를 가져오는 함수
	 * 
	 * @Method : currentDate
	 * @return : String
	 * @author : ese
	 * @since : 2018. 3. 12.
	 */
	public static String currentDate(DateTimeFormatter format){
		String currentDate = LocalDateTime.now(ZONE).format(format);
		return currentDate ;
	}
	
	public static long getTimestamp(String dateString, DateTimeFormatter formatter, ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
		return dateTime.toInstant(offset).toEpochMilli();
	}
	
	public static long getTimestampAdjust10(ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.now();
		long epochmilli = dateTime.toInstant(offset).toEpochMilli();
		dateTime = dateTime.minus(epochmilli%(1000*60*10), ChronoUnit.MILLIS);
		return dateTime.toInstant(offset).toEpochMilli();
	}
	
	public static long getCurrentTimestamp(ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.now();
		return dateTime.toInstant(offset).toEpochMilli();
	}
	
	public static long getCurrentTimestampSecond(ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.now();
		return dateTime.toEpochSecond(offset);
	}

	public static long getCurrentTimestampMillisecond(ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.now();
		return dateTime.toInstant(offset).toEpochMilli();
	}
	
	public static String getStringWithMillis(long timestampmillis, DateTimeFormatter formatter, ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestampmillis/1000, (int)(timestampmillis%1000), offset);
		return dateTime.format(formatter);
	}
	
	public static String getStringWithSeconds(long timestamp, DateTimeFormatter formatter, ZoneOffset offset) {
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp, 0, offset);
		return dateTime.format(formatter);
	}
	
	/**
	 * 
	 * [설명] string 형태의 데이터를  Date 포맷으로 변경하는 함수
	 * 
	 * @Method : stringToDate
	 * @param timeStamp
	 * @return
	 * @throws ParseException : Date
	 * @author : ese
	 * @since : 2018. 3. 12.
	 */
	public static LocalDateTime stringToDate(String timeStamp, DateTimeFormatter formatter) throws ParseException {
		return LocalDateTime.parse(timeStamp, formatter);
	}
	
	public static String dateTimeToString(ZonedDateTime dateTime, DateTimeFormatter formatter) throws ParseException {
		return dateTime.format(formatter);
	}
	public static String dateTimeToString(LocalDateTime dateTime, DateTimeFormatter formatter) throws ParseException {
		return dateTime.format(formatter);
	}
	

	public static boolean containLocalServerIp(String ip) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							/*&& !inetAddress.isSiteLocalAddress()*/) { // TODO isSiteLocalAddress 설정 여부 추후에 처리해야됨 
						if (inetAddress.getHostAddress().toString().equalsIgnoreCase(ip)) {
							return true;
						}
					}
				}
			}
		} catch (SocketException ex) {
			logger.error(ex.getMessage());
		}
		return false;
	}

	public static String getLocalMacAddress() {
		String result = "";
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			result = sb.toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}

		return result;
	}

	public static List<String> getLocalServerIps() {
		List<String> list = Lists.newArrayList();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							/*&& !inetAddress.isSiteLocalAddress()*/) { // TODO isSiteLocalAddress 설정 여부 추후에 처리해야됨 
						list.add(inetAddress.getHostAddress().toString());
//						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			logger.error(ex.getMessage());
		}
		return list;
	}

	public static ServerInfo getServerInfo() throws UnknownHostException {
		ServerInfo serverInfo = new ServerInfo();

		com.sun.management.OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		Long currentTimestamp = ZookeeperCommUtil.getCurrentTimestampMillisecond(ZookeeperCommUtil.OFFSET);

		Double cpuUsage = bean.getSystemCpuLoad();
		Long memoryFree = bean.getFreePhysicalMemorySize();
		Long memoryTotal =bean.getTotalPhysicalMemorySize();

		serverInfo.setTimestamp(currentTimestamp);
		serverInfo.setCpuUsage(cpuUsage);
		serverInfo.setMemoryFree(memoryFree);
		serverInfo.setMemoryTotal(memoryTotal);

		return serverInfo;
	}

	public static ServerInfo getServerInfo(String id, String mac) throws UnknownHostException {
		ServerInfo serverInfo = getServerInfo();
		serverInfo.setId(id);
		serverInfo.setMac(mac);
		return serverInfo;
	}

	public static ServerInfo getServerInfo(String id, String mac, String host, String port) throws UnknownHostException {
		ServerInfo serverInfo = getServerInfo(id, mac);
		serverInfo.setHost(host);
		serverInfo.setPort(port);
		return serverInfo;
	}

	public static String unescape(String string) {
		String escapes[][] = new String[][] { 
			{ "&lt;", "<" }, 
			{ "&gt;", ">" }, 
			{ "&amp;", "&" }, 
			{ "&quot;", "\"" }, 
			{ "&39;", "\'" } 
		};
		for (String[] esc : escapes) {
			string = string.replace(esc[0], esc[1]);
		}
		return string;
	}

}
