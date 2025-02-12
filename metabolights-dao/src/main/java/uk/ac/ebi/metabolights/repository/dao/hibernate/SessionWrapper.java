/*
 * EBI MetaboLights - http://www.ebi.ac.uk/metabolights
 * Cheminformatics and Metabolism group
 *
 * European Bioinformatics Institute (EMBL-EBI), European Molecular Biology Laboratory, Wellcome Trust Genome Campus, Hinxton, Cambridge CB10 1SD, United Kingdom
 *
 * Last modified: 2015-May-07
 * Modified by:   kenneth
 *
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.ac.ebi.metabolights.repository.dao.hibernate;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.metabolights.repository.dao.hibernate.datamodel.DataModel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: conesa
 * Date: 26/01/15
 * Time: 14:02
 */
public class SessionWrapper {
    private SessionFactory factory;
    private Session session;
    private int sessionCount = 0;
    public SessionWrapper(SessionFactory factory) {
        this.factory = factory;
    }
    private static Logger logger = LoggerFactory.getLogger(SessionWrapper.class);

    public Session getSession() {
        logger.info("Session Wrapper - getCurrentSession - Get session from session factory ");
        session = factory.getCurrentSession();
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session needSession() {

        logger.info("needSession - Getting the session");

        session = getSession();

        logger.info("Session Wrapper -  returning session");
        return session;


    }

    public void noNeedSession() {
        logger.info("Session Wrapper - noNeedSession - closing the txn");
    }

    public Query createQuery(String query){

        return session.createQuery(query);
    }

    public void delete(DataModel dataModel) {
        session.delete(dataModel);
    }

    public void saveOrUpdate(DataModel datamodel) {
        session.saveOrUpdate(datamodel);
    }
    public void save(DataModel datamodel) {
        session.save(datamodel);
    }

    public Object get(Class dataModelclass, Long id) {
        return session.get(dataModelclass,id);
    }

    public SQLQuery createSQLQuery(String SQLQuery) {
        return session.createSQLQuery(SQLQuery);
    }


    public Transaction startTxn() {
        Transaction tx = this.session.beginTransaction();
        return tx;
    }
}
