package com.va.removeconsult.pojo;

public enum EDeviceCommand {
	Open(10), Close(11);

	private int value;

	EDeviceCommand(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static EDeviceCommand fromValue(int value) {
		for (EDeviceCommand item : EDeviceCommand.values()) {
			if (item.value == value) {
				return item;
			}
		}
		return null;
	}
}