package cn.com.kxcomm.contractmanage.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import cn.com.kxcomm.common.util.PageInfo;
import cn.com.kxcomm.common.util.ReflectionUtils;


/** 
 * DAO操作基类<br> 
 * 本DAO层实现了通用的数据操作 
 *  
 * @author zhaobd
 *  
 * @param <T> 
 *            POJO实体对象 
 * @param <ID> 
 *            ID 
 */ 
@Repository
@SuppressWarnings("unchecked")  
public class CommonDAO<T> extends  HibernateDaoSupport {  

	private static final Logger logger = Logger  
	.getLogger(CommonDAO.class);  

	protected Class<T>  entityClass;
	public CommonDAO(Class<T> entityClass){
		this.entityClass = entityClass;
	}
	
	public CommonDAO(){
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}
	
	/** 
	 * 保存指定实体类 
	 *  
	 * @param entityobj 
	 *            实体类 
	 */  
	public Serializable save(T entity) {  
		try {  
			Serializable id =  getHibernateTemplate().save(entity);  
			if (logger.isDebugEnabled()) {  
				logger.debug("保存实体类成功," + entity.getClass().getName());  
			}  
			return id;
		} catch (RuntimeException e) {  
			logger.error("保存实体异常," + entity.getClass().getName(), e);  
			throw e;  
		}  
	}  

	/** 
	 * 删除指定实体 
	 *  
	 * @param entityobj 
	 *            实体类 
	 */  
	public void delete(T entity) {  
		try {  
			getHibernateTemplate().delete(entity); 
			System.out.println("entity:"+entity.getClass());
			if (logger.isDebugEnabled()) {  
				logger.debug("删除实体类成功," + entity.getClass().getName());  
			}  
		} catch (RuntimeException e) {  
			logger.error("删除实体异常", e);  
			throw e;  
		}  
	}  
	
	/** 
	 * 删除多个实体对象 
	 *  
	 * @param entityobj 
	 *            实体类 
	 */  
	public void deleteAll(Collection<T> entities) {  
		try {  
			this.getHibernateTemplate().deleteAll(entities);
			if (logger.isDebugEnabled()) {  
				logger.debug("删除实体类成功," + entities);  
			}  
		} catch (RuntimeException e) {  
			logger.error("删除实体异常", e);  
			throw e;  
		}  
	} 

	/** 
	 * 获取所有实体集合 
	 *  
	 * @param entityClass 
	 *            实体 
	 * @return 集合 
	 */  
	public List<T> findAll() {  
		try {  
			if (logger.isDebugEnabled()) {  
				logger.debug("开始删除实体：" + entityClass.getName());  
			}  
			return getHibernateTemplate().find("from " + entityClass.getName());  
		} catch (RuntimeException e) {  
			logger.error("查找指定实体集合异常，实体：" + entityClass.getName(), e);  
			throw e;  
		}  
	}  

	/** 
	 * 更新或保存指定实体 
	 *  
	 * @param entity 
	 *            实体类 
	 */  
	public void saveOrUpdate(T entity) {  
		try {  
			getHibernateTemplate().saveOrUpdate(entity);  
			if (logger.isDebugEnabled()) {  
				logger.debug("更新或者保存实体成功," + entity.getClass().getName());  
			}  
		} catch (RuntimeException e) {  
			logger.error("更新或保存实体异常", e);  
			throw e;  
		}  
	}    
	
	/** 
	 * 查找指定ID实体类对象 
	 *  
	 * @param entityClass 
	 *            实体Class 
	 * @param id 
	 *            实体ID 
	 * @return 实体对象 
	 */  
	public T findById(Serializable id) {  
		try {  
			if (logger.isDebugEnabled()) {  
				logger.debug("开始查找ID为" + id + "的实体：" + entityClass.getName());  
			}  
			return (T) getHibernateTemplate().get(entityClass, id);  
		} catch (RuntimeException e) {  
			logger.error("查找指定ID实体异常，ID：" + id, e);  
			throw e;  
		}  
	}

	/** 
	 * 查询指定HQL，并返回集合 
	 *  
	 * @param hql 
	 *            HQL语句 
	 * @param values 
	 *            可变的参数列表 
	 * @return 集合 
	 */  
	public List find(String hql, Object... values) {  
		try {  
			if (logger.isDebugEnabled()) {  
				logger.debug("开始查询指定HQL语句," + hql);  
			}  
			return getHibernateTemplate().find(hql, values);  
		} catch (RuntimeException e) {  
			logger.error("查询指定HQL异常，HQL：" + hql, e);  
			throw e;  
		}  
	}  	


	/** 
	 * 按照HQL语句查询唯一对象. 
	 *  
	 * @param hql 
	 *            HQL语句 
	 * @param values 
	 *            可变参数集合 
	 * @return OBJECT对象 
	 */  
	public Object findUnique(final String hql, final Object... values) {  
		try {  
			if (logger.isDebugEnabled()) {  
				logger.debug("开始查询返回唯一结果的HQL语句," + hql);  
			}  
			return getHibernateTemplate().execute(new HibernateCallback() {  
				public Object doInHibernate(Session s)  
				throws HibernateException, SQLException {  
					Query query = createQuery(s, hql, values);  
					return query.uniqueResult();  
				}  
			});  
		} catch (RuntimeException e) {  
			logger.error("查询指定HQL异常，HQL：" + hql, e);  
			throw e;  
		}  
	}  

	/** 
	 * 查找指定HQL并返回INT型 
	 *  
	 * @param hql 
	 *            HQL语句 
	 * @param values 
	 *            可变参数列表 
	 * @return INT 
	 */  
	public int findInt(final String hql, final Object... values) {
		Object temp=findUnique(hql, values);
		int returnint=0;
		if(temp!=null)
			returnint=Integer.parseInt(temp.toString());
		return returnint;  
	}  

	/** 
	 * 获取指定实体Class指定条件的记录总数 
	 *  
	 * @param entityClass 
	 *            实体Class 
	 * @param where 
	 *            HQL的查询条件,支持参数列表 
	 * @param values 
	 *            可变参数列表 
	 * @return 记录总数 
	 */  
	public int findTotalCount(final String hql,  
			final Object... values) {  

		return findInt(hql, values);  
	}  

	
	/**
	 * 
	 * 根据查询条件获取符合条件的数据条数
	 * @author chenxinwei 新增日期：2012-1-22
	 * @param criteria
	 * @return
	 */
	public Integer countByCriteria(final DetachedCriteria criteria){
		Assert.notNull(criteria, "DetachedCriteria must not be null");
		Integer count = (Integer) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Criteria c = criteria.getExecutableCriteria(session);
				return c.setProjection(Projections.rowCount()).uniqueResult();
			}
		});
		return count;
	}

	/** 
	 * 查找指定属性的实体集合 
	 *  
	 * @param entityClass 
	 *            实体 
	 * @param propertyName 
	 *            属性名 
	 * @param value 
	 *            条件 
	 * @return 实体集合 
	 */  
	public List<T> findByProperty(Class<T> entityClass, String propertyName,  
			Object value) {  
		try {  
			if (logger.isDebugEnabled()) {  
				logger.debug("开始查找指定属性：" + propertyName + "为" + value + "的实体"  
						+ entityClass.getName());  
			}  
			String queryStr = "from " + entityClass.getName()  
			+ " as model where model." + propertyName + "=?";  
			return getHibernateTemplate().find(queryStr, value);  
		} catch (RuntimeException e) {  
			logger.error("查找指定条件实体集合异常，条件：" + propertyName, e);  
			throw e;  
		}  
	}  

	/** 
	 * 模糊查询指定条件对象集合 <br> 
	 * 用法：可以实例化一个空的T对象，需要查询某个字段，就set该字段的条件然后调用本方法<br> 
	 * 缺点：目前测试貌似只能支持String的模糊查询，虽然有办法重写，但没必要，其他用HQL<br> 
	 *  
	 * @param entity 
	 *            条件实体 
	 * @return 结合 
	 */  
	public List<T> findByExample(T entity) {  
		try {  
			List<T> results = getHibernateTemplate().findByExample(entity);  
			return results;  
		} catch (RuntimeException re) {  
			logger.error("查找指定条件实体集合异常", re);  
			throw re;  
		}  
	}  

	/** 
	 * 补充方法(未测) 据说可以无视session的状态持久化对象 
	 *  
	 * @param entity 
	 *            实体类 
	 * @return 持久后的实体类 
	 */  
	public T merge(T entity) {  
		try {  
			T result = (T) getHibernateTemplate().merge(entity);  
			return result;  
		} catch (RuntimeException re) {  
			logger.error("merge异常", re);  
			throw re;  
		}  
	}  

	/** 
	 * 清除实体的锁定状态<br> 
	 * 方法未测 
	 *  
	 * @param entity 
	 *            实体 
	 */  
	public void attachClean(T entity) {  
		try {  
			getHibernateTemplate().lock(entity, LockMode.NONE);  
		} catch (RuntimeException re) {  
			logger.error("实体解锁异常", re);  
			throw re;  
		}  
	
	}  
	
	/**
	 * 
	* 方法用途和描述: 
	* @param hql
	* @param p
	* @author chenliang 新增日期：2012-5-18
	* @author 你的姓名 修改日期：2012-5-18
	* @since gdgroup
	 */
	public int executeByHQL(final String hql,final Object ...p){
		return (Integer)super.getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query = session.createQuery(hql);
				for (int i = 0; i < p.length; i++) {
					if(p[i]  instanceof Long){
						Long params = Long.parseLong(p[i].toString());
						query.setLong(i, params);
					}
					else if(p[i]  instanceof Integer){
						Integer params = Integer.parseInt(p[i].toString());
						query.setInteger(i, params);
					}
                    else if(p[i]  instanceof String){
                    	query.setString(i, p[i].toString());
					}
                    else if(p[i]  instanceof Date){
                    	query.setTimestamp(i, (Date)p[i]);
                    }
                    else if(p[i] instanceof Double){
                    	query.setDouble(i, (Double) p[i]);
                    }
					
				}
				return query.executeUpdate();
			}
			
		});
	}
	

	/** 
	 * 按HQL分页查询. 
	 *  
	 * @param page 
	 *            页面对象 
	 * @param hql 
	 *            HQL语句 
	 * @param values 
	 *            可变参数列表 
	 * @return 分页数据 
	 */  
	public PageInfo<T> findByPage(final PageInfo<T> page, final String hql, 
			final Object... values) {  
		try {  
			final String counthql = "select count(*) "+hql;
			if (logger.isDebugEnabled()) {  
				logger.debug("开始查找指定HQL分页数据," + hql);  
			}  
			return (PageInfo<T>) getHibernateTemplate().execute(  
					new HibernateCallback() {  
						public Object doInHibernate(Session s)  
						throws HibernateException, SQLException {  
							Query query = createQuery(s, hql, values);  
							if ((page.getCurrentPage() !=0 && page.getPageSize() !=0)  ) {  
								query.setFirstResult((page.getCurrentPage()-1)*page.getPageSize()).setMaxResults(page.getPageSize());
							}  
							
							PageInfo<T> pageList = new PageInfo<T>();
							pageList.setTotal(findTotalCount(counthql, values));
							pageList.setRows(query.list());  
							if (logger.isDebugEnabled()) {  
								logger.debug("查找指定HQL分页数据成功," + hql);  
							}  
							return pageList;  
						}  
					});  
		} catch (RuntimeException e) {  
			logger.error("分页查询异常，HQL：" + hql, e);  
			throw e;  
		}  
	}  

	/**
	 * 分页查询
	 * @param example
	 * @param pageInfo
	 * @return
	 */
	public PageInfo<T> findByPage(Example example, PageInfo<T> pageInfo){
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		criteria.add(example);
		return this.findByPage(criteria, pageInfo);
	}
	
	public PageInfo<T> findByPage(PageInfo<T> pageInfo){
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		return this.findByPage(criteria, pageInfo);
	}
	

	/**
	 * 
	 * 分页查询
	 * @author chenxinwei 新增日期：2012-1-22
	 * @param criteria 带查询条件的DetachedCriteria
	 * @param pageInfo 分页信息
	 * @return
	 */
	public PageInfo<T> findByPage(DetachedCriteria criteria, PageInfo<T> pageInfo){
		Assert.notNull(criteria, "DetachedCriteria must not be null");
		Assert.notNull(pageInfo, "PageInfo must not be null");
		Integer count = this.countByCriteria(criteria);
		criteria.setProjection(null);
		criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		PageInfo<T> pList = new PageInfo<T>();
		logger.debug("count:"+count);
		pList.setTotal(count);
//		if(pageInfo.getOrderFiled()!=null){
//			if(!("desc").equals(pageInfo.getOrderDirection())){
//				criteria.addOrder(Property.forName(pageInfo.getOrderFiled()).asc());
//			}else{
//				criteria.addOrder(Property.forName(pageInfo.getOrderFiled()).desc());
//			}
//		}
		pList.setRows(this.findByCriteria(criteria, pageInfo.getCurrentPage(), pageInfo.getPageSize()));
		return pList;
	}
	
	/** 
	 * 根据查询条件与参数列表创建Query对象 
	 *  
	 * @param session 
	 *            Hibernate会话 
	 * @param hql 
	 *            HQL语句 
	 * @param objects 
	 *            参数列表 
	 * @return Query对象 
	 */  
	public Query createQuery(Session session, String hql, Object... objects) {  
		Query query = session.createQuery(hql);  
		logger.info("objects:"+objects);
		int j = 0;
		if (objects != null) {  
			for (int i = 0; i < objects.length; i++) { 
				logger.debug("objects["+i+"]"+objects[i]);
				if(objects[i] != null && !"".equals(objects[i])){
					query.setParameter(j, objects[i]);
					j++;
				}
			}  
		}  
		return query;  
	}  

	/** 
	 * 从Spring上下文中获取本类对象<br> 
	 * 此方法可能存在线程并发问题（待测） 
	 *  
	 * @param context 
	 *            Spring上下文 
	 * @return 本类对象 
	 */  
	public static CommonDAO getFromApplicationContext(  
			WebApplicationContext context) {  
		return (CommonDAO) context.getBean("BaseHibernateDAO");  
	}  

	/**
	 * 根据某字段=某值查询单个对象
	 * @author wukq 新增日期:2012-03-23
	 * @param fieldName 字段名
	 * @param fieldValue 字段值
	 * @return 单个对象 或 null
	 */
	public T findByField(String fieldName,Object fieldValue){
		Assert.notNull(fieldName, "fieldName must not be null");
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		criteria.add(Restrictions.eq(fieldName, fieldValue));
		return this.getByCriteria(criteria);
	}
	
	/**
	 * 
	 * 根据查询条件查询单个对象 
	 * @author chenxinwei 新增日期：2012-1-22
	 * @author wukq 修改日期:2012-03-23
	 * @param criteria
	 * @return
	 */
	public T getByCriteria(DetachedCriteria criteria){
		Assert.notNull(criteria, "DetachedCriteria must not be null");
		List<T> result = this.findByCriteria(criteria, 0, 1);
		if(result!=null && result.size()>0){
			return result.get(0);
		}else{
			return null;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults){
		Assert.notNull(criteria, "DetachedCriteria must not be null");
		return this.getHibernateTemplate().findByCriteria(criteria, firstResult, maxResults);
	}

	/**
	 * 
	 * 执行Sql
	 * @author chenl 新增日期：2012-1-22
	 * @param criteria 带查询条件的DetachedCriteria
	 * @param sql  执行的   
	 * @return
	 */
	public List findByPage(String sql,Object ...objects){
		Session  sessionUse = this.getSession();
		SQLQuery sq=sessionUse.createSQLQuery(sql);
		int j=0;
		for (int i = 0; i < objects.length; i++) {
			logger.debug("objects["+i+"]"+objects[i]);
			if(null!=objects[i] && !"".equals(objects[i]) && -1!=Integer.parseInt(objects[i].toString())){
				sq.setParameter(j, objects[i]);
				j++;
			}
		}
		List lista = sq.list();
		return lista;
	}
	
	/**
	 * 
	 * 执行hql
	 * @author chenl 新增日期：2012-1-22
	 * @param criteria 带查询条件的DetachedCriteria
	 * @param hql  执行的   
	 * @return
	 */
	public List<T> pageInfoQuery(String hql,PageInfo<T> pageInfo,Object ...p){
		Session  sessionUse = this.getSession();
		Query query=sessionUse.createQuery(hql);
		int j=0;
		for (int i = 0; i < p.length; i++) {
			logger.debug("objects["+i+"]"+p[i]);
			if(null!=p[i] && !"".equals(p[i])){
				if(p[i]  instanceof Long){
					Long params = Long.parseLong(p[i].toString());
					query.setLong(j, params);
				}
				else if(p[i]  instanceof Integer){
					Integer params = Integer.parseInt(p[i].toString());
					query.setInteger(j, params);
				}
                else if(p[i]  instanceof String){
                	query.setString(j, p[i].toString());
				}
                else if(p[i]  instanceof Date){
                	query.setTimestamp(j, (Date)p[i]);
                }
				j++;
			}
		}
		if ((pageInfo.getCurrentPage() !=0 && pageInfo.getPageSize() !=0)  ) {  
			query.setFirstResult((pageInfo.getCurrentPage()-1)*pageInfo.getPageSize()).setMaxResults(pageInfo.getPageSize());
		}
		List<T> lista = query.list();
		return lista;
	}
	
}  

 
