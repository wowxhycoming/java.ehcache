package me.xhy.javaEhcache.single.spring.annotation.service;

import me.xhy.javaEhcache.single.spring.annotation.bean.Movie;
import me.xhy.javaEhcache.single.spring.annotation.dao.MovieDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xuhuaiyu on 2016/8/31.
 */
@Service
public class MovieService {

	@Autowired
	private MovieDao movieDao;

	/**
	 * 通过给定对象查询
	 * @param movie
	 * @return
	 */
	public Movie queryMovie(Movie movie) {
		return movieDao.queryMovie(movie);
	}

	/**
	 * 通过给定id查询
	 * @param id
	 * @return
	 */
	public Movie queryMovie(String id) {
		return movieDao.queryMovie(id);
	}

	public void updateMovie(String id) {
		movieDao.update(id);
	}

	public void updateMovie(Movie movie) {
		movieDao.update(movie);
	}

	public MovieDao getMovieDao() {
		return movieDao;
	}

	public void setMovieDao(MovieDao movieDao) {
		this.movieDao = movieDao;
	}

}
