package org.cloudfoundry.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for initializing MongoDB with sample data
 * <p>
 * For a complete reference to MongoDB
 * see http://www.mongodb.org/
 * <p>
 * For transactions, see http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/transaction.html
 * 
 * @author Krams at {@link http://krams915@blogspot.com}
 */
@Transactional
public class InitService {

	//protected static Logger logger = Logger.getLogger("service");
	
	private MongoTemplate mongoTemplate;

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	private void init() {
		// Populate our MongoDB database
		//logger.debug("Init MongoDB users");
		//Clear all
		//List<Person> people = mongoTemplate.find(new Query(where("age").lt(100)), Person.class);
		//mongoTemplate.remove(people);
		// Create new object
		Person p = new Person ();
		p.setId(UUID.randomUUID().toString());
		p.setFirstName("John");
		p.setAge(25);
		
		// Insert to db
	    mongoTemplate.save(p);

	    // Create new object
		p = new Person ();
		p.setId(UUID.randomUUID().toString());
		p.setFirstName("Jane");
		p.setAge(20);
		
		// Insert to db
	    mongoTemplate.save(p);
        
	    // Create new object
		p = new Person ();
		p.setId(UUID.randomUUID().toString());
		p.setFirstName("Jeff");
		p.setAge(30);
		
		// Insert to db
	    mongoTemplate.save(p);
	}
}
