package cn.creditease.bdp.ali;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

import cn.creditease.bdp.ali.JD_RecCodeAuto.CodeRec;

public class Taobao_RecCodeAuto{  
	private static  String DLLPATH=null;
	private static  String LIBPATH=null;
	private static int libIndex=0;
	public static  int CODELENTH=8;
	public static boolean test=false;
    public static byte[] OUTINIT=null;
    
    static{
    	Taobao_RecCodeAuto.init();
    }
	
    public static void init(){
		DLLPATH = IOTool.getRootPath("dll/taobao/sunday_x64.dll");
		System.out.println("DLLPATH:"+DLLPATH);
		LIBPATH = IOTool.getRootPath("dll/taobao/aliyun_2016.lib");
		libIndex = CodeRec.INSTANCE.LoadLibFromFile(LIBPATH,"123");
		System.out.println("init finish");
    }
    
	public interface CodeRec extends StdCallLibrary{
		CodeRec INSTANCE = (CodeRec) Native.loadLibrary(DLLPATH, CodeRec.class);  
		int LoadLibFromFile(String path,String pwd);
		int LoadLibFromFile(String pwd);
		boolean GetCodeFromBuffer(int index,byte[] img,int len,byte[] code);
	}
	
	public static void main(String[] args) throws IOException {
		byte[] bs = IOTool.getContent("D:/1.jpg");
		getCode(bs);
	}
	
    public static synchronized String getCode(byte[] imgbs)  { 
		long begin = System.currentTimeMillis();
		byte[] code = new byte[CODELENTH];    
		String rtnCode = null;
		boolean result = CodeRec.INSTANCE.GetCodeFromBuffer(libIndex,imgbs,imgbs.length,code);
		if(result){
			long end = System.currentTimeMillis();
			try {
				rtnCode = new String(code,"GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
				return null;
			}
			if(rtnCode==null){
				return null;
			}
			rtnCode = rtnCode.trim();
			System.out.println("识别时间:"+(end-begin)+"ms 识别结果:"+rtnCode);
			return rtnCode;
		}
		return null;
	}
}
