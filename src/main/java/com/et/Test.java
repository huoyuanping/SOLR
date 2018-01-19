package com.et;



import java.io.IOException;
import java.util.List;
import java.util.Map;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.GroupParams;
public class Test {
	
	static String urlString = "http://localhost:8080/solr/core1";   
	static SolrClient solr;
	static {
		solr = new HttpSolrClient(urlString);
	}
	public static void main(String[] args) throws SolrServerException, IOException {
		//write();
		//read();
		groupBy();
	}
	
	/**
	 * ����д�����ݵ�solr
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void write() throws SolrServerException, IOException{
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "5");
		document.addField("foodname_ik", "����Ϻ");
		document.addField("price_d", "60");
		UpdateResponse response = solr.add(document);
		solr.commit();
		solr.close();
	}
	
	/**
	 * ���Դ�solr��ȡ����
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void read() throws SolrServerException, IOException{
		SolrQuery solrQuery=new SolrQuery();
		//��ѯ
		solrQuery.setQuery("foodname_s:*");
		//���� ���ɸ���  ��Ϊû�е÷� ������query��
		//solrQuery.setFilterQueries("foodname_ik:��");
		//����
		solrQuery.setSort("id", ORDER.desc);
		//��ҳ��ѯ��������
		//��ʼλ�� ��0��ʼ
		solrQuery.setStart(0);
		//����������
		solrQuery.setRows(2);
		//���ø���
		//�Ƿ����
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("foodname_s");
		solrQuery.set("hl.fl", "foodname_s");
		solrQuery.setHighlightSimplePre("<font color=red>");
		solrQuery.setHighlightSimplePost("</font>");
		//solrQuery.set(HighlightParams.FIELDS, "foodname_ik");
		
		QueryResponse query = solr.query(solrQuery);
		//��ȡ����document
		SolrDocumentList results = query.getResults();
		//��ȡ����
		Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
		for(SolrDocument doc:results){
			//��ǰdocument��id
			String id=doc.getFieldValue("id").toString();
			System.out.println(id);
			System.out.println(doc.getFieldValue("foodname_s"));
			//��ȡ������һ������ļ�ֵ��
			Map<String, List<String>> msl=highlighting.get(id);
			//��ȡֵ
			List<String> list=msl.get("foodname_s");
			//��ȡ������ֵ
			String highStr=list.get(0);
			System.out.println(highStr);
		}
		solr.close();
	}
	/**
	 * �����Ƿ�������� ͬʱ���Ի�ȡ�������µ�һ����Ԫ�أ����ݣ�
	 *   ������
	 *     document
	 *     document
	 *     document
	 *   ������
	 *     document
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void groupBy() throws SolrServerException, IOException{
		SolrQuery solrQuery=new SolrQuery("content_ik:����");
		//�Ƿ����
		solrQuery.setParam(GroupParams.GROUP, true);
		//���ò���
		solrQuery.setParam(GroupParams.GROUP_FIELD, "type_s");
		//�Ƿ��ȡͳ������
		solrQuery.setParam("group.ngroups", true);  
		//Ĭ��ֻ��ȡ�����еĵ�һ��
		solrQuery.setParam(GroupParams.GROUP_LIMIT,"10");
		//��ȡ��� query����
		QueryResponse query = solr.query(solrQuery);
		//����
		GroupResponse groupResponse = query.getGroupResponse();
		//��ȡ������
		List<GroupCommand> values = groupResponse.getValues();
		for (GroupCommand groupCommand : values) {
			//��ȡ������
			String name = groupCommand.getName();
			//����ķ���
			List<Group> groups=groupCommand.getValues();  
			for (Group group : groups) {
				//��������ֵ
				System.out.println(group.getGroupValue());
				//�ӷ�����ȡ����Ӧ������
				SolrDocumentList results = group.getResult();
				System.out.println(results.size());
				for(SolrDocument doc:results){
					System.out.println(doc.getFieldValue("content_ik"));
				}
				System.out.println("-----------------------");
			}
			
		}
	}
	/**
	 * ͨ��idɾ��
	 * delete from food where fooid=1
	 * delete from food where foodname like '%����%'
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void delete() throws SolrServerException, IOException{
		UpdateResponse up=solr.deleteById("4");
		solr.commit();
		solr.close();
	}
	/**
	 * ͨ������ɾ��
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void deleteByCondition() throws SolrServerException, IOException{
		UpdateResponse up=solr.deleteByQuery("foodname_s:����");
		solr.commit();
		solr.close();
	}	
}
