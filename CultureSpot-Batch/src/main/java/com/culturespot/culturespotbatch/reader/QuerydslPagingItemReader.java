package com.culturespot.culturespotbatch.reader;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class QuerydslPagingItemReader<T> extends AbstractPagingItemReader<T> {

	protected final Map<String, Object> jpaPropertyMap = new HashMap<>();
	protected EntityManagerFactory entityManagerFactory;
	protected EntityManager entityManager;
	protected Function<JPAQueryFactory, JPAQuery<T>> queryFunction;
	protected boolean transacted = true;    //default value

	protected QuerydslPagingItemReader() {
		setName(ClassUtils.getShortName(QuerydslPagingItemReader.class));
	}

	public QuerydslPagingItemReader(EntityManagerFactory emf, int pageSize, Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
		this();
		this.entityManagerFactory = emf;
		this.queryFunction = queryFunction;
		setPageSize(pageSize);
	}

	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

	protected void clearIfTransacted() {
		if (transacted) {
			entityManager.clear();
		}
	}

	protected void initResults() {
		if (CollectionUtils.isEmpty(results)) {
			results = new CopyOnWriteArrayList<>();
		} else {
			results.clear();
		}
	}

	protected JPAQuery<T> createQuery() {
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		return queryFunction.apply(queryFactory);
	}

	protected void fetchQuery(JPAQuery<T> query) {
		if (!transacted) {
			List<T> queryResult = query.fetch();
			for (T entity : queryResult) {
				entityManager.detach(entity);
				results.add(entity);
			}
		} else {
			results.addAll(query.fetch());
		}
	}

	@Override
	protected void doOpen() throws Exception {
		super.doOpen();

		EntityManager em = entityManagerFactory.createEntityManager(jpaPropertyMap);
		if (em == null) {
			throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
		}
	}

	@Override
	protected void doClose() throws Exception {
		entityManager.close();
		super.doClose();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doReadPage() {

		clearIfTransacted();

		JPAQuery<T> query = createQuery()
				.offset(getPage() * getPageSize())
				.limit(getPageSize());

		initResults();
		fetchQuery(query);
	}
}
