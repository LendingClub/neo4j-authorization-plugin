# neo4j-authorization-plugin

This neo4j plugin adds some fine-grained authorization that is misisng from the community edition of Neo4j.

It allows users to be marked as read-only.

It is implemented using Neo4j's KernelExtension mechansim which allows callbacks to be registered before and after transaction commit.

Inside the callback hook, we look to see if:

a. the user is in a list of read-only users and
b. the transaction has mutated any data

If both of these conditions evaluate to true, the transaction is aborted and rolled back.

## Usage

Copy the jar file that is build into the ```plugins``` directory of neo4j.  Restart neo4j.

## Requirements

This plugin currently supports neo4j 3.1+.

There was a small method signature change that Neo4j made in 3.0 -> 3.1.

If there is interest, this could be easily back-ported to 3.0.

## Configuration

Configuration is managed through the standard ```neo4j.conf``` file.

There is currently only one option: ```authz.readonly_users```.  This contains a comma-separated list

```
authz.readonly_users=user1, user2, etc.
```


## Future Development

This current implementation solves a very narrow use-case.  It could be enhanced in the future to be more general purpose.

Until then, it is a nice clear example of using a fairly obscure Neo4j feature.