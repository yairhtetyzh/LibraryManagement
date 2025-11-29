package com.book.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.book.library.model.Borrower;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long>, JpaSpecificationExecutor<Borrower> {

	Optional<Borrower> findByEmail(String email);

}
