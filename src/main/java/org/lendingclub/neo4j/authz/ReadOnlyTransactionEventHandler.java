package org.lendingclub.neo4j.authz;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

public class ReadOnlyTransactionEventHandler implements TransactionEventHandler<Object> {

	GraphDatabaseService graphDatabaseService;
	Map<String, String> config;

	Set<String> readOnlyUserSet;

	public ReadOnlyTransactionEventHandler(GraphDatabaseService graphDatabaseService, Map<String, String> config) {
		this.graphDatabaseService = graphDatabaseService;
		Map<String, String> mapCopy = new ConcurrentHashMap<>();
		mapCopy.putAll(config);
		this.config = Collections.unmodifiableMap(mapCopy);

		Set<String> readOnlyUsers = new HashSet<>();
		String readOnlyUserList = config.get("authz.readonly_users");

		if (readOnlyUserList == null) {
			readOnlyUserList = "";
		}

		StringTokenizer st = new StringTokenizer(readOnlyUserList, ",");
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			name = name.trim();
			if (!name.isEmpty()) {
				readOnlyUsers.add(name);
			}
		}
		readOnlyUserSet = Collections.unmodifiableSet(readOnlyUsers);
	}

	protected boolean isMutation(TransactionData data) {

		if (data.assignedLabels().iterator().hasNext()) {
			return true;
		}
		if (data.assignedNodeProperties().iterator().hasNext()) {
			return true;
		}
		if (data.assignedRelationshipProperties().iterator().hasNext()) {
			return true;
		}
		if (data.createdNodes().iterator().hasNext()) {
			return true;
		}
		if (data.createdRelationships().iterator().hasNext()) {
			return true;
		}
		if (data.deletedNodes().iterator().hasNext()) {
			return true;
		}
		if (data.deletedRelationships().iterator().hasNext()) {
			return true;
		}
		if (data.removedLabels().iterator().hasNext()) {
			return true;
		}
		if (data.removedNodeProperties().iterator().hasNext()) {
			return true;
		}
		if (data.removedRelationshipProperties().iterator().hasNext()) {
			return true;
		}

		return false;
	}

	protected boolean isReadOnlyUser(String username) {
		return (!readOnlyUserSet.isEmpty()) && readOnlyUserSet.contains(username);
	}

	@Override
	public Object beforeCommit(TransactionData data) throws Exception {

		String user = data.username();
		if (user == null) {
			return null;
		}

		if (isReadOnlyUser(user) && isMutation(data)) {

				throw new GeneralSecurityException("not allowed to update");

		}

		return null;
	}

	@Override
	public void afterCommit(TransactionData data, Object state) {

	}

	@Override
	public void afterRollback(TransactionData data, Object state) {

	}

}
