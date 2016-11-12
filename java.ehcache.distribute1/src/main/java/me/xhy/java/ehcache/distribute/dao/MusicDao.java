package me.xhy.java.ehcache.distribute.dao;

import me.xhy.java.ehcache.distribute.model.Music;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * Created by xuhuaiyu on 2016/10/3.
 */
@Repository
public class MusicDao {

	private static Music music = new Music("10001","if you");

	/**
	 * 通过给定id查询
	 * @param id
	 * @return
	 */
	@Cacheable("distribute")
	public Music queryMusic(String id) {
		System.out.println("查询数据中 by id... | id = " + id);
		slowQuery(2000L);
		return this.music;
	}

	/**
	 * 根据id更新
	 * @param id
	 */
	@CacheEvict("distribute")
	public Music updateMusic(String id) {

		System.out.println("按id进行更新，id = " + id);

		this.music.setId(this.music.getId() + id);
		this.music.setName(this.music.getName() + id);

		return this.music;

	}

	/**
	 * 通过给定对象查询
	 * @param music
	 * @return
	 */
	@Cacheable(value="distribute", key="#music.toString()")
	public Music queryMusic(Music music) {

		this.music = music;

		System.out.println("查询数据中 by Movie... | movie = " + music);
		slowQuery(2000L);
		return this.music;
	}



	/**
	 * 根据movie更新
	 * @param music
	 */
	@CacheEvict(value="distribute", key="#music.toString()")
	public Music updateMusic(Music music) {
		System.out.println("按movie进行更新，movie = " + music);

		this.music = music;

		return this.music;
	}

	private void slowQuery(long seconds){
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}


}
