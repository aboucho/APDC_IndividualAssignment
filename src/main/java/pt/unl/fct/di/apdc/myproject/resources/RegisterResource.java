package pt.unl.fct.di.apdc.myproject.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;


import pt.unl.fct.di.apdc.myproject.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON  +";charset=utf-8")

public class RegisterResource {
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			
	public RegisterResource() {  }
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(RegisterData data) {
		LOG.fine("Register attempt by user: " + data.username);

		if (!data.validationData()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists").build();
			} else {
				user = Entity.newBuilder(userKey)
						.set("user_name", data.username)
						.set("userID", data.userId)
						.set("email", data.email)
						.set("password", DigestUtils.sha512Hex(data.password))
						.set("role", Role.USER.toString())
						.set("state", State.ENABLED.toString())
						.set("user_creation_time", Timestamp.now()).build();
				txn.add(user);
				LOG.fine("User registered " + data.username);
				txn.commit();
				return Response.ok("User registered " + data.username).build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}

	}
	
}
