package me.xhy.javaEhcache.single.spring.annotation.dao;

import me.xhy.javaEhcache.single.spring.annotation.bean.Movie;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * Created by xuhuaiyu on 2016/8/31.
 */

@Repository
public class MovieDao {

	private static Movie movie = new Movie("1","a");

	/**
	 * 通过给定id查询
	 * @param id
	 * @return
	 */
	@Cacheable("annotation")
	public Movie queryMovie(String id) {
		System.out.println("查询数据中 by id... | id = " + id);
		slowQuery(2000L);
		return this.movie;
	}

	/**
	 * 通过给定对象查询
	 * @param movie
	 * @return
	 */
//	@Cacheable(value="annotation", key="targetClass+methodName+#movie.id")
	@Cacheable(value="annotation", key="#movie.toString()")
	public Movie queryMovie(Movie movie) {
		System.out.println("查询数据中 by Movie... | movie = " + movie);
		slowQuery(2000L);
		return this.movie;
	}

	/**
	 * 根据id更新
	 * @param id
	 */
	@CacheEvict("annotation")
	public void update(String id) {
		System.out.println("按id进行更新，id = " + id);
		this.movie.setId(id + 1);
	}

	/**
	 * 根据movie更新
	 * @param movie
	 */
	@CacheEvict("annotation")
	public void update(Movie movie) {
		System.out.println("按movie进行更新，movie = " + movie);
		movie.setId(movie.getId() + 1);
		this.movie = movie;
	}

	private void slowQuery(long seconds){
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
