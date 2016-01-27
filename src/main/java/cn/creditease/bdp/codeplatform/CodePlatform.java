package cn.creditease.bdp.codeplatform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


@Path("/")
public class CodePlatform {
	
	static Map<String,Object> cache = new HashMap<String,Object>();
	
	static{
		try {
			Properties properties = new Properties();
			properties.load(CodePlatform.class.getClassLoader().getResource("uuw.properties").openStream());
			UUAPI.DLLPATH = properties.getProperty("DLLPATH");
			System.out.println(UUAPI.DLLPATH);
			UUAPI.SOFTID = Integer.parseInt(properties.getProperty("SOFTID"));
			UUAPI.SOFTKEY = properties.getProperty("SOFTKEY");	//KEY 获取方式：http://dll.uuwise.com/index.php?n=ApiDoc.GetSoftIDandKEY
			UUAPI.DLLVerifyKey = properties.getProperty("DLLVerifyKey");//校验API文件是否被篡改，实际上此值不参与传输，关系软件安全，高手请实现复杂的方法来隐藏此值，防止反编译,获取方式也是在后台获取软件ID和KEY一个地方
			
			UUAPI.USERNAME = properties.getProperty("USERNAME");		//用户帐号和密码(非开发者帐号)，在打码之前，需要先设置好，给用户留一个输入帐号和密码的地方
			UUAPI.PASSWORD = properties.getProperty("PASSWORD");
			boolean status = UUAPI.checkAPI();
			if(!status){
				System.out.print("API文件校验失败，无法使用打码服务");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@POST
    @Path("/decode")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String decode(@FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @FormDataParam("type") String type) {
        int codeType = 1004;
        try{
        	codeType = Integer.parseInt(type);
        }catch(Exception e){
        }
        try {
        	
        	byte[] body = toByte(file);
        	String value = getCache(body);
        	if (value != null){
        		System.out.println("get cache :"+value);
        		return value;
        	}
        	file.read(body, 0, file.available());
        	String[] result = UUAPI.easyDecaptcha(body, codeType);
        	JSONObject json = new JSONObject();
        	json.put("codeId", result[0]);
        	json.put("result", result[1]);
        	cache.put(getMD5(body), json.toString());
        	return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"codeId\":\"-1\",\"result\":\"系统出错\"}";
    }
	
	
	private byte[] toByte(InputStream in){
		byte[] buff = new byte[1024];
		int len = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			while((len = in.read(buff,0,buff.length))!=-1){
				baos.write(buff, 0, len);
			}
			buff = baos.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			try {
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		return buff;
	}
	
	private String getCache(byte[] body){
		try {
			String md5 = getMD5(body);
			return (String) cache.get(md5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getMD5(byte[] body)throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(body);
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
	
}
