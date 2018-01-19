package com.et;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolrTest {
	 //请求的url  
    public static final String url="http://localhost:8080/solr/mycore";  
	public static void main(String[] args) throws SolrServerException, IOException {
		write(); 
		Read();

		
	}
   
	
	/**
	 * 测试写入数据到solr
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	
	public static void write() throws SolrServerException, IOException{
		HttpSolrClient hsr=new HttpSolrClient(url);
		MyFood mf=new MyFood();
		mf.setId("2");
		mf.setFoodname_ik("白灼虾");
	    hsr.addBean(mf);
		hsr.commit();
		hsr.close();
	}
	/**
	 * 测试从solr读取数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void Read() throws SolrServerException, IOException{
		
		HttpSolrClient hsc=new HttpSolrClient(url);  
		//查询
        SolrQuery sq=new SolrQuery();  
        sq.setQuery("foodname_ik:白");
    	//排序
        sq.set("sort", "id desc");  
        //分页查询两个参数
        //开始位置 从0开始
        sq.setStart(0);  
        //返回总行数
        sq.setRows(2);  
 
        //获取最终document
        List<MyFood> sdl=hsc.query(sq).getBeans(MyFood.class);
        for(MyFood sd:sdl){  
            System.out.println(sd.getFoodname_ik());  
        }  
        hsc.close();  
	}

   
}
