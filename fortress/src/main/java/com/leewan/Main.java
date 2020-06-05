package com.leewan;

import java.io.File;

import org.springframework.boot.ApplicationHome;

import com.leewan.util.DateUtils;

public class Main {
	public static void main(String[] args) throws Exception {
		System.out.println(DateUtils.getDateString(1591761600000L, "yyyy-MM-dd HH:mm:ss"));
	}
}
