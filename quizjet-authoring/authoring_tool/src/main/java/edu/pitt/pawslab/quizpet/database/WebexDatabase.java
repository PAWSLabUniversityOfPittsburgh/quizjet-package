package edu.pitt.pawslab.quizpet.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mysql.jdbc.Blob;
import com.mysql.jdbc.Statement;

import edu.pitt.pawslab.quizpet.instance.ConceptNode;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.Setting;
import edu.pitt.pawslab.quizpet.instance.Topic;
import edu.pitt.pawslab.quizpet.instance.SiteUser;

public class WebexDatabase extends JdbcDaoSupport {
	
	private static RowMapper<Integer> getCount = new RowMapper<Integer>() {
		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Integer(rs.getInt(1));
		}
	};

	/*
	 * User
	 */
	public Integer checkUsername(String username){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_user` WHERE login = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{username}, getCount);
	}
	
	public String getUserPassword(String username){
		final String sql = "SELECT `ent_user`.`password` FROM `" + Setting.DBNAME + "`.`ent_user` WHERE login = ?";
		return getJdbcTemplate().queryForObject(
				sql,
				new Object[]{username},
				new RowMapper<String>(){
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});		
	}
	
	public SiteUser getUserByLogin(StringBuilder login){
		final String sql = "SELECT `ent_user`.`id`, `ent_user`.`name`, `ent_user`.`role`" + 
				" FROM `" + Setting.DBNAME + "`.`ent_user` WHERE login = ?";
		return getJdbcTemplate().queryForObject(
				sql,
				new Object[]{login.toString()},
				new RowMapper<SiteUser>() {
					@Override
					public SiteUser mapRow(ResultSet rs, int rowNum) throws SQLException {
						SiteUser rsUser = new SiteUser();
						rsUser.setId(rs.getInt("id"));
						rsUser.setName(new StringBuilder(rs.getString("name")));
						rsUser.setRole(new StringBuilder(rs.getString("role")));
						return rsUser;
					}
				});
	}
	
	/*
	 * Topics
	 */
	public HashMap<Integer, StringBuilder> getAllTopics(){
		final String sql = "SELECT `ent_jquestion`.`QuestionID`, `ent_jquestion`.`Title` FROM `" + Setting.DBNAME + "`.`ent_jquestion` WHERE domain = ?";
		final HashMap<Integer, StringBuilder> result = new HashMap<Integer, StringBuilder>();
		getJdbcTemplate().query(
				sql,
				new Object[]{Setting.DOMAIN},
				new RowMapper<Void>(){
					@Override
					public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
						result.put(rs.getInt("QuestionID"), new StringBuilder(rs.getString("Title")));
						return null;
					}
				});
		return result;
	}
	
	/*
	 * Topics
	 */
	public HashMap<Integer, StringBuilder> getUserAllTopics(int authorid){
		final String sql = "SELECT `ent_jquestion`.`QuestionID`, `ent_jquestion`.`Title` FROM `" + Setting.DBNAME + "`.`ent_jquestion` WHERE domain = ? and (AuthorId = ? or Privacy = '0')";
		final HashMap<Integer, StringBuilder> result = new HashMap<Integer, StringBuilder>();
		getJdbcTemplate().query(
				sql,
				new Object[]{Setting.DOMAIN,authorid},
				new RowMapper<Void>(){
					@Override
					public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
						result.put(rs.getInt("QuestionID"), new StringBuilder(rs.getString("Title")));
						return null;
					}
				});
		return result;
	}
	
	
	public Integer ifQuizTopicRelExists(Integer quizId){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`rel_question_quiz` WHERE QuizID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{quizId}, getCount);
	}
	
	public Integer getTopicIdByQuiz(Integer quizId){
		final String sql = "SELECT `rel_question_quiz`.`QuestionID` FROM `" + Setting.DBNAME + "`.`rel_question_quiz` WHERE QuizID = ?";
		return getJdbcTemplate().queryForObject(
				sql,
				new Object[]{quizId},
				new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new Integer(rs.getInt(1));
					}
				});
	}
	
	public Integer createQuizTopicRelation(Integer quizId, Integer topicId){
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`rel_question_quiz` (`QuizID`, `QuestionID`) VALUES (?, ?)";
		return getJdbcTemplate().update(sql, new Object[]{quizId, topicId});
	}
	
	public Integer removeQuizTopicRelation(Integer quizId){
		final String sql = "DELETE FROM `" + Setting.DBNAME + "`.`rel_question_quiz` WHERE QuizID = ?";
		return getJdbcTemplate().update(sql, quizId);
	}
	
	public Topic newTopic(final Topic newTopic){
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_jquestion` ( `AuthorID`, `GroupID`, `Title`, `Description`, `Privacy`, `domain`) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pStatement.setInt(1, newTopic.getAuthorId());
				pStatement.setInt(2, newTopic.getGroupId());
				pStatement.setString(3, newTopic.getTitle().toString());
				pStatement.setString(4, newTopic.getDecp().toString());
				pStatement.setInt(5, newTopic.isPrivacy() == true ? 1 : 0);
				pStatement.setString(6, Setting.DOMAIN);
				return pStatement;
			}
		}, keyHolder);
		
		newTopic.setTopicId(keyHolder.getKey().intValue());
		return newTopic;
	}
	
	public Integer updateTopic(Topic newTopic){
		final String sql = "UPDATE `" + Setting.DBNAME + "`.`ent_jquestion` SET `Title` = ?, `Description` = ?, `Privacy` = ? WHERE `QuestionID` = ?";
		return getJdbcTemplate().update(
				sql,
				new Object[]{
						newTopic.getTitle().toString(),
						newTopic.getDecp().toString(),
						newTopic.isPrivacy() == true ? 1 : 0,
						newTopic.getTopicId()
				});
	}
	
	public Topic getTopicById(Integer topicId){
		final String sql = "SELECT * FROM `" + Setting.DBNAME + "`.`ent_jquestion` WHERE QuestionID = ? AND domain = ?";
		return getJdbcTemplate().queryForObject(
				sql,
				new Object[]{topicId, Setting.DOMAIN},
				new RowMapper<Topic>(){
					@Override
					public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
						Topic rsTopic = new Topic();
						rsTopic.setTopicId(rs.getInt(1));
						rsTopic.setAuthorId(rs.getInt(2));
						rsTopic.setGroupId(rs.getInt(3));
						rsTopic.setTitle(new StringBuilder(rs.getString(4)));
						rsTopic.setDecp(new StringBuilder(rs.getString(5)));
						rsTopic.setPrivacy(rs.getInt(6) == 1);
						return rsTopic;
					}
				});
	}
	
	public ArrayList<Topic> getTopicsByAuthorId(int authorId){
		final String sql = "SELECT `ent_jquestion`.`QuestionID`, `ent_jquestion`.`Title`, `ent_jquestion`.`Description`, `ent_jquestion`.`Privacy` FROM `" 
				+ Setting.DBNAME + "`.`ent_jquestion` WHERE domain = ? AND AuthorID = ?";
		return (ArrayList<Topic>) getJdbcTemplate().query(
				sql,
				new Object[]{Setting.DOMAIN, authorId},
				new RowMapper<Topic>(){
					@Override
					public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
						Topic rsTopic = new Topic();
						rsTopic.setTopicId(rs.getInt(1));
						rsTopic.setTitle(new StringBuilder(rs.getString(2)));
						rsTopic.setDecp(new StringBuilder(rs.getString(3)));
						rsTopic.setPrivacy(rs.getInt(4) == 1);
						return rsTopic;
					}
				});
	}
	
	/*
	 * Quiz
	 */
	public Integer rdfIdCount(StringBuilder rdfId){
		StringBuilder withPrefix = Quiz.getStrWithPrefix(rdfId.toString());
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_jquiz` WHERE rdfID= ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{withPrefix.toString()}, getCount);
	}
	
	public Integer ifQuizIdExists(Integer quizId){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_jquiz` WHERE QuizID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{quizId}, getCount);
	}
	
	public Integer ifQuizHasClasses(Integer quizId){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`rel_quiz_class` WHERE QuizID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{quizId}, getCount);
	}
	
	public ArrayList<Quiz> blurSearch(StringBuilder keyword, Integer authorId){
		keyword.insert(0, Setting.QJAVAPREFIX + "_%");
		keyword.append("%");
		final String sql = "SELECT `ent_jquiz`.`QuizID`, `ent_jquiz`.`Title`,  `ent_jquiz`.`rdfID`"
				+ " FROM `" + Setting.DBNAME + "`.`ent_jquiz` "
				+ "WHERE Title LIKE ? AND (Privacy = 0 OR AuthorID = ?)";
		return (ArrayList<Quiz>) getJdbcTemplate().query(
				sql,
				new Object[]{keyword.toString(), authorId},
				new RowMapper<Quiz>(){
					@Override
					public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
						Quiz rsQuestion = new Quiz();
						rsQuestion.setQuizId(rs.getInt(1));
						rsQuestion.setTitle(new StringBuilder(rs.getString(2)));
						rsQuestion.setRdfId(new StringBuilder(rs.getString(3)));
						
						return rsQuestion;
					}
				});
	}
	
	public Quiz getQuizById(Integer quizId){
		final String sql = "SELECT * FROM `" + Setting.DBNAME + "`.`ent_jquiz` WHERE QuizID = ?";
		return getJdbcTemplate().queryForObject(
				sql,
				new Object[]{quizId},
				new RowMapper<Quiz>(){
					@Override
					public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
						Quiz rsPythonQuiz = new Quiz();
						rsPythonQuiz.setQuizId(rs.getInt("QuizId"));
						rsPythonQuiz.setAuthorId(rs.getInt("AuthorID"));
						rsPythonQuiz.setGroupId(rs.getInt("GroupID"));
						rsPythonQuiz.setTitle(Quiz.getStrWithoutPrefix(rs.getString("Title")));
						rsPythonQuiz.setDecp(new StringBuilder(rs.getString("Description")));
						
						Blob code = (Blob) rs.getBlob("Code");
						BufferedReader reader = new BufferedReader(new InputStreamReader(code.getBinaryStream()));
						StringBuilder codeSb = new StringBuilder();
						
						String line;
						try {
							line = reader.readLine();
							while(line != null){
								codeSb.append(line);
								codeSb.append("\n");
								line = reader.readLine();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						rsPythonQuiz.setCode(codeSb);
						
						rsPythonQuiz.setMinVar(rs.getInt("MinVar"));
						rsPythonQuiz.setMaxVar(rs.getInt("MaxVar"));
						rsPythonQuiz.setAwsTypeId(rs.getInt("AnsType"));
						rsPythonQuiz.setPrivacy(rs.getInt("Privacy") > 0 ? true : false);
						rsPythonQuiz.setRdfId(Quiz.getStrWithoutPrefix(rs.getString("rdfID")));
						rsPythonQuiz.setQuestionTypeId(rs.getInt("QuesType"));


						
						return rsPythonQuiz;
					}
				});
	}
	
	public HashSet<Integer> getClassListByQuizId(Integer quizId){
		final String sql = "SELECT `rel_quiz_class`.`ClassID` FROM `" + Setting.DBNAME + "`.`rel_quiz_class` WHERE QuizID = ?";
		ArrayList<Integer> classIds = (ArrayList<Integer>) getJdbcTemplate().query(
				sql,
				new Object[]{quizId},
				new RowMapper<Integer>(){
					@Override
					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new Integer(rs.getInt(1));
					}
				});
		return new HashSet<Integer>(classIds);
	}
	
	public Quiz newQuiz(final Quiz quiz){
		final String sql = "INSERT INTO `" + Setting.DBNAME
			+ "`.`ent_jquiz` (`AuthorID`, `GroupID`, `Title`, `Description`, `Code`, `MinVar`, `MaxVar`, `AnsType`, `Privacy`, `rdfID`, `QuesType`) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		//quiz timestamp
		quiz.setTimestamp(System.currentTimeMillis() / 1000);
		//quiz time
		quiz.timestampToDate();
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pStatement.setInt(1, quiz.getAuthorId());
				pStatement.setInt(2, quiz.getGroupId());
				pStatement.setString(3, Quiz.getStrWithPrefix(quiz.getTitle().toString()).toString());
				pStatement.setString(4, quiz.getDecp().toString());
				pStatement.setString(5, quiz.getCode().toString());
				pStatement.setInt(6, quiz.getMinVar());
				pStatement.setInt(7, quiz.getMaxVar());
				pStatement.setInt(8, quiz.getAwsTypeId());
				pStatement.setInt(9, quiz.isPrivacy() == true ? 1 : 0);
				pStatement.setString(10, quiz.getRdfIdInDb());
				pStatement.setInt(11, quiz.getQuestionTypeId());

				return pStatement;
			}
		}, keyHolder);
		
		quiz.setQuizId(keyHolder.getKey().intValue());
		quiz.setRdfId(new StringBuilder(quiz.getRdfIdInDb()));
		return quiz;
	}
	
	public Integer updateQuiz(Quiz quiz){
		//set timeStamp
		quiz.setTimestamp(System.currentTimeMillis() / 1000);
		
		final String sql = "UPDATE `" + Setting.DBNAME + "`.`ent_jquiz` SET `Title` = ?, `Description` = ?, `Privacy` = ? WHERE `QuizID` = ?";
		return getJdbcTemplate().update(
				sql,
				new Object[]{
						Quiz.getStrWithPrefix(quiz.getTitle().toString()).toString(),
						quiz.getDecp().toString(),
						quiz.isPrivacy() == true ? 1 : 0,
						quiz.getQuizId()
				});
	}
	
	public Integer addClassesToQuiz(Integer quizId, HashSet<Integer> classId){
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`rel_quiz_class` (`QuizID`, `ClassID`) VALUES (?, ?)";
		Integer rs = 0;
		
		Iterator<Integer> iterator = classId.iterator();
		while(iterator.hasNext())
			rs += getJdbcTemplate().update(sql, new Object[]{quizId, iterator.next()});
		
		return rs;
	}
	
	public Integer removeClassesUnderQuiz(Integer quizId){
		final String sql = "DELETE FROM `" + Setting.DBNAME + "`.`rel_quiz_class` WHERE QuizID = ?";
		return getJdbcTemplate().update(sql, quizId);
	}
	
	/*
	 * External Classes
	 */
	public HashMap<Integer, StringBuilder> getAllPyClasses(){
		final String sql = "SELECT * FROM `" + Setting.DBNAME + "`.`ent_class`";
		final HashMap<Integer, StringBuilder> result = new HashMap<Integer, StringBuilder>();
		getJdbcTemplate().query(
				sql,
				new RowMapper<Void>(){
					@Override
					public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
						result.put(rs.getInt("ClassID"), new StringBuilder(rs.getString("ClassName")));
						return null;
					}
				});
		return result;
	}
	
	public Integer getMaxJavaClassId(){
		final String sql = "SELECT max(ClassID) as m FROM `" + Setting.DBNAME + "`.`ent_class`";
		return getJdbcTemplate().queryForObject(sql,getCount);
	}
	//delete class type here 
	public Integer newPyClass(final StringBuilder filename){
		int mid = getMaxJavaClassId();
		//final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_class` ('ClassID','ClassName') VALUES (?,?)";
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_class` (`ClassID`, `ClassName`) VALUES (?, ?)";
		
		 getJdbcTemplate().update(sql, new Object[]{mid+1,filename});
		 return mid+1;
//		KeyHolder keyHolder = new GeneratedKeyHolder();
//		getJdbcTemplate().update(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement pStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//				pStatement.setString(1, filename.toString());
//				//pStatement.setString(2, Setting.JAVACLASSSUFFIX);
//				return pStatement;
//			}
//		}, keyHolder);
//		return keyHolder.getKey().intValue();
	}
	
	public Integer checkClassFileName(StringBuilder filename){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_class` WHERE ClassName = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{filename.toString()}, getCount);
	}
	
	public Integer checkClassId(Integer classId){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_class` WHERE ClassID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{classId}, getCount);		
	}
	
	public StringBuilder getClassFileNameById(Integer classId){
		final String sql = "SELECT `ent_class`.`ClassName` FROM `" + Setting.DBNAME + "`.`ent_class` WHERE ClassID = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{classId},
				new RowMapper<StringBuilder>(){
					@Override
					public StringBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new StringBuilder(rs.getString(1));
					}
				});	
	}
	
	public Integer getClassIdByFileName(String filename){
		final String sql = "SELECT `ent_class`.`ClassID` FROM `" + Setting.DBNAME + "`.`ent_class` WHERE ClassName = ?";
		return getJdbcTemplate().queryForObject(
				sql, 
				new Object[]{filename}, 
				new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet arg0, int arg1) throws SQLException {
						return arg0.getInt(1);
					}
				});
	}
	
	/*
	 * Concepts
	 */	
	public Integer checkConcepts(String title){
		final String sql = "SELECT COUNT(*) FROM `" + Setting.DBNAME + "`.`ent_jquiz_concept` WHERE title = ?";
		return getJdbcTemplate().queryForObject(sql, new Object[]{title}, getCount);
	}
	
	public ArrayList<ConceptNode> getAllConceptsOfOneQuiz(String rdfId){
		final String sql = "SELECT * FROM `" + Setting.DBNAME + "`.`ent_jquiz_concept` WHERE title = ?";
		try{
			return (ArrayList<ConceptNode>) getJdbcTemplate().query(
					sql,
					new Object[]{rdfId},
					new RowMapper<ConceptNode>(){
						@Override
						public ConceptNode mapRow(ResultSet row, int rowNum) throws SQLException {
							ConceptNode rs = new ConceptNode();
							rs.setId(row.getInt("id"));
							rs.setName(new StringBuilder(row.getString("concept")));
							rs.setClassFile(new StringBuilder(row.getString("class")));
							rs.setStartLine(row.getInt("sline"));
							rs.setEndLine(row.getInt("eline"));
							rs.setWeight(row.getString("weight"));
							rs.setDirection(row.getString("direction"));
							return rs;
						}
					});
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<ConceptNode>();
		}
	}
	
	public Integer removeConcepts(String title){
		final String sql = "DELETE FROM `" + Setting.DBNAME + "`.`ent_jquiz_concept` WHERE title = ?";
		return getJdbcTemplate().update(sql, title);
	}
	public Integer removeConceptById(int id){
		final String sql = "DELETE FROM `" + Setting.DBNAME + "`.`ent_jquiz_concept` WHERE id = ?";
		return getJdbcTemplate().update(sql, id);
	}
	public Integer removeConceptByRdfid(String rdfid){
		final String sql = "DELETE FROM `" + Setting.DBNAME + "`.`ent_jquiz_concept` WHERE title = ?";
		return getJdbcTemplate().update(sql, rdfid);
	}
	
	
	public Integer addConceptById(final String rdfId, final ConceptNode curr){ 
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_jquiz_concept` (`concept`, `class`, `sline`, `eline`, `weight`, `direction`, `title`)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					//pStatement.setInt(1, id);
					pStatement.setString(1, curr.getName().toString());
					pStatement.setString(2, curr.getClassFile().toString());
					pStatement.setInt(3, curr.getStartLine());
					pStatement.setInt(4, curr.getEndLine());
					pStatement.setString(5, curr.getWeight());
					pStatement.setString(6, curr.getDirection());
					pStatement.setString(7, rdfId);
					return pStatement;
				}
			}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	public HashMap<String,Integer> addConceptMulById(final String rdfId, final String cps){
		final String[] temp = cps.split(":");
		HashMap< String,Integer> result = new HashMap<String,Integer>();
		for ( int i =1;i<temp.length;i++){
			
			
			final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_jquiz_concept` (`concept`, `class`, `sline`, `eline`, `weight`, `direction`, `title`)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			final String cons = temp[i];
			KeyHolder keyHolder = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement pStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						//pStatement.setInt(1, id);
						pStatement.setString(1, cons);
						pStatement.setString(2, temp[0]);
						pStatement.setInt(3, 0);
						pStatement.setInt(4, 0);
						pStatement.setString(5, "1.0");
						pStatement.setString(6, "outcome");
						pStatement.setString(7, rdfId);
						return pStatement;
					}
				}, keyHolder);
			
			result.put(temp[i], keyHolder.getKey().intValue());		
			
		}
		return result;
	}
	public Integer updateConceptLineById(final String id, String line){ 
		line = line.trim();
		final String newline = line;
		final String sql = "UPDATE `" + Setting.DBNAME + "`.`ent_jquiz_concept` SET `sline` = ?, `eline` = ? WHERE `id` = ?";
		return getJdbcTemplate().update(
				sql,
				new Object[]{
						newline.split("-")[0],
						newline.split("-")[1],
						id
				});
		
		

	}
	
	
	public Integer addConcepts(String title, ArrayList<ConceptNode> conceptList){
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_jquiz_concept` (`title`, `concept`, `class`, `sline`, `eline`, `weight`, `direction`)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
		Integer rs = 0;
		
		Iterator<ConceptNode> iterator = conceptList.iterator();
		while(iterator.hasNext()){
			ConceptNode curr = iterator.next();
			rs += getJdbcTemplate().update(sql,
					new Object[]{
							title,
							curr.getName().toString(),
							curr.getClassFile().toString(),
							curr.getStartLine(),
							curr.getEndLine(),
							curr.getWeight(),
							curr.getDirection()
					});
		}
		
		return rs;
	}
	
	public ArrayList<Quiz> topicSearch(StringBuilder keyword, final Integer authorId){
//		final String sql = "SELECT `ent_jquiz`.`QuizID`, `ent_jquiz`.`Title`, `ent_jquiz`.`rdfIdDisplay`, `ent_jquiz`.`version`"
//				+ " FROM `" + Setting.DBNAME + "`.`ent_jquiz` "
//				+ "WHERE Title LIKE ?";
		
		if(keyword.toString().equals("-1")){
			final String sql = "SELECT `a`.`QuizID`, `a`.`Title`, `a`.`rdfID` , `a`.`AuthorId`, `d`.`name`"
					+ " FROM `" + Setting.DBNAME + "`.`ent_jquiz` as a, `" + Setting.DBNAME + "`.`rel_question_quiz` as b, `" + Setting.DBNAME + "`.`ent_jquestion` as c, `"+ Setting.DBNAME + "`.`ent_user` as d "
					+ "WHERE  d.id = a.AuthorId and a.QuizID = b.QuizID and  c.QuestionID = b.QuestionID and c.domain ='java' and (a.AuthorId = ? or a.Privacy = '0')";
			System.out.println(sql);
			System.out.println(keyword.toString());
			return (ArrayList<Quiz>) getJdbcTemplate().query(
					sql,
					new Object[]{authorId},
					new RowMapper<Quiz>(){
						@Override
						public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
							Quiz rsQuestion = new Quiz();
							rsQuestion.setQuizId(rs.getInt(1));
							rsQuestion.setTitle(new StringBuilder(rs.getString(2)));
							rsQuestion.setRdfId(new StringBuilder(rs.getString(3)));
							rsQuestion.setAuthor(rs.getString(5));
							if(rs.getInt(4)==authorId)
								rsQuestion.setCanModify(true);
							else rsQuestion.setCanModify(false);
								
							
							
							return rsQuestion;
						}
					});
			
		}
		
		final String sql = "SELECT `a`.`QuizID`, `a`.`Title`, `a`.`rdfID`, `a`.`AuthorId`, `d`.`name`"
				+ " FROM `" + Setting.DBNAME + "`.`ent_jquiz` as a, `" + Setting.DBNAME + "`.`rel_question_quiz` as b, `"+ Setting.DBNAME + "`.`ent_user` as d "
				+ "WHERE  d.id = a.AuthorId and a.QuizID = b.QuizID and b.QuestionID = ? and (a.AuthorId = ? or a.Privacy = '0') ";
		System.out.println(sql);
		System.out.println(keyword.toString());
		return (ArrayList<Quiz>) getJdbcTemplate().query(
				sql,
				new Object[]{keyword.toString(),authorId},
				new RowMapper<Quiz>(){
					@Override
					public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
						Quiz rsQuestion = new Quiz();
						rsQuestion.setQuizId(rs.getInt(1));
						rsQuestion.setTitle(new StringBuilder(rs.getString(2)));
						rsQuestion.setRdfId(new StringBuilder(rs.getString(3)));
						rsQuestion.setAuthor(rs.getString(5));
						if(rs.getInt(4)==authorId)
							rsQuestion.setCanModify(true);
						else rsQuestion.setCanModify(false);
							
						
						
						return rsQuestion;
					}
				});
	}
	
	
	/*
	 * Topics
	 */

	
	public Integer addConceptsByClassFile(String title, HashMap<StringBuilder, ArrayList<ConceptNode>> conceptListForOneQuiz){
		final String sql = "INSERT INTO `" + Setting.DBNAME + "`.`ent_jquiz_concept` (`title`, `concept`, `class`, `sline`, `eline`, `weight`, `direction`)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
		Integer rs = 0;
		
		Iterator<StringBuilder> iterator = conceptListForOneQuiz.keySet().iterator();
		while(iterator.hasNext()){
			StringBuilder filename = iterator.next();
			ArrayList<ConceptNode> conceptNodes = conceptListForOneQuiz.get(filename);
			
			Iterator<ConceptNode> iterator2 = conceptNodes.iterator();
			while(iterator2.hasNext()){
				ConceptNode conceptNode = iterator2.next();
				
				if(conceptNode.getName().toString().equals("root"))
					continue;
				
				int sline = conceptNode.getStartLine();
				int eline = conceptNode.getEndLine() == null ? sline : conceptNode.getEndLine();
				
				rs += getJdbcTemplate().update(sql, new Object[]{
						title,
						conceptNode.getName(),
						filename.toString(),
						sline,
						eline,
						conceptNode.getWeight(),
						conceptNode.getDirection()});
			}
		}
		
		return rs;
	}
	
	
	
}
