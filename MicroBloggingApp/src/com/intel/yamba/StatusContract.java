package com.intel.yamba;

public class StatusContract {

		public final static String DB_NAME = "Yamba";
		public static final int DB_VERSION = 1;
		public static final String TABLE_NAME = "Statuses";
		
		public class Columns {
			public static final String USERNAME = "user";
			public static final String MESSAGE = "message";
		}
}
