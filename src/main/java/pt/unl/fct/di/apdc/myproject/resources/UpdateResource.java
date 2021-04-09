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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.myproject.util.UpdateData;
import pt.unl.fct.di.apdc.myproject.util.UpdatePasswordData;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class UpdateResource {

	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public UpdateResource() {
	}

	@POST
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(UpdateData data) {
		LOG.fine("Update profile attempt by user: " + data.userId);

		if (!data.validationData()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);
			Entity token = txn.get(tokenKey);

			if (user == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User does not exists").build();
			} else {
				if (token != null) {
					if (token.getLong("token_expiration_data")>=System.currentTimeMillis()) {
						user = Entity.newBuilder(txn.get(userKey)).set("user_name", data.new_username)
								.set("email", data.new_email).set("password", DigestUtils.sha512Hex(data.new_password))
								.set("profile", data.profile).set("phone", data.phone)
								.set("mobile_phone", data.mobile_phone).set("address", data.address)
								.set("addressC", data.addressC).set("location", data.location)
								.set("zip_code", data.zip_code).build();
						txn.update(user);
						LOG.fine("User " + data.userId+ " updated");
						txn.commit();
						return Response.ok("User " + data.userId+ " updated").build();
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

	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update1(UpdateData data) {
		LOG.fine("Update role attempt by user: " + data.userId);

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key userKey2 = datastore.newKeyFactory().setKind("User").newKey(data.userId2);
			Entity user2 = txn.get(userKey2);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);

			Entity token = txn.get(tokenKey);

			if (user == null || user2 == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User does not exists").build();
			} else {
				if (token != null) {
					if (token.getLong("token_expiration_data")>=System.currentTimeMillis()) {
						if ((user.getString("role").equals(Role.SU.toString())
								|| user.getString("role").equals(Role.GA.toString()))
								&& user2.getString("role").equals(Role.USER.toString())) {
							if (data.new_role_or_state.equals(Role.GBO.toString())) {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("role", data.new_role_or_state)
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							}
						}
						if (user.getString("role").equals(Role.SU.toString())
								&& user2.getString("role").equals(Role.USER.toString())) {
							if (data.new_role_or_state.equals(Role.GBO.toString())
									|| data.new_role_or_state.equals(Role.GA.toString())) {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("role", data.new_role_or_state)
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							}
						}

						LOG.warning("Permission denied " + data.userId);
						return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();

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

	@POST
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update2(UpdateData data) {
		LOG.fine("Update role attempt by user: " + data.userId);

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key userKey2 = datastore.newKeyFactory().setKind("User").newKey(data.userId2);
			Entity user2 = txn.get(userKey2);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);

			Entity token = txn.get(tokenKey);

			if (user == null || user2 == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User does not exists").build();
			} else {
				if (token != null) {
					if (token.getLong("token_expiration_data")>=System.currentTimeMillis()) {
						if (!user.getString("role").equals(Role.USER.toString())
								&& user2.getString("role").equals(Role.USER.toString())) {
							if (user2.getString("state").equals(State.ENABLED.toString())) {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.DISABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							} else {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.ENABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							}
						}

						else if (!user.getString("role").equals(Role.USER.toString())
								&& !user.getString("role").equals(Role.GBO.toString())
								&& user2.getString("role").equals(Role.GBO.toString())) {
							if (user2.getString("state").equals(State.ENABLED.toString())) {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.DISABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							} else {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.ENABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							}
						}

						else if (user.getString("role").equals(Role.SU.toString())
								&& user2.getString("role").equals(Role.GA.toString())) {
							if (user2.getString("state").equals(State.ENABLED.toString())) {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.DISABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							} else {
								user2 = Entity.newBuilder(txn.get(userKey2)).set("state", State.ENABLED.toString())
										.build();
								txn.update(user2);
								LOG.fine("User " + data.userId2 + " updated");
								txn.commit();
								return Response.ok("User " + data.userId2 + " updated").build();
							}
						}

						else {
							LOG.warning("Permission denied " + data.userId);
							return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();
						}
					}
					else {
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
	
	@POST
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(UpdatePasswordData data) {

		LOG.fine("Update password attempt by user: " + data.userId);

		if (!data.validationData()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		if (data.password.equals(data.new_password)) {
			return Response.status(Status.BAD_REQUEST).entity("New password and old password are the same").build();
		}

		else {
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
							if (user.getString("role").equals(Role.USER.toString())) {
								user = Entity.newBuilder(txn.get(userKey))
										.set("password", DigestUtils.sha512Hex(data.new_password)).build();
								txn.update(user);
								txn.commit();
								LOG.fine("User " + data.userId+ " updated");
								return Response.ok("User " + data.userId+ " updated").build();
							} else {
								LOG.warning("Permission denied " + data.userId);
								return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();
							}
						} else {
							txn.delete(tokenKey);
							txn.commit();
							return Response.status(Status.BAD_REQUEST).entity("User logged in expired").build();
						}
					}

					else {
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
