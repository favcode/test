package org.yi.spider.service.yidu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.yi.spider.db.DBPool;
import org.yi.spider.db.YiQueryRunner;
import org.yi.spider.entity.NovelEntity;
import org.yi.spider.model.UserModel;
import org.yi.spider.service.BaseService;
import org.yi.spider.service.INovelService;
import org.yi.spider.utils.ObjectUtils;
import org.yi.spider.utils.StringUtils;

public class NovelServiceImpl extends BaseService implements INovelService {
	
	@Override
	protected UserModel loadAdmin() {
		return null;
	}
	
	@Override
	public void repair(NovelEntity novel, NovelEntity newNovel) throws SQLException {
		String sqlPre = "update t_article set lastupdate=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(System.currentTimeMillis()));
		
		StringBuffer sql = new StringBuffer();
		if(StringUtils.isNotBlank(newNovel.getIntro())){
			sql.append(" ,intro = ?");
			params.add(newNovel.getIntro());
		}
		if(newNovel.getTopCategory() != null){
			sql.append(" ,category = ?");
			params.add(newNovel.getTopCategory());
		}
		if(newNovel.getSubCategory() != null){
			sql.append(" ,subcategory = ?");
			params.add(newNovel.getSubCategory());
		}
		if(newNovel.getFullFlag() != null){
			sql.append(" ,fullflag = ?");
			params.add(newNovel.getFullFlag());
		}
		if(StringUtils.isNotBlank(newNovel.getKeywords())){
			sql.append(" ,keywords = ?");
			params.add(newNovel.getKeywords());
		}
		sql.append(" where articleno = ?");
		params.add(novel.getNovelNo());
		if(sql.length() > 0) {
			update(sqlPre + sql.toString(), params.toArray());
		}
	}
	
	@Override
	public int update(NovelEntity novel) throws SQLException {
		String sql = "update t_article set lastupdate=?,lastchapterno=?,lastchapter=?,chapters=?,size=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(System.currentTimeMillis()));
		params.add(novel.getLastChapterno());
		params.add(novel.getLastChapterName());
		params.add(novel.getChapters());
		params.add(novel.getSize());
		if(novel != null && novel.getImgFlag() != null) {
			sql += ",imgflag=? ";
			params.add(novel.getImgFlag());
		}
		sql += " where articleno = ?";
		params.add(novel.getNovelNo());
	    return update(sql, params.toArray());
	}

	@Override
	public boolean exist(String name) throws SQLException {
		String sql = "select count(*) from t_article where articlename=?";
		Object count = query(sql, new Object[]{name});
		return ObjectUtils.obj2Int(count)>0;
	}

	@Override
	public Integer saveNovel(NovelEntity novel) throws SQLException {
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true); 
		
		String sql = "INSERT INTO t_article("
                   + "articlename, initial ,keywords ,authorid ,author ,category ,subcategory, "
                   + "intro ,fullflag ,postdate,dayvisit, weekvisit, monthvisit,  "
                   + "allvisit, dayvote, weekvote, monthvote, allvote ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		Object[] params = new Object[]{novel.getNovelName(), novel.getInitial(), StringUtils.trimToEmpty(novel.getKeywords()), 
				0, novel.getAuthor(), novel.getTopCategory(), novel.getSubCategory(),
				novel.getIntro(), novel.getFullFlag(), new Timestamp(System.currentTimeMillis()), 
				0, 0, 0, 0, 0, 0, 0, 0};
		
		return queryRunner.save(conn, sql, params);
	}

	@Override
	public NovelEntity find(String novelName) throws SQLException {
		
		Connection conn = DBPool.getInstance().getConnection();
		YiQueryRunner queryRunner = new YiQueryRunner(true);  
		
		String sql = "select * from t_article where deleteflag=false and articlename=?";
		
		return queryRunner.query(conn, sql, new ResultSetHandler<NovelEntity>() {

			@Override
			public NovelEntity handle(ResultSet rs) throws SQLException {
				NovelEntity novel = null;
				if(rs != null && rs.next()) {
					novel = new NovelEntity();
					novel.setNovelNo(rs.getInt("articleno"));
					novel.setNovelName(rs.getString("articlename"));
					novel.setAuthor(rs.getString("author"));
					novel.setTopCategory(rs.getInt("category"));
					novel.setSubCategory(rs.getInt("subcategory"));
					novel.setIntro(rs.getString("intro"));
					novel.setInitial(rs.getString("initial"));
					novel.setKeywords(rs.getString("keywords"));
				}
				
				return novel;
			}
			
		}, novelName);
	}

}
