package org.lendingclub.neo4j.authz;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.impl.spi.KernelContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;



public class AuthzKernelExtensionFactory extends KernelExtensionFactory<AuthzKernelExtensionFactory.Dependencies> {

	public AuthzKernelExtensionFactory() {
		super("AuthzKerelExtensionFactory");
		
	}

	public interface Dependencies {
		GraphDatabaseService getGraphDatabaseService();
		Config getConfig();
	}
	
    @Override
    public Lifecycle newInstance(KernelContext kernelContext, final Dependencies dependencies) throws Throwable {
    	
    	
        return new LifecycleAdapter() {

            private ReadOnlyTransactionEventHandler handler;
          
            @Override
            public void start() throws Throwable {

                handler = new ReadOnlyTransactionEventHandler(dependencies.getGraphDatabaseService(),dependencies.getConfig().getParams());
                System.out.println("registering "+handler);
                dependencies.getGraphDatabaseService().registerTransactionEventHandler(handler);
           
            }

            @Override
            public void shutdown() throws Throwable {
             
                dependencies.getGraphDatabaseService().unregisterTransactionEventHandler(handler);
            }
        };
    }
}
