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

import cn.creditease.bdp.ali.RecCodeAuto;


@Path("/")
public class CodePlatform {
	
	@POST
    @Path("/decode")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String decode(@FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @FormDataParam("type") String type) {
        int codeType = 1000;
        try{
        	codeType = Integer.parseInt(type);
        }catch(Exception e){
        }
        try {
        	byte[] body = toByte(file);
        	 if (codeType == 8000){
        		 String result = RecCodeAuto.getCode(body);
        		 return "{\"codeId\":\"0\",\"result\":\""+result+"\"}";
             }
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
	
}
