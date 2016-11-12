package me.xhy.java.ehcache.distribute.model;

import java.io.Serializable;

/**
 * Created by xuhuaiyu on 2016/10/3.
 */
public class Music implements Serializable{

	private String id;
	private String name;

	@Override
	public String toString() {
		return "Music{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				'}';
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Music(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
