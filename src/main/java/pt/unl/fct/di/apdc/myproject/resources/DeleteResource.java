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

import pt.unl.fct.di.apdc.myproject.util.DeleteData;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class DeleteResource {
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public DeleteResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(DeleteData data) {
		LOG.fine("Delete attempt by user: " + data.userId);

		if (!data.validationData()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		if (!data.userId.equals(data.userId2)) {
			Transaction txn = datastore.newTransaction();
			try {
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
				Entity user = txn.get(userKey);

				Key userKey2 = datastore.newKeyFactory().setKind("User").newKey(data.userId2);
				Entity user2 = txn.get(userKey2);

				Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId))
						.setKind("Token").newKey(data.userId);

				Entity token = txn.get(tokenKey);

				if (user2 == null || user == null) {
					txn.rollback();
					return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
				} else {
					if (token != null) {
						if (token.getLong("token_expiration_data")>=System.currentTimeMillis()) {
							if (user.getString("role").equals(Role.GBO.toString())
									|| user.getString("role").equals(Role.GA.toString())) {
								txn.delete(userKey2);
								txn.delete(tokenKey);
								txn.commit();
								LOG.fine("User deleted");
								return Response.ok("User deleted").build();
							} else {
								return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();
							}
						} else {
							txn.delete(tokenKey);
							txn.commit();
							return Response.status(Status.BAD_REQUEST).entity("User logged in expired").build();
						}
					} else {
						return Response.status(Status.BAD_REQUEST).entity("User is not logged in").build();
					}
				}
			} finally {
				if (txn.isActive())
					txn.rollback();
			}
		} else {
			LOG.fine("Delete attempt by user: " + data.userId);

			Transaction txn = datastore.newTransaction();
			try {
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
				Entity user = txn.get(userKey);

				Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId))
						.setKind("Token").newKey(data.userId);

				Entity token = txn.get(tokenKey);

				if (user == null) {
					txn.rollback();
					return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
				} else {
					if (token != null) {
						if (token.getLong("token_expiration_data")>=System.currentTimeMillis()) {
							txn.delete(userKey);
							txn.delete(tokenKey);
							txn.commit();
							LOG.fine("User deleted");
							return Response.ok("User deleted").build();
						} else {
							txn.delete(tokenKey);
							txn.commit();
							return Response.status(Status.BAD_REQUEST).entity("User logged in expired").build();
						}
					} else {
						return Response.status(Status.BAD_REQUEST).entity("User is not logged in").build();
					}
				}

			} finally {
				if (txn.isActive())
					txn.rollback();
			}
		}

	}
}
