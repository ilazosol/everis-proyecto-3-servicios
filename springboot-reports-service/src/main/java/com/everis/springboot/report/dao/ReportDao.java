package com.everis.springboot.report.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.springboot.report.documents.ReportDocument;

public interface ReportDao extends ReactiveMongoRepository<ReportDocument, String> {

}
