package pt.unl.fct.di.apdc.myproject.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.myproject.util.LoginData;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class LogoutResource {
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LogoutResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response dologin(LoginData data) {
		LOG.fine("Logout attempt by user: " + data.userId);

		if (data.userId.equals(null)) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);

			Entity token = txn.get(tokenKey);

			if (user != null && token != null) {
				LOG.info("User '" + data.userId + "' logged out successfully.");
				txn.delete(tokenKey);
				txn.commit();
				return Response.ok("User " + data.userId + " logged out successfully.").build();
			} else {
				return Response.status(Status.BAD_REQUEST).entity("User is not logged in or does not exist").build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}

