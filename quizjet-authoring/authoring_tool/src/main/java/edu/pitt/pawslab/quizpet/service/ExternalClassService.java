package edu.pitt.pawslab.quizpet.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExternalClassService {
	
	@Autowired
	private WebexDatabase webexDatabase;
	
	
	/*
	 * this method returns all the class names
	 */
	public HashMap<Integer, StringBuilder> getAllClasses(){
		return webexDatabase.getAllPyClasses();
	}
	
	/*
	 * this method adds one row to the class table
	 */
	private Integer addFilenameToDatabase(StringBuilder filename){
		return webexDatabase.newPyClass(filename);
	}
	
	/*
	 * this method handles the file uploading
	 */
	public ServerMessage uploadClass(MultipartFile file){
		ServerMessage result = new ServerMessage();
		if(!file.isEmpty()){
			try{
				//check if the file already exists
				StringBuilder fileName = new StringBuilder(file.getOriginalFilename());
				if(webexDatabase.checkClassFileName(fileName) > 0){
					result.setSuccess(false);
					result.setMessage(new StringBuilder("The file name is not available, please change the filename:"+fileName));
				}else{
					byte[] bytes = file.getBytes();
					System.out.println(Setting.JAVACLASSFOLDER);
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(Setting.JAVACLASSFOLDER, fileName.toString())));
					stream.write(bytes);
					stream.close();
					
					Integer classId = addFilenameToDatabase(fileName);
					HashMap<Integer, StringBuilder> messageContent = new HashMap<Integer, StringBuilder>();
					messageContent.put(classId, fileName);
					
					result.setSuccess(true);
					result.setMessage(new StringBuilder("File uploaded."));
					result.setContent(new Object[]{messageContent});
				}
			}catch(Exception e){
				System.out.println(e.toString());
				result.setSuccess(false);
				result.setMessage(new StringBuilder(e.getMessage()));
			}
		}else{
			result.setSuccess(false);
			result.setMessage(new StringBuilder("The request is empty."));
		}
		return result;
	}
	
	/*
	 * this method returns one file
	 */
	public ServerMessage getClassById(Integer classId){
		ServerMessage serverMessage = new ServerMessage();
		//check if it exists
		if(webexDatabase.checkClassId(classId) > 0){
			StringBuilder filename = webexDatabase.getClassFileNameById(classId);
			try{
				StringBuilder fileContent = new StringBuilder();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Setting.JAVACLASSFOLDER + "//" + filename.toString())));
				String line = reader.readLine();
				while(line != null){
					fileContent.append(line);
					fileContent.append("\n");
					line = reader.readLine();
				}
				reader.close();
				
				serverMessage.setSuccess(true);
				serverMessage.setContent(new Object[]{fileContent});;
			}catch(Exception e){
				serverMessage.setSuccess(false);
				serverMessage.setMessage(new StringBuilder(e.getMessage()));
			}
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("No such class."));
		}
		return serverMessage;
	}
	/*
	 * this method returns one file
	 */

}
