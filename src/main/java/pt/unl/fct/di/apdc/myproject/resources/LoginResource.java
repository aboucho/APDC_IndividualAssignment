package pt.unl.fct.di.apdc.myproject.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.myproject.util.AuthToken;
import pt.unl.fct.di.apdc.myproject.util.LoginData;
import pt.unl.fct.di.apdc.myproject.util.RegisteredUserData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")

public class LoginResource {
	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();

	public LoginResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response dologin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.userId);

		if (data.userId.equals(null) || data.password.equals(null)) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);

			Entity token = txn.get(tokenKey);

			if (user != null) {
				if (token == null) {
					String hashedPWD = user.getString("password");
					if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
						AuthToken token2 = new AuthToken(data.userId, user.getString("role"));

						Key tokenKey2 = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId))
								.setKind("Token").newKey(data.userId);

						Entity aToken = Entity.newBuilder(tokenKey2).set("token_userId", token2.userId)
								.set("token_role", token2.role).set("token_creation_data", token2.creationData)
								.set("token_expiration_data", token2.expirationData).build();

						LOG.info("User '" + data.userId + "' logged in sucessfully.");
						txn.put(aToken);
						txn.commit();
						return Response.ok(g.toJson(token2)).build();
					} else {
						LOG.warning("Wrong password for username: " + data.userId);
						return Response.status(Status.FORBIDDEN).entity("Wrong password for username: " + data.userId)
								.build();
					}

				} else {
					LOG.warning("User " + data.userId + " is already logged in");
					return Response.status(Status.FORBIDDEN).entity("User " + data.userId + " is already logged in")
							.build();
				}
			} else {
				LOG.warning("Failed login atempt for username: " + data.userId);
				return Response.status(Status.FORBIDDEN).entity("Failed login atempt for username: " + data.userId)
						.build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@POST
	@Path("/user/logs")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response userLogs(LoginData data) {

		if (data.userId.equals(null) || data.password.equals(null)) {
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
				if (token.getLong("token_expiration_data") >= System.currentTimeMillis()) {
					if (user.getString("role").toString().equals(Role.GBO.toString())) {

						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -2);
						Timestamp period = Timestamp.of(cal.getTime());

						Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
								.setFilter(PropertyFilter.ge("user_creation_time", period))
								.setOrderBy(OrderBy.desc("user_creation_time")).setLimit(10).build();

						QueryResults<Entity> logs = datastore.run(query);

						 List<Date> creationDates = new ArrayList<Date>();
		                    logs.forEachRemaining(userlog->{
		                        creationDates.add(userlog.getTimestamp("user_creation_time").toDate());
		                    });

		                    return Response.ok(g.toJson(creationDates)).build();

					} else {
						LOG.warning("Permission denied " + data.userId);
						return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();
					}
				} else {
					txn.delete(tokenKey);
					txn.commit();
					return Response.status(Status.BAD_REQUEST).entity("User logged in expired").build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist or user is not logged in")
						.build();
			}

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

	@POST
	@Path("/user/data")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response userData(RegisteredUserData data) {

		if (data.userId.equals(null) || data.registered_user.equals(null)) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userId);
			Entity user = txn.get(userKey);

			Key regKey = datastore.newKeyFactory().setKind("User").newKey(data.registered_user);
			Entity registered = txn.get(regKey);

			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", data.userId)).setKind("Token")
					.newKey(data.userId);

			Entity token = txn.get(tokenKey);

			if (user != null && token != null) {
				if (user.getString("role").toString().equals(Role.GA.toString())) {
					if (token.getLong("token_expiration_data") >= System.currentTimeMillis()) {
						if (regKey != null && (!registered.getString("role").equals(Role.GA.toString())
								|| !registered.getString("role").equals(Role.SU.toString()))) {
							Entity info = Entity.newBuilder(regKey).set("username", registered.getString("username"))
									.set("email", registered.getString("email"))
									.set("role", registered.getString("role"))
									.set("profile", registered.getString("profile"))
									.set("phone", registered.getString("phone"))
									.set("mobile_phone", registered.getString("mobile_phone"))
									.set("address", registered.getString("address"))
									.set("addressC", registered.getString("addressC"))
									.set("location", registered.getString("location"))
									.set("zip_code", registered.getString("zip_code")).build();
							return Response.ok(g.toJson(info)).build();
						} else {
							return Response.status(Status.BAD_REQUEST).entity("User is not registered").build();
						}
					} else {
						txn.delete(tokenKey);
						txn.commit();
						return Response.status(Status.BAD_REQUEST).entity("User logged in expired").build();
					}
				} else {
					LOG.warning("Permission denied " + data.userId);
					return Response.status(Status.FORBIDDEN).entity("Permission denied " + data.userId).build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist or user is not logged in")
						.build();
			}

		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}

}
