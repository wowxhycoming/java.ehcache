<%@ page pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<base href="<%=basePath%>">

<html>
<body>
<h2>java.ehcache.distribute1</h2>

初始化：
查询，后台永远返回 Music{id='10001', name='if you'}  <br>
按id更新 ： 在id 和 name 字段后面加上这个id值 <br>
按Music更新 ： 永远更新成 Music{id='20001', name='good body'} <br>
<br>

<a href="d1/id/1">1. 按 id 查询</a>  ： 查询条件为 "1" <br>
<br>
<a href="d1/id/1/update">3. 按 id 更新</a>  ： 更新条件为 "1" ；更新方法为将“1”追加到dao持有的Music对象的所有属性上 <br>
<br>
<a href="d1/music/2">2. 按 Music 查询</a>  ： 永远传入字符串 "2"，并用该字符串 new Music("2","2") 作为查询条件 <br>
<br>
<a href="d1/music/2/update">4. 按 Music 更新</a>  ： 永远传入字符串 "2" 作为更新条件； 更新方法为 new Music("2","2") 覆盖dao层持有的对象 <br>
<br>
=====================<br>
${msg}<br>
=====================<br>

<br>
<table>
	<tr><td>1. 更新时会在缓存中删除 key</td></tr>
	<tr><td>2. 查询时会在缓存中添加 key</td></tr>

	<tr><td>如果在节点1对 key 进行了更新操作，这时，所有节点将同时删除 key 的缓存， 由于没有使用数据库，所以持久化数据是不同步的。</td></tr>

	<tr><td>要想使缓存同步，需要在发生更新的节点上 紧接一个查询操作，这时，将同步该查询结果到其他节点。</td></tr>
</table>
</body>
</html>