/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.mgmt;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.CosmosDBAccounts;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountKind;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountListKeysResult;
import com.microsoft.azure.management.cosmosdb.implementation.CosmosDBManager;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CosmosdbTool {
    public static final String DATABASE_ID = "TestDB";
    private CosmosDBAccounts cosmosDBAccounts;

    public CosmosdbTool(Access access) {
        cosmosDBAccounts = CosmosDBManager
                .authenticate(access.credentials(), access.subscription())
                .databaseAccounts();
    }

    public CosmosDBAccount createCosmosDBInNewGroup(String resourceGroup, String prefix) {
        final String docDBName = SdkContext.randomResourceName(prefix, 20);

        //create CosmosDB
        final CosmosDBAccount cosmosDBAccount = cosmosDBAccounts.define(docDBName)
                .withRegion(Region.US_EAST)
                .withNewResourceGroup(resourceGroup)
                .withKind(DatabaseAccountKind.GLOBAL_DOCUMENT_DB)
                .withStrongConsistency()
                .create();
        log.info("create cosmosDB account over");
        return cosmosDBAccount;
    }

    public void createDBAndAddCollection(CosmosDBAccount cosmosDBAccount) {
        // Get credentials for the CosmosDB.
        final DatabaseAccountListKeysResult databaseAccountListKeysResult = cosmosDBAccount.listKeys();
        final String masterKey = databaseAccountListKeysResult.primaryMasterKey();
        final String endPoint = cosmosDBAccount.documentEndpoint();
        // Connect to CosmosDB
        try {
            final DocumentClient documentClient = new DocumentClient(endPoint,
                    masterKey, ConnectionPolicy.GetDefault(),
                    ConsistencyLevel.Session);

            // Define a new database using the id above.
            final Database myDatabase = new Database();
            myDatabase.setId(DATABASE_ID);
            documentClient.createDatabase(myDatabase, null);

            log.info("create DB and collection over");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
