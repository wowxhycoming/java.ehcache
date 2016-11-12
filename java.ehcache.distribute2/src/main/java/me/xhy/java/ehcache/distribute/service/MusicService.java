package me.xhy.java.ehcache.distribute.service;

import me.xhy.java.ehcache.distribute.dao.MusicDao;
import me.xhy.java.ehcache.distribute.model.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xuhuaiyu on 2016/10/3.
 */
@Service
public class MusicService {

	@Autowired
	private MusicDao musicDao;

	/**
	 * 通过给定id查询
	 * @param id
	 * @return
	 */
	public Music queryMusic(String id) {
		return musicDao.queryMusic(id);
	}

	/**
	 * 通过给定对象查询
	 * @param music
	 * @return
	 */
	public Music queryMusic(Music music) {
		return musicDao.queryMusic(music);
	}

	/**
	 * 根据id更新
	 * @param id
	 */
	public Music updateMusic(String id) {
		return musicDao.updateMusic(id);
	}

	/**
	 * 根据movie更新
	 * @param music
	 */
	public Music updateMusid(Music music) {
		return musicDao.updateMusic(music);
	}

	public MusicDao getMusicDao() {
		return musicDao;
	}

	public void setMusicDao(MusicDao musicDao) {
		this.musicDao = musicDao;
	}
}
