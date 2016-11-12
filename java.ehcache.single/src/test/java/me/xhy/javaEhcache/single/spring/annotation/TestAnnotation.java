package me.xhy.javaEhcache.single.spring.annotation;

import me.xhy.javaEhcache.single.spring.annotation.bean.Movie;
import me.xhy.javaEhcache.single.spring.annotation.service.MovieService;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xuhuaiyu on 2016/9/1.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAnnotation {

	static ClassPathXmlApplicationContext context;
	static MovieService movieService;

	String movieId;
	Movie movie = new Movie("1","a");

	@BeforeClass
	public static void beforeClass() {
		context = new ClassPathXmlApplicationContext("spring-context.xml");
		movieService = (MovieService)context.getBean("movieService");
	}

	@AfterClass
	public static void afterClass() {
		context.close();
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("==========一个测试开始==========");
		movieId = "1";
//		movie = new Movie("1","a");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("==========一个测试结束==========");
	}

	@Test
	public void test1QueryById() {
		Movie retMovie;

		for(int i=0; i<5; i++) {
			retMovie = movieService.queryMovie(movieId);
			System.out.println(retMovie);
		}


		movieId = movieId + 1;
		System.out.println("更改查新条件id的值：id = " + movieId);
		retMovie = movieService.queryMovie(movieId);
		System.out.println(retMovie);
	}

	@Test
	public void test2QueryAndUpdateById() {
		this.test1QueryById();

		movieService.updateMovie(movieId);

		this.test1QueryById();
	}

	@Test
	public void test3QueryByMovie() {
		System.out.println("movie hash code = " + movie.hashCode());
		System.out.println("movie toString  = " + movie.toString());

		for(int i=0; i<5; i++) {
			Movie retMovie = movieService.queryMovie(movie);

			System.out.println(retMovie);
		}
	}

	@Test
	public void test4QueryAndUpdateByMovie() {
		this.test3QueryByMovie();

		movieService.updateMovie(movie);

		this.test3QueryByMovie();
	}

}