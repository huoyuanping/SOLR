package com.et;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class SolrTest {
	 //�����url  
    public static final String url="http://localhost:8080/solr/mycore";  
	public static void main(String[] args) throws SolrServerException, IOException {
		write(); 
		Read();

		
	}
   
	
	/**
	 * ����д�����ݵ�solr
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	
	public static void write() throws SolrServerException, IOException{
		HttpSolrClient hsr=new HttpSolrClient(url);
		MyFood mf=new MyFood();
		mf.setId("2");
		mf.setFoodname_ik("����Ϻ");
	    hsr.addBean(mf);
		hsr.commit();
		hsr.close();
	}
	/**
	 * ���Դ�solr��ȡ����
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void Read() throws SolrServerException, IOException{
		
		HttpSolrClient hsc=new HttpSolrClient(url);  
		//��ѯ
        SolrQuery sq=new SolrQuery();  
        sq.setQuery("foodname_ik:��");
    	//����
        sq.set("sort", "id desc");  
        //��ҳ��ѯ��������
        //��ʼλ�� ��0��ʼ
        sq.setStart(0);  
        //����������
        sq.setRows(2);  
 
        //��ȡ����document
        List<MyFood> sdl=hsc.query(sq).getBeans(MyFood.class);
        for(MyFood sd:sdl){  
            System.out.println(sd.getFoodname_ik());  
        }  
        hsc.close();  
	}

   
}
