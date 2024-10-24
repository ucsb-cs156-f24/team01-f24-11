package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBDate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The HelpRequestsRepository is a repository for HelpRequests entities.
 */

@Repository
public interface HelpRequestsRepository extends CrudRepository<HelpRequests, Long> {
  
}