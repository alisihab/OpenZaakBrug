package nl.haarlem.translations.zdstozgw.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StufUtils {

	public static String getStufDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
		return now.format(formatter);
	}

	public static String getStufDateFromDateString(String dateString) {
		if (dateString == null) {
			return null;
		}
		var year = dateString.substring(0, 4);
		var month = dateString.substring(5, 7);
		var day = dateString.substring(8, 10);
		return year + month + day;
	}
}
