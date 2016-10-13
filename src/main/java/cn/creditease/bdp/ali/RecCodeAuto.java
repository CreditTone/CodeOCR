package cn.creditease.bdp.ali;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public class RecCodeAuto{  
	private static  String DLLPATH=null;
	private static  String LIBPATH=null;
	private static int libIndex=0;
	public static  int CODELENTH=8;
	public static boolean test=false;
    public static byte[] OUTINIT=null;
    
    static{
    	RecCodeAuto.init("OCR.lib");
    }
	
    public static void init(String libName){
		DLLPATH = IOTool.getRootPath("dll/sunday_x64.dll");
		System.out.println("DLLPATH:"+DLLPATH);
		LIBPATH = IOTool.getRootPath("dll/"+libName);
		libIndex = CodeRec.INSTANCE.LoadLibFromFile(LIBPATH,"123");
		System.out.println("init finish");
    }
    
    public static void init(String libName,String dllPath){
    	DLLPATH = IOTool.getRootPath("dll/"+dllPath);
    	System.out.println("DLLPATH:"+DLLPATH);
    	LIBPATH = IOTool.getRootPath("dll/"+libName);
    	libIndex = CodeRec.INSTANCE.LoadLibFromFile(LIBPATH,"123");
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
