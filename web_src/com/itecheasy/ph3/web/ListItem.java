package com.itecheasy.ph3.web;

public class ListItem<T, E> {
	private T key;
	private E value;

	public ListItem(T key, E value) {
		this.key = key;
		this.value = value;
	}

	public T getKey() {
		return key;
	}

	public void setKey(T key) {
		this.key = key;
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

}
