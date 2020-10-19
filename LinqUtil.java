package com.transsion.store.utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * linq查询
 * @date:  20171114
 * @author: yzh
 */
public class LinqUtil {
	private static final Logger logger = LoggerFactory.getLogger(LinqUtil.class);
	public interface Select<T,E> {
        T select(E obj) ;
    }
	public interface Where<D> {
        boolean where(D obj) ;
    }

	public static <D> double sum(Collection<D> colls ,String fieldName) {
		Number[] ns=select2List(colls,fieldName).toArray(new Number[]{});
		return Arrays.stream(ns).reduce(0.0,(a,b)-> a.doubleValue() + b.doubleValue()).doubleValue();
	}
	public static <T,E> double sum(Collection<E> colls ,Select<T,E> gb) {
		Number[] ns=select2List(colls,gb).toArray(new Number[]{});
		return Arrays.stream(ns).reduce(0.0,(a,b)-> a.doubleValue() + b.doubleValue()).doubleValue();
	}
	public static final <T,E> List<T> select2List(Collection<E> colls ,String fieldName){
        return select2List(colls,new Select<T,E>(){
        	@Override
			public T select(E obj){
				Object v=getFieldValueByName(obj,fieldName);
				return (T)v;
			}
        });
    }
    public static final <T,E> List<T> select2List(Collection<E> colls,Select<T,E> gb){
        Iterator<E> iter = colls.iterator() ;
        List<T> set=new ArrayList<T>();
        while(iter.hasNext()) {
            E d = iter.next() ;
            T t = gb.select(d) ;
            if(t!=null){
            	set.add(t);
            }
        }
        return set;
    }
    /**
     * 通过接口函数选择对象集合的属性值 
     * @param colls
     * @param gb
     * @return
     * <T extends Comparable<T> ,D>
     */
    public static final <D> List<D> where(Collection<D> colls ,Where<D> gb){
        Iterator<D> iter = colls.iterator() ;
        List<D> set=new ArrayList<D>();
        while(iter.hasNext()) {
            D d = iter.next() ;
            if(gb.where(d)){
            	set.add(d);
            }
        }
        return set;
    }
	/*
	 *选择对象集合的属性值 
	 *<T extends Comparable<T> ,D>
	 */
	public static final <T,D> Set<T> select(Collection<D> colls ,String fieldName){
        return select(colls,new Select<T,D>(){
        	@Override
			public T select(D obj){
				Object v=getFieldValueByName(obj,fieldName);
				return (T)v;
			}
        });
    }
    /**
     * 通过接口函数选择对象集合的属性值 
     * @param colls
     * @param gb
     * @return
     * <T extends Comparable<T> ,D>
     */
    public static final <T,D> Set<T> select(Collection<D> colls,Select<T,D> gb){
        
        Iterator<D> iter = colls.iterator() ;
        Set<T> set=new HashSet<T>();
        while(iter.hasNext()) {
            D d = iter.next() ;
            T t = gb.select(d) ;
            if(t!=null){
            	set.add(t);
            }
        }
        return set;
    }
    
    public static <T> List<List<T>> groupByCount(List<T> list, int quantity) {
    	List<List<T>> wrapList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return wrapList;
        }
        int count = 0;
        while (count < list.size()) {
        	List<T> subList=new ArrayList<>(list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity));
        	wrapList.add(subList);
            count += quantity;
        }
        
        return wrapList;
    }
    /**
     * 分組依據接口，用于集合分組時，獲取分組依據
     * @author	ZhangLiKun
     * @title	GroupBy
     * @date	2013-4-23
     */
    public interface GroupBy<T,E> {
        T groupBy(E obj) ;
    }
    public interface MapBy<T,E> {
        T mapBy(E obj) ;
    }
    public interface Construct<T,D,E> {
        D construct(T key,List<E> es);
    }
    /**
     * 对象集合构建为键值形式
     * @param colls
     * @param fieldName
     * @return
     */
    public static final <T,D> Map<T ,D> mapBy(Collection<D> colls ,String fieldName){
    	return mapBy(colls,new MapBy<T,D>(){
    		@Override
			public T mapBy(D obj){
				Object v=getFieldValueByName(obj,fieldName);
				return (T)v;
			}
    	});
    }
    /**
     * 对象集合构建为键值形式
     * @param colls
     * @param gb
     * @return
     * extends Comparable<T> 
     */
    public static final <T,D> Map<T,D> mapBy(Collection<D> colls,MapBy<T,D> gb){
        Iterator<D> iter = colls.iterator();
        Map<T,D> map = new HashMap<T,D>() ;
        while(iter.hasNext()) {
            D d = iter.next();
            T t = gb.mapBy(d);
            map.put(t,d) ;
        }
        return map;
    }
    /**
     * 通过属性对集合分组
     * @param colls
     * @param gb
     * @return
     * extends Comparable<T> 
     */
    public static final <T,D> Map<T ,List<D>> groupBy(Collection<D> colls ,String fieldName){
    	return groupBy(colls,new GroupBy<T,D>(){
    		@Override
			public T groupBy(D obj){
				Object v=getFieldValueByName(obj,fieldName);
				return (T)v;
			}
    	});
    }
    /**
     * 通过属性对集合分组
     * @param colls
     * @param gb
     * @return
     * extends Comparable<T> 
     */
    public static final <T,D> Map<T ,List<D>> groupBy(Collection<D> colls ,GroupBy<T,D> gb){
    	Map<T ,List<D>> map = new HashMap<T, List<D>>();
        Iterator<D> iter = colls.iterator() ;
        while(iter.hasNext()) {
            D d = iter.next() ;
            T t = gb.groupBy(d) ;
            if(map.containsKey(t)) {
                map.get(t).add(d) ;
            } else {
                List<D> list = new ArrayList<D>() ;
                list.add(d) ;
                map.put(t, list) ;
            }
        }
        return map ;
    }
    
    public interface Equals<T> {
        boolean equals(T obj1,T obj2) ;
    }
   
    /** 
	 * 根据属性名获取属性值 
	 * */  
   public static Object getFieldValueByName(Object o,String fieldName) {  
       try {    
           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
           String getter = "get" + firstLetter + fieldName.substring(1);    
           Method method = o.getClass().getMethod(getter, new Class[] {});    
           Object value = method.invoke(o, new Object[] {});    
           return value;    
       } catch (Exception e) {    
           logger.error(e.getMessage(),e);    
           return null;    
       }    
   }
   /*
    * set排序,由小到大
    */
	public static List<String> sortString(Set<String> set) {
		List<String> factoryDevNos=Arrays.asList(set.toArray(new String[]{}));
        //由小到大排序
        Collections.sort(factoryDevNos , new Comparator<String>() {
    		public int compare(String arg0, String arg1) {
    			return arg0.compareTo(arg1);
    		}
    	});
        return factoryDevNos;
	}
	public static List<Integer> sortInteger(Set<Integer> set) {
		List<Integer> factoryDevNos=Arrays.asList(set.toArray(new Integer[]{}));
        //由小到大排序
        Collections.sort(factoryDevNos , new Comparator<Integer>() {
    		public int compare(Integer arg0, Integer arg1) {
    			return arg0.compareTo(arg1);
    		}
    	});
        return factoryDevNos;
	}
}
