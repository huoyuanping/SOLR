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
	 * 测试写入数据到solr
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void write() throws SolrServerException, IOException{
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "5");
		document.addField("foodname_ik", "白灼虾");
		document.addField("price_d", "60");
		UpdateResponse response = solr.add(document);
		solr.commit();
		solr.close();
	}
	
	/**
	 * 测试从solr读取数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void read() throws SolrServerException, IOException{
		SolrQuery solrQuery=new SolrQuery();
		//查询
		solrQuery.setQuery("foodname_s:*");
		//过滤 不可高亮  因为没有得分 高亮在query中
		//solrQuery.setFilterQueries("foodname_ik:白");
		//排序
		solrQuery.setSort("id", ORDER.desc);
		//分页查询两个参数
		//开始位置 从0开始
		solrQuery.setStart(0);
		//返回总行数
		solrQuery.setRows(2);
		//设置高亮
		//是否高亮
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("foodname_s");
		solrQuery.set("hl.fl", "foodname_s");
		solrQuery.setHighlightSimplePre("<font color=red>");
		solrQuery.setHighlightSimplePost("</font>");
		//solrQuery.set(HighlightParams.FIELDS, "foodname_ik");
		
		QueryResponse query = solr.query(solrQuery);
		//获取最终document
		SolrDocumentList results = query.getResults();
		//获取高亮
		Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
		for(SolrDocument doc:results){
			//当前document的id
			String id=doc.getFieldValue("id").toString();
			System.out.println(id);
			System.out.println(doc.getFieldValue("foodname_s"));
			//获取类名和一个数组的键值对
			Map<String, List<String>> msl=highlighting.get(id);
			//获取值
			List<String> list=msl.get("foodname_s");
			//获取高亮的值
			String highStr=list.get(0);
			System.out.println(highStr);
		}
		solr.close();
	}
	/**
	 * 分组是分类的升级 同时可以获取到分组下的一部分元素（数据）
	 *   跟团游
	 *     document
	 *     document
	 *     document
	 *   自由行
	 *     document
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void groupBy() throws SolrServerException, IOException{
		SolrQuery solrQuery=new SolrQuery("content_ik:桂林");
		//是否分组
		solrQuery.setParam(GroupParams.GROUP, true);
		//设置参数
		solrQuery.setParam(GroupParams.GROUP_FIELD, "type_s");
		//是否获取统计数量
		solrQuery.setParam("group.ngroups", true);  
		//默认只获取分组中的第一条
		solrQuery.setParam(GroupParams.GROUP_LIMIT,"10");
		//获取结果 query对象
		QueryResponse query = solr.query(solrQuery);
		//分组
		GroupResponse groupResponse = query.getGroupResponse();
		//获取分组结果
		List<GroupCommand> values = groupResponse.getValues();
		for (GroupCommand groupCommand : values) {
			//获取分组名
			String name = groupCommand.getName();
			//具体的分组
			List<Group> groups=groupCommand.getValues();  
			for (Group group : groups) {
				//输出分组的值
				System.out.println(group.getGroupValue());
				//从分组中取到对应的数据
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
	 * 通过id删除
	 * delete from food where fooid=1
	 * delete from food where foodname like '%肘子%'
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static void delete() throws SolrServerException, IOException{
		UpdateResponse up=solr.deleteById("4");
		solr.commit();
		solr.close();
	}
	/**
	 * 通过名字删除
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void deleteByCondition() throws SolrServerException, IOException{
		UpdateResponse up=solr.deleteByQuery("foodname_s:肘子");
		solr.commit();
		solr.close();
	}	
}
