package br.com.aliceraltecnologia.model.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.util.Strings;

import br.com.aliceraltecnologia.model.entity.Produto;
import br.com.aliceraltecnologia.model.entity.Produto_;
import br.com.aliceraltecnologia.model.filter.ProdutoFilter;
import br.com.aliceraltecnologia.model.repository.ProdutoRepositoryQuery;

public class ProdutoRepositoryImpl implements ProdutoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public List<Produto> pesquisa(ProdutoFilter produtoFilter) {

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);
		Root<Produto> root = criteria.from(Produto.class);

		Predicate[] predicates = criaRestricoes(produtoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Produto> query = manager.createQuery(criteria);

		return query.getResultList();
	}
	
	private Predicate[] criaRestricoes(ProdutoFilter produtoFilter, CriteriaBuilder builder, Root<Produto> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if (Strings.isNotBlank(produtoFilter.getQ())) {
			Predicate namePrecidate = builder.like(builder.lower(root.get(Produto_.name)), "%" + produtoFilter.getQ().toLowerCase() + "%");
			Predicate descriptionPrecidate = builder.like(builder.lower(root.get(Produto_.description)), "%" + produtoFilter.getQ().toLowerCase() + "%");
			predicates.add(builder.or(namePrecidate, descriptionPrecidate));
		}
		
		if (produtoFilter.getMin_price() != null)
			predicates.add(builder.greaterThanOrEqualTo(root.get(Produto_.price), produtoFilter.getMin_price()));
		
		if (produtoFilter.getMax_price() != null)
			predicates.add(builder.lessThanOrEqualTo(root.get(Produto_.price), produtoFilter.getMax_price()));
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
