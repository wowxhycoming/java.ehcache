package me.xhy.java.ehcache.distribute.controller;

import me.xhy.java.ehcache.distribute.model.Music;
import me.xhy.java.ehcache.distribute.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

/**
 * Created by xuhuaiyu on 2016/10/3.
 */
@Controller
@RequestMapping("d1")
public class MusicController {

	@Autowired
	private MusicService musicService;

	private static Music music = new Music("20001","good body");

	@RequestMapping("/gotoInfo")
	public String gotoInfo(Model model) {
		model.addAttribute("msg","");
		return "info";
	}

	/**
	 * 按 id 查询
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/id/{id}")
	public String queryMusicById(@PathVariable String id,Model model) {

		String ss = musicService.queryMusic(id).toString();
		System.out.println(ss);

		model.addAttribute("msg", "按 id 进行查询，结果 ： " + ss);

		return "info";
	}

	/**
	 * 按 id 更新
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/id/{id}/update")
	public String updateMusicById(@PathVariable String id, Model model) {

		String ss = musicService.updateMusic(id).toString();

		model.addAttribute("msg","按 id 进行更新， 结果 ： " + ss);

		return "info";
	}


	@RequestMapping("/music/{music}")
	public String queryMusic(@PathVariable String music, Model model){

		String ss = musicService.queryMusic(new Music(music,music)).toString();
		System.out.println(ss);

		model.addAttribute("msg","按 music 进行查询，结果 ： " + ss);

		return "info";
	}



	@RequestMapping("/music/{music}/update")
	public String updateMusic(@PathVariable String music, Model model){

		String ss = musicService.updateMusid(new Music(music, music)).toString();

		model.addAttribute("msg", "按 music 进行更新，结果 ： " + ss);

		return "info";
	}

	// properties
	public MusicService getMusicService() {
		return musicService;
	}

	public void setMusicService(MusicService musicService) {
		this.musicService = musicService;
	}
}
