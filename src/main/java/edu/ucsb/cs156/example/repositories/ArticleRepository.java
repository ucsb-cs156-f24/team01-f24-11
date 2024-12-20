package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.Article;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The ArticleRepository is a repository for Articles entities
 */
@Repository
public interface ArticleRepository extends CrudRepository<Article, String> {
 
}