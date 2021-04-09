package pt.unl.fct.di.apdc.myproject.boot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.myproject.resources.*;

public class ServerInit implements ServletContextListener {


    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService(); 

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		Transaction txn = datastore.newTransaction();
        try {    
            Key userKey = datastore.newKeyFactory().setKind("User").newKey("admin");
            Entity user = txn.get(userKey);    
            if(user != null) {
                txn.rollback();
            }
            else {
                user = Entity.newBuilder(userKey)
                        .set("userID","admin")
                        .set("user_name", "Big Boss")
                        .set("password", DigestUtils.sha512Hex("#theBossArrived@"))
                        .set("email", "bigboss@gmail.com")
                        .set("user_creation_time", Timestamp.now())
                        .set("role", Role.SU.toString())
                        .set("state", State.ENABLED.toString())
                        .build();
                txn.add(user);
                txn.commit();
            }
        }finally {
            if(txn.isActive())
                txn.rollback();
        }
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}
}
